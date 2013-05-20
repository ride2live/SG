package com.fallen.streamgetter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;

import com.fallen.streamgetterfree.R;
import com.google.android.vending.licensing.AESObfuscator;
import com.google.android.vending.licensing.LicenseChecker;
import com.google.android.vending.licensing.ServerManagedPolicy;

public class AsyncLoad extends AsyncTask<Boolean, StatusObj, StatusObj> implements CallBackCheck{

	String stringUrl;
	//int code;
	String [] currentState;
	AsyncLoadProgress alp;
	CallBackToServer cbts;
	int bufferMax = 100000;
	public boolean saveCurrent = false;
	private boolean metaIntFail = false;
	public boolean isBuffered = false;
	public boolean redirectNeed = false;
	public String currentStation;
	private int maxSaveCount;
	//public Resources res;
	private int filesEnum;
	//private StatusObj statusObject;
	String packageName;
	public Context mContext;
	public ContentResolver mContentResolver;
	public boolean cutTracks;
	Socket client;
	//private boolean connectedToSocked = false;
	OutputStream out;
	FileOutputStream tempOut;
	private ServerSocket serverSocket;
	InputStream is;
	StatusObj lastStatObj;
	FileOutputStream f;
	public int newCode =-1;
	public boolean needOpenSocket = false;
	public boolean newActionRecieved = false;
	public boolean canceled = false;
	public boolean recording = false;
	public boolean playing = false;
	public boolean wasConnected = false;
	String cancelMessage;
	String errorMessage;
	String connecting;
	String lowSpace;
	String play;
	String rec;
	String pr;
	String noTitle;
	private String limitExceed;
	private boolean fileLimitReached;
	private static String tempOutputFilename = "temp";
	public void setAlp(AsyncLoadProgress alp) {
		this.alp = alp;
	}
	
	public AsyncLoad(String urlBySelected, boolean playing, boolean recording, Resources resources, int maxSaveCount) {
		// TODO Auto-generated constructor stub
		this.stringUrl =urlBySelected;
		this.playing = playing;
		this.recording = recording;
		this.maxSaveCount = maxSaveCount;
		cancelMessage = resources.getString(R.string.statusStop);
		errorMessage = resources.getString(R.string.statusError);
		connecting= resources.getString(R.string.statusConnecting);
		lowSpace= resources.getString(R.string.lowSpace);
		play =resources.getString(R.string.statusPlay);
			rec  =resources.getString(R.string.statusRec);
			pr = resources.getString(R.string.statusPR);
			noTitle = resources.getString(R.string.noTitle);
			limitExceed = resources.getString(R.string.limitMessage);
		//res = resources;
	}

