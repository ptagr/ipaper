package com.ipaper.myapp;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MIDDAY extends EpaperBase {

	private static Map<String, String> cs = new HashMap<String, String>();

	public MIDDAY() {
		this(null, null, null);
	}

	public MIDDAY(String lang, String city, Date date) {
		super(lang, city, date);
		name = "MIDDAY";
		description = "India's Best Tabloid Stories";

		citymap = new HashMap<Language, Set<String>>();
		populateCSMap();
		citymap.put(Language.ENGLISH, cs.keySet());
		pageCount = 60;
		expiryTimeInterval = 30 * 60; // 30 minutes
		maxNumPerConn = 1;
		id = buildFileName();
	}

	void populateCSMap() {
		cs.put("DELHI", "dn");
		cs.put("MUMBAI", "mn");
		cs.put("BANGALORE", "bn");
		cs.put("PUNE", "pn");
	}

	public String buildURL(Language lang, String city, Date date) {
		if (!citymap.containsKey(lang))
			return null;

		if (!(citymap.get(lang).contains(city.toUpperCase())))
			return null;

		StringBuilder urlStr = new StringBuilder();
		Format format = new SimpleDateFormat("ddMMyyyy");
		String dateStr = format.format(date);

		urlStr = new StringBuilder("http://epaper2.mid-day.com/DRIVE/");
		urlStr.append(city.toLowerCase()).append("/");
		urlStr.append(dateStr).append("/epaperpdf/");
		urlStr.append(dateStr).append("-md-");
		urlStr.append(cs.get(city.toUpperCase())).append("-");

		return urlStr.toString();
	}

	@Override
	public List<String> buildPageUrls() {
		String urlStr = buildURL(lang, city, date);
		List<String> urls = new ArrayList<String>();

		for (int i = 0; i < pageCount; i++) {
			urls.add(buildPageUrl(urlStr, i + 1));
		}

		return urls;
	}

	public String buildPageUrl(String urlStr, int pagenumber) {
		// System.out.println("Print: "+urlStr);
		return urlStr.toString() + pagenumber + ".pdf";
	}

}
