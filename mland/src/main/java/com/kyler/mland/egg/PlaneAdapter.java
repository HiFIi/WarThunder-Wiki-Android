package com.kyler.mland.egg;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.textview.MaterialTextView;

public class PlaneAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {
    Context context;
    String[] countryList;
    int[] flags;
    LayoutInflater inflter;

    public PlaneAdapter(Context applicationContext, String countryList, int flags) {
        this.countryList = new String[]{countryList};
        this.flags = new int[]{flags};
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return countryList.length;
    }


    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        // Get the data item for this position
        view = inflter.inflate(R.layout.layout_mock_plane_page, null);

        MaterialTextView country = (MaterialTextView) view.findViewById(R.id.planeName);
        ImageView icon = (ImageView) view.findViewById(R.id.planeListImages);
        country.setText(countryList[i]);
        icon.setImageResource(flags[i]);
        return view;
    }

    public void addAll(String usaPlanesListText) {
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(view.getContext(), "position = ", Toast.LENGTH_SHORT).show();

        /** Or you can use For loop if you have long list of items. */
        for (int i = 0; i < getCount(); i++) {
            Toast.makeText(view.getContext(), "position = ", Toast.LENGTH_SHORT).show();
        }
    }
}