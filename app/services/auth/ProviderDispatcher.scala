package services.auth

import services.auth.providers._
import play.api.mvc.RequestHeader

object ProviderDispatcher {

  private val providers: Seq[AuthProvider] = Seq(
    Twitter,
    Facebook,
    GitHub,
    GooglePlus,
    LinkedIn,
    Scoopit,
    StackExchange,
    Trello,
    Viadeo,
    BetaSeries)

  def apply(providerName: String) = get(providerName)

  def get(providerName: String): Option[AuthProvider] =
    providers.find(_.name == providerName)

  def atLeastOneIsConnected(implicit req: RequestHeader): Boolean =
    providers.exists(_.hasToken)

  def listAll: Seq[AuthProvider] = providers

  def listConnecteds(implicit req: RequestHeader): Seq[AuthProvider] =
    providers.filter(_.hasToken)

}

