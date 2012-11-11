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
import models.Column
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
    cmd.getOrElse(Command("cmd not found")).name match {
      case "allColumns" => {
        UserDao.findOneById(idUser).foreach { user =>
          if (user.isDefined && user.get.columns.isDefined) {
            val jsonRes = User.toJsonC(user.get.columns.get)
            UserInfosActor.sendTo(idUser, Json.toJson(Command(cmd.get.name, Some(JsArray(jsonRes)))))
          }
        }
      }
      case "addColumn" => {
        log.info("addColumn :: "+cmd.get.body.get)
        val newColumn = User.fromJsonC(Seq(cmd.get.body.get))
        UserDao.findOneById(idUser).foreach { user =>
          if (user.isDefined) {
            UserDao.update(
              User(user.get.accounts,
                user.get.distants,
                Some(user.get.columns.getOrElse(Seq[Column]()) ++ newColumn)))
            UserInfosActor.startProfiderFor(idUser, newColumn.head.unifiedRequests)
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
        UserInfosActor.sendTo(idUser, Json.toJson(Command(cmd.get.name, Some(Service.toJson))))
      }
      case _ => {
        Logger.error("Command not found " + cmd)
        //TODO RM : return to client ?
      }
    }
  }

}