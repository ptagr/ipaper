package com.ipaper.myapp;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DAINIK extends EpaperBase {

	private static Map<String, String> cs = new HashMap<String, String>();


	public DAINIK() {
		this(null, null, null);
	}

	public DAINIK(String lang, String city, Date date) {
		super(lang, city, date);
		name = "DAINIK";
		description = "Hindustan Dainik";

		citymap = new HashMap<Language, Set<String>>();
		populateCSMap();
		citymap.put(Language.HINDI, cs.keySet());
		pageCount = 30;
		expiryTimeInterval = 30 * 60; // 30 minutes
		maxNumPerConn = 1;
		id = buildFileName();
		//buildPageUrls();
	}

	void populateCSMap() {
		cs.put("DELHI", "HT");
		cs.put("RANCHI", "HR");
		cs.put("PATNA", "HP");
		cs.put("LUCKNOW", "HL");
	}

//	@Override
//	public String buildFileName() {
//		StringBuilder paperName = new StringBuilder();
//		paperName.append(name);
//		String pageStr;
//        if(date !=null){
//        	pageStr = new SimpleDateFormat("ddMMyyyy").format(date);;
//        	paperName.append("_").append(pageStr);
//        }
//        if(lang != null)
//        	paperName.append("_").append(lang.name());
//        	
//        if(city != null)
//        	paperName.append("_").append(city).append(".pdf");
//		
//		return paperName.toString();
//	}

	public String buildURL(Language lang, String city, Date date) {
		if (!citymap.containsKey(lang))
			return null;

		if (!(citymap.get(lang).contains(city.toUpperCase())))
			return null;

		StringBuilder urlStr = new StringBuilder();
		Format format = new SimpleDateFormat("yyyy/MM/dd");
		String dateStr = format.format(date);
		String pageStr = new SimpleDateFormat("dd_MM_yyyy_").format(date);
		if (lang.equals(Language.HINDI)) {
			urlStr = new StringBuilder(
					"http://epaper.hindustandainik.com/PUBLICATIONS/HT/");
			urlStr.append(cs.get(city.toUpperCase())).append("/");
		} 

		urlStr.append(dateStr);
		urlStr.append("/PagePrint/");
		urlStr.append(pageStr);
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
		return urlStr.toString() + getThreeDigitString(pagenumber) + ".pdf";
	}

}



