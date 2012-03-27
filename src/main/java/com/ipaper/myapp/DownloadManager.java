package com.ipaper.myapp;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;

public class DownloadManager {

	// The unique instance of this class
	// private static DownloadManager sInstance = null;

	// Constant variables
	private static final int DEFAULT_NUM_CONN_PER_DOWNLOAD = 8;
	public static final String DEFAULT_OUTPUT_FOLDER = "";

	// Member variables
	private int mNumConnPerDownload;
	private ArrayList<Downloader> mDownloadList;

	/** Protected constructor */
	public DownloadManager() {
		mNumConnPerDownload = DEFAULT_NUM_CONN_PER_DOWNLOAD;
		mDownloadList = new ArrayList<Downloader>();
	}

	/**
	 * Get the max. number of connections per download
	 */
	public int getNumConnPerDownload() {
		return mNumConnPerDownload;
	}

	/**
	 * Set the max number of connections per download
	 */
	public void SetNumConnPerDownload(int value) {
		mNumConnPerDownload = value;
	}

	/**
	 * Get the downloader object in the list
	 * 
	 * @param index
	 * @return
	 */
	public Downloader getDownload(int index) {
		return mDownloadList.get(index);
	}

	public void removeDownload(int index) {
		mDownloadList.remove(index);
	}

	/**
	 * Get the download list
	 * 
	 * @return
	 */
	public ArrayList<Downloader> getDownloadList() {
		return mDownloadList;
	}

	// public Downloader createDownload(URL verifiedURL, String outputFolder,
	// int downloadNum, ByteArrayInputStream[] bais) {
	// HttpDownloader fd = new HttpDownloader(verifiedURL, outputFolder,
	// mNumConnPerDownload, downloadNum, bais);
	// mDownloadList.add(fd);
	//
	// return fd;
	// }
	//
	// public Downloader createDownload(URL verifiedURL, String outputFolder,
	// int numConnPerDownload, int downloadNum, ByteArrayInputStream[] bais) {
	// HttpDownloader fd = new HttpDownloader(verifiedURL, outputFolder,
	// numConnPerDownload, downloadNum, bais);
	// mDownloadList.add(fd);
	//
	// return fd;
	// }

	public Downloader createDownload(URL verifiedURL, String outputFolder,
			int numConnPerDownload, int downloadNum,
			ByteArrayInputStream[] bais, String epaper) {
		HttpDownloader fd = null;
//		String cname = "com.ipaper.myapp." + epaper.toUpperCase()
//				+ "HttpDownloader";
//		Class cdef;
//		try {
//			cdef = Class.forName(cname);
//			Constructor cons = cdef.getConstructor(URL.class, String.class,
//					Integer.class, Integer.class, ByteArrayInputStream[].class);
//			fd = (HttpDownloader) cons.newInstance(verifiedURL, outputFolder,
//					numConnPerDownload, downloadNum, bais);
//		} catch (ClassNotFoundException e) {
//			fd = new HttpDownloader(verifiedURL, outputFolder,
//					numConnPerDownload, downloadNum, bais);
//		} catch (InstantiationException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IllegalAccessException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (SecurityException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (NoSuchMethodException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IllegalArgumentException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (InvocationTargetException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		if (epaper.equalsIgnoreCase("ht")) {
			fd = new HTHttpDownloader(verifiedURL, outputFolder,
					numConnPerDownload, downloadNum, bais);
		} else if (epaper.equalsIgnoreCase("mint")) {
			fd = new MINTHttpDownloader(verifiedURL, outputFolder,
					numConnPerDownload, downloadNum, bais);
		} else if (epaper.equalsIgnoreCase("hindu")) {
			fd = new HINDUHttpDownloader(verifiedURL, outputFolder,
					numConnPerDownload, downloadNum, bais);
		} else
			fd = new HttpDownloader(verifiedURL, outputFolder,
					numConnPerDownload, downloadNum, bais);

		mDownloadList.add(fd);

		return fd;
	}

	// /**
	// * Get the unique instance of this class
	// * @return the instance of this class
	// */
	// public static DownloadManager getInstance() {
	// if (sInstance == null)
	// sInstance = new DownloadManager();
	//
	// return sInstance;
	// }

	public boolean areDownloadsOver() {
		for (Downloader d : mDownloadList) {
			if (d.getState() == Downloader.DOWNLOADING)
				return false;
		}

		mDownloadList.get(0).exitConnect();

		return true;
	}

	/**
	 * Verify whether an URL is valid
	 * 
	 * @param fileURL
	 * @return the verified URL, null if invalid
	 */
	public static URL verifyURL(String fileURL) {
		// Only allow HTTP URLs.
		if (!fileURL.toLowerCase().startsWith("http://"))
			return null;

		// Verify format of URL.
		URL verifiedUrl = null;
		try {
			verifiedUrl = new URL(fileURL);
		} catch (Exception e) {
			return null;
		}

		// Make sure URL specifies a file.
		if (verifiedUrl.getFile().length() < 2)
			return null;

		return verifiedUrl;
	}

}
