package model;

object SocialNetwork extends Enumeration {
  type SocialNetwork = Value
  val Twitter, Facebook, Viadeo, LinkedIn, StackExchange, Github, Trello, GooglePlus = Value // FIXME : Endpoints, Not Providers
}
import SocialNetwork._
