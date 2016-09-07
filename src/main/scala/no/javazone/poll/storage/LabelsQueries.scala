package no.javazone.poll.storage

import doobie.imports._

object LabelsQueries {
  def ensureCreatedLabel(boxId: Int, label: String): Update0 = {
    sql"""
           INSERT INTO labels (box_id, name)
           SELECT ${boxId}, ${label}
           WHERE NOT EXISTS (
              SELECT 1e
              FROM labels
              WHERE box_id = ${boxId}
           )
         """.update
  }
}
