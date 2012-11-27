package controllers;

import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.Play.current
import scala.concurrent.future
import services.endpoints.JsonRequest._
import play.modules.reactivemongo._
import scala.concurrent.{ ExecutionContext, Future }
import reactivemongo.api._
import reactivemongo.bson._
import reactivemongo.bson.handlers.DefaultBSONHandlers.DefaultBSONDocumentWriter
import reactivemongo.bson.handlers.DefaultBSONHandlers.DefaultBSONReaderHandler
import reactivemongo.core.commands.LastError
import services.auth.GenericProvider
import models.user._
import java.util.Date
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

  def findOneById(id: String): Future[Option[models.User]] = {
    val query = BSONDocument("accounts.id" -> new BSONString(id))
    collection.find(query).headOption()
  }

  def addAccount(user: models.User, account: models.user.Account) = {
    val query = BSONDocument("accounts.id" -> new BSONString(user.accounts.head.id))
    val update = BSONDocument("$push" -> BSONDocument("accounts" -> models.user.Account.toBSON(account)))
    collection.update(query, update)
  }

  def addColumn(user: models.User, column: models.user.Column) = {
    val query = BSONDocument("accounts.id" -> new BSONString(user.accounts.head.id))
    val update = BSONDocument("$push" -> BSONDocument("columns" -> models.user.Column.toBSON(column)))
    collection.update(query, update)
  }

  def addProviderUser(user: models.User, providerUser: models.user.ProviderUser) = {
    getToken(user.accounts.head.id, ProviderDispatcher.get(providerUser.socialType).get).map(optToken =>
      optToken.map(token =>
        setToken(
          user.accounts.head.id,
          ProviderDispatcher.get(providerUser.socialType).get,
          token,
          Some(providerUser.id))))
  }

  def updateColumn(user: models.User, title: String, column: Column) = {
    deleteColumn(user, title)
    val query = BSONDocument("accounts.id" -> new BSONString(user.accounts.head.id))
    val update = BSONDocument("$push" -> BSONDocument("columns" -> Column.toBSON(column)))
    collection.update(query, update)
  }

  def findByIdProvider(provider: String, id: String): Future[Option[models.User]] = {
    val query = BSONDocument(
      "distants.social" -> new BSONString(provider),
      "distants.id" -> new BSONString(id))
    collection.find(query).headOption()
  }

  def deleteColumn(user: models.User, columnTitle: String) = {
    val query = BSONDocument("accounts.id" -> new BSONString(user.accounts.head.id))
    val update = BSONDocument("$pull" -> BSONDocument("columns" -> BSONDocument("title" -> new BSONString(columnTitle))))
    collection.update(query, update)
  }

  def delete(user: models.User) = {
    val query = BSONDocument("accounts.id" -> new BSONString(user.accounts.head.id))
    collection.remove(query)
  }

  def hasToken(idUser: String, provider: GenericProvider): Future[Boolean] = {
    getToken(idUser, provider).map(_.isDefined)
  }

  def getToken(idUser: String, provider: GenericProvider): Future[Option[SkimboToken]] = {
    val query = BSONDocument("accounts.id" -> new BSONString(idUser))
    collection.find(query).headOption().map { optUser =>
      if (optUser.isDefined) {
        val distant = optUser.get.distants.getOrElse(Seq()).filter { distant =>
          distant.socialType == provider.name
        }
        if (distant.size == 0) {
          None
        } else {
          distant.head.token
        }
      } else {
        None
      }
    }
  }

  def setToken(idUser: String, provider: GenericProvider, token: SkimboToken, distantId: Option[String] = None) = {
    val query = BSONDocument("accounts.id" -> new BSONString(idUser))
    findOneById(idUser).map { user =>
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
      collection.update(query, models.User.toBSON(toUpdate), upsert = true);
    }
  }

  def removeToken(idUser: String, provider: GenericProvider) = {
    val query = BSONDocument("accounts.id" -> new BSONString(idUser))
    val update = BSONDocument("$pull" -> BSONDocument("distants" -> BSONDocument("social" -> new BSONString(provider.name))))
    collection.update(query, update)
  }

  def merge(fromUser: models.User, toUser: models.User) = {
    delete(fromUser) map { _ =>
      fromUser.accounts.foreach(account => addAccount(toUser, account))
      fromUser.columns.map(_.foreach(column => addColumn(toUser, column)))
      fromUser.distants.map(_.foreach { distant =>
        if (!distant.id.isEmpty()) { // new user hasn't id
          addProviderUser(toUser, distant)
        }
      })
    } map { _ =>
      findOneById(toUser.accounts.head.id)
    }
  }

}