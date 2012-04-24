package com.ipaper.myapp;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class INDIANEXPRESS extends EpaperBase {

	private static Map<String, String> cs = new HashMap<String, String>();

	public INDIANEXPRESS() {
		this(null, null, null);
	}

	public INDIANEXPRESS(String lang, String city, Date date) {
		super(lang, city, date);
		name = "IEXPRESS";
		description = "Indian Express";

		citymap = new HashMap<Language, Set<String>>();
		populateCSMap();
		citymap.put(Language.ENGLISH, cs.keySet());
		pageCount = 30;
		expiryTimeInterval = 30 * 60; // 30 minutes
		maxNumPerConn = 1;
		id = buildFileName();
		
	}

	void populateCSMap() {
		cs.put("DELHI",
				"http://epaper.indianexpress.com/t/226/latest/Indian-Express");
		cs.put("CHANDIGARH",
				"http://epaper.indianexpress.com/t/271/latest/Chandigarh");
		cs.put("KOLKATA",
				"http://epaper.indianexpress.com/t/336/latest/Kolkata");
		cs.put("PUNE",
				"http://epaper.indianexpress.com/t/266/latest/Indian-Express-Pune");
		cs.put("MUMBAI",
				"http://epaper.indianexpress.com/t/236/latest/Indian-Express-Mumbai");
		cs.put("AHMEDABAD",
				"http://epaper.indianexpress.com/t/300/latest/Ahmedabad");
		cs.put("LUCKNOW",
				"http://epaper.indianexpress.com/t/433/latest/Lucknow");
	}

	public String buildURL(Language lang, String city, Date date)
			 {

		if (!citymap.containsKey(lang))
			return null;

		if (!(citymap.get(lang).contains(city.toUpperCase())))
			return null;
		StringBuilder urlStr = new StringBuilder();

		try{
		URL url = new URL(cs.get(city.toUpperCase()));

		HttpURLConnection uc = (HttpURLConnection) url.openConnection();
		uc.setInstanceFollowRedirects(false);
		uc.connect();
		System.out.println(uc.getResponseCode() + " : "
				+ uc.getResponseMessage());
		String location = uc.getHeaderField("Location");
		System.out.println(location);
		String s[] = location.split("/");
		System.out.println(s[3]);
		uc.disconnect();


		urlStr = new StringBuilder("http://epaper.indianexpress.com/pdf/get/");
		urlStr.append(s[3]).append("/");
		}catch(IOException e){
			e.printStackTrace();
			return null;
		}
		return urlStr.toString();
	}

	@Override
	public void buildPageUrls() {
		String urlStr = buildURL(lang, city, date);

		for (int i = 0; i < pageCount; i++) {
			urls.add(buildPageUrl(urlStr, i + 1));
		}

		
	}

	public String buildPageUrl(String urlStr, int pagenumber) {
		// System.out.println("Print: "+urlStr);
		return urlStr.toString() + pagenumber;
	}

}
