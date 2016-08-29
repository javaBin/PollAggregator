package no.javazone.poll.storage

import doobie.imports._
import no.javazone.poll.OnlineMessage

object BoxesQueries extends MetaImplicit{

  def ensureCreatedBox(msg: OnlineMessage): Update0 = {
    val label = 0
    sql"""
           INSERT INTO boxes (mac, label, online)
           SELECT ${msg.mac}, $label, FALSE
           WHERE NOT EXISTS (
              SELECT 1
              FROM boxes
              WHERE mac = ${msg.mac}
              AND label = $label
           )
         """.update
  }

  def updateOnline(msg: OnlineMessage): Update0 = {
    val label = 0
    sql"""
            UPDATE boxes
            SET online = ${msg.online}
            WHERE mac = ${msg.mac}
            AND label = $label
         """.update
  }

  def findBoxId(mac: String, label: Int): Query0[Int] = {
    sql"""
          SELECT id FROM boxes
          WHERE mac = $mac
          AND label = $label
       """.query[Int]
  }

}

