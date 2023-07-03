package com.kyler.mland.egg.activities;

import static com.kyler.mland.egg.activities.Home.setWindowFlag;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.samples.apps.iosched.ui.widget.ObservableScrollView;
import com.kyler.mland.egg.R;
import com.kyler.mland.egg.activities.planes.USAPlanes;
import com.kyler.mland.egg.utils.UIUtils;

/*
 TODO
*/

/**
 * Created by kyler on 1/18/16.
 */
@SuppressWarnings("DefaultFileTemplate")
public class MoreInfo extends AppCompatActivity {
    private static final float PHOTO_ASPECT_RATIO = 1.7777777f;
    // int scrollY = mScrollView.getScrollY();
    private static AppBarLayout parallaxAPB;
    private static TabLayout planeTabs;
    private CollapsingToolbarLayout collapsingTB;
    private NestedScrollView NSV;
    private boolean mHasPhoto;
    private ImageView planePic;
    private int mPhotoHeightPixels;
    private ObservableScrollView mScrollView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.more_info);
        planeTabs = findViewById(R.id.plane_tabs2);
        parallaxAPB = findViewById(R.id.appBarLayout);
        collapsingTB = findViewById(R.id.collapsingToolbarLayout);
        NSV = findViewById(R.id.nestedScrollView);
        planePic = findViewById(R.id.ivParallax);
        mScrollView = (ObservableScrollView) findViewById(R.id.scroll_view);


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


        hideStatusBar();
        mHasPhoto = true;


        mPhotoHeightPixels = 1;
        mPhotoHeightPixels = (int) (planePic.getWidth() / PHOTO_ASPECT_RATIO);
        mPhotoHeightPixels = Math.min(mPhotoHeightPixels, planePic.getHeight() / 3);

        float gapFillProgress = 1;
        int baseColor = getApplication().getColor(android.R.color.black);
        planePic.measure(0, 0);
        float alpha = Math.min(0, (float) 0.75 / planePic.getHeight());
        planePic.setBackgroundColor(UIUtils.getColorWithAlpha(alpha, (darkenColor(baseColor))));
        System.out.println(baseColor);
        System.out.println("TEST 3\n\n" + gapFillProgress);
        Toast.makeText(getApplicationContext(), "test:\n" + alpha, Toast.LENGTH_SHORT).show();

        if (mPhotoHeightPixels != 0) {
            gapFillProgress =
                    Math.min(Math.max(UIUtils.getProgress(1, 0, mPhotoHeightPixels), 0), 1);
            if (gapFillProgress != 1) {
                Toast.makeText(getApplicationContext(), "We arent locked", Toast.LENGTH_SHORT).show();


            } else if (gapFillProgress == 1) {
                System.out.println(gapFillProgress);
                Toast.makeText(getApplicationContext(), "We are locked", Toast.LENGTH_SHORT).show();

            }
        }
    }

    private void hideStatusBar() {
        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        super.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    public int darkenColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.8f;
        return Color.HSVToColor(hsv);
        // Credits for this: https://github.com/Musenkishi/wally
    }
}
