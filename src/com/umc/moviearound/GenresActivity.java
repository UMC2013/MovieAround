package com.umc.moviearound;

import java.io.IOException;
import java.util.List;

import org.json.JSONException;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class GenresActivity extends Activity implements 
	AsyncTaskCompleteListener<String>, 
	OnItemSelectedListener  {

	private TextView textView;
	private Spinner genresSpinner;
	private String selectedGenre;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_genres);
		
		textView = (TextView)findViewById(R.id.textView1);
		genresSpinner = (Spinner)findViewById(R.id.spinnerGenres);
		
		genresSpinner.setOnItemSelectedListener(this);
		
		textView.setText("Loading...");
		
		//todo: carregar a lista de genêros já salvos localmente do usuário
		
        ConnectivityManager connMgr = (ConnectivityManager) 
            getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new GetTask(this).execute("genres");
        } else {
            textView.setText("No network connection available.");
        }
        
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.genres, menu);
		return true;
	}

	@Override
	public void onTaskComplete(String result) {
		
		List<Genre> genres;
		try {
			genres = Utils.DeserializeGenreList(result);
			if (genres.size() > 0) {
			    ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, genres);
			    genresSpinner.setAdapter(spinnerArrayAdapter);				
				
				textView.setText("");
			}
			else
				textView.setText("No genres found");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long id) {
		selectedGenre = parent.getItemAtPosition(pos).toString();		
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {

	}
	
	public void addGenre(View view) {
		//todo: salvar na lista de generos do usuário
		textView.setText(selectedGenre);
	}
}
