package controllers.api

import org.joda.time.DateTime
import models.User
import play.api.mvc._
import services.dao.UserDao
import services.auth.ProviderDispatcher
import services.actors.UserInfosActor
import services.actors.HelperUserInfosActor
import services.actors.HelperProviderActor
import services.commands.CmdToUser
import services.dao.StatsAppDao
import scala.util.Success
import scala.util.Failure
import reactivemongo.core.commands.LastError
import play.api.Routes
import org.joda.time.format.DateTimeFormat
import scala.concurrent.Future

object Stats extends Controller {

  import play.api.libs.concurrent.Execution.Implicits._
  
  def get() = Action { implicit request =>
    Async {
      createStatApp().map { stats =>
        Ok(toReadStats(stats, false))
      }
    }
  }
  
  def load(timestamp: String) = Action { implicit request =>
    Async {
      StatsAppDao.get(timestamp.toLong).map { _.map { stats =>
        Ok(toReadStats(stats, true))
      }.getOrElse(Ok("Ko"))}
    }
  }
  
  def all() = Action { implicit request =>
    Async {
      StatsAppDao.allTimestamp().map { l =>
        val s = new StringBuilder
        l.map { timestamp =>
          s ++= DateTimeFormat.forPattern("dd-MMMM-yyyy HH:mm").print(new DateTime(timestamp))
          s ++= " : "
          s ++= routes.Stats.load(timestamp.toString).absoluteURL(false)
          s ++= "\n"
        }
        Ok(s.toString)
      }
    }
  }
  
  def createStatApp(): Future[models.StatsApp] = {
    val today = new DateTime().withHourOfDay(0).withMillisOfDay(0)
    
    UserDao.findAll.map { users =>
        
        val list = ProviderDispatcher.listAll.map { provider =>
          (provider.name -> nbUsersByProvider(users, provider.name))
        }.sortBy(_._2).reverse
          
        models.StatsApp(
            DateTime.now().getMillis(),
            users.size,
            nbUsersByDate(users, today),
            list.map(s => models.StatsToken(s._1, s._2)),
            nbAccountsByUser(users).toSeq.map(s => models.StatsX(s._1, s._2)),
            nbColumnsByUser(users).toSeq.map(s => models.StatsX(s._1, s._2)),
            nbServiceByUser(users).toSeq.map(s => models.StatsX(s._1, s._2)),
            nbServiceByColumn(users).toSeq.map(s => models.StatsX(s._1, s._2)),
            models.StatsX(HelperUserInfosActor.getNbAccount, HelperUserInfosActor.getNbActor),
            models.StatsX(CmdToUser.getNbAccount, CmdToUser.getNbChannels),
            models.StatsX(HelperProviderActor.getNbAccount, HelperProviderActor.getNbActor),
            CmdToUser.getNbOtherAccount
        )
    }
  }
  
  def toReadStats(stats: models.StatsApp, includePermaLink: Boolean): String = {
    val res = new StringBuilder
    res ++= """
 _____ _    _           _             _____ _        _       
/  ___| |  (_)         | |           /  ___| |      | |      
\ `--.| | ___ _ __ ___ | |__   ___   \ `--.| |_ __ _| |_ ___ 
 `--. \ |/ / | '_ ` _ \| '_ \ / _ \   `--. \ __/ _` | __/ __|
/\__/ /   <| | | | | | | |_) | (_) | /\__/ / || (_| | |_\__ \
\____/|_|\_\_|_| |_| |_|_.__/ \___/  \____/ \__\__,_|\__|___/
                                                             
                                                             
"""
    
    res ++= "date = " + DateTimeFormat.forPattern("dd-MMMM-yyyy HH:mm").print(new DateTime(stats.timestamp)) + "\n\n"
      
    res ++= "nb users = " + stats.nbUsers + "\n\n"
    
    res ++= "nb connexions : " + stats.nbConnexion + "\n\n"
    
    res ++= "nb tokens :\n"
    stats.nbToken.foreach { m =>
      res ++= "\t" + m.providerName + " = " + m.nbToken + "\n"
    }
    res ++= "\n"
    
    res ++= "nb accounts by user :\n"
    res ++= formatMap(stats.nbUsers, stats.nbAccounts)
    
    res ++= "nb columns by user :\n"
    res ++= formatMap(stats.nbUsers, stats.nbColumns)
    
    res ++= "nb services by columns :\n"
    res ++= formatMap(stats.nbUsers, stats.nbServicesByColumn)
    
    res ++= "nb services by user :\n"
    res ++= formatMap(stats.nbUsers, stats.nbServices)
    
    res ++= "nb user actors actifs :\n"
    res ++= "\t" + HelperUserInfosActor.getNbAccount + " avec " + HelperUserInfosActor.getNbActor + "\n"
    res ++= "nb provider actor actifs : " + "\n"
    res ++= "\t" + HelperProviderActor.getNbAccount + " avec " + HelperProviderActor.getNbActor + "\n"
    
    res ++= "nb channels actifs :\n"
    res ++= "\t" + CmdToUser.getNbAccount + " avec " + CmdToUser.getNbChannels + "\n"
    res ++= "\t" + CmdToUser.getNbOtherAccount + " autres accounts\n\n"
    
    if(includePermaLink) {
      res ++= "Perma-link : " + routes.Stats.load(stats.timestamp.toString)
    }
    
    res.toString
  }
  
  def nbUsersByDate(users: Seq[User], date: DateTime):Int = {
    users.filter{ user =>
      user.accounts.exists { accounts =>
        new DateTime(accounts.lastUse).isAfter(date)
      }
    }.size
  }
  
  def nbUsersByProvider(users: Seq[User], providerName: String):Int = {
    users.filter{ user =>
      user.distants.exists { providerUser =>
        providerUser.socialType == providerName
      }
    }.size
  }
  
  def formatMap(nbUsers: Double, map : Seq[models.StatsX]) = {
    val res = new StringBuilder
    map.sortBy(_.nbUser).foreach { m => 
        res ++= "\t" + m.nbUser + " : " + m.nbX + "\n"
    }
    var nb = 0.0
    var coef = 0
    map.foreach(coef += _.nbX)
    map.foreach { v =>
      nb += v.nbX * v.nbUser
    }
    val r = nb/coef
    res ++= "Average : " + r + "\n\n"
  }
  
  def nbColumnsByUser(users: Seq[User]):Map[Int, Int] = {
    var map = Map.empty[Int, Int]
    users.foreach{ user =>
      val size = user.columns.size
      user.columns.map { columns =>
        val value = map.get(size).getOrElse(0) + 1
        map += (size -> value)
      }
    }
    map
  }
  
  def nbAccountsByUser(users: Seq[User]):Map[Int, Int] = {
    var map = Map.empty[Int, Int]
    users.foreach{ user =>
      val size = user.accounts.size
      val value = map.get(size).getOrElse(0) + 1
      map += (size -> value)
    }
    map
  }
  
  def nbServiceByColumn(users: Seq[User]):Map[Int, Int] = {
    var map = Map.empty[Int, Int]
    users.foreach{ user =>
      user.columns.map { column =>
        val size = column.unifiedRequests.size
        val value = map.get(size).getOrElse(0) + 1
        map += (size -> value)
      }
    }
    map
  }
  
  def nbServiceByUser(users: Seq[User]):Map[Int, Int] = {
    var map = Map.empty[Int, Int]
    users.foreach{ user =>
      var size = 0
      user.columns.map { column =>
        size += column.unifiedRequests.size
      }
      val value = map.get(size).getOrElse(0) + 1
      map += (size -> value)
    }
    map
  }
  
}