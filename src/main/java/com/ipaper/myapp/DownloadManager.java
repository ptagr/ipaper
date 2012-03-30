package com.ipaper.myapp;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


import com.Ostermiller.util.CircularByteBuffer;

public class DownloadManager {

	// The unique instance of this class
	// private static DownloadManager sInstance = null;

	//private static Logger logger = Logger.getLogger(DownloadManager.class);
	
	// Constant variables
	private static final int DEFAULT_NUM_CONN_PER_DOWNLOAD = 1;
	private static final int MAX_PAGES = 30;
	public static final String DEFAULT_OUTPUT_FOLDER = "";

	private static DownloadManager dmInstance = null;

	protected ExecutorService executorService = Executors
			.newFixedThreadPool(MAX_PAGES);
	
	protected List<CircularByteBuffer> cbbList = new ArrayList<CircularByteBuffer>();
	
	//protected EpaperBase epaper = null;

	// Member variables
	private int mNumConnPerDownload;
	//private ArrayList<Downloader> mDownloadList;

	public static DownloadManager getInstance() {
		if (dmInstance == null) {
			dmInstance = new DownloadManager();
			
			//dmInstance.initializeDM();
		}
		return dmInstance;
	}

	/** Protected constructor */
	private DownloadManager() {
		mNumConnPerDownload = DEFAULT_NUM_CONN_PER_DOWNLOAD;
		for (int i = 0; i < MAX_PAGES; i++) {
			cbbList.add(new CircularByteBuffer(CircularByteBuffer.INFINITE_SIZE));
		}
		
		//mDownloadList = new ArrayList<Downloader>();
	}

	public byte[] download(EpaperBase epaper) {

		
		
		List<Future<CircularByteBuffer>> cbbFutures = new ArrayList<Future<CircularByteBuffer>>();
		
		//CLear the cbbList
		for(CircularByteBuffer cbb : cbbList){
			cbb.clear();
		}
		
		// Create all the downloads
		for (int i = 0; i < epaper.getUrls().size(); i++) {
			HttpDownloader fd = null;
			String epapername = epaper.getName();
			String url = epaper.getUrls().get(i);
			if (epapername.equalsIgnoreCase("ht")) {
				fd = new HTHttpDownloader(url, cbbList.get(i));
			} else if (epapername.equalsIgnoreCase("mint")) {
				fd = new MINTHttpDownloader(url, cbbList.get(i));
			} else if (epapername.equalsIgnoreCase("hindu")) {
				fd = new HINDUHttpDownloader(url, cbbList.get(i));
			} else
				fd = new HttpDownloader(url, cbbList.get(i));

			
			
			//mDownloadList.add(fd);
			cbbFutures.add(executorService.submit(fd));

		}
		
		int pages = 0;
		
		//Wait for downloads to finish in order
		for(Future<CircularByteBuffer> f : cbbFutures){
			try {
//				CircularByteBuffer cbb = f.get();
//				if(cbb != null)
//					cbbList.add(cbb);
				if(f.get() != null)
					pages++;
			} catch (InterruptedException e) {
				System.out.println("exception while waiting for future");
				e.printStackTrace();
			} catch (ExecutionException e) {
				System.out.println("exception while waiting for future");
				e.printStackTrace();
			}
		}
		
		System.out.println("no of pages : " + pages);
		epaper.setPageCount(pages);
		
		return mergeCBBPDFs(cbbList, pages);

		// return fd;
	}
	
	public byte[] mergeCBBPDFs(List<CircularByteBuffer> cbbList, int pages) {
		if (cbbList.isEmpty()) {
			return new byte[0];
		} else {

			// System.out.println("MERGING PDFs");

			return MyPDFUtility.concatPDFs(cbbList, pages);
		}
	}

//	public void initializeDM() {
//
//		// Initialize the cbb list
		
//
//	}
//
//	public void refreshDM() {
//		for (CircularByteBuffer cbb : cbbList) {
//			cbb.clear();
//		}
//	}

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

//	/**
//	 * Get the downloader object in the list
//	 * 
//	 * @param index
//	 * @return
//	 */
//	public Downloader getDownload(int index) {
//		return mDownloadList.get(index);
//	}
//
//	public void removeDownload(int index) {
//		mDownloadList.remove(index);
//	}

//	/**
//	 * Get the download list
//	 * 
//	 * @return
//	 */
//	public ArrayList<Downloader> getDownloadList() {
//		return mDownloadList;
//	}

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

	private void createDownload(URL verifiedURL,
			List<CircularByteBuffer> cbbList, String epaper) {
		HttpDownloader fd = null;
		// String cname = "com.ipaper.myapp." + epaper.toUpperCase()
		// + "HttpDownloader";
		// Class cdef;
		// try {
		// cdef = Class.forName(cname);
		// Constructor cons = cdef.getConstructor(URL.class, String.class,
		// Integer.class, Integer.class, ByteArrayInputStream[].class);
		// fd = (HttpDownloader) cons.newInstance(verifiedURL, outputFolder,
		// numConnPerDownload, downloadNum, bais);
		// } catch (ClassNotFoundException e) {
		// fd = new HttpDownloader(verifiedURL, outputFolder,
		// numConnPerDownload, downloadNum, bais);
		// } catch (InstantiationException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (IllegalAccessException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (SecurityException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (NoSuchMethodException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (IllegalArgumentException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (InvocationTargetException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		if (epaper.equalsIgnoreCase("ht")) {
			fd = new HTHttpDownloader(verifiedURL);
		} else if (epaper.equalsIgnoreCase("mint")) {
			fd = new MINTHttpDownloader(verifiedURL);
		} else if (epaper.equalsIgnoreCase("hindu")) {
			fd = new HINDUHttpDownloader(verifiedURL);
		} else
			fd = new HttpDownloader(verifiedURL);

//		/mDownloadList.add(fd);

		// return fd;
	}

	// public void startAllDownloads(){
	// for(Downloader d : mDownloadList){
	// d.start();
	// }
	// }
	//
	// public void waitForDownloads(){
	// for(Downloader d : mDownloadList){
	// try {
	// d.join();
	// } catch (InterruptedException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	//
	// //Remove all the download threads
	// mDownloadList.clear();
	//
	// }

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

	// public boolean areDownloadsOver() {
	// for (Downloader d : mDownloadList) {
	// if (d.getDownloadState() != d.COMPLETED)
	// return false;
	// }
	//
	// mDownloadList.get(0).exitConnect();
	//
	// return true;
	// }

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

//	public EpaperBase getEpaper() {
//		return epaper;
//	}
//
//	public void setEpaper(EpaperBase epaper) {
//		this.epaper = epaper;
//	}
	
	

}
