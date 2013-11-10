package com.umc.moviearound;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpClient {
	
	public static InputStream OpenHttpConnection(String url) throws IOException {
		InputStream inputStream = null;

		URL ourl = new URL(url);
		HttpURLConnection con = (HttpURLConnection) ourl
			.openConnection();
		inputStream = con.getInputStream();

		return inputStream;	
	}
}
