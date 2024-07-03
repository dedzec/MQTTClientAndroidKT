package com.dedzec.mqttclientandroidkt

import android.content.Context
import android.util.Log
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*

class MQTTClient(context: Context, serverURI: String, clientId: String) {

    private var mqttClient: MqttAndroidClient = MqttAndroidClient(context, serverURI, clientId)
    private val qos = 0
    private val retained = false

    private val defaultCbConnect = object : IMqttActionListener {
        private val title = "defaultCbConnect"

        override fun onSuccess(asyncActionToken: IMqttToken) {
            Log.d(title, "(Default) Connection success")
        }

        override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
            Log.d(title, "Connection failure: ${exception.toString()}")
        }
    }

    private val defaultCbClient = object : MqttCallback {
        private val title = "defaultCbClient"

        override fun messageArrived(topic: String, message: MqttMessage) {
            Log.d(title, "Receive message: ${message.toString()} from topic: $topic")
        }

        override fun connectionLost(cause: Throwable) {
            Log.d(title, "Connection lost ${cause.toString()}")
        }

        override fun deliveryComplete(token: IMqttDeliveryToken) {
            Log.d(title, "Delivery completed")
        }
    }

    private val defaultCbSubscribe = object : IMqttActionListener {
        private val title = "defaultCbSubscribe"

        override fun onSuccess(asyncActionToken: IMqttToken) {
            Log.d(title, "Subscribed to topic")
        }

        override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
            Log.d(title, "Failed to subscribe topic")
        }
    }

    private val defaultCbUnsubscribe = object : IMqttActionListener {
        private val title = "defaultCbUnsubscribe"

        override fun onSuccess(asyncActionToken: IMqttToken) {
            Log.d(title, "Unsubscribed to topic")
        }

        override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
            Log.d(title, "Failed to unsubscribe topic")
        }
    }

    private val defaultCbPublish = object : IMqttActionListener {
        private val title = "defaultCbPublish"

        override fun onSuccess(asyncActionToken: IMqttToken) {
            Log.d(title, "Message published to topic")
        }

        override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
            Log.d(title, "Failed to publish message to topic")
        }
    }

    private val defaultCbDisconnect = object : IMqttActionListener {
        private val title = "defaultCbDisconnect"

        override fun onSuccess(asyncActionToken: IMqttToken) {
            Log.d(title, "Disconnected")
        }

        override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
            Log.d(title, "Failed to disconnect")
        }
    }

    fun connect(username: String, password: String, cbConnect: IMqttActionListener = defaultCbConnect, cbClient: MqttCallback = defaultCbClient) {
        mqttClient.setCallback(cbClient)
        val options = MqttConnectOptions().apply {
            this.userName = username
            this.password = password.toCharArray()
        }

        try {
            mqttClient.connect(options, null, cbConnect)
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    fun isConnected(): Boolean {
        return mqttClient.isConnected
    }

    fun subscribe(topic: String, qos: Int = this.qos, cbSubscribe: IMqttActionListener = defaultCbSubscribe) {
        try {
            mqttClient.subscribe(topic, qos, null, cbSubscribe)
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    fun unsubscribe(topic: String, cbUnsubscribe: IMqttActionListener = defaultCbUnsubscribe) {
        try {
            mqttClient.unsubscribe(topic, null, cbUnsubscribe)
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    fun publish(topic: String, msg: String, qos: Int = this.qos, retained: Boolean = this.retained, cbPublish: IMqttActionListener = defaultCbPublish) {
        try {
            val message = MqttMessage().apply {
                payload = msg.toByteArray()
                this.qos = qos
            }
            message.setRetained(retained)
            mqttClient.publish(topic, message, null, cbPublish)
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    fun disconnect(cbDisconnect: IMqttActionListener = defaultCbDisconnect) {
        try {
            mqttClient.disconnect(null, cbDisconnect)
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }
}