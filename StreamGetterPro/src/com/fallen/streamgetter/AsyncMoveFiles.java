package com.fallen.streamgetter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import android.os.AsyncTask;
import com.fallen.streamgetterfree.R;

public class AsyncMoveFiles extends AsyncTask<File [], Void, Void>{


private CallBackFilesCopy cbfc;
public AsyncMoveFiles(CallBackFilesCopy cbfc) {
	// TODO Auto-generated constructor stub
	this.cbfc = cbfc;
}
	@Override
	protected Void doInBackground(File[]... params) {
		// TODO Auto-generated method stub
		File []filesToAccept = params[0];
		File dirAcc = new File(Parameters.ACCEPTED);
		dirAcc.mkdirs();
		try {
		for (int i = 0; i < filesToAccept.length; i++) {

			FileChannel from = new FileInputStream(filesToAccept [i]).getChannel();
			FileChannel to = new FileOutputStream(dirAcc + "/"+filesToAccept [i].getName()).getChannel(); // create rewrite dialog
			to.transferFrom(from, 0, from.size());
			from.close();
			to.close();
			filesToAccept [i].delete();
		}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	@Override
	protected void onPostExecute(Void result) {
		// TODO Auto-generated method stub
		cbfc.onMoveComplete();
		super.onPostExecute(result);
	}
	

}
