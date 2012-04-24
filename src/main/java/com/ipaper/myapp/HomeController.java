package com.ipaper.myapp;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

import javax.servlet.http.HttpServletResponse;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.CollectionCallback;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.mongodb.DBCollection;
import com.mongodb.MongoException;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {

	@Autowired
	private EpaperFactory eFactory;
	
	@Autowired(required = false)
	MongoDbFactory mongoDbFactory; 
	
	@Autowired (required = false)
	MongoTemplate mongoTemplate;

	private static final Logger logger = LoggerFactory
			.getLogger(HomeController.class);

	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/hello", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		logger.info("Welcome home! the client locale is " + locale.toString());

		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG,
				DateFormat.LONG, locale);

		String formattedDate = dateFormat.format(date);

		model.addAttribute("serverTime", formattedDate);

		return "home";
	}

//	@RequestMapping(value = "/{epapername}/{date}/{city}/{lang}", method = RequestMethod.GET)
//	public @ResponseBody
//	EpaperBase getPaper(@PathVariable String epapername,
//			@PathVariable String date, @PathVariable String city,
//			@PathVariable String lang) {
//
//		SimpleDateFormat format = new SimpleDateFormat("ddMMyyyy");
//		EpaperBase epaper = eFactory.epaper(epapername);
//		epaper.setCity(city);
//		try {
//			epaper.setDate(format.parse(date));
//		} catch (ParseException e1) {
//			e1.printStackTrace();
//		}
//		epaper.setLang(Language.valueOf(lang.toUpperCase()));
//		CopyOfEpaperTask et = new CopyOfEpaperTask(epaper);
//		try {
//			et.getCompletePdf();
//		} catch (ExecutionException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return epaper;
//	}

	
	@RequestMapping(value = "/{epapername}/{date}/{city}/{lang}.pdf", method = RequestMethod.GET)
	public ModelAndView getDirectPaper(@PathVariable String epapername,
			@PathVariable String date, @PathVariable String city,
			@PathVariable String lang) {
		// logger.info("Welcome home! the client locale is "+
		// locale.toString());
		SimpleDateFormat format = new SimpleDateFormat("ddMMyyyy");
		EpaperBase epaper = eFactory.epaper(epapername);
		epaper.setCity(city);
		try {
			epaper.setDate(format.parse(date));
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		epaper.setLang(Language.valueOf(lang.toUpperCase()));
		
		return new ModelAndView("customPdfView", "epaper", epaper);
	}
	
	@RequestMapping(value = "/{epapername}/{date}/{city}/{lang}.json", method = RequestMethod.GET)
	public @ResponseBody EpaperBase getDirectPaperJSON(@PathVariable String epapername,
			@PathVariable String date, @PathVariable String city,
			@PathVariable String lang) {
		SimpleDateFormat format = new SimpleDateFormat("ddMMyyyy");
		EpaperBase epaper = eFactory.epaper(epapername);
		epaper.setCity(city);
		try {
			epaper.setDate(format.parse(date));
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		epaper.setLang(Language.valueOf(lang.toUpperCase()));
		
		epaper.setUrl(new EpaperTask(epaper).generateEpaper(true, mongoTemplate));
		
		return epaper;
	}

	
//	@RequestMapping(value = "/{city}/{lang}", method = RequestMethod.GET)
//	public @ResponseBody
//	EpaperBase[] getCurrentPaperByCityAndLang(@PathVariable String city,
//			@PathVariable String lang) {
//
//		List<EpaperBase> eList = new ArrayList<EpaperBase>();
//		
//		Calendar cal = new GregorianCalendar(
//				TimeZone.getTimeZone("Asia/Calcutta"));
//
//		for (EpaperBase epaper : eFactory.getAllEpapers()) {
//			System.out.println("epaper :"+epaper.getName());
//			System.out.println("========================================================================");
//			System.out.println("REACHED HERE");
//			HashMap<Language, HashSet<String>> citymap = epaper.getCitymap();
//			System.out.println("citymap.containsKey(Language.valueOf(lang.toUpperCase()) = "+citymap.containsKey(Language.valueOf(lang.toUpperCase())));
//			System.out.println("citymap.get(Language.valueOf(lang.toUpperCase())).contains(city)) = "+citymap.get(Language.valueOf(lang.toUpperCase())).contains(city.toUpperCase()));
//			
//			if(!citymap.containsKey(Language.valueOf(lang.toUpperCase()))
//					|| !citymap.get(Language.valueOf(lang.toUpperCase())).contains(city.toUpperCase()))
//				continue;
//			System.out.println("REACHED HERE TOO");
//			System.out.println("========================================================================");
//			epaper.setLang(Language.valueOf(lang.toUpperCase()));
//			epaper.setDate(cal.getTime());
//			epaper.setCity(city);
//			try {
//				new CopyOfEpaperTask(epaper).getCompletePdf();
//				eList.add(epaper);
//			} catch (ExecutionException e) {
//				e.printStackTrace();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//		return eList.toArray(new EpaperBase[]{});
//
//	}
	
	
//	@RequestMapping(value = "/{city}/{lang}/{date}", method = RequestMethod.GET)
//	public @ResponseBody
//	EpaperBase[] getCurrentPaperByCityAndLangAndDate(@PathVariable String city,
//			@PathVariable String lang, @PathVariable String date) {
//
//		List<EpaperBase> eList = new ArrayList<EpaperBase>();
//		SimpleDateFormat format = new SimpleDateFormat("ddMMyyyy");
//		Calendar cal = new GregorianCalendar(
//				TimeZone.getTimeZone("Asia/Calcutta"));
//
//		for (EpaperBase epaper : eFactory.getAllEpapers()) {
//			System.out.println("epaper :"+epaper.getName());
//			System.out.println("========================================================================");
//			System.out.println("REACHED HERE");
//			HashMap<Language, HashSet<String>> citymap = epaper.getCitymap();
//			System.out.println("citymap.containsKey(Language.valueOf(lang.toUpperCase()) = "+citymap.containsKey(Language.valueOf(lang.toUpperCase())));
//			System.out.println("citymap.get(Language.valueOf(lang.toUpperCase())).contains(city)) = "+citymap.get(Language.valueOf(lang.toUpperCase())).contains(city.toUpperCase()));
//			
//			if(!citymap.containsKey(Language.valueOf(lang.toUpperCase()))
//					|| !citymap.get(Language.valueOf(lang.toUpperCase())).contains(city.toUpperCase()))
//				continue;
//			System.out.println("REACHED HERE TOO");
//			System.out.println("========================================================================");
//			epaper.setLang(Language.valueOf(lang.toUpperCase()));
//			try {
//				epaper.setDate(format.parse(date));
//			} catch (ParseException e1) {
//				epaper.setDate(cal.getTime());
//				e1.printStackTrace();
//			}
//			epaper.setCity(city);
//			try {
//				new CopyOfEpaperTask(epaper).getCompletePdf();
//				eList.add(epaper);
//			} catch (ExecutionException e) {
//				e.printStackTrace();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//		return eList.toArray(new EpaperBase[]{});
//
//	}
	
	
	@RequestMapping(value = "{epaper}/supportedlanguages.json", method = RequestMethod.GET)
	public @ResponseBody
	Set<String> getSupportedLanguages(@PathVariable String epaper) {
		return eFactory.getAllSupportedLanguages(epaper);
	}
	
	
	@RequestMapping(value = "/supportedlanguages.json", method = RequestMethod.GET)
	public @ResponseBody
	Set<String> getAllSupportedLanguages() {
		return eFactory.getAllSupportedLanguages();
	}
	
	@RequestMapping(value = "{epaper}/{lang}/supportedcities.json", method = RequestMethod.GET)
	public @ResponseBody
	Set<String> getSupportedCitiesByLanguage(@PathVariable String epaper, @PathVariable String lang) {
		return eFactory.getAllSupportedCitiesByLanguage(epaper,Language.valueOf(lang.toUpperCase()));
	}
	
	
	
	@RequestMapping(value = "/supportedpapers.json", method = RequestMethod.GET)
	public @ResponseBody
	Set<String> getSupportedPapers() {
		return eFactory.getAllSupportedPapers();
	}
	
	
	@RequestMapping(value = "/supportedcities.json", method = RequestMethod.GET)
	public @ResponseBody
	Set<String> getSupportedCities() {
		return eFactory.getSupportedCities();
	}
	
	@RequestMapping(value = "/{city}/currentpapers.json", method = RequestMethod.GET)
	public @ResponseBody
	List<EpaperBase> getAllCurrentPapersByCity(@PathVariable String city) {
		return eFactory.getAllCurrentPapersByCity(city, false);
	}
	
	@RequestMapping(value = "/currentpapersofallcities.json", method = RequestMethod.GET)
	public @ResponseBody
	Map<String, List<EpaperBase>> getAllCurrentPapersOfAllCities() {
		return eFactory.getAllCurrentPapersOfAllCities();
	}
	

	@RequestMapping(value = "/name", method = RequestMethod.GET)
	public ModelAndView getEpapers() {
		String[] epapers = eFactory.getEpapers();
		ModelAndView mav = new ModelAndView("epaperXmlView",
				BindingResult.MODEL_KEY_PREFIX + "epapers", epapers);
		return mav;
	}

	public EpaperFactory geteFactory() {
		return eFactory;
	}

	public void seteFactory(EpaperFactory eFactory) {
		this.eFactory = eFactory;
	}
	
	
	
	@Autowired(required = false)
	@Qualifier(value = "serviceProperties")
	Properties serviceProperties;

	@RequestMapping(value = "/add")
	public void home(HttpServletResponse response) throws IOException {
		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();
		List<String> services = new ArrayList<String>();
		if (mongoDbFactory != null) {
			services.add("MongoDB: " + mongoDbFactory.getDb().getMongo().getAddress());
		}
		Random generator = new Random();
		Person p = new Person("Joe Cloud-" + generator.nextInt(100), generator.nextInt(100));
		mongoTemplate.save(p);
		
		List<Person> people = mongoTemplate.find(new Query(Criteria.where("age").lt(100)), Person.class);
		out.println("People : ");
		for(Person p1:people){
			out.println(p1);
		}
//		model.addAttribute("people", people);
//		model.addAttribute("services", services);
//		model.addAttribute("serviceProperties", getServicePropertiesAsList());

		String environmentName = (System.getenv("VCAP_APPLICATION") != null) ? "Cloud" : "Local";
		out.println("Environment : "+environmentName);
//		model.addAttribute("environmentName", environmentName);
	}
	
	
	@RequestMapping(value = "/currentpapers.json", method = RequestMethod.GET)
	public @ResponseBody
	List<EpaperBase> getCurrentPapers() {
		return mongoTemplate.findAll(EpaperBase.class, Collections.epaperCollectionName);
		//return eFactory.getAllSupportedPapers();
	}
	
	@RequestMapping(value = "/query")
	public void query(HttpServletResponse response) throws IOException {
		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();
		List<String> services = new ArrayList<String>();
		if (mongoDbFactory != null) {
			services.add("MongoDB: " + mongoDbFactory.getDb().getMongo().getAddress());
		}
		
		
		List<Person> people = mongoTemplate.find(new Query(Criteria.where("age").lt(100)), Person.class);
		out.println("People : ");
		for(Person p1:people){
			out.println(p1);
		}
//		model.addAttribute("people", people);
//		model.addAttribute("services", services);
//		model.addAttribute("serviceProperties", getServicePropertiesAsList());

		String environmentName = (System.getenv("VCAP_APPLICATION") != null) ? "Cloud" : "Local";
		out.println("Environment : "+environmentName);
		
		out.println("Collections : "+mongoTemplate.getCollectionNames());
//		model.addAttribute("environmentName", environmentName);
	}
	
	
	

	@RequestMapping("/env")
	public void env(HttpServletResponse response) throws IOException {
		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();
		out.println("System Properties:");
		for (Map.Entry<Object, Object> property : System.getProperties()
				.entrySet()) {
			out.println(property.getKey() + ": " + property.getValue());
		}
		out.println();
		out.println("System Environment:");
		for (Map.Entry<String, String> envvar : System.getenv().entrySet()) {
			out.println(envvar.getKey() + ": " + envvar.getValue());
		}
	}

	@RequestMapping("/service-properties")
	public void services(HttpServletResponse response) throws IOException {
		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();
		if (serviceProperties != null) {
			out.println("Cloud Service Properties:");
			// Map envMap = System.getenv();
			for (Object key : serviceProperties.keySet()) {
				out.println(key + ": " + serviceProperties.get(key));
			}
		} else {
			out.println("No Cloud Service Properties found.  Check configuration file for <cloud:service-properties/> element");
		}
		out.println(")<a href=\"/\">Return to previous page.</a>");
		out.println();
	}

	private List<String> getServicePropertiesAsList() {
		List<String> propList = new ArrayList<String>();
		if (serviceProperties != null) {
			for (Object key : serviceProperties.keySet()) {
				propList.add(key + ": " + serviceProperties.get(key));
			}
		}
		return propList;
	}

	@RequestMapping("/deleteAll")
	public void deleteAll(HttpServletResponse response) throws IOException {
		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();
		long count = mongoTemplate.execute(Person.class,
				new CollectionCallback<Long>() {
					public Long doInCollection(DBCollection collection)
							throws MongoException, DataAccessException {
						return collection.count();
					}

					
				});
		out.println("Deleted " + count + " entries");
		mongoTemplate.dropCollection("person");
		
	}


}
