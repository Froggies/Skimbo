package services.actors

import play.api.Logger
import play.api.mvc.RequestHeader
import services.endpoints.Endpoints
import scala.util.Success
import scala.util.Failure
import play.api.http.Status
import services.commands.CmdToUser
import models.command.Command
import models.command.Error
import models.command.TokenInvalid
import models.Skimbo
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.Future

object Fetcher {

  val log = Logger(Fetcher.getClass())

  def apply(parameter: ProviderActorParameter, sinceId: String)(implicit request: RequestHeader): Future[Option[List[Skimbo]]] = {

    val provider = parameter.provider
    val unifiedRequest = parameter.unifiedRequest
    val idUser = parameter.idUser
    val parser = parameter.parser
    val column = parameter.column

    log.info("[" + unifiedRequest.service + "] Fetching")

    if (provider.canStart) {
      val optSinceId = if (sinceId.isEmpty) None else Some(sinceId)
      val url = Endpoints.genererUrl(unifiedRequest.service, unifiedRequest.args.getOrElse(Map.empty), optSinceId);
      val config = Endpoints.getConfig(unifiedRequest.service).get

      log.info("[" + unifiedRequest.service + "] " + url.get)

      if (url.isDefined) {
        provider.fetch(url.get).withTimeout(config.delay * 1000).get.map { response =>
          if (response.status != Status.OK) {
            log.error("[" + unifiedRequest.service + "] HTTP Error " + response.status)
            log.info(response.body.toString)
            if (provider.isInvalidToken(response)) {
              CmdToUser.sendTo(idUser, TokenInvalid(provider.name))
              None
            } else if (provider.isRateLimiteError(response)) {
              CmdToUser.sendTo(idUser, Error(provider.name, "Rate limite exceeded on"))
              Some(List.empty[Skimbo])
            } else {
              CmdToUser.sendTo(idUser, Error(provider.name, "Error in column " + column.title + " with"))
              None
            }
          } else {
            val skimboMsgs = parser.get.getSkimboMsg(response, provider)
            if (skimboMsgs.isEmpty) {
              log.error("[" + unifiedRequest.service + "] Unexpected result")
              log.info(response.body.toString)
              CmdToUser.sendTo(idUser, Error(provider.name, "Unexpected result on"))
              None
            } else {
              skimboMsgs
            }
          }
        }
      } else {
        log.error("[" + unifiedRequest.service + "] Bad url" + unifiedRequest.args)
        Future(None)
      }
    } else if (provider.isAuthProvider && !provider.canStart) {
      CmdToUser.sendTo(idUser, TokenInvalid(provider.name))
      log.info("[" + unifiedRequest.service + "] No Token")
      Future(None)
    } else {
      log.error("Provider " + provider.name + " havn't parser for " + unifiedRequest.service)
      CmdToUser.sendTo(idUser, Error(provider.name, "No parser on"))
      Future(None)
    }
  }

}