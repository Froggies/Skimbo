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

object Commands {

  def buildResponse(cmd: String, jsonRes: JsValue): JsValue = {
    JsObject(Seq("cmd" -> JsString(cmd), "res" -> jsonRes))
  }

  def interpret(idUser: String, json: JsValue)(implicit context: scala.concurrent.ExecutionContext, req: RequestHeader) = {
    val cmd = (json \ "cmd").asOpt[String]
    if (cmd.isDefined) {
      cmd.get match {
        case "allColumns" => {
          UserDao.findOneById(idUser).foreach { user =>
            if (user.isDefined && user.get.columns.isDefined) {
              val jsonRes = User.toJsonC(user.get.columns.get)
              UserInfosActor.sendTo(idUser, buildResponse(cmd.get, JsArray(jsonRes)))
            }
          }
        }
        case "addColumn" => {
          val newColumn = User.fromJsonC(Seq((json \ "body").as[JsValue]))
          UserDao.findOneById(idUser).foreach { user =>
            if (user.isDefined) {
              UserDao.update(
                User(user.get.accounts, 
                user.get.distants, 
                Some(user.get.columns.getOrElse(Seq[Column]()) ++ newColumn))
              )
              UserInfosActor.startProfiderFor(idUser, newColumn.head.unifiedRequests)
              UserInfosActor.sendTo(idUser, buildResponse(cmd.get, JsString("Ok")))
            }
          }
        }
        case "delColumn" => {
          val delColumnTitle = ((json \ "body").as[JsValue] \ "title").as[String]
          UserDao.findOneById(idUser).foreach { user =>
            if (user.isDefined) {
              UserDao.deleteColumn(user.get, delColumnTitle)
              UserInfosActor.sendTo(idUser, buildResponse(cmd.get, JsString("Ok")))
            }
          }
        }
        case "allUnifiedRequests" => {
          UserInfosActor.sendTo(idUser, buildResponse(cmd.get, Service.toJson))
        }
        case _ => {
          Logger.error("Command not found " + cmd)
          //TODO RM : return to client ?
        }
      }
    } else {
      Logger.error("Command not correct " + json)
    }
  }

}