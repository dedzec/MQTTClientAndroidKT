package com.dedzec.mqttclientandroidkt

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.navigation.Navigation
import com.dedzec.mqttclientandroidkt.MQTTConstants.MQTT_CLIENT_ID
import com.dedzec.mqttclientandroidkt.MQTTConstants.MQTT_CLIENT_ID_KEY
import com.dedzec.mqttclientandroidkt.MQTTConstants.MQTT_PWD
import com.dedzec.mqttclientandroidkt.MQTTConstants.MQTT_PWD_KEY
import com.dedzec.mqttclientandroidkt.MQTTConstants.MQTT_SERVER_URI
import com.dedzec.mqttclientandroidkt.MQTTConstants.MQTT_SERVER_URI_KEY
import com.dedzec.mqttclientandroidkt.MQTTConstants.MQTT_USERNAME
import com.dedzec.mqttclientandroidkt.MQTTConstants.MQTT_USERNAME_KEY

class ConnectFragment : Fragment() {
    private lateinit var edittextServerUri: EditText
    private lateinit var edittextClientId: EditText
    private lateinit var edittextUsername: EditText
    private lateinit var edittextPassword: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_connect, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        edittextServerUri = view.findViewById(R.id.edittext_server_uri)
        edittextClientId = view.findViewById(R.id.edittext_client_id)
        edittextUsername = view.findViewById(R.id.edittext_username)
        edittextPassword = view.findViewById(R.id.edittext_password)

        view.findViewById<View>(R.id.button_prefill).setOnClickListener {
            // Set default values in edit texts
            edittextServerUri.setText(MQTT_SERVER_URI)
            edittextClientId.setText(MQTT_CLIENT_ID)
            edittextUsername.setText(MQTT_USERNAME)
            edittextPassword.setText(MQTT_PWD)
        }

        view.findViewById<View>(R.id.button_clean).setOnClickListener {
            // Clean values in edit texts
            edittextServerUri.setText("")
            edittextClientId.setText("")
            edittextUsername.setText("")
            edittextPassword.setText("")
        }

        view.findViewById<View>(R.id.button_connect).setOnClickListener {
            val serverURIFromEditText = edittextServerUri.text.toString()
            val clientIDFromEditText = edittextClientId.text.toString()
            val usernameFromEditText = edittextUsername.text.toString()
            val pwdFromEditText = edittextPassword.text.toString()

            val mqttCredentialsBundle = Bundle().apply {
                putString(MQTT_SERVER_URI_KEY, serverURIFromEditText)
                putString(MQTT_CLIENT_ID_KEY, clientIDFromEditText)
                putString(MQTT_USERNAME_KEY, usernameFromEditText)
                putString(MQTT_PWD_KEY, pwdFromEditText)
            }

            Log.d("fragment", "ConnectFragment called")
            Navigation.findNavController(view).navigate(R.id.action_ConnectFragment_to_ClientFragment, mqttCredentialsBundle)
        }
    }
}
