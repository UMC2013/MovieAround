package com.umc.moviearound.Model;

import java.util.LinkedList;
import java.util.List;

public class Movie {

		private int id;
		private String title;
		private String synopsis;
		private String genre;
		
		private List<Theater> theaters = new LinkedList<Theater>();
		
		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public String getSynopsis() {
			return synopsis;
		}
		public void setSynopsis(String synopsis) {
			this.synopsis = synopsis;
		}
		public String getGenre() {
			return genre;
		}
		public void setGenre(String genre) {
			this.genre = genre;
		}
		
		public String toString() {
			return title;
		}
		
		public List<Theater> getTheaters() {
			return theaters;
		}
		
		public void addTheater(Theater theater)
		{
			theaters.add(theater);
		}
}
