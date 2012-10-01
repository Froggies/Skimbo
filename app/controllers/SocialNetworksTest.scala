package controllers

import play.api._

import play.api.mvc._
import play.api.libs.ws._
import services.auth.providers._
import play.api.libs.json._
import play.api.libs.concurrent.execution.defaultContext
import java.util.zip.GZIPInputStream
import java.io.InputStreamReader
import java.io.BufferedReader

object SocialNetworksTest extends Controller {

	
	def twitter = Action { implicit request =>
		Twitter.getToken match {
			// Authentification twitter valide
			case Some(credentials) => Async { // DOC https://dev.twitter.com/docs/api/1.1
				Twitter.fetch("https://api.twitter.com/1.1/statuses/home_timeline.json")
					.get.map(r => Ok(r.json))
			}

			// Autoriser l'application
			case _ => Redirect(Twitter.authRoute)
		}
	}

	def facebook = Action { implicit request =>
		Facebook.getToken match {
			// Authentification OK
			case Some(token) => Async { // DOC https://developers.facebook.com/docs/reference/api/user/#home
				Facebook.fetch("https://graph.facebook.com/me/home")
					.get.map(r => r.status match {
						case 200 => Ok(r.json)
						case _ => Redirect(Facebook.authRoute)
					})
			}
			// Autoriser l'app
			case None => Redirect(Facebook.authRoute)
		}
	}

	def googleplus = Action { implicit request =>
		GooglePlus.getToken match {
			case Some(token) => Async { // DOC https://developers.google.com/+/api/
				GooglePlus.fetch("https://www.googleapis.com/plus/v1/people/me/activities/public")
					.get.map(r => Ok(r.json))
			}
			case _ => Redirect(GooglePlus.authRoute)
		}
	}

	def viadeo = Action { implicit request =>
		Viadeo.getToken match {
			case Some(token) => Async { // DOC http://dev.viadeo.com/graph-api-resource/?resource=%2Fuser%2FID%2Fsmart_news
				Viadeo.fetch("https://api.viadeo.com/me/smart_news.json")
					.get.map(r => Ok(r.json))
			}
			case _ => Redirect(Viadeo.authRoute)
		}
	}

	def linkedin = Action { implicit request =>
		LinkedIn.getToken match {
			case Some(credentials) => Async { // DOC https://developer.linkedin.com/documents/get-network-updates-and-statistics-api
				LinkedIn.fetch("http://api.linkedin.com/v1/people/~/network/updates")
					.get.map(r => Ok(r.json))
			}
			case _ => Redirect(LinkedIn.authRoute)
		}
	}

	def github = Action { implicit request =>

		def getUsersInfos =
			GitHub.fetch("https://api.github.com/user").get.map(_.json)

		def getEvents(login: String) = // DOC http://developer.github.com/v3/events/#list-events-that-a-user-has-received 
			GitHub.fetch("https://api.github.com/users/"+login+"/received_events").get.map(_.json)

		GitHub.getToken match {
			case Some(token) => Async {
				getUsersInfos.flatMap(userInfos =>
					getEvents((userInfos \ "login").as[String])
						.map(events => Ok(Json.obj(
							"user" -> userInfos,
							"events" -> events))))
			}
			case _ => Redirect(GitHub.authRoute)
		}
		
	}

	def stackexchange = Action { implicit request =>

		def getInbox(token: String) = // DOC https://api.stackexchange.com/docs/inbox
			StackExchange.fetch("https://api.stackexchange.com/2.1/inbox")
				.get.map(parseGzipJson(_))

		def getNotifications(token: String) = // DOC https://api.stackexchange.com/docs/notifications
			StackExchange.fetch("https://api.stackexchange.com/2.1/notifications")
				.get.map(parseGzipJson(_))

		def aggregateEvents(token: String) = {
			for {
				inbox <- getInbox(token)
				notif <- getNotifications(token)
			} yield Json.obj("inbox" -> inbox, "notif" -> notif)
		}

		StackExchange.getToken match {
			case Some(token) => Async(aggregateEvents(token).map(Ok(_)))
			case _ => Redirect(StackExchange.authRoute)
		}
	}
	
	def trello = Action { implicit request => 
		
		Trello.getToken match {
			case Some(credentials) => Async { // DOC https://trello.com/docs/api/notification/index.html
				  Trello.fetch("https://api.trello.com/1/members/me/notifications")
					.get.map(r => r.status match {
						case 200 => Ok(r.json)
						case _ => Redirect(Trello.authRoute)
					})
			}
			case _ => Redirect(Trello.authRoute)
		}
	}

	
	
	// TODO > Est surement natif à PLAY2 => Creuser
	// Sinon tenter via un iteratees
	private def parseGzipJson(response: play.api.libs.ws.Response): JsValue = {
		def read(buf: BufferedReader, acc: List[String]): List[String] =
			buf.readLine match {
				case null => acc
				case s => read(buf, s :: acc)
			}

		val buffered = new BufferedReader(new InputStreamReader(new GZIPInputStream(response.ahcResponse.getResponseBodyAsStream()), "UTF-8"))
		val content = read(buffered, Nil).reverse
		Json.parse(content.mkString)
	}
	
}