package com.fallen.streamgetter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.fallen.streamgetterfree.R;

public class FromBroadcastBroadcast extends BroadcastReceiver {
	CallBackBroadcast cbb;
	public FromBroadcastBroadcast(CallBackBroadcast cbb) {
		// TODO Auto-generated constructor stub
		this.cbb = cbb;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String event = intent.getStringExtra("event");
		//System.out.println ("GOT ANSWER FROM BROADAST");
		//System.out.println (event);
		if (event.equals("offhook"))
		cbb.onIncomingCall ();
		else if (event.equals("idle"))
		cbb.onHangOff();
		
	}

}
