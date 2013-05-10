package services.commands

import play.libs.Akka
import scala.concurrent.duration.DurationInt
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import services.dao.DelayedPostDao
import services.post.Posters
import models.Post
import play.api.Logger
import scala.util.Success
import scala.util.Failure

object DelayedPostPolling {
  
  val tempo = 1
  val log = Logger(DelayedPostPolling.getClass()).logger
  
  val scheduler = Akka.system.scheduler.schedule(0 second, tempo minute) {
    run()
  }
  
  def run() = {
    log.info("Run polling")
    DelayedPostDao.get(tempo).map( _.foreach { delayedPost =>
      delayedPost.providers.foreach { provider =>
        val post = delayedPost.post
        val toPost = Post(post.title, post.message, post.url, post.url_image, provider.providerPageId)
        log.info("Send polling message" + toPost)
        Posters.getPoster(provider.providerName).map( 
          _.post(delayedPost.idUser, toPost).onComplete {
            case Success(response) => {
              DelayedPostDao.delete(delayedPost)
            }
            case Failure(error) => {
              log.error("postDelayed can't send, retry later")
              log.error(error.getMessage())
            }
          }
        )
      }
    })
  }
  
}