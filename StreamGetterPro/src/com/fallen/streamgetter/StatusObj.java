package com.fallen.streamgetter;

import com.fallen.streamgetterfree.R;

public class StatusObj {
	int status = 0;
	String statusMessage = "";
	String genre = "";
	String bitrate = "";
	String site = "";
	String saveFolder = "";
	String title = "";
	boolean recording = false;
	boolean isBuffering = false; 
	int curRecCount = 0;
	int maxCount = 0;
	boolean notLicensed = false;
	private String currentFile ="";
	public boolean isNotLicensed() {
		return notLicensed;
	}
	public void setNotLicensed(boolean notLicensed) {
		this.notLicensed = notLicensed;
	}
	public int getMaxCount() {
		return maxCount;
	}
	public void setMaxCount(int maxCount) {
		this.maxCount = maxCount;
	}
	public int getCurRecCount() {
		return curRecCount;
	}
	public void setCurRecCount(int curRecCount) {
		this.curRecCount = curRecCount;
	}
	
	public boolean isRecording() {
		return recording;
	}
	public void setRecording(boolean recording) {
		this.recording = recording;
	}
	public boolean isBuffering() {
		return isBuffering;
	}
	public void setBuffering(boolean buffering) {
		this.isBuffering = buffering;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getStatusMessage() {
		return statusMessage;
	}
	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}
	public String getGenre() {
		return genre;
	}
	public void setGenre(String genre) {
		this.genre = genre;
	}
	public String getBitrate() {
		return bitrate;
	}
	public void setBitrate(String bitrate) {
		this.bitrate = bitrate;
	}
	public String getSite() {
		return site;
	}
	public void setSite(String site) {
		this.site = site;
	}
	public String getSaveFolder() {
		return saveFolder;
	}
	public void setSaveFolder(String saveFolder) {
		this.saveFolder = saveFolder;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public void setCurrentFile(String currentFile) {
		// TODO Auto-generated method stub
		this.currentFile = currentFile;
	}
	public String getCurrentFile ()
	{
		return currentFile ;
	}

	


	
}
