package com.ipaper.myapp;

import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.Ostermiller.util.CircularByteBuffer;

public class HTHttpDownloader extends HttpDownloader {
	private final static String username = "epaperDownloader%40live.com";
	private final static String password = "download";
	private static String myCookie = "";

	// public HTHttpDownloader(URL url, String outputFolder, int numConnections,
	// int downloadNum, ByteArrayInputStream[] baisList) {
	// super(url, outputFolder, numConnections, downloadNum, baisList);
	// }

	public HTHttpDownloader(URL url, String outputFolder, int numConnections,
			int downloadNum) {
		super(url, outputFolder, numConnections, downloadNum);
	}
	
	public HTHttpDownloader(URL url, List<CircularByteBuffer> cbbList) {
		super(url, cbbList);
	}

	@Override
	public void exitConnect() {
		myCookie = "";
	}

	@Override
	public void initConnect() throws IOException {
		synchronized (HTHttpDownloader.class) {
			
			if (myCookie.length() > 0)
				return;
			System.out.println("MY COOKIE ZERO LEN");
			HttpURLConnection uc;
			URL url;
			Map<String, String> cookies = new HashMap<String, String>();
			// System.out.println("GETTING PDFs !!!! ");
			String data = "__VIEWSTATE=%2FwEPDwUKMTcwMTY5MjgyMA9kFgICAw9kFhICCQ8WAh4Dc3JjBTJQdWJsaWNhdGlvbnMvSFQvSEQvVG9kYXlzUGFnZS90b2RheXN0aHVtYmltYWdlLmpwZ2QCCw8WAh8ABTJQdWJsaWNhdGlvbnMvSFQvSE0vVG9kYXlzUGFnZS90b2RheXN0aHVtYmltYWdlLmpwZ2QCDQ8WAh8ABTJQdWJsaWNhdGlvbnMvSFQvSEMvVG9kYXlzUGFnZS90b2RheXN0aHVtYmltYWdlLmpwZ2QCFQ8WAh8ABTNQdWJsaWNhdGlvbnMvSFQvSEtML1RvZGF5c1BhZ2UvdG9kYXlzdGh1bWJpbWFnZS5qcGdkAhcPFgIfAAUyUHVibGljYXRpb25zL0hUL0hSL1RvZGF5c1BhZ2UvdG9kYXlzdGh1bWJpbWFnZS5qcGdkAhkPFgIfAAUyUHVibGljYXRpb25zL0hUL0hUL1RvZGF5c1BhZ2UvdG9kYXlzdGh1bWJpbWFnZS5qcGdkAiEPFgIfAAUyUHVibGljYXRpb25zL0hUL0hQL1RvZGF5c1BhZ2UvdG9kYXlzdGh1bWJpbWFnZS5qcGdkAiMPFgIfAAUyUHVibGljYXRpb25zL0hUL0hML1RvZGF5c1BhZ2UvdG9kYXlzdGh1bWJpbWFnZS5qcGdkAikPD2QWAh4Hb25jbGljawUVcmV0dXJuIFZhbGlkYXRpb25zKCk7ZBgBBR5fX0NvbnRyb2xzUmVxdWlyZVBvc3RCYWNrS2V5X18WCQUJcmJ0bkRlbGhpBQpyYnRuTXVtYmFpBQ1yYnRuQ2huYWRpZ2FyBQ1yYnRuSHRLb2xrYXRhBQxyYnRuSHRSYW5jaGkFC3JidG5IdERlbGhpBQtyYnRuSHRQYXRuYQUNcmJ0bkh0THVja05vdwUGc3VibWl0KAJoZDCoepzS0YZhlVSeOPtScWQ%3D&__EVENTVALIDATION=%2FwEWDAKXosjfBgLT8dy8BQK1qbSRCwKXlIzVCwKPqK62AwLC%2B4PcCAK6tIP0CALT3vqBDwKIqL62AwLytvadAwK41cOCDALcu4S2BHtP9bT4aPRyyAe7mfRqjzlt2QDw&"
					+ "txtUserID="
					+ username
					+ "&txtPassword="
					+ password
					+ "&txtpub=47&submit.x=0&submit.y=0";

			HttpURLConnection uc0;
			URL url0;

			url0 = new URL("http://epaper.hindustantimes.com/login.aspx");

			uc0 = (HttpURLConnection) url0.openConnection();
			uc0.setConnectTimeout(0);
			uc0.setInstanceFollowRedirects(false);
			uc0.setRequestMethod("GET");
			uc0.setRequestProperty("User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; rv:5.0) Gecko/20100101 Firefox/5.0");
			uc0.setRequestProperty("Accept",
					"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8	");
			uc0.setRequestProperty("Host", "epaper.hindustantimes.com");
			uc0.setRequestProperty(
					"Accept",
					"text/html, application/xml;q=0.9, application/xhtml+xml, image/png, image/webp, image/jpeg, image/gif, image/x-xbitmap, */*;q=0.1");
			uc0.setRequestProperty("Accept-Language", "en-US,en;q=0.9");

			uc0.setRequestProperty("Content-Length", "1222");
			uc0.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
			uc0.connect();

			Map<String, List<String>> headers = uc0.getHeaderFields();
			List<String> values = headers.get("Set-Cookie");
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

			url = new URL("http://epaper.hindustantimes.com/login.aspx");
			uc = (HttpURLConnection) url.openConnection();

			// uc.setRequestMethod("POST");
			uc.setDoInput(true);
			uc.setDoOutput(true);
			uc.setInstanceFollowRedirects(false);

			uc.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");

			uc.setRequestProperty("Accept-Language", "en-us,en;q=0.5");
			uc.setRequestProperty("Proxy-Connection", "keep-alive");
			uc.setRequestProperty("Referer",
					"http://epaper.hindustantimes.com/login.aspx");
			uc.setRequestProperty("Content-Length", "1222");
			if (myCookie.length() == 0) {

				myCookie = "ASP.NET_SessionId="
						+ cookies.get("ASP.NET_SessionId");
				uc.setRequestProperty("Cookie", myCookie);

			}
			// uc.connect();
			DataOutputStream printout = new DataOutputStream(
					uc.getOutputStream());

			data = "__VIEWSTATE=%2FwEPDwUKMTcwMTY5MjgyMA9kFgICAw9kFhICCQ8WAh4Dc3JjBTJQdWJsaWNhdGlvbnMvSFQvSEQvVG9kYXlzUGFnZS90b2RheXN0aHVtYmltYWdlLmpwZ2QCCw8WAh8ABTJQdWJsaWNhdGlvbnMvSFQvSE0vVG9kYXlzUGFnZS90b2RheXN0aHVtYmltYWdlLmpwZ2QCDQ8WAh8ABTJQdWJsaWNhdGlvbnMvSFQvSEMvVG9kYXlzUGFnZS90b2RheXN0aHVtYmltYWdlLmpwZ2QCFQ8WAh8ABTNQdWJsaWNhdGlvbnMvSFQvSEtML1RvZGF5c1BhZ2UvdG9kYXlzdGh1bWJpbWFnZS5qcGdkAhcPFgIfAAUyUHVibGljYXRpb25zL0hUL0hSL1RvZGF5c1BhZ2UvdG9kYXlzdGh1bWJpbWFnZS5qcGdkAhkPFgIfAAUyUHVibGljYXRpb25zL0hUL0hUL1RvZGF5c1BhZ2UvdG9kYXlzdGh1bWJpbWFnZS5qcGdkAiEPFgIfAAUyUHVibGljYXRpb25zL0hUL0hQL1RvZGF5c1BhZ2UvdG9kYXlzdGh1bWJpbWFnZS5qcGdkAiMPFgIfAAUyUHVibGljYXRpb25zL0hUL0hML1RvZGF5c1BhZ2UvdG9kYXlzdGh1bWJpbWFnZS5qcGdkAikPD2QWAh4Hb25jbGljawUVcmV0dXJuIFZhbGlkYXRpb25zKCk7ZBgBBR5fX0NvbnRyb2xzUmVxdWlyZVBvc3RCYWNrS2V5X18WCQUJcmJ0bkRlbGhpBQpyYnRuTXVtYmFpBQ1yYnRuQ2huYWRpZ2FyBQ1yYnRuSHRLb2xrYXRhBQxyYnRuSHRSYW5jaGkFC3JidG5IdERlbGhpBQtyYnRuSHRQYXRuYQUNcmJ0bkh0THVja05vdwUGc3VibWl0KAJoZDCoepzS0YZhlVSeOPtScWQ%3D&__EVENTVALIDATION=%2FwEWDAKXosjfBgLT8dy8BQK1qbSRCwKXlIzVCwKPqK62AwLC%2B4PcCAK6tIP0CALT3vqBDwKIqL62AwLytvadAwK41cOCDALcu4S2BHtP9bT4aPRyyAe7mfRqjzlt2QDw&txtUserID=epaperDownloader%40live.com&txtPassword=download&txtpub=47&submit.x=0&submit.y=0";

			printout.writeBytes(data);
			printout.flush();
			printout.close();

			uc.getInputStream().close();

			HttpURLConnection uc2;
			URL url2;

			url2 = new URL(
					"http://epaper.hindustantimes.com/ajax/EpaperLibrary.AjaxUtilsMethods,EpaperLibrary.ashx?_method=CheckUserLoginStatus&_session=rw");

			uc2 = (HttpURLConnection) url2.openConnection();
			uc2.setConnectTimeout(0);
			uc2.setRequestMethod("POST");
			uc2.setDoInput(true);
			uc2.setDoOutput(true);
			// uc2.setInstanceFollowRedirects(false);

			uc2.setRequestProperty(
					"User-Agent",
					"Opera/9.80 (Windows NT 6.1; U; Edition United Kingdom Local; en) Presto/2.9.168 Version/11.52");
			// uc2.setRequestProperty("Accept",
			// "text/html, application/xml;q=0.9, application/xhtml+xml, image/png, image/webp, image/jpeg, image/gif, image/x-xbitmap");
			uc2.setRequestProperty("Host", "epaper.hindustantimes.com");
			uc2.setRequestProperty("Accept-Language", "en-US,en;q=0.9");

			uc2.setRequestProperty("Referer",
					"http://epaper.hindustantimes.com/PUBLICATIONS/HT/HD/2011/11/14/index.shtml");
			if (myCookie.length() > 0) {

				uc2.setRequestProperty("Cookie", myCookie);

			}
			uc2.setRequestProperty("Content-Length", "0");
			uc2.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
			uc2.setRequestProperty("Content-Transfer-Encoding", "binary"); //
			uc2.connect();
			DataOutputStream printout2 = new DataOutputStream(
					uc2.getOutputStream());

			// printout2.writeBytes(data);
			printout2.flush();
			printout2.close();
			//
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
		uc3.setRequestProperty(
				"User-Agent",
				"Opera/9.80 (Windows NT 6.1; U; Edition United Kingdom Local; en) Presto/2.9.168 Version/11.52");

		uc3.setRequestProperty("Host", "epaper.hindustantimes.com");
		uc3.setRequestProperty("Accept-Language", "en-US,en;q=0.9");

		if (myCookie.length() > 0) {
			uc3.setRequestProperty("Cookie", myCookie);

		}
		uc3.setRequestProperty("Content-Length", "0");
		uc3.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
		uc3.setRequestProperty("Content-Transfer-Encoding", "binary");
		uc3.connect();
		int contentLength = uc3.getContentLength();
		//System.out.println("Content Length : " + contentLength);
		return uc3;
	}

}
