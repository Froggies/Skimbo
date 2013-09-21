package models.user

import play.api.libs.json._
import play.api.libs.functional.syntax._
import reactivemongo.bson._
import services.dao.UtilBson

case class OptionUser (
    email: Option[String],
    isAdmin: Boolean,
    isPremium: Boolean,
    features: Seq[Feature]
)

object OptionUser {
  
  def create() = {
    OptionUser(None, false, false, Seq.empty)
  }
  
  def merge(fromOption: OptionUser, toOption: OptionUser): OptionUser = {
    OptionUser(
        fromOption.email.orElse(toOption.email),
        fromOption.isAdmin || toOption.isAdmin,
        fromOption.isPremium || toOption.isPremium,
        fromOption.features
    )
  }
  
  implicit val reader = (
    (__ \ "email").readNullable[String] and
    (__ \ "isAdmin").read[Boolean] and
    (__ \ "isPremium").read[Boolean] and
    (__ \ "features").read[Seq[Feature]])(OptionUser.apply _)

  implicit val writer = (
    (__ \ "email").writeNullable[String] and
    (__ \ "isAdmin").write[Boolean] and
    (__ \ "isPremium").write[Boolean] and
    (__ \ "features").write[Seq[Feature]])(unlift(OptionUser.unapply))

  def toBSON(optionUser: OptionUser) = {
    val features = UtilBson.toArray[Feature](optionUser.features, { feature =>
      Feature.toBSON(feature)
    })
    BSONDocument(
      "email" -> BSONString(optionUser.email.getOrElse("")),
      "isAdmin" -> BSONBoolean(optionUser.isAdmin),
      "isPremium" -> BSONBoolean(optionUser.isPremium),
      "features" -> features)
  }

  def fromBSON(c: BSONDocument) = {
    val features = UtilBson.tableTo[Feature](c, "features", { r =>
      Feature.fromBSON(r)
    })
    val email = c.getAs[String]("email").get
    OptionUser(
        if(email.isEmpty()) { None } else { Some(email) }, 
        c.getAs[Boolean]("isAdmin").get,
        c.getAs[Boolean]("isPremium").get, 
        features)
  }
  
}