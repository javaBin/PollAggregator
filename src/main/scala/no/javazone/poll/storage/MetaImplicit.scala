package no.javazone.poll.storage

import java.time.{LocalDateTime, ZoneOffset}

import doobie.util.meta.Meta

trait MetaImplicit {

  implicit val LocalDateTimeMeta: Meta[LocalDateTime] =
    Meta[java.sql.Timestamp].nxmap(
      ts => LocalDateTime.ofEpochSecond(ts.getTime, 0, ZoneOffset.UTC),
      dt => new java.sql.Timestamp(dt.toEpochSecond(ZoneOffset.UTC))
    )
}
