//package com.ipaper.myapp;
//
//import java.util.Iterator;
//import java.util.concurrent.ConcurrentHashMap;
//
//import com.dropbox.DropboxSample;
//import com.dropbox.client.DropboxAPI;
//import com.dropbox.client.DropboxAPI.Entry;
//
//public class CopyOfDeleteEpaperTask extends Thread{
//	private static ConcurrentHashMap<String, CopyOfDeleteEpaperTask> currentTasks = new ConcurrentHashMap<String, CopyOfDeleteEpaperTask>();
//	private volatile int timer = 30*60;  //600 seconds
//	private String file;
//	private String folder;
//	
//	private CopyOfDeleteEpaperTask(String folder, String file){
//		this.folder = folder;
//		this.file = file;
//	}
//	
//	public static void deleteEpaper(String folder, String file){
//		for(String key : currentTasks.keySet()){
//			CopyOfDeleteEpaperTask d = currentTasks.get(key);
//			if(d.timerExpired())
//				currentTasks.remove(key);
//		}
//		if(currentTasks.containsKey(file.toUpperCase())){
//			currentTasks.get(file.toUpperCase()).refreshTimer();
//		}else{
//			CopyOfDeleteEpaperTask det = new CopyOfDeleteEpaperTask(folder, file);
//			currentTasks.put(file.toUpperCase(), det);
//			det.start();
//		}
//	}
//	
//	
//	public static void deleteEpaperFolder(String folder1, String folder2){
//		DropboxSample ds = DropboxSample.getInstance();
//		DropboxAPI.Account account = ds.getAccountInfo();
//		System.out.print("Delele folder - "+ folder2+"  ACCOUNT INFO : " + account.email); 
//		ds.getAPI().fileDelete(folder1, folder2, "");
//		
//		
//		
//	}
//	
//	private void refreshTimer(){
//		timer = 30*60;
//	}
//
//	public void run() {
//		System.out.print("Thread going to sleep");
//		while(timer > 0){
//			try {
//				Thread.sleep(5000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			timer-=5;
//		}
//		
//		DropboxSample ds = DropboxSample.getInstance();
//		DropboxAPI.Account account = ds.getAccountInfo();
//		System.out.print("Delele file - "+ file + " ACCOUNT INFO : " + account.email);  
//		ds.getAPI().fileDelete(folder, file, "");
//		return;
//
//	}
//	
//	public boolean timerExpired(){
//		return timer <= 5;
//	}
//}
