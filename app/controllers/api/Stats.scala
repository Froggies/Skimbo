package controllers.api

import org.joda.time.DateTime
import models.User
import play.api.mvc._
import services.UserDao
import services.auth.ProviderDispatcher

object Stats extends Controller {

  import play.api.libs.concurrent.Execution.Implicits._
  
  val today = new DateTime().withHourOfDay(0).withMillisOfDay(0)
  val week = today.withDayOfWeek(1)
  val month = today.withDayOfMonth(1)
  val year = today.withMonthOfYear(1)
  
  def get() = Action { implicit request =>
    Async {
      UserDao.findAll.map { users =>
        val res = new StringBuilder
        res ++= "Skimbo's stats\n\n"
        
        res ++= "nb users = " + users.size + "\n\n"
        
        res ++= "nb connexions :\n"
        res ++= "\t" + "today = " + nbUsersByDate(users, today) + "\n"
        res ++= "\t" + "weekly = " + nbUsersByDate(users, week) + "\n"
        res ++= "\t" + "monthly = " + nbUsersByDate(users, month) + "\n"
        res ++= "\t" + "yearly = " + nbUsersByDate(users, year) + "\n\n"
        
        res ++= "nb tokens :\n"
        val list = ProviderDispatcher.listAll.map { provider =>
          (provider.name -> nbUsersByProvider(users, provider.name))
        }
        list.sortBy(_._2).reverse.foreach { m =>
          res ++= "\t" + m._1 + " = " + m._2 + "\n"
        }
        res ++= "\n"
        
        res ++= "nb accounts by user :\n"
        res ++= formatMap(users.size, nbAccountsByUser(users))
        
        res ++= "nb columns by user :\n"
        res ++= formatMap(users.size, nbColumnsByUser(users))
        
        res ++= "nb services by columns :\n"
        res ++= formatMap(users.size, nbServiceByColumn(users))
        
        res ++= "nb services by user :\n"
        res ++= formatMap(users.size, nbServiceByUser(users))
          
        Ok(res.toString)
      }
    }
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
      user.distants.map(_.exists { providerUser =>
        providerUser.socialType == providerName
      }).getOrElse(false)
    }.size
  }
  
  def formatMap(nbUsers: Double, map : Map[Int, Int]) = {
    val res = new StringBuilder
    map.toList.sortBy(_._1).foreach { m => 
        res ++= "\t" + m._1 + " : " + m._2 + "\n"
    }
    var nb = 0.0
    var coef = 0
    map.toList.foreach(coef += _._2)
    map.toList.foreach { v =>
      nb += v._2 * v._1
    }
    val r = nb/coef
    res ++= "Average : " + r + "\n\n"
  }
  
  def nbColumnsByUser(users: Seq[User]):Map[Int, Int] = {
    var map = Map.empty[Int, Int]
    users.foreach{ user =>
      user.columns.map { columns =>
        val size = columns.size
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
      user.columns.map { columns =>
        columns.map { column =>
          val size = column.unifiedRequests.size
          val value = map.get(size).getOrElse(0) + 1
          map += (size -> value)
        }
      }
    }
    map
  }
  
  def nbServiceByUser(users: Seq[User]):Map[Int, Int] = {
    var map = Map.empty[Int, Int]
    users.foreach{ user =>
      user.columns.map { columns =>
        var size = 0
        columns.map { column =>
          size += column.unifiedRequests.size
        }
        val value = map.get(size).getOrElse(0) + 1
        map += (size -> value)
      }
    }
    map
  }
  
}