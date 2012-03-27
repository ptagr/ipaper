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

	public HttpDownloader(URL url, String outputFolder, int numConnections,
			int downloadNum) {
		super(url, outputFolder, numConnections, downloadNum);
		//download();
	}
	
	public HttpDownloader(URL url, List<CircularByteBuffer> cbbList) {
		super(url, cbbList);
		//download();
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

	public void run() {

		// ByteArrayOutputStream[] _baos = new ByteArrayOutputStream[1 +
		// mNumConnections];
		HttpURLConnection conn = null;

		BufferedInputStream in = null;

		OutputStream _baos = null;

		CircularByteBuffer cbb = new CircularByteBuffer(CircularByteBuffer.INFINITE_SIZE);
		
		try {
			// Open connection to URL
			initConnect();
			conn = customConnect(-1, -1);

			// Make sure the response code is in the 200 range.
			if (conn.getResponseCode() / 100 != 2) {
				setState(ERROR);
				 System.out.println("Wrong error code :" +
				 conn.getResponseCode()+" : "+mURL.toString());
			}

			// Check for valid content length.
			int contentLength = conn.getContentLength();
			 System.out.println(mURL.toString() + " : " + contentLength
			 + " bytes ");

			if (contentLength < 1) {
				setState(ERROR);
				 System.out.println("Invalid Content Length :" +
				 contentLength+ " : "+ mURL.toString());
			}

			if (mFileSize == -1) {
				mFileSize = contentLength;
				// System.out.println("File size: " + mFileSize);
			}

			
			// Download the pdf in here
			
			// open Http connection to URL

			int startByte = 0;
			int endByte = mFileSize - 1;
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

			
			
			//_baos = new ByteArrayOutputStream();
			_baos = cbb.getOutputStream();
			
			
				
			byte data[] = new byte[BUFFER_SIZE];
			int numRead;
			int totalnumRead = 0;
			
			while (((numRead = in.read(data, 0, BUFFER_SIZE)) != -1)) {
				// write to buffer
				totalnumRead+=numRead;
				_baos.write(data, 0, numRead);
			}
			
			System.out.println("Total Read : "+totalnumRead);
			//Check for synchronization problem
			//mbaosList.add(_baos);
			
			cbbList.add(cbb);
			
			setState(COMPLETED);
			System.out.println("Page downloaded : "+mURL);
			in.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			setState(ERROR);
		} finally {
			
		}
	}
}
