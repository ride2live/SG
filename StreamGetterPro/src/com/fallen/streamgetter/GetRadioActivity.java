package com.fallen.streamgetter;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Locale;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.fallen.streamgetter.MainService.LocalBinder;
import com.fallen.streamgetterfree.R;


public class GetRadioActivity extends Activity implements AsyncLoadProgress, LoadImageResult,  CallBackStationListView, CallBAckUpdateStations{
	//boolean binded;
	protected static final int RIGHT = 1;
	protected static final int LEFT = 2;
	protected static final int UP = 3;
	protected static final int DOWN =4;
	private SQLiteDatabase db;
	private TextView currentRadio;
	private ArrayList<String> genres ;
	private ArrayList<String> stations ;
	float width;
	AlertDialog newStreamBuilder;
	private MainService mainService; 
	private TextView curStatus;
	private TextView curTitle;
	private TextView bitRate;
	private TextView saveTo;
	private TextView stationSite, kbText;
	private EditText newUrlStation, newNameStation;
	private ImageView trackImage;
	private ImageButton stop;
	//private String currentImageArtist;
	private Gallery gallery;
	private String selectedGenre;
	private String selectedRadio;
	private boolean binded = false;
	//LinearLayout switcher;
	LinearLayout optionsLayout;
	SeekBar volumeBar;
	private Handler handler;
	private Runnable runnable;
	//SlidingDrawer slider;
	AlertDialog dialog; // dialog for stations with ListView
	FromBroadcastBroadcast fbb;
	Resources res;
	LinearLayout topBar;
	LinearLayout statusLayout;
	ProgressBar loadingBar;
	float xDown = 0;
	float yDown = 0;
	float xUp = 0;
	float yUp = 0;
	AlertDialog piratDialog;
	private RelativeLayout bigLayout;
	private int height;
    private AudioManager audioManager;
    ImageButton favButton;
    ImageView pointer;
	//private TextView freeSpace;
	LinearLayout functionLayout;
	//private TextView recStatus;
	private int recCount;
	private CheckBox cutByTrackBox;
	private CheckBox loadImagesBox;
	private CheckBox showTopButtons;;
	private ImageButton saveCurrentSong, playBack, saveAll;
	private LinearLayout helpButtonsLayout, statusField;
	private RelativeLayout progressLayout;
	private boolean blinking = false;
	private boolean checkingSize = false;
	//ImageView up_down;
	/** Called when the activity is first created. */
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_in_line);
       if (!((this.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE))
       {
    	   setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
       }
       try {
			AccountManager accountManager = AccountManager.get(this); 
			Account[] accounts = accountManager.getAccountsByType("com.google");
			AsyncBlackListAction abla = new AsyncBlackListAction(accounts);
			//abla.execute("");
			//Toast.makeText(this, accounts[0].name, Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			// TODO: handle exception
		}
		
      
       //System.out.println("Creating MP from file " +filePath);
				//System.out.println("MediaPlayer null?? " +mpRec);
	 // Create the adView
       // up_down = (ImageView) findViewById(R.id.up_down);
        res = getResources();

        loadingBar = (ProgressBar)findViewById(R.id.loadingBar);
        progressLayout=(RelativeLayout)findViewById(R.id.progressLayout);
        kbText = (TextView)findViewById(R.id.kbtext);
        helpButtonsLayout = (LinearLayout)findViewById(R.id.helpButtonsLayout);
        saveAll = (ImageButton)findViewById(R.id.saveAllOn);
        playBack = (ImageButton)findViewById(R.id.playBackOn);
        saveCurrentSong = (ImageButton)findViewById(R.id.saveCurSong);
        loadImagesBox  = (CheckBox)findViewById(R.id.loadImages);
        cutByTrackBox = (CheckBox)findViewById(R.id.cutByTracks);
        showTopButtons= (CheckBox)findViewById(R.id.showHelpButtonss);
        audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        //adView = (AdView) findViewById(R.id.adView);
        //slider = (SlidingDrawer)findViewById(R.id.slidingDrawer1);
        favButton = (ImageButton)findViewById(R.id.favButton);
        topBar = (LinearLayout) findViewById(R.id.topBarLayout);
        statusField = (LinearLayout)findViewById(R.id.statusField);
        optionsLayout =(LinearLayout) findViewById(R.id.optionsLayout);
        volumeBar = (SeekBar)findViewById(R.id.volumeBar);
        statusLayout= (LinearLayout) findViewById(R.id.statusLayout);
        functionLayout = (LinearLayout) findViewById(R.id.functionLayout);
        bigLayout = (RelativeLayout) findViewById(R.id.bigLayout);
       // switcher = (LinearLayout) findViewById(R.id.switcher);
        gallery = (Gallery)findViewById(R.id.gallery);
        stationSite = (TextView)findViewById(R.id.stationSite);
        bitRate = (TextView)findViewById(R.id.bitRate);
        saveTo = (TextView)findViewById(R.id.saveTo);
        trackImage = (ImageView)findViewById(R.id.albumImage);
        currentRadio = (TextView)findViewById(R.id.curRadio);
        curTitle = (TextView)findViewById(R.id.curTitle);
        curStatus = (TextView)findViewById(R.id.curStatus);
        //recStatus = (TextView)findViewById(R.id.recStatus);
        //freeSpace = (TextView)findViewById(R.id.freeSpace);
        //
        constructPiratedialog();
        setActionListeners ();
        savedToggleChanged ();
        playBackToggleChanged ();
        saveAllToggleChanged ();
        recCount = -1;
        StationBase sb = new StationBase(this);
        db = sb.getWritableDatabase();
        genres = new ArrayList<String>();
        setOptionMosions();
        setLongClickListeners ();
        //setSliderOpenListener();
        cutByTrackBox.setChecked(Utils.isCutOn(this));
        cutByTrackBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				Utils.setCutByTracks(getApplicationContext(), isChecked);
			}
		});
        gallery.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				selectedGenre = genres.get(arg2);
				Utils.setCurGenre(arg2, getApplicationContext());
				openRadioList(selectedGenre);
			}
		});
        showTopButtons.setChecked(Utils.isShowHelpLayout(this));
        showTopButtons.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				
				Utils.setShowHelpLayout(getApplicationContext(), isChecked);
				setHelpButtonsVisibility();

			}
		});
        loadImagesBox.setChecked(Utils.isImagesOn(this));
        loadImagesBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				Utils.setImagesOn (getApplicationContext(), isChecked);
			}
		});

        
        //constructAndShowGallery (curGenre.getText().toString());
        volumeBar.setMax( audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        volumeBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				//System.out.println (seekBar.getProgress());
				audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, seekBar.getProgress(), 0);
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				if (handler!=null && runnable!=null)
					handler.removeCallbacks(runnable);
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				
			}
		});
        if (Utils.isFirstLaunch(this))
        {
        	String locale = detectLocale();
        	createDBFromAssets(locale);
     	   	showHelpGUI (true);
        }
        else
        {
            updateStationDB();
        }

       
       
    }
    
    
	private void setActionListeners() {
		
		 // TODO Auto-generated method stub
		playBack.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				boolean isPlayBackOn =  Utils.isPlayBackOn(getApplicationContext());
				// TODO Auto-generated method stub
				if (isPlayBackOn)
				{
					Utils.setPlayBack (getApplicationContext(), false);
					if (Utils.isSaveAllOn(getApplicationContext()))
						sendActionToService();
					else
						stop(null);
				}
				else
				{
					Utils.setPlayBack(getApplicationContext(), true);
					if (Utils.isSaveAllOn(getApplicationContext()))
						sendActionToService();
					else
						sendActionToService();
				}
				playBackToggleChanged();
			}
			
		});
		saveCurrentSong.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				boolean isCurrentSaveOn =  Utils.isSaveCurrentOn(getApplicationContext());
				if (Utils.isSaveAllOn(getApplicationContext()) && !isCurrentSaveOn)
				{
					alreadyRecToast();
					return;
				}
				if (!Utils.isSaveAllOn(getApplicationContext()) && !Utils.isPlayBackOn(getApplicationContext()) && !isCurrentSaveOn)
				{
					Toast.makeText(getApplicationContext(), res.getString(R.string.saveCurrentUnavailable), Toast.LENGTH_SHORT).show();
					return;
				}
				
				// TODO Auto-generated method stub
				if (isCurrentSaveOn)
				{
					Toast.makeText(getApplicationContext(), R.string.saveCurrentCancel, Toast.LENGTH_SHORT).show();
					Utils.setSaveCurrent(getApplicationContext(), false);
					
					if (mainService !=null && mainService.asyncLoad != null)
						mainService.asyncLoad.setCurrentSave(false);
						
				}
				else
				{
					Toast.makeText(getApplicationContext(), curTitle.getText() + " "+res.getString(R.string.saveCurrent) , Toast.LENGTH_SHORT).show();
					Utils.setSaveCurrent(getApplicationContext(), true);
					if (mainService !=null && mainService.asyncLoad != null)
						mainService.asyncLoad.setCurrentSave(true);
				}
				savedToggleChanged ();
			}
			
		});
		saveAll.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (Utils.isSaveCurrentOn(getApplicationContext()))
				{
						alreadyRecToast();
						return;
				}				
				boolean isSaveAllOn =  Utils.isSaveAllOn(getApplicationContext());
				// TODO Auto-generated method stub
				if (isSaveAllOn)
				{
					Utils.setSaveAll(getApplicationContext(), false);
					if (Utils.isPlayBackOn(getApplicationContext()))
						sendActionToService();
					else
						stop(null);
					
				}
				else
				{
					Utils.setSaveAll(getApplicationContext(), true);
					if (Utils.isPlayBackOn(getApplicationContext()))
						sendActionToService();
					else
						sendActionToService();
				}
				saveAllToggleChanged ();
			}
			
		});
		
	}
	private void saveAllToggleChanged() {
		// TODO Auto-generated method stub
		boolean isSavedAllOn =  Utils.isSaveAllOn(this);
		if (isSavedAllOn)
		{
			startBlink();
			saveAll.setBackgroundResource(R.drawable.recon);
			Utils.setSaveCurrent(this, false);
			savedToggleChanged();
		}
		else
			saveAll.setBackgroundResource(R.drawable.recoff);
	}

	private void playBackToggleChanged() {
		// TODO Auto-generated method stub
		boolean isPlayBackOn =  Utils.isPlayBackOn(this);
		if (isPlayBackOn)
			playBack.setBackgroundResource(R.drawable.playonbutton);
		else
			playBack.setBackgroundResource(R.drawable.playoffbutton);
	}


	private void savedToggleChanged ()
	{
		boolean isCurrentSaveOn =  Utils.isSaveCurrentOn(this);
		if (isCurrentSaveOn)
		{
			saveCurrentSong.setBackgroundResource(R.drawable.savecuron);
			//Utils.setSaveAll(this, false);
			//saveAllToggleChanged();
		}
		else
			saveCurrentSong.setBackgroundResource(R.drawable.savecuroff);
	}

	public void connect (View V)
	{
		//sendActionToService(selectedRadio, code);
	}
