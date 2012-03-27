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
public class TOI extends EpaperBase {

   private static Map<String, String> cs = new HashMap<String, String>();

    
   public TOI() {	
		this(null,null,null);
	}
    
    public TOI(String lang, String city, Date date) {
    	super(lang,city,date);		
		name = "TOI";
		description = "Times Of India";
		
		citymap = new HashMap<Language, Set<String>>();
		populateCSMap();
		citymap.put(Language.ENGLISH, cs.keySet());
		pageCount = 50;
		expiryTimeInterval = 30*60; //30 minutes   
    }

    

    void populateCSMap() {
        cs.put("AHMEDABAD", "TOIA");
        cs.put("BANGALORE", "TOIBG");
        cs.put("CHENNAI", "TOICH");
        cs.put("DELHI", "CAP");
        cs.put("HYDERABAD", "TOIH");
        cs.put("JAIPUR", "TOIJ");
        cs.put("KOLKATA", "TOIKM");
        cs.put("LUCKNOW", "TOIL");
        cs.put("MUMBAI", "TOIM");
        cs.put("PUNE", "TOIPU");
    }
    

    
    @Override
	public String buildFileName() {
		StringBuilder paperName = new StringBuilder("TOI");
        String pageStr = new SimpleDateFormat("ddMMyyyy").format(date);
        paperName.append("_").append(pageStr).append("_").append(lang.name()).append("_").append(city).append(".pdf");
        return paperName.toString();
	}

    public String buildURL(Language lang, String city, Date date) {
    	StringBuilder urlStr = new StringBuilder();
        Format format = new SimpleDateFormat("yyyy/MM/dd");
        String dateStr = format.format(date);
        String pageStr = new SimpleDateFormat("yyyy_M_d").format(date);
        urlStr = new StringBuilder("http://epaper.timesofindia.com/Repository/");
        urlStr.append(cs.get(city)).append("/").append(dateStr).append("/").append(cs.get(city)).append("_").append(pageStr).append("_");
        return urlStr.toString();
        
    }

      
    @Override
	public void buildPageUrls() {
		String urlStr = buildURL(lang, city, date);
		
		for (int i = 0; i < pageCount; i++) {
			urls.add(buildPageUrl(urlStr, i + 1));
		}
		
	}
	
    public String buildPageUrl(String urlStr, int pagenumber){
        //System.out.println("Print: "+urlStr);
        return urlStr.toString() + (pagenumber) + ".pdf";
    }
    
}
