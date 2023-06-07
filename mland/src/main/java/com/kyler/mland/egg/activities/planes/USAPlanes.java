package com.kyler.mland.egg.activities.planes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.kyler.mland.egg.PlaneAdapter;
import com.kyler.mland.egg.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kyler on 1/18/16.
 */
@SuppressWarnings("DefaultFileTemplate")
public class USAPlanes extends Fragment {
    private static final String wt = "https://wiki.warthunder.com/Category:USA_aircraft";
    List<String> list;
    private String usaPlanesListText;

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
                        //  MLandTextView descriptionText = (MLandTextView) getView().findViewById(R.id.tteesstt);

                        try {
                            Document doc = Jsoup.connect(wt).get();

                            Elements usaPlanesList = (Elements) doc.select("div.mw-category li");
                            list = new ArrayList<>();

                            for (Element element : doc.select("div.mw-category li")) {
                                list.add(element.text());
                            }

                            //********************************************************************
                            // Remove the letters "A" - "Z"
                            // usaPlanesList.select("h3").remove();
                            //********************************************************************
                            // Display the text
                            usaPlanesListText = usaPlanesList.text().replace(",", "").replace("[", "");

                        } catch (IOException e) {
                            stringBuilder
                                    .append("Error : ")
                                    .append(e.getMessage())
                                    .append(" ");
                        }

                        getActivity().runOnUiThread(
                                () -> {
                                    ListView simpleListView = (ListView) getView().findViewById(R.id.lvItems2);

                                    String[] templateListArray = list.toArray(new String[0]);
                                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.plane_list, R.id.planeName,
                                            templateListArray);
                                    PlaneAdapter customAdapter = new PlaneAdapter(getActivity().getApplicationContext(), usaPlanesListText, R.drawable.bubbles);
                                    customAdapter.addAll(usaPlanesListText);
                                    customAdapter.getCount();
                                    simpleListView.setAdapter(arrayAdapter);
                                    System.out.println("TEST 2\n\n" + simpleListView.getAdapter().getCount());
                                    simpleListView.setOnItemClickListener((parent, view, position, id) -> Toast.makeText(getActivity().getApplicationContext(), templateListArray[position], Toast.LENGTH_SHORT).show());

                                    Toast.makeText(getActivity().getApplicationContext(), "RAN", Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .start();
    }
}