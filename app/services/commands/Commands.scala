package services.commands
import play.api.libs.json.JsValue
import play.api.Logger
import play.api.libs.iteratee.Concurrent
import services.actors.UserInfosActor
import controllers.UserDao
import models.User
import play.api.libs.json.JsArray
import play.api.libs.json.JsObject
import play.api.libs.json.JsString
import models.user.Column
import models.Service
import play.api.mvc.RequestHeader
import play.api.libs.json.Json
import model.command.Command

object Commands {

  val log = Logger(Commands.getClass())
  
  def interpret(idUser: String, json: JsValue)(implicit context: scala.concurrent.ExecutionContext, req: RequestHeader):Any = {
    val cmd = Json.fromJson[Command](json).getOrElse(Command("_"))
    interpretCmd(idUser, cmd)
  }
  
  def interpretCmd(idUser: String, cmd: Command)(implicit context: scala.concurrent.ExecutionContext, req: RequestHeader):Any = {
    cmd.name match {
      case "allColumns" => {
        UserDao.findOneById(idUser).foreach { user =>
          if (user.isDefined && user.get.columns.isDefined) {
            val jsonRes = Json.toJson(user.get.columns.get)
            UserInfosActor.sendTo(idUser, Json.toJson(Command(cmd.name, Some(jsonRes))))
          }
        }
      }
      case "addColumn" => {
        val newColumn = Json.fromJson[Column](cmd.body.get).get
        UserDao.findOneById(idUser).foreach { user =>
          if (user.isDefined) {
            UserDao.update(
              User(user.get.accounts,
                user.get.distants,
                Some(user.get.columns.getOrElse(Seq[Column]()) :+ newColumn)))
            UserInfosActor.startProfiderFor(idUser, newColumn)
            UserInfosActor.sendTo(idUser, Json.toJson(Command(cmd.name, Some(JsString("Ok")))))
          }
        }
      }
      case "modColumn" => {
        val modColumnTitle = (cmd.body.get \ "title").as[String]
        val modColumn = Json.fromJson[Column]((cmd.body.get \ "column").as[JsValue]).get
        UserDao.findOneById(idUser).foreach { user =>
          if (user.isDefined) {
            UserDao.updateColumn(user.get, modColumnTitle, modColumn)
            UserInfosActor.killProfiderFor(idUser, modColumnTitle)
            UserInfosActor.startProfiderFor(idUser, modColumn)
            UserInfosActor.sendTo(idUser, Json.toJson(Command(cmd.name, Some(JsString("Ok")))))
          }
        }
      }
      case "delColumn" => {
        val delColumnTitle = (cmd.body.get \ "title").as[String]
        UserDao.findOneById(idUser).foreach { user =>
          if (user.isDefined) {
            UserDao.deleteColumn(user.get, delColumnTitle)
            UserInfosActor.killProfiderFor(idUser, delColumnTitle)
            UserInfosActor.sendTo(idUser, Json.toJson(Command(cmd.name, Some(JsString("Ok")))))
          }
        }
      }
      case "allUnifiedRequests" => {
        UserInfosActor.sendTo(idUser, Json.toJson(Command(cmd.name, Some(Service.toJsonWithUnifiedRequest))))
      }
      case "allProviders" => {
        UserInfosActor.sendTo(idUser, Json.toJson(Command(cmd.name, Some(Service.toJson))))
      }
      case _ => {
        Logger.error("Command not found " + cmd)
        Command("error", Some(JsString("Command not found " + cmd)))
        //TODO RM : return to client ?
      }
    }
  }

}