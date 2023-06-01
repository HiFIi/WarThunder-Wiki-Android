package com.kyler.mland.egg;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.kyler.mland.egg.activities.planes.Planes;

import java.util.ArrayList;

public class PlaneAdapter extends ArrayAdapter<Planes> {
    public PlaneAdapter(Context context, ArrayList<Planes> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Planes planes = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.plane_list, parent, false);
        }
        // Lookup view for data population
        TextView tvName = (TextView) convertView.findViewById(R.id.planeName);
        // Populate the data into the template view using the data object
        tvName.setText(planes.planeName);
        // Return the completed view to render on screen
        return convertView;
    }

    public void addAll(String usaPlanesListText) {
    }
}