package com.kyler.mland.egg.activities;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.DraweeView;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.core.ImageTranscoderType;
import com.facebook.imagepipeline.core.MemoryChunkType;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.kyler.mland.egg.MLandBase;
import com.kyler.mland.egg.R;
import com.kyler.mland.egg.ui.MLandTextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by kyler on 10/26/15.
 */
@SuppressWarnings("DefaultFileTemplate")
public class Home extends MLandBase {
    protected static final int NAVDRAWER_ITEM_USA = 0;
    private static final String mUSAFlagLink =
            "https://wiki.warthunder.com/images/9/9f/USA_flag.png";
    private static final String mGermanyFlagLink =
            "https://wiki.warthunder.com/images/4/49/Germany_flag.png";
    private static final String mUSSRFlagLink =
            "https://wiki.warthunder.com/images/f/f9/USSR_flag.png";
    private static final String mBritainFlagLink =
            "https://wiki.warthunder.com/images/d/d0/Britain_flag.png";
    private static final String mJapanFlagLink =
            "https://wiki.warthunder.com/images/2/2e/Japan_flag.png";
    private static final String mChinaFlagLink =
            "https://wiki.warthunder.com/images/a/ac/China_flag.png";
    private static final String mItalyFlagLink =
            "https://wiki.warthunder.com/images/e/e9/Italy_flag.png";
    private static final String mFranceFlagLink =
            "https://wiki.warthunder.com/images/7/73/France_flag.png";
    private static final String mSwedenFlagLink =
            "https://wiki.warthunder.com/images/c/ca/Sweden_flag.png";
    private static final String mIsraelFlagLink =
            "https://wiki.warthunder.com/images/f/f9/Israel_flag.png";
    public static Uri uri =
            Uri.parse("https://wiki.warthunder.com/images/4/4c/GarageImage_P-36G.jpg");
    private static Bitmap sIcon = null;
    private static Window window;
    private static Context cc;
    private final String mPlaneCategories = "https://wiki.warthunder.com/Category:";
    private final String[] title = {
            "USA_aircraft",
            "Germany",
            "USSR_aircraft",
            "Britain_aircraft",
            "Japan_aircraft",
            "Britain_aircraft",
            "Italy_aircraft",
            "France_aircraft",
            "Sweden_aircraft",
            "Israel_aircraft"
    };
    private final String wt = "https://wiki.warthunder.com/Aviation";
    private final ArrayList<Integer> mNavDrawerItems = new ArrayList<>();
    private Handler mHandler;
    private DraweeView dv;
    private WebView webView;
    private Button submit;

    public static Context getAppContext() {
        return Home.cc;
    }

    @Override
    protected int getSelfNavDrawerItem() {
        return NAVDRAWER_ITEM_HOME;
    }

    @Override
    protected Context getContext() {
        return Home.this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_MLand_Home);
        super.onCreate(savedInstanceState);
        Fresco.initialize(
                getApplicationContext(),
                ImagePipelineConfig.newBuilder(getApplicationContext())
                        .setMemoryChunkType(MemoryChunkType.BUFFER_MEMORY)
                        .setImageTranscoderType(ImageTranscoderType.JAVA_TRANSCODER)
                        .experiment()
                        .setNativeCodeDisabled(true)
                        .build());

        setContentView(R.layout.home);
        super.getWindow()
                .getDecorView()
                .setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        // activateLightNavigationBar();
        // activateLightStatusBar();

        /** final ListView list = findViewById(R.id.list);
         ArrayList<SubjectData> arrayList = new ArrayList<SubjectData>();
         arrayList.add(new SubjectData("USA", "https://www.tutorialspoint.com/java/", mUSAFlagLink));
         arrayList.add(
         new SubjectData(
         "Germany", "https://www.tutorialspoint.com/python/", mGermanyFlagLink));
         arrayList.add(
         new SubjectData(
         "USSR", "https://www.tutorialspoint.com/javascript/", mUSSRFlagLink));
         arrayList.add(
         new SubjectData(
         "Britain",
         "https://www.tutorialspoint.com/cprogramming/",
         mBritainFlagLink));
         arrayList.add(
         new SubjectData("Japan", "https://www.tutorialspoint.com/java/", mJapanFlagLink));
         arrayList.add(
         new SubjectData("China", "https://www.tutorialspoint.com/python/", mChinaFlagLink));
         arrayList.add(
         new SubjectData(
         "Italy", "https://www.tutorialspoint.com/javascript/", mItalyFlagLink));
         arrayList.add(
         new SubjectData(
         "France", "https://www.tutorialspoint.com/cprogramming/", mFranceFlagLink));
         arrayList.add(
         new SubjectData(
         "Sweden", "https://www.tutorialspoint.com/javascript/", mSwedenFlagLink));
         arrayList.add(
         new SubjectData(
         "Israel", "https://www.tutorialspoint.com/cprogramming/", mIsraelFlagLink));

         CustomAdapter customAdapter = new CustomAdapter(this, arrayList);
         list.setAdapter(customAdapter); **/

        AsyncTaskRunner runner = new AsyncTaskRunner();
        // String sleepTime = "20000";
        runner.execute("10");
        Log.d("tag", "GANESH LOGO: AsyncTaskRunner ... ");


        mHandler = new Handler();

        mActionBarToolbar.setNavigationIcon(R.drawable.ic_drawer);

        Home.cc = getApplication().getApplicationContext();

        Objects.requireNonNull(getSupportActionBar()).setTitle(null);

