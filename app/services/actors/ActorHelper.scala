package services.actors

import scala.collection.mutable.HashMap
import services.dao.UserDao
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
import scala.annotation.tailrec

trait ActorHelper[P] {

  val log = Logger("ActorHelper")
  protected val actors = new HashMap[String, List[(ActorRef, P)]]

  def getNbAccount = {
    actors.size
  }

  def getNbActor = {
    sum(actors.values.map(_.size).toList)
  }

  def sum(xs: List[Int]): Int = {
    @tailrec
    def inner(xs: List[Int], accum: Int): Int = {
      xs match {
        case x :: tail => inner(tail, accum + x)
        case Nil => accum
      }
    }
    inner(xs, 0)
  }

  protected def found(idUser: String): Option[List[(ActorRef, P)]] = {
    actors.get(idUser)
  }

  def delete(idUser: String, p: P) = synchronized {
    log.info(logName(p, " delete "))
    found(idUser).map { list =>
      val notRemove = list.filterNot(otherP => same(p, otherP._2))
      log.info(logName(p, " delete not remove " + notRemove.size))
      if (!notRemove.isEmpty) {
        actors.put(idUser, notRemove)
      } else {
        actors.remove(idUser)
      }
    }
  }
  
  private def add(idUser: String, param: (ActorRef, P)) = synchronized {
    actors.get(idUser).map(x => actors.put(idUser, x ++ List(param))).getOrElse(actors.put(idUser, List(param)))
  }

  protected def same(p: P, otherP: P): Boolean
  protected def create(p: P): ActorRef
  protected def exist(p: P, actor: ActorRef)
  protected def logName(p: P, msg: String): String

  def foundOrCreate(idUser: String, p: P): Future[ActorRef] = {
    val optT = found(idUser)
    log.info(logName(p, "first found = "))
    val isSame = optT.getOrElse(List.empty).filter(o => same(p, o._2))
    if (!isSame.isEmpty) {
      log.info(logName(p, "first exist "))
      exist(optT.get.head._2, optT.get.head._1)
      Future(optT.get.head._1)
    } else {
      log.info(logName(p, "find with other account "))
      UserDao.findOneById(idUser).map(_.map { user =>
        log.info(logName(p, "find user "))
        val alreadyRun = user.accounts.filter(account => found(account.id).isDefined)
        log.info(logName(p, "find account "))
        val alreadyP = found(alreadyRun.headOption.getOrElse(Account("", new Date())).id)
        val isSame = alreadyP.getOrElse(List.empty).filter(o => same(p, o._2))
        if (!isSame.isEmpty) {
          val t = isSame.head
          log.info(logName(p, "find account "))
          exist(t._2, t._1)
          t._1
        } else if (!alreadyRun.isEmpty) {
          val t = create(p)
          log.info(logName(p, "create 1 "))
          add(alreadyRun.head.id, (t, p))
          t
        } else {
          val t = create(p)
          log.info(logName(p, "create 2 "))
          add(idUser, (t, p))
          t
        }
      }.getOrElse {
        val t = create(p)
        log.info(logName(p, "create 3 "))
        add(idUser, (t, p))
        t
      })
    }
  }

}

object HelperUserInfosActor extends ActorHelper[String] {

  val system = ActorSystem("userInfos")

  def foundOrCreate(idUser: String): Future[ActorRef] = {
    super.foundOrCreate(idUser, idUser);
  }

  def delete(idUser: String) = {
    actors.remove(idUser)
  }

  protected def same(idUser: String, otherId: String) = {
    println("HelperUserInfos eq : " + idUser + " == " + otherId + " = " + (idUser == otherId))
    true //for speed : because in ActorHelper check in daouser.found so don't need to re-check
  }

  protected def create(idUser: String) = {
    println("UserInfosActor NEW !")
    val actor = system.actorOf(Props(new UserInfosActor(idUser)))
    system.eventStream.subscribe(actor, Retreive.getClass())
    system.eventStream.subscribe(actor, classOf[StartProvider])
    system.eventStream.subscribe(actor, classOf[Dead])
    system.eventStream.subscribe(actor, classOf[RefreshInfosUser])
    actor
  }

  protected def exist(id: String, actor: ActorRef) = {
    println("UserInfosActor EXIST in " + actors.size)
    CmdFromUser.interpretCmd(id, Command("allColumns"))
    actor ! Retreive
    ProviderActor.restart(id)
  }

  protected def logName(id: String, msg: String): String = {
    "UserInfosActor " + msg + id
  }

}

object HelperProviderActor extends ActorHelper[ProviderActorParameter] {

  val system = ActorSystem("providers")

  protected def same(parameter: ProviderActorParameter, other: ProviderActorParameter) = {
    parameter.provider.name == other.provider.name &&
      parameter.unifiedRequest.service == other.unifiedRequest.service &&
      parameter.column.title == other.column.title
  }

  protected def create(parameter: ProviderActorParameter) = {
    println("ProviderActor NEW !")
    val actor = system.actorOf(Props(new ProviderActor(parameter)))
    system.eventStream.subscribe(actor, classOf[Dead])
    system.eventStream.subscribe(actor, classOf[DeadColumn])
    system.eventStream.subscribe(actor, classOf[Ping])
    system.eventStream.subscribe(actor, classOf[DeadProvider])
    system.eventStream.subscribe(actor, classOf[Restart])
    actor
  }

  protected def exist(parameter: ProviderActorParameter, actor: ActorRef) = {
    println("ProviderActor EXIST !")
    actor ! Restart(parameter.idUser)
  }

  protected def logName(parameter: ProviderActorParameter, msg: String): String = {
    "ProviderActor " + msg + parameter.column.title + " : " + parameter.provider.name
  }

}