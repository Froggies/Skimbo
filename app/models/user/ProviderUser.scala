package models.user
import play.api.libs.json.Writes
import play.api.libs.json.JsValue
import play.api.libs.json.Json

//keep has Option rules username, name, desctription and avatar for condition's providers
case class ProviderUser(
  id: String,
  socialType: String,
  username: Option[String] = None,
  name: Option[String] = None,
  description: Option[String] = None,
  avatar: Option[String] = None)
  
object ProviderUser {
  implicit val writes = new Writes[ProviderUser] {
    def writes(pu: ProviderUser): JsValue = {
      Json.obj(
        "id" -> pu.id,
        "socialType" -> pu.socialType,
        "username" -> pu.username,
        "name" -> pu.name,
        "description" -> pu.description,
        "avatar" -> pu.avatar
      )
    }
  }
}