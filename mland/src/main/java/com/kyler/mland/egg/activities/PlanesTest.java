package com.kyler.mland.egg.activities;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.core.ImageTranscoderType;
import com.facebook.imagepipeline.core.MemoryChunkType;
import com.kyler.mland.egg.MLandBase;
import com.kyler.mland.egg.R;
import com.kyler.mland.egg.ui.MLandTextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PlanesTest extends MLandBase {
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
    private static final float PHOTO_ASPECT_RATIO = 1.7777777f;
    private static final float DRAWEE_PHOTO_ASPECT_RATIO = 1.33f;
    public static Uri countryImageURL =
            Uri.parse(
                    "https://wiki.warthunder.com/images/thumb/f/f9/USSR_flag.png/45px-USSR_flag.png");
    private static Bitmap sIcon = null;
    private final int RESULT_LOAD_IMAGE = 00023;
    private final String wt = "https://wiki.warthunder.com/Yak-23";
    public String textSource;
    private Handler mHandler;


    @Override
    protected int getSelfNavDrawerItem() {
        return NAVDRAWER_ITEM_HOME;
    }

    @Override
    protected Context getContext() {
        return PlanesTest.this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(
                getApplicationContext(),
                ImagePipelineConfig.newBuilder(getApplicationContext())
                        .setMemoryChunkType(MemoryChunkType.BUFFER_MEMORY)
                        .setImageTranscoderType(ImageTranscoderType.JAVA_TRANSCODER)
                        .experiment()
                        .setNativeCodeDisabled(true)
                        .build());

        super.getWindow()
                .getDecorView()
                .setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        setContentView(R.layout.layout_planes_test);

        getHtmlFromWeb();

        setupDrawerForPlanes();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(
                () -> {
                    // Background work here
                    SimpleDraweeView countryIV = (SimpleDraweeView) findViewById(R.id.countryImage);
                    handler.post(
                            () -> {
                                // UI Thread work here
                                countryIV.setImageURI(countryImageURL);
                                countryIV.setVisibility(View.VISIBLE);
                            });
                });

        SimpleDraweeView draweeView = (SimpleDraweeView) findViewById(R.id.planeDrawee);

        draweeView
                .getHierarchy()
                .setPlaceholderImage(
                        getContext().getResources().getDrawable(R.drawable.loading_image_drawee));

        mHandler = new Handler();

        Resources resources = this.getResources();

        Objects.requireNonNull(getSupportActionBar()).setTitle(null);
        mActionBarToolbar.setNavigationIcon(R.drawable.ic_drawer_white);

        if (sIcon == null) {
            // Cache to avoid decoding the same bitmap on every Activity change
            sIcon = BitmapFactory.decodeResource(resources, R.drawable.icon_mland_home);
        }
    }

    private void setupDrawerForPlanes() {
        // TODO: Implement this method

    }

    private void getHtmlFromWeb() {
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        final StringBuilder stringBuilder = new StringBuilder();
                        final StringBuilder countryTextStringBuilder = new StringBuilder();
                        final StringBuilder planeNameStringBuilder = new StringBuilder();
                        final StringBuilder countryRankStringBuilder = new StringBuilder();
                        final StringBuilder descriptionTextStringBuilder =
                                new StringBuilder();
                        MLandTextView resultTwo =
                                (MLandTextView) findViewById(R.id.planeText);
                        MLandTextView resultThree =
                                (MLandTextView) findViewById(R.id.planeTextTwo);
                        MLandTextView countryText =
                                (MLandTextView) findViewById(R.id.countryText);
                        MLandTextView descriptionText =
                                (MLandTextView) findViewById(R.id.descriptionText);
                        MLandTextView countryRank =
                                (MLandTextView) findViewById(R.id.countryRank);
                        SimpleDraweeView planeImage =
                                (SimpleDraweeView) findViewById(R.id.planeDrawee);

                        try {
                            Document doc = Jsoup.connect(wt).get();
                            // String title = doc.title();
                            Element links = doc.select("div.mw-headline").first();
                            Elements test = doc.select("Description");

                            //      -- Get Country title --
                            Elements hrefs = doc.select("a[href][title]");
                            Elements div = doc.select("div.Usage_in_battles");
                            String linkText = div.text();
                            //    -- end get Country title --

                            //      -- Get Country rank --
                            Elements divCountryRank = doc.select("div.general_info_rank");
                            String countryRankTextString = divCountryRank.text();
                            //     -- end get Country rank --


                            //      -- Get General Info --
                            Elements divGeneralInfo = doc.select("div.general_info");
                            String generalInfoTextString = divGeneralInfo.text();
                            //     -- end get general info --

                            //      -- Get plane name --
                            Element divTwo = doc.select("div.general_info_name").first();
                            String linkTextTwo = divTwo.text();
                            //    -- end get plane name --

                            //      -- Get plane image --
                            Element planeImageElement = doc.select("img[src$=.jpg]").last();
                            String absoluteUrl =
                                    planeImageElement.absUrl("src"); // absolute URL on src
                            String srcValue = planeImageElement.attr("src");
                            planeImage.setImageURI(absoluteUrl);
                            planeImage.setVisibility(View.VISIBLE);
                            //    -- end get plane image --

                            //      -- Get usage in battles text --
                            Elements divUsage = doc.select("div.usage_in_battles");
                            String usageTextString = divUsage.text();
                            //     -- end get usage in battles text --

                            // *************      -- CONTINUE --      *************
                            Element testt =
                                    doc.select("p:contains(The Yak-23)")
                                            .first()
                                            .nextElementSibling();
                            Element testtTwo = doc.select("p:contains(The Yak-23)").first();
                            String ddd = testt.text();
                            String dddd = testtTwo.text();
                            Elements linksTwo = doc.select("div.general_info_nation");
                            String linkHref =
                                    linksTwo.attr("title"); // "http://example.com/"
                            Elements elementsTwo =
                                    doc.getElementsByClass("general_info_nation");
                            Element countryId = doc.getElementById("title");
                            //  links.text().replace("<span class=", "")

                            Elements textElements = doc.select("h1, p");
                            String lu = textElements.text();
                            Elements elements = doc.getElementsByClass("mw-headline");
                            for (int i = 0; i < elements.size(); i++) {
                                Element para = elements.get(i);
                                // doc.add(para.nextSibling().toString());
                            }

                            String textTwo = doc.body().text();

                            Elements table = doc.select("table");
                            countryTextStringBuilder.append(linkText);
                            planeNameStringBuilder.append(linkTextTwo);
                            countryRankStringBuilder.append(countryRankTextString);
                            descriptionTextStringBuilder
                                    .append(dddd)
                                    .append("\n")
                                    .append("\n")
                                    .append(ddd);

                        } catch (IOException e) {
                            stringBuilder
                                    .append("Error : ")
                                    .append(e.getMessage())
                                    .append(" ");
                        }
                        runOnUiThread(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        resultTwo.setText(stringBuilder.toString());
                                        countryText.setText(
                                                countryTextStringBuilder.toString());
                                        resultThree.setText(
                                                planeNameStringBuilder.toString());
                                        countryRank.setText(
                                                countryRankStringBuilder.toString());
                                        descriptionText.setText(
                                                descriptionTextStringBuilder.toString());
                                    }
                                });
                    }
                })
                .start();
    }
}
