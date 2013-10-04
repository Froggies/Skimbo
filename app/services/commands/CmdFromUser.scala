package services.commands

import java.net.URLEncoder
import scala.util.Failure
import scala.util.Success
import org.apache.commons.mail.DefaultAuthenticator
import org.apache.commons.mail.SimpleEmail
import models.Comment
import models.Post
import models.Service
import models.command._
import models.user.Column
import models.user.Column.reader
import models.user.Column.writer
import play.api.Logger
import play.api.Play.current
import play.api.http.Status
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._
import play.api.libs.json.Json.toJsFieldJsValueWrapper
import play.api.mvc.RequestHeader
import services.actors._
import services.auth.ProviderDispatcher
import services.comment.Commenters
import services.dao.DelayedPostDao
import services.dao.UserDao
import services.endpoints.Endpoints
import services.post.Posters
import models.user.SinceId

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
          if(user.columns.isEmpty) {
            CmdToUser.sendTo(internalIdUser, Command(cmd.name, Some(Json.toJson(new JsArray))))
          } else {
            CmdToUser.sendTo(internalIdUser, Command(cmd.name, Some(Json.toJson(user.columns))))
          }
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
          user.columns.map { column =>
            Logger(CmdFromUser.getClass).info("Columns order "+column.title+" --> "+columnsOrder.indexOf(column.title))
            UserDao.updateColumn(
              internalIdUser,
              column.title,
              Column(
                column.title,
                column.unifiedRequests,
                columnsOrder.indexOf(column.title),
                column.width,
                column.height))
          }
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
          user.columns.filter(_.title == columnTitle).map { column =>
            UserDao.updateColumn(
              internalIdUser,
              column.title,
              Column(
                column.title,
                column.unifiedRequests,
                column.index,
                columnWidth,
                columnHeight))
          }
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
        ProviderDispatcher(providerName).map { provider =>
          ProviderActor.killProvider(internalIdUser, providerName)
          provider.deleteToken(internalIdUser)
          CmdToUser.sendTo(internalIdUser, Command(cmd.name, cmd.body))
          interpretCmd(internalIdUser, Command("allUnifiedRequests"))
          interpretCmd(internalIdUser, Command("allPosters"))
        }
      }
      case "pauseProvider" => {
        val providerName = (cmd.body.get \ "provider").as[String]
        ProviderActor.killProvider(internalIdUser, providerName)
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
            map( _.post(idUser, toPost).onComplete {
              case Success(response) => {
                
              }
              case Failure(error) => {
                CmdToUser.sendTo(internalIdUser, Error(providerName, ErrorType.Post))
              }
            }).
            getOrElse(Logger(CmdFromUser.getClass).error("not found poster "+providerName))
        }
      }
      case "delayedPost" => {
        val jsonProviders = (cmd.body.get \ "providers").as[Seq[JsObject]]
        val providers = jsonProviders.map { jsonProvider =>
          val providerName = (jsonProvider \ "name").as[String]
          val providerPageId = (jsonProvider \ "toPageId").asOpt[String]
          PostDelayedProvider(providerName, providerPageId)
        }
        val post = Json.fromJson[Post]((cmd.body.get \ "post").as[JsValue]).get
        val timeToPost = Json.fromJson[Long]((cmd.body.get \ "timeToPost").as[JsValue]).get
        DelayedPostDao.add(DelayedPost(idUser, post, providers, timeToPost))
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
            if(response.status != Status.OK) {
              CmdToUser.sendTo(internalIdUser, Error(service.provider.name, ErrorType.Star, Some(columnTitle)))
            }
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
                println("param helper for " + search + " : " + response.body.toString)
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
                    CmdToUser.sendTo(internalIdUser, Error(service.provider.name, ErrorType.Comment, Some(comment.columnTitle)))
                  }
                  detailsSkimbo(internalIdUser, comment.serviceName, comment.providerId, comment.columnTitle)
                }
                case Failure(error) => {
                  CmdToUser.sendTo(internalIdUser, Error(service.provider.name, ErrorType.Comment, Some(comment.columnTitle)))
                }
              }
            }
          }
        }
      }
      case "sendEmail" => {
        val fromEmail = (cmd.body.get \ "email").as[String]
        val subject = (cmd.body.get \ "object").as[String]
        val message = (cmd.body.get \ "message").as[String]
        val config = current.configuration.getConfig("email").get
        val toEmail = config.getString("to").get
        val host = config.getString("smtp.host").get
        val port = config.getInt("smtp.port").get
        val username = config.getString("smtp.user").get
        val password = config.getString("smtp.pass").get
        try {
          val email = new SimpleEmail()
          email.setHostName(host)
          email.setSmtpPort(port)
          email.setAuthenticator(new DefaultAuthenticator(username, password))
          email.setSSLOnConnect(true)
          email.setFrom(fromEmail)
          email.setSubject("From skimbo : "+subject)
          email.setCharset("utf-8")
          email.setMsg(message)
          email.addTo(toEmail)
          email.send()
          CmdToUser.sendTo(internalIdUser, Command(cmd.name))
        } catch {
          case t:Throwable => {
            Logger(CmdFromUser.getClass).error("Send mail fail : " + t.getMessage())
            CmdToUser.sendTo(internalIdUser, Error("skimbo", ErrorType.EmailNotSend))
          }
        }

      }
      case "updateSinceId" => {
        val columnTitle = (cmd.body.get \ "columnTitle").as[String]
        val uidProviderUser = (cmd.body.get \ "uidProviderUser").as[String]
        val sinceId = SinceId((cmd.body.get \ "sinceId").as[String], idUser)
        UserDao.updateSinceId(idUser, columnTitle, uidProviderUser, sinceId)
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