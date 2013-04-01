package services.dao;

import java.util.Date
import scala.concurrent.Future
import models.User.UserBSONReader
import models.user.Account
import models.user.Column
import models.user.ProviderUser
import models.user.SkimboToken
import play.api.Play.current
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.modules.reactivemongo.ReactiveMongoPlugin
import reactivemongo.bson.BSONDocument
import reactivemongo.bson.BSONString
import reactivemongo.bson.handlers.DefaultBSONHandlers.DefaultBSONDocumentWriter
import reactivemongo.bson.handlers.DefaultBSONHandlers.DefaultBSONReaderHandler
import services.auth.GenericProvider
import services.auth.ProviderDispatcher

object UserDao {

  import play.api.libs.concurrent.Execution.Implicits._
  import models.User._

  val db = ReactiveMongoPlugin.db
  val collection = db("users")

  def add(user: models.User) = {
    collection.insert(models.User.toBSON(user))
  }

  def findAll(): Future[List[models.User]] = {
    val query = BSONDocument()
    collection.find(query).toList
  }

  def findOneById(idUser: String): Future[Option[models.User]] = {
    val query = BSONDocument("accounts.id" -> new BSONString(idUser))
    collection.find(query).headOption()
  }

  def updateLastUse(idUser: String) = {
    val query = BSONDocument("accounts.id" -> new BSONString(idUser))
    val update = BSONDocument(
      "$set" -> BSONDocument(
        "accounts.$" -> models.user.Account.toBSON(models.user.Account(idUser, new Date()))))
    collection.update(query, update)
  }

  def addAccount(idUser: String, account: models.user.Account) = {
    val query = BSONDocument("accounts.id" -> new BSONString(idUser))
    val update = BSONDocument("$push" -> BSONDocument("accounts" -> models.user.Account.toBSON(account)))
    collection.update(query, update)
  }

  def addColumn(idUser: String, column: models.user.Column) = {
    val query = BSONDocument("accounts.id" -> new BSONString(idUser))
    val update = BSONDocument("$push" -> BSONDocument("columns" -> models.user.Column.toBSON(column)))
    collection.update(query, update)
  }

  def addProviderUser(idUser: String, providerUser: models.user.ProviderUser) = {
    getToken(idUser, ProviderDispatcher.get(providerUser.socialType).get).map(optToken =>
      optToken.map(token =>
        setToken(
          idUser,
          ProviderDispatcher.get(providerUser.socialType).get,
          token,
          Some(providerUser.id))))
  }

  def updateColumn(idUser: String, title: String, column: Column) = {
    val query = BSONDocument(
      "accounts.id" -> new BSONString(idUser),
      "columns.title" -> new BSONString(title))
    val update = BSONDocument("$set" -> BSONDocument("columns.$" -> Column.toBSON(column)))
    collection.update(query, update)
  }

  def findByIdProvider(provider: String, id: String): Future[Option[models.User]] = {
    val query = BSONDocument(
      "distants.social" -> new BSONString(provider),
      "distants.id" -> new BSONString(id))
    collection.find(query).headOption()
  }

  def deleteColumn(idUser: String, columnTitle: String) = {
    val query = BSONDocument("accounts.id" -> new BSONString(idUser))
    val update = BSONDocument("$pull" -> BSONDocument("columns" -> BSONDocument("title" -> new BSONString(columnTitle))))
    collection.update(query, update)
  }

  def delete(idUser: String) = {
    val query = BSONDocument("accounts.id" -> new BSONString(idUser))
    collection.remove(query)
  }

  def hasToken(idUser: String, provider: GenericProvider): Future[Boolean] = {
    getToken(idUser, provider).map(_.isDefined)
  }

  def getToken(idUser: String, provider: GenericProvider): Future[Option[SkimboToken]] = {
    val query = BSONDocument("accounts.id" -> new BSONString(idUser))
    collection.find(query)
      .headOption().map( _.flatMap( 
            _.distants.flatMap( 
                _.filter( _.socialType == provider.name ).headOption.flatMap( 
                    _.token ))))
  }

  def setToken(idUser: String, provider: GenericProvider, token: SkimboToken, distantId: Option[String] = None) = {
    val query = BSONDocument("accounts.id" -> new BSONString(idUser))
    findOneById(idUser).flatMap { user =>
      val toUpdate =
        if (user.isDefined) {
          val exist = user.get.distants.getOrElse(Seq[ProviderUser]()).exists {
            _.socialType == provider.name
          }
          val providersUser =
            if (exist) {
              user.get.distants.getOrElse(Seq[ProviderUser]()).map { distant =>
                if (distant.socialType == provider.name) {
                  ProviderUser(distantId.getOrElse(distant.id), distant.socialType, Some(token))
                } else {
                  distant
                }
              }
            } else {
              Seq(ProviderUser(
                distantId.getOrElse(""),
                provider.name, Some(token))) ++ user.get.distants.getOrElse(Seq[ProviderUser]())
            }
          models.User(user.get.accounts, Some(providersUser), user.get.columns)
        } else {
          models.User(
            Seq(Account(idUser, new Date())),
            Some(Seq(ProviderUser(distantId.getOrElse(""), provider.name, Some(token)))),
            None)
        }
      collection.update(query, models.User.toBSON(toUpdate), upsert = true)
    }
  }

  def removeToken(idUser: String, provider: GenericProvider) = {
    val query = BSONDocument("accounts.id" -> new BSONString(idUser))
    val update = BSONDocument("$pull" -> BSONDocument("distants" -> BSONDocument("social" -> new BSONString(provider.name))))
    collection.update(query, update)
  }

  def merge(fromUserId: String, toUserId: String, onDone: => Any = {}) = {
    findOneById(fromUserId).map(_.map { fromUser =>
      findOneById(toUserId).map(_.map { toUser =>
        if (fromUser.accounts.head.id != toUser.accounts.head.id) {
          val accounts = fromUser.accounts ++ toUser.accounts
          val columns = fromUser.columns.getOrElse(Seq()) ++ toUser.columns.getOrElse(Seq())
          val distants = fromUser.distants.getOrElse(Seq()).filter( _.id != "") ++ toUser.distants.getOrElse(Seq()).filter( _.id != "")
          val newUser = models.User(accounts, Some(distants), Some(columns))
          val query = BSONDocument("accounts.id" -> new BSONString(toUserId))
          delete(fromUserId) onSuccess {
            case _ => collection.update(query, models.User.toBSON(newUser)) onSuccess {
              case _ => onDone
            }
          }
        } else {
          onDone
        }
      })
    })
  }

}