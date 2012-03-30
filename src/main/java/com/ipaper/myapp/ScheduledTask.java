package com.ipaper.myapp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TimerTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;

public class ScheduledTask  extends TimerTask{
	
	
	@Autowired
	private EpaperFactory eFactory;

	@Autowired (required = false)
	MongoTemplate mongoTemplate;
	
	@Override
	public void run() {
		SimpleDateFormat format = new SimpleDateFormat("ddMMyyyy");
		List<Date> dates = new ArrayList<Date>();
		boolean collectionPresent = mongoTemplate.collectionExists(Collections.epaperCollectionName);
		if(!collectionPresent)
			mongoTemplate.createCollection(Collections.epaperCollectionName);

		Date today = Time.getCurrentIndiaTime();		
		Date yesterday = Time.addandReturnDate(-1);
		Date daybeforyesterday = Time.addandReturnDate(-2);
		
		dates.add(today);
		dates.add(yesterday);

		//if(today.getHours()<=1){
			//Remove day before yesterday's papers from server and DB
			Date d1 = new Date(yesterday.getYear(), yesterday.getMonth(), yesterday.getDate(), 0, 0, 0);
			DeleteEpaperTask.deleteEpaperFolder("/Public/epaper"
					+ format.format(daybeforyesterday));
			System.out.println("Deleting folder :" + "/Public/epaper"
					+ format.format(daybeforyesterday));
			BasicDBObject query = new BasicDBObject();
			query.put("date", new BasicDBObject("$lt",d1));
			DBCursor dbc = mongoTemplate.getCollection(Collections.epaperCollectionName).find(query);
			System.out.println("Returned " + dbc.count()+ " objects");
//			while(dbc.hasNext()){
//				System.out.println(dbc.next());
//			}
			mongoTemplate.getCollection(Collections.epaperCollectionName).remove(query);
			
			//mongoTemplate.remove(new Query(Criteria.where("date").lt(d)), Collections.epaperCollectionName);
		//}

		
		
		for (Date d : dates) {
			Set<String> epapers = eFactory.getAllSupportedPapers();
			for (String epaper : epapers) 
			{
				if(epaper.equalsIgnoreCase("INDIANEXPRESS") && d.equals(yesterday))
					continue;
				Set<String> languages = eFactory
						.getAllSupportedLanguages(epaper);
				for (String language : languages) {
					Set<String> cities = eFactory.getAllSupportedCitiesByLanguage(epaper,
							Language.valueOf(language.toUpperCase()));
					for (String city : cities) {
						EpaperBase paper = eFactory.epaper(epaper);
						paper.setCity(city);
						paper.setDate(d);
						paper.setLang(Language.valueOf(language.toUpperCase()));
						System.out.println("Scheduled Task : "+"Generating epaper for paramenters : ("+paper.getName()+", "+city+", "+language+", "+format.format(d)+")");
						new EpaperTask(paper).generateEpaper(false, mongoTemplate);
						long mem = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/(1024*1024);
				    	System.out.println("JVM memory in use = {" + mem +"}");
						
					}
				}
			}
		}
	}

	public EpaperFactory geteFactory() {
		return eFactory;
	}

	public void seteFactory(EpaperFactory eFactory) {
		this.eFactory = eFactory;
	}
	
	
	
}
