package services.auth

import providers._
import play.api.mvc._
import play.api.Play.current
import play.api.libs.ws.WS.WSRequestHolder
import java.util.UUID
import models.User
import play.api.libs.concurrent.futureToPlayPromise
import play.api.Logger
import scala.concurrent.Future
import play.api.libs.concurrent.execution.defaultContext
import services.auth.actions._

trait GenericProvider extends Results with WsProvider with AccountWsProvider with SecurityProvider {

  // Generic provider settings (override it)
  def name: String
  protected def namespace: String

  protected def permissions: Seq[String] = Seq.empty
  protected def permissionsSep = ","

  // From config file
  protected lazy val config = current.configuration.getConfig("social." + name).get
  protected lazy val logo = config.getString("urlLogo").get

  // Common config
  lazy val authRoute: Call = controllers.routes.Application.authenticate(name)

}