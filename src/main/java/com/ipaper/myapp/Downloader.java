package com.ipaper.myapp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.Callable;

import com.Ostermiller.util.CircularByteBuffer;

public abstract class Downloader implements Callable<CircularByteBuffer>{
	
	// Member variables
	/** The URL to download the file */
	protected URL mURL;
	
	
	
	/** Number of connections (threads) to download the file */
	protected int mNumConnections;
	

	
	/** The state of the download */
	protected int mState;
	
	/** downloaded size of the file (in bytes) */
	protected int mDownloaded;
	
	protected int mdownloadNum;
	
	//protected static List<CircularByteBuffer> cbbList = null;
	
	
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
	
	protected CircularByteBuffer cbb = null;
	
	
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
	
	
	
	
	protected Downloader(URL url) {
		mURL = url;
		mNumConnections = 1;
		
		mState = DOWNLOADING;
		mDownloaded = 0;
		mdownloadNum = 0;
	}
	
	
	protected Downloader(String url) {
		try {
			mURL = new URL(url);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		mNumConnections = 1;
		
		mState = DOWNLOADING;
		mDownloaded = 0;
		mdownloadNum = 0;
	}
	
	protected Downloader(String url, CircularByteBuffer cbb) {
		try {
			this.cbb = cbb;
			mURL = new URL(url);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		mNumConnections = 1;
		
		mState = DOWNLOADING;
		mDownloaded = 0;
		mdownloadNum = 0;
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


	public CircularByteBuffer getCbb() {
		return cbb;
	}


	public void setCbb(CircularByteBuffer cbb) {
		this.cbb = cbb;
	}



//	public static List<CircularByteBuffer> getCbbList() {
//		return cbbList;
//	}
//
//	public static void setCbbList(List<CircularByteBuffer> cbbList) {
//		Downloader.cbbList = cbbList;
//	}
	
	
	
	
}
