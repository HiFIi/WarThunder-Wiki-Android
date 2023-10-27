package com.kyler.mland.egg.activities;

import static com.kyler.mland.egg.activities.Home.setWindowFlag;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kyler.mland.egg.CustomAdapter;
import com.kyler.mland.egg.PlaneAdapter;
import com.kyler.mland.egg.R;
import com.kyler.mland.egg.SubjectData;
import com.kyler.mland.egg.ui.MLandTextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MockPlanePage extends AppCompatActivity {
    public static final String silverLionIcon = "https://wiki.warthunder.com/images/thumb/0/0f/Sl_icon.png/28px-Sl_icon.png";
    public static final String goldenEaglesIcon = "https://wiki.warthunder.com/images/thumb/7/74/Ge_icon.png/28px-Ge_icon.png";
    public static final String researchPointsIcon = "https://wiki.warthunder.com/images/thumb/4/4f/Specs-Card-Exp.png/14px-Specs-Card-Exp.png";
    private static final float PHOTO_ASPECT_RATIO = 1.7777777f;
    private static final float DRAWEE_PHOTO_ASPECT_RATIO = 1.33f;
    private static String usaPlanesListQuantity;
    private final String wt = "https://wiki.warthunder.com/Category:USA_aircraft";
    public ImageView imageView;
    public ArrayList<String> newsList = new ArrayList<>();
    public ArrayList<String> newsListMain = new ArrayList<>();
    public Elements news;

    List<String> list;

    int start = 0;
    String[] countryList = {"USA", "China", "australia", "Portugle", "Norway", "NewZealand"};
    int[] flags = {R.drawable.usa, R.drawable.china_flag, R.drawable.ussr_flag};
    private SimpleDraweeView dv;
    private String usaPlanesListText;
    private CustomAdapter customAdapter;

    private static String trim(String s, int width) {
        if (s.length() > width)
            return s.substring(0, width - 1) + ".";
        else
            return s;
    }

    private static void print(String msg, Object... args) {
        System.out.println(String.format(msg, args));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_mock_plane_page);

        Window window = getWindow();

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        super.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        setupTranslucentStatus();

        getPlaneInfo();
    }

    private void setupTranslucentStatus() {
        Window window = getWindow();

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        super.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    public void getPlaneInfo() {
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        MLandTextView planeName = (MLandTextView) findViewById(R.id.textAlphabeticalList);
                        final StringBuilder planeNameStringBuilder = new StringBuilder();

                        Document doc;
                        try {
                            doc = Jsoup.connect(wt).get();

                            // *******************************************************************
                            // *             GETS A LIST OF THE PLANES ALPHABETICALLY,           *
                            // *                     REMOVES A, B, C, etc                        *
                            // *******************************************************************
                            //********************************************************************
                            // Get USA Plane list (alphabetically)
                            Elements usaPlanesList = (Elements) doc.select("div.mw-category li");
                            list = new ArrayList<>();
                            String url = usaPlanesList.attr("href");
                            Elements links = doc.select("a[href]");
                            Elements media = doc.select("[src]");
                            Elements imports = doc.select("link[href]");
                            print("\nLinks: (%d)", links.size());
                            for (Element link : links) {
                                print(" * a: <%s>  (%s)", link.attr("abs:href"), trim(link.text(), 35));
                            }

                            Thread thread = new Thread() {
                                @Override
                                public void run() {
                                    // Call Looper.prepare() on the thread.
                                    Looper.prepare();
                                    // Call Toast.makeText() on the thread.
                                    Toast.makeText(getApplicationContext(), links.text().trim(), Toast.LENGTH_SHORT).show();
                                    // Call Looper.loop() to start the message loop.
                                    Looper.loop();
                                }
                            };

// Start the thread.
                            thread.start();


                            for (Element element : doc.select("div.mw-category li")) {
                                list.add(element.text());
                            }

                            //********************************************************************
                            // Remove the letters "A" - "Z"
                            // usaPlanesList.select("h3").remove();
                            //********************************************************************
                            // Display the text
                            usaPlanesListText = usaPlanesList.text().replace(",", "").replace("[", "");


                            for (Element e : usaPlanesList)
                                System.out.println("TEST 1\n\n" + Arrays.toString(list.toArray()));
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                        // *******************************************************************
                        // *                              DONE                               *
                        // *******************************************************************

                        Elements planeListPlane = (Elements) doc.select("div.tree-item-text");
                        Elements planeListImage = (Elements) doc.select("div.tree-item-img");


                        planeNameStringBuilder.append(usaPlanesListText);
                        runOnUiThread(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        ListView simpleListView = (ListView) findViewById(R.id.lvItems);

                                        String[] templateListArray = list.toArray(new String[0]);
                                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.plane_list, R.id.planeName,
                                                templateListArray);
                                        PlaneAdapter customAdapter = new PlaneAdapter(getApplicationContext(), usaPlanesListText, R.drawable.bubbles);
                                        customAdapter.addAll(usaPlanesListText);
                                        customAdapter.getCount();
                                        simpleListView.setAdapter(arrayAdapter);
                                        System.out.println("TEST 2\n\n" + simpleListView.getAdapter().getCount());
                                        simpleListView.setOnItemClickListener((parent, view, position, id) -> Toast.makeText(getApplicationContext(), templateListArray[position], Toast.LENGTH_SHORT).show());

                                    }
                                });
                    }
                })
                .start();
    }

    public int darkenColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.8f;
        return Color.HSVToColor(hsv);
        // Credits for this: https://github.com/Musenkishi/wally
    }

    private ArrayList<SubjectData> generateItemsList() {
        String[] itemNames = getResources().getStringArray(R.array.items_name);
        String[] itemDescriptions = getResources().getStringArray(R.array.item_description);

        ArrayList<SubjectData> list = new ArrayList<>();

        for (int i = 0; i < itemNames.length; i++) {
            list.add(new SubjectData(itemNames[i], "https://google.com", itemDescriptions[i]));
        }


        return list;
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
                doc = Jsoup.connect("https://wiki.warthunder.com/Category:USA_aircraft").get();
                news = doc.select("li.text-type>a");
                newsList.clear();
                for (Element news : news) {
                    newsList.add(news.attr("a[href]"));
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

        }

    }
}
