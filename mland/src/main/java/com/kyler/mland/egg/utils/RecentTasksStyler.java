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

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import com.kyler.mland.egg.R;

/**
 * Helper class that applies the proper icon, title and background color to recent tasks list.
 */
class RecentTasksStyler {
    private static Bitmap sIcon = null;

    private RecentTasksStyler() {
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void styleRecentTasksEntry(Activity activity) {

        Resources resources = activity.getResources();
        String label = resources.getString(activity.getApplicationInfo().labelRes);
        //noinspection deprecation
        @SuppressWarnings("deprecation") final int colorPrimaryDark = resources.getColor(R.color.colorPrimary);

        if (sIcon == null) {
            // Cache to avoid decoding the same bitmap on every Activity change
            //   sIcon = BitmapFactory.decodeResource(resources, R.drawable.tbmd_recents);
            sIcon = BitmapFactory.decodeResource(resources, android.R.color.transparent);
        }

        activity.setTaskDescription(
                new ActivityManager.TaskDescription(label, sIcon, colorPrimaryDark));
    }
}
