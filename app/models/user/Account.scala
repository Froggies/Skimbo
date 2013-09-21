package models.user

import java.util.Date
import play.api.libs.json._
import play.api.libs.functional.syntax._
import reactivemongo.bson._
import services.dao.UtilBson

case class Account(
  id: String,
  lastUse: Date,
  name: String,
  client: String,
  sinceId: Seq[SinceId])
  
object Account {
  
  def create(id: String) = {
    Account(id, new Date(), "", "Skimbo", Seq.empty)
  }
  
  implicit val writer = (
	  (__ \ "id").write[String] and
	  (__ \ "lastUse").write[Date] and
	  (__ \ "name").write[String] and 
	  (__ \ "client").write[String] and 
	  (__ \ "sinceId").write[Seq[SinceId]]
	)(unlift(Account.unapply))
	
  def toBSON(account:Account) = {
    val sinceId = UtilBson.toArray[SinceId](account.sinceId, { sinceId =>
      SinceId.toBSON(sinceId)
    })
    BSONDocument( 
      "id" -> BSONString(account.id),
      "lastUse" -> BSONDateTime(account.lastUse.getTime()),
      "name" -> BSONString(account.name),
      "client" -> BSONString(account.client),
      "sinceId" -> sinceId)
  }
  
  def fromBSON(a: BSONDocument) = {
    val lastUse = new Date(a.getAs[BSONDateTime]("lastUse").get.value)
    val sinceId = UtilBson.tableTo(a, "sinceId", { document =>
      SinceId.fromBSON(document)
    })
    Account(
        a.getAs[String]("id").get, 
        lastUse,
        a.getAs[String]("name").get, 
        a.getAs[String]("client").get, 
        sinceId
    )
  }
}