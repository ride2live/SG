package com.fallen.streamgetter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import com.fallen.streamgetterfree.R;

public class MediaButtonsReceiver extends BroadcastReceiver {

	private CallBackMediaBroadcast cbmb;

	public MediaButtonsReceiver(CallBackMediaBroadcast cbmb) {
		// TODO Auto-generated constructor stub
		this.cbmb = cbmb;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		//System.out.println ("media pressed");
		KeyEvent key = (KeyEvent) intent
				.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
		if (key.getAction() == KeyEvent.ACTION_UP) {
			int keycode = key.getKeyCode();
			if (keycode == KeyEvent.KEYCODE_MEDIA_NEXT) {
				cbmb.nextPressed();
			} else if (keycode == KeyEvent.KEYCODE_MEDIA_PREVIOUS) {
				cbmb.prevPressed();
			}
		}

	}

}
