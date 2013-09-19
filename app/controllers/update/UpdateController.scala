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
    
    var addedUser = new ArrayList[OldUser]
    var nonAddedUser = new ArrayList[OldUser]
    var sizeAllUsers = 0
    
    val f = OldUserDao.findAll.map { allUsers =>
      sizeAllUsers = allUsers.size
      allUsers.map { oldUser =>
        val user = OldUser.toUser(oldUser)
        UserDao.delete(oldUser.accounts.head.id).onComplete {
          case Failure(e) => nonAddedUser.add(oldUser)
          case Success(_) => {
            UserDao.add(user).onComplete {
              case Failure(e) => nonAddedUser.add(oldUser)
              case Success(_) => addedUser.add(oldUser)
            }
          }
        }
      }
    }
    
    Async {
      f.map { n =>
        val sb = new StringBuilder
        sb ++= "Added : "
        sb ++= addedUser.size.toString
        sb ++= "/"
        sb ++= sizeAllUsers.toString
        sb ++= "\n\n"
        var i = 0;
        if(!nonAddedUser.isEmpty) {
          sb ++= "users :\n\n"
        }
        while(i < nonAddedUser.size) {
          sb ++= nonAddedUser.get(i).accounts.head.id
          sb ++= "\n"
          i = i + 1
        }
        Ok(sb.toString)
      }
    }
    
  }
  
}