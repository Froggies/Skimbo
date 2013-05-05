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
import services.actors.Fetcher
import services.actors.ProviderActorParameter
import services.endpoints.JsonRequest.UnifiedRequest
import services.actors.FetcherParameter
import java.net.URLEncoder
import services.comment.Commenters
import play.api.mvc.RequestHeader
import scala.util.Success
import scala.util.Failure
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import models.command.Error
import play.api.http.Status

object CmdFromUser {

  import play.api.libs.concurrent.Execution.Implicits._

  def interpret(idUser: String, json: JsValue)(implicit req: RequestHeader): Unit = {
    val cmd = Json.fromJson[Command](json).getOrElse(Command("_"))
    interpretCmd(idUser, cmd)
  }

  //TODO transform in command pattern
  def interpretCmd(idUser: String, cmd: Command): Unit = {
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
        CmdToUser.sendTo(internalIdUser, Command(cmd.name, Some(Service.toJsonWithUnifiedRequest(idUser))))
      }
      case "allProviders" => {
        CmdToUser.sendTo(internalIdUser, Command(cmd.name, Some(Service.toJson(idUser))))
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
        CmdToUser.sendTo(internalIdUser, Command(cmd.name, Some(Posters.toJson(idUser))))
      }
      case "post" => {
        val providers = (cmd.body.get \ "providers").as[Seq[JsObject]]
        providers.map { jsonProvider =>
          val providerName = (jsonProvider \ "name").as[String]
          val providerPageId = (jsonProvider \ "toPageId").asOpt[String]
          val post = Json.fromJson[Post]((cmd.body.get \ "post").as[JsValue]).get
          val toPost = Post(post.title, post.message, post.url, post.url_image, providerPageId)
          Posters.getPoster(providerName).
            map( _.post(idUser, toPost)).
            getOrElse(Logger(CmdFromUser.getClass).error("not found poster "+providerName))
        }
      }
      case "detailsSkimbo" => {
        val serviceName = (cmd.body.get \ "serviceName").as[String]
        val idMsg = (cmd.body.get \ "id").as[String]
        val columnTitle = (cmd.body.get \ "columnTitle").as[String]
        detailsSkimbo(internalIdUser, serviceName, idMsg, columnTitle)
      }
      case "star" => {
        val serviceName = (cmd.body.get \ "serviceName").as[String]
        val idMsg = (cmd.body.get \ "id").as[String]
        val columnTitle = (cmd.body.get \ "columnTitle").as[String]
        Endpoints.getConfig(serviceName).map { service =>
            service.starer.map( _.star(idUser, idMsg).map { response =>
              detailsSkimbo(internalIdUser, serviceName, idMsg, columnTitle)
              Logger(CmdFromUser.getClass).info(response.body.toString)
              if(service.canParseResultStar) {
                service.parserDetails.map { parser =>
                  parser.getSkimboMsg(response, service.provider).map { listMsg =>
                    val msg = Json.obj("column" -> columnTitle, "msg" -> listMsg.head)
                    CmdToUser.sendTo(internalIdUser, Command("msg", Some(msg)))
                  }
                }
              }
          })
        }
      }
      case "paramHelperSearch" => {
        val serviceName = (cmd.body.get \ "serviceName").as[String]
        val search = (cmd.body.get \ "search").as[String]
        Endpoints.getConfig(serviceName).map { service =>
          service.paramParserHelper.map { parser =>
            service.paramUrlHelper.map { url =>
              service.provider.fetch(idUser, url.replace(":search", URLEncoder.encode(search, "UTF-8"))).withTimeout(service.delay * 1000).get.map { response =>
                parser.getParamsHelper(idUser, response, service.provider).map { params =>
                  val msg = Json.obj("serviceName" -> serviceName, "values" -> params)
                  CmdToUser.sendTo(internalIdUser, Command("paramHelperSearch", Some(msg)))
                }
              }
            }
          }
        }
      }
      case "paramPostHelperSearch" => {
        val serviceName = (cmd.body.get \ "serviceName").as[String]
        val search = (cmd.body.get \ "search").as[String]
        println("paramPostHelperSearch :: ")
        Posters.getPoster(serviceName).map { service =>
          println("paramPostHelperSearch :: "+service)
          service.helperPageId(idUser, search).map { params =>
            println("paramPostHelperSearch :: "+params)
            val msg = Json.obj("serviceName" -> serviceName, "values" -> params)
            CmdToUser.sendTo(internalIdUser, Command("paramPostHelperSearch", Some(msg)))
          }
        }
      }
      case "comment" => {
        val jsComment = Json.fromJson[Comment](cmd.body.get)
        jsComment.asOpt.map { comment =>
          Endpoints.getConfig(comment.serviceName).map { service =>
            Commenters.getCommenter(service.provider.name).map { commenter =>
              commenter.comment(idUser, comment).onComplete {
                case Success(response) => {
                  if(response.status != Status.OK) {
                    CmdToUser.sendTo(internalIdUser, Error(service.provider.name, "You can't comment", Some(comment.columnTitle)))
                  }
                  detailsSkimbo(internalIdUser, comment.serviceName, comment.providerId, comment.columnTitle)
                }
                case Failure(error) => {
                  CmdToUser.sendTo(internalIdUser, Error(service.provider.name, "You can't comment", Some(comment.columnTitle)))
                }
              }
            }
          }
        }
      }
      case _ => {
        Logger(CmdFromUser.getClass).error("Command not found " + cmd)
      }
    }
  }
  
  def detailsSkimbo(internalIdUser:String, serviceName:String, idMsg:String, columnTitle:String) = {
    Endpoints.getConfig(serviceName).map { service =>
      service.parserDetails.map { parser =>
        Fetcher(FetcherParameter(
          service.provider,
          Some(parser),
          Some(service.urlDetails.replace(":id", idMsg)),
          internalIdUser,
          columnTitle,
          serviceName
        )).map( _.map { listMsg =>
          val msg = Json.obj("column" -> columnTitle, "msg" -> listMsg.head)
          CmdToUser.sendTo(internalIdUser, Command("msg", Some(msg)))
        })
      }
    }
  }

}