package com.kyler.mland.egg.activities;

import static android.view.ViewTreeObserver.OnGlobalLayoutListener;

import static com.kyler.mland.egg.activities.PlanesTest.fiftyFiveHundred;
import static com.kyler.mland.egg.activities.PlanesTest.four;
import static com.kyler.mland.egg.activities.PlanesTest.oneF;
import static com.kyler.mland.egg.activities.PlanesTest.onePointOhFive;
import static com.kyler.mland.egg.activities.PlanesTest.two;
import static com.kyler.mland.egg.activities.PlanesTest.zero;

import android.animation.ValueAnimator;
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
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.DraweeView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.core.ImageTranscoderType;
import com.facebook.imagepipeline.core.MemoryChunkType;
import com.google.android.material.tabs.TabLayout;
import com.google.samples.apps.iosched.ui.widget.ObservableScrollView;
import com.kyler.mland.egg.MLandBase;
import com.kyler.mland.egg.R;
import com.kyler.mland.egg.activities.planes.USAPlanes;
import com.kyler.mland.egg.ui.MLandTextView;
import com.kyler.mland.egg.utils.UIUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Objects;

/**
 * Created by kyler on 10/26/15.
 */
@SuppressWarnings("DefaultFileTemplate")
public class Home extends MLandBase implements ObservableScrollView.Callbacks {
    protected static final int NAVDRAWER_ITEM_HOME = 0;
    private static final float PHOTO_ASPECT_RATIO = 1.7777777f;
    private static final float DRAWEE_PHOTO_ASPECT_RATIO = 1.33f;

