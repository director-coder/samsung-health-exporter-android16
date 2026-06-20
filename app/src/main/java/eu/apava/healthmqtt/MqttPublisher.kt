package eu.apava.healthmqtt

import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import java.time.Instant
import java.util.UUID

class MqttPublisher(private val settings: AppSettings) {
    fun publishSteps(steps: Long) {
        val clientId = "health-mqtt-" + UUID.randomUUID().toString().take(8)
        val client = MqttClient(settings.brokerUri, clientId, MemoryPersistence())
        val options = MqttConnectOptions().apply {
            isCleanSession = true
            connectionTimeout = 10
            keepAliveInterval = 30
            if (settings.username.isNotBlank()) {
                userName = settings.username
                password = settings.password.toCharArray()
            }
        }

        client.connect(options)
        try {
            publishDiscovery(client)
            publish(client, "${settings.baseTopic}/availability", "online", retained = true)
            publish(client, "${settings.baseTopic}/steps_today/state", steps.toString(), retained = true)
            publish(client, "${settings.baseTopic}/steps_today/last_sync", Instant.now().toString(), retained = true)
        } finally {
            client.disconnect()
            client.close()
        }
    }

    private fun publishDiscovery(client: MqttClient) {
        val uniqueId = "health_mqtt_${settings.baseTopic.replace('/', '_')}_steps_today"
        val objectId = "health_mqtt_steps_today"
        val configTopic = "homeassistant/sensor/$objectId/config"
        val json = """
            {
              "name":"${escape(settings.deviceName)} Steps Today",
              "unique_id":"${escape(uniqueId)}",
              "state_topic":"${escape(settings.baseTopic)}/steps_today/state",
              "availability_topic":"${escape(settings.baseTopic)}/availability",
              "unit_of_measurement":"steps",
              "icon":"mdi:walk",
              "device":{
                "identifiers":["${escape(settings.baseTopic)}"],
                "name":"${escape(settings.deviceName)}",
                "manufacturer":"APAVA",
                "model":"Health Connect MQTT Android16"
              }
            }
        """.trimIndent()
        publish(client, configTopic, json, retained = true)
    }

    private fun publish(client: MqttClient, topic: String, payload: String, retained: Boolean) {
        val message = MqttMessage(payload.toByteArray(Charsets.UTF_8)).apply {
            qos = 1
            isRetained = retained
        }
        client.publish(topic, message)
    }

    private fun escape(value: String): String {
        return value.replace("\\", "\\\\").replace("\"", "\\\"")
    }
}
