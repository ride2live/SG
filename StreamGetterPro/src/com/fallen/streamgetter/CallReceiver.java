package com.fallen.streamgetter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import com.fallen.streamgetterfree.R;


public class CallReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		//System.out.println("Catched");
		if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_IDLE))
		{
			//hang off  - return volume
			//System.out.println ("Idle");
			Intent intentTo = new Intent();
			intentTo.putExtra("event", "idle");
			intentTo.setAction("CallActionHappened");
			context.sendBroadcast(intentTo);
		}
		else if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_OFFHOOK))
		{
			Intent intentTo = new Intent();
			intentTo.putExtra("event", "offhook");
			intentTo.setAction("CallActionHappened");
			context.sendBroadcast(intentTo);
			//hang on - mute
			//System.out.println ("Offhook");
		}
		else if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_RINGING))
		{
			//System.out.println ("Ringing");
		}
	}
	
}
