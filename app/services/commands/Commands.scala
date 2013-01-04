package services.commands

import services.UserDao
import models.command.Command
import models._
import models.user._
import models.user.Column._
import play.api.Logger
import play.api.libs.json._
import play.api.mvc.RequestHeader
import services.actors.UserInfosActor
import services.actors.ProviderActor
import services.actors.PingActor

object Commands {

  import play.api.libs.concurrent.Execution.Implicits._

  def interpret(idUser: String, json: JsValue)(implicit req: RequestHeader): Unit = {
    val cmd = Json.fromJson[Command](json).getOrElse(Command("_"))
    interpretCmd(idUser, cmd)
  }

  def interpretCmd(idUser: String, cmd: Command)(implicit req: RequestHeader): Unit = {
    cmd.name match {
      case "allColumns" => {
        UserDao.findOneById(idUser).map(_.map { user =>
          user.columns.map(columns =>
            UserInfosActor.sendTo(idUser, Json.toJson(Command(cmd.name, Some(Json.toJson(columns)))))
          ).getOrElse(
            UserInfosActor.sendTo(idUser, Json.toJson(Command(cmd.name, Some(Json.toJson(new JsArray)))))
          )
        })
      }
      case "addColumn" => {
        val newColumn = Json.fromJson[Column](cmd.body.get).get
        UserDao.addColumn(idUser, newColumn)
        UserInfosActor.startProfiderFor(idUser, newColumn)
        UserInfosActor.sendTo(idUser, Json.toJson(Command(cmd.name, Some(JsString("Ok")))))
      }
      case "modColumn" => {
        val modColumnTitle = (cmd.body.get \ "title").as[String]
        val modColumn = Json.fromJson[Column]((cmd.body.get \ "column").as[JsValue]).get
        UserDao.updateColumn(idUser, modColumnTitle, modColumn)
        ProviderActor.killActorsForUserAndColumn(idUser, modColumnTitle)
        UserInfosActor.startProfiderFor(idUser, modColumn)
        UserInfosActor.sendTo(idUser, Json.toJson(Command(cmd.name, Some(JsString("Ok")))))
      }
      case "modColumnsOrder" => {
        val columnsOrder = (cmd.body.get \ "columns").as[List[String]]
        UserDao.findOneById(idUser).map(_.map { user =>
          user.columns.map(_.map { column =>
            UserDao.updateColumn(
              idUser,
              column.title,
              Column(
                column.title,
                column.unifiedRequests,
                columnsOrder.indexOf(column.title),
                column.width,
                column.height))
          })
        })
      }
      case "delColumn" => {
        val delColumnTitle = (cmd.body.get \ "title").as[String]
        UserDao.deleteColumn(idUser, delColumnTitle)
        ProviderActor.killActorsForUserAndColumn(idUser, delColumnTitle)
        UserInfosActor.sendTo(idUser, Json.toJson(Command(cmd.name, Some(JsString("Ok")))))
      }
      case "resizeColumn" => {
        val columnTitle = (cmd.body.get \ "columnTitle").as[String]
        val columnWidth = (cmd.body.get \ "width").as[Int]
        val columnHeight = (cmd.body.get \ "height").as[Int]
        UserDao.findOneById(idUser).map(_.map { user =>
          user.columns.map(_.filter(_.title == columnTitle).map { column =>
            UserDao.updateColumn(
              idUser,
              column.title,
              Column(
                column.title,
                column.unifiedRequests,
                column.index,
                columnWidth,
                columnHeight))
          })
        })
      }
      case "allUnifiedRequests" => {
        UserInfosActor.sendTo(idUser, Json.toJson(Command(cmd.name, Some(Service.toJsonWithUnifiedRequest))))
      }
      case "allProviders" => {
        UserInfosActor.sendTo(idUser, Json.toJson(Command(cmd.name, Some(Service.toJson))))
      }
      case "deleteProvider" => {
        val providerName = (cmd.body.get \ "provider").as[String]
        ProviderActor.killProvider(idUser, providerName)
        interpretCmd(idUser, Command("allUnifiedRequests"))
      }
      case "newToken" => {
        UserInfosActor.sendTo(idUser, Json.toJson(cmd))
      }
      case "pong" => {
        PingActor.ping(idUser);
      }
      case _ => {
        Logger.error("Command not found " + cmd)
      }
    }
  }

}