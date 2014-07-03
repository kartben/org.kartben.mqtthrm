package org.kartben.mqtthrm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Intent serviceIntent = new Intent(this, DataLayerListenerService.class);
		startService(serviceIntent);

		Log.d("DataLayerListenerServic", "MANUAL START");

	}
}
