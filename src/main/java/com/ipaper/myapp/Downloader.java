package com.ipaper.myapp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Observable;

import com.Ostermiller.util.CircularByteBuffer;

public abstract class Downloader extends Thread{
	
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
	
	protected List<CircularByteBuffer> cbbList = null;
	
	

	
	// Contants for block and buffer size
	protected static final int BLOCK_SIZE = 4096;
	protected static final int BUFFER_SIZE = 4096;
	//protected static final int MIN_DOWNLOAD_SIZE = BLOCK_SIZE * 100;
	protected static final int MIN_DOWNLOAD_SIZE = Integer.MAX_VALUE;
	
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
	
	
	protected Downloader(URL url, String outputFolder, int numConnections, int downloadNum) {
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
	}
	
	protected Downloader(URL url, List<CircularByteBuffer> cbbList) {
		mURL = url;
		mOutputFolder = "";
		mNumConnections = 1;
		
		// Get the file name from url path
		String fileURL = url.getFile();
		mFileName = fileURL.substring(fileURL.lastIndexOf('/') + 1);
//		/System.out.println("File name: " + mFileName);
		mFileSize = -1;
		mState = DOWNLOADING;
		mDownloaded = 0;
		mdownloadNum = 0;
		this.cbbList= cbbList;
	}
	
	/**
	 * Pause the downloader
	 */
	public void pause() {
		setState(PAUSED);
	}
	
//	/**
//	 * Resume the downloader
//	 */
//	public void resume() {
//		setState(DOWNLOADING);
//		download();
//	}
	
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
	public int getDownloadState() {
		return mState;
	}
	
	/**
	 * Set the state of the downloader
	 */
	protected void setState(int value) {
		mState = value;
		
	}
	
//	/**
//	 * Start or resume download
//	 */
//	protected void download() {
//		Thread t = new Thread(this);
//		t.start();
//	}
	
	
	
	
	
	
	public void initConnect() throws  IOException{
		return;
	}
	
	public void exitConnect(){
		return;
	}

	public List<CircularByteBuffer> getCbbList() {
		return cbbList;
	}

	public void setCbbList(List<CircularByteBuffer> cbbList) {
		this.cbbList = cbbList;
	}
	
	
	
	
}
