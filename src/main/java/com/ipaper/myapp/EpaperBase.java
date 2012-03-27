package com.ipaper.myapp;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;


import com.dropbox.client2.DropboxAPI.Account;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.DropboxMain;
import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;

@Document
public abstract class EpaperBase {
	@Id
	protected String id;

	protected String name;
	protected String description;
	protected String url;
	protected Language lang;
	protected String city;
	protected Date date;
	protected String thumbnailUrl;
	protected int expiryTimeInterval;
	protected int maxNumPerConn;
	protected Date generatedTime;
	protected float size;
	
	@Transient
	protected transient List<InputStream> pdfs = Collections
			.synchronizedList(new ArrayList<InputStream>());
			
	@Transient
	protected transient List<String> urls = Collections
			.synchronizedList(new ArrayList<String>());
	// protected EnumSet<Language> languages;

	// /protected EnumSet<Day> days;
	int pageCount;

	/* Map of language to list of cities */
	protected HashMap<Language, Set<String>> citymap;

	public EpaperBase() {
		/*
		 * languages = EnumSet.noneOf(Language.class); days =
		 * EnumSet.noneOf(Day.class); cities = new
		 */

	}

	public EpaperBase(String lang, String city, Date date) {
		if (lang != null)
			this.lang = Language.valueOf(lang);
		this.city = city;
		this.date = date;
		this.expiryTimeInterval = 30 * 60;
		this.maxNumPerConn = 8;
		this.generatedTime = Time.getCurrentIndiaTime();
		this.size = 0;
	}

	public abstract void buildPageUrls();

	public String buildFileName() {
		StringBuilder paperName = new StringBuilder(name);
		String pageStr;
		if (date != null) {
			pageStr = new SimpleDateFormat("ddMMyyyy").format(date);
			paperName.append("_").append(pageStr);
		}
		if (lang != null)
			paperName.append("_").append(lang.name());

		if (city != null)
			paperName.append("_").append(city).append(".pdf");
		return paperName.toString();
	}
	
	

	public List<String> getUrls() {
		return urls;
	}

	public void setUrls(List<String> urls) {
		this.urls = urls;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getPageCount() {
		return pageCount;
	}

	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}

	public HashMap<Language, Set<String>> getCitymap() {
		return citymap;
	}

