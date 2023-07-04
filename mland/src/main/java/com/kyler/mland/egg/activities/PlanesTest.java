package com.kyler.mland.egg.activities;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.Animation;

import androidx.annotation.NonNull;

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
    public static final float oneF = 1f;
    public static final float onePointOhThree = 1.03f;
    public static final int two = 2;
    public static final int four = 4;
    public static final int zero = 0;
    public static final int fiftyFiveHundred = 5500;
    private static final float PHOTO_ASPECT_RATIO = 1.7777777f;
    private static final float DRAWEE_PHOTO_ASPECT_RATIO = 1.33f;
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final char[] LowerCaseAlphabet = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
    private static final char[] UpperCaseAlphabet = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
    public static Uri countryImageURL =
            Uri.parse(
                    "https://wiki.warthunder.com/images/thumb/f/f9/USSR_flag.png/45px-USSR_flag.png");
    private static Bitmap sIcon = null;
    private final String wt = "https://wiki.warthunder.com/Yak-23";
    private final ExecutorService service = Executors.newFixedThreadPool(MAXIMUM_POOL_SIZE);
    public String textSource;

    @Override
    protected int getSelfNavDrawerItem() {
        return NAVDRAWER_ITEM_HOME;
    }

    @Override
    protected Context getContext() {
        return PlanesTest.this;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
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

        //  setupDrawerForPlanes();

        SimpleDraweeView draweeView = (SimpleDraweeView) findViewById(R.id.planeDrawee);

        draweeView.getHierarchy();
        // .setPlaceholderImage(getApplicationContext().getDrawable(R.drawable.bubbles));

        Resources resources = this.getResources();

        Objects.requireNonNull(getSupportActionBar()).setTitle(null);
        mActionBarToolbar.setNavigationIcon(R.drawable.ic_drawer_white);

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(oneF, onePointOhThree);
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

        if (sIcon == null) {
            // Cache to avoid decoding the same bitmap on every Activity change
            sIcon = BitmapFactory.decodeResource(resources, R.drawable.icon_mland_home);
        }

        Handler handler = new Handler(Looper.getMainLooper());

        service.execute(
                () -> {
                    // Background work here
                    SimpleDraweeView countryIV = (SimpleDraweeView) findViewById(R.id.countryImage);
                    handler.post(
                            () -> {
                                // UI Thread work here
                                countryIV.setImageURI(countryImageURL);
                                countryIV.setVisibility(View.VISIBLE);
                                getHtmlFromWeb();
                            });
                });
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
                        final StringBuilder getUsageInBattleStringBuilder = new StringBuilder();
                        final StringBuilder descriptionTextStringBuilder = new StringBuilder();
                        final StringBuilder researchCostStringBuilder = new StringBuilder();
                        MLandTextView countryText =
                                (MLandTextView) findViewById(R.id.countryText);
                        MLandTextView descriptionText =
                                (MLandTextView) findViewById(R.id.descriptionText);
                        MLandTextView getUsageInBattleText =
                                (MLandTextView) findViewById(R.id.usageInBattlesText);
                        MLandTextView countryRank =
                                (MLandTextView) findViewById(R.id.countryRank);
                        SimpleDraweeView planeImage =
                                (SimpleDraweeView) findViewById(R.id.planeDrawee);
                        MLandTextView planeName =
                                (MLandTextView) findViewById(R.id.planeName);
                        MLandTextView researchCostText =
                                (MLandTextView) findViewById(R.id.researchTextAmount);

                        try {
                            Document doc = Jsoup.connect(wt).get();
                            // String title = doc.title();
                            Element links = doc.select("div.mw-headline").first();
                            Elements test = doc.select("Description");

                            //      -- Get Country name --
                            Element div = doc.select("a[href*=/category:]").first();
                            String countryName = div.text();
                            //    -- end get Country name --

                            //      -- Get Country rank --
                            Elements divCountryRank = doc.select("div.general_info_rank");
                            String countryRankTextString = divCountryRank.text();
                            //     -- end get Country rank --

                            //      -- Get General Info --
                            Elements divGeneralInfo = doc.select("div.general_info");
                            String generalInfoTextString = divGeneralInfo.text();
                            //     -- end get general info --

                            //      -- Get Research Cost --
                            Elements divResearchCost = doc.select("div.general_info_price_research");
                            String researchCostTextString = divResearchCost.text();
                            //     -- end get Resaarch Cost --

                            //      -- Get plane name --
                            Elements getPlaneName = doc.select("h1");
                            String getPlaneNameText = getPlaneName.text();
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
                            Elements divUsage = doc.select("span[id=Usage_in_battles]");
                            divUsage.select("div > p");
                            String usageTextString = divUsage.text();
                            //     -- end get A-Z list of american planes --

                            Elements divs = doc.select("div");
                            for (Element elem : divs) {
                                System.out.println(elem.html()); //get all elements inside div
                            }

                            // *************      -- CONTINUE --      *************
                            Element description = doc.select("p:contains(The Yak-23)").first().nextElementSibling();
                            Element descriptionTwo = doc.select("p:contains(The Yak-23)").first();
                            String descriptionTextTwo = description.text();
                            String descriptionTextOne = descriptionTwo.text();
                            Elements linksTwo = doc.select("div.general_info_nation");
                            Elements elementsTwo =
                                    doc.getElementsByClass("general_info_nation");
                            Element countryId = doc.getElementById("title");

                            Elements elements = doc.getElementsByClass("mw-headline");
                            for (int i = 0; i < elements.size(); i++) {
                                Element para = elements.get(i);
                                // doc.add(para.nextSibling().toString());
                            }

                            String textTwo = doc.body().text();

                            countryTextStringBuilder.append(countryName);
                            countryRankStringBuilder.append(countryRankTextString);
                            getUsageInBattleStringBuilder.append(usageTextString);
                            planeNameStringBuilder.append(getPlaneNameText);
                            researchCostStringBuilder.append(researchCostTextString);

                            descriptionTextStringBuilder
                                    .append(descriptionTextOne)
                                    .append("\n")
                                    .append("\n")
                                    .append(descriptionTextTwo);

                        } catch (IOException e) {
                            stringBuilder
                                    .append("Error : ")
                                    .append(e.getMessage())
                                    .append(" ");
                        }
                        runOnUiThread(
                                () -> {
                                    getUsageInBattleText.setText(getUsageInBattleStringBuilder.toString());
                                    countryText.setText(countryTextStringBuilder.toString());
                                    planeName.setText(planeNameStringBuilder.toString());
                                    countryRank.setText(countryRankStringBuilder.toString());
                                    researchCostText.setText(researchCostStringBuilder.toString());
                                });
                    }
                })
                .start();
    }
}
