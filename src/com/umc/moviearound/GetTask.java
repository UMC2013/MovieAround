package com.umc.moviearound;

import java.io.IOException;

import android.os.AsyncTask;

public class GetTask extends AsyncTask<String, Void, String>{
	private AsyncTaskCompleteListener<String> callback;

    public GetTask(AsyncTaskCompleteListener<String> cb) {
        this.callback = cb;
    }
    
    @Override
    protected String doInBackground(String... url) {
        try {
        	MovieAroundApi api = new MovieAroundApi();
        	
            return api.getJson(url[0]);
        } catch (IOException e) {
            return "Unable to retrieve web page. URL may be invalid.";
        }
    }
    // onPostExecute displays the results of the AsyncTask.
    @Override
    protected void onPostExecute(String result) {
    	callback.onTaskComplete(result);
   }
}
