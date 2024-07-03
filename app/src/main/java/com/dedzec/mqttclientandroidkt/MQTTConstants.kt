package com.dedzec.mqttclientandroidkt

object MQTTConstants {
    const val MQTT_SERVER_URI_KEY = "MQTT_SERVER_URI"
    const val MQTT_CLIENT_ID_KEY = "MQTT_CLIENT_ID"
    const val MQTT_USERNAME_KEY = "MQTT_USERNAME"
    const val MQTT_PWD_KEY = "MQTT_PWD"

//    const val MQTT_SERVER_URI = "tcp://broker.hivemq.com:1883"
    const val MQTT_SERVER_URI = "tcp://broker.emqx.io:1883"
    var MQTT_CLIENT_ID = "kotlin_client_03"
    var MQTT_USERNAME = "test"
    var MQTT_PWD = "test"

    const val MQTT_TEST_TOPIC = "A3/test/logs"
    const val MQTT_TEST_MSG = "Hello!"
}