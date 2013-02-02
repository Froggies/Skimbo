package services.actors

import scala.collection.mutable.HashMap
import services.UserDao
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.Future
import akka.actor.ActorSystem
import akka.actor.Props
import play.api.mvc.RequestHeader
import akka.actor.ActorRef
import services.commands.CmdFromUser
import models.command.Command
import services.auth.GenericProvider
import services.endpoints.JsonRequest.UnifiedRequest
import parser.GenericParser
import models.user.Column
import play.api.Logger
import models.user.Account
import java.util.Date

trait ActorHelper[P] {

  val log = Logger("ActorHelper")
  protected val actors = new HashMap[String, List[(ActorRef,P)]]

  protected def found(idUser: String): Option[List[(ActorRef,P)]] = {
    actors.get(idUser)
  }
  
  def delete(idUser: String) = {
    actors.remove(idUser)
  }
  
  protected def same(p:P, otherP: P):Boolean
  protected def create(p:P)(implicit request: RequestHeader):ActorRef
  protected def exist(p:P, actor:ActorRef)(implicit request: RequestHeader)
  
  def foundOrCreate(idUser: String, p:P)(implicit request: RequestHeader):Future[ActorRef] = {
    val optT = found(idUser)
    log.info("first found = "+optT)
    val isSame = optT.getOrElse(List.empty).filter(o => same(p, o._2))
    if(!isSame.isEmpty) {
      log.info("first exist")
      exist(optT.get.head._2, optT.get.head._1)
      Future(optT.get.head._1)
    } else {
      log.info("find with other account")
      UserDao.findOneById(idUser).map(_.map { user =>
        log.info("find user "+user)
        val alreadyRun = user.accounts.filter( account => found(account.id).isDefined)
        log.info("find user "+alreadyRun)
        val alreadyP = found(alreadyRun.headOption.getOrElse(Account("", new Date())).id)
        val isSame = alreadyP.getOrElse(List.empty).filter(o => same(p, o._2))
        if(!isSame.isEmpty) {
          val t = isSame.head
          log.info("find account "+t)
          exist(t._2, t._1)
          t._1
        } else if(!alreadyRun.isEmpty) {
          val t = create(p)
          log.info("create 1 "+t)
          actors.put(alreadyRun.head.id, found(alreadyRun.head.id).get ++ List((t, p)))
          t
        } else {
          val t = create(p)
          log.info("create 1 "+t)
          actors.put(idUser, List((t, p)))
          t
        }
      }.getOrElse {
        val t = create(p)
        log.info("create 2 "+t)
        actors.put(idUser, List((t, p)))
        t
      })
    }
  }

}

object HelperUserInfosActor extends ActorHelper[String] {
  
  val system = ActorSystem("userInfos")
  
  def foundOrCreate(idUser: String)(implicit request: RequestHeader):Future[ActorRef] = {
    super.foundOrCreate(idUser, idUser);
  }
  
  protected def same(idUser:String, otherId:String) = {
    println("HelperUserInfos eq : "+idUser+" == "+otherId+ " = "+(idUser == otherId))
    true//for speed : because in ActorHelper check in daouser.found so don't need to re-check
  }
  
  protected def create(idUser:String)(implicit request: RequestHeader) = {
    println("UserInfosActor NEW !")
    val actor = system.actorOf(Props(new UserInfosActor(idUser)))
    system.eventStream.subscribe(actor, Retreive.getClass())
    system.eventStream.subscribe(actor, classOf[StartProvider])
    system.eventStream.subscribe(actor, classOf[Dead])
    system.eventStream.subscribe(actor, classOf[RefreshInfosUser])
    actor
  }
  
  protected def exist(id:String, actor:ActorRef)(implicit request: RequestHeader) = {
    println("UserInfosActor EXIST in "+actors.size)
    CmdFromUser.interpretCmd(id, Command("allColumns"))
    actor ! CheckAccounts(id)
    ProviderActor.restart(id)
  }
  
}

class HelperProviderActor(system: ActorSystem) extends ActorHelper[ProviderActorParameter] {
  
  protected def same(parameter:ProviderActorParameter, other:ProviderActorParameter) = {
    parameter.provider.name == other.provider.name &&
    parameter.unifiedRequest.service == other.unifiedRequest.service &&
    parameter.column.title == other.column.title
  }
  
  protected def create(parameter:ProviderActorParameter)(implicit request: RequestHeader) = {
    println("ProviderActor NEW !")
    val actor = system.actorOf(Props(new ProviderActor(parameter)))
    system.eventStream.subscribe(actor, classOf[Dead])
    system.eventStream.subscribe(actor, classOf[DeadColumn])
    system.eventStream.subscribe(actor, classOf[Ping])
    system.eventStream.subscribe(actor, classOf[DeadProvider])
    system.eventStream.subscribe(actor, classOf[Restart])
    actor
  }
  
  protected def exist(parameter:ProviderActorParameter, actor:ActorRef)(implicit request: RequestHeader) = {
    println("ProviderActor EXIST !")
    actor ! Restart(parameter.idUser)
  }
  
}