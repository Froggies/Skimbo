package services.actors

import services.auth.GenericProvider

case class Endpoint(provider: GenericProvider, url: String, interval: Int, idUser: String)