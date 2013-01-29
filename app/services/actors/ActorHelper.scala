package services.actors

import scala.collection.mutable.HashMap
import services.UserDao
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.Future

class ActorHelper[T] {

  private val actors = new HashMap[String, T]

  protected def found(idUser: String): Option[T] = {
    actors.get(idUser)
  }
  
  def foundOrCreate(idUser: String, create: => T, exist:(String,T) => Any):Future[T] = {
    val optUser = found(idUser)
    if(optUser.isDefined) {
      exist(idUser, optUser.get)
      Future(optUser.get)
    } else {
      UserDao.findOneById(idUser).map(_.map { user =>
        val alreadyRun = user.accounts.filter( account => found(account.id).isDefined)
        if(alreadyRun.isEmpty) {
          val t = create
          actors.put(idUser, t)
          t
        } else {
          val t = found(alreadyRun.head.id).get
          exist(alreadyRun.head.id, t)
          t
        }
      }.getOrElse {
        val t = create
        actors.put(idUser, t)
        t
      })
    }
  }

  def delete(idUser: String) = {
    actors.remove(idUser)
  }

}