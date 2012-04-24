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
import com.ipaper.myapp.EpaperBase;
import com.ipaper.myapp.EpaperFactory;
import com.ipaper.myapp.HINDUSTAN_TIMES;
import com.ipaper.myapp.HTHttpDownloader;
import com.ipaper.myapp.Language;
import com.ipaper.myapp.MyPDFUtility;
import com.ipaper.myapp.TOI;
import com.ipaper.myapp.Time;

public class HTTest {

	private static HINDUSTAN_TIMES ht;

	@BeforeClass
	public static void initialize() {
		ht = new HINDUSTAN_TIMES("ENGLISH", "MUMBAI", new Date());
	}

	// @Test
	// public void test() {
	// List<String> urls = ht.buildPageUrls();
	// for(String url:urls){
	// System.out.println(url);
	// }
	// }

	@Test
	public void testDownloader() throws MalformedURLException {
		List<CircularByteBuffer> cbList = new ArrayList<CircularByteBuffer>();
		HTHttpDownloader htd = new HTHttpDownloader(
				new URL(
						"http://epaper.hindustantimes.com/PUBLICATIONS/HT/HD/2011/11/14/PagePrint/14_11_2011_001.pdf"));

		// htd.run();
	}

	public static void main(String[] args) throws IOException {

		EpaperBase paper = new EpaperFactory().epaper("HINDUSTAN_TIMES");
		Date date = Time.addandReturnDate(-1);
		System.out.println(date);
		paper.setCity("MUMBAI");
		paper.setDate(date);
		paper.setLang(Language.valueOf("ENGLISH"));
		paper.setId();
		paper.buildPageUrls();
		byte[] paperpdf = DownloadManager.getInstance().download(paper);

		String strFilePath = "/home/punitag/demo.pdf";

		FileOutputStream fos = new FileOutputStream(strFilePath);
		fos.write(paperpdf);
		fos.flush();

		fos.close();
	}
}
