package com.fallen.streamgetter;

import com.fallen.streamgetterfree.R;

public interface CallBackToServer {
	public void onFilenameChange(String filePath);

	public void onPlayAvailable();

	void onAnyFail(StatusObj lastObj);

	void setNextFile(String nextFile);

	public void onCanceled(StatusObj lastObj);

	void onAnyRecording();

	void onPreviewedSuccess();

	public void onConnected();

	public void socketCreated();

	public void playTemp(String filePath);

	public void onSocketClosed();
	public void onFileRecorded(String title);
	public void onLimitExceed ();
}
