package com.kyler.mland.egg.activities.planes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.kyler.mland.egg.R;
import com.kyler.mland.egg.ui.MLandTextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * Created by kyler on 1/18/16.
 */
@SuppressWarnings("DefaultFileTemplate")
public class USAPlanes extends Fragment {
    private static final String wt = "https://wiki.warthunder.com/Category:USA_aircraft";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle
            savedInstanceState) {
        return inflater.inflate(R.layout.usa_planes, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        getUSAPlaneList();
    }

    private void getUSAPlaneList() {
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        final StringBuilder stringBuilder = new StringBuilder();
                        final StringBuilder descriptionTextStringBuilder =
                                new StringBuilder();
                        // MLandTextView descriptionText = (MLandTextView) getView().findViewById(R.id.USAPlanesText);

                        try {
                            Document doc = Jsoup.connect(wt).get();

                            //      -- Get USA Plane list alphabetically --
                            Elements divGetUSAPlanes = doc.select("div.mw-category-group");
                            String countryRankTextString = divGetUSAPlanes.text();
                            //     -- end Get USA Plane list alphabetically --

                            descriptionTextStringBuilder
                                    .append(divGetUSAPlanes)
                                    .append("\n")
                                    .append("\n")
                                    .append(countryRankTextString);

                        } catch (IOException e) {
                            stringBuilder
                                    .append("Error : ")
                                    .append(e.getMessage())
                                    .append(" ");
                        }
                        new Runnable() {
                            @Override
                            public void run() {
                               // descriptionText.setText(descriptionTextStringBuilder.toString());
                            }
                        };
                    }
                })
                .start();
    }
}