package com.ipaper.myapp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public abstract class Downloader extends Observable implements Runnable{
	
	// Member variables
	/** The URL to download the file */
	protected URL mURL;
	
	/** Output folder for downloaded file */
	protected String mOutputFolder;
	
	/** Number of connections (threads) to download the file */
	protected int mNumConnections;
	
	/** The file name, extracted from URL */
	protected String mFileName;
	
	/** Size of the downloaded file (in bytes) */
	protected int mFileSize;
	
	/** The state of the download */
	protected int mState;
	
	/** downloaded size of the file (in bytes) */
	protected int mDownloaded;
	
	protected int mdownloadNum;
	
	protected ByteArrayInputStream[] mbaisList;
	
	
	/** List of download threads */
	protected ArrayList<DownloadThread> mListDownloadThread;
	
	// Contants for block and buffer size
	protected static final int BLOCK_SIZE = 4096;
	protected static final int BUFFER_SIZE = 4096;
	protected static final int MIN_DOWNLOAD_SIZE = BLOCK_SIZE * 100;
	
	// These are the status names.
    public static final String STATUSES[] = {"Downloading",
    				"Paused", "Complete", "Cancelled", "Error"};
	
	// Contants for download's state
	public static final int DOWNLOADING = 0;
	public static final int PAUSED = 1;
	public static final int COMPLETED = 2;
	public static final int CANCELLED = 3;
	public static final int ERROR = 4;
	
	/**
	 * Constructor
	 * @param fileURL
	 * @param outputFolder
	 * @param numConnections
	 */
//	protected Downloader(URL url, String outputFolder, int numConnections, int downloadNum, ByteArrayInputStream[] baisList) {
//		this(url,outputFolder,numConnections,downloadNum,baisList,false);
//		
//	}
	
	
	protected Downloader(URL url, String outputFolder, int numConnections, int downloadNum, ByteArrayInputStream[] baisList) {
		mURL = url;
		mOutputFolder = outputFolder;
		mNumConnections = numConnections;
		
		// Get the file name from url path
		String fileURL = url.getFile();
		mFileName = fileURL.substring(fileURL.lastIndexOf('/') + 1);
//		/System.out.println("File name: " + mFileName);
		mFileSize = -1;
		mState = DOWNLOADING;
		mDownloaded = 0;
		mdownloadNum = downloadNum;
		mbaisList = baisList;
		mListDownloadThread = new ArrayList<DownloadThread>();
	}
	
	/**
	 * Pause the downloader
	 */
	public void pause() {
		setState(PAUSED);
	}
	
	/**
	 * Resume the downloader
	 */
	public void resume() {
		setState(DOWNLOADING);
		download();
	}
	
	/**
	 * Cancel the downloader
	 */
	public void cancel() {
		setState(CANCELLED);
	}
	
	/**
	 * Get the URL (in String)
	 */
	public String getURL() {
		return mURL.toString();
	}
	
	/**
	 * Get the downloaded file's size
	 */
	public int getFileSize() {
		return mFileSize;
	}
	
	/**
	 * Get the current progress of the download
	 */
	public float getProgress() {
		return ((float)mDownloaded / mFileSize) * 100;
	}
	
	/**
	 * Get current state of the downloader
	 */
	public int getState() {
		return mState;
	}
	
	/**
	 * Set the state of the downloader
	 */
	protected void setState(int value) {
		mState = value;
		stateChanged();
	}
	
	/**
	 * Start or resume download
	 */
	protected void download() {
		Thread t = new Thread(this);
		t.start();
	}
	
	/**
	 * Increase the downloaded size
	 */
	protected synchronized void downloaded(int value) {
		mDownloaded += value;
		stateChanged();
	}
	
	/**
	 * Set the state has changed and notify the observers
	 */
	protected void stateChanged() {
		setChanged();
		notifyObservers();
	}
	
	
	public void initConnect() throws  IOException{
		return;
	}
	
	public void exitConnect(){
		return;
	}
	
	/**
	 * Thread to download part of a file
	 */
	protected abstract class DownloadThread implements Runnable {
		protected int mThreadID;
		protected URL mURL;
		protected String mOutputFile;
		protected int mStartByte;
		protected int mEndByte;
		protected boolean mIsFinished;
		protected Thread mThread;
		protected ByteArrayOutputStream[] mbaosList;
		protected int mPartNum;
		
		public DownloadThread(int threadID, URL url, String outputFile, int startByte, int endByte, ByteArrayOutputStream[] _baosList, int partNum) {
			mThreadID = threadID;
			mURL = url;
			mOutputFile = outputFile;
			mStartByte = startByte;
			mEndByte = endByte;
			mIsFinished = false;
			mbaosList = _baosList;
			mPartNum = partNum;
			download();
		}
		
		/**
		 * Get whether the thread is finished download the part of file
		 */
		public boolean isFinished() {
			return mIsFinished;
		}
		
		/**
		 * Start or resume the download
		 */
		public void download() {
			mThread = new Thread(this);
			mThread.start();
		}
		
		/**
		 * Waiting for the thread to finish
		 * @throws InterruptedException
		 */
		public void waitFinish() throws InterruptedException {
			mThread.join();			
		}
		
	}
}
