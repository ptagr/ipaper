package com.sugarsync;

import java.io.File;
import java.io.IOException;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.FileRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.dom4j.Document;

public class GetAuth {

	private static String END_POINT = "https://api.sugarsync.com/authorization";
    
 	public static String getAuthQuest() throws HttpException, IOException {
		HttpClient client = new HttpClient();
		PostMethod post = new PostMethod(END_POINT);
		File input = new File("/resources/Auth.xml");
		RequestEntity entity = new FileRequestEntity(input, "application/xml; charset=UTF-8");
		post.setRequestEntity(entity);
		client.executeMethod(post);
		System.out.println("Response status code: " + post.getStatusCode());
		System.out.println("Response body: ");
		System.out.println(post.getResponseBodyAsString());
		System.out.println("Response header: ");
		Header[] headers=post.getResponseHeaders();

		for (int i = 0; i < headers.length; i++) {
			System.out.println(headers[i]);
		}

		return post.getResponseHeader("Location").getValue();
	}

	public static void main(String[] args) throws HttpException, IOException {
		GetAuth.getAuthQuest();
	}
}