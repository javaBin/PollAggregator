package no.javazone.poll

import java.time.LocalDateTime
import java.time.LocalDateTime.now

import org.eclipse.paho.client.mqttv3.MqttMessage

import scala.util.Try

sealed trait Message

case class VoteMessage(
    occurred: LocalDateTime,
    mac: MacAddress,
    buttonValue: Int
) extends Message

case class OnlineMessage(
    occurred: LocalDateTime,
    mac: MacAddress,
    online: Boolean
) extends Message

case class SerialLabelMessage(
    occurred: LocalDateTime,
    mac: MacAddress,
    id: Int
) extends Message

case class ParseFaultMessage(
    occurred: LocalDateTime,
    topic: String,
    content: String
) extends Message

case class UnknownMessage(
    occurred: LocalDateTime,
    topic: String,
    content: String
) extends Message

object Message {
  def apply(topic: String, message: MqttMessage): Message = {
    val t: List[String] = topic.split("/").toList.filter(_.trim.nonEmpty)
    t match {
      case "pollerbox" :: mac :: "vote" :: Nil =>
        parse(VoteMessage(
          now(),
          MacAddress(mac),
          new String(message.getPayload).toInt),
          topic,
          message)
      case "pollerbox" :: mac :: "online" :: Nil =>
        parse(OnlineMessage(
          now(),
          MacAddress(mac),
          new String(message.getPayload).toBoolean),
          topic,
          message)
      case "pollerbox" ::  mac :: "serial" :: Nil =>
        parse(SerialLabelMessage(
          now(),
          MacAddress(mac),
          new String(message.getPayload).toInt),
          topic,
          message)
      case _ => UnknownMessage(
        now(),
        topic,
        new String(message.getPayload))
    }
  }

  private def parse[T >: Message](p: => T, topic: String, message: MqttMessage): T = {
    Try(p).getOrElse(ParseFaultMessage(
      now(),
      topic,
      new String(message.getPayload)))
  }

}