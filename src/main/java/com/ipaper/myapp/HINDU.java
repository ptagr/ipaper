package com.ipaper.myapp;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HINDU extends EpaperBase {

	private static Map<String, String> cs = new HashMap<String, String>();
	

	public HINDU() {
		this(null, null, null);
	}

	public HINDU(String lang, String city, Date date) {
		super(lang, city, date);
		name = "HINDU";
		description = "India's National Newspaper since 1978";

		citymap = new HashMap<Language, Set<String>>();
		populateCSMap();
		citymap.put(Language.ENGLISH, cs.keySet());
		pageCount = 40;
		expiryTimeInterval = 30 * 60; // 30 minutes
		maxNumPerConn = 1;
		id = buildFileName();
		//buildPageUrls();
	}

	void populateCSMap() {
		cs.put("DELHI", "103");
		cs.put("CHENNAI", "101");
		cs.put("HYDERABAD", "102");
	}

	public String buildURL(Language lang, String city, Date date) {
		if (!citymap.containsKey(lang))
			return null;

		if (!(citymap.get(lang).contains(city.toUpperCase())))
			return null;

		StringBuilder urlStr = new StringBuilder();
		Format format = new SimpleDateFormat("yyyyMMdd");
		String dateStr = format.format(date);

		urlStr = new StringBuilder("http://epaper.thehindu.com/index.php?rt=article/showPdf&filename=");
		urlStr.append(dateStr).append("A_");

		
		return urlStr.toString();
	}

	@Override
	public void buildPageUrls() {
		String urlStr = buildURL(lang, city, date);
		List<String> urls = new ArrayList<String>();

		for (int i = 0; i < pageCount; i++) {
			urls.add(buildPageUrl(urlStr, i + 1));
		}

		
	}

	public String buildPageUrl(String urlStr, int pagenumber) {
		
		return urlStr.toString() + getThreeDigitString(pagenumber) + cs.get(city.toUpperCase());
	}

}
