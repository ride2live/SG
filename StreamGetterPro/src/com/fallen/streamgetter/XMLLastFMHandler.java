package com.fallen.streamgetter;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.fallen.streamgetterfree.R;

public class XMLLastFMHandler extends DefaultHandler {
	Bitmap trackImage;
	String tagValue;
	String garbageTagValue;
	String mLocalName;
	String mQName;
	String attrMedium;
	String attributes;
	String resultURLString;
	String artistTagValue;
	String artistToFind;
	
	private boolean firstAttempt;
	public XMLLastFMHandler(String parsedArtistWithoutUTF) {
		// TODO Auto-generated constructor stub
		firstAttempt = true;
		artistToFind = parsedArtistWithoutUTF;
	}
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		// TODO Auto-generated method stub
		
		mLocalName = localName;
		mQName= qName;
		tagValue="";
		if (attributes.getValue(0) !=null)
			this.attributes = attributes.getValue(0);
		//attrMedium = attributes.getValue("size");
	//	//System.out.println ("local name = " + mLocalName);
	//	//System.out.println ("attr = " + attributes.getValue(0));
				super.startElement(uri, localName, qName, attributes);
	}

	
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		// TODO Auto-generated method stub
					tagValue =tagValue+ new String(ch).substring(start, length);
			////System.out.println ("tagValue = " +tagValue);

	
		super.characters(ch, start, length);
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (mLocalName.equals("artist"))
		{
			////System.out.println (artistTagValue + "artToFind" + artistToFind);
			artistTagValue = tagValue;
			
		}
		////System.out.println("garb = " +garbageTagValue);
		
		if (firstAttempt && artistTagValue.equals(artistToFind))
		{
			if (mLocalName.equals("image") && attributes.equals("extralarge")&& resultURLString==null)
			{
			resultURLString = tagValue;
			//System.out.println("First Attempt, artist find, resultURLString = " +resultURLString);
			}
		}
		else if (!firstAttempt && artistTagValue.equals(artistToFind));
		{
			if (mLocalName.equals("image") && attributes.equals("mega")&& resultURLString==null)
			{
			resultURLString = tagValue;
			//System.out.println("!First Attempt resultURLString = " +resultURLString);
			}
		}
		
	/*	if (mLocalName.equals("content"))
		{
			//System.out.println (tagValue);
		}*/
		// TODO Auto-generated method stub
		super.endElement(uri, localName, qName);
	}

	public Bitmap getResult() {
		// TODO Auto-generated method stub
		URL imageURL;
		firstAttempt = false;
		try {
			//System.out.println ("BEFORE LOAD CHECK = " +resultURLString);
			imageURL = new URL (resultURLString);
			URLConnection ucon  =imageURL.openConnection();
			ucon.setConnectTimeout(3000);
			ucon.connect();
			trackImage  = BitmapFactory.decodeStream(ucon.getInputStream());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		resultURLString = null;
		return trackImage;
	}

}
