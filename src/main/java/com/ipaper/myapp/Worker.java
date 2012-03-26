package com.ipaper.myapp;



import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * 
 * @author Puneet
 */
public class Worker {

	private final String localStr;
	private final String paper;
	private final int id;
	private byte[] buf = null;
	private Map<String, String> cookies = null;

	public Worker(String localstr, int id) {
		this.localStr = localstr;
		this.id = id;
		this.paper = null;
	}

	public Worker(String localstr, int id, String paper) {
		this.localStr = localstr;
		this.id = id;
		this.paper = paper;
	}

	public Worker(String localstr, int id, String paper,
			Map<String, String> cookies) {
		this.localStr = localstr;
		this.id = id;
		this.paper = paper;
		this.cookies = cookies;
	}

	public ByteArrayInputStream call() throws IOException {

		InputStream in = null;
		int contentLength = 0;
		URL url = null;
		HttpURLConnection uc = null;
		try {

			url = new URL(localStr);
			uc = (HttpURLConnection) url.openConnection();
			uc.setConnectTimeout(0);
			uc.setRequestMethod("GET");
			if (paper != null && paper.equalsIgnoreCase("MINT")) {
				if (cookies != null) {

					String myCookie = "ASP.NET_SessionId="
							+ cookies.get("ASP.NET_SessionId")
							+ "; IsUserReg=True";
					uc.setRequestProperty("Cookie", myCookie);

				}
				uc.setRequestProperty("Referer",
						"http://epaper.livemint.com/Default.aspx?BMode=100");
				uc.setRequestProperty(
						"User-Agent",
						"Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US; rv:1.9.2.12) Gecko/20101026 AskTbTRL2/3.11.3.15590 Firefox/3.6.12");
			}else if (paper != null && paper.equalsIgnoreCase("HT")) {
				if (cookies != null) {

					String myCookie = "ASP.NET_SessionId="
							+ cookies.get("ASP.NET_SessionId");
					uc.setRequestProperty("Cookie", myCookie);

				}
				
//				uc.setRequestProperty("Host", "epaper.hindustantimes.com");
//				uc.setRequestProperty("Connection", "keep-alive");
//				uc.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
//				uc.setRequestProperty("Accept-Encoding", "gzip,deflate,sdch");
//				uc.setRequestProperty("Accept-Language", "en-US,en;q=0.8");
//				uc.setRequestProperty("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.3");
				uc.setRequestProperty("Referer",
						url.toString().substring(0, url.toString().indexOf("PagePrint"))+"index.shtml");
				uc.setRequestProperty(
						"User-Agent",
						"Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US; rv:1.9.2.12) Gecko/20101026 AskTbTRL2/3.11.3.15590 Firefox/3.6.12");
			} 
			
			uc.connect();
			contentLength = uc.getContentLength();
			
			if(uc.getResponseCode() != 404 && contentLength > 40960){ // greater than 40 kb
				
				System.out.println("url :" + localStr+ "contentLength : "+contentLength);

				

				in = new BufferedInputStream(uc.getInputStream());
				buf = new byte[contentLength];
				int bytesRead = 0;
				int offset = 0;
				while (offset < contentLength) {
					bytesRead = in.read(buf, offset, buf.length - offset);

					if (bytesRead == -1) {
						break;
					}
					offset += bytesRead;
				}
				in.close();
				return new ByteArrayInputStream(buf);
			}else{
				return null;
			}

		} catch (IOException ex) {
			System.out.println("Exception thrown : " + localStr);
			return null;
		} 
	}
}

