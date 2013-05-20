package com.fallen.streamgetter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import com.fallen.streamgetterfree.R;

public class ImageLoadAsyncTask extends AsyncTask<String, Void, Bitmap> {
	LoadImageResult lir;
	String title;
	XMLLastFMHandler xmlHandler;
	private static String API =  "api_key=adee6cbf2cc4a80beb6c4b5ed00e8249";
	private static String METHOD = "http://ws.audioscrobbler.com/2.0/?method=track.search";
	private static String METHOD_RESERVE = "http://ws.audioscrobbler.com/2.0/?method=artist.search";
	public ImageLoadAsyncTask(LoadImageResult lir) {
		// TODO Auto-generated constructor stub
		this.lir = lir;
	}
	@Override
	protected Bitmap doInBackground(String... params) {
		// TODO Auto-generated method stub
			title= params[0];
			try {
				//System.out.println ("start image back " + title);
				String parsedArtist = URLEncoder.encode(Utils.parseArtist(params [0]), "UTF-8");
				String parsedTrack = URLEncoder.encode(Utils.parseTrack(params [0]), "UTF-8");
				if (parsedArtist.equals(""))
					return null;
				xmlHandler = new XMLLastFMHandler(Utils.parseArtist(params [0]));
				xmlHandler.artistTagValue = parsedArtist;
				String request = METHOD +"&"+ API + "&track="+parsedTrack + "&artist="+ parsedArtist + "&autocorrect=1";
				String request_reserve =  METHOD_RESERVE +"&"+ API + "&artist="+ URLEncoder.encode(Utils.parseArtist(params [0]), "UTF-8") + "&autocorrect=1";
				//boolean requestOk  = ;
				startRequest (request);
				Bitmap bitmap = xmlHandler.getResult();
				if (bitmap !=null)
					return bitmap;
				else
				{
					startRequest(request_reserve);
					bitmap = xmlHandler.getResult();
					return bitmap;
				}
				
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
				//return xmlHandler.getResult();
				
	}

	private void startRequest(String request) {
		try {
			
			//String request =  METHOD +"&"+ API + "&artist="+ URLEncoder.encode(Utils.parseArtist(params [0]), "UTF-8") + "&track=" +URLEncoder.encode(Utils.parseTrack(params[0]) , "UTF-8")+"&autocorrect=1";
			//String request =  METHOD +"&"+ API + "&artist="+ URLEncoder.encode("Guano Apes", "UTF-8") + "&track=" +URLEncoder.encode("Break The Line" , "UTF-8")+"&autocorrect=1";
			//String request = "http://api.deezer.com/2.0/search/album?q="+URLEncoder.encode(params[0].replace(" - ", " "))+"&output=xml";
		//System.out.println (request);
		URL url = new URL (request);
		URLConnection uCon = url.openConnection();
		uCon.setConnectTimeout(2000);
		uCon.connect();
		SAXParserFactory spf = SAXParserFactory.newInstance();
		SAXParser sp;
		sp = spf.newSAXParser();
		sp.parse(uCon.getInputStream(), xmlHandler);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		// TODO Auto-generated method stub
	/*	if (xmlHandler.getResult()!=null)
			return true;
		else
			return false;*/
					
	}
	@Override
	protected void onPostExecute(Bitmap result) {
		// TODO Auto-generated method stub
		
		lir.onImageLoaded(result, title);
		super.onPostExecute(result);
	}

}
