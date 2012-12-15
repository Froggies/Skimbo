package updateDb._20121215

import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._
import services.UserDao
import reactivemongo.api._
import reactivemongo.bson._
import reactivemongo.bson.handlers.DefaultBSONHandlers.DefaultBSONDocumentWriter
import reactivemongo.bson.handlers.DefaultBSONHandlers.DefaultBSONReaderHandler
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.concurrent.Future

object addIndexSizeColumns extends Specification {

  val testConfiguration = Map("mongodb.db" -> "skimbo")

  "Columns" should {
    "Add index, width and height" in {
      running(FakeApplication(additionalConfiguration = testConfiguration)) {

        import scala.concurrent.ExecutionContext.Implicits.global

        def findAll(): Future[List[OldUser]] = {
          val query = BSONDocument()
          implicit val reader = OldUser.UserBSONReader
          UserDao.collection.find(query).toList
        }

        def deleteColumn(user: OldUser, columnTitle: String) = {
          val query = BSONDocument("accounts.id" -> new BSONString(user.accounts.head.id))
          val update = BSONDocument("$pull" -> BSONDocument("columns" -> BSONDocument("title" -> new BSONString(columnTitle))))
          UserDao.collection.update(query, update)
        }

        def updateColumn(user: OldUser, title: String, column: OldColumn) = {
          deleteColumn(user, title)
          val query = BSONDocument("accounts.id" -> new BSONString(user.accounts.head.id))
          val update = BSONDocument("$push" -> BSONDocument("columns" -> OldColumn.toBSON(column)))
          UserDao.collection.update(query, update)
        }

        val optionUsers: List[OldUser] = Await.result(findAll, Duration("10 seconds"))
        optionUsers.map({ user =>
          user.columns.map(_.map { column =>
            Await.result(updateColumn(user, column.title, column), Duration("10 seconds"))
          })
        })

        val optionUsers2: List[models.User] = Await.result(UserDao.findAll, Duration("10 seconds"))

        optionUsers.size must be equalTo optionUsers2.size
      }
    }
  }

}