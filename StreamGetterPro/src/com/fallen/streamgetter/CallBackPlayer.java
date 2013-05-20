package com.fallen.streamgetter;

import com.fallen.streamgetterfree.R;

public interface CallBackPlayer {
	void onStartPlayNewFile (int overallTime, String title);
	void onProgressUpdate (int currentTime);
}