private void constructPiratedialog() {
		// TODO Auto-generated method stub
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setMessage(res.getString(R.string.noLicense));
    	builder.setPositiveButton(res.getString(R.string.piracyBad), new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse("market://details?id=com.fallen.streamgetter"));
				startActivity(intent);
			}
		});
    	builder.setNegativeButton(res.getString(R.string.piracyOk), null);
    	piratDialog = builder.create();
	}

	private void showHelpGUI(final boolean realyFirst) {
		// TODO Auto-generated method stub
    	pointer = (ImageView)findViewById(R.id.pointer);
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setMessage(res.getString(R.string.helpGUI));
    	builder.setPositiveButton("OK", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				//helpButtons ();
				pointerDown();
			}
		});
    	
    	builder.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				// TODO Auto-generated method stub
				if (realyFirst)
					pointerDown();
					//helpButtons ();
			}
		});
    	if (!realyFirst)
    		builder.setNegativeButton("Cancel", null);
    	AlertDialog ad = builder.create();
    	ad.show();
    	
    }
	public void helpButtons ()
	{
		AlertDialog.Builder builder;

		LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.customdialog,
		                               (ViewGroup) findViewById(R.id.layout_root));
		builder = new AlertDialog.Builder(this);
		builder.setView(layout);
		builder.setPositiveButton("Ok", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				pointerDown();
			}
		});
		builder.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				// TODO Auto-generated method stub
				pointerDown();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
		
		
	}
	
	private void pointerDown() {
		// TODO Auto-generated method stub
		final Animation a = new TranslateAnimation(0, 0, -height, 0);
	   	a.setDuration(1000);
	   	pointer.setVisibility(View.VISIBLE);
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
				showTopBar();
				new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						pointerUp();
					}
				}, 2000);
			}
		});
    	
		
	}
	private void pointerUp() {
		pointer.setVisibility(View.VISIBLE);
		final Animation a = new TranslateAnimation(0, 0, 0, -height);
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
				pointer.setVisibility(View.GONE);
				hideTopBar();
				new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						pointerUpAgain();
					}
				}, 2000);
			}
		});
	}
	private void pointerUpAgain() {
		final Animation a = new TranslateAnimation(0, 0, height , 0);
	   	a.setDuration(1000);
	   	pointer.setVisibility(View.VISIBLE);
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
				showStatusLayoutParams(true);
				new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						pointerDownAgain();
					}


				}, 2000);
			}
		});
	}
	
	private void pointerDownAgain() {
		// TODO Auto-generated method stub
		pointer.setVisibility(View.VISIBLE);
		final Animation a = new TranslateAnimation(0, 0, 0, height);
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
				pointer.setVisibility(View.GONE);
				hideStatusLayout();
				new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						pointerOptions();
					}
				}, 2000);
			}
		});
	}
	
	private void pointerOptions() {
		pointer.setVisibility(View.VISIBLE);
		final Animation a = new TranslateAnimation(-width, 0, 0, 0);
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
				showOptionsLayout();
				new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						pointerOptionsBack();
					}
				}, 2000);
			}
		});
		
	}
	private void pointerOptionsBack() {
		pointer.setVisibility(View.VISIBLE);
		final Animation a = new TranslateAnimation(0, -width, 0, 0);
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
				pointer.setVisibility(View.GONE);
				hideOptionsLayout();
				new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						pointerRecActivity();
					}
				}, 2000);
			}
		});
		
	}
	private void pointerRecActivity() {
		final Animation a = new TranslateAnimation(width, 0, 0, 0);
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
					startPlayerActivity(true);
				
			}
		});
		
	}


    
	@Override
    public void onConfigurationChanged(Configuration newConfig) {
    	// TODO Auto-generated method stub
    	super.onConfigurationChanged(newConfig);
    	setContentView(R.layout.all_in_line);
    }
	private void setOptionMosions() {
		// TODO Auto-generated method stub
    	height = getWindow().getWindowManager().getDefaultDisplay().getHeight();
    	width = getWindow().getWindowManager().getDefaultDisplay().getWidth();
    	gallery.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					yDown = event.getY();
					xDown = event.getX();
					break;
				case MotionEvent.ACTION_UP:
					yUp =event.getY();
					xUp = event.getX();
					//System.out.println (xDown);
					//System.out.println (yDown);
					//System.out.println (xUp);
					//System.out.println (yUp);
					float moveY = yDown - yUp;
					float moveX = xDown - xUp;
					
						if (moveY > 40 && Math.abs(moveX) < Math.abs(moveY))
						{
							//Toast.makeText(getApplicationContext(), "slide up", Toast.LENGTH_SHORT).show();
							//slide(PREVIOUS);
							slide (UP);
						}
					break;
				default:
					
					break;
				}
				
				return false;
			}
		});
    	bigLayout.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					xDown = event.getX();
					yDown = event.getY();
					break;
				case MotionEvent.ACTION_UP:
					xUp = event.getX();
					yUp =event.getY();
					//System.out.println (xDown);
					//System.out.println (yDown);
					//System.out.println (xUp);
					//System.out.println (yUp);
					float moveX = xDown - xUp;
					float moveY = yDown - yUp;
					if (Math.abs(moveX) > Math.abs(moveY)) // left or right slide 
					{
						if (moveX > 50)
						{
							//Toast.makeText(getApplicationContext(), "slide left", Toast.LENGTH_SHORT).show();
							slide (LEFT);
						}
						else if (moveX < -50)
						{
							//Toast.makeText(getApplicationContext(), "slide right", Toast.LENGTH_SHORT).show();
							slide (RIGHT);
							//slide(NEXT);
						}

					}
					else // up or down slide
					{
						if (moveY > 50)
						{
							//Toast.makeText(getApplicationContext(), "slide up", Toast.LENGTH_SHORT).show();
							//slide(PREVIOUS);
							slide (UP);
						}
						else if (moveY < -50)
						{
							//Toast.makeText(getApplicationContext(), "slide down", Toast.LENGTH_SHORT).show();
							slide (DOWN);
						}
					}

					
					
					//yUp = event.getY(); 
					break;
				default:
					
					break;
				}
				
				
				
				return true;
			}
		});
	}


	protected void slide(int motion) {
		// TODO Auto-generated method stub
		switch (motion) {
		case RIGHT:
			if (optionsLayout.getVisibility() == View.GONE)
				showOptionsLayout();
			 
			break;
		case LEFT:
			if (optionsLayout.getVisibility() == View.VISIBLE)
				hideOptionsLayout();
			else
				startPlayerActivity(false);
			break;
		case UP:
			if (topBar.getVisibility() == View.VISIBLE)
				hideTopBar();
			else
				showStatusLayoutParams(true);
			//slider.animateOpen();
			break;
		case DOWN:
			if (topBar.getVisibility() == View.GONE  && statusLayout.getVisibility() == View.GONE)
				showTopBar();
			else if (topBar.getVisibility() == View.GONE && statusLayout.getVisibility() == View.VISIBLE)
				hideStatusLayout();
			
			break;

		default:
			break;
		}
		
	}
	private void hideStatusLayout()
	{
		Animation a = new TranslateAnimation(0, 0, 0, height);
    	a.setDuration(600);
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
				statusLayout.setVisibility(View.GONE);
				favButton.setVisibility(View.GONE);
			}
		});
    	statusLayout.startAnimation(a);
    	favButton.startAnimation(a);
	}

	private void showOptionsLayout() {
		// TODO Auto-generated method stub
		optionsLayout.setVisibility(View.VISIBLE);
		Animation a = new TranslateAnimation(-width, 0, 0, 0);
    	a.setDuration(300);
    	optionsLayout.startAnimation(a);
	}

	private void hideOptionsLayout() {
		// TODO Auto-generated method stub
		Animation a = new TranslateAnimation(0, -width, 0, 0);
    	a.setDuration(300);
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
				optionsLayout.setVisibility(View.GONE);
				
			}
		});
    	optionsLayout.startAnimation(a);
    	
	}

	private void startPlayerActivity(boolean training) {
		// TODO Auto-generated method stub
		//System.out.println ("startact");
		Intent intent =  new Intent (this, SGPlayer.class);
		intent.putExtra("training", training);
		intent.putExtra("file", saveTo.getText());
		startActivityForResult(intent, 1);
		
	}
	private void hideTopBar() {
		// TODO Auto-generated method stub
		Animation a = new TranslateAnimation(0, 0, 0, -height);
    	a.setDuration(600);
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
				topBar.setVisibility(View.GONE);
				
			}
		});
    	topBar.startAnimation(a);
    	
    	
	}

	public void showTopBar()
	{
		topBar.setVisibility(View.VISIBLE);
		Animation a = new TranslateAnimation(0, 0, -height, 0);
    	a.setDuration(600);
    	topBar.startAnimation(a);
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
				gallery.setSelection(Utils.getCurGenre(getApplicationContext()), false);
			}
		});
    	
    	
    	
	}

    
   /* private void setSliderOpenListener() {
		// TODO Auto-generated method stub
        slider.setOnDrawerOpenListener(new OnDrawerOpenListener() {
			
			@Override
			public void onDrawerOpened() {
				// TODO Auto-generated method stub
				updateFileList();
				
			}
		});
        
	}*/
	


	public void openRadioList (final String selectedGenre) 
	{
		constructStationArray (selectedGenre);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setTitle(res.getString(R.string.selectStation));
    	//builder.setIcon(R.drawable.explogo3); 	// icon for title
    	boolean canEdit;
    	if (selectedGenre.equals("Favorites"))
    		canEdit = true;
    	else
    		canEdit=false;
    	builder.setAdapter(new GalleryAdapter(getApplicationContext(), stations,canEdit, this), new DialogInterface.OnClickListener() {		//creating object of List adapter
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				selectedRadio = stations.get(which);
				currentRadio.setText(selectedRadio);
				Utils.setPrevStation(selectedRadio, getApplicationContext());
				Utils.setPlayBack(getApplicationContext(), true);
				Utils.setSaveAll(getApplicationContext(), false);
				Utils.setSaveCurrent(getApplicationContext(), false);
				sendActionToService();
				onChangeToggles();
				//sendActionToService(getUrlBySelected(stations.get(which)), Parameters.PLAY);
			}
		});
    	builder.setNegativeButton(res.getString(R.string.back), null);
    	dialog = builder.create();
    	dialog.show();
    }

	private void constructStationArray(String selectedGenre) {
		// TODO Auto-generated method stub
		stations = new ArrayList<String>();
		Cursor cursorStations = null;
		if (selectedGenre.equals("All genres"))
		{
			//cursorStations = db.rawQuery("SELECT * FROM 'radios' ORDER BY 'name' ASC",null);		
			cursorStations =db.query(false, "radios", null, null, null, null, null, "name", null);
		}
		else if (selectedGenre.equals("Favorites"))
		{
			cursorStations =db.query(false, "favorites", null, null, null, null, null, "name", null);
			//Toast.makeText(this, "fav selected", 1).show();
		}
		else
		{
			cursorStations =db.query(false, "radios", null,"genre = '"+selectedGenre+"'" , null, null, null, "name", null);
			//cursorStations = db.rawQuery("SELECT * FROM 'radios' WHERE genre = '"+selectedGenre+"'", null);
		}
		if (cursorStations!=null) //kostyl
		{
		while (cursorStations.moveToNext())
		{
			stations.add( cursorStations.getString(cursorStations.getColumnIndex("name")));
		}	
		//selectedRadio = stations.get(0);
		//currentRadio.setText(selectedRadio);
		//curGenre.setText(selectedGenre);
		cursorStations.close();
		}
	}
	void noStationCase (){
		String prevSt = "";

			prevSt =getRandomStaton();
			Utils.setPrevStation(prevSt, this);
			currentRadio.setText(prevSt);
			Toast.makeText(this, res.getString(R.string.random)+" " +prevSt, Toast.LENGTH_SHORT).show();
		
	}
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		
		
        super.onStart();
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(GetRadioActivity.this, MainService.class);
		bindService(intent, servCon, Context.BIND_AUTO_CREATE);
		binded = true;
		startService(intent);
		setHelpButtonsVisibility();
		super.onResume();
	}
    private void setHelpButtonsVisibility() {
		// TODO Auto-generated method stub
		if (Utils.isShowHelpLayout(this))
			helpButtonsLayout.setVisibility(View.GONE);
		else
			helpButtonsLayout.setVisibility(View.VISIBLE);
	}

	private String getRandomStaton() {
		// TODO Auto-generated method stub

    	Cursor c =  db.query("radios", new String [] {"name"}, null, null, null, null, null);
    	int count = c.getCount();
    	int randomIndex = (int) (Math.random()*count);
    	c.moveToPosition(randomIndex);
    	String randStation = c.getString(0);
    	c.close();
    	//SALT, getPackageName(), Settings.Secure.ANDROID_ID
    	
    	return randStation;
	}
    
    @Override
    protected void onPause() {
    	// TODO Auto-generated method stub
    	if (mainService!=null)
    		mainService.activityBindOrStopNotify(null);
    	if (binded)
    		unbindService(servCon);
    	stopCheckingFile = true;
    	super.onPause();
    }

	@Override
    protected void onStop() {

    	
    	//Utils.setPrevStation(currentRadio.getText().toString(), this);
    	super.onStop();
    }
    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	db.close();
    	super.onDestroy();
    }
    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection servCon = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			//System.out.println (name.toString() + " Connected");
			mainService = ((LocalBinder)service).getService();
			AsyncLoad al =mainService.getAsyncObject();
			mainService.activityBindOrStopNotify(GetRadioActivity.this);
			StatusObj lastStatObj = mainService.lastStatusObject;
			if (lastStatObj !=null)
				setStatusToGUI (lastStatObj);
			if (al!=null)
			{
				notifyAboutInterface (al);
				notifyServerAboutInterface ();
				//System.out.println ("onServCon Load refresh GUI???");
			}
		//	else if (selectedRadio != null)
			//	sendActionToService(getUrlBySelected(selectedRadio), Parameters.PREVIEW);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mainService = null;
			//System.out.println ( " DisConnected");
		}

    };
	private boolean isDebuggable = false;
	private boolean updating =false;
	private boolean stopCheckingFile = false;
	private String fileToCheck = "";
	protected void notifyAboutInterface(AsyncLoad al) {
		// TODO Auto-generated method stub
		al.setAlp(this);
	}
		protected void setStatusToGUI(StatusObj statObj) {
		// TODO Auto-generated method stub
		
			if (mainService!=null)
				mainService.lastStatusObject = statObj;
			// System.out.println ("debug = " + isDebuggable);
			if (piratDialog!=null && !piratDialog.isShowing())
			{
				if (statObj.notLicensed)
				 {
					showPirateDialog ();
				 }
				 if (isDebuggable)
				 {
					showPirateDialog ();
				 }
			}
			if ( !bitRate.getText().equals(statObj.getBitrate())  && statObj.getBitrate().length()>1 )
			{
				bitRate.setText(statObj.getBitrate());
				showStatusLayoutParams(false);
			}
			String maxFilesCount ="";
			if (statObj.isRecording())
			{
				statusField.setVisibility(View.VISIBLE);
				fileToCheck = statObj.getCurrentFile();
					if (!checkingSize && statObj.getCurrentFile().length() > 1)
					{
						stopCheckingFile=false;
						startCheckFileSize();
					}
					recCount = statObj.getCurRecCount();
					int maxCount = Utils.getMax(this);
						if (maxCount == 0)
							maxFilesCount = res.getString(R.string.noLimitSave); 
						else
							maxFilesCount	 = String.valueOf(maxCount);
				curStatus.setText(res.getString(R.string.recorded) + ": "+String.valueOf(recCount)+ "/" + maxFilesCount);
			}
			else
			{

				
				stopCheckingFile  = true;
				String statusMessgae = statObj.getStatusMessage();
				//System.out.println ("not recording");
				//System.out.println ("statusMessgae " +statusMessgae);
				if (statusMessgae.length()>0)
				{
					
					curStatus.setText(statusMessgae);
					statusField.setVisibility(View.VISIBLE);
				}
				else if (!statObj.isRecording())
				{
					statusField.setVisibility(View.INVISIBLE);
				}
					
				
			}
			if (statObj.getSite() != "")
				stationSite.setText(statObj.getSite());
			
			String saveToString = statObj.getSaveFolder();
			if (saveToString != "")
			{
				saveTo.setText(saveToString);
			}
			String titleFromServ="";
			titleFromServ =  statObj.getTitle();
			//if (titleFromServ.equals(curTitle.getText()))
			//System.out.println(titleFromServ + " fromservice");
			//System.out.println(curTitle.getText() + " inActivity");
	        if (titleFromServ != "" && !(titleFromServ.equals(curTitle.getText().toString())) )
			{
	        	curTitle.setText(titleFromServ);
	        	
				if ((titleFromServ.length()>2) && !(titleFromServ.equals(res.getString(R.string.noTitle))))
				{
					//isDebuggable =  (0!= ( getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE));
					if (Utils.isImagesOn(this))
						loadImage(titleFromServ);
				}
				else
				{
					trackImage.setImageResource(R.drawable.audio);
				}
			}
		
	}


		private void showPirateDialog() {
			// TODO Auto-generated method stub

	    	piratDialog.show();
	    	isDebuggable = false;
		}

		/*protected String checkStatus(String myStatus) {
		// TODO Auto-generated method stub
			String resString  = "";
			if (myStatus.equals(Parameters.STATUS_BUFFERING))
				resString = res.getString(R.string.statusBuf);
			else if (myStatus.equals(Parameters.STATUS_CONNECTING))
				resString = res.getString(R.string.statusConnecting);
			else if (myStatus.equals(Parameters.STATUS_ERROR))
				resString = res.getString(R.string.statusError);
			else if (myStatus.equals(Parameters.STATUS_P_R))
				resString = res.getString(R.string.statusPR);
			else if (myStatus.equals(Parameters.STATUS_PLAYING))
				resString = res.getString(R.string.statusPlay);
			else if (myStatus.equals(Parameters.STATUS_RECORDING))
				resString = res.getString(R.string.statusRec);
			else if (myStatus.equals(Parameters.STATUS_STOPPED))
				resString = res.getString(R.string.statusStop);
			else
				resString = myStatus;
		return resString;
	}*/
		protected void notifyServerAboutInterface() {
		// TODO Auto-generated method stub
			mainService.aslp = this;
		
	}
		protected void sendActionToService() {
		// TODO Auto-generated method stub
			String url = getUrlBySelected(currentRadio.getText().toString());
			//System.out.println ("url = " + url);
			removeAutohide();
			recCount = -1;
			if (url == null)
				return;
		if (mainService !=null)
		{
			if (mainService.getNextCommandAlreadyRecieved())
				return;
			//setInfoToDefault();
			else
				mainService.go(url);										
			
		}
		//else
			//Toast.makeText(this, "No service detected", 1).show();
	}
