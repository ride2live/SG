package com.fallen.streamgetter;

import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Handler;
import com.fallen.streamgetterfree.R;

public class AsyncWaitMedia extends AsyncTask<MediaPlayer, Integer, Void>
{

	private CallBackWaitMedia cbwm;
	private boolean prepareSended = false;
	public AsyncWaitMedia(CallBackWaitMedia cbwm) {
		// TODO Auto-generated constructor stub
		this.cbwm =cbwm;
	}

	@Override
	protected Void doInBackground(MediaPlayer... params) {
		// TODO Auto-generated method stub
		MediaPlayer currentPlayer = params[0];
		//System.out.println(currentPlayer.isPlaying());
		//System.out.println ("AsyncTaskPlayer duration " + currentPlayer.getDuration() +" position " + currentPlayer.getCurrentPosition());
		prepareSended = false;
		while (currentPlayer.getDuration() - currentPlayer.getCurrentPosition()>1000)
		{
			//System.out.println ("duration"+currentPlayer.getDuration() +" position"+currentPlayer.getCurrentPosition());
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		publishProgress(currentPlayer.getCurrentPosition());
		return null;
	}
	


	@Override
	protected void onProgressUpdate(Integer... values) {
		// TODO Auto-generated method stub
		cbwm.onNeedPrepare(values[0]);
		super.onProgressUpdate(values);
	}
	


	

}
