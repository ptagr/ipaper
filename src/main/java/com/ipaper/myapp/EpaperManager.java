//package com.ipaper.myapp;
//
//import java.util.concurrent.Callable;
//import com.dropbox.client.DropboxAPI;
//
//public class EpaperManager implements Runnable {
//	byte[] content;
//	String fileName;
//	String folder;
//
//	public EpaperManager(byte[] content, String fileName, String fodler) {
//		super();
//		this.content = content;
//		this.fileName = fileName;
//		this.folder = fodler;
//	}
//
//	public String persistNewspaper() {
//
//		try {
//
//			DropboxSample ds = DropboxSample.getInstance();
//			DropboxAPI.Account account = ds.getAccountInfo();
//			String uploadedUrl = "http://dl.dropbox.com/u/" + account.uid
//					+ "/epaper" + folder + "/" + fileName;
//
//			ds.getAPI().putFile("dropbox", "/Public/epaper" + folder, content,
//					fileName);
//			return uploadedUrl;
//		} finally {
//			content = null;
//		}
//	}
//
//	public void run() {
//		String url = persistNewspaper();
//		//EpaperList.getInstance().addEpaperToList(fileName, url);
//		
//		
//		// return url;
//	}
//}
