package services.commands

import controllers.UserDao
import models.command.Command
import models._
import models.user._
import models.user.Column._
import play.api.Logger
import play.api.libs.json._
import play.api.mvc.RequestHeader
import services.actors.UserInfosActor

object Commands {

  def interpret(idUser: String, json: JsValue)(implicit context: scala.concurrent.ExecutionContext, req: RequestHeader) : Unit = {
    val cmd = Json.fromJson[Command](json).getOrElse(Command("_"))
    interpretCmd(idUser, cmd)
  }
  
  def interpretCmd(idUser: String, cmd: Command)(implicit context: scala.concurrent.ExecutionContext, req: RequestHeader) : Unit = {
    println(idUser)
    cmd.name match {
      case "allColumns" => {
        UserDao.findOneById(idUser).map(_.map { user =>
          if (user.columns.isDefined) {
            val jsonRes = Json.toJson(user.columns.get)
            UserInfosActor.sendTo(idUser, Json.toJson(Command(cmd.name, Some(jsonRes))))
          }
        })
      }
      case "addColumn" => {
        val newColumn = Json.fromJson[Column](cmd.body.get).get
        UserDao.findOneById(idUser).map(_.map { user =>
          UserDao.addColumn(user, newColumn)
          UserInfosActor.startProfiderFor(idUser, newColumn)
          UserInfosActor.sendTo(idUser, Json.toJson(Command(cmd.name, Some(JsString("Ok")))))
        })
      }
      case "modColumn" => {
        val modColumnTitle = (cmd.body.get \ "title").as[String]
        val modColumn = Json.fromJson[Column]((cmd.body.get \ "column").as[JsValue]).get
        UserDao.findOneById(idUser).map(_.map { user =>
          UserDao.updateColumn(user, modColumnTitle, modColumn)
          UserInfosActor.killProfiderFor(idUser, modColumnTitle)
          UserInfosActor.startProfiderFor(idUser, modColumn)
          UserInfosActor.sendTo(idUser, Json.toJson(Command(cmd.name, Some(JsString("Ok")))))
        })
      }
      case "delColumn" => {
        val delColumnTitle = (cmd.body.get \ "title").as[String]
        UserDao.findOneById(idUser).map(_.map { user =>
          UserDao.deleteColumn(user, delColumnTitle)
          UserInfosActor.killProfiderFor(idUser, delColumnTitle)
          UserInfosActor.sendTo(idUser, Json.toJson(Command(cmd.name, Some(JsString("Ok")))))
        })
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