package org.kartben.mqtthrm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootCompletedReceiver extends BroadcastReceiver{
     @Override
     public void onReceive(Context context, Intent intent) {
		Intent serviceIntent = new Intent(context,
				DataLayerListenerService.class);
		Log.d("DataLayerListenerServic", "AUTOSTART");
		context.startService(serviceIntent);
     }
}