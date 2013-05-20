package com.fallen.streamgetter;



import java.io.File;
import java.io.IOException;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import com.fallen.streamgetterfree.R;

public class PlayerService extends Service implements CallBackBroadcast, CallBackMediaBroadcast, CallBackUnplug{
	private final IBinder mBinder = new LocalPlayerBinder();
	MediaPlayer mpRec;
	protected int startIndex = -1;
	protected File[] files;
	
	protected CallBackPlayer cbp;
	private boolean blinking = false;
	private FromBroadcastBroadcast fbb;
	private MediaButtonsReceiver mbr;
	private HeadSetUnplugReceiver hsur;
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		//System.out.println ("PlayerServiceOnBind");
		return mBinder;
	}
	
	public class LocalPlayerBinder extends Binder {
		PlayerService getService() {
			return PlayerService.this;
		}
	}
	
	
	@Override
	public void onIncomingCall() {
		// TODO Auto-generated method stub
		if (mpRec!=null)
			mpRec.stop();
		
	}

	@Override
	public void onHangOff() {
		// TODO Auto-generated method stub
		
	}
	public void stop() {
		// TODO Auto-generated method stub
		if (mpRec!=null)
		{ 
			mpRec.stop();
		}
		
		//stopSelf();
	}
	@Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		if (mpRec !=null && !mpRec.isPlaying())
		{
			stopSelf();
			//System.out.println ("stopself");
		}
		return super.onUnbind(intent);
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		//System.out.println ("PlayerServiceOnDestroy");
		unregisterReceiver(fbb);
		unregisterReceiver(mbr);
		unregisterReceiver(hsur);
		super.onDestroy();
	}
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		//System.out.println ("PlayerServiceOnCreate");
		fbb = new FromBroadcastBroadcast(this);
		IntentFilter filter = new IntentFilter();
		filter.addAction("CallActionHappened");
		filter.setPriority(100);
		registerReceiver(fbb, filter);
		mbr = new MediaButtonsReceiver(this);
		IntentFilter filterMedia = new IntentFilter();
		filterMedia.addAction(Intent.ACTION_MEDIA_BUTTON);
		filterMedia.setPriority(100000000);
		registerReceiver(mbr, filterMedia);
		hsur = new HeadSetUnplugReceiver(this);
		IntentFilter filterHeadset = new IntentFilter();
		filterHeadset.addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
		filterHeadset.setPriority(100000000);
		registerReceiver(hsur, filterHeadset);
		super.onCreate();
	}
	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		//System.out.println ("PlayerServiceOnStart");
		super.onStart(intent, startId);
	}

	public void play(int index) {
		// TODO Auto-generated method stub
		if (files == null)
			return;
		if (index == files.length || index <0)
			index = 0;

		startIndex = index;
		if (mpRec!=null)
		{
		 	mpRec.stop();
		 	mpRec.release();
		}
		
			mpRec  = new MediaPlayer();
			try {
				mpRec.setOnCompletionListener(new OnCompletionListener() {
					
					@Override
					public void onCompletion(MediaPlayer mp) {
						// TODO Auto-generated method stub
						
						startIndex++;
						
						play(startIndex);
					}
				});
				mpRec.setDataSource(files [index].getAbsolutePath());
				mpRec.prepare();
				mpRec.start();
				sendInfoCurrentSong ();
				if (!blinking)
					startBlink();
				
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}

	private void sendInfoCurrentSong() {
		// TODO Auto-generated method stub
		if (cbp!=null && mpRec!=null && files !=null)
		{
			
			cbp.onStartPlayNewFile(mpRec.getDuration(), files [startIndex].getName());
		}
	}

	public void seekTo(int progress) {
		// TODO Auto-generated method stub
		if (mpRec != null)
		mpRec.seekTo(progress);
	}

	public void notifyAboutInterface(CallBackPlayer cbp2) {
		// TODO Auto-generated method stub
		cbp = cbp2;
		sendInfoCurrentSong();
		if (mpRec != null && mpRec.isPlaying() && !blinking)
		startBlink();
		
	}
	private void startBlink() {
		// TODO Auto-generated method stub
		 
		
		  new Handler().postDelayed(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					blink();

				}
			}, 1000);
	}

	protected void blink() {
		// TODO Auto-generated method stub
		if (cbp !=null && mpRec!=null && mpRec.isPlaying())
		{
			blinking = true;
			cbp.onProgressUpdate(mpRec.getCurrentPosition());
			startBlink();
		}
		else
		{
			blinking = false;
		}
		
	}

	public void next() {
		// TODO Auto-generated method stub
		if (mpRec !=null && mpRec.isPlaying())
		{
			startIndex++;
			play(startIndex);
		}
	}
	public void prev() {
		// TODO Auto-generated method stub
		if (mpRec !=null && mpRec.isPlaying())
		{
			startIndex--;
			play(startIndex);
		}
	}

	@Override
	public void nextPressed() {
		// TODO Auto-generated method stub
		next();
	}

	@Override
	public void prevPressed() {
		// TODO Auto-generated method stub
		prev();
	}

	@Override
	public void headset_unplug() {
		// TODO Auto-generated method stub
		if (mpRec != null && mpRec.isPlaying())
			mpRec.stop();
	}
	
	
	

}
