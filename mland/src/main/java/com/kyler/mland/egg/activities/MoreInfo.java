package com.kyler.mland.egg.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;
import com.kyler.mland.egg.R;
import com.kyler.mland.egg.utils.UIUtils;

/*
 TODO
*/

/**
 * Created by kyler on 1/18/16.
 */
@SuppressWarnings("DefaultFileTemplate")
public class MoreInfo extends AppCompatActivity {
    // int scrollY = mScrollView.getScrollY();
    private static AppBarLayout parallaxAPB;
    private static TabLayout planeTabs;
    private CollapsingToolbarLayout collapsingTB;
    private NestedScrollView NSV;

    private ImageView planePic;

    private int mPhotoHeightPixels;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.more_info);
        planeTabs = findViewById(R.id.plane_tabs2);
        parallaxAPB = findViewById(R.id.appBarLayout);
        collapsingTB = findViewById(R.id.collapsingToolbarLayout);
        NSV = findViewById(R.id.nestedScrollView);
        planePic = findViewById(R.id.ivParallax);

        int baseColor = getApplication().getColor(android.R.color.white);
        float alpha = Math.min(1, (float) 0.5 / planePic.getHeight());
        Window window = getWindow();
        planePic.setBackgroundColor(UIUtils.getColorWithAlpha(alpha, (darkenColor(baseColor))));

        planePic.measure(0, 0);
        int planePicHeight = planePic.getMeasuredHeight();

        mPhotoHeightPixels = 0;


        float gapFillProgress = 1;
        gapFillProgress = Math.min(Math.max(UIUtils.getProgress(1, 0, planePicHeight), 0), 1);

        if (planePicHeight != 0) {
            gapFillProgress = Math.min(Math.max(UIUtils.getProgress(3,
                    100,
                    mPhotoHeightPixels), 0), 1);

            if (gapFillProgress == 1) {
                //    Toast.makeText(this, "Okay we're locked", Toast.LENGTH_LONG).show();
            } else if (gapFillProgress >= 1) {

            }
        }
    }

    public int darkenColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.8f;
        return Color.HSVToColor(hsv);
        // Credits for this: https://github.com/Musenkishi/wally
    }
}
