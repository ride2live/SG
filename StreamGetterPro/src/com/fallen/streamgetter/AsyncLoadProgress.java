package com.fallen.streamgetter;

import com.fallen.streamgetterfree.R;




public interface AsyncLoadProgress {
	void onBufferingStart();
	void onBufferingEnd();
	void onReconnectStart(int reconnectTimes);
	void onProgressUpdate(StatusObj statusObjectToActivity);
	void onChangeToggles (); 
	
	//public void onProgressUpdate(StatusObj statusObjectToActivity);
	//public void onReconnect(int times);
}
