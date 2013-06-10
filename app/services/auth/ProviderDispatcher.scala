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
    BetaSeries,
    Bitbucket,
    Reddit)

  def apply(providerName: String) = get(providerName)

  def get(providerName: String): Option[AuthProvider] =
    providers.find(_.name == providerName)

  def atLeastOneIsConnected(idUser: String): Boolean =
    providers.exists(_.hasToken(idUser))

  def listAll: Seq[AuthProvider] = providers

  def listConnecteds(idUser: String): Seq[AuthProvider] =
    providers.filter(_.hasToken(idUser))

}

