package com.dedzec.mqttclientandroidkt

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttMessage
import com.dedzec.mqttclientandroidkt.MQTTConstants.MQTT_SERVER_URI_KEY
import com.dedzec.mqttclientandroidkt.MQTTConstants.MQTT_CLIENT_ID_KEY
import com.dedzec.mqttclientandroidkt.MQTTConstants.MQTT_USERNAME_KEY
import com.dedzec.mqttclientandroidkt.MQTTConstants.MQTT_PWD_KEY
import com.dedzec.mqttclientandroidkt.MQTTConstants.MQTT_TEST_TOPIC
import com.dedzec.mqttclientandroidkt.MQTTConstants.MQTT_TEST_MSG

class ClientFragment : Fragment() {
    private lateinit var mqttClient: MQTTClient
    private lateinit var edittextPubTopic: EditText
    private lateinit var edittextPubMsg: EditText
    private lateinit var edittextSubTopic: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("ClientFragment", "ClientFragment init")

        requireActivity().onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            private val logText = "ClientFragment"

            override fun handleOnBackPressed() {
                // Handle the back button event
                if (mqttClient.isConnected()) {
                    // Disconnect from MQTT Broker
                    mqttClient.disconnect(object : IMqttActionListener {
                        override fun onSuccess(asyncActionToken: IMqttToken) {
                            Log.d(logText, "Disconnected")

                            Toast.makeText(activity?.applicationContext, "MQTT Disconnection success", Toast.LENGTH_SHORT).show()

                            // Disconnection success, come back to Connect Fragment
                            Navigation.findNavController(view!!).navigate(R.id.action_ClientFragment_to_ConnectFragment)
                        }

                        override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                            Log.d(logText, "Failed to disconnect")
                        }
                    })
                } else {
                    Log.d(logText, "Impossible to disconnect, no server connected")
                }
            }

            private fun closeFragment() {
                // Disable to close fragment
                isEnabled = false
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_client, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val logText = "ClientFragment"

        // Get EditText's
        edittextPubTopic = view.findViewById(R.id.edittext_pubtopic)
        edittextPubMsg = view.findViewById(R.id.edittext_pubmsg)
        edittextSubTopic = view.findViewById(R.id.edittext_subtopic)

        // Get arguments passed by ConnectFragment
        val serverURI = requireArguments().getString(MQTT_SERVER_URI_KEY)
        val clientId = requireArguments().getString(MQTT_CLIENT_ID_KEY)
        val username = requireArguments().getString(MQTT_USERNAME_KEY)
        val pwd = requireArguments().getString(MQTT_PWD_KEY)

        // Check if passed arguments are valid
        if (!serverURI.isNullOrBlank() && !clientId.isNullOrBlank() && !username.isNullOrBlank() && !pwd.isNullOrBlank()) {
            // Open MQTT Broker communication
            mqttClient = MQTTClient(requireActivity().applicationContext, serverURI, clientId)

            // Connect and login to MQTT Broker
            mqttClient.connect(username, pwd,
                object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken?) {
                        Log.d(logText, "Connection success")
                        Toast.makeText(requireActivity().applicationContext, "MQTT Connection success", Toast.LENGTH_SHORT).show()
                    }

                    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                        Log.d(logText, "Connection failure: $exception")
                        Toast.makeText(requireActivity().applicationContext, "MQTT Connection fails: $exception", Toast.LENGTH_SHORT).show()
                        Navigation.findNavController(view).navigate(R.id.action_ClientFragment_to_ConnectFragment)
                    }
                },
                object : MqttCallback {
                    override fun messageArrived(topic: String?, message: MqttMessage?) {
                        val msg = "Receive message: ${message?.toString()} from topic: $topic"
                        Log.d(logText, msg)
                        Toast.makeText(requireActivity().applicationContext, msg, Toast.LENGTH_SHORT).show()
                    }

                    override fun connectionLost(cause: Throwable?) {
                        Log.d(logText, "Connection lost $cause")
                    }

                    override fun deliveryComplete(token: IMqttDeliveryToken?) {
                        Log.d(logText, "Delivery complete")
                    }
                })

        } else {
            // Arguments are not valid, come back to Connect Fragment
            Navigation.findNavController(view).navigate(R.id.action_ClientFragment_to_ConnectFragment)
        }

        view.findViewById<View>(R.id.button_prefill_client).setOnClickListener {
            // Set default values in edit texts
            edittextPubTopic.setText(MQTT_TEST_TOPIC)
            edittextPubMsg.setText(MQTT_TEST_MSG)
            edittextSubTopic.setText(MQTT_TEST_TOPIC)
        }

        view.findViewById<View>(R.id.button_clean_client).setOnClickListener {
            // Clean values in edit texts
            edittextPubTopic.setText("")
            edittextPubMsg.setText("")
            edittextSubTopic.setText("")
        }

        view.findViewById<View>(R.id.button_disconnect).setOnClickListener {
            if (mqttClient.isConnected()) {
                mqttClient.disconnect(object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken?) {
                        Log.d(logText, "Disconnected")
                        Toast.makeText(requireActivity().applicationContext, "MQTT Disconnection success", Toast.LENGTH_SHORT).show()
                        Navigation.findNavController(view).navigate(R.id.action_ClientFragment_to_ConnectFragment)
                    }

                    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                        Log.d(logText, "Failed to disconnect")
                    }
                })
            } else {
                Log.d(logText, "Impossible to disconnect, no server connected")
            }
        }

        view.findViewById<View>(R.id.button_publish).setOnClickListener {
            val topic = edittextPubTopic.text.toString()
            val message = edittextPubMsg.text.toString()

            if (mqttClient.isConnected()) {
                mqttClient.publish(topic, message, 1, false, object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken?) {
                        val msg = "Publish message: $message to topic: $topic"
                        Log.d(logText, msg)
                        Toast.makeText(requireActivity().applicationContext, msg, Toast.LENGTH_SHORT).show()
                    }

                    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                        Log.d(logText, "Failed to publish message to topic")
                    }
                })
            } else {
                Log.d(logText, "Impossible to publish, no server connected")
            }
        }

        view.findViewById<View>(R.id.button_subscribe).setOnClickListener {
            val topic = edittextSubTopic.text.toString()

            if (mqttClient.isConnected()) {
                mqttClient.subscribe(topic, 1, object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken?) {
                        val msg = "Subscribed to: $topic"
                        Log.d(logText, msg)
                        Toast.makeText(requireActivity().applicationContext, msg, Toast.LENGTH_SHORT).show()
                    }

                    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                        Log.d(logText, "Failed to subscribe: $topic")
                    }
                })
            } else {
                Log.d(logText, "Impossible to subscribe, no server connected")
            }
        }

        view.findViewById<View>(R.id.button_unsubscribe).setOnClickListener {
            val topic = edittextSubTopic.text.toString()

            if (mqttClient.isConnected()) {
                mqttClient.unsubscribe(topic, object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken?) {
                        val msg = "Unsubscribed to: $topic"
                        Log.d(logText, msg)
                        Toast.makeText(requireActivity().applicationContext, msg, Toast.LENGTH_SHORT).show()
                    }

                    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                        Log.d(logText, "Failed to unsubscribe: $topic")
                    }
                })
            } else {
                Log.d(logText, "Impossible to unsubscribe, no server connected")
            }
        }
    }
}
