package no.javazone.poll.storage

import java.time.{Instant, LocalDateTime, ZoneOffset}

import doobie.util.meta.Meta
import doobie.util.meta.Meta._
import no.javazone.poll.MacAddress
import org.postgresql.util.PGobject

import scalaz.Scalaz._

trait MetaImplicit {

  implicit val LocalDateTimeType: Meta[LocalDateTime] =
    Meta[java.sql.Timestamp].nxmap(
      ts => Instant.ofEpochMilli(ts.getTime).atZone(ZoneOffset.UTC).toLocalDateTime,
      dt => new java.sql.Timestamp(dt.toInstant(ZoneOffset.UTC).toEpochMilli)
    )

  implicit val MacAddrType = Meta.other[PGobject]("macaddr")
      .xmap[MacAddress](
    a => MacAddress(a.getValue),
    b => new PGobject <| (_.setType("macaddr")) <| (_.setValue(b.address))
  )

}
