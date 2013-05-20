package com.fallen.streamgetter;

import java.io.File;
import java.io.UnsupportedEncodingException;

import org.apache.http.util.ByteArrayBuffer;

import android.content.Context;
import com.fallen.streamgetterfree.R;

 
public class Utils {

	public static int getGenreImage (int position)
	{
		
		 switch (position) {
			case 0:
				return R.drawable.star_none; 
			case 1:
				return R.drawable.database; 
			case 2:
				return R.drawable.alt; 
			case 3:
				return R.drawable.classic; 
			case 4:
				return R.drawable.dancenew;
			case 5:
				return R.drawable.hipnew; 
			case 6:
				return R.drawable.jazznew;
			case 7:
				return R.drawable.news;
			case 8:
				return R.drawable.pop; 
			case 9:
				return R.drawable.metall; 
			case 10:
				return R.drawable.soundtr; 
			default:
				return -1;
			}
		
		
	}
	public static void deleteTemp (){
		File tempFiles = new File (Parameters.TEMP);
		if (tempFiles.isDirectory())
		{
			File[] files =tempFiles.listFiles();
			if (!(files.length >0))
				return;
			for (int i = 0; i < files.length; i++) 
			{
				////System.out.println ("deleting " +tempFiles.listFiles() [i].getAbsoluteFile());
				//System.out.println ("deleting");
				files[i].delete();
			}
		}
		
	}

	public static String parseArtist (String fullTitle)
	{
		if (!fullTitle.contains(" - "))
			return "";
		String divider = " - ";
		String artist = fullTitle.substring(0, fullTitle.indexOf (divider));
		//System.out.println (artist);
		return artist;
	}
	public static String parseTrack (String fullTitle)
	{
		if (!fullTitle.contains(" - "))
			return "";
		String divider = " - ";
		String track = fullTitle.substring(fullTitle.indexOf (divider)+divider.length());
		//System.out.println (track);
		return track;
		
	}
	public static byte[] getID3Tags(String title) {
		
		// TODO Auto-generated method stub
		String artist = parseArtist(title);
		String trackName = parseTrack(title);
		ByteArrayBuffer bab = new ByteArrayBuffer(300);
		byte[] header = constructHeader();
		byte [] tiltleFullTag = constructFullTitleTag(trackName);
		byte [] artistFullTag = constructArtistFullTag (artist);
		byte [] genreFullTag = constructAlbumFullTag ("StreamGetter");
		bab.append(header, 0,header.length);
		bab.append(tiltleFullTag, 0,tiltleFullTag.length);
		bab.append(artistFullTag, 0,artistFullTag.length);
		bab.append(genreFullTag, 0,genreFullTag.length);
		return bab.toByteArray();
	}

