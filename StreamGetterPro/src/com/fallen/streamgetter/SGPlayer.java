package com.fallen.streamgetter;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.fallen.streamgetter.PlayerService.LocalPlayerBinder;
import com.fallen.streamgetterfree.R;

public class SGPlayer extends Activity implements CallBackListView, CallBackFilesCopy, CallBackPlayer{
	ListView fileListView;
	TextView noFiles;
	LinearLayout playerLayout;
	ImageView pointer;
	float xDown = 0;
	float xUp = 0;
	float yDown = 0;
	float yUp = 0;
	float xDownL = 0;
	float xUpL = 0;
	String saveTo;
	boolean canPlay = true;
	PlayerService ps;
	private File[] filesToAccept;
	PlayerService playerService;
	SeekBar mediaSeekBar;
	TextView titleText;
	TextView currentTimeText;
	TextView overalTimeText;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sgplayer);
		titleText = (TextView)findViewById(R.id.titleText);
		currentTimeText = (TextView)findViewById(R.id.currentTimeText);
		overalTimeText = (TextView)findViewById(R.id.overalTimeText);
		mediaSeekBar = (SeekBar)findViewById(R.id.mediaSeek);
		pointer =(ImageView)findViewById(R.id.pointer);
		playerLayout =(LinearLayout)findViewById(R.id.playerLayout);
		fileListView = (ListView)findViewById(R.id.fileListView);
		if (getIntent().getExtras().getBoolean("training"))
			showHelp();
		saveTo =getIntent().getExtras().getString("file");
		noFiles = (TextView) findViewById(R.id.noFilesText);
		mediaSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			private boolean userMovedSeekBar;

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				if (playerService!=null && userMovedSeekBar)
				{
					playerService.seekTo (seekBar.getProgress());
					userMovedSeekBar = false;
				}
				tracking = false;
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				tracking = true;
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				userMovedSeekBar = fromUser;
				currentTimeText.setText(convertMsecsToReadbleFormat(progress));

			}
		});
        updateFileList();
		
	}
	
	private void showHelp() {
		// TODO Auto-generated method stub
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				pointerBack();
			}
		}, 1000);
       /* playerLayout.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					xDownL = event.getX();
					//System.out.println ("downlayout");
					break;
				case MotionEvent.ACTION_UP:
					//System.out.println ("uplayout");
					xUpL = event.getX();
					float moveX = xDownL - xUpL;
					//System.out.println (moveX);
					if (moveX < -50)
					{
						////System.out.println ("slideback");
						finishActivityMethod();
						break;
					}

				default:
					break;
				
				}
				return true;
			}
		});*/
	}
	protected void pointerBack() {
		// TODO Auto-generated method stub
		pointer.setVisibility(View.VISIBLE);
		final Animation a = new TranslateAnimation(-getWindow().getWindowManager().getDefaultDisplay().getWidth(), 0, 0, 0);
	   	a.setDuration(1000);
    	pointer.startAnimation(a);
    	a.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						finishActivityMethod();
					}
				}, 500);
				
				
			}
		});
	}
	
	private void updateFileList() {
		// TODO Auto-generated method stub
		File dir = new File(Parameters.SAVE);
		File [] files = dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
				// TODO Auto-generated method stub
				String filePath = dir+ "/" + filename;
				if (filePath.equals(saveTo))
				{
					//return true;
					return false;
				}
				
				else
				{
					return true;
				}
			}
		});
		if (files == null)
		{
			noFiles.setVisibility(View.VISIBLE);
			return;
		}
		else if (files.length == 0)
		{
			noFiles.setVisibility(View.VISIBLE);
		}
		else
			noFiles.setVisibility(View.GONE);
		
		
		filesToAccept = files;
		Arrays.sort(files);
		constructFileList (files);
		
		BaseAdapter ba = (BaseAdapter)fileListView.getAdapter();
		if (ba == null) 
			return;
		ba.notifyDataSetChanged();
		////System.out.println ("notifyed about need list Update");
	}
	private void constructFileList(
			final File[] files) {
		// TODO Auto-generated method stub
		if (fileListView==null)
			return;
		fileListView.setAdapter(new ListFilesAdapter(this, files, this));
		//fileListView.invalidateViews();
		fileListView.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					xDown = event.getX();
					yDown = event.getY();
				//	//System.out.println ("listDown");
					break;
				case MotionEvent.ACTION_UP:
					xUp = event.getX();
					yUp = event.getY();
					////System.out.println ("ListUp");
					float moveY = yDown - yUp;
					float moveX = xDown - xUp;
					if (Math.abs(moveX) < Math.abs(moveY))
						break;
					if (moveX < -50)
					{
						canPlay = false;
						////System.out.println ("slideback");
						finishActivityMethod();
						
						break;
					}
					else
					{
						canPlay = true;
						//cblv.play(position);
						break;
					}
				default:
					break;
				
				}
				return false;
			}
		});
		fileListView.setOnItemClickListener(new OnItemClickListener() {
			

			private boolean binded;

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				//Intent intent = new Intent(SGPlayer.this, MainService.class);
				//bindService(intent, servCon, Context.BIND_AUTO_CREATE);
				//startService(intent);
				//System.out.println (files [arg2].getAbsolutePath());
				// TODO Auto-generated method stub
				if (!canPlay)
				{
					//System.out.println ("cant play");
					return;
				}
				if (playerService!=null)
				{
					playerService.files = files;
					playerService.play(arg2);
				}
				else
				{	
					//System.out.println ("PLAY SERVICE IS NULL!!!");
				}
				/*MediaPlayer mpRec = new MediaPlayer();
				try {
					mpRec.setDataSource(files [arg2].getAbsolutePath());
					mpRec.prepare();
					mpRec.start();
					
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
				/*Uri uri = (Uri)arg0.getAdapter().getItem(arg2);
		       Intent intent = new Intent();
		       	intent.setAction(Intent.ACTION_VIEW);
				////System.out.println(uri.toString());
				intent.setData(uri);
				intent.setDataAndType(uri, "audio/mp3");
				startActivity(intent);*/
			}
		});
		/*fileListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				updateFileList();
				
			}
		});*/
		
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		connectToPlayService();
		if (playerService !=null)
			sendInterfaceToPlayerService(this);
		super.onResume();
	}
	private void connectToPlayService() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(SGPlayer.this, PlayerService.class);
		bindService(intent, servCon, Context.BIND_AUTO_CREATE);
		startService(intent);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		try {
			unbindService(servCon);
			sendInterfaceToPlayerService (null);
			
		} catch (Exception e) {
			// TODO: handle exception
		}
			
		super.onPause();
	}
	
	protected void finishActivityMethod() {
		// TODO Auto-generated method stub
		finish();
	}
	@Override
	public void onDelete(int curPosition) {
		// TODO Auto-generated method stub
		curPosition = fileListView.getFirstVisiblePosition();
		updateFileList();
		fileListView.setSelection(curPosition);
	}
	
	public void moveToAccepted (View V)
	{
		if (filesToAccept ==null)
			return;
		AsyncMoveFiles amv = new AsyncMoveFiles(this);
		amv.execute(filesToAccept);
		
		
	}
	@Override
	public void onMoveComplete() {
		// TODO Auto-generated method stub
		updateFileList();
		Toast.makeText(this, getResources().getString(R.string.moveComplete), Toast.LENGTH_SHORT).show();
	}
	public void showAcceptFolderHelp (View V)
	{
		Toast.makeText(this, getResources().getString(R.string.moveHelp), Toast.LENGTH_SHORT).show();
	}
	private ServiceConnection servCon = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			//System.out.println ("onServConnected");
			LocalPlayerBinder binder = (LocalPlayerBinder)service;
			playerService = binder.getService();
			sendInterfaceToPlayerService(SGPlayer.this);
			//playerService.cbp = ;
		}
	
	};
	private boolean tracking;
	public void stop (View V)
	{
		if (playerService !=null)
		{
			playerService.stop();
		}
	}

	protected void sendInterfaceToPlayerService(CallBackPlayer cbp) {
		// TODO Auto-generated method stub
		if (playerService!=null)
		playerService.notifyAboutInterface (cbp);
	}

	@Override
	public void onStartPlayNewFile(int overallTime, String title) {
		// TODO Auto-generated method stub
		titleText.setText(title);
		overalTimeText.setText(convertMsecsToReadbleFormat(overallTime));
		mediaSeekBar.setMax(overallTime);
	}

	@Override
	public void onProgressUpdate(int currentTime) {
		// TODO Auto-generated method stub
		if (!tracking)
			mediaSeekBar.setProgress(currentTime);
		
	}
	private String convertMsecsToReadbleFormat (int msecs)
	{
		String convertedString = "";
		int secs = Math.round(msecs/1000);
		int minutes = Math.round(secs/60);
		secs = secs - minutes*60;
		//System.out.println (minutes + " secs" + secs);
		String minutesString = String.valueOf(minutes);
		String secondsString;
		if (secs  <10 )
			secondsString =  "0" +String.valueOf(secs);
		else
			secondsString = String.valueOf(secs);
		
		convertedString =minutesString  + ":" + secondsString;
		
		
		return convertedString;
	}
	public void next(View V)
	{
		if (playerService!=null)
			playerService.next();
	}
	public void prev(View V)
	{
		if (playerService!=null)
			playerService.prev();
	}

}
