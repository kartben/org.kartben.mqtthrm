package org.kartben.mqtthrm;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Listens to DataItems and Messages from the local node.
 */
public class DataLayerListenerService extends WearableListenerService {

	private static final String TAG = "DataLayerListenerServic";

	GoogleApiClient mGoogleApiClient;

	private MqttClient mqttClient;

	@Override
	public void onCreate() {
		super.onCreate();
		mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(
				Wearable.API).build();
		mGoogleApiClient.connect();

		try {
			mqttClient = new MqttClient("tcp://iot.eclipse.org:1883",
					MqttClient.generateClientId(), new MemoryPersistence());
			mqttClient.connect();
			// TODO subscribe to a specific topic to enable 2-way communication
		} catch (MqttException e) {
			LOGD(TAG, e.getMessage());
		}

	}

	@Override
	public void onDataChanged(DataEventBuffer dataEvents) {

	}

	@Override
	public void onMessageReceived(MessageEvent messageEvent) {
		LOGD(TAG, "onMessageReceived: " + messageEvent);

		// post using MQTT
		try {
			if (!mqttClient.isConnected()) {
				mqttClient.connect();
				// TODO subscribe to a specific topic to enable 2-way
				// communication... and use clean-session=false too
			}

			mqttClient.publish(messageEvent.getPath(), new MqttMessage(
					messageEvent.getData()));
		} catch (MqttException e) {
			LOGD(TAG, e.getMessage());
		}
	}

	@Override
	public void onPeerConnected(Node peer) {
		LOGD(TAG, "onPeerConnected: " + peer);
	}

	@Override
	public void onPeerDisconnected(Node peer) {
		LOGD(TAG, "onPeerDisconnected: " + peer);
	}

	public static void LOGD(final String tag, String message) {
		if (Log.isLoggable(tag, Log.DEBUG)) {
			Log.d(tag, message);
		}
	}
}
