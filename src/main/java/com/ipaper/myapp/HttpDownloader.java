package com.ipaper.myapp;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.Ostermiller.util.CircularByteBuffer;

public class HttpDownloader extends Downloader {

	// public HttpDownloader(URL url, String outputFolder, int numConnections,
	// int downloadNum, ByteArrayInputStream[] baisList) {
	// super(url, outputFolder, numConnections, downloadNum, baisList);
	// download();
	// }

	

	public HttpDownloader(URL url) {
		super(url);
		// download();
	}

	public HttpDownloader(String url) {
		super(url);
		// download();
	}
	
	public HttpDownloader(String url, CircularByteBuffer cbb) {
		super(url, cbb);
		// download();
	}

	private void error() {
		// System.out.println("ERROR");
		setState(ERROR);
		// System.out.println("Wrong error code :" + mURL.toString());
		// synchronized (mbaisList) {
		// mbaisList[mdownloadNum] = null;
		// }
	}

	public HttpURLConnection customConnect(int sb, int eb) throws IOException {
		// System.out.println("using superclass CUstom Connection");
		HttpURLConnection conn = null;
		conn = (HttpURLConnection) mURL.openConnection();

		if (sb != -1 && eb != -1) {
			// set the range of byte to download
			String byteRange = sb + "-" + eb;
			conn.setRequestProperty("Range", "bytes=" + byteRange);
		}

		conn.setConnectTimeout(10000);

		// Connect to server
		conn.connect();
		return conn;
	}

	

	public ByteArrayOutputStream call() throws Exception {
		HttpURLConnection conn = null;
		BufferedInputStream in = null;
		ByteArrayOutputStream _baos = null;
		

		try {
			// Open connection to URL
			initConnect();
			conn = customConnect(-1, -1);

			// Make sure the response code is in the 200 range.
			if (conn.getResponseCode() / 100 != 2) {
				setState(ERROR);
				System.out.println("Wrong error code :"
						+ conn.getResponseCode() + " : " + mURL.toString());
				return null;
			}

			// Check for valid content length.
			int contentLength = conn.getContentLength();
			System.out.println(mURL.toString() + " : " + contentLength
					+ " bytes ");

			if (contentLength < 1) {
				setState(ERROR);
				System.out.println("Invalid Content Length :" + contentLength
						+ " : " + mURL.toString());
				return null;
			}

			
			
			
			// A bit bigger than content length to allow for room
			//cbb = new CircularByteBuffer(contentLength + 1024);

			// Download the pdf in here

			// open Http connection to URL

			int startByte = 0;
			int endByte = contentLength - 1;
			try {
				conn = customConnect(startByte, endByte);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// Make sure the response code is in the 200 range.
			if (conn.getResponseCode() / 100 != 2) {
				error();
			}

			// get the input stream
			in = new BufferedInputStream(conn.getInputStream());

			//Clear the previous bytes
			//this.cbb.clear();
			
			// _baos = new ByteArrayOutputStream();
			_baos = new ByteArrayOutputStream();

			byte data[] = new byte[BUFFER_SIZE];
			int numRead;
			int totalnumRead = 0;

			while (((numRead = in.read(data, 0, BUFFER_SIZE)) != -1)) {
				// write to buffer
				totalnumRead += numRead;
				_baos.write(data, 0, numRead);
			}

			System.out.println("Total Read : " + totalnumRead);

			setState(COMPLETED);
			// System.out.println("Page downloaded : "+mURL);
			in.close();
			return _baos;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			setState(ERROR);
			return null;
		}
	}
}
