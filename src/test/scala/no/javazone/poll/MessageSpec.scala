package no.javazone.poll

import org.eclipse.paho.client.mqttv3.MqttMessage
import org.scalatest.{FunSpec, Matchers}

class MessageSpec extends FunSpec with Matchers {
  val mac: String = "5C:CF:7F:06:00:00"

  describe("vote topic") {
    val voteTopic: String = s"pollerbox/$mac/vote"

    it("should parse correctly") {
      val msg: Message = Message(voteTopic, mqttMsg("1"))

      msg should be(VoteMessage(mac, 1))
    }

    it("should not parse non number content") {
      val msg: Message = Message(voteTopic, mqttMsg("a"))

      msg should be(ParseFaultMessage(voteTopic, "a"))
    }
  }

  describe("online topic") {
    val onlineTopic: String = s"pollerbox/$mac/online"

    it("should parse correctly") {
      val msg: Message = Message(onlineTopic, mqttMsg("true"))

      msg should be(OnlineMessage(mac, online = true))
    }

    it("should not parse non number content") {
      val msg: Message = Message(onlineTopic, mqttMsg("a"))

      msg should be(ParseFaultMessage(onlineTopic, "a"))
    }
  }

  describe("unknown topic") {
    val unknownTopic: String = s"pollerbox/$mac/unknown"
    val unknownPollerboxTopic: String = s"pollerbox/$mac/unknown"

    it("should give unknown message") {
      val msg: Message = Message(unknownTopic, mqttMsg("1"))

      msg should be(UnknownMessage(unknownTopic, "1"))
    }

    it("should give unknown message for sub pollerbox topic") {
      val msg: Message = Message(unknownPollerboxTopic, mqttMsg("1"))

      msg should be(UnknownMessage(unknownPollerboxTopic, "1"))
    }
  }


  def mqttMsg(content: String): MqttMessage = {
    new MqttMessage(content.getBytes)
  }

}
