package org.kartben.mqtthrm;

import static org.kartben.mqtthrm.DataLayerListenerService.LOGD;

import java.util.Collection;
import java.util.HashSet;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

/**
 * Shows events and photo from the Wearable APIs.
 */
public class MainActivity extends Activity implements ConnectionCallbacks,
		OnConnectionFailedListener, DataApi.DataListener,
		MessageApi.MessageListener, SensorEventListener {

	private static final String TAG = "MainActivity";

	private GoogleApiClient mGoogleApiClient;
	private ListView mDataItemList;
	private TextView mIntroText;
	private View mLayout;
	private Handler mHandler;

	private SensorManager sensorManager;

	private Sensor hrmSensor;

	private long timeElapsedReference;

	@Override
	public void onCreate(Bundle b) {
		super.onCreate(b);
		mHandler = new Handler();
		LOGD(TAG, "onCreate");
		setContentView(R.layout.main_activity);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		mIntroText = (TextView) findViewById(R.id.intro);

		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addApi(Wearable.API).addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this).build();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mGoogleApiClient.connect();
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

	}

	private class PublishHrmTask extends AsyncTask<String, Void, Void> {
		@Override
		protected Void doInBackground(String... args) {
			Collection<String> nodes = getNodes();
			for (String node : nodes) {
				Wearable.MessageApi.sendMessage(mGoogleApiClient, node, node
						+ "/heartrate", args[0].getBytes());
			}
			return null;
		}
	}

	private Collection<String> getNodes() {
		HashSet<String> results = new HashSet<String>();
		NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi
				.getConnectedNodes(mGoogleApiClient).await();
		for (Node node : nodes.getNodes()) {
			results.add(node.getId());
		}
		return results;
	}

	@Override
	protected void onPause() {
		super.onPause();
		Wearable.DataApi.removeListener(mGoogleApiClient, this);
		Wearable.MessageApi.removeListener(mGoogleApiClient, this);
		mGoogleApiClient.disconnect();

		sensorManager.unregisterListener(this);

	}

	@Override
	public void onConnected(Bundle connectionHint) {
		LOGD(TAG, "onConnected(): Successfully connected to Google API client");
		Wearable.DataApi.addListener(mGoogleApiClient, this);
		Wearable.MessageApi.addListener(mGoogleApiClient, this);

		mIntroText.setText("Taking your pulse...");
		hrmSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
		sensorManager.registerListener(this, hrmSensor,
				SensorManager.SENSOR_DELAY_NORMAL);
		timeElapsedReference = System.currentTimeMillis();
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		Log.d("SENSOR ACCURACY", "" + event.accuracy);
		float pulse = event.values[0];
		Log.d("SENSOR VALUE", "" + pulse);

		if (event.accuracy == SensorManager.SENSOR_STATUS_NO_CONTACT) {
			mIntroText.setText("No contact.");
		} else if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) {
			mIntroText.setText("Unreliable heartrate measurment.");
		} else {
			mIntroText.setText("Pulse: " + pulse);
			if (System.currentTimeMillis() - timeElapsedReference > 1000L) {
				new PublishHrmTask().execute(new Float(pulse).toString());
				timeElapsedReference = System.currentTimeMillis();
			}
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO?
	}

	@Override
	public void onConnectionSuspended(int cause) {
		LOGD(TAG,
				"onConnectionSuspended(): Connection to Google API client was suspended");
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		Log.e(TAG, "onConnectionFailed(): Failed to connect, with result: "
				+ result);
	}

	@Override
	public void onDataChanged(DataEventBuffer dataEvents) {
		LOGD(TAG, "onDataChanged(): " + dataEvents);
	}

	@Override
	public void onMessageReceived(MessageEvent event) {
		LOGD(TAG, "onMessageReceived: " + event);
	}

}