        runGitScript();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set Home selected
        bottomNavigationView.setSelectedItemId(R.id.bb_usa);

        // Perform item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                Intent intent;
                int itemId = item.getItemId();
                if (itemId == R.id.bb_usa) {
                    intent = new Intent(getApplicationContext(), PlanesTest.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                }
                return true;
            }
        });


        Resources resources = this.getResources();
        String label = resources.getString(this.getApplicationInfo().labelRes);
        final int aboutPrimaryDark = resources.getColor(R.color.colorPrimaryDark);

        if (sIcon == null) {
            // Cache to avoid decoding the same bitmap on every Activity change
            sIcon = BitmapFactory.decodeResource(resources, R.drawable.icon_mland_home);
        }

        this.setTaskDescription(
                new ActivityManager.TaskDescription(label, sIcon, aboutPrimaryDark));
    }

    private void runGitScript() {
        Process p = null;
        try {
            p =
                    new ProcessBuilder()
                            .command("/storage/emulated/0/Iconify/generateTagGroupedGitlog.sh")
                            .start();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (p != null) p.destroy();
        }
    }


    /**
     * Sets the status bar to be light or not. Light status bar means dark icons.
     */
    @TargetApi(Build.VERSION_CODES.O)
    private void activateLightNavigationBar() {
        int oldSystemUiFlags = getWindow().getDecorView().getSystemUiVisibility();
        int newSystemUiFlags = oldSystemUiFlags;
        newSystemUiFlags |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
        getWindow().setNavigationBarColor(getResources().getColor(android.R.color.white));
        if (newSystemUiFlags != oldSystemUiFlags) {
            final int systemUiFlags = newSystemUiFlags;
            runOnUiThread(() -> getWindow().getDecorView().setSystemUiVisibility(systemUiFlags));
        }
    }

    private void getBodyText() {

        final StringBuilder builder = new StringBuilder();

        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        MLandTextView result = findViewById(R.id.result);
                        // binding.getRoot().findViewById(R.id.iv);
                        //SimpleDraweeView draweeView =
                        //       (SimpleDraweeView) findViewById(R.id.home_pic);

                        try {
                            Document doc = Jsoup.connect(wt).get();

                            //draweeView.setImageURI(uri);
                            //draweeView.setVisibility(View.VISIBLE);

                            String title = doc.title();
                            String text = doc.body().text(); // "An example link"
                            Element content = doc.getElementById("content");
                            Elements table = doc.select("table");
                            Elements myin = doc.getElementsByClass("wt-class-table");
                            Elements links = content.getElementsByTag("a");
                            Elements masthead = doc.select("p:contains(Aviation ):");

                            Elements test = doc.select("img[src$=.png]");
                            for (Element link : links) {
                                String linkHref = link.attr("href");
                                String linkText = link.text();
                            }
                            // Elements links = doc.select("h3.r > a");
                            for (Element link : links) {
                                builder.append(title)
                                        .append(myin)
                                        .append(table)
                                        .append("")
                                        .append(link.text());
                            }

                        } catch (Exception e) {

                        }
                        mHandler.post(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        // result.setText(builder.toString());
                                    }
                                });
                    }
                })
                .start();
    }

    private void activateLightStatusBar() {
        int oldSystemUiFlags = getWindow().getDecorView().getSystemUiVisibility();
        int newSystemUiFlags = oldSystemUiFlags;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            newSystemUiFlags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            getWindow()
                    .setStatusBarColor(
                            ContextCompat.getColor(Home.this, R.color.black__10_percent));
        }
        if (newSystemUiFlags != oldSystemUiFlags) {
            final int systemUiFlags = newSystemUiFlags;
            runOnUiThread(
                    () -> {
                        getWindow().getDecorView().setSystemUiVisibility(systemUiFlags);
                        activateLightStatusBar();
                    });
        }
    }

    public void onStart() {
        super.onStart();
//        updateNavigationBarState();
    }

    public void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    // Remove inter-activity transition to avoid screen tossing on tapping bottom navigation items

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    private class AsyncTaskRunner extends AsyncTask<String, String, String> {

        private String resp;

        @Override
        protected String doInBackground(String... params) {
            publishProgress("Sleeping..."); // Calls onProgressUpdate()
            try {
                // Do your long operations here and return the result
                int time = Integer.parseInt(params[0]);
                // Sleeping for given time period
                Thread.sleep(time);
                resp = "Slept for " + time + " milliseconds";
            } catch (InterruptedException e) {
                e.printStackTrace();
                resp = e.getMessage();
            } catch (Exception e) {
                e.printStackTrace();
                resp = e.getMessage();
            }
            return resp;
        }

        @Override
        protected void onPostExecute(String result) {
            // execution of result of Long time consuming operation
            //        finalResult.setText(result);
            Log.d("tag", "GANESH LOGO: sleeping FINISHED thread");

            CharSequence text = "done";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(cc.getApplicationContext(), text, duration);
            toast.show();
            getBodyText();
        }

        @Override
        protected void onPreExecute() {
            // Things to be done before execution of long running operation. For
            // example showing ProgessDialog
            Log.d("tag", "GANESH LOGO: onPreExecute... ");
        }

        @Override
        protected void onProgressUpdate(String... text) {
            Log.d("tag", "GANESH LOGO: thread" + text[0]);
            // finalResult.setText(text[0]);
            // Things to be done while execution of long running operation is in
            // progress. For example updating ProgessDialog
        }
    } // class
}