	private static byte[] constructAlbumFullTag(String myAlbum) {
		// TODO Auto-generated method stub
		ByteArrayBuffer bab= null;
		try {
			byte [] albumBytes = myAlbum.getBytes("UTF-16");
			byte [] albumTagID = constructAlbumTagID(albumBytes.length);
			byte encoding = 1; //utf-16
			bab = new ByteArrayBuffer(137); //full tag bytes 127 (max tag size) + 10 (frame)
			bab.append(albumTagID, 0, albumTagID.length);
			bab.append(encoding);
			bab.append(albumBytes, 0, albumBytes.length);
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bab.toByteArray();
	}
	private static byte[] constructAlbumTagID(int albumBytesLenght) {
		// TODO Auto-generated method stub
		byte [] albumTagID = new byte [10];
		albumTagID [0] = 'T';
		albumTagID [1] = 'A';
		albumTagID[2] = 'L';
		albumTagID [3] = 'B';
		albumTagID[4] = 0;
		albumTagID [5] = 0;
		albumTagID [6]= 0;
		albumTagID[7] = (byte) (albumBytesLenght+1); //size + encoding
		albumTagID[8] =0; //flags
		albumTagID [9] = 0;
		return albumTagID;
	}

	private static byte[] constructArtistFullTag(String artist) {
		// TODO Auto-generated method stub
		ByteArrayBuffer bab= null;
		try {
			if (artist.length()>63)
				artist = artist.substring(0,63);
			byte [] artistBytes = artist.getBytes("UTF-16");
			byte [] artistTagID = constructArtistTagID(artistBytes.length);
			byte encoding = 1; //utf-16
			bab = new ByteArrayBuffer(137); //full tag bytes 127 (max tag size) + 10 (frame)
			bab.append(artistTagID, 0, artistTagID.length);
			bab.append(encoding);
			bab.append(artistBytes, 0, artistBytes.length);
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bab.toByteArray();
	}
	private static byte[] constructArtistTagID(int artistBytesLenght) {
		// TODO Auto-generated method stub
		byte [] artistTagID = new byte [10];
		artistTagID [0] = 'T';
		artistTagID [1] = 'P';
		artistTagID[2] = 'E';
		artistTagID [3] = '1';
		artistTagID[4] = 0;
		artistTagID [5] = 0;
		artistTagID [6]= 0;
		artistTagID[7] = (byte) (artistBytesLenght+1); //size + encoding
		artistTagID[8] =0; //flags
		artistTagID [9] = 0;
		return artistTagID;

	}
	private static byte[] constructFullTitleTag(String trackName) {
		// TODO Auto-generated method stub
		ByteArrayBuffer bab= null;
		try {
			if (trackName.length()>63)
				trackName = trackName.substring(0,63);
			byte [] titleBytes = trackName.getBytes("UTF-16");
			byte [] titleTagID = constructTitleTagID(titleBytes.length);
			byte encoding = 1; //utf-16
			bab = new ByteArrayBuffer(137); //full tag bytes 127 (max tag size) + 10 (frame)
			bab.append(titleTagID, 0, titleTagID.length);
			bab.append(encoding);
			bab.append(titleBytes, 0, titleBytes.length);
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bab.toByteArray();
	}
	private static byte[] constructTitleTagID(int trackBytesLenght) {
		// TODO Auto-generated method stub
		byte [] titleTagID = new byte [10];
		titleTagID [0] = 'T';
		titleTagID [1] = 'I';
		titleTagID[2] = 'T';
		titleTagID [3] = '2';
		titleTagID[4] = 0;
		titleTagID [5] = 0;
		titleTagID [6]= 0;
		titleTagID[7] = (byte) (trackBytesLenght+1); //size + encoding
		titleTagID[8] =0; //flags
		titleTagID [9] = 0;
		return titleTagID;
	}
	private static byte[] constructHeader() {
		// TODO Auto-generated method stub
		byte[] header = new byte [10];
		header  [0] = 'I';
        header [1] = 'D';
        header[2] = '3';
        header [3] = 3;
        header [4]=0;
        header[5] = 0;
        header [6] = 0;
        header [7]= 0;
        header[8] = 10;
        header [9] = 127; //frames lenght
		return header;
	}
	public static String getPrevStation (Context context) {
		String st  = context.getSharedPreferences("prefs", 0).getString("previos_station", "");
		return st;
	}
	public static void setPrevStation (String st,Context context) {
		//System.out.println(st + " to prefs");
		context.getSharedPreferences("prefs", 0).edit().putString("previos_station", st).commit();
	}
	public static int getCurGenre (Context context) {
		int pos  = context.getSharedPreferences("prefs", 0).getInt("current_genre", 0);
		return pos;
	}
	public static void setCurGenre (int pos,Context context) {
		//System.out.println(st + " to prefs");
		context.getSharedPreferences("prefs", 0).edit().putInt("current_genre", pos).commit();
	}
	public static boolean isFirstLaunch (Context context) {
		boolean isFirstLaunch  = context.getSharedPreferences("prefs", 0).getBoolean("firstLaunch", true);
		return isFirstLaunch;
	}
	public static void setNotFirstLaunch (Context context) {
		//System.out.println(st + " to prefs");
		context.getSharedPreferences("prefs", 0).edit().putBoolean("firstLaunch", false).commit();
	}
	public static int getMax (Context context) {
		int pos  = context.getSharedPreferences("prefs", 0).getInt("max_count", 0);
		return pos;
	}
	public static void setMax (int pos,Context context) {
		//System.out.println(st + " to prefs");
		context.getSharedPreferences("prefs", 0).edit().putInt("max_count", pos).commit();
	}

	public static boolean isPiracy(Context context) {
		// TODO Auto-generated method stub
		int piracy  = context.getSharedPreferences("prefs", 0).getInt("piracy", 0);
		//System.out.println ("Utils says " + piracy );
		if (piracy == 89118)
			return true;
		else
			return false;
	}
	public static int getVersionUpdate(Context context) {
		// TODO Auto-generated method stub
		int vers  = context.getSharedPreferences("prefs", 0).getInt("vers", -1);
		return vers;
	}
	public static void setVersionUpdate (int vers, Context context) {
		//System.out.println(st + " to prefs");
		context.getSharedPreferences("prefs", 0).edit().putInt("vers", vers).commit();
	}
	public static boolean isBaseLoaded (Context context) {
		boolean isFirstLaunch  = context.getSharedPreferences("prefs", 0).getBoolean("baseload", false);
		return isFirstLaunch;
	}
	public static void setBaseLoaded (Context context, boolean loaded) {
		//System.out.println(st + " to prefs");
		context.getSharedPreferences("prefs", 0).edit().putBoolean("baseload", loaded).commit();
	}
	public static boolean isBaseLoadedFromAssets (Context context) {
		boolean isBaseLoadedFromAssets  = context.getSharedPreferences("prefs", 0).getBoolean("baseloadasset", false);
		return isBaseLoadedFromAssets;
	}
	public static void setBaseLoadedFromAssets (Context context, boolean loaded) {
		//System.out.println(st + " to prefs");
		context.getSharedPreferences("prefs", 0).edit().putBoolean("baseloadasset", loaded).commit();
	}
	public static boolean isCutOn (Context context) {
		boolean cut  = context.getSharedPreferences("prefs", 0).getBoolean("cutByTracks", true);
		return cut;
	}
	public static void setCutByTracks (Context context, boolean cut) {
		//System.out.println(st + " to prefs");
		context.getSharedPreferences("prefs", 0).edit().putBoolean("cutByTracks", cut).commit();
	}
	public static boolean isImagesOn (Context context) {
		boolean cut  = context.getSharedPreferences("prefs", 0).getBoolean("images", true);
		return cut;
	}
	public static void setImagesOn (Context context, boolean isImagesOn) {
		//System.out.println(st + " to prefs");
		context.getSharedPreferences("prefs", 0).edit().putBoolean("images", isImagesOn).commit();
	}
	public static String getMyLocale (Context context) {
		String locale  = context.getSharedPreferences("prefs", 0).getString("locale", "");
		return locale;
	}
	public static void setMyLocale (String locale,Context context) {
		//System.out.println(st + " to prefs");
		context.getSharedPreferences("prefs", 0).edit().putString("locale", locale).commit();
	}
	public static boolean isSaveCurrentOn (Context context) {
		boolean cut  = context.getSharedPreferences("prefs", 0).getBoolean("saveCurrent", false);
		return cut;
	}
	public static void setSaveCurrent (Context context, boolean cut) {
		//System.out.println(st + " to prefs");
		context.getSharedPreferences("prefs", 0).edit().putBoolean("saveCurrent", cut).commit();
	}
	public static boolean isPlayBackOn (Context context) {
		boolean cut  = context.getSharedPreferences("prefs", 0).getBoolean("playback", false);
		return cut;
	}
	public static void setPlayBack (Context context, boolean cut) {
		//System.out.println(st + " to prefs");
		context.getSharedPreferences("prefs", 0).edit().putBoolean("playback", cut).commit();
	}
	public static boolean isSaveAllOn (Context context) {
		boolean cut  = context.getSharedPreferences("prefs", 0).getBoolean("saveAll", false);
		return cut;
	}
	public static void setSaveAll (Context context, boolean cut) {
		//System.out.println(st + " to prefs");
		context.getSharedPreferences("prefs", 0).edit().putBoolean("saveAll", cut).commit();
	}
	public static boolean isShowHelpLayout (Context context) {
		boolean cut  = context.getSharedPreferences("prefs", 0).getBoolean("helpLayout", false);
		return cut;
	}
	public static void setShowHelpLayout (Context context, boolean cut) {
		//System.out.println(st + " to prefs");
		context.getSharedPreferences("prefs", 0).edit().putBoolean("helpLayout", cut).commit();
	}
}
