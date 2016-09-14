package no.javazone.poll.storage

import java.time.LocalDateTime

import doobie.imports._
import no.javazone.poll.{OnlineMessage, SerialLabelMessage, VoteMessage}

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
      boxId <- BoxesQueries.findBoxMac(msg.mac).unique
      id <- EventQueries.insertEvent(msg, boxId).withUniqueGeneratedKeys[Int]("id")
    } yield id

    res.transact(xa)
  }

  def setBoxLabel(msg: SerialLabelMessage): Task[Unit] = {
    val res = for {
      boxId <- BoxesQueries.findBoxMac(msg.mac).unique
      _ <- LabelsQueries.ensureCreatedLabel(boxId, s"serial_${msg.id}").withUniqueGeneratedKeys[Int]("id")
    } yield ()
    res.transact(xa)
  }

  def votesForLabel(
      label: String,
      inFrom: Option[LocalDateTime],
      inTo: Option[LocalDateTime]
  ): Task[Map[String, Int]] = {
    val now: LocalDateTime = LocalDateTime.now
    val from  = inFrom.getOrElse(now.minusHours(1))
    val to: LocalDateTime = inTo.getOrElse(now)
    val res = for {
      buttonOne <- EventQueries.countValuesBetweenDates(label, 1, from, to).unique
      buttonTwo <- EventQueries.countValuesBetweenDates(label, 2, from, to).unique
      buttonTree <- EventQueries.countValuesBetweenDates(label, 3, from, to).unique
    } yield {
      Map(
        "total" -> (buttonOne + buttonTwo + buttonTree),
        "green" -> buttonOne,
        "yellow" -> buttonTwo,
        "red" -> buttonTree)
    }

    res.transact(xa)
  }

}
