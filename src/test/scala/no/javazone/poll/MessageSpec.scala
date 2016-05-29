package no.javazone.poll

import org.eclipse.paho.client.mqttv3.MqttMessage
import org.scalatest.{FunSpec, Inside, Matchers}

class MessageSpec extends FunSpec with Matchers with Inside {
  val mac: String = "5C:CF:7F:06:00:00"

  describe("vote topic") {
    val voteTopic: String = s"pollerbox/$mac/vote"

    it("should parse correctly") {
      val msg: Message = Message(voteTopic, mqttMsg("1"))

      inside(msg) { case VoteMessage(_, macAddress, buttonValue) =>
        macAddress should be(mac)
        buttonValue should be(1)
      }
    }

    it("should not parse non number content") {
      val msg: Message = Message(voteTopic, mqttMsg("a"))

      inside(msg) { case ParseFaultMessage(_, topic, content) =>
        topic should be(voteTopic)
        content should be("a")
      }
    }
  }

  describe("online topic") {
    val onlineTopic: String = s"pollerbox/$mac/online"

    it("should parse correctly") {
      val msg: Message = Message(onlineTopic, mqttMsg("true"))

      inside(msg) { case OnlineMessage(_, macAddress, online) =>
        macAddress should be(mac)
        online should be(true)
      }
    }

    it("should not parse non number content") {
      val msg: Message = Message(onlineTopic, mqttMsg("a"))

      inside(msg) { case ParseFaultMessage(_, topic, content) =>
        topic should be(onlineTopic)
        content should be("a")
      }
    }
  }

  describe("unknown topic") {
    val unknownTopic: String = s"pollerbox/$mac/unknown"
    val unknownPollerboxTopic: String = s"pollerbox/$mac/unknown"

    it("should give unknown message") {
      val msg: Message = Message(unknownTopic, mqttMsg("1"))

      inside(msg) { case UnknownMessage(_, topic, content) =>
        topic should be(unknownTopic)
        content should be("1")
      }
    }

    it("should give unknown message for sub pollerbox topic") {
      val msg: Message = Message(unknownPollerboxTopic, mqttMsg("1"))

      inside(msg) { case UnknownMessage(_, topic, content) =>
        topic should be(unknownPollerboxTopic)
        content should be("1")
      }
    }
  }


  def mqttMsg(content: String): MqttMessage = {
    new MqttMessage(content.getBytes)
  }

}
