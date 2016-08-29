package no.javazone.poll.storage

import doobie.imports._
import no.javazone.poll.{OnlineMessage, VoteMessage}

import scalaz.concurrent.Task

class StorageService(xa: Transactor[Task]) {

  def onlineEvent(msg: OnlineMessage): Task[Unit] = {
    val res = for {
      _ <- BoxesQueries.ensureCreatedBox(msg).withGeneratedKeys[Int]("id").run
      _ <- BoxesQueries.updateOnline(msg).run
    } yield ()
    res.transact(xa)
  }

  def voteEvent(msg: VoteMessage): Task[Int] = {
    val res = for {
      boxId <- BoxesQueries.findBoxId(msg.mac, 0).unique
      id <- EventQueries.insertEvent(msg, boxId).withUniqueGeneratedKeys[Int]("id")
    } yield id

    res.transact(xa)
  }

}
