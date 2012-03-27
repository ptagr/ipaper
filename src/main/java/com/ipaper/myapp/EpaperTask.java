package com.ipaper.myapp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.data.mongodb.core.MongoTemplate;

import com.Ostermiller.util.CircularByteBuffer;

public class EpaperTask {

//	public List<InputStream> pdfs = Collections
//			.synchronizedList(new ArrayList<InputStream>());

	List<CircularByteBuffer> cbbList = Collections.
			synchronizedList(new ArrayList<CircularByteBuffer>());
	
	private ByteArrayOutputStream _boutStream = new ByteArrayOutputStream();

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
		// for(String url : urls)
		// System.out.println(url);
		System.out.println("GETTING PDFs !!!! ");
		// DownloadManager dm = DownloadManager.getInstance();
		DownloadManager dm = new DownloadManager();
		//ByteArrayInputStream[] baisList = new ByteArrayInputStream[epaper.getPdfs().size()];
		
		//List<CircularByteBuffer> cbbList = new ArrayList<CircularByteBuffer>();

		try {

			for (int i = 0; i < epaper.getUrls().size(); i++) {
				dm.createDownload(new URL(epaper.getUrls().get(i)), cbbList,
						epaper.getName());
			}

			while (!dm.areDownloadsOver()) {
			}
			;
			dm = null;
			System.gc();

//			for (ByteArrayInputStream bais : baisList) {
//				if (bais != null) {
//					pdfs.add(bais);
//				}
//			}

			System.out.println("no of pages : " + cbbList.size());

			epaper.setPageCount(cbbList.size());
			//mergePDFs();
			mergeCBBPDFs();

			System.out.println("Size - " + _boutStream.size() / 1024 + "KB");
			System.out.println("ENDTIME - "
					+ new SimpleDateFormat("ddMMyyyy HH:mm:ss:SS")
							.format(new Date()));
			return _boutStream.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			
			
			cbbList.clear();
			cbbList = null;
			_boutStream.close();
			_boutStream = null;
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
	
	
	public void mergeCBBPDFs() {
		if (cbbList.isEmpty()) {
			return;
		} else {

			// System.out.println("MERGING PDFs");

			MyPDFUtility.concatPDFs(cbbList, _boutStream);
		}
	}

	public String generateEpaper(boolean delete, MongoTemplate mongoTemplate) {
		String returnUrl = "";

//		Date yesterday = Time.addandReturnDate(-1);
//		Date d = new Date(yesterday.getYear(), yesterday.getMonth(),
//				yesterday.getDate(), 0, 0, 0);
		try {
			epaper.setId();

			EpaperBase temp;
			temp = mongoTemplate.findById(epaper.getId(), EpaperBase.class,
					com.ipaper.myapp.Collections.epaperCollectionName);
			if (temp != null) {
				return temp.getUrl();
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
