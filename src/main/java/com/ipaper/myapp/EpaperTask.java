package com.ipaper.myapp;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.data.mongodb.core.MongoTemplate;

import com.Ostermiller.util.CircularByteBuffer;

public class EpaperTask {

	protected EpaperBase epaper = null;

	protected EpaperTask(EpaperBase epaper) {
		this(epaper, null);

	}

	protected EpaperTask(EpaperBase epaper, OutputStream outs) {
		this.epaper = epaper;
	}

	public String getThreeDigitString(int no) {
		if (no >= 0 && no <= 9) {
			return "00" + no;

		} else if (no <= 99) {
			return "0" + no;
		} else {
			return "Error";
		}

	}

	public byte[] getDirectCompletePdf() throws ExecutionException, IOException {

		System.out.println("STARTTIME - "
				+ new SimpleDateFormat("ddMMyyyy HH:mm:ss:SS")
						.format(new Date()));
		epaper.buildPageUrls();
		if(epaper.getUrls()== null || epaper.getUrls().size() == 0){
			return new byte[0];
		}

		System.out.println("GETTING PDFs !!!! ");

		
		
		try {
			return DownloadManager.getInstance().download(epaper);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {

			System.out.println("ENDTIME - "
					+ new SimpleDateFormat("ddMMyyyy HH:mm:ss:SS")
							.format(new Date()));
			
			System.gc();
		}
	}

//	public void mergePDFs() {
//		if (pdfs.isEmpty()) {
//			return;
//		} else {
//
//			// System.out.println("MERGING PDFs");
//
//			MyPDFUtility.concatPDFs(pdfs, _boutStream, false);
//		}
//	}
	
	
	public byte[] mergeCBBPDFs(List<CircularByteBuffer> cbbList, int pages) {
		if (cbbList.isEmpty()) {
			return new byte[0];
		} else {

			// System.out.println("MERGING PDFs");

			return MyPDFUtility.concatPDFs(cbbList, pages);
		}
	}

	public String generateEpaper(boolean delete, MongoTemplate mongoTemplate) {
		String returnUrl = "";


		try {
			epaper.setId();

			EpaperBase temp;
			temp = mongoTemplate.findById(epaper.getId(), EpaperBase.class,
					com.ipaper.myapp.Collections.epaperCollectionName);
			if (temp != null) {
				System.out.println("PAPER FOUND IN DB");
				if(temp.getUrl() != null && temp.getUrl().length() > 0){
					System.out.println("URL : "+ temp.getUrl());
					return temp.getUrl();
					
				}
			}

//			boolean f = epaper.isPaperOnDropbox();
//			System.out.println("EPAPER ON DROPBOX :" + f);
//			if (!f) {
				returnUrl = epaper.uploadToDropbox(getDirectCompletePdf());
//			}
//			if (delete && returnUrl != null && !epaper.getDate().after(d))
//				epaper.startDeleteTask(mongoTemplate);

			if (returnUrl != null) {
				// epaper.setId();
				mongoTemplate.insert(epaper,
						com.ipaper.myapp.Collections.epaperCollectionName);
				epaper.setGeneratedTime(Time.getCurrentIndiaTime());
			} 
//			else {
//				epaper.setUrl("http://ipaper.cloudfoundry.com/404.html");
//			}
			
			
			return returnUrl;

		} catch (ExecutionException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

}
