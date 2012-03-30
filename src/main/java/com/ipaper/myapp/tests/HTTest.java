package com.ipaper.myapp.tests;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.Ostermiller.util.CircularByteBuffer;
import com.ipaper.myapp.DownloadManager;
import com.ipaper.myapp.HINDUSTAN_TIMES;
import com.ipaper.myapp.HTHttpDownloader;
import com.ipaper.myapp.MyPDFUtility;
import com.ipaper.myapp.TOI;



public class HTTest {

	private static HINDUSTAN_TIMES ht;
	
	@BeforeClass
	public static void initialize(){
		ht = new HINDUSTAN_TIMES("ENGLISH","MUMBAI", new Date());
	}
//	@Test
//	public void test() {
//		List<String> urls = ht.buildPageUrls();
//		for(String url:urls){
//			System.out.println(url);
//		}
//	}
	
	@Test
	public void testDownloader() throws MalformedURLException{
		List<CircularByteBuffer> cbList = new ArrayList<CircularByteBuffer>();
		HTHttpDownloader htd = new HTHttpDownloader(new URL("http://epaper.hindustantimes.com/PUBLICATIONS/HT/HD/2011/11/14/PagePrint/14_11_2011_001.pdf"));
		
		
		//htd.run();
	}

	
	public static void main(String[] args) throws IOException {
		
		DownloadManager dm = DownloadManager.getInstance();

		List<CircularByteBuffer> cbList = new ArrayList<CircularByteBuffer>();
//		dm.createDownload(new URL("http://epaper.hindustantimes.com/PUBLICATIONS/HT/HM/2011/11/22/PagePrint/22_11_2011_013.pdf"),cbList, "ht");
//		dm.createDownload(new URL("http://epaper.hindustantimes.com/PUBLICATIONS/HT/HM/2011/11/22/PagePrint/22_11_2011_012.pdf"),cbList, "ht");
		//dm.createDownload(new URL("http://epaper.hindustantimes.com/PUBLICATIONS/HT/HM/2011/11/22/PagePrint/22_11_2011_011.pdf"),cbList, "ht");
		
		
		
		
//		dm.startAllDownloads();
//		
//		dm.waitForDownloads();
		
		//HTHttpDownloader htd = new HTHttpDownloader(new URL("http://epaper.hindustantimes.com/PUBLICATIONS/HT/HM/2011/11/22/PagePrint/22_11_2011_013.pdf"),cbList);
		
		//HTHttpDownloader htd2 = new HTHttpDownloader(new URL("http://epaper.hindustantimes.com/PUBLICATIONS/HT/HM/2011/11/22/PagePrint/22_11_2011_012.pdf"),cbList);
		
		System.out.println("ALL DOWNLOADS OVER");
		
		
		
		System.out.println(cbList.size());
		for(CircularByteBuffer cbb : cbList){
			System.out.println(cbb.getAvailable());
		}
		
		ByteArrayOutputStream _boutStream = new ByteArrayOutputStream();

		MyPDFUtility.concatPDFs(cbList, 2);
		
		System.out.println("PAPERS MERGED");
		
		
		//InputStream s = cbList.get(0).getInputStream();
	    String strFilePath = "/home/punitag/demo.pdf";

		FileOutputStream fos = new FileOutputStream(strFilePath);
		fos.write(_boutStream.toByteArray());
		
		
//		int numRead;
//		int totalnumRead = 0;
//		byte data[] = new byte[4096];
//		while ((numRead = s.read(data, 0, 4096))!=-1) {
//			// write to buffer
//			//numRead = s.read(data, 0, 4096);
//			totalnumRead+=numRead;
//			fos.write(data, 0, numRead);
//		}
//		System.out.println("totalnumRead : "+totalnumRead);
		
		
		fos.close();
	}
}
