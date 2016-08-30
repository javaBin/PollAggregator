package no.javazone.poll

import org.http4s.HttpService
import org.http4s.dsl.{Root, _}

object Api {

  private val ping = HttpService {
    case req@GET -> Root / "ping" => Ok("pong")
  }

  val services = ping

}
