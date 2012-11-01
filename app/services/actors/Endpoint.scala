package services.actors

import services.auth.GenericProvider

case class Endpoint(provider: GenericProvider, url : String, interval: Int = 5)