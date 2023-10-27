package com.kyler.mland.egg.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class CircleView extends View {

    private Paint mPaint;
    private Paint mStrokePaint;

    public CircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mStrokePaint.setAntiAlias(true);
        mStrokePaint.setStyle(Paint.Style.STROKE);
        mStrokePaint.setStrokeWidth(2);
        mStrokePaint.setColor(Color.BLACK);
        mStrokePaint.setStrokeCap(Paint.Cap.BUTT);

    }

    public void setColor(String color) {
        mPaint.setColor(Color.parseColor(color));
    }


    @Override
    protected void onDraw(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();

        canvas.drawCircle(width / 2, height / 2, (width - 10) / 2, mPaint);
        canvas.drawCircle(width / 2, height / 2, (width - 10) / 2, mStrokePaint);
    }
}
