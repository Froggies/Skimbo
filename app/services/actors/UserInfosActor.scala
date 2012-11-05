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
import models.ProviderUser
import scala.concurrent.Future

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
      findUser { user =>
        println("User !!!"+user)
        
          println("in user")
          endpoints.foreach { endpoint =>
            println("in endpoint ")+endpoint.provider.name
            if (endpoint.provider.hasToken(request)) { //TODO RM : remove when api endpoint from JL was done
              println("has token for "+endpoint.provider.name)
              val notFound = user.distants.getOrElse(Seq()).count { provider =>
                provider.socialType == endpoint.provider.name
              } == 0
              if (notFound) {
                println("endpoint not found "+endpoint.provider.name)
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

  def findUser(callback:(User) => Any) = {
    UserDao.findOneById(idUser).onSuccess { 
      case optionUser =>
        if(optionUser.isDefined) { 
          println("FIND USER BY ID NONE !!!")
          callback(optionUser.get)
        } else {
          findByIdProviders(endpoints, { optionuser =>
              println("FIND USER BY ID PROVIDER RETURN "+endpoints.head.provider.name+" !!!")
              callback(optionuser.getOrElse {
                println("CREATE USER "+endpoints.head.provider.name+" !!!")
                UserDao.add(User(idUser)).await(1000).get
              })
          })
        }
    }
  }
  
  def findByIdProviders(endpoints:Seq[Endpoint], callback:(Option[User]) => Any):Any = {
    if(endpoints.size == 0) {
      println("NONE FIND USER !!!")
      Future(None)
    } else {
      findByIdProvider(endpoints.head).map { user =>
        println("FIND USER BY ID PROVIDER "+endpoints.head.provider.name+" !!!")
        user.map { u =>
          println("FIND USER BY ID PROVIDER MAP "+endpoints.head.provider.name+" !!!")
          callback(user)
        }.orElse {
          println("RECURSION USER !!!")
          findByIdProviders(endpoints.splitAt(1)._2, callback)
          Some()
        }
      }
    }
  }
  
  def findByIdProvider(endpoint:Endpoint):Future[Option[User]] = {
    endpoint.provider.getUser.flatMap { providerUser =>
      println("FIND DISTANT USER !!!")
      if(providerUser.isDefined) {
        UserDao.findByIdProvider(endpoint.provider.name, providerUser.get.id)
      } else {
        Future(None)
      }
    }
  }

}