package services.commands

import services.dao.UserDao
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
import services.auth.ProviderDispatcher
import services.post.Posters
import services.endpoints.Endpoints

object CmdFromUser {

  import play.api.libs.concurrent.Execution.Implicits._

  def interpret(idUser: String, json: JsValue)(implicit req: RequestHeader): Unit = {
    val cmd = Json.fromJson[Command](json).getOrElse(Command("_"))
    interpretCmd(idUser, cmd)
  }

  def interpretCmd(idUser: String, cmd: Command)(implicit req: RequestHeader): Unit = {
    val internalIdUser = CmdToUser.getInternalIdUser(idUser)
    cmd.name match {
      case "allColumns" => {
        UserDao.findOneById(internalIdUser).map(_.map { user =>
          user.columns.map(columns =>
            CmdToUser.sendTo(internalIdUser, Command(cmd.name, Some(Json.toJson(columns))))
          ).getOrElse(
            CmdToUser.sendTo(internalIdUser, Command(cmd.name, Some(Json.toJson(new JsArray))))
          )
        })
      }
      case "addColumn" => {
        val newColumn = Json.fromJson[Column](cmd.body.get).get
        UserDao.addColumn(internalIdUser, newColumn)
        UserInfosActor.startProfiderFor(internalIdUser, newColumn)
        CmdToUser.sendTo(internalIdUser, Command(cmd.name, Some(cmd.body.get)))
      }
      case "modColumn" => {
        val modColumnTitle = (cmd.body.get \ "title").as[String]
        val modColumn = Json.fromJson[Column]((cmd.body.get \ "column").as[JsValue]).get
        UserDao.updateColumn(internalIdUser, modColumnTitle, modColumn)
        ProviderActor.killActorsForUserAndColumn(internalIdUser, modColumnTitle)
        UserInfosActor.startProfiderFor(internalIdUser, modColumn)
        CmdToUser.sendTo(internalIdUser, Command(cmd.name, Some(cmd.body.get)))
      }
      case "modColumnsOrder" => {
        val columnsOrder = (cmd.body.get \ "columns").as[List[String]]
        UserDao.findOneById(internalIdUser).map(_.map { user =>
          user.columns.map(_.map { column =>
            UserDao.updateColumn(
              internalIdUser,
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
        UserDao.deleteColumn(internalIdUser, delColumnTitle)
        ProviderActor.killActorsForUserAndColumn(internalIdUser, delColumnTitle)
        CmdToUser.sendTo(internalIdUser, Command(cmd.name, Some(cmd.body.get)))
      }
      case "resizeColumn" => {
        val columnTitle = (cmd.body.get \ "columnTitle").as[String]
        val columnWidth = (cmd.body.get \ "width").as[Int]
        val columnHeight = (cmd.body.get \ "height").as[Int]
        UserDao.findOneById(internalIdUser).map(_.map { user =>
          user.columns.map(_.filter(_.title == columnTitle).map { column =>
            UserDao.updateColumn(
              internalIdUser,
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
        CmdToUser.sendTo(internalIdUser, Command(cmd.name, Some(Service.toJsonWithUnifiedRequest)))
      }
      case "allProviders" => {
        CmdToUser.sendTo(internalIdUser, Command(cmd.name, Some(Service.toJson)))
      }
      case "deleteProvider" => {
        val providerName = (cmd.body.get \ "provider").as[String]
        ProviderActor.killProvider(internalIdUser, providerName)
        interpretCmd(internalIdUser, Command("allUnifiedRequests"))
      }
      case "newToken" => {
        CmdToUser.sendTo(internalIdUser, cmd)
      }
      case "pong" => {
        PingActor.ping(internalIdUser)
      }
      case "allPosters" => {
        CmdToUser.sendTo(internalIdUser, Command(cmd.name, Some(Posters.toJson)))
      }
      case "post" => {
        val providersName = (cmd.body.get \ "providers").as[Seq[String]]
        providersName.map { providerName =>
          val poster = Posters.getPoster(providerName)
          poster.map( _.post(Json.fromJson[Post]((cmd.body.get \ "post").as[JsValue]).get))
        }
      }
      case "detailsSkimbo" => {
        val service = (cmd.body.get \ "service").as[String]
        Endpoints.getConfig(service).map { service =>
          service.parser.map { parser =>
            
          }
        }
      }
      case _ => {
        Logger.error("Command not found " + cmd)
      }
    }
  }

}