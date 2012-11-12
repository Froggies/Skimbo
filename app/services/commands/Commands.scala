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
  
  def buildResponse(cmd: String, jsonRes: JsValue): JsValue = {
    JsObject(Seq("cmd" -> JsString(cmd), "res" -> jsonRes))
  }

  def interpret(idUser: String, json: JsValue)(implicit context: scala.concurrent.ExecutionContext, req: RequestHeader) = {
    val cmd = Json.fromJson[Command](json)
    cmd.getOrElse(Command("_")).name match {
      case "allColumns" => {
        UserDao.findOneById(idUser).foreach { user =>
          if (user.isDefined && user.get.columns.isDefined) {
            val jsonRes = Json.toJson(user.get.columns.get)
            UserInfosActor.sendTo(idUser, Json.toJson(Command(cmd.get.name, Some(jsonRes))))
          }
        }
      }
      case "addColumn" => {
        val newColumn = Json.fromJson[Column](cmd.get.body.get).get
        UserDao.findOneById(idUser).foreach { user =>
          if (user.isDefined) {
            UserDao.update(
              User(user.get.accounts,
                user.get.distants,
                Some(user.get.columns.getOrElse(Seq[Column]()) :+ newColumn)))
            UserInfosActor.startProfiderFor(idUser, newColumn.unifiedRequests)
            UserInfosActor.sendTo(idUser, Json.toJson(Command(cmd.get.name, Some(JsString("Ok")))))
          }
        }
      }
      case "delColumn" => {
        val delColumnTitle = ((json \ "body").as[JsValue] \ "title").as[String]
        UserDao.findOneById(idUser).foreach { user =>
          if (user.isDefined) {
            UserDao.deleteColumn(user.get, delColumnTitle)
            UserInfosActor.sendTo(idUser, Json.toJson(Command(cmd.get.name, Some(JsString("Ok")))))
          }
        }
      }
      case "allUnifiedRequests" => {
        UserInfosActor.sendTo(idUser, Json.toJson(Command(cmd.get.name, Some(Service.toJsonWithUnifiedRequest))))
      }
      case "allProviders" => {
        UserInfosActor.sendTo(idUser, Json.toJson(Command(cmd.get.name, Some(Service.toJson))))
      }
      case _ => {
        Logger.error("Command not found " + cmd)
        //TODO RM : return to client ?
      }
    }
  }

}