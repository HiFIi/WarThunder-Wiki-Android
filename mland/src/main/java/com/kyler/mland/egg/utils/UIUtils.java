/*
 * Copyright 2014 Google Inc. All rights reserved.
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

package com.kyler.mland.egg.utils;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.text.format.DateUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

/**
 * An assortment of UI helpers.
 */
public class UIUtils {
    public static final String GOOGLE_PLUS_PACKAGE_NAME = "com.google.android.apps.plus";
    public static final String YOUTUBE_PACKAGE_NAME = "com.google.android.youtube";
    public static final int ANIMATION_FADE_IN_TIME = 250;
    public static final String TRACK_ICONS_TAG = "tracks";
    /**
     * Factor applied to session color to derive the background color on panels and when a session
     * photo could not be downloaded (or while it is being downloaded)
     */
    private static final float SESSION_BG_COLOR_SCALE_FACTOR = 0.75f;
    private static final String TARGET_FORM_FACTOR_ACTIVITY_METADATA =
            "com.google.samples.apps.iosched.meta.TARGET_FORM_FACTOR";
    private static final String TARGET_FORM_FACTOR_HANDSET = "handset";
    private static final String TARGET_FORM_FACTOR_TABLET = "tablet";
    private static final float SESSION_PHOTO_SCRIM_ALPHA = 0.25f; // 0=invisible, 1=visible image
    private static final float SESSION_PHOTO_SCRIM_SATURATION = 0.2f; // 0=gray, 1=color image
    /**
     * Flags used with {@link android.text.format.DateUtils#formatDateRange}.
     */
    private static final int TIME_FLAGS =
            DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_ABBREV_WEEKDAY;
    /**
     * Regex to search for HTML escape sequences.
     *
     * <p>
     *
     * <p>
     *
     * <p>
     *
     * <p>
     *
     * <p>
     *
     * <p>
     *
     * <p>
     *
     * <p>Searches for any continuous string of characters starting with an ampersand and ending with
     * a semicolon. (Example: &amp;amp;)
     */
    private static final Pattern REGEX_HTML_ESCAPE = Pattern.compile(".*&\\S;.*");
    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat sDayOfWeekFormat = new SimpleDateFormat("E");
    private static final DateFormat sShortTimeFormat = DateFormat.getTimeInstance(DateFormat.SHORT);
    private static CharSequence sNowPlayingText;
    private static CharSequence sLivestreamNowText;
    private static CharSequence sLivestreamAvailableText;
    public Context context;
    long currentTimeMillis = UIUtils.getCurrentTime(null);

    /**
     * Enables and disables {@linkplain android.app.Activity activities} based on their {@link
     * #TARGET_FORM_FACTOR_ACTIVITY_METADATA}" meta-data and the current device. Values should be
     * either "handset", "tablet", or not present (meaning universal).
     *
     * <p>
     *
     * <p>
     *
     * <p>
     *
     * <p><a href="http://stackoverflow.com/questions/13202805">Original code</a> by Dandre Allison.
     *
     * @param context the current context of the device
     */
    public static void enableDisableActivitiesByFormFactor(Context context) {
        final PackageManager pm = context.getPackageManager();
        boolean isTablet = false;

        try {
            PackageInfo pi =
                    pm.getPackageInfo(
                            context.getPackageName(),
                            PackageManager.GET_ACTIVITIES | PackageManager.GET_META_DATA);
            if (pi == null) {
                return;
            }

            final ActivityInfo[] activityInfos = pi.activities;
            for (ActivityInfo info : activityInfos) {
                String targetDevice = null;
                if (info.metaData != null) {
                    targetDevice = info.metaData.getString(TARGET_FORM_FACTOR_ACTIVITY_METADATA);
                }
                boolean tabletActivity = TARGET_FORM_FACTOR_TABLET.equals(targetDevice);
                boolean handsetActivity = TARGET_FORM_FACTOR_HANDSET.equals(targetDevice);

                boolean enable = !tabletActivity;

                String className = info.name;
                pm.setComponentEnabledSetting(
                        new ComponentName(context, Class.forName(className)),
                        enable
                                ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                                : PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                        PackageManager.DONT_KILL_APP);
            }
        } catch (PackageManager.NameNotFoundException | ClassNotFoundException ignored) {
        }
    }

    public static int setColorOpaque(int color) {
        return Color.argb(255, Color.red(color), Color.green(color), Color.blue(color));
    }

    private static int scaleColor(int color, float factor, boolean scaleAlpha) {
        return Color.argb(
                scaleAlpha ? (Math.round(Color.alpha(color) * factor)) : Color.alpha(color),
                Math.round(Color.red(color) * factor),
                Math.round(Color.green(color) * factor),
                Math.round(Color.blue(color) * factor));
    }

    public static int scaleSessionColorToDefaultBG(int color) {
        return scaleColor(color, SESSION_BG_COLOR_SCALE_FACTOR, false);
    }

    private static long getCurrentTime(final Context context) {
    /*    if (BuildConfig.DEBUG) {
          return context.getSharedPreferences("mock_data", Context.MODE_PRIVATE)
    .getLong("mock_current_time", System.currentTimeMillis())
    + System.currentTimeMillis();
    //            return ParserUtils.parseTime("2012-06-27T09:44:45.000-07:00")
    //                    + System.currentTimeMillis() - sAppLoadTime;
    } else { */
        return System.currentTimeMillis();
    }
    //  }

    // Desaturates and color-scrims the image
    public static ColorFilter makeSessionImageScrimColorFilter(int sessionColor) {
        float a = SESSION_PHOTO_SCRIM_ALPHA;
        float sat = SESSION_PHOTO_SCRIM_SATURATION; // saturation (0=gray, 1=color)
        return new ColorMatrixColorFilter(
                new float[]{
                        ((1 - 0.213f) * sat + 0.213f) * a,
                        ((0 - 0.715f) * sat + 0.715f) * a,
                        ((0 - 0.072f) * sat + 0.072f) * a,
                        0,
                        Color.red(sessionColor) * (1 - a),
                        ((0 - 0.213f) * sat + 0.213f) * a,
                        ((1 - 0.715f) * sat + 0.715f) * a,
                        ((0 - 0.072f) * sat + 0.072f) * a,
                        0,
                        Color.green(sessionColor) * (1 - a),
                        ((0 - 0.213f) * sat + 0.213f) * a,
                        ((0 - 0.715f) * sat + 0.715f) * a,
                        ((1 - 0.072f) * sat + 0.072f) * a,
                        0,
                        Color.blue(sessionColor) * (1 - a),
                        0,
                        0,
                        0,
                        0,
                        255
                });
    }

    public static float getProgress(int value, int min, int max) {
        if (min == max) {
            throw new IllegalArgumentException("Max (" + max + ") cannot equal min (" + min + ")");
        }

        return (value - min) / (float) (max - min);
    }

    /**
     * Create a color integer value with specified alpha.
     *
     * <p>
     *
     * <p>
     *
     * <p>
     *
     * <p>This may be useful to change alpha value of background color.
     *
     * @param alpha     Alpha value from 0.0f to 1.0f.
     * @param baseColor Base color. alpha value will be ignored.
     * @return A color with alpha made from base color.
     */
    public static int getColorWithAlpha(float alpha, int baseColor) {
        int a = Math.min(255, Math.max(0, (int) (alpha * 255))) << 24;
        int rgb = 0x00ffffff & baseColor;
        return a + rgb;
    }
}