    private static Bitmap sIcon = null;
    private static Context cc;
    private static int planeTabsHeight;
    private static int toolBarHeight;
    private static int toolBarPlusPlaneTabsHeight;
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
    public SimpleDraweeView draweeView;
    private int mPhotoHeightPixels;
    private View mScrollViewChild;
    private int mHeaderHeightPixels;
    private View mDetailsContainer;
    private ObservableScrollView mScrollView;
    private View mPhotoViewContainer;
    private boolean mHasPhoto;
    private float mMaxHeaderElevation;
    private View mHeaderBox;
    //  private static String draweeUrlString;
    private final OnGlobalLayoutListener mGlobalLayoutListener =
            new OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    recomputePhotoAndScrollingMetrics();
                }
            };
    private Handler mHandler;
    private TabLayout planeTabs;
    private Handler mHandlerr;
    private Handler mHandlerTwo;
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

        AsyncTaskRunner runner = new AsyncTaskRunner();
        // String sleepTime = "20000";
        runner.execute("10");
        Log.d("tag", "GANESH LOGO: AsyncTaskRunner ... ");

        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;

        Home.cc = getApplication().getApplicationContext();

        Objects.requireNonNull(getSupportActionBar()).setTitle(null);

        runGitScript();
        Resources resources = this.getResources();
        String label = resources.getString(this.getApplicationInfo().labelRes);
        final int aboutPrimaryDark = resources.getColor(R.color.colorPrimaryDark);

        if (sIcon == null) {
            // Cache to avoid decoding the same bitmap on every Activity change
            sIcon = BitmapFactory.decodeResource(resources, R.drawable.icon_mland_home);
        }

        this.setTaskDescription(new ActivityManager.TaskDescription(label, sIcon, aboutPrimaryDark));

        mHasPhoto = true;

        mHandler = new Handler();
        mHandlerr = new Handler();

        initViews();
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mScrollView == null) {
            return;
        }
        ViewTreeObserver vto = mScrollView.getViewTreeObserver();
        if (vto.isAlive()) {
            vto.removeGlobalOnLayoutListener(mGlobalLayoutListener);
        }
    }

    private void initViews() {
        mMaxHeaderElevation =
                getResources().getDimensionPixelSize(R.dimen.session_detail_max_header_elevation);
        mScrollView = (ObservableScrollView) findViewById(R.id.scroll_viewTwo);
        mScrollView.addCallbacks(this);
        ViewTreeObserver vto = mScrollView.getViewTreeObserver();
        if (vto.isAlive()) {
            vto.addOnGlobalLayoutListener(mGlobalLayoutListener);
        }
        mScrollViewChild = findViewById(R.id.scroll_view_childTwo);
        mScrollViewChild.setVisibility(View.VISIBLE);
        mDetailsContainer = findViewById(R.id.details_containerTwo);
        mActionBarToolbar.setVisibility(View.VISIBLE);
        mHeaderBox = findViewById(R.id.header_sessionTwo);
        mActionBarToolbar.setVisibility(View.VISIBLE);
        mPhotoViewContainer = findViewById(R.id.session_photo_containerTwo);
        TabLayout planeTabs = (TabLayout) findViewById(R.id.plane_tabs);
        planeTabs = findViewById(R.id.plane_tabs);

        Uri mDraweeUri =
                Uri.parse(
                        "https://static.warthunder.com/upload/image//wallpapers/3840x2160_logo_drone_age_battlecruiser_alaska_eng_f08dcc6737f71a993755c2946b529f9c.jpg");
        draweeView = (SimpleDraweeView) findViewById(R.id.session_photoTwo);
        planeTabs.measure(zero, zero);
        mActionBarToolbar.measure(zero, zero);
        draweeView.setImageURI(mDraweeUri);
        planeTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            Intent intent;

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.add(R.id.your_placeholder, new USAPlanes());
                        ft.commit();
                        break;
                    case 1:
                        intent = new Intent(getApplicationContext(), MockPlanePage.class);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                        break;
                    case 2:
//                        codes related to the third tab
                        break;
                    case 3:
//                        codes related to the fourth tab
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        // get the height of the planetabs and toolbar, divide both by 2
        // this is done to anchor it to the tabs layout on parallax scrolling
        planeTabsHeight = planeTabs.getMeasuredHeight() / two;
        toolBarHeight = mActionBarToolbar.getMeasuredHeight() / two;

        // get both above values and then quarter it
        // this is done to put the drawer icon in the correct place
        toolBarPlusPlaneTabsHeight = planeTabsHeight + toolBarHeight / four;
        mActionBarToolbar.setPadding(zero, toolBarPlusPlaneTabsHeight, zero, zero);

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(oneF, onePointOhFive);
        valueAnimator.setDuration(fiftyFiveHundred);
        valueAnimator.addUpdateListener(
                new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(@NonNull ValueAnimator animation) {
                        draweeView.setScaleX((Float) animation.getAnimatedValue());
                        draweeView.setScaleY((Float) animation.getAnimatedValue());
                    }
                });
        valueAnimator.setRepeatCount(Animation.INFINITE);
        valueAnimator.setRepeatMode(ValueAnimator.REVERSE);
        valueAnimator.start();
        valueAnimator.start();

        // display the drawee image, etc
        displayData();
    }

    private void displayData() {
        mHandler.post(
                new Runnable() {
                    @Override
                    public void run() {
                        Uri mDraweeUri =
                                Uri.parse(
                                        "https://static.warthunder.com/upload/image//wallpapers/3840x2160_logo_drone_age_battlecruiser_alaska_eng_f08dcc6737f71a993755c2946b529f9c.jpg");

                        draweeView = (SimpleDraweeView) findViewById(R.id.session_photoTwo);
                        //     draweeView.setAspectRatio(DRAWEE_PHOTO_ASPECT_RATIO);
                        draweeView.setImageURI(mDraweeUri);
                        mScrollViewChild.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void recomputePhotoAndScrollingMetrics() {
        mHeaderHeightPixels = mHeaderBox.getHeight();

        mPhotoHeightPixels = 0;
        if (mHasPhoto) {
            mPhotoHeightPixels = (int) (draweeView.getWidth() / PHOTO_ASPECT_RATIO);
            mPhotoHeightPixels = Math.min(mPhotoHeightPixels, mScrollView.getHeight());
        }

        ViewGroup.LayoutParams lp;
        lp = mPhotoViewContainer.getLayoutParams();
        if (lp.height != mPhotoHeightPixels) {
            lp.height = mPhotoHeightPixels;
            mPhotoViewContainer.setLayoutParams(lp);
        }

        ViewGroup.MarginLayoutParams mlp =
                (ViewGroup.MarginLayoutParams) mDetailsContainer.getLayoutParams();
        if (mlp.topMargin != mHeaderHeightPixels + mPhotoHeightPixels) {
            // mlp.topMargin = mHeaderHeightPixels + mPhotoHeightPixels;
            mDetailsContainer.setLayoutParams(mlp);
            // mDetailsContainer.setPadding(16, 150, 16, 150);
        }

        onScrollChanged(0, 0); // trigger scroll handling
    }

    @Override
    public void onScrollChanged(int deltaX, int deltaY) {
        // Reposition the header bar -- it's normally anchored to the top of the content,
        // but locks to the top of the screen on scroll
        int scrollY = mScrollView.getScrollY();

        float newTop = Math.max(mPhotoHeightPixels, scrollY);

        mHeaderBox.setTranslationY(newTop);

        float gapFillProgress = 1;
        if (mPhotoHeightPixels != 0) {
            gapFillProgress =
                    Math.min(Math.max(UIUtils.getProgress(scrollY, 0, mPhotoHeightPixels), 0), 1);

            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            if (gapFillProgress == 1) {

            } else {
                getWindow()
                        .setFlags(
                                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                                WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }

            // Move background photo (parallax effect)
            mPhotoViewContainer.setTranslationY(scrollY * 0.5f);
        }
    }

    private void getBodyText() {

        final StringBuilder builder = new StringBuilder();

        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        MLandTextView result = findViewById(R.id.result);

                        try {
                            Document doc = Jsoup.connect(wt).get();

                            String title = doc.title();
                            String text = doc.body().text(); // "An example link"
                            Element content = doc.getElementById("content");
                            Elements table = doc.select("table");
                            Elements myin = doc.getElementsByClass("wt-class-table");
                            Elements links = content.getElementsByTag("a");
                            Elements masthead = doc.select("p:contains(Aviation ):");

                            //      -- Get Country rank --
                            Elements planeList = doc.select("div.mw-category-generated");
                            //  String countryRankTextString = divCountryRank.text();
                            //     -- end get Country rank --

                            Elements test = doc.select("img[src$=.png]");
                            for (Element link : links) {
                                String linkHref = link.attr("href");
                                String linkText = link.text();
                            }
                            // Elements links = doc.select("h3.r > a");
                            for (Element link : links) {
                                builder.append(title).append(myin).append(table).append("").append(link.text());
                            }

                        } catch (Exception e) {

                        }
                        mHandlerr.post(
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
        newSystemUiFlags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        getWindow().setStatusBarColor(ContextCompat.getColor(Home.this, R.color.black__10_percent));
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
        //      overridePendingTransition(0, 0);
    }

    // Remove inter-activity transition to avoid screen tossing on tapping bottom navigation items

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    /**
     * @noinspection deprecation
     */
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

        private void activateLightStatusBar() {
            int oldSystemUiFlags = getWindow().getDecorView().getSystemUiVisibility();
            int newSystemUiFlags = oldSystemUiFlags;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                newSystemUiFlags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                getWindow().setStatusBarColor(ContextCompat.getColor(Home.this, R.color.black__10_percent));
            }
            if (newSystemUiFlags != oldSystemUiFlags) {
                getWindow().getDecorView().setSystemUiVisibility(newSystemUiFlags);
            }
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
