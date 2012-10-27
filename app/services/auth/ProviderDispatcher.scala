package services.auth

import services.auth.providers._
import play.api.mvc.RequestHeader

object ProviderDispatcher {
  
  private val providers = Seq(
      Twitter,
      Facebook,
      GitHub,
      GooglePlus,
      LinkedIn,
      Scoopit,
      StackExchange,
      Trello,
      Twitter,
      Viadeo
  ) 
  
  def apply(providerName: String) = getByName(providerName)

  def getByName(providerName: String) : Option[GenericProvider] = 
    providers.find(_.name == providerName)
    
  def atLeastOneIsConnected(implicit req: RequestHeader) : Boolean = 
    providers.exists(_.hasToken)
    
  def listAll : Seq[GenericProvider] = providers
  
  def listConnecteds(implicit req: RequestHeader) : Seq[GenericProvider] =
     providers.filter(_.hasToken)
    
}