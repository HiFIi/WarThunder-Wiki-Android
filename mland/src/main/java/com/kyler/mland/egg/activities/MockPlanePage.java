package com.kyler.mland.egg.activities;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kyler.mland.egg.CustomAdapter;
import com.kyler.mland.egg.PlaneAdapter;
import com.kyler.mland.egg.R;
import com.kyler.mland.egg.activities.planes.Planes;
import com.kyler.mland.egg.ui.MLandTextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class MockPlanePage extends AppCompatActivity {
    private static final float PHOTO_ASPECT_RATIO = 1.7777777f;
    private static final float DRAWEE_PHOTO_ASPECT_RATIO = 1.33f;
    private static final String silverLionIcon = "https://wiki.warthunder.com/images/thumb/0/0f/Sl_icon.png/28px-Sl_icon.png";
    private static final String goldenEaglesIcon = "https://wiki.warthunder.com/images/thumb/7/74/Ge_icon.png/28px-Ge_icon.png";
    private static String usaPlanesListQuantity;
    private final String wt = "https://wiki.warthunder.com/Category:USA_aircraft";
    public ImageView imageView;
    public ArrayList<String> newsList = new ArrayList<>();
    public ArrayList<String> newsListMain = new ArrayList<>();
    public Elements news;
    int start = 0;
    private SimpleDraweeView dv;
    private String usaPlanesListText;
    private ListView lv;
    private CustomAdapter customAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_mock_plane_page);

        lv = (ListView) findViewById(R.id.news_list);
        customAdapter = new CustomAdapter(this, newsListMain);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.activity_listview_detail, R.id.headline, newsList);
        ArrayAdapter<String> adapterMain = new ArrayAdapter<String>(this, R.layout.activity_listview_detail, R.id.headline_main, newsListMain);

        getPlaneInfo();
    }

    public void getPlaneInfo() {
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        MLandTextView planeName = (MLandTextView) findViewById(R.id.textAlphabeticalList);
                        final StringBuilder planeNameStringBuilder = new StringBuilder();

                        try {
                            Document doc = Jsoup.connect(wt).get();

                            // *******************************************************************
                            // *             GETS A LIST OF THE PLANES ALPHABETICALLY,           *
                            // *                     REMOVES A, B, C, etc                        *
                            // *******************************************************************
                            //********************************************************************
                            // Get USA Plane list (alphabetically)
                            Elements usaPlanesList = (Elements) doc.select("div.mw-category li");

                            //********************************************************************
                            // Remove the letters "A" - "Z"
                            // usaPlanesList.select("h3").remove();
                            //********************************************************************
                            // Display the text
                            usaPlanesListText = usaPlanesList.text();

                            usaPlanesListQuantity = String.valueOf(usaPlanesList.stream().collect(Collectors.toList()));


                            for (Element e : usaPlanesList)
                                System.out.println(e.text());
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                        // *******************************************************************
                        // *                              DONE                               *
                        // *******************************************************************

                        planeNameStringBuilder.append(usaPlanesListText);
                        runOnUiThread(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        //     planeName.setText(planeNameStringBuilder.toString());
                                        //  loadIntoList();
                                        lv.setAdapter(customAdapter);

                                    }
                                });
                    }
                })
                .start();
    }

    private void loadIntoList() {
        ArrayList<Planes> arrayOfUsers = new ArrayList<Planes>();
// Create the adapter to convert the array to views
        PlaneAdapter planeAdapter = new PlaneAdapter(this, arrayOfUsers);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1);

// Attach the adapter to a ListView
        ListView listView = (ListView) findViewById(R.id.lvItems);
        listView.setAdapter(adapter);
        Planes newUser = new Planes(usaPlanesListText);
        adapter.add(newUser.toString());
// Or even append an entire new collection
// Fetching some data, data has now returned
// If data was JSON, convert to ArrayList of User objects.
        adapter.addAll(usaPlanesListText);
    }

    public int darkenColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.8f;
        return Color.HSVToColor(hsv);
        // Credits for this: https://github.com/Musenkishi/wally
    }

    public class NewThread extends AsyncTask<String, Void, String> {

        ProgressDialog dialog = new ProgressDialog(MockPlanePage.this);


        @Override
        protected void onPreExecute() {
            dialog.setMessage(getString(R.string.app_name));
            dialog.show();

        }

        @Override
        protected String doInBackground(String... arg) {

            Document doc;
            try {
                doc = Jsoup.connect("https://varzesh3.com").get();
                news = doc.select("li.text-type>a");
                newsList.clear();
                for (Element news : news) {
                    newsList.add(news.attr("href"));
                    newsListMain.add(news.text());

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            dialog.dismiss();

            lv.setAdapter(customAdapter);

        }

    }
}
