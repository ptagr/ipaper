package com.ipaper.myapp;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.Ostermiller.util.CircularByteBuffer;

public class HINDUHttpDownloader extends HttpDownloader {
	// private final static String username = "epaperDownloader%40live.com";
	// private final static String password = "download";
	private volatile static String myCookie = "";

	// public HTHttpDownloader(URL url, String outputFolder, int numConnections,
	// int downloadNum, ByteArrayInputStream[] baisList) {
	// super(url, outputFolder, numConnections, downloadNum, baisList);
	// }

	public HINDUHttpDownloader(URL url) {
		super(url);

	}
	
	public HINDUHttpDownloader(String url, CircularByteBuffer cbb) {
		super(url, cbb);

	}
	

	public HINDUHttpDownloader(String url) {
		super(url);

	}

	@Override
	public void exitConnect() {
		myCookie = "";
	}

	@Override
	public void initConnect() throws IOException {
		synchronized (HINDUHttpDownloader.class) {

			if (myCookie.length() > 0)
				return;
			System.out.println("MY COOKIE ZERO LEN");
			
			// // System.out.println("GETTING PDFs !!!! ");
			// String data = "__EVENTTARGET=submit&__EVENTARGUMENT=&txtUserID="
			// + username + "&txtPassword=" + password;

			URL url = new URL(
					"http://epaper.thehindu.com/index.php?rt=login/demologin&demo=1");

			HttpURLConnection uc = (HttpURLConnection) url.openConnection();
			
			uc.connect();
			
			Map<String, List<String>> headers = uc.getHeaderFields();
			Map<String, String> cookies = new HashMap<String, String>();
			
			
			List<String> values = headers.get("Set-Cookie");
			if(values.size() == 0){
				values = headers.get("set-cookie");
			}
			
			List<String> newValues = new ArrayList<String>();
			for (String value : values) {
				newValues.add(value.substring(0, value.indexOf(';')));
			}

			for (String token : newValues) {
				String[] keyValuePair = token.split("=");
				if (keyValuePair.length == 2)
					cookies.put(keyValuePair[0], keyValuePair[1]);
			}

			for (String s : cookies.keySet()) {
				System.out.println(s + ":" + cookies.get(s));
			}

			
			if (myCookie.length() == 0) {
				myCookie = "PHPSESSID="
						+ cookies.get("PHPSESSID");
				uc.setRequestProperty("Cookie", myCookie);
				//System.out.println(this.getClass().toString() + " : "+ myCookie);
			}

		}
	}

	@Override
	public HttpURLConnection customConnect(int sb, int eb) throws IOException {

		
		HttpURLConnection uc3 = (HttpURLConnection) mURL.openConnection();
		if (sb != -1 && eb != -1) {
			// set the range of byte to download
			String byteRange = sb + "-" + eb;
			uc3.setRequestProperty("Range", "bytes=" + byteRange);
		}
		
		uc3.setConnectTimeout(0);
		uc3.setRequestMethod("GET");
		uc3.setRequestProperty("Host", "epaper.thehindu.com");
		uc3.setRequestProperty("Proxy-Connection", "keep-alive");
		uc3.setRequestProperty(
				"User-Agent",
				"Opera/9.80 (Windows NT 6.1; U; Edition United Kingdom Local; en) Presto/2.9.168 Version/11.52");
				
		uc3.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

		if (myCookie.length() > 0) {
			uc3.setRequestProperty("Cookie", myCookie);
			System.out.println("Cookie set to "+myCookie);
		}
		
//		uc3.setRequestProperty("Content-Length", "0");
//		uc3.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
//		uc3.setRequestProperty("Content-Transfer-Encoding", "binary");
		
		
		uc3.connect();
		int contentLength = uc3.getContentLength();

		System.out.println("Content Length : " + contentLength);
		System.out.println("customConnect - " + mURL.toString());
		
		
		return uc3;
	}

}
