package services.actors

import akka.actor._
import akka.util.duration.intToDurationInt
import play.api.libs.concurrent.futureToPlayPromise
import play.api.libs.iteratee.{ Concurrent, Enumerator }
import play.api.libs.json.{ JsValue, Json }
import play.api.mvc.RequestHeader
import play.libs.Akka
import play.api.UnexpectedException
import play.api.Logger
import controllers.UserDao
import models.User
import play.api.libs.iteratee.Iteratee
import reactivemongo.bson.handlers.DefaultBSONHandlers._
import reactivemongo.bson._
import scala.collection.mutable.ListBuffer

case class Refresh()

object UserInfosActor {

  val system: ActorSystem = ActorSystem("userInfos");

  def apply(idUser: String, endpoints: Seq[Endpoint])(implicit request: RequestHeader) = {
    system.actorOf(Props(new UserInfosActor(idUser, endpoints))) ! Refresh
  }
}

class UserInfosActor(idUser: String, endpoints: Seq[Endpoint])(implicit request: RequestHeader) extends Actor {

  import play.api.libs.concurrent.execution.defaultContext

  def receive() = {
    case Refresh => {
      UserDao.findOrCreate(idUser).foreach { user =>
        endpoints.foreach { endpoint =>
          if (endpoint.provider.hasToken(request)) { //TODO RM : remove when api endpoint from JL was done
            val notFound = user.distants.getOrElse(Seq()).count { provider =>
              provider.socialType == endpoint.provider.name
            } == 0
            if (notFound) {
              endpoint.provider.getUser.foreach { providerUser =>
                providerUser.map { pu =>
                  val distants = new ListBuffer[models.ProviderUser]() //TODO JL is good ?
                  distants ++= user.distants.getOrElse(Seq())
                  distants += pu
                  val userModif = User(endpoint.idUser, Some(distants.toList))
                  UserDao.update(userModif)
                }
              }
            }
          }
        }
      }
      // TODO RM : find when kill actor
      //. onDone {
      //  self ! Dead
      //}
    }
    case Dead => {
      context.stop(self)
    }
    case e: Exception => throw new UnexpectedException(Some("Incorrect message receive"), Some(e))
  }

}