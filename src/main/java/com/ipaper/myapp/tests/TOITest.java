package com.ipaper.myapp.tests;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.ipaper.myapp.TOI;



public class TOITest {

	private static TOI toi;
	
	@BeforeClass
	public static void initialize(){
		toi = new TOI("ENGLISH","MUMBAI", new Date());
	}
	@Test
	public void test() {
		List<String> urls = toi.buildPageUrls();
		for(String url:urls){
			System.out.println(url);
		}
	}

}
