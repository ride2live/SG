package com.fallen.streamgetter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import com.fallen.streamgetterfree.R;

public class AsyncUpdateStations extends AsyncTask<String, ContentValues, ContentValues> {
	CallBAckUpdateStations cbus;
	StationBase sb;
	SQLiteDatabase db ;
	int vers = -1;
	
	public AsyncUpdateStations(CallBAckUpdateStations cbus, SQLiteDatabase db, int vers) {
		// TODO Auto-generated constructor stub
		this.cbus = cbus;
		this.db = db;
		this.vers = vers; // from preferences stations version
	}

	@Override
	protected ContentValues doInBackground(String... params) {
		// TODO Auto-generated method stub
		int result = Parameters.UPDATE_FAIL; 
		int value = -1; // from server stations version
		ContentValues cv = new ContentValues();
		cv.put(Parameters.UPDATE_KEY_VALUE, value);
		cv.put(Parameters.UPDATE_KEY_RESULT, result);
		try {
			URL url = new URL(Parameters.URL_VERS);
			HttpURLConnection uCon = (HttpURLConnection) url.openConnection();
			uCon.setConnectTimeout(3000);
			uCon.setReadTimeout(3000);
			uCon.connect();
			if (!(uCon.getResponseCode() == HttpURLConnection.HTTP_OK))
				return cv;
			BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
			
			value = Integer.valueOf(br.readLine());
			//System.out.println ("Value from dropbox = " +value);
			if (vers < value)
			{
				try {
					db.delete("radios_temp", null, null);
				} catch (Exception e) {
					// TODO: handle exception
				}
				
				//db.execSQL("CREATE TABLE radios_temp ( _ID INTEGER PRIMARY KEY AUTOINCREMENT, 'genre' TEXT, 'url' TEXT, 'name' TEXT);");
				String stationsByLocale =  params[0];
				//stationsByLocale = "http://dl.dropbox.com/u/102433765/test/"; 
				boolean success = downloadStationList(stationsByLocale);
				if (!success)
					result = Parameters.UPDATE_FAIL;
				else
					result = Parameters.UPDATE_SUCCESS;
			}
			else
			{
				result = Parameters.UPDATE_EQUAL;
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return cv;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return cv;
		}
		cv.put(Parameters.UPDATE_KEY_VALUE, value);
		cv.put(Parameters.UPDATE_KEY_RESULT, result);
		return cv;
	}
	private boolean downloadStationList(String langFolder) {
		// TODO Auto-generated method stub
		URL url;
		try {
			url = new URL(Parameters.URL_GENRES);
			BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
			String genre = "";
			
			ArrayList<String> genreList = new ArrayList<String>();
			while ((genre = br.readLine()) !=null)
			{
				genreList.add(genre);
				//System.out.println (genre);
			}
			for (int i = 0; i<genreList.size(); i++)
			{
				genre = URLEncoder.encode(genreList.get(i), "UTF-8");
				genre = genre.replace("+", "%20");
				String urlGenre = langFolder +genre;
				//System.out.println (urlGenre);
				br = new BufferedReader(new InputStreamReader(new URL (urlGenre).openStream()));
				
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
						values.put("genre", genreList.get(i));
						ind = 0;
						//System.out.println ("url -" + mString);
						
						db.insert("radios_temp", null, values); 
					}
				}
		}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	@Override
	protected void onPostExecute(ContentValues cv) {
		// TODO Auto-generated method stub
		cbus.onUpdateStationBase(cv.getAsInteger(Parameters.UPDATE_KEY_RESULT), cv.getAsInteger(Parameters.UPDATE_KEY_VALUE));
		super.onPostExecute(cv);
	}




}
