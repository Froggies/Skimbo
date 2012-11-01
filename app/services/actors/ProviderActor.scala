package services.actors;

import akka.actor._
import akka.util.duration._
import play.api.libs.concurrent.futureToPlayPromise
import play.api.libs.iteratee._
import play.api.libs.json._
import play.api.mvc.RequestHeader
import play.libs.Akka
import services.auth.GenericProvider
import play.api.libs.concurrent.Promise
import play.api.mvc.Result

case class UserIdActors(id: String, actors: Seq[ActorRef])

object ProviderActor {

  val (enumUserIdActors, channelUserIdActors) = Concurrent.broadcast[UserIdActors]

  def create(endpoints: Seq[Endpoint])(implicit request: RequestHeader): Enumerator[JsValue] = {
    val (rawStream, channel) = Concurrent.broadcast[JsValue]
    val actors = endpoints.map({ endpoint =>
      val actor = Akka.system.actorOf(Props(new ProviderActor(channel, endpoint)))
      rawStream.onDoneEnumerating({
        println("ACTOR DONE " + endpoint.provider.name)
        actor ! Kill
      })
      actor
    })
    val userId = request.session.get("id").getOrElse("None") //FIXME : meilleurs moyen pour récupérer l'unique id
    println("ACTOR PUSH " + userId + " :: " + actors)
    channelUserIdActors.push(UserIdActors(userId, actors))
    rawStream
  }

  /*val ite = Iteratee.fold[UserIdActors, UserIdActors]({
    userIdActors:UserIdActors => userIdActors.actors.foreach({
        println("ACTOR FOUND KILL SEND")
        actor => actor ! Kill
      })
      userIdActors
  })*/

  def killActorsForUser(userId: String) = {
    println("ACTOR KILL SEARCH " + enumUserIdActors)

    lazy val filter = Enumeratee.filter[UserIdActors]({ uia =>
      val b = uia.id == userId
      println("ACTOR FILTER " + b)
      b
    })

    lazy val onRun = Enumeratee.map[UserIdActors](
      userIdActors => {
        userIdActors.actors.foreach({
          println("ACTOR FOUND KILL SEND")
          actor => actor ! Kill
        })
        userIdActors
      })

    val toStr = Enumeratee.map[UserIdActors](uia => uia.toString)

    enumUserIdActors &> filter &> onRun &> toStr
  }

}

class ProviderActor(channel: Concurrent.Channel[JsValue], endpoint: Endpoint)(implicit request: RequestHeader) extends Actor {

  context.setReceiveTimeout(endpoint.interval seconds)

  def receive = {
    case ReceiveTimeout => {
      println("ACTOR GO " + endpoint.provider.name)
      endpoint.provider.fetch(endpoint.url).get.await(10000).fold( // TODO : Virer cet await que je ne saurais voir !
        error => {
          channel.push(Json.toJson("error with " + endpoint.provider.name))
          self ! Kill
        },
        response => channel.push(response.json))
    }
    case Kill => {
      println("ACTOR KILL " + endpoint.provider.name)
      context.stop(self)
    }
    case e: Exception => println("unexpected Error: " + e)
  }

}