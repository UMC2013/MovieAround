package com.umc.moviearound;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Utils {
	
	public static Genre DeserializeGenre(JSONObject jsonObject) throws JSONException {
		Genre genre = new Genre();
		
		genre.setId(jsonObject.getInt("GenreId"));
		genre.setName(jsonObject.getString("Name"));
		
		return genre;
	}

	public static List<Genre> DeserializeGenreList(String jsonString) throws JSONException {
		List<Genre> genres = new LinkedList<Genre>();
		
		if (jsonString.startsWith("[") || jsonString.startsWith("{")) {
			JSONArray jsonArray = new JSONArray(jsonString.toString());
			for (int i = 0; i < jsonArray.length(); i++) {
				genres.add(DeserializeGenre(jsonArray.getJSONObject(i)));
			}
		}
		
		return genres;
	}
	
	public static Theater DeserializeTheater(JSONObject jsonObject) throws JSONException {
		Theater theater = new Theater();
		
		theater.setId(jsonObject.getInt("TheaterId"));
		theater.setName(jsonObject.getString("Name"));
		
		return theater;
	}

	public static List<Theater> DeserializeTheaterList(String jsonString) throws JSONException {
		List<Theater> theaters = new LinkedList<Theater>();
		
		if (jsonString.startsWith("[") || jsonString.startsWith("{")) {
			JSONArray jsonArray = new JSONArray(jsonString.toString());
			for (int i = 0; i < jsonArray.length(); i++) {
				theaters.add(DeserializeTheater(jsonArray.getJSONObject(i)));
			}
		}
		
		return theaters;
	}
}
