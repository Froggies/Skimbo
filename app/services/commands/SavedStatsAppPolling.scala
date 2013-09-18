package services.commands

import scala.concurrent.duration.DurationInt
import org.joda.time.DateTime
import controllers.api.Stats
import play.api.Logger
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.libs.Akka
import services.dao.StatsAppDao
import org.joda.time.format.DateTimeFormat

object SavedStatsAppPolling {
  
  val tempo = 10
  val log = Logger(SavedStatsAppPolling.getClass()).logger
  
  val scheduler = Akka.system.scheduler.schedule(0 second, tempo minute) {
    run()
  }
  
  def run() = {
    log.info("Run polling saved stats app")
    StatsAppDao.last().map( _.map { last:models.StatsApp =>
      log.info("is after : "+new DateTime(last.timestamp).plusHours(1).isBeforeNow())
      log.info(DateTimeFormat.forPattern("dd-MMMM-yyyy HH:mm").print(new DateTime(last.timestamp)))
      log.info(DateTimeFormat.forPattern("dd-MMMM-yyyy HH:mm").print(DateTime.now))
      if(new DateTime(last.timestamp).plusHours(1).isEqualNow() || 
         new DateTime(last.timestamp).plusHours(1).isBeforeNow()) {
      log.info("Save stats app")
        Stats.createStatApp().flatMap { newStats =>
          StatsAppDao.add(newStats)
        }
      }
    })
  }
  
}