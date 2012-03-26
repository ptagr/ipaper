package com.ipaper.myapp;

import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Order;
import org.springframework.data.mongodb.core.query.Query;

public class EpaperFactory {
	private  String[] epapers;
	
	@Autowired (required = false)
	MongoTemplate mongoTemplate;
	
	public EpaperFactory() {
		// TODO Auto-generated constructor stub
		init();
	}
	
	public void init(){
//		SimpleDateFormat format = new SimpleDateFormat("ddMMyyyy");
//		System.out.println("Deleting entire epaper folder on restart");
//		List<Date> dates = new ArrayList<Date>();
//		boolean collectionPresent = mongoTemplate.collectionExists(Collections.epaperCollectionName);
//		if(!collectionPresent)
//			mongoTemplate.createCollection(Collections.epaperCollectionName);
//
//		Date date = new Date();
//		Calendar cal = Calendar.getInstance();
//		Date today = cal.getTime();
//
//		cal.add(Calendar.DATE, -1);
//		Date yesterday = cal.getTime();
//
//		dates.add(today);
//		dates.add(yesterday);
//
//		cal.add(Calendar.DATE, -1);
//		Date daybeforyesterday = cal.getTime();
//
//		//Delete old newspapers
//		DeleteEpaperTask.deleteEpaperFolder("dropbox", "/Public/epaper"
//				+ format.format(daybeforyesterday));
	}
	
	public  EpaperBase epaper(String name){
		String cname = "com.ipaper.myapp."+name.toUpperCase();
		try {
			System.out.println("cname : "+cname);
			Class cdef = Class.forName(cname);
			return (EpaperBase) cdef.newInstance();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
	public  EpaperBase epaper(String name, String lang, String city, Date date){
		String cname = "com.ipaper.myapp."+name.toUpperCase();
		try {
			System.out.println("cname : "+cname);
			return (EpaperBase) Class.forName(cname).getConstructor(String.class, String.class, Date.class).newInstance(lang, city, date);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public  List<EpaperBase> getAllEpapers(){
		List<EpaperBase> newList = new ArrayList<EpaperBase>();
		for(String s:epapers){
			String cname = "com.ipaper.myapp."+s.toUpperCase();
			try {
				Class cdef = Class.forName(cname);
				Object o = cdef.newInstance();
				newList.add((EpaperBase) o);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return newList;
	}
	
	
	public Set<String> getAllSupportedLanguages(String epaper){
		Set<String> newList = new HashSet<String>();
		//for(String s:epapers){
			String cname = "com.ipaper.myapp."+epaper.toUpperCase();
			try {
				Class cdef = Class.forName(cname);
				Object o = cdef.newInstance();
				for (Language language:((EpaperBase)o).getCitymap().keySet()){
					newList.add(language.name());
				}
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		//}
		return newList;
	}
	
	public Set<String> getAllSupportedLanguages(){
		Set<String> newList = new HashSet<String>();
		for(String s:epapers){
			String cname = "com.ipaper.myapp."+s.toUpperCase();
			try {
				Class cdef = Class.forName(cname);
				Object o = cdef.newInstance();
				for (Language language:((EpaperBase)o).getCitymap().keySet()){
					newList.add(language.name());
				}
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return newList;
	}
	
	
	public Set<String> getAllSupportedPapers(){
		Set<String> newList = new HashSet<String>();
		for(String s:epapers){
			newList.add(s);
		}
		return newList;
	}
	
	
	public Set<String> getAllCurrentPapers(){
		Set<String> newList = new HashSet<String>();
		for(String s:epapers){
			newList.add(s);
		}
		return newList;
	}
	
	public Set<String> getAllSupportedCitiesByLanguage(String epaper, Language language){
		Set<String> newList = new HashSet<String>();
		//for(String s:epapers){
			String cname = "com.ipaper.myapp."+epaper.toUpperCase();
			try {
				Class cdef = Class.forName(cname);
				Object o = cdef.newInstance();
				Set<String> cities = ((EpaperBase)o).getCitymap().get(language);
				if(cities !=null && cities.size()>0){
					for (String city:cities){
						newList.add(city);
					}
				}
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		//}
		return newList;
	}

	
	public List<EpaperBase> getAllCurrentPapersByCity(String city, boolean aggregate){
		List<String> cities = new ArrayList<String>();
		if(city!= null)
			cities.add(city.toUpperCase());
		if(!aggregate)
			cities.add("INDIA");
		Query q = new Query(Criteria.where("city").in(cities));
		q.sort().on("date", Order.DESCENDING);
		return mongoTemplate.find(q		
				, EpaperBase.class, Collections.epaperCollectionName);
	}
	
	
	public Map<String, List<EpaperBase>> getAllCurrentPapersOfAllCities(){
		Set<String> cities = getSupportedCities();
		Map<String, List<EpaperBase>> papers = new HashMap<String, List<EpaperBase>>();
		for(String city : cities){
			papers.put(city, getAllCurrentPapersByCity(city, true));
		}
		return papers;
	}
	
	public Set<String> getSupportedCities(){
		
//		List<EpaperBase> epapers = mongoTemplate.findAll(EpaperBase.class, Collections.epaperCollectionName);
//		//Set<String> newList = new HashSet<String>();
//		
//		for(EpaperBase eb : epapers){
//			for(Set<String> cities: eb.getCitymap().values()){
//				newList.addAll(cities);
//			}
//		}
		
		Set<String> newList = new HashSet<String>();
		
		for(String s:epapers){
			String cname = "com.ipaper.myapp."+s.toUpperCase();
			try {
				Class cdef = Class.forName(cname);
				Object o = cdef.newInstance();		
				for(Set<String> cities:((EpaperBase)o).getCitymap().values()){
					newList.addAll(cities);
				}
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		
		return newList;
	}
	

	public  String[] getEpapers() {
		return epapers;
	}

	public  void setEpapers(String[] epapers) {
		this.epapers = epapers;
	}
	
	
}


