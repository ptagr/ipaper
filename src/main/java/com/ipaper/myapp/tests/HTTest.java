package com.ipaper.myapp.tests;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.ipaper.myapp.HINDUSTAN_TIMES;
import com.ipaper.myapp.HTHttpDownloader;
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
		ByteArrayInputStream[] baisList  = new ByteArrayInputStream[1];
		HTHttpDownloader htd = new HTHttpDownloader(new URL("http://epaper.hindustantimes.com/PUBLICATIONS/HT/HD/2011/11/14/PagePrint/14_11_2011_001.pdf"), 
				null, 1, 1, baisList);
		
		
		//htd.run();
	}

	
	public static void main(String[] args) throws MalformedURLException {
		ByteArrayInputStream[] baisList  = new ByteArrayInputStream[1];
		HTHttpDownloader htd = new HTHttpDownloader(new URL("http://epaper.hindustantimes.com/PUBLICATIONS/HT/HM/2011/11/22/PagePrint/22_11_2011_013.pdf"), 
				null, 1, 1, baisList);
		
		HTHttpDownloader htd2 = new HTHttpDownloader(new URL("http://epaper.hindustantimes.com/PUBLICATIONS/HT/HM/2011/11/22/PagePrint/22_11_2011_012.pdf"), 
				null, 1, 1, baisList);
		
	}
}
