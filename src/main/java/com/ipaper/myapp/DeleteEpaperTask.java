package com.ipaper.myapp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.dropbox.client2.DropboxMain;
import com.dropbox.client2.DropboxAPI.Account;



public class DeleteEpaperTask extends TimerTask {

	@Autowired(required = false)
	MongoTemplate mongoTemplate;


	public static void deleteEpaperFolder(String folder2) {
		DropboxMain ds = DropboxMain.getInstance();
		//Account account = ds.getAccountInfo();
//		System.out.print("Delele folder - " + folder2 + "  ACCOUNT INFO : "
//				+ account.email);
		ds.deleteFile(folder2);
		//ds.getAPI().fileDelete(folder1, folder2, "");

	}

	

	public void run() {

		boolean collectionPresent = mongoTemplate
				.collectionExists(Collections.epaperCollectionName);
		if (!collectionPresent)
			return;
		Date yesterday = Time.addandReturnDate(-1);
		Date d = new Date(yesterday.getYear(), yesterday.getMonth(),
				yesterday.getDate(), 0, 0, 0);
		DropboxMain ds = DropboxMain.getInstance();
		Account account = ds.getAccountInfo();
		
//		DropboxSample ds = DropboxSample.getInstance();
//		DropboxAPI.Account account = ds.getAccountInfo();
		

		List<EpaperBase> dbpapers = mongoTemplate.findAll(EpaperBase.class,
				Collections.epaperCollectionName);

		for (EpaperBase e : dbpapers) {
			
			if (!e.getDate().after(d)) {
				long diff = Time.getCurrentIndiaTime().getTime() - e.getGeneratedTime().getTime();
				long diffSeconds = diff / 1000;
				if(diffSeconds < 0){
					System.out.println("Generated Time of epaper more than current time. So refreshing the generated time");
					e.setGeneratedTime(Time.getCurrentIndiaTime());
					continue;
				}else if(diffSeconds < e.getExpiryTimeInterval()){
					System.out.println("Epaper generated less than epiry time ago");
					continue;
				}else{
					SimpleDateFormat format = new SimpleDateFormat("ddMMyyyy");
					String file = "/Public/epaper" + format.format(e.getDate())
							+ "/" + e.buildFileName();
					mongoTemplate.remove(new Query(Criteria.where("_id")
							.is(e.getId())), Collections.epaperCollectionName);
					//ds.getAPI().fileDelete("dropbox", file, "");
					ds.deleteFile(file);
				}
				
				
			}
		}

		return;

	}

}
