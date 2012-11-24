package services.actors;


import scala.concurrent.duration._
import org.joda.time.DateTime
import akka.actor._
import json.parser.GenericParser
import models.command._
import models.user.Column
import play.api._
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.iteratee._
import play.api.libs.json._
import play.api.mvc.RequestHeader
import play.libs.Akka
import services.auth.GenericProvider
import services.endpoints.Endpoints
import services.endpoints.JsonRequest.UnifiedRequest
import json.Skimbo
import play.api.http.Status

sealed case class Ping(idUser: String)
sealed case class Dead(idUser: String)
sealed case class DeadColumn(idUser: String, columnTitle: String)
sealed case class DeadProvider(idUser: String, providerName: String)

object ProviderActor {

  private val system: ActorSystem = ActorSystem("providers");

  def create(channel: Concurrent.Channel[JsValue], userId: String, column: Column)(implicit request: RequestHeader) {
    column.unifiedRequests.foreach { unifiedRequest =>
      val endpoint = for (
        provider <- Endpoints.getProvider(unifiedRequest.service);
        time <- Endpoints.getConfig(unifiedRequest.service).map(_.delay);
        parser <- Endpoints.getConfig(unifiedRequest.service).map(_.parser)
      ) yield (provider, time, parser)

      endpoint match {
        case Some((provider, time, parser)) => {
          val actor = system.actorOf(Props(
            new ProviderActor(channel, provider, unifiedRequest, time, userId, false, parser, column)))
          system.eventStream.subscribe(actor, classOf[Dead])
          system.eventStream.subscribe(actor, classOf[DeadColumn])
          system.eventStream.subscribe(actor, classOf[Ping])
          system.eventStream.subscribe(actor, classOf[DeadProvider])
        }
        case _ => Logger.error("Provider or Url not found for " + unifiedRequest.service + " :: args = " + unifiedRequest.args)
      }

    }
  }

  def ping(userId: String) = {
    system.eventStream.publish(Ping(userId))
  }
  
  def modifActorsForUserAndColumn(userId: String, columnTitle:String, column:Column) = {
    system.eventStream.publish(ModifProvider(userId, columnTitle, column))
  }

  def killActorsForUser(userId: String) = {
    system.eventStream.publish(Dead(userId))
  }

  def killActorsForUserAndColumn(userId: String, columnTitle: String) = {
    system.eventStream.publish(DeadColumn(userId, columnTitle))
  }
  
  def killProfider(idUser:String, providerName:String) = {
    system.eventStream.publish(DeadProvider(idUser, providerName))
  }

}

class ProviderActor(channel: Concurrent.Channel[JsValue],
  provider: GenericProvider, 
  unifiedRequest: UnifiedRequest, 
  delay: Int,
  idUser: String, longPolling: Boolean,
  parser: Option[GenericParser] = None, 
  column: Column)(implicit request: RequestHeader) extends Actor {

  val log = Logger(ProviderActor.getClass())
  var sinceId = ""
  var sinceDate = new DateTime().minusYears(1)

  val scheduler = Akka.system.scheduler.schedule(0 second, delay second) {
    self ! ReceiveTimeout
  }

  def receive = {
    case ReceiveTimeout => {
      if (longPolling) {
        scheduler.cancel() //need ping to call provider
      }

      log.info("["+unifiedRequest.service+"] Fetching")

      if (provider.hasToken(request) && parser.isDefined) {
        val optSinceId = if (sinceId.isEmpty) None else Some(sinceId)
        val url = Endpoints.genererUrl(unifiedRequest.service, unifiedRequest.args.getOrElse(Map.empty), optSinceId);
        val config = Endpoints.getConfig(unifiedRequest.service).get

        log.info("["+unifiedRequest.service+"] "+url.get)

        if (url.isDefined) {
          provider.fetch(url.get).get.map(response => {
            if (response.status != Status.OK) {
              log.error("["+unifiedRequest.service+"] Error during fetching messages (Invalid Token?)")
              log.error(response.body.toString)
              // TODO (in a far future) : parse errors (rate, token...)
              channel.push(Json.toJson(TokenInvalid(provider.name)))
            }
            
            val skimboMsgs = parser.get.cut(provider.resultAsJson(response)).map(jsonMsg => parser.get.asSkimboSafe(jsonMsg)).flatten
            val listJson = if (config.manualNextResults || config.mustBeReordered) reorderMessagesByDate(skimboMsgs) else skimboMsgs

            log.info("["+unifiedRequest.service+"] Messages reçus : "+listJson.size)

            val messages = Enumerator.enumerate(listJson)
            val ite = Iteratee.foreach { skimboMsg: Skimbo =>
	          val msg = Json.obj("column" -> column.title, "msg" -> skimboMsg)
	            if (config.manualNextResults) {
	              if (skimboMsg.createdAt.isAfter(sinceDate)) {
	                channel.push(Json.toJson(Command("msg", Some(msg))))
	                sinceDate = skimboMsg.createdAt // TODO : utiliser uniquement SinceID (convertir date en string)
	              }
	            } else {
	              sinceId = parser.get.nextSinceId(skimboMsg.sinceId, sinceId)
	              channel.push(Json.toJson(Command("msg", Some(msg))))
	            }
            }
            messages(ite)
          })
        }
      } else if (!provider.hasToken(request)) {
        channel.push(Json.toJson(TokenInvalid(provider.name)))
        log.info("["+unifiedRequest.service+"] No Token")
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

    case ModifProvider(id: String, columnTitle, columnModif) => {
      //TODO RM : add old column instead columnTitle for this
      if (id == idUser && columnTitle == column.title) {
        val isMe = columnModif.unifiedRequests.exists { ur =>
          val argExist = if(ur.args.isDefined) {
            ur.args.get.forall { a =>
              unifiedRequest.args.getOrElse(Map.empty).get(a._1) == a._2
            }
          } else {
            !unifiedRequest.args.isDefined
          }
          ur.service == unifiedRequest.service && argExist
        }
        if(isMe) {
          // ??
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
    case DeadProvider(id: String, providerName: String) => {
      if (providerName == provider.name && id == idUser) {
        self ! Dead(idUser)
      }
    }
    case e: Exception => throw new UnexpectedException(Some("Incorrect message receive"), Some(e))
  }
  
  def reorderMessagesByDate(msgs: List[Skimbo]) = {
    msgs.sortWith((msg1, msg2) => msg1.createdAt.isBefore(msg2.createdAt))
  }

}