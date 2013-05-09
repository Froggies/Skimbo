package services.commands

import play.libs.Akka
import scala.concurrent.duration.DurationInt
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import services.dao.DelayedPostDao
import services.post.Posters
import models.Post

object DelayedPostPolling {
  
  val tempo = 15
  
  val scheduler = Akka.system.scheduler.schedule(0 second, tempo minute) {
    run()
  }
  
  def run() = {
    DelayedPostDao.get(tempo).map( _.foreach { delayedPost =>
      delayedPost.providers.foreach { provider =>
        val post = delayedPost.post
        val toPost = Post(post.title, post.message, post.url, post.url_image, provider.providerPageId)
        Posters.getPoster(provider.providerName).
          map( _.post(delayedPost.idUser, toPost))
      }
    })
  }
  
}