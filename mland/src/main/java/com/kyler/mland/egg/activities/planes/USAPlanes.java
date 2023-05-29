package com.kyler.mland.egg.activities.planes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.kyler.mland.egg.R;
import com.kyler.mland.egg.ui.MLandTextView;

/**
 * Created by kyler on 1/18/16.
 */
@SuppressWarnings("DefaultFileTemplate")
public class USAPlanes extends Fragment {
    private static final String wt = "https://wiki.warthunder.com/Category:USA_aircraft";
    public StringBuilder descriptionTextStringBuilder;
    public MLandTextView descriptionText;

    public USAPlanes() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.usa_planes, container, false);
    }

}