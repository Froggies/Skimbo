package controllers.update

import play.api.mvc.Action
import play.api.mvc.Controller
import oldModel.OldUserDao
import scala.concurrent.ExecutionContext.Implicits.global
import oldModel.OldUser
import services.dao.UserDao
import scala.util.Failure
import scala.util.Success
import java.util.ArrayList

object UpdateController extends Controller {
  
  def updateUser() = Action {  implicit request =>
    
    var addedUser = 0
    var nonAddedUser = 0
    var sizeAllUsers = 0
    
    val f = OldUserDao.findAll.map { allUsers =>
      sizeAllUsers = allUsers.size
      allUsers.map { oldUser =>
        val user = OldUser.toUser(oldUser)
        UserDao.delete(oldUser.accounts.head.id).onComplete {
          case Failure(e) => nonAddedUser = nonAddedUser+1
          case Success(_) => {
            UserDao.add(user).onComplete {
              case Failure(e) => nonAddedUser = nonAddedUser+1
              case Success(_) => addedUser = addedUser+1
            }
          }
        }
      }
    }
    
    Async {
      f.map { n =>
        val sb = new StringBuilder
        sb ++= "Added : "
        sb ++= addedUser.toString
        sb ++= "/"
        sb ++= sizeAllUsers.toString
        sb ++= "\n\n"
        var i = 0;
        if(nonAddedUser > 0) {
          sb ++= "non added users : "
          sb ++= nonAddedUser.toString
          sb ++= "\n\n"
        }
        Ok(sb.toString)
      }
    }
    
  }
  
}