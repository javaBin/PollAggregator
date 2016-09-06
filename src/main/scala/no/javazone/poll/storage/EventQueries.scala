package no.javazone.poll.storage

import java.time.LocalDateTime

import doobie.imports._
import no.javazone.poll.VoteMessage

object EventQueries extends MetaImplicit {
  def insertEvent(msg: VoteMessage, boxId: Int): Update0 = {
    sql"""
        INSERT INTO events (
          box_id,
          occurred,
          vote
        ) values (
          $boxId,
          ${msg.occurred},
          ${msg.buttonValue}
        )
      """.update
  }

  def countValuesBetweenDates(label: String, value: Int, from: LocalDateTime, to: LocalDateTime): Query0[Int] =  {
    sql"""
          SELECT count(*)
          FROM events e JOIN labels l ON l.box_id = e.box_id
          WHERE l.name = $label
          AND e.occurred BETWEEN $from AND $to
        """.query[Int]
  }

}
