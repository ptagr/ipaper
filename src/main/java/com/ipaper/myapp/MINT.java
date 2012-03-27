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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * 
 * @author Puneet
 */
public class MINT extends EpaperBase {


	
	public MINT() {
		this(null, null, null);
	}

	public MINT(String lang, String city, Date date) {
		super(lang, city, date);
		name = "MINT";
		description = "The Wall Street Journal";

		citymap = new HashMap<Language, Set<String>>();
		Set<String> cities = new HashSet<String>();
		cities.add("INDIA");
		citymap.put(Language.ENGLISH, cities);
		pageCount = 30;
		expiryTimeInterval = 30 * 60; // 30 minutes
		maxNumPerConn = 1;
		
		id = buildFileName();
		//buildPageUrls();
	}




	public String buildURL(Language lang, String city, Date date) {
		if (!citymap.containsKey(lang))
			return null;



		Format format = new SimpleDateFormat("dd_MM_yyyy");
		String dateStr = format.format(date);
		StringBuilder urlStr = new StringBuilder(
				"http://epaper.livemint.com/PDFHandler.ashx?p1=Web&p2=");
		urlStr.append(dateStr.toString()).append("_");
		System.out.println("urlStr : " + urlStr);
		return urlStr.toString();
	}

	@Override
	public void buildPageUrls() {
		String urlStr = buildURL(lang, city, date);
		List<String> urls = new ArrayList<String>();

		for (int i = 0; i < pageCount; i++) {
			if(i==0)
				urls.add(buildPageUrl(urlStr, i + 1));
			urls.add(buildPageUrl(urlStr, i + 1));
		}

	}

	public String buildPageUrl(String urlStr, int pagenumber) {
		// System.out.println("Print: "+urlStr);
		return urlStr.toString() + getThreeDigitString(pagenumber) + ".pdf";
	}

}
