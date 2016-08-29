package no.javazone.poll.storage

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
}
