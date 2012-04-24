//package com.ipaper.myapp;
//
//import java.awt.Graphics2D;
//import java.awt.Image;
//import java.awt.Rectangle;
//import java.awt.image.BufferedImage;
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.net.URL;
//import java.nio.ByteBuffer;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Date;
//import java.util.List;
//import java.util.concurrent.ExecutionException;
//
//import javax.imageio.ImageIO;
//
//import com.sun.pdfview.PDFFile;
//import com.sun.pdfview.PDFPage;
//
//public class CopyOfEpaperTask {
//	public final List<String> files = Collections
//			.synchronizedList(new ArrayList<String>());
//
//	public final List<InputStream> pdfs = Collections
//			.synchronizedList(new ArrayList<InputStream>());
//
//	private ByteArrayOutputStream _boutStream = new ByteArrayOutputStream();
//	private ByteArrayOutputStream _boutStreamJPG = new ByteArrayOutputStream();
//
//	private OutputStream _outStream = null;
//
//	protected EpaperBase epaper = null;
//
//	protected CopyOfEpaperTask(EpaperBase epaper) {
//		this(epaper, null);
//
//	}
//
//	protected CopyOfEpaperTask(EpaperBase epaper, OutputStream outs) {
//		this.epaper = epaper;
//		this._outStream = outs;
//
//	}
//
//	public String getThreeDigitString(int no) {
//		if (no >= 0 && no <= 9) {
//			return "00" + no;
//
//		} else if (no <= 99) {
//			return "0" + no;
//		} else {
//			return "Error";
//		}
//
//	}
//
//	public Boolean getCompletePdf() throws ExecutionException, IOException {
//		try {
//
//			System.out
//					.println("********************************************************");
//			System.out
//					.println("***********    CREATED BY -- Punit Agrawal   ***********");
//			System.out
//					.println("*******    Contact me @ punit.agrawal@wsu.edu  *********");
//			System.out
//					.println("********************************************************");
//			System.out.println("");
//			final String fileName = epaper.buildFileName();
//			SimpleDateFormat format = new SimpleDateFormat("ddMMyyyy");
//
//			DropboxSample ds = DropboxSample.getInstance();
//			DropboxAPI.Account account = ds.getAccountInfo();
//
//			System.out.println("copyofepapertask - ACCOUNT INFO : " + account.email);
//
//			Entry e = ds.getAPI().metadata(
//					"dropbox",
//					"/Public/epaper" + format.format(epaper.getDate()) + "/"
//							+ fileName, 10000, null, true);
//			if (e.bytes > 2000) {
////				epaper.setUrl("http://dl.dropbox.com/u/" + account.uid
////						+ "/epaper" + format.format(epaper.getDate()) + "/"
////						+ fileName);
//				epaper.setThumbnailUrl("http://dl.dropbox.com/u/" + account.uid
//						+ "/epaper" + format.format(epaper.getDate()) + "/"
//						+ changeExtension(fileName, "jpg"));
//				return true;
//			}
//
//			List<String> urls = epaper.buildPageUrls();
//			System.out.println("GETTING PDFs !!!! ");
//
////			for (int i = 0; i < urls.size(); i++) {
////				System.out.println("Fetching page : " + urls.get(i));
////				Worker c = new Worker(urls.get(i), i, epaper.getName());
////				InputStream in = c.call();
////				if (in != null) {
////					pdfs.add(in);
////				}
////			}
//			
////			DownloadManager dm = DownloadManager.getInstance();
////			ByteArrayInputStream[] baisList = new ByteArrayInputStream[urls.size()];
////			for (int i = 0; i < urls.size(); i++) {
////				System.out.println("Fetching page : " + urls.get(i));
////				dm.createDownload(new URL(urls.get(i)), null, i, baisList); 
////			}
////			
////			while(!dm.areDownloadsOver()){};
////				
////			for(ByteArrayInputStream bais:baisList){
////				if(bais != null)
////					pdfs.add(bais);	
////			}
//			
//			epaper.setPageCount(pdfs.size());
//			mergePDFs();
//			//createThumbnail();
//
//			System.out
//					.println("Starting background job to upload to dropbox : Filename - "
//							+ fileName
//							+ "Size - "
//							+ _boutStream.size()
//							/ 1024
//							+ "KB");
////			epaper.setUrl(EpaperManager.persistNewspaper(fileName,
////					_boutStream.toByteArray(), format.format(epaper.getDate())));
//
//			System.out
//					.println("Starting background job to upload to dropbox : Filename - "
//							+ changeExtension(fileName, "jpg")
//							+ "Size - "
//							+ _boutStreamJPG.size() / 1024 + "KB");
////			epaper.setThumbnailUrl(EpaperManager.persistNewspaper(
////					changeExtension(fileName, "jpg"),
////					_boutStreamJPG.toByteArray(),
////					format.format(epaper.getDate())));
//
//			System.out.println("Background Upload Jobs finished");
//
//			if (_outStream != null)
//				_boutStream.writeTo(_outStream);
//			/*
//			 * new Thread(new Runnable() {
//			 * 
//			 * @Override public void run() { // TODO Auto-generated method stub
//			 * 
//			 * } }).start();
//			 */
//			System.out.println("PDFs Merged");
//			return false;
//		} catch (Exception e) {
//			e.printStackTrace();
//			return false;
//		} finally {
//			for (InputStream in : pdfs) {
//				in.close();
//			}
//			_boutStream.close();
//			_boutStreamJPG.close();
//
//		}
//	}
//
//	
//	public byte[] getDirectCompletePdf() throws ExecutionException, IOException {
//		try {
//
//			System.out.println("STARTTIME - "+new SimpleDateFormat("ddMMyyyy HH:mm:ss:SS").format(new Date()));
//			
////			final String fileName = epaper.buildFileName();
////			SimpleDateFormat format = new SimpleDateFormat("ddMMyyyy");
//
//			List<String> urls = epaper.buildPageUrls();
//			System.out.println("GETTING PDFs !!!! ");
//			
////			DownloadManager dm = DownloadManager.getInstance();
////			ByteArrayInputStream[] baisList = new ByteArrayInputStream[urls.size()];
////			for (int i = 0; i < urls.size(); i++) {
////				//System.out.println("Fetching page : " + urls.get(i));
////				dm.createDownload(new URL(urls.get(i)), null, i, baisList); 
////			}
////			
////			while(!dm.areDownloadsOver()){};
//				
////			for(ByteArrayInputStream bais:baisList){
////				if(bais != null)
////					pdfs.add(bais);	
////			}
//			
//			epaper.setPageCount(pdfs.size());
//			mergePDFs();
//
////			System.out
////					.println("Filename - "
////							+ fileName
////							+ "Size - "
////							+ _boutStream.size()
////							/ 1024
////							+ "KB");
////			epaper.setUrl(EpaperManager.persistNewspaper(fileName,
////					_boutStream.toByteArray(), format.format(epaper.getDate())));
//
////			System.out
////					.println("Starting background job to upload to dropbox : Filename - "
////							+ changeExtension(fileName, "jpg")
////							+ "Size - "
////							+ _boutStreamJPG.size() / 1024 + "KB");
////			
////			epaper.setThumbnailUrl(EpaperManager.persistNewspaper(
////					changeExtension(fileName, "jpg"),
////					_boutStreamJPG.toByteArray(),
////					format.format(epaper.getDate())));
////
////			System.out.println("Background Upload Jobs finished");
//
////			if (_outStream != null)
////				_boutStream.writeTo(_outStream);
//			
////			System.out.println("PDFs Merged");
//			System.out.println("ENDTIME - "+new SimpleDateFormat("ddMMyyyy HH:mm:ss:SS").format(new Date()));
//			return _boutStream.toByteArray();
//		} catch (Exception e) {
//			e.printStackTrace();
//			return null;
//		} finally {
//			for (InputStream in : pdfs) {
//				in.close();
//			}
//			_boutStream.close();
//			_boutStreamJPG.close();
//
//		}
//	}
//	
//	public void mergePDFs() {
//		if (pdfs.isEmpty()) {
//			return;
//		} else {
//
//			System.out.println("MERGING PDFs");
//
//			MyPDFUtility.concatPDFs(pdfs, _boutStream, false);
//		}
//	}
//
//	public void createThumbnail() {
//		ByteBuffer bbuf;
//		PDFFile pdf;
//		PDFPage page;
//		try {
//			bbuf = ByteBuffer.wrap(_boutStream.toByteArray());
//			 pdf = new PDFFile(bbuf);
//			 page = pdf.getPage(0);
//
//			// create the image
//			Rectangle rect = new Rectangle(0, 0, (int) page.getBBox()
//					.getWidth(), (int) page.getBBox().getHeight());
//			BufferedImage bufferedImage = new BufferedImage(rect.width,
//					rect.height, BufferedImage.TYPE_INT_RGB);
//
//			Image image = page.getImage(rect.width, rect.height, // width &
//																	// height
//					rect, // clip rect
//					null, // null for the ImageObserver
//					true, // fill background with white
//					true // block until drawing is done
//					);
//			Graphics2D bufImageGraphics = bufferedImage.createGraphics();
//			bufImageGraphics.drawImage(image, 0, 0, null);
//			bufImageGraphics.dispose();
//			ImageIO.write(bufferedImage, "JPEG", _boutStreamJPG);
//			
//		} catch (IOException ie) {
//			ie.printStackTrace();
//		} finally {
//			
//		}
//	}
//
//	public static String changeExtension(String originalName,
//			String newExtension) {
//		int lastDot = originalName.lastIndexOf(".");
//		if (lastDot != -1) {
//			return originalName.substring(0, lastDot + 1) + newExtension;
//		} else {
//			return originalName + newExtension;
//		}
//	}// end changeExtension
//
//}