	@Override
	protected StatusObj doInBackground(Boolean... params) {
		// TODO Auto-generated method stub
		//System.out.println ("maxsavecount = " + maxSaveCount);
		String fileToPlay = "";	
		boolean lastRecording = false;
		boolean lastPlaying = false;
		wasConnected = false;
		canceled = false;
		//System.out.println ("start doInBackGround");
		//connectedToSocked = false;
		saveCurrent = Utils.isSaveCurrentOn(mContext);
		newActionRecieved = true;
		StatusObj statusObject = new StatusObj();
		if (serverSocket !=null)
		{
			try {
				serverSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		statusObject.setMaxCount(maxSaveCount); //added from free
		fileToPlay ="";
		filesEnum = 0;
		String folderTosave="";
		isBuffered=false;
		if (canceled)
		{
			closeSocketServer();
			closeAllStreams();
			return setStatusToCanceled(statusObject);
		}
		statusObject.setNotLicensed(false);
		statusObject.setStatus(Parameters.CODE_PUB_CONNECTING);
		statusObject.setStatusMessage(connecting);
		statusObject.setMaxCount(maxSaveCount);
		if (recording)
			statusObject.setRecording(true);
		//statusObject.setCurRecCount(-1);
		publishProgress(statusObject);
		int realSize = 0;
		int metLength = 0;
		int sumRealSize = 0;
		File file = null;
		is = null;
		f =  null;
		String mainMessage = "";
		int mainStatus=0;
		//int timesBuffered = 0;
		byte [] buffer = new byte [1024];
		int byteL = buffer.length;
		try {
			URL url = new URL (stringUrl);
			HttpURLConnection uc =  getUrlConnection (url);
			
			if (uc!=null) 
			{
				//System.out.println (uc.getResponseMessage());
				uc.connect();
				is = uc.getInputStream();
				
			}
			else
			{
				statusObject.setStatus(Parameters.CODE_PUB_ERROR);
				statusObject.setStatusMessage(errorMessage);
				return statusObject;
			}
			int metaInt = getMetInt(uc);
			String bitRate = getBitRate (uc);
			String stationURL = getStationURL(uc);
			//sending connect status
			statusObject.setStatus(Parameters.CODE_PUB_IDLE);
			wasConnected = true;
			//statusObject.setStatusMessage(mainMessage);
			statusObject.setBitrate(bitRate);
			statusObject.setSite(stationURL);
			publishProgress(statusObject);
			if (metaInt ==-1 || metaInt ==0)
			{
				metaIntFail = true;
			}
			else
			{
				metaIntFail = false;
				//System.out.println ("Metaint " +metaInt);
			}
			createFolders();
			String title = "";
			int bufferedBytes = 0;
			while (((realSize =is.read(buffer, 0, byteL ))!=-1) )
			{
				if (canceled)
				{
					//System.out.println ("!!!!!canceled in cycle!!!!!!!");
					closeSocketServer();
					closeAllStreams();
					statusObject = setStatusToCanceled(statusObject);
					return statusObject;
				}
				sumRealSize+=realSize;
				//System.out.println( sumRealSize +" rs="+ realSize + " canceled " +canceled);
				
				if (!metaIntFail && sumRealSize == metaInt)
				{
					metLength = is.read()*16;
					//System.out.println("metalenght = " + metLength);
				//	System.out.println("running. reach icy-lenght");
					
					if (metLength > 0) // new title incoming
					{
						
							if (!isFreeSpaceEnought())
							{
								statusObject.setStatusMessage(lowSpace);
								statusObject.setStatus(Parameters.CODE_PUB_STOPPED);
								statusObject.setRecording(false);
								publishProgress(statusObject);
								canceled = true;
								//System.out.println("is space enought = false");
								closeSocketServer();
								closeAllStreams();
								return statusObject;
							}
						int sumTitleLenght = 0;
						int sizeReaded = 0;
						byte[]metaBuffer = new byte [metLength];
						StringBuilder sb = new StringBuilder();
						while (sumTitleLenght < metLength)
						{
							sizeReaded = is.read(metaBuffer, 0, metLength);
							sumTitleLenght = sumTitleLenght +sizeReaded;
							//System.out.println ("sizeRead from meta = " + sizeReaded);
							sb.append(new String (metaBuffer).trim());
							metLength = metLength - sumTitleLenght;
						}
						
						if (recording)
						{
								
								//statusObject.setSaveFolder(fileToPlay);
								//System.out.println("recorded files = " + filesEnum+" max= " +maxSaveCount);
								if(filesEnum >= maxSaveCount && maxSaveCount!=0)
								{
									//canceled = true;
									statusObject.setStatus(Parameters.CODE_PUB_LIMIT_EXCEED);
									statusObject.setStatusMessage(limitExceed);
									statusObject.setRecording(false);
									
									publishProgress(statusObject);
									recording = false;
									if (!playing)
									{
										canceled = true;
										closeSocketServer();
										closeAllStreams();
										return statusObject;
									}
									else
									{
										fileLimitReached = true;
									}
									
								}
								else if (f!=null) 
								{
									statusObject.setStatus(Parameters.CODE_PUB_FILE_RECORDED);
									publishProgress(statusObject);
									statusObject.setCurRecCount(filesEnum);
								}
								filesEnum++;
								
								
						}
						else
						{
							statusObject.setRecording(false);
							
						}
						if (f!=null)
						{
							f.close();
							f = null;
							if (playing) 
							{
								if (!saveCurrent && !recording)
								{
									
									if (fileLimitReached) 
										fileLimitReached = false;
									else
										deleteFile(fileToPlay);
								}
								else
								{
									saveCurrent = false;
									statusObject.setStatus(Parameters.CODE_PUB_FILE_RECORDED);
									publishProgress(statusObject);
									
								}
							}
						}
						statusObject.setStatus(Parameters.CODE_PUB_IDLE);
						title = fixTitle(sb.toString());
						statusObject.setTitle(title);
						fileToPlay = createOutputFileByTitle(title);
						statusObject.setCurrentFile(fileToPlay);
						f = new FileOutputStream(fileToPlay);
						f.write(Utils.getID3Tags(title));
						/*if(filesEnum >= maxSaveCount && maxSaveCount!=0)
						{
							if (!isFreeSpaceEnought())
							{
								statusObject.setStatusMessage(res.getString(R.string.freeSpace));
								publishProgress(statusObject);
							}
							closeAllStreams();
							return true;
						}*/
					
					//System.out.println(title + "new title");
					//
				publishProgress(statusObject);

					}
					
					byteL = buffer.length;
					sumRealSize = 0;
				}
				if (newActionRecieved)
				{
					//checkMe();
					newActionRecieved=false;
					if (playing)
					{
						if (!lastPlaying)
						{
							
							//statusObject.setStatusMessage(play);
							//statusObject.setStatus(Parameters.CODE_PUB_PLAYING);
							if (out == null)
							{
									isBuffered = false;
									bufferedBytes =0;
									startSocket();
							}
							if (tempOut == null && !isBuffered)
							{
									tempOut=new FileOutputStream(Parameters.TEMP + tempOutputFilename+".mp3");
									//System.out.println(Parameters.TEMP + "temp.mp3");
							}
						}
						
					}
					else
					{
						if (out!=null)
						{
							out = null;
							//System.out.println("closing sockets, stopping proxy selfstreaming ");
							closeTempOutPut();
							closeSocketServer();
						}
					}
					
					if (recording)
					{
						statusObject.setRecording(true);
						if (!lastRecording && f !=null)
						{
							f.close();
							f = null;
							deleteFile(fileToPlay);
							f= new FileOutputStream(fileToPlay);
							//statusObject.setStatusMessage(rec);
							
						}
						
					}
					else
					{
						if (lastRecording)
						{
							//System.out.println ("Was recording, now !recording");
							statusObject.setStatus(Parameters.CODE_PUB_FILE_RECORDED);
							statusObject.setSaveFolder(fileToPlay);
							statusObject.setRecording(false);
							publishProgress(statusObject);
							statusObject.setStatus(Parameters.CODE_PUB_IDLE);
							fileToPlay = createOutputFileByTitle(title);
							f = new FileOutputStream(fileToPlay);
							//statusObject.setStatus(Parameters.CODE_PUB_PLAYING);
							
						}
					}
					//System.out.println("publish when title change " + statusObject.getTitle());
					publishProgress(statusObject);
					lastPlaying = playing;
					lastRecording = recording;
				}
				if (f!=null && !canceled)
					f.write(buffer, 0, realSize);
/*				else if (f == null && !canceled && !fileToPlay.equals(""))
				{
					startRecordNewFile(fileToPlay, statusObject, title);
				}*/
				if (out!=null && !canceled )
				{
				
					
					out.write(buffer, 0, realSize);
					out.flush();
					if (!isBuffered)
					{
						//timesBuffered++;
					 	//tempOutputFilename = "temp" + String.valueOf(timesBuffered);
						//closeTempOutPut();
						//tempOut=new FileOutputStream(Parameters.TEMP + tempOutputFilename+".mp3");
					 	//
						statusObject.isBuffering = true;
						bufferedBytes = bufferedBytes + realSize;
						//System.out.println (bufferedBytes);
						if (bufferedBytes > bufferMax)
						{
							isBuffered = true;
							statusObject.isBuffering = false;
							statusObject.setStatus(Parameters.CODE_PUB_BUFFERED);
							publishProgress(statusObject);
							statusObject.setStatus(Parameters.CODE_PUB_IDLE);
							bufferedBytes = 0;
							statusObject.setStatusMessage("");
							
						}
						
					}
					
					if (tempOut!=null)
						tempOut.write(buffer, 0, realSize);

				}
				if (sumRealSize + buffer.length >= metaInt && !metaIntFail)
				{
					byteL = metaInt - sumRealSize;
					//System.out.println ("next lenght = " +byteL);
				}
				//setStatus ();f
			}
			
		} catch (MalformedURLException e) {
			setStatusToFailed(statusObject);
			// TODO Auto-generated catch block
			/*statusObject.setStatus(Parameters.CODE_PUB_ERROR);
			statusObject.setStatusMessage(res.getString(R.string.statusError));
			publishProgress(statusObject);*/
			e.printStackTrace();
		} catch (IOException e) {
			setStatusToFailed(statusObject);
			// TODO Auto-generated catch block
			/*statusObject.setStatus(Parameters.CODE_PUB_ERROR);
			statusObject.setStatusMessage(res.getString(R.string.statusError));
			publishProgress(statusObject);*/
			e.printStackTrace();
		} 
		//System.out.println ("end stream download, lenght= " +realSize);
		/*statusObject.setStatus(Parameters.CODE_PUB_ERROR);
		statusObject.setStatusMessage(res.getString(R.string.statusError));
		publishProgress(statusObject);*/
		//publishProgress(statusObject);
		closeSocketServer();
		closeAllStreams();
		return statusObject;
	}

	private StatusObj setStatusToFailed(StatusObj statusObject) {
	// TODO Auto-generated method stub
		statusObject.setRecording(false);
		statusObject.setStatus(Parameters.CODE_PUB_ERROR);
		statusObject.setStatusMessage(errorMessage);
	return statusObject;
	}
	
		private StatusObj setStatusToCanceled(StatusObj statusObject) {
		// TODO Auto-generated method stub
			statusObject.setRecording(false);
			statusObject.setStatus(Parameters.CODE_PUB_STOPPED);
			statusObject.setStatusMessage(cancelMessage);
		return statusObject;
	}

		private void closeSocketServer() {
		// TODO Auto-generated method stub
			/*if (out!=null)
			{
				try {
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println ("set out to null");
				out = null;
			}*/
			if (client !=null && client.isConnected())
			{
				try {
					client.shutdownOutput();
					client.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		
			if (serverSocket!=null && !serverSocket.isClosed())
			{
			try {
				serverSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			client = null;
			out=null;
			serverSocket = null;

			}
			if (cbts !=null)
				cbts.onSocketClosed();
			
			
	}

		private void closeTempOutPut() {
		// TODO Auto-generated method stub
			if (tempOut!=null)
			{
			try {
				tempOut.close();
				//tempClosedByMe = true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			tempOut=null;
			}
	}


		private String createOutputFileByTitle(String title) {
		// TODO Auto-generated method stub
			String fileName = "";
			
			if (!cutTracks)
				fileName = currentStation +" " +noTitle;
			
			else if (title.length()<2)
				{
					fileName = currentStation + " " + title;
					//fileName = currentStation+String.valueOf(Math.round(Math.random()*10000));
				}
			
			else
				fileName = title;
			
			
			File checkFileExists = new File (Parameters.SAVE + fileName+".mp3");
			int fileIndex =0;
				while (checkFileExists.exists())
				{
					//System.out.println ("checkFileExists = " + checkFileExists.getName());
					fileIndex++;
					checkFileExists = new File (Parameters.SAVE + fileName+"("+String.valueOf(fileIndex)+").mp3");
				}
			String fileToPlay = Parameters.SAVE + checkFileExists.getName();
			//System.out.println ("constructed name = " + fileToPlay);
		return fileToPlay;
	}

		private void closeAllStreams() {
		// TODO Auto-generated method stub
			try {
				if (is != null)
					is.close();
				if (f!=null)
					f.close();
				if (out!=null)
					out.close();
				if (client!=null)
					client.close();
				if (serverSocket!=null)
					serverSocket.close();
				//System.out.println ("socket close");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	}

		private void startSocket() {
		// TODO Auto-generated method stub
		/*	try {
				tempOut = new FileOutputStream(Parameters.TEMP + "temp.mp3");
			} catch (FileNotFoundException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}*/
			//System.out.println ("create serv socket");
			try {
				serverSocket = new ServerSocket(8889);
				
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				//System.out.println ("unable to create serv socket");
				e1.printStackTrace();
				
			}
			

			new Thread(new Runnable() {
				
				@Override
				
				public void run() {
					// TODO Auto-generated method stub
					try {

					if (serverSocket == null || serverSocket.isClosed())
						return;
					StatusObj so = new StatusObj();
					so.setStatus(Parameters.CODE_PUB_SOCKET);
					publishProgress(so);
					client = serverSocket.accept();
					//client.setReceiveBufferSize(1024);
					//client.setSendBufferSize(1024);
					//System.out.println ("connected");
					String responseCode = ( "HTTP/1.1 200 OK\r\n");
					String cache = "Cache-Control: no-cache\r\n";
					//String contentLenght = "Content-Lenght: 100000\r\n";
					String contentType = ( "Content-Type: audio/mpeg\r\n\r\n");
				//	FileInputStream is = new FileInputStream(com.example.mp3toraw.Parameters.TEMP + "test");
					out = client.getOutputStream();
					out.write(responseCode.getBytes());
					out.write(cache.getBytes());
					//out.write(contentLenght.getBytes());
					out.write(contentType.getBytes());
					//System.out.println ("out inicialisated");
					//out.flush();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}).start();
	}

		private String getBitRate(URLConnection uc) {
		// TODO Auto-generated method stub
			String bitRateTemp="";
			try {
				bitRateTemp =uc.getHeaderField("icy-br"); 
				if (bitRateTemp == null)
					return "no bitrate";
				//System.out.println (bitRateTemp);
				}
			catch (Exception e) {
				// TODO: handle exception	
				//System.out.println ("no br");
				return "no bitrate";
			}
			return bitRateTemp;
	}
		
	private void createFolders() {
		// TODO Auto-generated method stub
		File dirs = new File (Parameters.TEMP);
		dirs.mkdirs();
		dirs = new File (Parameters.SAVE);
		dirs.mkdirs();
	}
	

	private int getMetInt(URLConnection uc) {
		// TODO Auto-generated method stub
		int metaInt=0;
		try {
			metaInt =Integer.parseInt(uc.getHeaderField("icy-metaint")); // how much byte I need to read before metadata block begin
			//System.out.println ("metaint " +metaInt);
			if (metaInt == 0)
				return -1;
			}
		catch (Exception e) {
			// TODO: handle exception	
			//System.out.println ("metadata infoheader exeption");
			return -1;
		}
		return metaInt;
	}
	private String getStationURL(URLConnection uc) {
		// TODO Auto-generated method stub
		 String stationURL =uc.getHeaderField("icy-url");
		 if (stationURL == null)
			 return "";
		 return stationURL;
	}

	private HttpURLConnection getUrlConnection(URL url) {
		// TODO Auto-generated method stub
		
		HttpURLConnection urlConnection = null;
		try {
			urlConnection = (HttpURLConnection)url.openConnection();
			//urlConnection.setUseCaches(true);
			urlConnection.addRequestProperty("Icy-MetaData", "1");	//Need it always, cause we gonna inform user about current title anyway
			//urlConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/536.6 (KHTML, like Gecko) Chrome/20.0.1092.0 Safari/536.6");
			//urlConnection.addRequestProperty("Connection", "keep-alive");
			//urlConnection.setAllowUserInteraction(false);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return urlConnection;
	}

	private String fixTitle(String title) {
		// TODO Auto-generated method stub
		if (title == null)
			return "NoTitle";
		int endIndex = title.indexOf(";");
		if (endIndex ==-1 || title.length()<2)
		{
			return "NoTitle";
		}
		else
		{
			title = title.substring(0, endIndex-1);
			for (int i =0;i<Parameters.unwantedSymbols.length; i++)
			 title = title.replace(Parameters.unwantedSymbols[i], "");
			 return title;
		}
		
	}
	
	
	
	@Override
	protected void onProgressUpdate(StatusObj... values) {
		// TODO Auto-generated method stub
	//	String filePath = "";
		if (values!=null)
			lastStatObj = values[0];
		else
			return;	
		if (canceled)
			return;
		lastStatObj = values[0];
		if (values[0].getStatus() == Parameters.CODE_PUB_SOCKET)
		{
			//System.out.println ("SOCKET CREATE CATCHED, run sec delay mpRec");
			new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				//System.out.println ("send callback after delay");
				if (cbts!=null)
					cbts.socketCreated();
			}
		}, 1000);
		}
		if (values[0].getStatus() == Parameters.CODE_PUB_BUFFERED)
		{
			if (cbts!=null)
			{
				//System.out.println ("send play temp, ON BUFFERED catched");
				cbts.playTemp(values[0].getCurrentFile());
			}
		}
		if (values[0].getStatus() == Parameters.CODE_PUB_FILE_RECORDED)
		{
			//System.out.println("file recoded status");
			if (cbts !=null && isFreeSpaceEnought())
			{
				Utils.setSaveCurrent(mContext, false);
				cbts.onFileRecorded(values[0].getCurrentFile());
			}
		}
		if (values[0].getStatus() == Parameters.CODE_PUB_LIMIT_EXCEED)
		{
			//System.out.println("limit exceed progupdate");
			if (cbts !=null)
				cbts.onLimitExceed();
		}
		StatusObj statusObjectToActivity = values[0];
		//System.out.println("status " +statusObjectToActivity.getStatus());
		//if (statusObjectToActivity.getStatus() == Parameters.CODE_PUB_PLAYING)
			//statusObjectToActivity.setSaveFolder(res.getString(R.string.justPlaying));
		if (statusObjectToActivity.getStatus() == Parameters.CODE_PUB_CONNECTED && cbts!=null)
			cbts.onConnected();
		if (statusObjectToActivity.getTitle().length()<2)
			statusObjectToActivity.setTitle("NoTitle");
		
		if (alp!=null)
		{		//System.out.println("on UPDATE in AT SEND TO GUI");
					alp.onProgressUpdate(statusObjectToActivity);
				if (statusObjectToActivity.isRecording() && cbts!=null)
						cbts.onAnyRecording();
		}
			////System.out.println("ALP = NULL!!!!");
		//alp.onProgressAsync(title, status, showLimit, filePath);*/
		super.onProgressUpdate(values);
	}
	@Override
	protected void onPostExecute(StatusObj statObj) {
		// TODO Auto-generated method stub
		lastStatObj = statObj;
		closeSocketServer();
		closeAllStreams();
		if (alp !=null)
			alp.onProgressUpdate(statObj);
		if (cbts == null)
			return;
		if ((saveCurrent || recording) && wasConnected)
			cbts.onFileRecorded(statObj.getCurrentFile());
		else
			deleteFile(statObj.getCurrentFile());
		if (canceled)
		{
			//System.out.println("postExecute called, result cancel");
			//System.out.println("canceled " + statObj.getStatusMessage());
			cbts.onCanceled(statObj);
		}
		else
		{
			//System.out.println("postExecute called, result fail");
			cbts.onAnyFail(statObj);
		}
		
		//if (alp!=null)
			//alp.onProgressUpdate(statusObject);
				//alp.onProgressAsync(title, status, limitLeft, "");
		super.onPostExecute(statObj);
	}

	private void deleteFile(String currentFile) {
		// TODO Auto-generated method stub
		File fileToDel = new File(currentFile);
		fileToDel.delete();
	}

	/*public StatusObj getStatusObject() {
		// TODO Auto-generated method stub
		return lastStatObj;                                                                                               
	}*/
	private boolean isFreeSpaceEnought() {
		// TODO Auto-generated method stub
		StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
		double sdAvailSize = (double)stat.getAvailableBlocks()
		                   * (double)stat.getBlockSize();
		int megaAvailable = (int)(sdAvailSize / 1048576); // 1024*1024
		//System.out.println(megaAvailable +" ********!!!!!#$%$^$R");
		//freeSpace.setText(String.valueOf(megaAvailable) + " Mbyte");
		if (megaAvailable >100)
		return true;
		else
		return false;	
	}
	
	 void checkMe()
	    {
	    	MyLicenseCheckerCallback checkerCallback = new MyLicenseCheckerCallback(this);
	    	LicenseChecker mChecker = new LicenseChecker(mContext, new ServerManagedPolicy(mContext,new AESObfuscator(Parameters.SALT, packageName, android.provider.Settings.Secure.getString(mContentResolver, android.provider.Settings.Secure.ANDROID_ID))),Parameters.BASE64_PUBLIC_KEY );
	    	mChecker.checkAccess(checkerCallback);
	    }

	@Override
	public void onLicenseChecked(int reason) {
		// TODO Auto-generated method stub
		if (Utils.isPiracy(mContext))
		{
			StatusObj sto = new StatusObj();
			sto.setNotLicensed(true);
			publishProgress(sto);
			//System.out.println ("piracy");
			//publishProgress(values)
			//statusObject.setNotLicensed(true);
		}
	}

	public void mpBuffered() {
		// TODO Auto-generated method stub
		isBuffered = true;
		//closeTempOutPut();
	}

	public void setCurrentSave(boolean isSaveCurrent) {
		// TODO Auto-generated method stub
		saveCurrent = isSaveCurrent;
	}
	


}
