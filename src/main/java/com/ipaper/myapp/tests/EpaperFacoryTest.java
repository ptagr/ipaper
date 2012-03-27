package com.ipaper.myapp.tests;

import static org.junit.Assert.*;

import org.aspectj.lang.annotation.Before;
import org.junit.Test;

import com.ipaper.myapp.EpaperFactory;
import com.ipaper.myapp.Language;

public class EpaperFacoryTest {
	
	EpaperFactory ef;
	
	@org.junit.Before
	public void init(){
		ef= new EpaperFactory();
		ef.setEpapers(new String[]{"HD","DNA"});
	}
	
	
	@Test
	public void test() {
		
		assertNotNull(ef.getAllEpapers());
		assertNotNull(ef.getAllSupportedPapers());
		assertTrue(ef.getAllSupportedCitiesByLanguage("HD",Language.HINDI).size() == 4);
		
		
	}

}
