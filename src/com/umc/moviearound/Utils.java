package com.umc.moviearound;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.umc.moviearound.Model.*;

public class Utils {
	
	public static Genre DeserializeGenre(JSONObject jsonObject) {
		Genre genre = new Genre();
		
		try {
			genre.setId(jsonObject.getInt("GenreId"));
			genre.setName(jsonObject.getString("Name"));
		}
		catch (JSONException e) {
			return null;
		}
		
		return genre;
	}

	public static List<Genre> DeserializeGenreList(String jsonString) throws JSONException {
		List<Genre> genres = new LinkedList<Genre>();
		
		if (jsonString.startsWith("[") || jsonString.startsWith("{")) {
			JSONArray jsonArray = new JSONArray(jsonString.toString());
			for (int i = 0; i < jsonArray.length(); i++) {
				Genre genre = DeserializeGenre(jsonArray.getJSONObject(i));
				if (genre != null)
					genres.add(genre);
			}
		}
		
		return genres;
	}
	
	public static Movie DeserializeMovie(JSONObject jsonObject) throws JSONException {
		Movie movie = new Movie();
		
		movie.setId(jsonObject.getInt("MovieId"));
		
		if (jsonObject.has("MovieTitle"))
			movie.setTitle(jsonObject.getString("MovieTitle"));
		else if (jsonObject.has("Title"))
			movie.setTitle(jsonObject.getString("Title"));
		
		if (jsonObject.has("Synopsis"))
			movie.setSynopsis(jsonObject.getString("Synopsis"));
		
		if (jsonObject.has("Genres")) {
			List<Genre> generos = DeserializeGenreList(jsonObject.getString("Genres"));
			movie.setGenre(generos.get(0).toString());
			
//			movie.setGenre(jsonObject.getString("Genres"));
//			movie.setGenre(genre.getName());
//			Toast.makeText(null, genre.toString(), Toast.LENGTH_SHORT).show();
		}
		
		if (jsonObject.has("Theaters")) {
			JSONArray theatersArray = jsonObject.getJSONArray("Theaters");
			for (int i = 0; i < theatersArray.length(); i++) {
				JSONObject theaterObject = theatersArray.getJSONObject(i);
				Theater theater = DeserializeTheater(theaterObject);
				movie.addTheater(theater);
			}
		}
		
		return movie;
	}

	public static List<Movie> DeserializeMovieList(String jsonString) throws JSONException {
		List<Movie> movies = new LinkedList<Movie>();
		
		if (jsonString.startsWith("[") || jsonString.startsWith("{")) {
			JSONArray jsonArray = new JSONArray(jsonString.toString());
			for (int i = 0; i < jsonArray.length(); i++) {
				movies.add(DeserializeMovie(jsonArray.getJSONObject(i)));
			}
		}
		
		return movies;
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
