package com.umc.moviearound.Model;

public class Movie {

		private int id;
		private String title;
		private String synopsis;
		private String genre;
		
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
}
