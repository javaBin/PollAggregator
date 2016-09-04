package no.javazone.poll.storage

import doobie.imports._
import no.javazone.poll.{MacAddress, OnlineMessage}

object BoxesQueries extends MetaImplicit{

  def ensureCreatedBox(msg: OnlineMessage): Update0 = {
    sql"""
           INSERT INTO boxes (mac, online)
           SELECT ${msg.mac}, FALSE
           WHERE NOT EXISTS (
              SELECT 1
              FROM boxes
              WHERE mac = ${msg.mac}
           )
         """.update
  }

  def updateOnline(msg: OnlineMessage): Update0 = {
    sql"""
            UPDATE boxes
            SET online = ${msg.online}
            WHERE mac = ${msg.mac}
         """.update
  }

  def findBoxMac(mac: MacAddress): Query0[Int] = {
    sql"""
          SELECT id FROM boxes
          WHERE mac = $mac
       """.query[Int]
  }

}

