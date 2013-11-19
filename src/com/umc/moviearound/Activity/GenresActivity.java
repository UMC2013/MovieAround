package com.umc.moviearound.Activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONException;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.umc.moviearound.AsyncTaskCompleteListener;
import com.umc.moviearound.GetTask;
import com.umc.moviearound.R;
import com.umc.moviearound.Utils;
import com.umc.moviearound.Model.Genre;

public class GenresActivity extends Activity implements 
	AsyncTaskCompleteListener<String>, 
	OnItemSelectedListener  {

	public static final String TAG = "MovieAround";
	public static final String SharedPref_GENRES = "genres";

	Set<String> loadedGenres;
	private TextView textShowSelectedGenres;
	private Spinner genresSpinner;
	private String selectedGenre;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_genres);
		
		textShowSelectedGenres = (TextView) findViewById(R.id.textView1);
		genresSpinner = (Spinner) findViewById(R.id.spinnerGenres);
		genresSpinner.setOnItemSelectedListener(this);

		loadedGenres = loadGenres();
		textShowSelectedGenres.setText(loadedGenres.toString());
		

		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			new GetTask(this).execute("genres");			
		} else {
			textShowSelectedGenres.setText("No network connection available.");
		}
	}
	
	protected void onStop() {
		super.onStop();

		// GRAVA QUANDO SAIR DA ACTIVITY
		commitGenres();

//		copiaArquivos();
	}
	
	@Override
	public void onTaskComplete(String result) {
		List<Genre> genres;
		try {
			genres = Utils.DeserializeGenreList(result);
			if (genres.size() > 0) {
			    ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, genres);
			    genresSpinner.setAdapter(spinnerArrayAdapter);				
				
				//textView.setText("");
			}
			else
				textShowSelectedGenres.setText("No genres found");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		selectedGenre = parent.getItemAtPosition(pos).toString();		
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {

	}
	
	
	
	
	
	
	
	public void addGenre(View view) {
		if (selectedGenre != null) {
			loadedGenres.add(selectedGenre);
			
			textShowSelectedGenres.setText(loadedGenres.toString());
			
			Log.i(TAG, "Adicionando ao dinamico o genero: " + selectedGenre);
			Log.i(TAG, "Generos selecionados: " + loadedGenres);
		} else {
			 Toast.makeText(this, "No genre selected.", Toast.LENGTH_LONG).show();
		}
	}
	
	public Set<String> loadGenres() {
		SharedPreferences prefs = getSharedPreferences("preferences", Context.MODE_PRIVATE);
		HashSet<String> generos = (HashSet<String>) prefs.getStringSet(SharedPref_GENRES, new HashSet<String>());
		
		Log.i(TAG, "Carregando generos salvos, " + generos.toString());
		return generos;
	}
	
	public void commitGenres() {
		SharedPreferences prefs = getSharedPreferences("preferences", Context.MODE_PRIVATE);
		Editor ed = prefs.edit();
		
//		limpaListaGeneros(null);
		ed.clear();
		ed.commit();
		
		ed.putStringSet(SharedPref_GENRES, loadedGenres);
		
		//Comita mudanças
		ed.commit();
		Log.i(TAG, "Salvando os generos." + loadedGenres.toString());
	}
	
	public void cleanGenres(View view) {
		loadedGenres = new HashSet<String>();
		Log.i("MovieAround", "Limpando generos dinamicos.");
		
		textShowSelectedGenres.setText("No genres selected.");
	}
	
	public void copiaArquivos() {
		try {
			String source = getFilesDir().getParent() + "/shared_prefs/preferences.xml";
	        File s = new File(source);
	        File destination = new File("/storage/extSdCard/" + "preferences.xml");
			
			if(!destination.exists())
				destination.createNewFile();
			
		    InputStream in = new FileInputStream(source);
		    OutputStream out = new FileOutputStream(destination);
	
		    // Transfer bytes from in to out
		    byte[] buf = new byte[1024];
		    int len;
		    while ((len = in.read(buf)) > 0) {
		        out.write(buf, 0, len);
		    }
		    in.close();
		    out.close();
		    
		    Log.i(TAG, ">>> COPIANDO <<<");
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
