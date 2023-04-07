package com.kyler.mland.egg;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.kyler.mland.egg.activities.MockPlanePage;
import com.kyler.mland.egg.activities.PlanesTest;
import com.kyler.mland.egg.ui.MLandTextView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CustomAdapter implements ListAdapter {
    ArrayList<SubjectData> arrayList;
    Context context;

    public CustomAdapter(Context context, ArrayList<SubjectData> arrayList) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SubjectData subjectData = arrayList.get(position);
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.list_row, null);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (position == 0) {
                        Intent intent;
                        Toast.makeText(context.getApplicationContext(), "1", Toast.LENGTH_LONG).show();

                        intent = new Intent(context.getApplicationContext(), PlanesTest.class);
                        context.startActivity(intent);
                    }

                    if (position == 1) {
                        Intent intentt;
                        intentt = new Intent(context.getApplicationContext(), MockPlanePage.class);
                        context.startActivity(intentt);
                    }
                    if (position == 2) {
                    }
                    if (position == 3) {

                    }
                }
            });
            MLandTextView tittle = convertView.findViewById(R.id.title);
            ImageView imag = convertView.findViewById(R.id.icon);
            tittle.setText(subjectData.SubjectName);
            Picasso.get()
                    .load(subjectData.Image)
                    .into(imag);
        }
        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return arrayList.size();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}