package com.umc.moviearound.Activity;

import java.util.List;

import org.json.JSONException;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.umc.moviearound.AsyncTaskCompleteListener;
import com.umc.moviearound.GetTask;
import com.umc.moviearound.R;
import com.umc.moviearound.Utils;
import com.umc.moviearound.Model.Theater;

import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
	private ListView listViewTheaters;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setFastestInterval(1000);
        
        mLocationClient = new LocationClient(this, this, this);
        
        textViewMessage = (TextView) findViewById(R.id.textViewNoMovies);
        listViewTheaters = (ListView) findViewById(R.id.listViewTheaters);
        
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
        	//todo: pegar a lista de generos salvos e enviar como par�metro junto com a localiza��o
            new GetTask(this).execute("theaters");
        }
        
    }
    
    @Override
	public void onTaskComplete(String result) {
    	List<Theater> theaters;
		try {
			theaters = Utils.DeserializeTheaterList(result);
			if (theaters.size() > 0) {
				ArrayAdapter listArrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, theaters);
				listViewTheaters.setAdapter(listArrayAdapter);				
			}
			else
				textViewMessage.setText(R.string.text_no_movies);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    
    private void startPeriodicUpdates() {

        mLocationClient.requestLocationUpdates(mLocationRequest, this);
        
    }
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
             showGenres();
             return true;
    	default:
            return super.onOptionsItemSelected(item);
    	}
    }
    
    public void showGenres() {
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
		 Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
		 
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
    	textLocation.setText("Voc� est� em: \nLatitude: " + String.valueOf(location.getLatitude() + ", longitude: " + location.getLongitude()));
		
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
}