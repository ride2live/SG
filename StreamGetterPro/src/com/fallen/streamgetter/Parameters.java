package com.fallen.streamgetter;

import android.graphics.Color;
import android.os.Environment;
import com.fallen.streamgetterfree.R;

public final class Parameters {
	public final static int PLAY = 0;
	public final static int REC = 1; 
	public static final int PLAY_REC = 2;
	public static final int STOP = 3;
	public static final int PREVIEW = 4;
	public static final int NEXT = 5;
	public static final int PREVIOUS = 6;
	public final static String ROOT = Environment.getExternalStorageDirectory().getAbsolutePath()+"/GamerCatch";
	public final static String TEMP = Environment.getExternalStorageDirectory().getAbsolutePath()+"/StreamGetter/temp/";
	public final static String SAVE = Environment.getExternalStorageDirectory().getAbsolutePath()+"/StreamGetter/save/";
	public final static String ACCEPTED = Environment.getExternalStorageDirectory().getAbsolutePath()+"/StreamGetter/accepted/";
	public final static String BASE64_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqxhwa4adjhe4bpFZqrcaeRHdJo7N1AlFvHGwPV08UjbZXUXMjn61OnWTsdjXKp+8oFdwsp2cFkb2nmyEinS2bdK8KQzyYss+LuCFBoScMyedGXrlSFhkT4FleD5k5zBdmCHQXzr9FCojbSLkYrxRSoNjKPzePPkm1hFortn08TPNo02hfDtJBbt18YNAe2gax1SqbrR5YguRGpJ82bd+RnEOK30FUIMH6sVm8LUMaxlet/7RWT4b88kJK2vZQCwfvizQRS9OyxS1XJkBL2cSDjH1yCS6t5nxMXBeDSiJYMAH2G+p0vt3S2SUF5XP5rFFD/JKy6zWNkJzmuzq0z95BwIDAQAB";
	public final static String [] unwantedSymbols = new String []{"?","/","'","StreamTitle=", "!",".", "*","|", "\\", "<", ":", ">", "\""};
	//public static String SAVE_COUNT_MESSAGE = Resources.getSystem().getString(R.string.countSaveHelp);
	public final static int CODE_PUB_CONNECTED = 100;
	public final static int CODE_PUB_CHANGE_TITLE = 101; 
	//public static final int CODE_PUB_STAT_CHANGE = 102;
	public final static int CODE_PUB_NEW_FILE = 103;
	public final static int CODE_PUB_PLAYING = 104;
	public final static int CODE_PUB_BUFFERING = 105;
	public final static int CODE_PUB_BUFFERED = 99;
	public final static int CODE_PUB_REC = 106;
	public final static int CODE_PUB_STOPPED = 107;
	public final static int CODE_PUB_ERROR = 108;
	public final static int CODE_PUB_P_R = 109;
	public final static int CODE_PUB_CONNECTING = 110;
	public final static int CODE_PUB_RECONNECTING = 111;
	public final static int CODE_PUB_SOCKET = 112;
	public final static String URL_VERS = "http://dl.dropbox.com/u/102433765/vers.txt";
	public final static String URL_GENRES = "http://dl.dropbox.com/u/102433765/genrestxt.txt";
	public final static String URL_STATIONS_RU = "http://dl.dropbox.com/u/102433765/ru/";
	public final static String URL_STATIONS_WORLD = "http://dl.dropbox.com/u/102433765/world/";
	public final static int UPDATE_EQUAL = -2;
	public final static int UPDATE_FAIL = -1;
	public final static int UPDATE_SUCCESS = -3;
	public final static int GREEN = Color.rgb(80, 80, 80);
	public final static String UPDATE_KEY_VALUE = "value";
	public final static String UPDATE_KEY_RESULT = "result";
	//public final static String STATUS_SYSTEM_EVENT = "systemevent";
	public static boolean imageUpdated = false;
	public static int [] maxCountArray = new int [] {1,2,3,4,5,10, 15, 20,30,50,100,0};
	 public static final byte[] SALT = new byte[] {
	     -122, 122, 32, -12, -113, -11, 74, -64, 51, 88, -95,
	     -45, 77, -117, -36, -113, -112, 33, -62, 81
	     };
	public static final int CODE_PUB_IDLE = 113;
	public static final int CODE_PUB_LIMIT_EXCEED = 114;
	public static final int CODE_PUB_FILE_RECORDED = 115;

	public static boolean isImageUpdated() {
		return imageUpdated;
	}
	public static void setImageUpdated(boolean imageUpdated) {
		Parameters.imageUpdated = imageUpdated;
	}


}
