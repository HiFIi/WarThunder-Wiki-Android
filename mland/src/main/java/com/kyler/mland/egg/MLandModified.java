/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kyler.mland.egg;

import android.animation.LayoutTransition;
import android.animation.TimeAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

// It's like LLand, but "M"ultiplayer.
public class MLandModified extends FrameLayout {
    public static final int MAX_PLAYERS = 6;
    static final float[] hsv = {0, 0, 0};
    static final Rect sTmpRect = new Rect();
    private static final String TAG = "MLand";
    private static final boolean DEBUG = Log.isLoggable(TAG, Log.DEBUG);
    private static final boolean DEBUG_DRAW = false; // DEBUG
    /**
     * Originally, this was set to true, but idk. It looked pretty weird when the screen was touched
     * so I, Kyler (aka IAmReinvented) set the boolean to false.
     *
     * <p>
     *
     * <p>
     *
     * <p>
     *
     * <p>public static final boolean SHOW_TOUCHES = true;
     */
    private static final boolean SHOW_TOUCHES = false;
    private static final float PI_2 = (float) (Math.PI / 2);
    private static final boolean AUTOSTART = true;
    private static final boolean HAVE_STARS = true;
    private static final float DEBUG_SPEED_MULTIPLIER = 0.5f; // only if DEBUG
    private static final boolean DEBUG_IDDQD = Log.isLoggable(TAG + ".iddqd", Log.DEBUG);
    private static final int DEFAULT_PLAYERS = 1;
    private static final int MIN_PLAYERS = 1;
    private static final float CONTROLLER_VIBRATION_MULTIPLIER = 2f;
    private static final int[] ANTENNAE = new int[]{R.drawable.mm_antennae, R.drawable.mm_antennae2};
    private static final int[] EYES = new int[]{R.drawable.mm_eyes, R.drawable.mm_eyes2};
    private static final int[] MOUTHS =
            new int[]{
                    R.drawable.mm_mouth1, R.drawable.mm_mouth2, R.drawable.mm_mouth3, R.drawable.mm_mouth4
            };
    private static final int[] CACTI = {R.drawable.cactus1, R.drawable.cactus2, R.drawable.cactus3};
    private static final int[] MOUNTAINS = {
            R.drawable.mountain1, R.drawable.mountain2, R.drawable.mountain3
    };
    private static final int DAY = 0, NIGHT = 1, TWILIGHT = 2, SUNSET = 3, WTF = 4;
    private static final int[][] SKIES = {
            {0xFF4285F4, 0xFF6499BE}, // DAY
            {0xFF000010, 0xFF000000}, // NIGHT
            {0xFF000040, 0xFF000010}, // TWILIGHT
            {0xFFa08020, 0xFF204080}, // SUNSET
            {0xFF161718, 0xFFFFFFFF}, // WTF
    };
    private static final int SCENE_CITY = 0, SCENE_TX = 1, SCENE_ZRH = 2;
    private static final int SCENE_COUNT = 4;
    private static Params PARAMS;
    private static float dp = 1f;
    private final AudioAttributes mAudioAttrs =
            new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_GAME).build();
    private final Vibrator mVibrator;
    private final AudioManager mAudioManager;
    private final ArrayList<Player> mPlayers = new ArrayList<>();
    private final ArrayList<Obstacle> mObstaclesInPlay = new ArrayList<>();
    private final Paint mTouchPaint;
    private final Paint mPlayerTracePaint;
    private final ArrayList<Integer> mGameControllers = new ArrayList<>();
    private TimeAnimator mAnim;
    private View mSplash;
    private ViewGroup mScoreFields;
    private float t, dt;
    private float mLastPipeTime; // in sec
    private int mCurrentPipeId; // basically, equivalent to the current score
    private int mWidth, mHeight;
    private boolean mAnimating, mPlaying;
    private boolean mFrozen; // after death, a short backoff
    private int mCountdown = 0;
    private boolean mFlipped;
    private int mTaps;
    private int mTimeOfDay;
    private int mScene;

    public MLandModified(Context context) {
        this(context, null);
    }

    public MLandModified(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MLandModified(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        setFocusable(true);
        PARAMS = new Params(getResources());
        mTimeOfDay = irand(0, SKIES.length - 1);
        mScene = irand(0, SCENE_COUNT);

        mTouchPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTouchPaint.setColor(0x80FFFFFF);
        mTouchPaint.setStyle(Paint.Style.FILL);

        mPlayerTracePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPlayerTracePaint.setColor(0x80FFFFFF);
        mPlayerTracePaint.setStyle(Paint.Style.STROKE);
        mPlayerTracePaint.setStrokeWidth(2 * dp);

        // we assume everything will be laid out left|top
        setLayoutDirection(LAYOUT_DIRECTION_LTR);

        setupPlayers(DEFAULT_PLAYERS);

        //    MetricsLogger.count(getContext(), "egg_mland_create", 1);
    }

    private static void L(String s, Object... objects) {
        if (DEBUG) {
            Log.d(TAG, objects.length == 0 ? s : String.format(s, objects));
        }
    }

    private static boolean isGamePad(InputDevice dev) {
        int sources = dev.getSources();

        // Verify that the device has gamepad buttons, control sticks, or both.
        return (((sources & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD)
                || ((sources & InputDevice.SOURCE_JOYSTICK) == InputDevice.SOURCE_JOYSTICK));
    }

    private static float luma(int bgcolor) {
        return 0.2126f * (float) (bgcolor & 0xFF0000) / 0xFF0000
                + 0.7152f * (float) (bgcolor & 0xFF00) / 0xFF00
                + 0.0722f * (float) (bgcolor & 0xFF) / 0xFF;
    }

    private static float lerp(float x, float a, float b) {
        return (b - a) * x + a;
    }

    private static float rlerp(float v, float a, float b) {
        return (v - a) / (b - a);
    }

    private static float clamp(float f) {
        return f < 0f ? 0f : f > 1f ? 1f : f;
    }

    private static float frand() {
        return (float) Math.random();
    }

    private static float frand(float a, float b) {
        return lerp(frand(), a, b);
    }

    private static int irand(int a, int b) {
        return Math.round(frand((float) a, (float) b));
    }

    private static int pick(int[] l) {
        return l[irand(0, l.length - 1)];
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        dp = getResources().getDisplayMetrics().density;

        reset();
        if (AUTOSTART) {
            start(false);
        }
    }

    @Override
    public boolean willNotDraw() {
        return !DEBUG;
    }

    public int getGameWidth() {
        return mWidth;
    }

    public int getGameHeight() {
        return mHeight;
    }

    private float getGameTime() {
        return t;
    }

    public float getLastTimeStep() {
        return dt;
    }

    public void setScoreFieldHolder(ViewGroup vg) {
        mScoreFields = vg;
        if (vg != null) {
            final LayoutTransition lt = new LayoutTransition();
            lt.setDuration(250);
            mScoreFields.setLayoutTransition(lt);
        }
        for (Player p : mPlayers) {
            mScoreFields.addView(
                    p.mScoreField,
                    new MarginLayoutParams(MarginLayoutParams.WRAP_CONTENT, MarginLayoutParams.MATCH_PARENT));
        }
    }

    public void setSplash(View v) {
        mSplash = v;
    }

    public ArrayList getGameControllers() {
        mGameControllers.clear();
        int[] deviceIds = InputDevice.getDeviceIds();
        for (int deviceId : deviceIds) {
            InputDevice dev = InputDevice.getDevice(deviceId);
            if (isGamePad(dev)) {
                if (!mGameControllers.contains(deviceId)) {
                    mGameControllers.add(deviceId);
                }
            }
        }
        return mGameControllers;
    }

    private int getControllerPlayer(int id) {
        final int player = mGameControllers.indexOf(id);
        if (player < 0 || player >= mPlayers.size()) return 0;
        return player;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        dp = getResources().getDisplayMetrics().density;

        stop();

        reset();
        if (AUTOSTART) {
            start(false);
        }
    }

    private Player getPlayer(int i) {
        return i < mPlayers.size() ? mPlayers.get(i) : null;
    }

    private int addPlayerInternal(Player p) {
        mPlayers.add(p);
        realignPlayers();
        @SuppressLint("InflateParams")
        TextView scoreField =
                (TextView) LayoutInflater.from(getContext()).inflate(R.layout.mland_scorefield, null);
        if (mScoreFields != null) {
            mScoreFields.addView(
                    scoreField,
                    new MarginLayoutParams(MarginLayoutParams.WRAP_CONTENT, MarginLayoutParams.MATCH_PARENT));
        }
        p.setScoreField(scoreField);
        return mPlayers.size() - 1;
    }

    private void removePlayerInternal(Player p) {
        if (mPlayers.remove(p)) {
            removeView(p);
            mScoreFields.removeView(p.mScoreField);
            realignPlayers();
        }
    }

    private void realignPlayers() {
        final int N = mPlayers.size();
        float x = (mWidth - (N - 1) * PARAMS.PLAYER_SIZE) / 2;
        for (int i = 0; i < N; i++) {
            final Player p = mPlayers.get(i);
            p.setX(x);
            x += PARAMS.PLAYER_SIZE;
        }
    }

    private void clearPlayers() {
        while (mPlayers.size() > 0) {
            removePlayerInternal(mPlayers.get(0));
        }
    }

    public void setupPlayers(int num) {
        clearPlayers();
        for (int i = 0; i < num; i++) {
            addPlayerInternal(Player.create(this));
        }
    }

    public void addPlayer() {
        if (getNumPlayers() == MAX_PLAYERS) return;
        addPlayerInternal(Player.create(this));
    }

    public int getNumPlayers() {
        return mPlayers.size();
    }

    public void removePlayer() {
        if (getNumPlayers() == MIN_PLAYERS) return;
        removePlayerInternal(mPlayers.get(mPlayers.size() - 1));
    }

    private void thump(int playerIndex, long ms) {
        if (mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT) {
            // No interruptions. Not even game haptics.
            return;
        }
        if (playerIndex < mGameControllers.size()) {
            int controllerId = mGameControllers.get(playerIndex);
            InputDevice dev = InputDevice.getDevice(controllerId);
            if (dev != null && dev.getVibrator().hasVibrator()) {
                dev.getVibrator().vibrate((long) (ms * CONTROLLER_VIBRATION_MULTIPLIER), mAudioAttrs);
                return;
            }
        }
        mVibrator.vibrate(ms, mAudioAttrs);
    }

    private void reset() {
        L("reset");
        final Drawable sky =
                new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, SKIES[mTimeOfDay]);
        sky.setDither(true);
        setBackground(sky);

        mFlipped = frand() > 0.5f;
        setScaleX(mFlipped ? -1 : 1);

        int i = getChildCount();
        while (i-- > 0) {
            final View v = getChildAt(i);
            if (v instanceof GameView) {
                removeViewAt(i);
            }
        }

        mObstaclesInPlay.clear();
        mCurrentPipeId = 0;

        mWidth = getWidth();
        mHeight = getHeight();

        boolean showingSun = (mTimeOfDay == DAY || mTimeOfDay == SUNSET) && frand() > 0.25;
        if (showingSun) {
            final Star sun = new Star(getContext());
            sun.setBackgroundResource(R.drawable.sun);
            final int w = getResources().getDimensionPixelSize(R.dimen.sun_size);
            sun.setTranslationX(frand(w, mWidth - w));
            if (mTimeOfDay == DAY) {
                sun.setTranslationY(
                        frand(w, (mHeight * 0.33f))); // Modified October 26th. #neverforget 0.66f
                sun.getBackground().setTint(0);
            } else {
                sun.setTranslationY(frand(mHeight * 0.66f, mHeight - w));
                sun.getBackground().setTintMode(PorterDuff.Mode.SRC_ATOP);
                sun.getBackground().setTint(0xC0FF8000);
            }
            addView(sun, new LayoutParams(w, w));
        }
        if (!showingSun) {
            final boolean dark = mTimeOfDay == NIGHT || mTimeOfDay == TWILIGHT;
            final float ff = frand();
            if ((dark && ff < 0.75f) || ff < 0.5f) {
                final Star moon = new Star(getContext());
                moon.setBackgroundResource(R.drawable.moon);
                moon.getBackground().setAlpha(dark ? 255 : 128);
                moon.setScaleX(frand() > 0.5 ? -1 : 1);
                moon.setRotation(moon.getScaleX() * frand(5, 30));
                final int w = getResources().getDimensionPixelSize(R.dimen.sun_size);
                moon.setTranslationX(frand(w, mWidth - w));
                moon.setTranslationY(frand(w, mHeight - w));
                addView(moon, new LayoutParams(w, w));
            }
        }

        final int mh = mHeight / 6;
        final boolean cloudless = frand() < 0.25;
        final int N = 20;
        for (i = 0; i < N; i++) {
            final float r1 = frand();
            final Scenery s;
            if (HAVE_STARS && r1 < 0.3 && mTimeOfDay != DAY) {
                s = new Star(getContext());
            } else if (r1 < 0.6 && !cloudless) {
                s = new Cloud(getContext());
            } else {
                switch (mScene) {
                    case SCENE_ZRH:
                        s = new Mountain(getContext());
                        break;
                    case SCENE_TX:
                        s = new Cactus(getContext());
                        break;
                    case SCENE_CITY:
                    default:
                        s = new Building(getContext());
                        break;
                }
                s.z = (float) i / N;
                // no more shadows for these things
                //s.setTranslationZ(PARAMS.SCENERY_Z * (1+s.z));
                s.v = 0.85f * s.z; // buildings move proportional to their distance
                if (mScene == SCENE_CITY) {
                    s.setBackgroundColor(Color.GRAY);
                    s.h = irand(PARAMS.BUILDING_HEIGHT_MIN, mh);
                }
                final int c = (int) (255f * s.z);
                final Drawable bg = s.getBackground();
                if (bg != null) bg.setColorFilter(Color.rgb(c, c, c), PorterDuff.Mode.MULTIPLY);
            }
            final LayoutParams lp = new LayoutParams(s.w, s.h);
            if (s instanceof Building) {
                lp.gravity = Gravity.BOTTOM;
            } else {
                lp.gravity = Gravity.TOP;
                final float r = frand();
                if (s instanceof Star) {
                    lp.topMargin = (int) (r * r * mHeight);
                } else {
                    lp.topMargin = (int) (1 - r * r * mHeight / 2) + mHeight / 2;
                }
            }

            addView(s, lp);
            s.setTranslationX(frand(-lp.width, mWidth + lp.width));
        }

        for (Player p : mPlayers) {
            addView(p); // put it back!
            p.reset();
        }

        realignPlayers();

        if (mAnim != null) {
            mAnim.cancel();
        }
        mAnim = new TimeAnimator();
        mAnim.setTimeListener((timeAnimator, t, dt) -> step(t, dt));
    }

    public void start(boolean startPlaying) {
        L("start(startPlaying=%s)", startPlaying ? "true" : "false");
        if (startPlaying && mCountdown <= 0) {
            showSplash();

            mSplash.findViewById(R.id.play_button).setEnabled(false);

            final View playImage = mSplash.findViewById(R.id.play_button_image);
            final TextView playText = (TextView) mSplash.findViewById(R.id.play_button_text);

            playImage.animate().alpha(0f);
            playText.animate().alpha(1f);

            mCountdown = 5;
            post(
                    new Runnable() {
                        @Override
                        public void run() {
                            if (mCountdown == 0) {
                                startPlaying();
                            } else {
                                postDelayed(this, 500);
                            }
                            playText.setText(String.valueOf(mCountdown));
                            mCountdown--;
                        }
                    });
        }

        for (Player p : mPlayers) {
            p.setVisibility(View.INVISIBLE);
        }

        if (!mAnimating) {
            mAnim.start();
            mAnimating = true;
        }
    }

    private void hideSplash() {
        if (mSplash != null && mSplash.getVisibility() == View.VISIBLE) {
            mSplash.setClickable(false);
            mSplash
                    .animate()
                    .alpha(0)
                    .translationZ(0)
                    .setDuration(300)
                    .withEndAction(() -> mSplash.setVisibility(View.GONE));
        }
    }

    public void showSplash() {
        if (mSplash != null && mSplash.getVisibility() != View.VISIBLE) {
            mSplash.setClickable(true);
            mSplash.setAlpha(0.75f);
            mSplash.setVisibility(View.VISIBLE);
            mSplash.animate().alpha(1f).setDuration(500);
            mSplash.findViewById(R.id.play_button_image).setAlpha(1f);
            mSplash.findViewById(R.id.play_button_text).setAlpha(0f);
            mSplash.findViewById(R.id.play_button).setEnabled(true);
            mSplash.findViewById(R.id.play_button).requestFocus();
        }
    }

    private void startPlaying() {
        mPlaying = true;

        t = 0;
        // there's a sucker born every OBSTACLE_PERIOD
        mLastPipeTime = getGameTime() - PARAMS.OBSTACLE_PERIOD;

        hideSplash();

        realignPlayers();
        mTaps = 0;

        final int N = mPlayers.size();
        //    MetricsLogger.histogram(getContext(), "egg_mland_players", N);
        for (int i = 0; i < N; i++) {
            final Player p = mPlayers.get(i);
            p.setVisibility(View.VISIBLE);
            p.reset();
            p.start();
            p.boost(-1, -1); // start you off flying!
            p.unboost(); // not forever, though
        }
    }

    public void stop() {
        boolean mGodMode = false;
        if (mAnimating) {
            mAnim.cancel();
            mAnim = null;
            mAnimating = false;
            mPlaying = false;
            mTimeOfDay = irand(0, SKIES.length - 1); // for next reset
            mScene = irand(0, SCENE_COUNT);
            mFrozen = true;

            for (Player p : mPlayers) {
                p.die();
            }
            postDelayed(() -> mFrozen = false, 250);
        }
    }

    @SuppressLint("RtlHardcoded")
    private void step(long t_ms, long dt_ms) {
        t = t_ms / 1000f; // seconds
        dt = dt_ms / 1000f;

        if (DEBUG) {
            t *= DEBUG_SPEED_MULTIPLIER;
            dt *= DEBUG_SPEED_MULTIPLIER;
        }

        // 1. Move all objects and update bounds
        final int N = getChildCount();
        int i = 0;
        for (; i < N; i++) {
            final View v = getChildAt(i);
            if (v instanceof GameView) {
                ((GameView) v).step(t_ms, dt_ms, t, dt);
            }
        }

        if (mPlaying) {
            int livingPlayers = 0;
            for (i = 0; i < mPlayers.size(); i++) {
                final Player p = getPlayer(i);

                if (p.mAlive) {
                    // 2. Check for altitude
                    if (p.below(mHeight)) {
                        if (DEBUG_IDDQD) {
                            poke(i);
                            unpoke(i);
                        } else {
                            L("player %d hit the floor", i);
                            thump(i, 80);
                            p.die();
                        }
                    }

                    // 3. Check for obstacles
                    int maxPassedStem = 0;
                    for (int j = mObstaclesInPlay.size(); j-- > 0; ) {
                        final Obstacle ob = mObstaclesInPlay.get(j);
                        if (ob.intersects(p) && !DEBUG_IDDQD) {
                            L("player hit an obstacle");
                            thump(i, 80);
                            p.die();
                        } else if (ob.cleared(p)) {
                            if (ob instanceof Stem) {
                                maxPassedStem = Math.max(maxPassedStem, ((Stem) ob).id);
                            }
                        }
                    }

                    if (maxPassedStem > p.mScore) {
                        p.addScore(1);
                    }
                }

                if (p.mAlive) livingPlayers++;
            }

            if (livingPlayers == 0) {
                stop();

                //    MetricsLogger.count(getContext(), "egg_mland_taps", mTaps);
                mTaps = 0;
                final int playerCount = mPlayers.size();
                for (int pi = 0; pi < playerCount; pi++) {
                    final Player p = mPlayers.get(pi);
                    //    MetricsLogger.histogram(getContext(), "egg_mland_score", p.getScore());
                }
            }
        }

        // 4. Handle edge of screen
        // Walk backwards to make sure removal is safe
        while (i-- > 0) {
            final View v = getChildAt(i);
            if (v instanceof Obstacle) {
                if (v.getTranslationX() + v.getWidth() < 0) {
                    removeViewAt(i);
                    mObstaclesInPlay.remove(v);
                }
            } else if (v instanceof Scenery) {
                final Scenery s = (Scenery) v;
                if (v.getTranslationX() + s.w < 0) {
                    v.setTranslationX(getWidth());
                }
            }
        }

        // 3. Time for more obstacles!
        if (mPlaying && (t - mLastPipeTime) > PARAMS.OBSTACLE_PERIOD) {
            mLastPipeTime = t;
            mCurrentPipeId++;
            final int obstacley =
                    (int) (frand() * (mHeight - 2 * PARAMS.OBSTACLE_MIN - PARAMS.OBSTACLE_GAP))
                            + PARAMS.OBSTACLE_MIN;

            final int inset = (PARAMS.OBSTACLE_WIDTH - PARAMS.OBSTACLE_STEM_WIDTH) / 2;
            final int yinset = PARAMS.OBSTACLE_WIDTH / 2;

            final int d1 = irand(0, 250);
            final Obstacle s1 = new Stem(getContext(), obstacley - yinset, false);
            addView(
                    s1, new LayoutParams(PARAMS.OBSTACLE_STEM_WIDTH, (int) s1.h, Gravity.TOP | Gravity.LEFT));
            s1.setTranslationX(mWidth + inset);
            s1.setTranslationY(-s1.h - yinset);
            s1.setTranslationZ(PARAMS.OBSTACLE_Z * 0.75f);
            s1.animate().translationY(0).setStartDelay(d1).setDuration(250);
            mObstaclesInPlay.add(s1);

            final Obstacle p1 = new Pop(getContext(), PARAMS.OBSTACLE_WIDTH);
            addView(
                    p1,
                    new LayoutParams(
                            PARAMS.OBSTACLE_WIDTH, PARAMS.OBSTACLE_WIDTH, Gravity.TOP | Gravity.LEFT));
            p1.setTranslationX(mWidth);
            p1.setTranslationY(-PARAMS.OBSTACLE_WIDTH);
            p1.setTranslationZ(PARAMS.OBSTACLE_Z);
            p1.setScaleX(0.25f);
            p1.setScaleY(-0.25f);
            p1.animate()
                    .translationY(s1.h - inset)
                    .scaleX(1f)
                    .scaleY(-1f)
                    .setStartDelay(d1)
                    .setDuration(250);
            mObstaclesInPlay.add(p1);

            final int d2 = irand(0, 250);
            final Obstacle s2 =
                    new Stem(getContext(), mHeight - obstacley - PARAMS.OBSTACLE_GAP - yinset, true);
            addView(
                    s2, new LayoutParams(PARAMS.OBSTACLE_STEM_WIDTH, (int) s2.h, Gravity.TOP | Gravity.LEFT));
            s2.setTranslationX(mWidth + inset);
            s2.setTranslationY(mHeight + yinset);
            s2.setTranslationZ(PARAMS.OBSTACLE_Z * 0.75f);
            s2.animate().translationY(mHeight - s2.h).setStartDelay(d2).setDuration(500);
            mObstaclesInPlay.add(s2);

            final Obstacle p2 = new Pop(getContext(), PARAMS.OBSTACLE_WIDTH);
            addView(
                    p2,
                    new LayoutParams(
                            PARAMS.OBSTACLE_WIDTH, PARAMS.OBSTACLE_WIDTH, Gravity.TOP | Gravity.LEFT));
            p2.setTranslationX(mWidth);
            p2.setTranslationY(mHeight);
            p2.setTranslationZ(PARAMS.OBSTACLE_Z);
            p2.setScaleX(0.25f);
            p2.setScaleY(0.25f);
            p2.animate()
                    .translationY(mHeight - s2.h - yinset)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setStartDelay(d2)
                    .setDuration(400);
            mObstaclesInPlay.add(p2);
        }

        if (SHOW_TOUCHES || DEBUG_DRAW) invalidate();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        L("touch: %s", ev);
        final int actionIndex = ev.getActionIndex();
        final float x = ev.getX(actionIndex);
        final float y = ev.getY(actionIndex);
        int playerIndex = (int) (getNumPlayers() * (x / getWidth()));
        if (mFlipped) playerIndex = getNumPlayers() - 1 - playerIndex;
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                poke(playerIndex, x, y);
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                unpoke(playerIndex);
                return true;
        }
        return false;
    }

    @Override
    public boolean onTrackballEvent(MotionEvent ev) {
        L("trackball: %s", ev);
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                poke(0);
                return true;
            case MotionEvent.ACTION_UP:
                unpoke(0);
                return true;
        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent ev) {
        L("keyDown: %d", keyCode);
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_DPAD_UP:
            case KeyEvent.KEYCODE_SPACE:
            case KeyEvent.KEYCODE_ENTER:
            case KeyEvent.KEYCODE_BUTTON_A:
                int player = getControllerPlayer(ev.getDeviceId());
                poke(player);
                return true;
        }
        return false;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent ev) {
        L("keyDown: %d", keyCode);
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_DPAD_UP:
            case KeyEvent.KEYCODE_SPACE:
            case KeyEvent.KEYCODE_ENTER:
            case KeyEvent.KEYCODE_BUTTON_A:
                int player = getControllerPlayer(ev.getDeviceId());
                unpoke(player);
                return true;
        }
        return false;
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent ev) {
        L("generic: %s", ev);
        return false;
    }

    private void poke(int playerIndex) {
        poke(playerIndex, -1, -1);
    }

    private void poke(int playerIndex, float x, float y) {
        L("poke(%d)", playerIndex);
        if (mFrozen) return;
        if (!mAnimating) {
            reset();
        }
        if (!mPlaying) {
            start(true);
        } else {
            final Player p = getPlayer(playerIndex);
            if (p == null) return; // no player for this controller
            p.boost(x, y);
            mTaps++;
            if (DEBUG) {
                p.dv *= DEBUG_SPEED_MULTIPLIER;
                p.animate().setDuration((long) (200 / DEBUG_SPEED_MULTIPLIER));
            }
        }
    }

    private void unpoke(int playerIndex) {
        L("unboost(%d)", playerIndex);
        if (mFrozen || !mAnimating || !mPlaying) return;
        final Player p = getPlayer(playerIndex);
        if (p == null) return; // no player for this controller
        p.unboost();
    }

    @Override
    public void onDraw(Canvas c) {
        super.onDraw(c);

        if (SHOW_TOUCHES) {
            for (Player p : mPlayers) {
                if (p.mTouchX > 0) {
                    mTouchPaint.setColor(0x80FFFFFF & p.color);
                    mPlayerTracePaint.setColor(0x80FFFFFF & p.color);
                    float x1 = p.mTouchX;
                    float y1 = p.mTouchY;
                    c.drawCircle(x1, y1, 100, mTouchPaint);
                    float x2 = p.getX() + p.getPivotX();
                    float y2 = p.getY() + p.getPivotY();
                    float angle = PI_2 - (float) Math.atan2(x2 - x1, y2 - y1);
                    x1 += 100 * Math.cos(angle);
                    y1 += 100 * Math.sin(angle);
                    c.drawLine(x1, y1, x2, y2, mPlayerTracePaint);
                }
            }
        }

        if (!DEBUG_DRAW) return;

        final Paint pt = new Paint();
        pt.setColor(0xFFFFFFFF);
        for (Player p : mPlayers) {
            final int L = p.corners.length;
            final int N = L / 2;
            for (int i = 0; i < N; i++) {
                final int x = (int) p.corners[i * 2];
                final int y = (int) p.corners[i * 2 + 1];
                c.drawCircle(x, y, 4, pt);
                c.drawLine(x, y, p.corners[(i * 2 + 2) % L], p.corners[(i * 2 + 3) % L], pt);
            }
        }

        pt.setStyle(Paint.Style.STROKE);
        pt.setStrokeWidth(getResources().getDisplayMetrics().density);

        final int M = getChildCount();
        pt.setColor(0x8000FF00);
        for (int i = 0; i < M; i++) {
            final View v = getChildAt(i);
            if (v instanceof Player) continue;
            if (!(v instanceof GameView)) continue;
            if (v instanceof Pop) {
                final Pop pop = (Pop) v;
                c.drawCircle(pop.cx, pop.cy, pop.r, pt);
            } else {
                final Rect r = new Rect();
                v.getHitRect(r);
                c.drawRect(r, pt);
            }
        }

        pt.setColor(Color.BLACK);
        final StringBuilder sb = new StringBuilder("obstacles: ");
        for (Obstacle ob : mObstaclesInPlay) {
            sb.append(ob.hitRect.toShortString());
            sb.append(" ");
        }
        pt.setTextSize(20f);
        c.drawText(sb.toString(), 20, 100, pt);
    }

    private interface GameView {
        void step(long t_ms, long dt_ms, float t, float dt);
    }

    private static class Params {
        final float TRANSLATION_PER_SEC;
        final int OBSTACLE_SPACING;
        final int OBSTACLE_PERIOD;
        final int BOOST_DV;
        final int PLAYER_HIT_SIZE;
        final int PLAYER_SIZE;
        final int OBSTACLE_WIDTH;
        final int OBSTACLE_STEM_WIDTH;
        final int OBSTACLE_GAP;
        final int BUILDING_WIDTH_MIN;
        final int BUILDING_WIDTH_MAX;
        final int BUILDING_HEIGHT_MIN;
        final int CLOUD_SIZE_MIN;
        final int CLOUD_SIZE_MAX;
        final int STAR_SIZE_MIN;
        final int STAR_SIZE_MAX;
        final int G;
        final int MAX_V;
        final float SCENERY_Z;
        final float OBSTACLE_Z;
        final float PLAYER_Z;
        final float PLAYER_Z_BOOST;
        final float HUD_Z;
        int OBSTACLE_MIN;

        Params(Resources res) {
            TRANSLATION_PER_SEC = res.getDimension(R.dimen.translation_per_sec);
            OBSTACLE_SPACING = res.getDimensionPixelSize(R.dimen.obstacle_spacing);
            OBSTACLE_PERIOD = (int) (OBSTACLE_SPACING / TRANSLATION_PER_SEC);
            BOOST_DV = res.getDimensionPixelSize(R.dimen.boost_dv);
            PLAYER_HIT_SIZE = res.getDimensionPixelSize(R.dimen.player_hit_size);
            PLAYER_SIZE = res.getDimensionPixelSize(R.dimen.player_size);
            OBSTACLE_WIDTH = res.getDimensionPixelSize(R.dimen.obstacle_width);
            OBSTACLE_STEM_WIDTH = res.getDimensionPixelSize(R.dimen.obstacle_stem_width);
            OBSTACLE_GAP = res.getDimensionPixelSize(R.dimen.obstacle_gap);
            OBSTACLE_MIN = res.getDimensionPixelSize(R.dimen.obstacle_height_min);
            BUILDING_HEIGHT_MIN = res.getDimensionPixelSize(R.dimen.building_height_min);
            BUILDING_WIDTH_MIN = res.getDimensionPixelSize(R.dimen.building_width_min);
            BUILDING_WIDTH_MAX = res.getDimensionPixelSize(R.dimen.building_width_max);
            CLOUD_SIZE_MIN = res.getDimensionPixelSize(R.dimen.cloud_size_min);
            CLOUD_SIZE_MAX = res.getDimensionPixelSize(R.dimen.cloud_size_max);
            STAR_SIZE_MIN = res.getDimensionPixelSize(R.dimen.star_size_min);
            STAR_SIZE_MAX = res.getDimensionPixelSize(R.dimen.star_size_max);

            G = res.getDimensionPixelSize(R.dimen.G);
            MAX_V = res.getDimensionPixelSize(R.dimen.max_v);

            SCENERY_Z = res.getDimensionPixelSize(R.dimen.scenery_z);
            OBSTACLE_Z = res.getDimensionPixelSize(R.dimen.obstacle_z);
            PLAYER_Z = res.getDimensionPixelSize(R.dimen.player_z);
            PLAYER_Z_BOOST = res.getDimensionPixelSize(R.dimen.player_z_boost);
            HUD_Z = res.getDimensionPixelSize(R.dimen.hud_z);

            // Sanity checking
            if (OBSTACLE_MIN <= OBSTACLE_WIDTH / 2) {
                L("error: obstacles might be too short, adjusting");
                OBSTACLE_MIN = OBSTACLE_WIDTH / 2 + 1;
            }
        }
    }

    private static class Player extends ImageView implements GameView {
        static int sNextColor = 0;
        final int color;
        private final int[] sColors =
                new int[]{
                        //0xFF78C557,
                        0xFFF1F1F1, 0xFF3B78E7, 0xFFF4B400, 0xFF0F9D58, 0xFF7B1880, 0xFF9E9E9E,
                };
        private final float[] sHull =
                new float[]{
                        0.3f, 0f, // left antenna
                        0.7f, 0f, // right antenna
                        0.92f, 0.33f, // off the right shoulder of Orion
                        0.92f, 0.75f, // right hand (our right, not his right)
                        0.6f, 1f, // right foot
                        0.4f, 1f, // left foot BLUE!
                        0.08f, 0.75f, // sinistram
                        0.08f, 0.33f, // cold shoulder
                };
        final float[] corners = new float[sHull.length];
        float dv;
        private MLandModified mLandModified;
        private boolean mBoosting;
        private float mTouchX = -1, mTouchY = -1;
        private boolean mAlive;
        private int mScore;
        private TextView mScoreField;

        public Player(Context context) {
            super(context);

            setBackgroundResource(R.drawable.android);
            getBackground().setTintMode(PorterDuff.Mode.SRC_ATOP);
            color = sColors[(sNextColor++ % sColors.length)];
            getBackground().setTint(color);
            setOutlineProvider(
                    new ViewOutlineProvider() {
                        @Override
                        public void getOutline(View view, Outline outline) {
                            final int w = view.getWidth();
                            final int h = view.getHeight();
                            final int ix = (int) (w * 0.3f);
                            final int iy = (int) (h * 0.2f);
                            outline.setRect(ix, iy, w - ix, h - iy);
                        }
                    });
        }

        static Player create(MLandModified land) {
            final Player p = new Player(land.getContext());
            p.mLandModified = land;
            p.reset();
            p.setVisibility(View.INVISIBLE);
            land.addView(p, new LayoutParams(PARAMS.PLAYER_SIZE, PARAMS.PLAYER_SIZE));
            return p;
        }

        public int getScore() {
            return mScore;
        }

        private void setScore(int score) {
            mScore = score;
            if (mScoreField != null) {
                mScoreField.setText(DEBUG_IDDQD ? "??" : String.valueOf(score));
            }
        }

        private void addScore(int incr) {
            setScore(mScore + incr);
        }

        void setScoreField(TextView tv) {
            mScoreField = tv;
            if (tv != null) {
                setScore(mScore); // reapply
                //mScoreField.setBackgroundResource(R.drawable.scorecard);
                mScoreField.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
                mScoreField.setTextColor(luma(color) > 0.7f ? 0xFF000000 : 0xFFFFFFFF);
            }
        }

        void reset() {
            //setX(mLand.mWidth / 2);
            setY(
                    mLandModified.mHeight / 2
                            + (int) (Math.random() * PARAMS.PLAYER_SIZE)
                            - PARAMS.PLAYER_SIZE / 2);
            setScore(0);
            setScoreField(mScoreField); // refresh color
            mBoosting = false;
            dv = 0;
        }

        void prepareCheckIntersections() {
            final int inset = (PARAMS.PLAYER_SIZE - PARAMS.PLAYER_HIT_SIZE) / 2;
            final int scale = PARAMS.PLAYER_HIT_SIZE;
            final int N = sHull.length / 2;
            for (int i = 0; i < N; i++) {
                corners[i * 2] = scale * sHull[i * 2] + inset;
                corners[i * 2 + 1] = scale * sHull[i * 2 + 1] + inset;
            }
            final Matrix m = getMatrix();
            m.mapPoints(corners);
        }

        boolean below(int h) {
            final int N = corners.length / 2;
            for (int i = 0; i < N; i++) {
                final int y = (int) corners[i * 2 + 1];
                if (y >= h) return true;
            }
            return false;
        }

        public void step(long t_ms, long dt_ms, float t, float dt) {
            if (!mAlive) {
                // float away with the garbage
                setTranslationX(getTranslationX() - PARAMS.TRANSLATION_PER_SEC * dt);
                return;
            }

            if (mBoosting) {
                dv = -PARAMS.BOOST_DV;
            } else {
                dv += PARAMS.G;
            }
            if (dv < -PARAMS.MAX_V) dv = -PARAMS.MAX_V;
            else if (dv > PARAMS.MAX_V) dv = PARAMS.MAX_V;

            final float y = getTranslationY() + dv * dt;
            setTranslationY(y < 0 ? 0 : y);
            setRotation(90 + lerp(clamp(rlerp(dv, PARAMS.MAX_V, -1 * PARAMS.MAX_V)), 90, -90));

            prepareCheckIntersections();
        }

        void boost(float x, float y) {
            mTouchX = x;
            mTouchY = y;
            boost();
        }

        void boost() {
            mBoosting = true;
            dv = -PARAMS.BOOST_DV;

            animate().cancel();
            animate().scaleX(1.25f).scaleY(1.25f).translationZ(PARAMS.PLAYER_Z_BOOST).setDuration(100);
            setScaleX(1.25f);
            setScaleY(1.25f);
        }

        void unboost() {
            mBoosting = false;
            mTouchX = mTouchY = -1;

            animate().cancel();
            animate().scaleX(1f).scaleY(1f).translationZ(PARAMS.PLAYER_Z).setDuration(200);
        }

        void die() {
            // boolean mAlive = false;
            mAlive = false;
            if (mScoreField != null) {
                //mScoreField.setTextColor(0xFFFFFFFF);
                //mScoreField.getBackground().setColorFilter(0xFF666666, PorterDuff.Mode.SRC_ATOP);
                //mScoreField.setBackgroundResource(R.drawable.scorecard_gameover);
            }
        }

        void start() {
            mAlive = true;
        }
    }

    private static class Obstacle extends View implements GameView {
        final Rect hitRect = new Rect();
        final float h;

        Obstacle(Context context, float h) {
            super(context);
            setBackgroundColor(0xFFFF0000);
            this.h = h;
        }

        boolean intersects(Player p) {
            final int N = p.corners.length / 2;
            for (int i = 0; i < N; i++) {
                final int x = (int) p.corners[i * 2];
                final int y = (int) p.corners[i * 2 + 1];
                if (hitRect.contains(x, y)) return true;
            }
            return false;
        }

        boolean cleared(Player p) {
            final int N = p.corners.length / 2;
            for (int i = 0; i < N; i++) {
                final int x = (int) p.corners[i * 2];
                if (hitRect.right >= x) return false;
            }
            return true;
        }

        @Override
        public void step(long t_ms, long dt_ms, float t, float dt) {
            setTranslationX(getTranslationX() - PARAMS.TRANSLATION_PER_SEC * dt);
            getHitRect(hitRect);
        }
    }

    private static class Scenery extends FrameLayout implements GameView {
        float z;
        float v;
        int h;
        int w;

        public Scenery(Context context) {
            super(context);
        }

        @Override
        public void step(long t_ms, long dt_ms, float t, float dt) {
            setTranslationX(getTranslationX() - PARAMS.TRANSLATION_PER_SEC * dt * v);
        }
    }

    private class Pop extends Obstacle {
        // The marshmallow illustration and hitbox is 2/3 the size of its container.
        final Drawable antenna;
        int mRotate;
        int cx, cy, r;
        Drawable eyes;
        Drawable mouth;

        Pop(Context context, float h) {
            super(context, h);
            setBackgroundResource(R.drawable.mm_head);
            antenna = context.getDrawable(pick(ANTENNAE));
            if (frand() > 0.5f) {
                eyes = context.getDrawable(pick(EYES));
                if (frand() > 0.8f) {
                    mouth = context.getDrawable(pick(MOUTHS));
                }
            }
            setOutlineProvider(
                    new ViewOutlineProvider() {
                        @Override
                        public void getOutline(View view, Outline outline) {
                            final int pad = (int) (getWidth() * 1f / 6);
                            outline.setOval(pad, pad, getWidth() - pad, getHeight() - pad);
                        }
                    });
        }

        public boolean intersects(Player p) {
            final int N = p.corners.length / 2;
            for (int i = 0; i < N; i++) {
                final int x = (int) p.corners[i * 2];
                final int y = (int) p.corners[i * 2 + 1];
                if (Math.hypot(x - cx, y - cy) <= r) return true;
            }
            return false;
        }

        @Override
        public void step(long t_ms, long dt_ms, float t, float dt) {
            super.step(t_ms, dt_ms, t, dt);
            if (mRotate != 0) {
                setRotation(getRotation() + dt * 45 * mRotate);
            }

            cx = (hitRect.left + hitRect.right) / 2;
            cy = (hitRect.top + hitRect.bottom) / 2;
            r = getWidth() / 3; // see above re 2/3 container size
        }

        @Override
        public void onDraw(Canvas c) {
            super.onDraw(c);
            if (antenna != null) {
                antenna.setBounds(0, 0, c.getWidth(), c.getHeight());
                antenna.draw(c);
            }
            if (eyes != null) {
                eyes.setBounds(0, 0, c.getWidth(), c.getHeight());
                eyes.draw(c);
            }
            if (mouth != null) {
                mouth.setBounds(0, 0, c.getWidth(), c.getHeight());
                mouth.draw(c);
            }
        }
    }

    private class Stem extends Obstacle {
        final Paint mPaint = new Paint();
        final Path mShadow = new Path();
        final GradientDrawable mGradient = new GradientDrawable();
        final boolean mDrawShadow;
        final int id; // use this to track which pipes have been cleared
        Path mJandystripe;
        Paint mPaint2;

        Stem(Context context, float h, boolean drawShadow) {
            super(context, h);
            id = mCurrentPipeId;

            mDrawShadow = drawShadow;
            setBackground(null);
            mGradient.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
            mPaint.setColor(0xFF000000);
            mPaint.setColorFilter(new PorterDuffColorFilter(0x22000000, PorterDuff.Mode.MULTIPLY));

            if (frand() < 0.01f) {
                mGradient.setColors(new int[]{0xFFFFFFFF, 0xFFDDDDDD});
                mJandystripe = new Path();
                mPaint2 = new Paint();
                mPaint2.setColor(0xFFFF0000);
                mPaint2.setColorFilter(new PorterDuffColorFilter(0xFFFF0000, PorterDuff.Mode.MULTIPLY));
            } else {
                //mPaint.setColor(0xFFA1887F);
                mGradient.setColors(new int[]{0xFFBCAAA4, 0xFFA1887F});
            }
        }

        @Override
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            setWillNotDraw(false);
            setOutlineProvider(
                    new ViewOutlineProvider() {
                        @Override
                        public void getOutline(View view, Outline outline) {
                            outline.setRect(0, 0, getWidth(), getHeight());
                        }
                    });
        }

        @Override
        public void onDraw(Canvas c) {
            final int w = c.getWidth();
            final int h = c.getHeight();
            mGradient.setGradientCenter(w * 0.85f, 0);
            mGradient.setBounds(0, 0, w, h);
            mGradient.draw(c);

            if (mJandystripe != null) {
                mJandystripe.reset();
                mJandystripe.moveTo(0, w);
                mJandystripe.lineTo(w, 0);
                mJandystripe.lineTo(w, 4 * w);
                mJandystripe.lineTo(0, 3 * w);
                mJandystripe.close();
                for (int y = 0; y < h; y += 4 * w) {
                    c.drawPath(mJandystripe, mPaint2);
                    mJandystripe.offset(0, 4 * w);
                }
            }

            if (!mDrawShadow) return;
            mShadow.reset();
            mShadow.moveTo(0, 0);
            mShadow.lineTo(w, 0);
            mShadow.lineTo(w, PARAMS.OBSTACLE_WIDTH * 0.8f + w * 1.5f);
            mShadow.lineTo(0, PARAMS.OBSTACLE_WIDTH * 0.8f);
            mShadow.close();
            c.drawPath(mShadow, mPaint);
        }
    }

    private class Building extends Scenery {
        public Building(Context context) {
            super(context);

            w = irand(PARAMS.BUILDING_WIDTH_MIN, PARAMS.BUILDING_WIDTH_MAX);
            h = 0; // will be setup later, along with z
        }
    }

    private class Cactus extends Building {
        public Cactus(Context context) {
            super(context);

            setBackgroundResource(pick(CACTI));
            w = h = irand(PARAMS.BUILDING_WIDTH_MAX / 4, PARAMS.BUILDING_WIDTH_MAX / 2);
        }
    }

    private class Mountain extends Building {
        public Mountain(Context context) {
            super(context);

            setBackgroundResource(pick(MOUNTAINS));
            w = h = irand(PARAMS.BUILDING_WIDTH_MAX / 2, PARAMS.BUILDING_WIDTH_MAX);
            z = 8;
        }
    }

    private class Cloud extends Scenery {
        public Cloud(Context context) {
            super(context);
            setBackgroundResource(frand() < 0.01f ? R.drawable.cloud_off : R.drawable.cloud);
            getBackground().setAlpha(0x50);
            w = h = irand(PARAMS.CLOUD_SIZE_MIN, PARAMS.CLOUD_SIZE_MAX);
            z = 0;
            v = frand(0.25f, 0.5f);
        }
    }

    private class Star extends Scenery {
        public Star(Context context) {
            super(context);
            setBackgroundResource(R.drawable.star);
            w = h = irand(PARAMS.STAR_SIZE_MIN, PARAMS.STAR_SIZE_MAX);
            v = z = 0;
        }
    }
}
