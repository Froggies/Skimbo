package services.actors

import scala.concurrent.Future

import models.Skimbo
import models.command.Error
import models.command.ErrorType
import models.command.TokenInvalid
import parser.GenericParser
import play.api.Logger
import play.api.http.Status
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import services.auth.GenericProvider
import services.commands.CmdToUser

case class FetcherParameter(
  provider:GenericProvider,
  parser:Option[GenericParser],
  url:Option[String], 
  idUser:String, 
  columnName: String,
  serviceName: String= "",
  delay: Int = 6
)

object Fetcher {

  val log = Logger(Fetcher.getClass())

  def apply(parameter:FetcherParameter): Future[Option[List[Skimbo]]] = {

    val provider = parameter.provider
    val parser = parameter.parser
    val url = parameter.url
    val idUser = parameter.idUser
    
    log.info("[" + parameter.serviceName + "] Fetching")

    if (provider.canStart(idUser)) {

      log.info("[" + parameter.serviceName + "] " + url.get)

      if (url.isDefined) {
        val get = provider.fetch(idUser, url.get).withTimeout(parameter.delay * 1000).get
        get.onFailure { 
          case e:Throwable => {
            log.error("[" + parameter.serviceName + "] Timeout HTTP", e)
            CmdToUser.sendTo(idUser, Error(provider.name, ErrorType.Timeout, Some(parameter.columnName)))
          }
        }
        get.map { response =>
          if (response.status != Status.OK) {
            log.error("[" + parameter.serviceName + "] HTTP Error " + response.status)
            log.info(response.body.toString)
            if (provider.isInvalidToken(idUser, response)) {
              CmdToUser.sendTo(idUser, TokenInvalid(provider.name))
              None
            } else if (provider.isRateLimiteError(response)) {
              CmdToUser.sendTo(idUser, Error(provider.name, ErrorType.RateLimit, Some(parameter.columnName)))
              Some(List.empty)
            } else {
              CmdToUser.sendTo(idUser, Error(provider.name, ErrorType.Unknown, Some(parameter.columnName)))
              None
            }
          } else {
            val skimboMsgs = parser.get.getSkimboMsg(response, provider)
            if (skimboMsgs.isEmpty) {
              log.error("[" + parameter.serviceName + "] Unexpected result")
              log.info(response.body.toString)
              CmdToUser.sendTo(idUser, Error(provider.name, ErrorType.Parser, Some(parameter.columnName)))
              None
            } else {
              skimboMsgs
            }
          }
        }
      } else {
        log.error("[" + parameter.serviceName + "] Bad url" + url)
        Future(None)
      }
    } else if (provider.isAuthProvider && !provider.canStart(idUser)) {
      CmdToUser.sendTo(idUser, TokenInvalid(provider.name))
      log.info("[" + parameter.serviceName + "] No Token")
      Future(None)
    } else {
      log.error("Provider " + provider.name + " havn't parser for " + parameter.serviceName)
      CmdToUser.sendTo(idUser, Error(provider.name, ErrorType.NoParser, Some(parameter.columnName)))
      Future(None)
    }
  }

}