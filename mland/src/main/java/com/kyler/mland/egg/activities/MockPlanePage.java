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
    }

    public int darkenColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.8f;
        return Color.HSVToColor(hsv);
        // Credits for this: https://github.com/Musenkishi/wally
    }
}
