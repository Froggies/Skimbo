package oldModel

case class OldUnifiedRequest(
  service: String,
  args: Option[Map[String, String]]
)

object OldUnifiedRequest {

}