	public void setCitymap(HashMap<Language, Set<String>> citymap) {
		this.citymap = citymap;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Language getLang() {
		return lang;
	}

	public void setLang(Language lang) {
		this.lang = lang;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getThumbnailUrl() {
		return thumbnailUrl;
	}

	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}

	public int getExpiryTimeInterval() {
		return expiryTimeInterval;
	}

	public void setExpiryTimeInterval(int expiryTimeInterval) {
		this.expiryTimeInterval = expiryTimeInterval;
	}

	public int getMaxNumPerConn() {
		return maxNumPerConn;
	}

	public void setMaxNumPerConn(int maxNumPerConn) {
		this.maxNumPerConn = maxNumPerConn;
	}

	public Date getGeneratedTime() {
		return generatedTime;
	}

	public void setGeneratedTime(Date generatedTime) {
		this.generatedTime = generatedTime;
	}
	
	

	// public boolean isPaperOnDropbox() {
	// SimpleDateFormat format = new SimpleDateFormat("ddMMyyyy");
	// DropboxSample ds = DropboxSample.getInstance();
	// DropboxAPI.Account account = ds.getAccountInfo();
	//
	//
	// String fileName = buildFileName();
	// System.out.println("isPaperOnDropbox called --> "+ getId() +" --> " +
	// Time.getCurrentIndiaTime().toGMTString());
	// Entry e = ds.getAPI().metadata("dropbox",
	// "/Public/epaper" + format.format(getDate()) + "/" + fileName,
	// 10000, null, true);
	//
	// if (e.bytes > 2000) {
	// setUrl("http://dl.dropbox.com/u/" + account.uid + "/epaper"
	// + format.format(getDate()) + "/" + fileName);
	// setThumbnailUrl("http://dl.dropbox.com/u/" + account.uid
	// + "/epaper" + format.format(getDate()) + "/"
	// + changeExtension(fileName, "jpg"));
	// return true;
	// }
	//
	// return false;
	// }

	public List<InputStream> getPdfs() {
		return pdfs;
	}

	public void setPdfs(List<InputStream> pdfs) {
		this.pdfs = pdfs;
	}
	
	public void insertPdf(InputStream pdf) {
		this.pdfs.add(pdf);
	}

	public String uploadToDropbox(byte content[]) {
		SimpleDateFormat format = new SimpleDateFormat("ddMMyyyy");

		if (content == null || content.length == 0) {
			System.out.println("Content size 0");
			return null;
		}

//		DropboxSample ds = DropboxSample.getInstance();
		DropboxMain ds = DropboxMain.getInstance();
		Account account = ds.getAccountInfo();

		// System.out.println("ACCOUNT INFO : " + account.email);

		// Build dropbox filename
		String fileName = buildFileName();

		// Check for metadata on dropbox if paper exists
		String dirPath = "epaper" + format.format(getDate()) + "/" + fileName;
		
		Entry e = ds.getMetaData("/Public/"+dirPath);
//		Entry e = ds.getAPI().metadata("dropbox",
//				"/Public/epaper" + format.format(getDate()) + "/" + fileName,
//				10000, null, true);

		// the uploaded url
		String uploadedUrl = "http://dl.dropbox.com/u/" + account.uid
				+ "/"+dirPath;

		if (e != null && e.bytes > 2000) {
			System.out.println("FILE ALREADY EXISTS");
		} else {
			ds.putFile("/Public/"+dirPath, content);
//			ds.getAPI().putFile("dropbox",
//					"/Public/epaper" + format.format(getDate()), content,
//					fileName);
		}

		setUrl(uploadedUrl);
		setSize(content.length/(1024*1024));

		try {

			// Build dropbox thumbnail filename
			String tnfileName = changeExtension(fileName, "png");
			
			String tndirPath = "epaper" + format.format(getDate()) + "/" + tnfileName;
			
			// Check for metadata on dropbox if thumbnail of paper exists
			Entry tne = ds.getMetaData("/Public/"+tndirPath);

//			Entry tne = ds.getAPI().metadata(
//					"dropbox",
//					"/Public/epaper" + format.format(getDate()) + "/"
//							+ tnfileName, 10000, null, true);
			// the uploaded url of thumbnail
			String tnuploadedUrl = "http://dl.dropbox.com/u/" + account.uid
					+ "/"+tndirPath;

			if (tne != null && tne.bytes > 2000) {
				System.out.println("FILE ALREADY EXISTS");
			} else {
				ByteBuffer bbuf = ByteBuffer.wrap(content);
				PDFFile pdf = new PDFFile(bbuf);
				PDFPage page = pdf.getPage(0);
				ByteArrayInputStream bais = new ByteArrayInputStream(content);
				byte image[] = PDFToImage.convertToImage2(bais);
				if (image != null){
					ds.putFile("/Public/"+tndirPath, image);
//					ds.getAPI().putFile("dropbox",
//							"/Public/epaper" + format.format(getDate()), image,
//							tnfileName);
					
				}
				bais.close();
			}
			
			setThumbnailUrl(tnuploadedUrl);

		} catch (IOException ie) {
			// swallow exception
			// ie.printStackTrace();
		}

		// setThumbnailUrl("http://dl.dropbox.com/u/" + account.uid + "/epaper"
		// + format.format(getDate()) + "/"
		// + changeExtension(fileName, "jpg"));

		return uploadedUrl;
	}

	// public byte[] createThumbnail(PDFPage page) {
	//
	// ByteArrayOutputStream _boutStreamJPG = new ByteArrayOutputStream();
	// try {
	//
	// // create the image
	// Rectangle rect = new Rectangle(0, 0, (int) page.getBBox()
	// .getWidth(), (int) page.getBBox().getHeight());
	// BufferedImage bufferedImage = new BufferedImage(rect.width,
	// rect.height, BufferedImage.TYPE_INT_RGB);
	//
	// Image image = page.getImage(rect.width, rect.height, // width &
	// // height
	// rect, // clip rect
	// null, // null for the ImageObserver
	// true, // fill background with white
	// true // block until drawing is done
	// );
	// Graphics2D bufImageGraphics = bufferedImage.createGraphics();
	// bufImageGraphics.drawImage(image, 0, 0, null);
	// bufImageGraphics.dispose();
	// ImageIO.write(bufferedImage, "JPEG", _boutStreamJPG);
	// return _boutStreamJPG.toByteArray();
	// } catch (IOException ie) {
	// ie.printStackTrace();
	// return null;
	// }
	// }

	// public void startDeleteTask(MongoTemplate mongoTemplate) {
	//
	// DeleteEpaperTask.deleteEpaper("dropbox",
	// "/Public/epaper" + format.format(getDate()) + "/" + fileName);
	// mongoTemplate.remove(new Query(Criteria.where("_id").is(getId())),
	// Collections.epaperCollectionName);
	// return;
	// }

	public static String changeExtension(String originalName,
			String newExtension) {
		int lastDot = originalName.lastIndexOf(".");
		if (lastDot != -1) {
			return originalName.substring(0, lastDot + 1) + newExtension;
		} else {
			return originalName + newExtension;
		}
	}// end changeExtension

	public String getThreeDigitString(int no) {
		if (no >= 0 && no <= 9) {
			return "00" + no;

		} else if (no <= 99) {
			return "0" + no;
		} else {
			return "Error";
		}

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setId() {
		setId(buildFileName());
	}

	public float getSize() {
		return size;
	}

	public void setSize(float size) {
		this.size = size;
	}
	
	

}
