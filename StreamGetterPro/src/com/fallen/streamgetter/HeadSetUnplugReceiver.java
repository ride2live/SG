package com.fallen.streamgetter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import com.fallen.streamgetterfree.R;

public class HeadSetUnplugReceiver extends BroadcastReceiver {

	private CallBackUnplug cbu;

	public HeadSetUnplugReceiver(CallBackUnplug cbu) {
		// TODO Auto-generated constructor stub
		this.cbu = cbu;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		//System.out.println ("unplug");
		if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction()))
		cbu.headset_unplug();
	}

}
