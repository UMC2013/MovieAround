package com.umc.moviearound.Activity;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.umc.moviearound.AsyncTaskCompleteListener;
import com.umc.moviearound.GetTask;
import com.umc.moviearound.R;
import com.umc.moviearound.Utils;
import com.umc.moviearound.Model.Movie;
import com.umc.moviearound.R.id;
import com.umc.moviearound.R.layout;
import com.umc.moviearound.R.menu;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.Menu;
import android.widget.TextView;

public class MovieDetailsActivity extends Activity implements
AsyncTaskCompleteListener<String> {

	TextView textName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_movie_details);
		
		Bundle extras = this.getIntent().getExtras();
		int movie_id = extras.getInt("id");
		
		textName = (TextView) findViewById(R.id.textMovieTitle);
		
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
        	//call the api with the location and user's genres
        	String url = String.format("movies/%s", movie_id);
            new GetTask(this).execute(url);
        }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.movie_details, menu);
		return true;
	}

	@Override
	public void onTaskComplete(String result) {
		try {
			Movie movie = Utils.DeserializeMovie(new JSONObject(result));
			if (movie != null) {
				textName.setText(movie.getTitle());
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
