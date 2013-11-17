package com.umc.moviearound;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.umc.moviearound.Model.Movie;
import com.umc.moviearound.Model.Theater;

public class MoviesAdapter extends ArrayAdapter<Movie> {

	int resource;
	String response;
	Context context;
	
	public MoviesAdapter(Context context, int resource, List<Movie> items) {
		super(context, resource, items);
		this.resource = resource;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout movieView;
		
		Movie m = getItem(position);
		
		if (convertView==null) {
			movieView = new LinearLayout(getContext());
			String inflater = Context.LAYOUT_INFLATER_SERVICE;
			LayoutInflater vi;
			vi = (LayoutInflater)getContext().getSystemService(inflater);
			vi.inflate(resource, movieView, true);
		}
		else {
			movieView = (LinearLayout) convertView;
		}
		movieView.setId(m.getId());
		
		TextView movieName = (TextView)movieView.findViewById(R.id.txtTitleBig);
		TextView movieGenre = (TextView)movieView.findViewById(R.id.txtLine1Big);
		TextView theatersNames = (TextView)movieView.findViewById(R.id.txtLine2Big);
		
		movieName.setText(m.getTitle());
		movieGenre.setText(m.getGenre());
		
		String theaters = "";
		for (Theater theater : m.getTheaters()) {
			theaters += theater.getName() + ";";
		}
		
		theatersNames.setText(theaters);
		
		
		return movieView;
	}
}
