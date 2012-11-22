package services.actors;

import play.api.libs.iteratee.{ Concurrent, Enumerator }
import play.api.libs.json.{ JsValue, Json }
import play.api.mvc.RequestHeader
import play.libs.Akka
import play.api.PlayException
import play.api.UnexpectedException
import play.api.Logger
import services.endpoints.Endpoints
import services.endpoints.JsonRequest._
import services.auth.GenericProvider
import services.auth.providers._
import model.parser.GenericParser
import model.Skimbo
import model.command.TokenInvalid
import model.command.Command
import play.api.libs.json.JsString
import models.user.Column
import play.api.libs.iteratee.Iteratee
import play.api.libs.json.JsArray
import play.api.libs.concurrent.Execution.Implicits._
import scala.concurrent.duration._
import akka.actor._
import org.joda.time.DateTime

sealed case class Ping(idUser: String)
sealed case class Dead(idUser: String)
sealed case class DeadColumn(idUser: String, columnTitle: String)

object ProviderActor {

  val system: ActorSystem = ActorSystem("providers");

  def create(channel: Concurrent.Channel[JsValue], userId: String, column: Column)(implicit request: RequestHeader) {
    column.unifiedRequests.foreach { unifiedRequest =>
      val endpoint = for (
        provider <- Endpoints.getProvider(unifiedRequest.service);
        time <- Endpoints.getConfig(unifiedRequest.service).map(_.delay);
        parser <- Endpoints.getConfig(unifiedRequest.service).map(_.parser)
      ) yield (provider, time, parser)

      endpoint match {
        case Some((provider, time, parser)) => {
          Logger.info("Provider : " + provider.name + " every " + time + " seconds")
          val actor = system.actorOf(Props(
            new ProviderActor(channel, provider, unifiedRequest, time, userId, false, parser, column)))
          system.eventStream.subscribe(actor, classOf[Dead])
          system.eventStream.subscribe(actor, classOf[DeadColumn])
          system.eventStream.subscribe(actor, classOf[Ping])
        }
        case _ => Logger.error("Provider or Url not found for " + unifiedRequest.service + " :: args = " + unifiedRequest.args)
      }

    }
  }

  def ping(userId: String) = {
    system.eventStream.publish(Ping(userId))
  }

  def killActorsForUser(userId: String) = {
    system.eventStream.publish(Dead(userId))
  }

  def killActorsForUserAndColumn(userId: String, columnTitle: String) = {
    system.eventStream.publish(DeadColumn(userId, columnTitle))
  }

}

class ProviderActor(channel: Concurrent.Channel[JsValue],
  provider: GenericProvider, unifiedRequest: UnifiedRequest, delay: Int,
  idUser: String, longPolling: Boolean,
  parser: Option[GenericParser] = None, column: Column)(implicit request: RequestHeader) extends Actor {

  val log = Logger(ProviderActor.getClass())
  val sinceId = new StringBuilder();
  var sinceDate = new DateTime().minusYears(1);

  val scheduler = Akka.system.scheduler.schedule(0 second, delay second) {
    self ! ReceiveTimeout
  }

  def receive = {
    case ReceiveTimeout => {
      if (longPolling) {
        scheduler.cancel() //need ping to call provider
      }
      if (provider.hasToken(request) && parser.isDefined) {
        val optSinceId = sinceId.isEmpty match {
          case true => None
          case false => Some(sinceId.toString())
        }
        val url = Endpoints.genererUrl(unifiedRequest.service, unifiedRequest.args.getOrElse(Map.empty), optSinceId);
        val config = Endpoints.getConfig(unifiedRequest.service).get
        if (url.isDefined) {
          log.info("Fetch provider " + provider.name + " url=" + url)
          provider.fetch(url.get).get.map(response => {
            val listJson = if(config.manualNextResults) {
              parser.get.cut(provider.resultAsJson(response)).sort { (js1, js2) =>
                val skimboMsg = parser.get.asSkimbo(js1)
                val skimboMsg2 = parser.get.asSkimbo(js2)
                skimboMsg.get.createdAt.isBefore(skimboMsg2.get.createdAt)
              }
            } else {
              parser.get.cut(provider.resultAsJson(response))
            }
            val messages = Enumerator.enumerate(listJson)
            val ite = Iteratee.foreach { jsonMsg: JsValue =>
              val skimboMsg = parser.get.asSkimbo(jsonMsg)
              if(skimboMsg.isDefined) {
                val msg = Json.obj(
                  "column" -> column.title,
                  "msg" -> Json.toJson(skimboMsg.get))
                //log.info("Messages : "+msg)
                if(config.manualNextResults) {
                  if(skimboMsg.get.createdAt.isAfter(sinceDate)) {
                    channel.push(Json.toJson(Command("msg", Some(msg))))
                    sinceDate = skimboMsg.get.createdAt
                  } else {
                    log.info("old msg !!!!")
                  }
                } else {
                  val temp = sinceId.toString()
                  sinceId.clear();
                  sinceId append parser.get.nextSinceId(skimboMsg.get.sinceId, temp)
                  log.info("sinceId for " + unifiedRequest.service + " is " + sinceId.toString())
                  channel.push(Json.toJson(Command("msg", Some(msg))))
                }
              } else {
                log.error("Msg unsuported ! Service="+provider.name+" msg="+skimboMsg)
              }
            }
            messages(ite).onComplete { e =>
              log.info("ite finish for " + unifiedRequest.service + " with " + parser.get.cut(provider.resultAsJson(response)).size + " messages")
              e
            }
          })
        }
      } else if (!provider.hasToken(request)) {
        channel.push(Json.toJson(TokenInvalid(provider.name)))
        self ! Dead(idUser)
      } else {
        log.error("Provider " + provider.name + " havn't parser for " + unifiedRequest.service)
        channel.push(Json.toJson(Command("error", Some(JsString("provider havn't parser")))))
        self ! Dead(idUser)
      }
    }
    case Ping(id) => {
      if (id == idUser) {
        Akka.system.scheduler.scheduleOnce(delay second) {
          self ! ReceiveTimeout
        }
      }
    }
    case Dead(id) => {
      if (id == idUser) {
        scheduler.cancel()
        context.stop(self)
      }
    }
    case DeadColumn(idUser: String, columnTitle: String) => {
      if (columnTitle == column.title) {
        self ! Dead(idUser)
      }
    }
    case e: Exception => throw new UnexpectedException(Some("Incorrect message receive"), Some(e))
  }

}