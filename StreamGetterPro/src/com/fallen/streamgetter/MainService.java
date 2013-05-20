package com.fallen.streamgetter;

import java.io.IOException;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.SoundPool;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import com.fallen.streamgetterfree.R;

public class MainService extends android.app.Service implements
		CallBackToServer, CallBackBroadcast {
	private final IBinder mBinder = new LocalBinder();
	boolean crying = false;
	AsyncLoad asyncLoad;
	SoundPool sp;
	private MediaPlayer mpMainServ;
	String nextFile;
	String urlBySelected;
	String nextUrlBySelected;
	int nextCode;
	private Handler handler;
	private FromBroadcastBroadcast fbb;
	private int reconnectMax = 10;
	private int reconnectTimeOut = 5;
	private int reconnectTimes = 0;
	public AsyncLoadProgress aslp;
	private Runnable runnable;
	private NotificationManager notificationManager;
	private boolean telephoneActionHappened = false;
	protected StatusObj lastStatusObject;
	//private boolean needToPlayLastFileFromEndAfterCall = false;
	private MediaPlayer secondMp;
	private boolean mpPrepared = false;
	private int secondMpStartPosition = 0;
	private boolean stopIsCalled;
	private boolean needToStartAnotherAsync = false;
	public boolean recording = false;
	public boolean playing = false;
	private boolean isTheSameUrl;
	 WifiLock wifiLock;
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return mBinder;
		
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		fbb = new FromBroadcastBroadcast(this);
		IntentFilter filter = new IntentFilter();
		filter.addAction("CallActionHappened");
		filter.setPriority(100);
		registerReceiver(fbb, filter);
		////System.out.println ("onCreateMainServiceSG");
		super.onCreate();
	}

	public class LocalBinder extends Binder {
		MainService getService() {
			return MainService.this;
		}
	}

	public void go(String urlBySelectedFromActivity) {
		////System.out.println("Service go method " + urlBySelected);
		reconnectTimes = 0;
		
		if (urlBySelectedFromActivity.equals(urlBySelected))
			isTheSameUrl = true;
		else
			isTheSameUrl = false;
		removeReconnect();
		urlBySelected = urlBySelectedFromActivity;
		playing = Utils.isPlayBackOn(this);
		recording = Utils.isSaveAllOn(this);

		if (!playing)
		{
			if (mpMainServ!=null && mpMainServ.isPlaying())
				mpMainServ.pause();
			releaseSecondMP();
			if (!recording)
			{
				stopIsCalled = true;
				releaseAsyncLoad();
				removeReconnect();
				return;
			}
		}
		
		startNewAsync();
		
		//Utils.deleteTemp();
		//releaseMP();
		
		
	}

	private void removeReconnect() {
		// TODO Auto-generated method stub
		if (handler != null && runnable !=null)
		{
			handler.removeCallbacks(runnable);
			if (aslp!=null)
			{
				////System.out.println("SEND TO GUI from remove recconect");
				StatusObj stObj = new StatusObj();
				stObj.setStatusMessage(this.getResources().getString(R.string.statusStop));
				aslp.onProgressUpdate(stObj);
				//aslp.onStatusChange(this.getResources().getString(R.string.reconnectFail));
			}
			
		}
	}

	private void startNewAsync() {
		// TODO Auto-generated method stub
		////System.out.println("starting new Async");
		 WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
	      if (wifiManager != null) {
	    	  wifiLock  = wifiManager.createWifiLock("0 Backup wifi lock");
	        wifiLock.acquire();
	      }
		stopIsCalled = false;
		if (asyncLoad == null)
		{
			asyncLoad = new AsyncLoad(urlBySelected, playing, recording,getResources(), Utils.getMax(this));
			asyncLoad.cbts = this;
			asyncLoad.packageName = getPackageName();
			asyncLoad.mContext = getApplicationContext();
			asyncLoad.mContentResolver = getContentResolver();
			asyncLoad.setAlp(aslp);
			asyncLoad.currentStation = Utils.getPrevStation(this);
			asyncLoad.cutTracks = Utils.isCutOn(this);
			asyncLoad.execute(true);
			////System.out.println ("start new async");
			//showpos();
			
		}

		else
		{
			////System.out.println (code +" " + asyncLoad.code);
			if (isTheSameUrl)
			{	
					asyncLoad.setAlp(aslp);
					asyncLoad.playing = playing;
					asyncLoad.recording = recording;
					asyncLoad.newActionRecieved = true;
					////System.out.println ("send new code to async, url the same, recording = "+ recording + " playing = " + playing);
			}
			else
			{
				releaseMP();
				releaseSecondMP();
				releaseAsyncLoad();
				needToStartAnotherAsync = true;
				//System.out.println ("cancel old, start new");
				
			}
			
		}
	}

	private void releaseAsyncLoad() {
		// TODO Auto-generated method stub
		
		if (asyncLoad != null)
		{
			//System.out.println("releaseAsyncLoad");
			asyncLoad.canceled = true;
			wifiLock.release();
			asyncLoad = null;
		}
		else
		{
			turnOffToggles();
			//System.out.println("cant cancel, asyncLoad is null");
		}
	}

	private void releaseMP() {
		// TODO Auto-generated method stub
		//System.out.println("release MP");
		// nextFile = null;
		if (mpMainServ != null) {
			try {
				mpMainServ.stop();
			} catch (Exception e) {
				// TODO: handle exception
			}
			try {
				mpMainServ.reset();
			} catch (Exception e) {
				// TODO: handle exception
			}

			mpMainServ = null;
		}
	}
	private void releaseSecondMP() {
		// TODO Auto-generated method stub
		//System.out.println("release secMP");
		// nextFile = null;
		if (secondMp != null) {
			secondMp.stop();
			secondMp.reset();
			//mpRec.release();
			secondMp = null;
		}
	}
	




	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return Service.START_NOT_STICKY;
	}

	@Override
	public void onLowMemory() {
		////System.out.println("low memory");
		// TODO Auto-generated method stub
		super.onLowMemory();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		//System.out.println("destroyed");
		//cancelNotification();
		turnOffToggles();
		unregisterReceiver(fbb);
		super.onDestroy();
	}

	public void stopMe() {
		// TODO Auto-generated method stub
		/*stopIsCalled = true;
		if (mpMainServ !=null && mpMainServ.isPlaying())
			mpMainServ.pause();
		releaseMP();
		releaseSecondMP();
		removeReconnect();
		releaseAsyncLoad();*/
	}

	@Override
	public void onAnyFail(StatusObj lastObj) {
		// TODO Auto-generated method stub
		lastStatusObject = lastObj;
		//stopIsCalled = true;
		//System.out.println("on Any Fail called");
		//cancelNotification();
		//releaseAsyncLoad();
		asyncLoad=null;
		if (needToStartAnotherAsync)
		{
			needToStartAnotherAsync = false;
			//System.out.println ("startnew async from OnAnyFail");
			startNewAsync();
			return;
		}
		
		
		releaseMP();
		releaseSecondMP();
		if (stopIsCalled)
		{
			turnOffToggles();
			return;
		}
		reconnectTimes++;
		if (reconnectTimes< reconnectMax)
			startReconnect();
		else
			if (aslp!=null)
			{
				StatusObj stObj = new StatusObj();
				stObj.setStatusMessage(this.getResources().getString(R.string.reconnectFail));
				lastStatusObject = stObj;
				aslp.onProgressUpdate(stObj);
				turnOffToggles();
				//aslp.onStatusChange(this.getResources().getString(R.string.reconnectFail));
			}
			
	}

	private void turnOffToggles() {
		// TODO Auto-generated method stub
		Utils.setSaveCurrent(this, false);
		Utils.setSaveAll(this, false);
		Utils.setPlayBack(this, false);
	if (aslp !=null)
		aslp.onChangeToggles();
	}

	private void startReconnect() {
		// TODO Auto-generated method stub
		
		if (aslp!=null)
		{
			//StatusObj stObj = new StatusObj();
			//stObj.setStatusMessage(this.getResources().getString(R.string.reconnect) +" "+ reconnectTimes);
			aslp.onReconnectStart(reconnectTimes);
		}
			//aslp.onStatusChange(this.getResources().getString(R.string.reconnect) +" "+ reconnectTimes);
		
		handler = new Handler();
		handler.postDelayed(runnable = new Runnable  () {
		  @Override
		  public void run() {
			
		    startNewAsync();
		  }
		}, reconnectTimeOut*1000);
	}
	
	@Override
	public void onCanceled(StatusObj lastObj) {
		// TODO Auto-generated method stub
		stopIsCalled = true;
		lastStatusObject = lastObj;
		asyncLoad = null;
		releaseMP();
		releaseSecondMP();
		//cancelNotification();
		if (needToStartAnotherAsync)
		{
			needToStartAnotherAsync = false;
			//System.out.println ("startnew async from OnCanceled");
			startNewAsync();
		}
		else 
		{
			turnOffToggles();
		}
			
	}
	

	private void cancelNotification() {
		// TODO Auto-generated method stub
		if (notificationManager!=null)
		{
		notificationManager.cancel(100500);
		notificationManager.cancel(100501);
		}
		
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		if (asyncLoad == null)
			stopSelf();
		return super.onUnbind(intent);
	}

	@Override
	public void onFilenameChange(String filePath) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPlayAvailable() {
		// TODO Auto-generated method stub
		//System.out.println("available ");
		//creatingMP();
		
	}

	
	private void showpos() {
		// TODO Auto-generated method stub
		//System.out.println("showPosMethod");
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (secondMp!=null)
					//System.out.println(secondMp.getCurrentPosition() + " dur " + secondMp.getDuration());
				showpos();
			}
		}, 1000);
		
		
		
	}

	@Override
	public void setNextFile(String nextFile) {
		// TODO Auto-generated method stub
		//this.nextFile = nextFile;
		////System.out.println("Set next file  " + nextFile);
	}

	public AsyncLoad getAsyncObject() {
		return asyncLoad;
	}

	@Override
	public void onIncomingCall() {
		// TODO Auto-generated method stub
		if (asyncLoad !=null)
		{
			mpMainServ.pause();
			asyncLoad.playing = false;
			asyncLoad.newActionRecieved = true;
			Utils.setPlayBack(this, false);
			if (aslp!=null)
				aslp.onChangeToggles();
			
		}
		
			
		////System.out.println("incoming");
		
	}

	@Override
	public void onHangOff() {
		// TODO Auto-generated method stub
		////System.out.println("hangoff");
		if (telephoneActionHappened )
		{
			// send play from now
		}
	}

	@Override
	public void onAnyRecording() {
		// TODO Auto-generated method stub
		String text = "Recording";
		CharSequence contentText = "Loading in progress";
		//showNotification (text, contentText, 100500);
		////System.out.println ("onAnyRecord called");

	}

	private void showNotification(String text, CharSequence contentText, int notificationID) {
		// TODO Auto-generated method stub
		String notifyService = Context.NOTIFICATION_SERVICE;
		CharSequence contentTitle = "StreamGetter";
		notificationManager = (NotificationManager) getSystemService(notifyService);
		int icon = R.drawable.explogo3;
		//notification.setLatestEventInfo(this, contentTitle, contentText, contentIntent);
		Context context = getApplicationContext();
		Intent notificationIntent = new Intent(this, GetRadioActivity.class);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		Notification notification = new Notification(icon,null,0);
		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		//Notification notification = nb.getNotification();
		notificationManager.notify(notificationID, notification);
	}

	@Override
	public void onPreviewedSuccess() {
		// TODO Auto-generated method stub
		////System.out.println("PreView Success");
		//asyncLoad = null;
	}

	public void activityBindOrStopNotify(AsyncLoadProgress aslp) {
		// TODO Auto-generated method stub
		////System.out.println("Service Notifyed About Interface " + aslp);
		this.aslp = aslp;
		if (asyncLoad!=null)
			asyncLoad.alp  =aslp;
	}

	@Override
	public void onConnected() {
		// TODO Auto-generated method stub
		reconnectTimes = 0;
	}

	@Override
	public void socketCreated() {
		// TODO Auto-generated method stub
		if (stopIsCalled)
			return;
		mpPrepared = false;
		mpMainServ = new MediaPlayer();
		mpMainServ.setOnErrorListener(new OnErrorListener() {
			
			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				// TODO Auto-generated method stub
				//System.out.println ("mpMainServ error");
				return false;
			}
		});
		try {
			//setListeners();
			secondMpStartPosition = 0;
			//System.out.println ("creating mpRec");
			mpMainServ.setDataSource("http://127.0.0.1:8889");
			//mpRec.setDataSource("http://10.0.2.15:8889");
			mpMainServ.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mpMainServ.setOnErrorListener(new OnErrorListener() {
				
				@Override
				public boolean onError(MediaPlayer mp, int what, int extra) {
					// TODO Auto-generated method stub
					return false;
				}
			});
			mpMainServ.setOnInfoListener(new OnInfoListener() {
				
				@Override
				public boolean onInfo(MediaPlayer mpsdvafgv, int what, int extra) {
					// TODO Auto-generated method stub
					if (what ==MediaPlayer.MEDIA_INFO_BUFFERING_START)
					{
						if (aslp !=null && !stopIsCalled)
						{
							//System.out.println ("aslp = " + aslp.toString());
							aslp.onBufferingStart();
						}
						//System.out.println ("buffering + isMPplaying = " +mpMainServ.isPlaying());
					}
					else if (what ==MediaPlayer.MEDIA_INFO_BUFFERING_END)
					{
						if (aslp !=null && !stopIsCalled)
						{
							aslp.onBufferingEnd();
						}
						
						//System.out.println ("buffered + isMPplaying = " +mpMainServ.isPlaying());
						//mpRec.seekTo(0);
						//mpRec.start();
						//mpRec.prepareAsync();
					}
					
					return false;
				}
			});
			mpMainServ.setOnBufferingUpdateListener(new OnBufferingUpdateListener() {
				
				@Override
				public void onBufferingUpdate(MediaPlayer mpdd, int percent) {
					// TODO Auto-generated method stub
				//	//System.out.println (String.valueOf(percent));
					//if (!mpRec.isPlaying())
						//mpRec.start();
				}
			});
			mpMainServ.setOnCompletionListener(new OnCompletionListener() {
				
				@Override
				public void onCompletion(MediaPlayer mpdd) {
					// TODO Auto-generated method stub
					//System.out.println ("complete");
				}
			});
			mpMainServ.setOnPreparedListener(new OnPreparedListener() {
				
				@Override
				public void onPrepared(MediaPlayer prepmp) {
					// TODO Auto-generated method stub
					if (stopIsCalled)
					{
						releaseMP();
						return;
					}
					if (mpMainServ == null)
						return;
					mpPrepared= true;
					if (asyncLoad!=null)
						asyncLoad.mpBuffered();
					//System.out.println (" mpMainServ prepared + duration " + mpMainServ.getDuration());
					if (secondMp != null && secondMp.isPlaying())
					{ 
						//System.out.println ("mpMainServ seek to " + secondMp.getCurrentPosition());
						mpMainServ.seekTo(secondMp.getCurrentPosition());
						releaseSecondMP();
					}
					else 
					{
						////System.out.println ("mpRec start with mpstartpos = " + mpStartPosition);
						//mpRec.seekTo(0);
					}
					mpMainServ.start();	
				/*	if (aslp !=null && !stopIsCalled)
					{
						aslp.onBufferingEnd();
					}*/
					secondMpStartPosition = 0;
				//	
					
				}
			});
			//System.out.println ("preparing mpRec");
			
			if (aslp !=null && !stopIsCalled)
				aslp.onBufferingStart();
			mpMainServ.prepareAsync();
			
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

	@Override
	public void playTemp(String uselessfilePath) {
		// TODO Auto-generated method stub
		 final String filePath = Parameters.TEMP + "temp.mp3";
		//System.out.println ("playTemp");
		secondMp = new MediaPlayer();
		secondMp.setOnErrorListener(new OnErrorListener() {
			
			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				// TODO Auto-generated method stub
				//System.out.println ("secondMp error");
				return false;
			}
		});
		try {
			//final FileInputStream fileInputStream = new FileInputStream(Parameters.TEMP + "temp.mp3");
			//FileInputStream fis = new FileInputStream(new File(Parameters.TEMP + "temp.mp3"));
			secondMp.setDataSource(filePath);
			//System.out.println (filePath);
			secondMp.setOnCompletionListener(new OnCompletionListener() {
			
							@Override
				public void onCompletion(MediaPlayer ssmp) {
					// TODO Auto-generated method stub
					//System.out.println ("secondMp complete");
					if (secondMp == null)
						return;
					secondMpStartPosition = secondMp.getCurrentPosition();
					playTemp(filePath);
					/*if (!mpPrepared && asyncLoad!=null)
					{
						asyncLoad.isBuffered = false;
						mpStartPosition = secondMp.getCurrentPosition();
						if (aslp !=null && !stopIsCalled)
							aslp.onBufferingStart();
					}*/
	

				}
			});
			//System.out.println ("secondMP preparing");
			secondMp.prepareAsync();
			if (aslp !=null && !stopIsCalled)
				aslp.onBufferingStart();
			
			secondMp.setOnPreparedListener(new OnPreparedListener() {
				@Override
				public void onPrepared(MediaPlayer ssmp) {
					// TODO Auto-generated method stub
					//showpos();
					//System.out.println ("secondMP preparED");
					
					if (secondMp == null)
						return;
					//System.out.println ("secondMpStartPosition = " +secondMpStartPosition +" duration"+ secondMp.getDuration());
					if (secondMp.getDuration() - secondMpStartPosition < 3000)
					{
						//System.out.println ("getting duration, if low - return");
						asyncLoad.isBuffered = false;
						return;
					}
					secondMp.seekTo(secondMpStartPosition);
					secondMp.start();
					
				}
			});
			
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

	@Override
	public void onSocketClosed() {
		// TODO Auto-generated method stub
		/*releaseMP();
		releaseSecondMP();
		if (needToStartAnotherAsync)
		{
			needToStartAnotherAsync = false;
			asyncLoad = null;
			startNewAsync(urlBySelected);
		}*/
	}

	public boolean getNextCommandAlreadyRecieved() {
		// TODO Auto-generated method stub
		return needToStartAnotherAsync;
	}

	@Override
	public void onFileRecorded(String title) {
		// TODO Auto-generated method stub
		Toast.makeText(this, title +" " + this.getResources().getString(R.string.trackRecorded), Toast.LENGTH_SHORT).show();
		if (aslp!=null)
			aslp.onChangeToggles();
	}
	public void seekForward()
	{
		if (mpPrepared && mpMainServ !=null)
		{
			mpMainServ.seekTo(mpMainServ.getCurrentPosition() + 5000);
		
		}
	}
	public void seekBackward ()
	{
		mpMainServ.seekTo(mpMainServ.getCurrentPosition() - 5000);
	}

	@Override
	public void onLimitExceed() {
		// TODO Auto-generated method stub
		Utils.setSaveAll(this, false);
		if (aslp!=null)
			aslp.onChangeToggles();
	}
	
}
