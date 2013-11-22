/**
 * DEVELOPED BY: 
 * 		IGOR KLAFKE
 * 		PAULO GRABIN
 * 		RONALD FLORES
 * 
 * UBIQUITOUS AND MOBILE COMPUTING - 2013/2
 * CRISTIANO ANDRE DA COSTA
 * UNISINOS
 */

package com.umc.moviearound.Activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.json.JSONException;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.umc.moviearound.AsyncTaskCompleteListener;
import com.umc.moviearound.GetTask;
import com.umc.moviearound.MoviesAdapter;
import com.umc.moviearound.R;
import com.umc.moviearound.Utils;
import com.umc.moviearound.Model.Movie;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends FragmentActivity implements
	AsyncTaskCompleteListener<String>, 
	LocationListener,
	GooglePlayServicesClient.ConnectionCallbacks,
	GooglePlayServicesClient.OnConnectionFailedListener {
	
	// Global constants
    /*
     * Define a request code to send to Google Play services
     * This code is returned in Activity.onActivityResult
     */
	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	
	private LocationRequest mLocationRequest;
	private LocationClient mLocationClient;
	
	private TextView textViewMessage;
	private ListView listViewMovies;
	public static final String SharedPref_GENRES = "genres";
	
	MoviesAdapter moviesAdapter;
	List<Movie> movies;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        if (loadGenre() == null) {
        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
        	builder.setMessage("We've noticed that you haven't selected your favorite genres yet. \n\nPlease, click on the button below to select some.")
        		   .setTitle("Welcome!")
        		   .setCancelable(false)
        		   .setPositiveButton("Select", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						showGenresActivity();					
					}
				});
        	
        	AlertDialog dialog = builder.create();
        	dialog.show();
        }
        
        
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setFastestInterval(1000);
        
        mLocationClient = new LocationClient(this, this, this);
        
        textViewMessage = (TextView) findViewById(R.id.textView2);
        listViewMovies = (ListView) findViewById(R.id.listViewMovies);
        
        movies = new ArrayList<Movie>();
        moviesAdapter = new MoviesAdapter(this, R.layout.list_item, movies);
        
        listViewMovies.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent;
        		Bundle bundle = new Bundle();
        		intent = new Intent(view.getContext(), MovieDetailsActivity.class);
        		
        		bundle.putInt("id", view.getId());
        		intent.putExtras(bundle);
        		startActivityForResult(intent, 0);
			}
		
		});
        
    }
    
    //This method is executed after the request do the api is completed
    @Override
	public void onTaskComplete(String result) {
		try {
			List<Movie> moviesList;
			movies.clear();
			moviesList = Utils.DeserializeMovieList(result);
			if (moviesList.size() > 0) {
				//ArrayAdapter listArrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, movies);
				//listViewMovies.setAdapter(listArrayAdapter);
				for(Movie m : moviesList) {
					movies.add(m);
				}
				listViewMovies.setAdapter(moviesAdapter);
				
				moviesAdapter.notifyDataSetChanged();
				textViewMessage.setText(R.string.text_movies_found);
			}
			else
				textViewMessage.setText(R.string.text_no_movies);
			
//			stopPeriodicUpdates();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    
    //start periodic updates to get the current location
    private void startPeriodicUpdates() {

        mLocationClient.requestLocationUpdates(mLocationRequest, this);
        
    }
    
    //stop periodic updates to get the current location
    private void stopPeriodicUpdates() {
        mLocationClient.removeLocationUpdates(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Connect the client.
        mLocationClient.connect();
    }
    
    @Override
    public void onResume() {
        super.onResume();
    }
    
    @Override
    protected void onStop() {
    	// If the client is connected
        if (mLocationClient.isConnected()) {
            stopPeriodicUpdates();
        }
        
        // Disconnecting the client invalidates it.
        mLocationClient.disconnect();
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
    	MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    	 case R.id.addGenre:
    		 showGenresActivity();
             return true;
    	default:
            return super.onOptionsItemSelected(item);
    	}
    }
    
    public void showGenresActivity() {
    	Intent intent = new Intent(this, GenresActivity.class);
    	startActivity(intent);
    }
    
    // Define a DialogFragment that displays the error dialog
    public static class ErrorDialogFragment extends DialogFragment {
        // Global field to contain the error dialog
        private Dialog mDialog;
        // Default constructor. Sets the dialog field to null
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }
        // Set the dialog to display
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }
        // Return a Dialog to the DialogFragment.
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }
    
    /*
     * Handle results returned to the FragmentActivity
     * by Google Play services
     */
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        // Decide what to do based on the original request code
        switch (requestCode) {
            case CONNECTION_FAILURE_RESOLUTION_REQUEST :
            /*
             * If the result code is Activity.RESULT_OK, try
             * to connect again
             */
                switch (resultCode) {
                    case Activity.RESULT_OK :
                    /*
                     * Try the request again
                     */

                    break;
                }
        }
     }
    
    private boolean servicesConnected() {
        // Check that Google Play services is available
        int resultCode =
                GooglePlayServicesUtil.
                        isGooglePlayServicesAvailable(this);
        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d("Location Updates",
                    "Google Play services is available.");
            // Continue
            return true;
        // Google Play services was not available for some reason
        } else {
        	Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this, 0);

            // If Google Play services can provide an error dialog
            if (dialog != null) {
                // Create a new DialogFragment for the error dialog
                ErrorDialogFragment errorFragment =
                        new ErrorDialogFragment();
                // Set the dialog in the DialogFragment
                errorFragment.setDialog(dialog);
                errorFragment.show(getSupportFragmentManager(), "MovieAround");
            }
            return false;
        }
    }


	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		
		  if (connectionResult.hasResolution()) {
	            try {
	                // Start an Activity that tries to resolve the error
	                connectionResult.startResolutionForResult(
	                        this,
	                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
	                /*
	                 * Thrown if Google Play services canceled the original
	                 * PendingIntent
	                 */
	            } catch (IntentSender.SendIntentException e) {
	                // Log the error
	                e.printStackTrace();
	            }
	        } else {
	            /*
	             * If no resolution is available, display a dialog to the
	             * user with the error.
	             */
	            //showErrorDialog(connectionResult.getErrorCode());
	        }
	}


	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub
		 Toast.makeText(this, "Searching for your location, please wait...", Toast.LENGTH_SHORT).show();
		 
		 startPeriodicUpdates();
	}


	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		 Toast.makeText(this, "Disconnected. Please re-connect.",
	                Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onLocationChanged(Location location) {
		TextView textLocation = (TextView) findViewById(R.id.textViewLocation);
		
    	Geocoder geoCoder = new Geocoder(this, Locale.getDefault());
    	try {
    	    List<Address> address = geoCoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
    	    String cityName = address.get(0).getLocality().toString();
    	    
			textLocation.setText("You are at " + cityName + ",\n" + "near " + String.valueOf(location.getLatitude() + ", " + location.getLongitude() + "."));
			
			
			 ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		        if (networkInfo != null && networkInfo.isConnected()) {
		        	//call the api with the location and user's genres
		        	String genres = loadGenre().toString().replace("[", "").replace("]", "").replace(" ", "");
		        	String url = String.format("movies?latitude=%s&longitude=%s&genres=%s", location.getLatitude(), location.getLongitude(), genres);
		            new GetTask(this).execute(url);
		        }
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getLocation(View v) {

        // If Google Play Services is available
        if (servicesConnected()) {
        	
            // Get the current location
            Location currentLocation = mLocationClient.getLastLocation();

            // Display the current location in the UI
            TextView textLocation = (TextView)findViewById(R.id.textViewLocation);
        	textLocation.setText(String.valueOf(currentLocation.getLatitude()));
            //mLatLng.setText(LocationUtils.getLatLng(this, currentLocation));
        }
    }
	
	public Set<String> loadGenre() {
		SharedPreferences prefs = getSharedPreferences("preferences",
				Context.MODE_PRIVATE);
		Set<String> generos = prefs.getStringSet(SharedPref_GENRES, null);

		if (generos != null) {

			if (generos.size() > 0) {
				Log.i(GenresActivity.TAG, "Tela inicial - Retornando " + generos.size());
				return generos;
			} else {
				Log.i(GenresActivity.TAG, "Tela inicial - Retornando 0");
				return null;
			}
		} else {
			Log.i(GenresActivity.TAG, "Tela inicial - Retornando null");
			return null;
		}
	}
	
}