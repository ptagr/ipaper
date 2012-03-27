package com.dropbox.client2;

import java.io.ByteArrayInputStream;
import java.io.PrintWriter;

import org.apache.http.HttpResponse;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Account;

import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.RequestTokenPair;
import com.dropbox.client2.session.Session.AccessType;
import com.dropbox.client2.session.WebAuthSession;

public class DropboxMain {
	final static private String APP_KEY = "ma2gq2wq1t6xf6k";
	final static private String APP_SECRET = "ib8q27zr90tmu7r";
	//static private String ACCESS_TOKEN_KEY = "ulg9dkuflah3wi3"; // Read
	  static private String ACCESS_TOKEN_KEY = "b8alqay26wuviaa"; // Read
	 // key
	  static private String ACCESS_TOKEN_SECRET = "zxl452rs48y3g3b";
	  //static private String ACCESS_TOKEN_SECRET = "2u7eo3j7aj9skmk";
//	static private String ACCESS_TOKEN_KEY = ""; // Read
//	// key
//	static private String ACCESS_TOKEN_SECRET = "";
	final static private AccessType ACCESS_TYPE = AccessType.DROPBOX;

	private DropboxAPI<WebAuthSession> mDBApi;

	private static DropboxMain instance;

	private DropboxMain() {

		AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
		WebAuthSession session = new WebAuthSession(appKeys, ACCESS_TYPE);
		mDBApi = new DropboxAPI<WebAuthSession>(session);

		if (ACCESS_TOKEN_KEY == "" || ACCESS_TOKEN_SECRET == "") {
			try {
				System.out.println("Please go to this URL and hit \"Allow\": ");
				System.out.println(mDBApi.getSession().getAuthInfo().url);

				Thread.currentThread().sleep(10000);

				AccessTokenPair tokenPair = mDBApi.getSession()
						.getAccessTokenPair();

				// Wait for user to Allow app in browser
				System.out.println("Finished allowing?  Enter 'next' if so: ");

				RequestTokenPair tokens = new RequestTokenPair(tokenPair.key,
						tokenPair.secret);

				mDBApi.getSession().retrieveWebAccessToken(tokens);
				ACCESS_TOKEN_KEY = session.getAccessTokenPair().key;

				ACCESS_TOKEN_SECRET = session.getAccessTokenPair().secret;
				System.out.println(ACCESS_TOKEN_KEY);
				System.out.println(ACCESS_TOKEN_SECRET);
				System.out.println("Authentication Successful!");

			} catch (DropboxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		// System.out.println("Please go to this URL and hit \"Allow\": " +
		// mDBApi.getSession().getAuthInfo().url); // tell user to go to app
		// allowance URL
		// AccessTokenPair tokenPair = mDBApi.getSession().getAccessTokenPair();

		AccessTokenPair reAuthTokens = new AccessTokenPair(ACCESS_TOKEN_KEY,
				ACCESS_TOKEN_SECRET);
		mDBApi.getSession().setAccessTokenPair(reAuthTokens);
		System.out.println("Re-authentication Sucessful!");

		// Run test command

	}

	public static DropboxMain getInstance() {
		if (instance == null)
			instance = new DropboxMain();
		return instance;
	}

	public Account getAccountInfo() {
		try {
			return mDBApi.accountInfo();
		} catch (DropboxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public Entry getMetaData(String filepath, int fileLimit, String hash,
			boolean list, String rev) {
		try {
			// /String url_path = "/files/" + root + path;

			return mDBApi.metadata(filepath, fileLimit, hash, list, rev);
		} catch (DropboxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public Entry getMetaData(String filepath) {
		try {
			// /String url_path = "/files/" + root + path;

			return mDBApi.metadata(filepath, 0, null, false, null);
		} catch (DropboxException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return null;
		}
	}

	public void putFile(String filepath, byte[] localFile) {
		try {
			// String url_path = "/files/" + root + path;
			ByteArrayInputStream inputStream = new ByteArrayInputStream(
					localFile);
			Entry newEntry = mDBApi.putFile(filepath, inputStream,
					localFile.length, null, null);
			System.out.println(newEntry.fileName());
			// HttpResponse resp = mDBApi.putFile(root, dbPath, localFile,
			// fileName);
			// System.out.println("Response Code : "+resp.toString());

		} catch (DropboxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void deleteFile(String filepath) {
		try {

			mDBApi.delete(filepath);

		} catch (DropboxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		DropboxMain main = DropboxMain.getInstance();
		String file = "HelloWorld2 !!!";

		main.putFile("/Public/hello2.txt", file.getBytes());

		Account a = main.getAccountInfo();

		Entry e = main.getMetaData("/Public/hello2.txt");

		String uploadedUrl = "http://dl.dropbox.com/u/" + a.uid + "/hello2.txt";

		System.out.println(uploadedUrl);

		main.deleteFile("/Public/hello.txt");
	}

}
