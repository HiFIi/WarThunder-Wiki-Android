package com.kyler.mland.egg.activities;

import static android.view.ViewTreeObserver.OnGlobalLayoutListener;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;

import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.samples.apps.iosched.ui.widget.CheckableFloatingActionButton;
import com.google.samples.apps.iosched.ui.widget.ObservableScrollView;
import com.kyler.mland.egg.MLandBase;
import com.kyler.mland.egg.R;
import com.kyler.mland.egg.utils.UIUtils;

/**
 * Created by kyler on 10/6/15.
 */
public class About extends MLandBase implements ObservableScrollView.Callbacks {
    private static final float PHOTO_ASPECT_RATIO = 1.7777777f;
    private static final float DRAWEE_PHOTO_ASPECT_RATIO = 1.33f;
    public SimpleDraweeView draweeView;
    private int mPhotoHeightPixels;
    private View mAddScheduleButtonContainer;
    private CheckableFloatingActionButton mAddScheduleButton;
    private int mAddScheduleButtonContainerHeightPixels;
    private View mScrollViewChild;
    private int mHeaderHeightPixels;
    private View mDetailsContainer;
    private ObservableScrollView mScrollView;
    private View mPhotoViewContainer;
    private boolean mHasPhoto;
    private float mMaxHeaderElevation;
    private View mHeaderBox;
    private Handler mHandler;
    private float mFABElevation;
    //  private static String draweeUrlString;
    private final OnGlobalLayoutListener mGlobalLayoutListener =
            new OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    recomputePhotoAndScrollingMetrics();
                }
            };

    @Override
    protected int getSelfNavDrawerItem() {
        return NAVDRAWER_ITEM_ABOUT;
    }

    @Override
    protected Context getContext() {
        return About.this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(getContext());
        super.getWindow()
                .getDecorView()
                .setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        boolean shouldBeFloatingWindow = false;
        setContentView(R.layout.about);
        getSupportActionBar().setTitle(null);

        //        Bitmap imageBitmap =
        // BitmapFactory.decodeResource(getResources(),R.drawable.wt_plane);
        //
        //        imageView.setImageBitmap(imageBitmap);
        //        DisplayMetrics displayMetrics = new DisplayMetrics();
        //        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        //        int height = displayMetrics.heightPixels;
        //        int width = displayMetrics.widthPixels;
        //
        //        Bitmap backgroundDominantColorBitmap
        // =PaletteUtils.getDominantGradient(imageBitmap,height,width);
        //        backgroundImageView.setImageBitmap(backgroundDominantColorBitmap);
        //
        mHasPhoto = true;

        mHandler = new Handler();
        initViews();
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
        mFABElevation = getResources().getDimensionPixelSize(R.dimen.fab_elevation);
        mMaxHeaderElevation =
                getResources().getDimensionPixelSize(R.dimen.session_detail_max_header_elevation);
        mScrollView = (ObservableScrollView) findViewById(R.id.scroll_view);
        mScrollView.addCallbacks(this);
        ViewTreeObserver vto = mScrollView.getViewTreeObserver();
        if (vto.isAlive()) {
            vto.addOnGlobalLayoutListener(mGlobalLayoutListener);
        }
        mScrollViewChild = findViewById(R.id.scroll_view_child);
        mScrollViewChild.setVisibility(View.VISIBLE);
        mDetailsContainer = findViewById(R.id.details_container);
        mHeaderBox = findViewById(R.id.header_session);
        mActionBarToolbar.setVisibility(View.VISIBLE);
        mPhotoViewContainer = findViewById(R.id.session_photo_container);
        mAddScheduleButtonContainer = findViewById(R.id.add_schedule_button_container);
        mAddScheduleButton = (CheckableFloatingActionButton) findViewById(R.id.add_schedule_button);

        Uri mDraweeUri =
                Uri.parse(
                        "https://static.warthunder.com/upload/image//wallpapers/3840x2160_logo_drone_age_battlecruiser_alaska_eng_f08dcc6737f71a993755c2946b529f9c.jpg");
        draweeView = (SimpleDraweeView) findViewById(R.id.session_photo);
        // draweeView.setAspectRatio(DRAWEE_PHOTO_ASPECT_RATIO);
        draweeView.setImageURI(mDraweeUri);
        displayData();
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
            mlp.topMargin = mHeaderHeightPixels + mPhotoHeightPixels;
            mDetailsContainer.setLayoutParams(mlp);
            mDetailsContainer.setPadding(16, 150, 16, 150);
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
        mAddScheduleButtonContainer.setTranslationY(
                newTop + mHeaderHeightPixels - mAddScheduleButtonContainerHeightPixels / 2);

        float gapFillProgress = 1;
        if (mPhotoHeightPixels != 0) {
            gapFillProgress =
                    Math.min(Math.max(UIUtils.getProgress(scrollY, 0, mPhotoHeightPixels), 0), 1);

            clearLightStatusBar();
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            if (gapFillProgress == 1) {
                activateLightStatusBar();
            } else {
                getWindow()
                        .setFlags(
                                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                                WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }

            ViewCompat.setElevation(draweeView, gapFillProgress * mMaxHeaderElevation / 2);
            ViewCompat.setElevation(mDetailsContainer, gapFillProgress * mMaxHeaderElevation / 4);
            ViewCompat.setElevation(mHeaderBox, gapFillProgress * mMaxHeaderElevation / 4);
            ViewCompat.setElevation(
                    mAddScheduleButtonContainer, gapFillProgress * mMaxHeaderElevation + mFABElevation / 4);
            ViewCompat.setElevation(
                    mAddScheduleButton, gapFillProgress * mMaxHeaderElevation + mFABElevation / 2);

            ViewCompat.setTranslationZ(mHeaderBox, gapFillProgress * mMaxHeaderElevation / 2);

            // testing
            ViewCompat.setTranslationZ(draweeView, gapFillProgress * mMaxHeaderElevation / 2);
            ViewCompat.setTranslationZ(mDetailsContainer, gapFillProgress * mMaxHeaderElevation / 2);

            // Move background photo (parallax effect)
            mPhotoViewContainer.setTranslationY(scrollY * 0.5f);
        }
    }

    private void activateLightStatusBar() {
        int oldSystemUiFlags = getWindow().getDecorView().getSystemUiVisibility();
        int newSystemUiFlags = oldSystemUiFlags;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            newSystemUiFlags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            getWindow().setStatusBarColor(ContextCompat.getColor(About.this, R.color.black__10_percent));
        }
        if (newSystemUiFlags != oldSystemUiFlags) {
            final int systemUiFlags = newSystemUiFlags;
            getWindow().getDecorView().setSystemUiVisibility(systemUiFlags);
            activateLightStatusBar();
        }
    }

    private void clearLightStatusBar() {
        int oldSystemUiFlags = getWindow().getDecorView().getSystemUiVisibility();
        int newSystemUiFlags = oldSystemUiFlags;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            newSystemUiFlags |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            getWindow().setStatusBarColor(ContextCompat.getColor(About.this, R.color.black__10_percent));
        }
        if (newSystemUiFlags != oldSystemUiFlags) {
            final int systemUiFlags = newSystemUiFlags;
            getWindow().getDecorView().setSystemUiVisibility(systemUiFlags);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    public void displayData() {
        mHandler.post(
                new Runnable() {
                    @Override
                    public void run() {
                        Uri mDraweeUri =
                                Uri.parse(
                                        "https://static.warthunder.com/upload/image//wallpapers/3840x2160_logo_drone_age_battlecruiser_alaska_eng_f08dcc6737f71a993755c2946b529f9c.jpg");

                        draweeView = (SimpleDraweeView) findViewById(R.id.session_photo);
                        //     draweeView.setAspectRatio(DRAWEE_PHOTO_ASPECT_RATIO);
                        draweeView.setImageURI(mDraweeUri);
                        mScrollViewChild.setVisibility(View.VISIBLE);
                    }
                });
    }

    public int darkenColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.8f;
        return Color.HSVToColor(hsv);
        // Credits for this: https://github.com/Musenkishi/wally
    }
}
