package org.kartben.mqtthrm;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Listens to DataItems and Messages from the local node.
 */
public class DataLayerListenerService extends WearableListenerService {

	private static final String TAG = "DataLayerListenerServic";

	GoogleApiClient mGoogleApiClient;

	@Override
	public void onCreate() {
		super.onCreate();
		mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(
				Wearable.API).build();
		mGoogleApiClient.connect();
	}

	@Override
	public void onDataChanged(DataEventBuffer dataEvents) {
		LOGD(TAG, "onDataChanged: " + dataEvents);
	}

	@Override
	public void onMessageReceived(MessageEvent messageEvent) {
		LOGD(TAG, "onMessageReceived: " + messageEvent);
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
		if (true && Log.isLoggable(tag, Log.DEBUG)) {
			Log.d(tag, message);
		}
	}
}
