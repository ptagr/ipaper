package com.ipaper.myapp;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class DNA extends EpaperBase {

	public DNA() {
		// TODO Auto-generated constructor stub
		// languages = EnumSet.of(Language.ENGLISH);
		// days = EnumSet.allOf(Day.class);
		this(null,null,null);
		
		
	}
	
	public DNA(String lang, String city, Date date){
		super(lang,city,date);		
		name = "DNA";
		description = "Daily News & Analysis";
		
		citymap = new HashMap<Language, Set<String>>();
		Set<String> cities = new HashSet<String>();
		cities.add("AHMEDABAD");
		cities.add("JAIPUR");
		cities.add("BANGALORE");
		cities.add("MUMBAI");
		cities.add("PUNE");
		cities.add("INDORE");
		citymap.put(Language.ENGLISH, cities);

		//PageCount
		pageCount = 30;
		expiryTimeInterval = 30*60; //30 minutes
		maxNumPerConn = 4;
		
		id = buildFileName();
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
	
	public String buildPageUrl(String urlStr, int pagenumber){
        return urlStr.toString() + pagenumber + "-0.pdf";
    }

//	@Override
//	public String buildFileName() {
//		StringBuilder paperName = new StringBuilder("DNA");
//        String pageStr;
//        if(date !=null){
//        	pageStr = new SimpleDateFormat("ddMMyyyy").format(date);
//        	paperName.append("_").append(pageStr);
//        }
//        if(lang != null)
//        	paperName.append("_").append(lang.name());
//        	
//        if(city != null)
//        	paperName.append("_").append(city).append(".pdf");
//        return paperName.toString();
//	}

	public String buildURL(Language lang, String city, Date date) {

		if (!citymap.containsKey(lang))
			return null;

		if (!(citymap.get(lang).contains(city.toUpperCase())))
			return null;

		StringBuilder urlStr = new StringBuilder();

		// updatePageCount(3);
		int dateminusone = 0;
		Format format = new SimpleDateFormat("ddMMyyyy");
		String dateStr = format.format(date);
		String pageStr = new SimpleDateFormat("dd_MM_yyyy_").format(date);

		urlStr = new StringBuilder("http://cdn.epaper.dnaindia.com/epaperimages/");
		urlStr.append(city.toLowerCase()).append("/");
		urlStr.append(dateStr).append("/");
//		if (city.equalsIgnoreCase("AHMEDABAD")) {
//			urlStr.append("dnaahmedabad/");
//		} else if (city.equalsIgnoreCase("JAIPUR")) {
//			urlStr.append("dnaahmedabad/");
//		} else if (city.equalsIgnoreCase("BANGALORE")) {
//			urlStr.append("dnabangalore/");
//		}

//		urlStr.append("epaperpdf/").append(dateStr.toString()).append("/");
		dateminusone = date.getDate() - 1;
		if (dateminusone == 0) {
			switch (date.getMonth()) {
			// 31 days in prev month
			case 0:
			case 1:
			case 3:
			case 5:
			case 7:
			case 8:
			case 10:
				dateminusone = 31;
				break;
			case 4:
			case 6:
			case 9:
			case 11:
				dateminusone = 30;
				break;
			case 2:
				dateminusone = 28;

			}
		}

		urlStr.append(dateminusone);

		switch (date.getDay()) {
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:
			
			if (city.equalsIgnoreCase("MUMBAI")){
				urlStr.append("main_edition");
			}else if (city.equalsIgnoreCase("JAIPUR")){
				urlStr.append("jaipur");
			}else if (city.equalsIgnoreCase("AHMEDABAD")){
				urlStr.append("ahm_main_edition");
			}else if (city.equalsIgnoreCase("BANGALORE")){
				urlStr.append("bangalore");
			}else if (city.equalsIgnoreCase("PUNE")){
				urlStr.append("pune");
			}else if (city.equalsIgnoreCase("INDORE")){
				urlStr.append("indore");
			}
			urlStr.append("-pg");

			break;
		case 0:
			if (city.equalsIgnoreCase("MUMBAI")){
				urlStr.append("main_edition");
			}else if (city.equalsIgnoreCase("JAIPUR")){
				urlStr.append("jaipur");
			}else if (city.equalsIgnoreCase("AHMEDABAD")){
				urlStr.append("ahm_main");
			}else if (city.equalsIgnoreCase("BANGALORE")){
				urlStr.append("bangalore");
			}else if (city.equalsIgnoreCase("PUNE")){
				urlStr.append("pune");
			}else if (city.equalsIgnoreCase("INDORE")){
				urlStr.append("indo");
			}
			urlStr.append("-pg");
			break;
		case 6:
			if (city.equalsIgnoreCase("MUMBAI")){
				urlStr.append("main_edition");
			}else if (city.equalsIgnoreCase("JAIPUR")){
				urlStr.append("jaipur");
			}else if (city.equalsIgnoreCase("AHMEDABAD")){
				urlStr.append("ahm_main_edition");
			}else if (city.equalsIgnoreCase("BANGALORE")){
				urlStr.append("bangalore");
			}else if (city.equalsIgnoreCase("PUNE")){
				urlStr.append("pune");
			}else if (city.equalsIgnoreCase("INDORE")){
				urlStr.append("indo");
			}
			urlStr.append("-pg");
		}
		System.out.println("urlStr : " + urlStr);
		return urlStr.toString();
	}

}
