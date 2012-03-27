/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ipaper.myapp;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * 
 * @author Puneet
 */
public class HINDUSTAN_TIMES extends EpaperBase {

	private static Map<String, String> cs = new HashMap<String, String>();

	
	public HINDUSTAN_TIMES() {
		this(null, null, null);
	}

	public HINDUSTAN_TIMES(String lang, String city, Date date) {
		super(lang, city, date);
		name = "HT";
		description = "Hindustan Times";

		citymap = new HashMap<Language, Set<String>>();
		populateCSMap();
		citymap.put(Language.ENGLISH, cs.keySet());
		pageCount = 30;
		expiryTimeInterval = 30 * 60; // 30 minutes
		maxNumPerConn = 1;
		
		id = buildFileName();
		buildPageUrls();
	}

	void populateCSMap() {
		cs.put("DELHI", "HD");
		cs.put("MUMBAI", "HM");
		cs.put("KOLKATA", "HKL");
		cs.put("CHANDIGARH", "HC");
	}

//	@Override
//	public String buildFileName() {
//		StringBuilder paperName = new StringBuilder();
//		paperName.append(name);
//		String pageStr = new SimpleDateFormat("ddMMyyyy").format(date);
//		paperName.append("_").append(pageStr).append("_").append(lang.name())
//				.append("_").append(city).append(".pdf");
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
		if (lang.equals(Language.ENGLISH)) {

			urlStr = new StringBuilder(
					"http://epaper.hindustantimes.com/PUBLICATIONS/HT/");
			urlStr.append(cs.get(city.toUpperCase())).append("/");
		}

		urlStr.append(dateStr);
		urlStr.append("/PagePrint/");
		urlStr.append(pageStr);
		return urlStr.toString();
	}

	@Override
	protected void buildPageUrls() {
		String urlStr = buildURL(lang, city, date);
		List<String> urls = new ArrayList<String>();

		for (int i = 0; i < pageCount; i++) {
			urls.add(buildPageUrl(urlStr, i + 1));
		}

		
	}

	public String buildPageUrl(String urlStr, int pagenumber) {
		// System.out.println("Print: "+urlStr);
		return urlStr.toString() + getThreeDigitString(pagenumber) + ".pdf";
	}

}
