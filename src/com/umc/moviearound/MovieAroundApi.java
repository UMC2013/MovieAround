package com.umc.moviearound;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MovieAroundApi {

	private static String _baseURL = "http://moviearound.azurewebsites.net/api/";

	public String getJson(String url) throws IOException {
		String json = "";
		json = readJson(_baseURL + url);
		
		return json;
	}
	
	private static String readJson(String url) throws IOException {
		StringBuilder builder = new StringBuilder();

		InputStream content = HttpClient.OpenHttpConnection(url);
		if (content != null) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(content));
			String line;
			while ((line = reader.readLine()) != null) {
				builder.append(line);
			}
			content.close();
			return builder.toString();
		}

		return null;
	}
}


