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
import scala.concurrent._
import scala.annotation.tailrec
import akka.util.Duration
import services.auth.GenericProvider

case class Refresh()

object UserInfosActor {

  val system: ActorSystem = ActorSystem("userInfos");

  def apply(idUser: String)(implicit request: RequestHeader) = {
    system.actorOf(Props(new UserInfosActor(idUser))) ! Refresh
  }
}

class UserInfosActor(idUser: String)(implicit request: RequestHeader) extends Actor {

  import play.api.libs.concurrent.execution.defaultContext

  def receive() = {
    case Refresh => {
      //findUser()
//      findUser { user =>
//        println("User !!!!!!!!"+user)
//        
//          println("in user")
//          endpoints.foreach { endpoint =>
//            println("in endpoint ")+endpoint.provider.name
//            if (endpoint.provider.hasToken(request)) { //TODO RM : remove when api endpoint from JL was done
//              println("has token for "+endpoint.provider.name)
//              val notFound = user.distants.getOrElse(Seq()).count { provider =>
//                provider.socialType == endpoint.provider.name
//              } == 0
//              if (notFound) {
//                println("endpoint not found "+endpoint.provider.name)
//                endpoint.provider.getUser.foreach { providerUser =>
//                  providerUser.map { pu =>
//                    val distants = new ListBuffer[models.ProviderUser]() //TODO JL is good ?
//                    distants ++= user.distants.getOrElse(Seq())
//                    distants += pu
//                    val userModif = User(endpoint.idUser, Some(distants.toList))
//                    UserDao.update(userModif)
//                  }
//                }
//              }
//            }
//          }
//      }
      
      
      
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

//  def findUser() = {
//    val byId = UserDao.findOneById(idUser)
//    val byIdProvider = findByIdProviders(endpoints)
//    val create = UserDao.add(User(idUser))
//    
//    val anyQuote = byId fallbackTo byIdProvider fallbackTo create
//    anyQuote onSuccess { 
//      case u => println("FINISH "+u) 
//    }
//    
//  }
//  
//  def findByIdProviders(endpoints: => Seq[Endpoint]) = {
//    @tailrec
//    def findByIdProviders_(endpoints:Seq[Endpoint]):Future[Option[User]] = {
//      if(endpoints.size == 0) {
//        println("NONE FIND USER !!!")
//        Future(None)
//      } else {
//        val maybeUser = findByIdProvider(endpoints.head)
//        val v = for { user <- maybeUser } yield {
//          println("FIND USER BY ID PROVIDER "+endpoints.head.provider.name+" !!!")
//          if(user.isDefined) {
//            println("FIND USER BY ID PROVIDER MAP "+endpoints.head.provider.name+" !!!")
//            user
//          } else {
//            println("RECURSION USER !!!")
//            None
//          }
//        }
//        findByIdProviders_(endpoints.splitAt(1)._2)
//      }
//    }
//    findByIdProviders_(endpoints)
//  }
  
  def findByIdProvider(provider:GenericProvider):Future[Option[User]] = {
    provider.getUser.flatMap { providerUser =>
      println("FIND DISTANT USER !!!;"+providerUser.get)
      if(providerUser.isDefined) {
        UserDao.findByIdProvider(provider.name, providerUser.get.id)
      } else {
        Future(None)
      }
    }
  }

}