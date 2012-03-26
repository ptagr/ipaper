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

public class EpaperTask {

	public List<InputStream> pdfs = Collections
			.synchronizedList(new ArrayList<InputStream>());

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
		List<String> urls = epaper.buildPageUrls();
		if(urls == null || urls.size() == 0){
			return new byte[0];
		}
		// for(String url : urls)
		// System.out.println(url);
		System.out.println("GETTING PDFs !!!! ");
		// DownloadManager dm = DownloadManager.getInstance();
		DownloadManager dm = new DownloadManager();
		ByteArrayInputStream[] baisList = new ByteArrayInputStream[urls.size()];

		try {

			for (int i = 0; i < urls.size(); i++) {
				dm.createDownload(new URL(urls.get(i)), null,
						epaper.getMaxNumPerConn(), i, baisList,
						epaper.getName());
			}

			while (!dm.areDownloadsOver()) {
			}
			;
			dm = null;
			System.gc();

			for (ByteArrayInputStream bais : baisList) {
				if (bais != null) {
					pdfs.add(bais);
				}
			}

			System.out.println("no of pages : " + pdfs.size());

			epaper.setPageCount(pdfs.size());
			mergePDFs();

			System.out.println("Size - " + _boutStream.size() / 1024 + "KB");
			System.out.println("ENDTIME - "
					+ new SimpleDateFormat("ddMMyyyy HH:mm:ss:SS")
							.format(new Date()));
			return _boutStream.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			for (InputStream in : pdfs) {
				in.close();
			}
			pdfs = null;
			for (ByteArrayInputStream bais : baisList) {
				if (bais != null)
					bais.close();
			}
			baisList = null;
			_boutStream.close();
			_boutStream = null;
			System.gc();
		}
	}

	public void mergePDFs() {
		if (pdfs.isEmpty()) {
			return;
		} else {

			// System.out.println("MERGING PDFs");

			MyPDFUtility.concatPDFs(pdfs, _boutStream, false);
		}
	}

	public String generateEpaper(boolean delete, MongoTemplate mongoTemplate) {
		String returnUrl = "";

		Date yesterday = Time.addandReturnDate(-1);
		Date d = new Date(yesterday.getYear(), yesterday.getMonth(),
				yesterday.getDate(), 0, 0, 0);
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
