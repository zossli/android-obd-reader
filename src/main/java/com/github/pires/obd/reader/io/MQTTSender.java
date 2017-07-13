package com.github.pires.obd.reader.io;

import com.github.pires.obd.reader.activity.ConfigActivity;

import android.content.SharedPreferences;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;

/**
 * Created by reto on 07.07.2017.
 */


public abstract class MQTTSender {
    private static final String TAG = MQTTSender.class.getName();

    public static void sendMessage(SharedPreferences prefs, MqttAndroidClient client, String cmdID, String cmdResult) {
            try {
                if (cmdResult == null) {
                    cmdResult = "NULL";
                }
                byte[] encodedPayload = cmdResult.getBytes("UTF-8");
                MqttMessage message = new MqttMessage(encodedPayload);
                message.setRetained(true);
                String topic = prefs.getString(ConfigActivity.MQTT_TOPIC, "");
                if (topic == "") {
                    topic = prefs.getString(ConfigActivity.VEHICLE_ID_KEY, "UNDEFINED_VIN") + "/" + cmdID;
                } else {
                    topic = topic + "/" + prefs.getString(ConfigActivity.VEHICLE_ID_KEY, "UNDEFINED_VIN") + "/" + cmdID;
                }
                client.publish(topic, message);

            } catch (UnsupportedEncodingException | MqttException e) {
                e.printStackTrace();
            }
        }

}