/*	private void setInfoToDefault() {
			// TODO Auto-generated method stub
		 curStatus.setText("");
		 curTitle.setText("");
		 bitRate.setText("");
		 saveTo.setText("");
		 stationSite.setText("");
		trackImage.setImageResource(R.drawable.audio);
		}*/
	protected String getUrlBySelected(String stName) {											
		// TODO Auto-generated method stub
		
		Cursor cursorUrl = db.rawQuery("SELECT url FROM 'radios' WHERE name = '"+stName+"'", null);
		if (cursorUrl.getCount() < 1)
			return null;
		cursorUrl.moveToFirst();
		String firstUrl = cursorUrl.getString(0);
		cursorUrl.close();
		return firstUrl;
	}
    private void getGenres() { 
		// TODO Auto-generated method stub
    	checkDBOpen();
		Cursor cursorGenres = db.rawQuery("SELECT distinct genre FROM 'radios'", null); 					// Select from database all.. because Im noop in SQL )
		genres = new ArrayList<String>();
		genres.add("All genres");
		while (cursorGenres.moveToNext())
		{
			String genreStream = cursorGenres.getString(cursorGenres.getColumnIndex("genre"));
			if (genreStream!=null && genreStream.length()>2)
			genres.add(genreStream);						// Add genres while moving cursor by rows
		}
		cursorGenres.close();
		
        gallery.setAdapter(new ListGenresAdapter(this, genres));
	}




	private void loadImage (String titleFromServ)
	{
		trackImage.setImageResource(R.drawable.audio);
		//System.out.println (curTitle.getText());
		ImageLoadAsyncTask ilat = new ImageLoadAsyncTask(this);
		ilat.execute(titleFromServ);
		
	}

	public void stop (View V)
	{
		stopCheckingFile = true;

		//stopService(new Intent(this, MainService.class));
		 if (mainService!=null)
		{
			 Utils.setSaveAll(this, false);
			 Utils.setSaveCurrent(this, false);
			 Utils.setPlayBack(this, false);
			 onChangeToggles();
			sendActionToService();
			//mainService.getAsyncObject().cancel(true);
			 //mainService.getAsyncObject().getStatusObject();
			// mainService.stopMe();
			//curStatus.setText(res.getString(R.string.statusStop));
		}
		 else
		 {
			// System.out.println ("MAINSERVICE NULL!!!!!");
		 }

	}
	public void play (View V)
	{
		
			//
			
			sendActionToService ();
		
		//Toast.makeText(getApplicationContext(), "Play " + url, 1).show();
		
	}
	/*private void startAsking() {
		// TODO Auto-generated method stub
		
		startBuyMedia (R.raw.buyedited);
		
		AlertDialog.Builder buyDialog = new AlertDialog.Builder(this);
		buyDialog.setTitle(res.getString(R.string.buyTitle));
		buyDialog.setMessage(res.getString(R.string.buyMessage));
		buyDialog.setPositiveButton("Ok", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				buy(null);

			}
		});
		buyDialog.setNegativeButton("Cancel", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				startBuyMedia (R.raw.buybuyedited);
				Toast.makeText(getApplicationContext(), ":_(", Toast.LENGTH_SHORT).show();
				
			}
		});

		AlertDialog ad = buyDialog.create();
		ad.show();
	}*/


	public void play_rec (View V)
	{
		
		//if (filesCheck.length > Parameters.STATUS_SYSTEM_EVENT.length() || filesCheck.length == Parameters.STATUS_SYSTEM_EVENT.length())
		//String url = getUrlBySelected(currentRadio.getText().toString());
		//Toast.makeText(getApplicationContext(), "p&r " + url, 1).show();
		sendActionToService ();
	}

	public void rec (View V)
	{
		//String url = getUrlBySelected(currentRadio.getText().toString());
		//Toast.makeText(getApplicationContext(), "Rec " + url, 1).show();
		sendActionToService ();
	}
	@Override
	public void onImageLoaded(Bitmap trackPic, String currentImageArtist) {
		// TODO Auto-generated method stub
		if (trackPic !=null)
		{
			trackImage.setImageBitmap(trackPic);
			//System.out.println ("setPicture");
			trackPic = null;
		}
		else
		{
			trackImage.setImageResource(R.drawable.audio);
			//System.out.println ("setDefaultImage");
		}
		//this.currentImageArtist = currentImageArtist;
	}
	
	private void setLongClickListeners() {
		// TODO Auto-generated method stub
		/*play = (ImageButton)findViewById(R.id.play);
		play.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				showHelp(Parameters.PLAY);
				return true;
			}
		});
		play_rec = (ImageButton)findViewById(R.id.p_r);
		play_rec.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				showHelp(Parameters.PLAY_REC);
				return true;
			}
		});
		rec = (ImageButton)findViewById(R.id.rec);
		rec.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				showHelp(Parameters.REC);
				return true;
			}
		});
		stop = (ImageButton)findViewById(R.id.stop);
		stop.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				showHelp(Parameters.STOP);
				return true;
			}
		});*/

		}

	protected void showHelp(int param) {
		// TODO Auto-generated method stub
		String helpString = "";
		switch (param) {
		case Parameters.PLAY:
			helpString = getResources().getString(R.string.helpPlay);
			break;
		case Parameters.PLAY_REC:
			helpString = getResources().getString(R.string.helpPR);
			break;
		case Parameters.REC:
			helpString = getResources().getString(R.string.helpSave);
			break;
		case Parameters.STOP:
			helpString = getResources().getString(R.string.helpStop);
			break;
				default:
			break;
		}
		Toast.makeText(this, helpString, Toast.LENGTH_SHORT).show();
		}

	public void updateLib (View V)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(res.getString(R.string.libUpdate));
		builder.setTitle(res.getString(R.string.updLib));
		builder.setNegativeButton(res.getString(R.string.cancel), null);
		builder.setPositiveButton("OK", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				//Toast.makeText(getApplicationContext(), et.getText().toString(), 1).show();
				sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
			}
		});
		AlertDialog ad = builder.create();
		ad.show();

		
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		//if (slider.isOpened())
			//slider.animateClose();
		//else
		if (statusLayout.getVisibility() == View.VISIBLE)
		{
			hideStatusLayout();
			return;
		}
		if (optionsLayout.getVisibility() == View.VISIBLE)
		{
			hideOptionsLayout();
			return;
		}
		if(topBar.getVisibility() == View.VISIBLE)
		{
			hideTopBar();
			return;
		}
		
		super.onBackPressed();
	}
	public void addToFav (View V)
	{
		// this will be call after help dialog OK
		String station = Utils.getPrevStation(this);
		//System.out.println ("fav");
		Cursor cFav =db.query(false, "favorites", null,"name = '"+station+"'" , null, null, null, null, null);
		
		//ursor c =  db.query("radios", new String [] {"name"}, null, null, null, null, null);
		//db.query("favorites", null, "", null, null, null, null);
		if (cFav!=null && cFav.getCount() <1)
		{
		ContentValues values = new ContentValues();
		values.put("name", station);
			if (!(station != null && station.length()>2))
				return;
		db.insert("favorites", null, values);
		Toast.makeText(this, res.getString(R.string.addedToFav), Toast.LENGTH_SHORT).show();
		}
		else
		{
			Toast.makeText(this, res.getString(R.string.alreadyFav), Toast.LENGTH_SHORT).show();
		}
		
		
	}
	@Override
	public void onDeleteStation(String stationName) {
		// TODO Auto-generated method stub
		//System.out.println ("OnDeleteStation " + stationName);
		db.delete("favorites", "name = " + "'"+stationName+ "'", null);
		ListView stList = dialog.getListView();
		int curPos = stList.getFirstVisiblePosition();
		constructStationArray("Favorites");
		stList.setAdapter(new GalleryAdapter(this, stations, true, this));
		BaseAdapter ba = (BaseAdapter)stList.getAdapter();
		ba.notifyDataSetChanged();
		stList.setSelection(curPos+1);
	}
	public void setSaveCount(View V)
	{
		//Toast.makeText(this, res.getString(R.string.countSaveHelp), 1).show();
		//LayoutInflater inflater = getLayoutInflater();
		final String titleDialog = res.getString(R.string.countSaveMessage);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		View alertView = getLayoutInflater().inflate(R.layout.count_dialog, null, true);
		final TextView title = (TextView) alertView.findViewById(R.id.seekText);
		final SeekBar sk = (SeekBar) alertView.findViewById(R.id.seekBar);//1,2,3,4,5,10, 15, 20,30,50,100,0
		sk.setMax(11);
		int seekPositionSet = -1;
		int curCount = Utils.getMax(this);
		for (int i = 0; i < Parameters.maxCountArray.length; i++) {
			if (Parameters.maxCountArray [i] == curCount)
				seekPositionSet = i;
		}
		if (seekPositionSet == -1)
			return;
		
		//title.setText(titleDialog + ": " + String.valueOf(Parameters.getMaxFiles()));
		sk.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				//title.setText(titleDialog + ": " + String.valueOf(seekBar.getProgress()+1));
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				//title.setText(titleDialog + ": " + String.valueOf(seekBar.getProgress()+1));
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				//int realArg = seekBar.getProgress()+1;
				//int whole = realArg * 10;
				String textToshow = "";
				if (Parameters.maxCountArray [progress]== 0)
					textToshow = res.getString(R.string.noLimitSave);
				else
					textToshow = String.valueOf(Parameters.maxCountArray [progress]);
				title.setText(titleDialog + ": " +textToshow);
				//seekBar.setProgress(whole);
			}
		});
		if (seekPositionSet == 0)
			title.setText(titleDialog + ": " +String.valueOf(Parameters.maxCountArray [0]));
		sk.setProgress(seekPositionSet);
		builder.setView(alertView);
		//builder.setCustomTitle(title);
		//builder.setMessage(res.getString(R.string.countSaveMessage));
		builder.setNegativeButton(res.getString(R.string.cancel), null);
		builder.setPositiveButton("OK", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				//Toast.makeText(getApplicationContext(), et.getText().toString(), 1).show();
				//int maxConutnInput = (sk.getProgress()+1)*10;
				int valueToPut = Parameters.maxCountArray [sk.getProgress()];
				//System.out.println ("put value " + valueToPut);
				Utils.setMax(valueToPut, getApplicationContext());
			}
		});
		AlertDialog ad = builder.create();
		ad.show();

	}


	
	public void rateMe(View V)
	{
		Toast.makeText(this, res.getString(R.string.thanks), Toast.LENGTH_LONG).show();
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse("market://details?id=com.fallen.streamgetterfree"));
		startActivity(intent);
	}

	public void buy(View V)
	{
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse("market://details?id=com.fallen.streamgetter"));
		startActivity(intent);
	}
	/*public void startBuyMedia(final int sound) {
		// TODO Auto-generated method stub
		final MediaPlayer buyMedia = new MediaPlayer();
		try {
			AssetFileDescriptor afd = this.getResources().openRawResourceFd(sound);
			buyMedia.setAudioStreamType(AudioManager.STREAM_MUSIC);
			buyMedia.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(),afd.getDeclaredLength());
			buyMedia.setOnCompletionListener(new OnCompletionListener() {
				
				@Override
				public void onCompletion(MediaPlayer buyMedia) {
					// TODO Auto-generated method stub
					buyMedia.release();
					buyMedia = null;
					if (sound == R.raw.buybuyedited)
					{
					String url = getUrlBySelected(currentRadio.getText().toString());
					sendActionToService (url,Parameters.PLAY);
					}

				}
			});
			buyMedia.setOnPreparedListener(new OnPreparedListener() {
				
				@Override
				public void onPrepared(MediaPlayer buyMedia) {
					// TODO Auto-generated method stub
					
					buyMedia.start();
				}
			});
			buyMedia.prepareAsync();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
	@Override
	public void onProgressUpdate(StatusObj statusObjectToActivity) {
		// TODO Auto-generated method stub
		if (statusObjectToActivity!=null)
			setStatusToGUI(statusObjectToActivity);
	}
	private void showStatusLayoutParams(boolean fromClick) {
		// TODO Auto-generated method stub
		if (statusLayout.getVisibility() == View.VISIBLE)
			return;
		//Toast.makeText(this, "statClick", Toast.LENGTH_SHORT).show();
		setVolumeProgress ();
		Animation a = new TranslateAnimation(0, 0, height, 0);
    	a.setDuration(600);
    	statusLayout.setVisibility(View.VISIBLE);
    	favButton.setVisibility(View.VISIBLE);
    	statusLayout.startAnimation(a);
    	favButton.startAnimation(a);
		if (!fromClick)
		{
			handler = new Handler();
			handler.postDelayed(runnable = new Runnable() {
				@Override
				public void run() {

					hideStatusLayout();
				}
			}, 5000);
		}
		else
		{
			removeAutohide();
		}
			
		
	}
	private void removeAutohide() {
		// TODO Auto-generated method stub
		if (handler!=null && runnable!=null)
		handler.removeCallbacks(runnable);
	}

	private void setVolumeProgress() {
		// TODO Auto-generated method stub
		//561
		volumeBar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP)
			setVolumeProgress();
		return super.onKeyDown(keyCode, event);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (requestCode == 1 && Utils.isFirstLaunch(this))
		{
			Utils.setNotFirstLaunch(this);
			Toast.makeText(this, res.getString(R.string.helpGUIEnd), Toast.LENGTH_LONG).show();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}


	public void showDemoAgain (View V)
	{
		hideOptionsLayout();
		showHelpGUI(false);
		/*AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setMessage(res.getString(R.string.helpGUI));
    	builder.setPositiveButton("OK", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				pointerDown ();
			}
		});
    	builder.setNegativeButton("Cancel", null);
    	AlertDialog ad = builder.create();
    	ad.show();*/
	}

	@Override
	public void onUpdateStationBase(Integer result, int value) {
		// TODO Auto-generated method stub
		
		if (result == Parameters.UPDATE_FAIL)
		{
		    Toast.makeText(this, res.getString(R.string.updateStationBaseFail), Toast.LENGTH_LONG).show();
			//Utils.setBaseLoaded(this, false);
		    if (!Utils.isBaseLoadedFromAssets(this))
		    	createDBFromAssets(Utils.getMyLocale(this));
		    else
		    	fillByContent();
			
		}
		else if (result == Parameters.UPDATE_SUCCESS)
		{
			//copy from temp databse on success
			db.delete("radios", null, null); 
			db.execSQL("INSERT INTO 'radios' SELECT * FROM 'radios_temp';");
			Utils.setVersionUpdate(value, this);
			Utils.setBaseLoaded(this, true);
			Toast.makeText(this, res.getString(R.string.updatedStationBase), Toast.LENGTH_LONG).show();
			fillByContent();
			
		}
		else if (result == Parameters.UPDATE_EQUAL)
		{
			//System.out.println ("Base equal");
			fillByContent();
		}
		updating = false;
	}

	private void fillByContent() {
		// TODO Auto-generated method stub
		getGenres();
	    constructStationArray(genres.get(1));
	    currentRadio.setText(selectedRadio);
		String previosStation = Utils.getPrevStation(getApplicationContext());
		if (previosStation == null || previosStation.length()<2)
			noStationCase();
		else
			currentRadio.setText(previosStation);
	}

	private void createDBFromAssets(String locale) {
		// TODO Auto-generated method stub
		if (Utils.isBaseLoadedFromAssets(this))
			return;
		AssetManager am = getAssets(); 
        try {
			String [] alist = am.list("genres"); // get genres array
			for (int i = 0; i<alist.length; i++)
			{
				String genre = alist [i];
				InputStream is  = am.open("genres/"+alist[i]);
				BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
				String mString = "";
				ContentValues values = new ContentValues();
				int ind = 0;
				while ((mString =br.readLine()) != null)
				{
					ind++;
					if (ind == 1) // when line is name
					{
						values.put("name", mString);
						//System.out.println ("name " +mString);
					}
					else if (ind == 2) // when line is url
					{
						values.put("url", mString);
						values.put("genre", genre);
						ind = 0;
						//System.out.println ("url -" + mString);
						db.insert("radios", null, values); 
					}
				}
				
			}
			//Toast.makeText(this, "DataBase Created from assets", Toast.LENGTH_LONG).show();
			Utils.setBaseLoadedFromAssets(this, true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Utils.setBaseLoadedFromAssets(this, false);
			e.printStackTrace();
		}
		fillByContent();
		updateStationDB();

	}
	private void checkDBOpen() {
		// TODO Auto-generated method stub
		if (db == null || !db.isOpen())
    		db = new StationBase(this).getWritableDatabase();
	}
    private String detectLocale() {
		// TODO Auto-generated method stub
    	String locale = "";
    	locale = Locale.getDefault().getLanguage();
    	Utils.setMyLocale(locale, this);
    	return locale;
	}
    private void updateStationDB ()
    {
    	checkDBOpen();
    	int vers = Utils.getVersionUpdate(this);
    	AsyncUpdateStations aus = new AsyncUpdateStations(this,db,vers);
    	String locale = Utils.getMyLocale(this);
    	if(locale.equals("ru"))
    	{
    		aus.execute(Parameters.URL_STATIONS_RU);
    	}
    	else
    	{
    		aus.execute(Parameters.URL_STATIONS_WORLD);
    	}
    }
    public void topClicked (View V)
    {
    	if (topBar.getVisibility() == View.GONE)
    		showTopBar();
    	else
    		hideTopBar();
    			
    }
    public void statusClicked (View V)
    {
    	if (statusLayout.getVisibility() == View.GONE)
    		showStatusLayoutParams(true);
    	else
    		hideStatusLayout();
    }
    public void tracksClicked (View V)
    {
    	startPlayerActivity(false);
    }
    public void optionsClicked (View V)
    {
    	if (optionsLayout.getVisibility() == View.GONE)
    		showOptionsLayout();
    	else
    		hideOptionsLayout();
    }



	@Override
	public void onBufferingStart() {
		// TODO Auto-generated method stub
		statusField.setVisibility(View.VISIBLE);
		curStatus.setText(res.getString(R.string.statusBuf));
		
	}



	@Override
	public void onBufferingEnd() {
		if (mainService !=null)
			 setStatusToGUI(mainService.lastStatusObject);
	
		// TODO Auto-generated method stub
		/*
		switch (codeFromServiceWhenBufered) {
		case Parameters.PLAY:
			curStatus.setText(res.getString(R.string.statusPlay));
			break;
		case Parameters.PLAY_REC:
			curStatus.setText(res.getString(R.string.statusPR));
			break;
			
		}*/
	}



	@Override
	public void onReconnectStart(int reconnectTimes) {
		// TODO Auto-generated method stub
		curStatus.setText(res.getString(R.string.reconnect) + String.valueOf(reconnectTimes));
	}

	@Override
	public void onChangeToggles() {
		// TODO Auto-generated method stub
		
		savedToggleChanged();
		saveAllToggleChanged();
		playBackToggleChanged();
		
	}
	 private void startBlink() {
		// TODO Auto-generated method stub
		 
		 blinking = true;
		  new Handler().postDelayed(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					if (Utils.isSaveAllOn(getApplicationContext()))
					{
						//blink();
					}
					else
					{
						blinking = false;
					}
				}
			}, 1000);
	}

	protected void blink() {
		// TODO Auto-generated method stub
		if (saveAll.getVisibility() == View.GONE)
			saveAll.setVisibility(View.VISIBLE);
		else
			saveAll.setVisibility(View.GONE);
		
		startBlink();
	}

	public void forward(View V) {
		if (mainService != null)
			mainService.seekForward();

	}

	public void backward(View V) {
		mainService.seekBackward();
	}

	

	private void startCheckFileSize() {
			// TODO Auto-generated method stub
			 
		// System.out.println("start check " + fileToCheck);
		// System.out.println(stopCheckingFile + " " +checkingSize);
			  new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						if (!stopCheckingFile)
						{
							checkSize();
							checkingSize = true;
							progressLayout.setVisibility(View.VISIBLE);
						}
						else
						{
							progressLayout.setVisibility(View.GONE);
							checkingSize = false;
						}
					}
				}, 1000);
		}

		protected void checkSize() {
			// TODO Auto-generated method stub
			File file = new File (fileToCheck);
			long fileSiz = Math.abs(file.length()/1000);
			loadingBar.setProgress((int) fileSiz);
			kbText.setText(String.valueOf(fileSiz)+ " Kb");
			startCheckFileSize();
		}
		private void alreadyRecToast ()
		{
			Toast.makeText(this, res.getString(R.string.alreadyRec), Toast.LENGTH_SHORT).show();
		}
		
		public void addStation(View V)
		{
			AlertDialog.Builder builder  = new AlertDialog.Builder(this);
		    // Get the layout inflater
		    LayoutInflater inflater = getLayoutInflater();
		    LinearLayout dialogLayout = (LinearLayout)inflater.inflate(R.layout.add_url_layout, null);
		    // Inflate and set the layout for the dialog
		    // Pass null as the parent view because its going in the dialog layout
		    builder.setView(dialogLayout);
		    
	        newUrlStation = (EditText)dialogLayout.findViewById(R.id.newStationUrl) ;
	        newNameStation  = (EditText)dialogLayout.findViewById(R.id.newStationName);
	        newStreamBuilder = builder.create();
	        newStreamBuilder.show();
		}
		public void addConfirm (View V)
		{
			//db.execSQL("CREATE TABLE radios ( _ID INTEGER PRIMARY KEY AUTOINCREMENT, 'genre' TEXT, 'url' TEXT, 'name' TEXT);"); // create

			//db.execSQL("CREATE TABLE favorites ( _ID INTEGER PRIMARY KEY AUTOINCREMENT, 'name' TEXT);");
			String newUrlStationString = "";
			String newNameStationString = "";
			newUrlStationString = newUrlStation.getText().toString();
			newNameStationString = newNameStation.getText().toString();
			ContentValues values = new ContentValues();
			values.put("genre", "");
			values.put("url", newUrlStationString);
			values.put("name", newNameStationString);
			db.insert("radios", null, values);
			ContentValues favValues = new ContentValues();
			favValues.put("name", newNameStationString);
			db.insert("favorites", null, favValues );
			Toast.makeText(this, res.getString(R.string.addedToFav), Toast.LENGTH_SHORT).show();
			newStreamBuilder.cancel();
		}
}
