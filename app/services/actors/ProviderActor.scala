package services.actors;

import scala.concurrent.duration.DurationInt

import org.joda.time.DateTime

import akka.actor.Actor
import akka.actor.ReceiveTimeout
import akka.actor.actorRef2Scala
import models.Skimbo
import models.command.Command
import models.user.Column
import models.user.UnifiedRequest
import parser.GenericParser
import play.api.Logger
import play.api.UnexpectedException
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.iteratee.Enumerator
import play.api.libs.iteratee.Iteratee
import play.api.libs.json.Json
import play.api.libs.json.Json.toJsFieldJsValueWrapper
import play.libs.Akka
import services.auth.GenericProvider
import services.commands.CmdToUser
import services.endpoints.EndpointConfig
import services.endpoints.Endpoints

sealed case class ProviderActorParameter(
  provider: GenericProvider,
  unifiedRequest: UnifiedRequest,
  delay: Int,
  idUser: String,
  parser: Option[GenericParser] = None,
  column: Column
)

sealed case class Dead(idUser: String)
sealed case class DeadColumn(idUser: String, columnTitle: String)
sealed case class DeadProvider(idUser: String, providerName: String)
sealed case class Restart(idUser: String)

object ProviderActor {

  def create(idUser: String, column: Column) {
    column.unifiedRequests.foreach { unifiedRequest =>
      val endpoint = for (
        provider <- Endpoints.getProvider(unifiedRequest.service);
        time <- Endpoints.getConfig(unifiedRequest.service).map(_.delay);
        parser <- Endpoints.getConfig(unifiedRequest.service).map(_.parser)
      ) yield (provider, time, parser)

      endpoint match {
        case Some((provider, time, parser)) => {
          println("---------------------------------------------------")
          println("ProviderActor - launch : "+provider.name+" "+unifiedRequest.service)
          HelperProviderActor.foundOrCreate(idUser, ProviderActorParameter(provider, unifiedRequest, time, idUser, parser, column))
        }
        case _ => Logger.error("Provider or Url not found for " + unifiedRequest.service + " :: args = " + unifiedRequest.args)
      }
    }
  }
  
  def restart(idUser: String) = {
    HelperProviderActor.system.eventStream.publish(Restart(idUser))
  }

  def killActorsForUser(userId: String) = {
    HelperProviderActor.system.eventStream.publish(Dead(userId))
  }

  def killActorsForUserAndColumn(userId: String, columnTitle: String) = {
    HelperProviderActor.system.eventStream.publish(DeadColumn(userId, columnTitle))
  }

  def killProvider(idUser: String, providerName: String) = {
    HelperProviderActor.system.eventStream.publish(DeadProvider(idUser, providerName))
  }

}

class ProviderActor(parameter:ProviderActorParameter) extends Actor {

  val log = Logger(ProviderActor.getClass())
  
  val delay = parameter.delay
  val provider = parameter.provider
  val unifiedRequest = parameter.unifiedRequest
  val idUser = parameter.idUser
  val parser = parameter.parser
  val column = parameter.column
  val config = Endpoints.getConfig(unifiedRequest.service).get
  
  var sinceId:Option[String] = None
  var sinceDate = new DateTime().minusYears(1)
  
  val scheduler = Akka.system.scheduler.schedule(0 second, delay second) {
    self ! ReceiveTimeout
  }
  
  override def postStop() = {
    scheduler.cancel
  }

  def receive = {
    case ReceiveTimeout => {
      val url = Endpoints.genererUrl(unifiedRequest.service, unifiedRequest.args, sinceId);
      Fetcher(FetcherParameter(
          provider, 
          parser, 
          url,
          idUser,
          column.title,
          unifiedRequest.service,
          config.delay)).map { listSkimbo =>
        if(!listSkimbo.isDefined) {
          self ! Dead(idUser)
        } else {
          val listJson = if (config.mustBeReordered) reorderMessagesByDate(listSkimbo.get) else listSkimbo.get
          val messagesEnumerators = Enumerator.enumerate(listJson)
          val pusherIteratee = pushNewMessagesIteratee(config)
          messagesEnumerators(pusherIteratee)
        }
      }
    }

    case Dead(id) => {
      if (id == idUser) {
        scheduler.cancel()
        HelperProviderActor.delete(idUser, parameter)
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
    case Restart(id: String) => {
      log.info("ProviderActor RESTART => "+id+" :: "+idUser)
      if(id == idUser) {
        sinceId = None
        sinceDate = new DateTime().minusYears(1)
        self ! ReceiveTimeout
        log.info("ProviderActor RESTART")
      }
    }
    case e: Exception => throw new UnexpectedException(Some("Incorrect message receive"), Some(e))
  }

  def reorderMessagesByDate(msgs: List[Skimbo]) = {
    msgs.sortWith((msg1, msg2) => msg1.createdAt.isBefore(msg2.createdAt))
  }

  def pushNewMessagesIteratee(config: EndpointConfig) = {
    Iteratee.foreach { skimboMsg: Skimbo =>
      val msg = Json.obj("column" -> column.title, "msg" -> skimboMsg)
      if (config.since.isEmpty /* == None */) {
        if (skimboMsg.createdAt.isAfter(sinceDate)) {
          CmdToUser.sendTo(idUser, Command("msg", Some(msg)))
          sinceDate = skimboMsg.createdAt
        }
      } else {
        sinceId = Some(parser.get.nextSinceId(skimboMsg.sinceId, sinceId))
        CmdToUser.sendTo(idUser, Command("msg", Some(msg)))
      }
    }
  }

}