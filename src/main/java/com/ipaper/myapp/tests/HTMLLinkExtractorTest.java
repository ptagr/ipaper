package com.ipaper.myapp.tests;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.http.client.methods.HttpGet;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ipaper.myapp.HTMLLinkExtractor;
import com.ipaper.myapp.HTMLLinkExtractor.HtmlLink;
 
 
 
/**
 * HTML link extrator Testing
 * @author mkyong
 *
 */
public class HTMLLinkExtractorTest {
 
  private HTMLLinkExtractor HTMLLinkExtractor;
  private List<String> html;
  
//  @Before
//  public void initData() throws IOException{
//	HTMLLinkExtractor = new HTMLLinkExtractor();
//	URL mUrl = new URL("http://google.com/"); 
//	HttpURLConnection mConn = (HttpURLConnection) mUrl.openConnection();
//	mConn.connect();
//	
//	BufferedReader br = new BufferedReader(new InputStreamReader(mConn.getInputStream()));
//	html = new ArrayList<String>();
//	String h = null;
//	while((h = br.readLine()) != null){
//		html.add(h);
//	}
//	mConn.disconnect();
//  }
  
  @Before
  public void initData() throws IOException{
	HTMLLinkExtractor = new HTMLLinkExtractor();
//	URL mUrl = new URL("http://google.com/"); 
//	HttpURLConnection mConn = (HttpURLConnection) mUrl.openConnection();
//	mConn.connect();
	
	BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("/home/punitag/hello.html")));
	html = new ArrayList<String>();
	String h = null;
	while((h = br.readLine()) != null){
		html.add(h);
	}
	//mConn.disconnect();
  }
 
  //@DataProvider
  public Object[][] HTMLContentProvider() {
    return new Object[][]{
     new Object[] {"abc hahaha <a href='http://www.google.com'>google</a>"},
     new Object[] {"abc hahaha <a HREF='http://www.google.com'>google</a>"},
     new Object[] {"abc hahaha <A HREF='http://www.google.com'>google</A> , " +
       "abc hahaha <A HREF='http://www.google.com' target='_blank'>google</A>"},
     new Object[] {"abc hahaha <A HREF='http://www.google.com' target='_blank'>google</A>"},
     new Object[] {"abc hahaha <A target='_blank' HREF='http://www.google.com'>google</A>"},
     new Object[] {"abc hahaha <a HREF=http://www.google.com>google</a>"},
   };
}
 
  @Test
  public void ValidHTMLLinkTest() {
 
	Vector<HtmlLink> links = new Vector<HTMLLinkExtractor.HtmlLink>(); 
	for(String h : html){
			links.addAll(HTMLLinkExtractor.grabHTMLLinks(h));
	}
 
	Assert.assertTrue(links.size()!=0);
 
	for(int i=0; i<links.size() ; i++){
		HtmlLink htmlLinks = links.get(i);
		System.out.println(htmlLinks);
	}
 
  }	
}