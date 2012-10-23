package model;

import services.auth._

import java.util.concurrent.TimeUnit

import play.libs.{Akka => KK}
import play.api._
import play.api.Play.current
import play.api.mvc._
import play.api.libs._
import play.api.libs.json._
import play.api.libs.ws.{Response => ResponseWS, _}
import play.api.libs.iteratee._
import play.api.libs.concurrent._
import play.api.libs.concurrent.execution.defaultContext

import akka.actor._
import akka.actor.ReceiveTimeout
import akka.util._

object UserActor {
  def create(out:Enumerator[String], request:RequestHeader, provider:GenericProvider, token:Any, endUrl:String):ActorRef = {
    //new User(provider, token, endUrl);//not actor yet
    //val system = ActorSystem("ActorSystem")
    Akka.system.actorOf(Props(new UserActor(out, request, provider, token, endUrl)), "myactor")
  }
}

class UserActor(out:Enumerator[String], r:RequestHeader, provider:GenericProvider, token:Any, endUrl:String) extends Actor {

  private val interval = 5;//Config.getLong("mail.check.interval")
  context.setReceiveTimeout(Duration(interval, TimeUnit.SECONDS))

  def receive = {
    case ReceiveTimeout => {
      self ! Dispatch          
    }
    case Dispatch => dispatch()
    case e: Exception => println("unexpected Error: " + e)
  }

  def dispatch() { 
      implicit var request = r; 
      sender ! provider.fetch(endUrl).get; 
  }
}

case class Dispatch()

class GenericPost {

}

object SseActor {

  /*val toGenericPost: Enumeratee[JsValue, GenericPost] = Enumeratee.map[JsValue] {
    case e@Operation(_, amout) if amout > lowerBound && amout < higherBound => e
    case e@SystemStatus(_) => e
  }*/

  def operations(r:RequestHeader, provider:GenericProvider, endUrl:String): 
    Enumerator[JsValue] = Enumerator.generateM[JsValue] {
      //KK.asPromise(User.create(Enumerator(), r, provider, "", endUrl) ! Dispatch);
      implicit var request = r; 
      provider.fetch(endUrl).get.map{r => Some(r.json)};
  }
}