package com.fallen.streamgetter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;
import com.fallen.streamgetterfree.R;

public class StationBase extends SQLiteOpenHelper {

	private Context context;

	public StationBase(Context context) {
		super(context, "rdb", null, 2);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) { // when database created, get
												// station data (genre, name,
												// url) from asset folder
		// TODO Auto-generated method stub
		try {
			db.execSQL("CREATE TABLE radios ( _ID INTEGER PRIMARY KEY AUTOINCREMENT, 'genre' TEXT, 'url' TEXT, 'name' TEXT);"); // create
																																// rad
																																// table
																																// structure
		} catch (Exception e) {
			// TODO: handle exception
		}
		try {
			db.execSQL("CREATE TABLE favorites ( _ID INTEGER PRIMARY KEY AUTOINCREMENT, 'name' TEXT);"); // create
																											// fav
																											// table
																											// structure
		} catch (Exception e) {
			// TODO: handle exception
		}
		try {
			db.execSQL("CREATE TABLE radios_temp ( _ID INTEGER PRIMARY KEY AUTOINCREMENT, 'genre' TEXT, 'url' TEXT, 'name' TEXT);"); // create
																																		// rad
																																		// table
																																		// structure
		} catch (Exception e) {
			// TODO: handle exception
		}

		/*
		 * AssetManager am = context.getAssets(); try { String [] alist =
		 * am.list("genres"); // get genres array for (int i = 0;
		 * i<alist.length; i++) { String genre = alist [i]; InputStream is =
		 * am.open("genres/"+alist[i]); BufferedReader br = new
		 * BufferedReader(new InputStreamReader(is, "UTF-8")); String mString =
		 * ""; ContentValues values = new ContentValues(); int ind = 0; while
		 * ((mString =br.readLine()) != null) { ind++; if (ind == 1) // when
		 * line is name { values.put("name", mString); //System.out.println
		 * ("name " +mString); } else if (ind == 2) // when line is url {
		 * values.put("url", mString); values.put("genre", genre); ind = 0;
		 * //System.out.println ("url -" + mString);
		 * 
		 * db.insert("radios", null, values); } }
		 * 
		 * } // } catch (IOException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */
		Toast.makeText(context, "DataBase onCreate method", Toast.LENGTH_LONG)
				.show();

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		try {
			db.execSQL("CREATE TABLE radios_temp ( _ID INTEGER PRIMARY KEY AUTOINCREMENT, 'genre' TEXT, 'url' TEXT, 'name' TEXT);");
		} catch (Exception e) {
			// TODO: handle exception
			 // create
		}
		
																																	// rad
																																	// table
																																	// structure
	}

}
