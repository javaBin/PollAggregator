package no.javazone.poll

import java.time.LocalDateTime

import no.javazone.poll.storage.StorageService
import org.http4s.dsl.{Root, _}
import org.http4s.headers.`Content-Type`
import org.http4s.server.Router
import org.http4s.{HttpService, MediaType}
import org.json4s._
import org.json4s.native.Serialization.write

object Api {

  implicit val formats = DefaultFormats
  val jsonContentType: Some[`Content-Type`] = Some(`Content-Type`(MediaType.`application/json`))

  def services(storage: StorageService) = Router(
    "" -> root,
    "/votes" -> votes(storage)
  )

  private def root = HttpService {
    case req@GET -> Root / "ping" => Ok("pong")
  }

//  implicit val localDateTimeParamDecoder: QueryParamDecoder[LocalDateTime] = new QueryParamDecoder[LocalDateTime] {
//    def decode(value: QueryParameterValue): ValidationNel[ParseFailure, LocalDateTime] =
//      QueryParamDecoder.decodeBy[LocalDateTime, String](s => LocalDateTime.parse(s)).decode(value)
//  } // ehm..? todo

  object From extends OptionalQueryParamDecoderMatcher[String]("from")
  object To extends OptionalQueryParamDecoderMatcher[String]("to")

  private def votes(storage: StorageService) = HttpService {

    case req@GET -> Root / label :? From(from) +& To(to) => {
      val run = storage.votesForLabel(label, from.map(LocalDateTime.parse), to.map(LocalDateTime.parse)).run

      Ok()
          .withBody(write(run))
          .withContentType(jsonContentType)
    }
  }

}
