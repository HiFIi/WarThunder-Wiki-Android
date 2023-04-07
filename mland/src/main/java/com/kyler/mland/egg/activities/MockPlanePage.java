package com.kyler.mland.egg.activities;

import android.animation.ValueAnimator;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.core.ImageTranscoderType;
import com.facebook.imagepipeline.core.MemoryChunkType;
import com.kyler.mland.egg.R;

public class MockPlanePage extends AppCompatActivity {
    private static final float PHOTO_ASPECT_RATIO = 1.7777777f;
    private static final float DRAWEE_PHOTO_ASPECT_RATIO = 1.33f;
    public ImageView imageView;
    private SimpleDraweeView dv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(
                getApplicationContext(),
                ImagePipelineConfig.newBuilder(getApplicationContext())
                        .setMemoryChunkType(MemoryChunkType.BUFFER_MEMORY)
                        .setImageTranscoderType(ImageTranscoderType.JAVA_TRANSCODER)
                        .experiment()
                        .setNativeCodeDisabled(true)
                        .build());

        setContentView(R.layout.layout_mock_plane_page);

        SimpleDraweeView dv = (SimpleDraweeView) findViewById(R.id.plane_page_text);

        Uri mDraweeUri =
                Uri.parse(
                        "https://static.warthunder.com/upload/image//wallpapers/3840x2160_logo_drone_age_battlecruiser_alaska_eng_f08dcc6737f71a993755c2946b529f9c.jpg");
        dv.setAspectRatio(DRAWEE_PHOTO_ASPECT_RATIO);
        dv.setImageURI(mDraweeUri);
        dv.setVisibility(View.VISIBLE);


        ImageView img_animation = (ImageView) findViewById(R.id.mpi);
        Display display = getWindowManager().getDefaultDisplay();
        float width = display.getWidth();
        TranslateAnimation animation =
                new TranslateAnimation(
                        12, 80, 15, 30); // new TranslateAnimation(xFrom,xTo, yFrom,yTo)
        animation.setDuration(4300); // animation duration
        animation.setRepeatCount(Animation.INFINITE); // animation repeat count
        animation.setRepeatMode(ValueAnimator.REVERSE);

        img_animation.startAnimation(animation);
    }

    public int darkenColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.8f;
        return Color.HSVToColor(hsv);
        // Credits for this: https://github.com/Musenkishi/wally
    }
}
