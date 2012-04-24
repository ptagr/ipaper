package com.ipaper.myapp;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

public class Epaper {
	private EpaperFactory eFactory;

	public void run() throws ExecutionException, IOException, ParseException {
//		System.out.println();
//		System.out.println("Run a scheduled  task here");
//		//SimpleDateFormat format = new SimpleDateFormat("ddMMyyyy");
//		//EpaperBase epaper;
//
//		CopyOfEpaperTask task;
//		Calendar cal = new GregorianCalendar(
//				TimeZone.getTimeZone("Asia/Calcutta"));
//		for (int i = 0; i < 2; i++) {
//			for (EpaperBase epaper : eFactory.getAllEpapers()) {
//				//epaper = eFactory.epaper("dna");
//				HashMap<Language, HashSet<String>> citymap = epaper
//						.getCitymap();
//				for (Language lang : citymap.keySet()) {
//
//					epaper.setLang(lang);
//					epaper.setDate(cal.getTime());
//					for (String city : citymap.get(lang)) {
//						epaper.setCity(city);
//						task = new CopyOfEpaperTask(epaper);
//						task.getCompletePdf();
//					}
//				}
//				cal.add(Calendar.DATE, -1);
//			}
//		}
//
//		System.out.println();
	}

	public EpaperFactory geteFactory() {
		return eFactory;
	}

	public void seteFactory(EpaperFactory eFactory) {
		this.eFactory = eFactory;
	}

}
