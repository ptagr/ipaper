package com.ipaper.myapp;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class HttpDownloader extends Downloader {

	// public HttpDownloader(URL url, String outputFolder, int numConnections,
	// int downloadNum, ByteArrayInputStream[] baisList) {
	// super(url, outputFolder, numConnections, downloadNum, baisList);
	// download();
	// }

	public HttpDownloader(URL url, String outputFolder, int numConnections,
			int downloadNum, ByteArrayInputStream[] baisList) {
		super(url, outputFolder, numConnections, downloadNum, baisList);
		download();
	}

	private void error() {
		// System.out.println("ERROR");
		setState(ERROR);
		//System.out.println("Wrong error code :" + mURL.toString());
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

		ByteArrayOutputStream baos = null;
		ByteArrayOutputStream[] _baos = new ByteArrayOutputStream[1 + mNumConnections];
		HttpURLConnection conn = null;
		for (int i = 0; i < 1 + mNumConnections; i++)
			_baos[i] = null;

		try {
			// Open connection to URL
			initConnect();
			conn = customConnect(-1, -1);

			// Make sure the response code is in the 200 range.
			if (conn.getResponseCode() / 100 != 2) {
				setState(ERROR);
				//System.out.println("Wrong error code :" + conn.getResponseCode()+" : "+mURL.toString());
			}

			// Check for valid content length.
			int contentLength = conn.getContentLength();
//			System.out.println(mURL.toString() + " : " + contentLength
//					+ " bytes ");
			
			if (contentLength < 1) {
				setState(ERROR);
				//System.out.println("Invalid Content Length :" + contentLength+ " : "+ mURL.toString());
			}

			if (mFileSize == -1) {
				mFileSize = contentLength;
				stateChanged();
				// System.out.println("File size: " + mFileSize);
			}

			// if the state is DOWNLOADING (no error) -> start downloading
			if (mState == DOWNLOADING) {
				// check whether we have list of download threads or not, if not
				// -> init download
				if (mListDownloadThread.size() == 0) {
					if (mFileSize > MIN_DOWNLOAD_SIZE && mNumConnections > 1) {
						// downloading size for each thread
						int partSize = Math
								.round(((float) mFileSize / mNumConnections)
										/ BLOCK_SIZE)
								* BLOCK_SIZE;
						// System.out.println("Part size: " + partSize);

						// start/end Byte for each thread
						int startByte = 0;
						int endByte = partSize - 1;
						HttpDownloadThread aThread = new HttpDownloadThread(0,
								mURL, mOutputFolder + mFileName, startByte,
								endByte, _baos, 0);
						mListDownloadThread.add(aThread);
						int i = 1;
						while (endByte < mFileSize) {
							startByte = endByte + 1;
							endByte += partSize;
							aThread = new HttpDownloadThread(i, mURL,
									mOutputFolder + mFileName, startByte,
									endByte, _baos, i);
							mListDownloadThread.add(aThread);
							++i;
						}
					} else {
						HttpDownloadThread aThread = new HttpDownloadThread(0,
								mURL, mOutputFolder + mFileName, 0, mFileSize,
								_baos, 0);
						mListDownloadThread.add(aThread);
					}
				} else { // resume all downloading threads
					for (int i = 0; i < mListDownloadThread.size(); ++i) {
						if (!mListDownloadThread.get(i).isFinished())
							mListDownloadThread.get(i).download();
					}
				}

				// waiting for all threads to complete
				for (int i = 0; i < mListDownloadThread.size(); ++i) {
					mListDownloadThread.get(i).waitFinish();
				}

				synchronized (mbaisList) {
					baos = new ByteArrayOutputStream();
					for (ByteArrayOutputStream b : _baos) {
						if (b != null)
							b.writeTo(baos);
					}

//					System.out.print("Url : " + mURL);
//					System.out.println("       Size of page :" + baos.size());

					if (baos.size() != 0 && baos.size() > 1024)
						mbaisList[mdownloadNum] = new ByteArrayInputStream(
								baos.toByteArray());
				}

				// check the current state again
				if (mState == DOWNLOADING) {
					setState(COMPLETED);
				}

			}
		} catch (Exception e) {
			error();
		} finally {
			if (conn != null)
				conn.disconnect();
			try {
				for (ByteArrayOutputStream b : _baos) {
					if (b != null)
						b.close();
				}
				_baos = null;
				if (baos != null) {
					baos.close();
				}
				System.gc();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	/**
	 * Thread using Http protocol to download a part of file
	 */
	private class HttpDownloadThread extends DownloadThread {

		/**
		 * Constructor
		 * 
		 * @param threadID
		 * @param url
		 * @param outputFile
		 * @param startByte
		 * @param endByte
		 */
		public HttpDownloadThread(int threadID, URL url, String outputFile,
				int startByte, int endByte, ByteArrayOutputStream[] _baosList,
				int partNum) {
			super(threadID, url, outputFile, startByte, endByte, _baosList,
					partNum);
		}

		public void run() {
			BufferedInputStream in = null;
			// RandomAccessFile raf = null;
			ByteArrayOutputStream _baos = null;
			HttpURLConnection conn = null;
			try {
				// open Http connection to URL

				conn = customConnect(mStartByte, mEndByte);

				// Make sure the response code is in the 200 range.
				if (conn.getResponseCode() / 100 != 2) {
					error();
				}

				// get the input stream
				in = new BufferedInputStream(conn.getInputStream());

				// open the output file and seek to the start location
				// raf = new RandomAccessFile(mOutputFile, "rw");
				// raf.seek(mStartByte);
				_baos = new ByteArrayOutputStream();

				byte data[] = new byte[BUFFER_SIZE];
				int numRead;
				while ((mState == DOWNLOADING)
						&& ((numRead = in.read(data, 0, BUFFER_SIZE)) != -1)) {
					// write to buffer
					// raf.write(data,0,numRead);
					_baos.write(data, 0, numRead);

					// increase the startByte for resume later
					mStartByte += numRead;

					// increase the downloaded size
					downloaded(numRead);
				}

				if (mState == DOWNLOADING) {
					mIsFinished = true;
				}

				synchronized (mbaosList) {
					mbaosList[mPartNum] = _baos;
				}

			} catch (IOException e) {
				error();
			} finally {
				// if (raf != null) {
				// try {
				// raf.close();
				// } catch (IOException e) {}
				// }

				if (in != null) {
					try {
						//System.out.println("Closing Connections");
						in.close();
					} catch (IOException e) {
					}
				}else if(conn != null){
					conn.disconnect();
				}
			}

			// System.out.println("End thread " + mThreadID);
		}
	}
}
