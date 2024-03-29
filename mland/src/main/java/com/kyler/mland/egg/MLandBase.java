package com.kyler.mland.egg;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.samples.apps.iosched.ui.widget.ScrimInsetsScrollView;
import com.kyler.mland.egg.activities.Home;
import com.kyler.mland.egg.activities.MockPlanePage;
import com.kyler.mland.egg.activities.MoreInfo;
import com.kyler.mland.egg.activities.PlanesTest;
import com.kyler.mland.egg.utils.LUtils;
import com.kyler.mland.egg.utils.UIUtils;

import java.util.ArrayList;
import java.util.Calendar;

public abstract class MLandBase extends AppCompatActivity {

    /*
    symbols for navdrawer items (indices must correspond to array below). This is
    not a list of items that are necessarily *present* in the Nav Drawer; rather,
    it's a list of all possible items.
    */
    protected static final int NAVDRAWER_ITEM_HOME = 0;
    protected static final int NAVDRAWER_ITEM_MLAND = 1;
    protected static final int NAVDRAWER_ITEM_MLANDMODIFIED = 2;
    protected static final int NAVDRAWER_ITEM_TANKS = 3;
    protected static final int NAVDRAWER_ITEM_ABOUT = 4;

    private static final int NAVDRAWER_ITEM_INVALID = -1;
    private static final int NAVDRAWER_ITEM_SEPARATOR = -2;
    private static final int NAVDRAWER_ITEM_SEPARATOR_SPECIAL = -3;

    /*
    fade in and fade out durations for the main content when switching between
    different Activities of the app through the Nav Drawer
    */
    private static final long MAIN_CONTENT_FADEOUT_DURATION = 150L;
    private static final long MAIN_CONTENT_FADEIN_DURATION = 400L;
    private static final long NAVDRAWER_CLOSE_PRELAUNCH = 300L;
    private static final long NAVDRAWER_LAUNCH_DELAY = 250L;
    private static final long POST_LAUNCH_FADE = 300L;

    // titles for navdrawer items (indices must correspond to the above)
    private static final int[] NAVDRAWER_TITLE_RES_ID =
            new int[]{R.string.real_home, R.string.home, R.string.mland_original, R.string.mland_modified, R.string.about};
    // icons for navdrawer items (indices must correspond to above array)
    private static final int[] NAVDRAWER_ICON_RES_ID =
            new int[]{
                    R.drawable.ic_home, R.drawable.temp_drawer_plane, R.drawable.temp_drawer_naval, R.drawable.temp_drawer_tank, 0
            };
    // list of navdrawer items that were actually added to the navdrawer, in order
    private final ArrayList<Integer> mNavDrawerItems = new ArrayList<>();
    // What nav drawer item should be selected?
    private final int selfItem = getSelfNavDrawerItem();
    private final int drawerColorCalendar = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
    // Primary toolbar and drawer toggle
    public Toolbar mActionBarToolbar;
    public Context context;
    private DrawerLayout mDrawerLayout;
    // A Runnable that we should execute when the navigation drawer finishes its closing animation
    private Runnable mDeferredOnDrawerClosedRunnable;
    private CharSequence mTitle;
    // views that correspond to each navdrawer item, null if not yet created
    private View[] mNavDrawerItemViews = null;
    // Helper methods for L APIs
    private LUtils mLUtils;
    private ActionBarDrawerToggle mDrawerToggle;
    private SharedPreferences.Editor editor;

    @SuppressLint("InlinedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //    RecentTasksStyler.styleRecentTasksEntry(this);

        SharedPreferences first = PreferenceManager.getDefaultSharedPreferences(this);

        if (getIntent().getBooleanExtra("EXIT", false)) {
            super.finish();
        }

        if (!first.getBoolean("firstTimeRan", false)) {

            new Handler().post(() -> {
            });

            SharedPreferences.Editor editor = first.edit();

            editor.putBoolean("firstTimeRan", true);
            editor.apply();
        }

        Handler mHandler = new Handler();

    /*
    Enable or disable each Activity depending on the form factor. This is necessary
    because this app uses many implicit intents where we don't name the exact Activity
    in the Intent, so there should only be one enabled Activity that handles each
    Intent in the app.
    */
        UIUtils.enableDisableActivitiesByFormFactor(this);

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Returns the navigation drawer item that corresponds to this Activity. Subclasses of
     * BaseActivity override this to indicate what nav drawer item corresponds to them Return
     * NAVDRAWER_ITEM_INVALID to mean that this Activity should not have a Nav Drawer.
     */
    protected int getSelfNavDrawerItem() {
        return NAVDRAWER_ITEM_INVALID;
    }

    /**
     * Sets up the navigation drawer as appropriate. Note that the nav drawer will be different
     * depending on whether the attendee indicated that they are attending the event on-site vs.
     * attending remotely.
     */
    @SuppressWarnings("UnusedAssignment")
    private void setupNavDrawer() {
        // What nav drawer item should be selected?
        int selfItem = getSelfNavDrawerItem();
        int toolbarHeight = 0;
        TypedValue tv = new TypedValue();

        toolbarHeight =
                (int)
                        (tv.getDimension(getResources().getDisplayMetrics())
                                / getResources().getDisplayMetrics().density);
        int drawerMaxWidth =
                (int)
                        (getResources().getDimension(R.dimen.nav_drawer_max_width)
                                / getResources().getDisplayMetrics().density);

        Configuration configuration = getResources().getConfiguration();
        int screenWidthDp = configuration.screenWidthDp;
        int drawerWidth = screenWidthDp - toolbarHeight;

        mDrawerLayout = findViewById(R.id.drawer_layout);
        if (mDrawerLayout == null) {
            return;
        }
        boolean isDarkThemeOn =
                (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)
                        == Configuration.UI_MODE_NIGHT_YES;

        mDrawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.transparent));
        ScrimInsetsScrollView navDrawer = mDrawerLayout.findViewById(R.id.navdrawer);

        ViewGroup.LayoutParams layout_description = navDrawer.getLayoutParams();
        layout_description.width =
                (int)
                        TypedValue.applyDimension(
                                TypedValue.COMPLEX_UNIT_DIP,
                                drawerWidth < drawerMaxWidth ? drawerWidth : drawerMaxWidth,
                                getResources().getDisplayMetrics());
        navDrawer.setLayoutParams(layout_description);

        if (selfItem == NAVDRAWER_ITEM_INVALID) {
            // do not show a nav drawer
            ((ViewGroup) navDrawer.getParent()).removeView(navDrawer);
            mDrawerLayout = null;
            return;
        }

        if (mActionBarToolbar != null) {
            mActionBarToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_drawer));
            mActionBarToolbar.setNavigationOnClickListener(
                    view -> mDrawerLayout.openDrawer(GravityCompat.START));
            if (selfItem == NAVDRAWER_ITEM_ABOUT) {
                mActionBarToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_drawer_white));
            }
            if (selfItem == NAVDRAWER_ITEM_HOME) {
                mActionBarToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_drawer_white));
            }
        }

        //noinspection deprecation
        mDrawerLayout.setDrawerListener(
                new DrawerLayout.DrawerListener() {
                    @Override
                    public void onDrawerClosed(@NonNull View drawerView) {
                        // run deferred action, if we have one
                        if (mDeferredOnDrawerClosedRunnable != null) {
                            mDeferredOnDrawerClosedRunnable.run();
                            mDeferredOnDrawerClosedRunnable = null;
                        }
                        onNavDrawerStateChanged(false, false);
                    }

                    @Override
                    public void onDrawerOpened(@NonNull View drawerView) {
                        onNavDrawerStateChanged(true, false);
                    }

                    @Override
                    public void onDrawerStateChanged(int newState) {
                        onNavDrawerStateChanged(isNavDrawerOpen(), newState != DrawerLayout.STATE_IDLE);
                    }

                    @Override
                    public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                        onNavDrawerSlide(slideOffset);
                    }
                });

        // populate the nav drawer with the correct items
        populateNavDrawer();
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        getActionBarToolbar();
    }

    // Subclasses can override this for custom behavior
    private void onNavDrawerStateChanged(boolean isOpen, boolean isAnimating) {
    }

    private void onNavDrawerSlide(float offset) {
    }

    private boolean isNavDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START);
    }

    private void closeNavDrawer() {
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    /**
     * Populates the navigation drawer with the appropriate items.
     */
    private void populateNavDrawer() {
        mNavDrawerItems.clear();
        mNavDrawerItems.add(NAVDRAWER_ITEM_HOME);

        mNavDrawerItems.add(NAVDRAWER_ITEM_MLAND);
        mNavDrawerItems.add(NAVDRAWER_ITEM_MLANDMODIFIED);
        mNavDrawerItems.add(NAVDRAWER_ITEM_TANKS);

        mNavDrawerItems.add(NAVDRAWER_ITEM_SEPARATOR);

        mNavDrawerItems.add(NAVDRAWER_ITEM_ABOUT);

        createNavDrawerItems();
    }

    @Override
    public void onBackPressed() {
        if (isNavDrawerOpen()) {
            closeNavDrawer();
        } else {
            super.onBackPressed();
        }
    }

    private void createNavDrawerItems() {
        ViewGroup mDrawerItemsListContainer = findViewById(R.id.navdrawer_items_list);
        if (mDrawerItemsListContainer == null) {
            return;
        }

        mNavDrawerItemViews = new View[mNavDrawerItems.size()];
        mDrawerItemsListContainer.removeAllViews();
        int i = 0;
        for (int itemId : mNavDrawerItems) {
            mNavDrawerItemViews[i] = makeNavDrawerItem(itemId, mDrawerItemsListContainer);
            mDrawerItemsListContainer.addView(mNavDrawerItemViews[i]);
            ++i;
        }
    }

    /**
     * Sets up the given navdrawer item's appearance to the selected state. Note: this could also be
     * accomplished (perhaps more cleanly) with state-based layouts.
     */
    private void setSelectedNavDrawerItem(int itemId) {
        if (mNavDrawerItemViews != null) {
            for (int i = 0; i < mNavDrawerItemViews.length; i++) {
                if (i < mNavDrawerItems.size()) {
                    int thisItemId = mNavDrawerItems.get(i);
                    formatNavDrawerItem(mNavDrawerItemViews[i], thisItemId, itemId == thisItemId);
                }
            }
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setupNavDrawer();

        View mainContent = findViewById(R.id.main_content);
        //noinspection StatementWithEmptyBody
        if (mainContent != null) {
            mainContent.setAlpha(0);
            mainContent.animate().alpha(1).setDuration(MAIN_CONTENT_FADEIN_DURATION);
        } else {
            //  (~˘▾˘)~
        }
    }

    private void goToNavDrawerItem(int item) {
        Intent intent;
        switch (item) {
            case NAVDRAWER_ITEM_HOME:
                intent = new Intent(this, Home.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                break;
            case NAVDRAWER_ITEM_MLAND:
                intent = new Intent(this, PlanesTest.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                break;
            case NAVDRAWER_ITEM_MLANDMODIFIED:
                intent = new Intent(this, MockPlanePage.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                break;
            case NAVDRAWER_ITEM_ABOUT:
                intent = new Intent(this, MoreInfo.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                break;
        }
    }

    private void onNavDrawerItemClicked(final int itemId) {
        if (itemId == getSelfNavDrawerItem()) {
            new Handler()
                    .postDelayed(
                            () -> mDrawerLayout.closeDrawer(GravityCompat.START), NAVDRAWER_CLOSE_PRELAUNCH);
            return;
        }

        if (isSpecialItem(itemId)) {
            goToNavDrawerItem(itemId);
        } else {

            mDrawerLayout.closeDrawer(GravityCompat.START);

            new Handler()
                    .postDelayed(
                            () -> {
                                // change the active item on the list so the user can see the item
                                // changed
                                setSelectedNavDrawerItem(itemId);

                                goToNavDrawerItem(itemId);
                            },
                            NAVDRAWER_LAUNCH_DELAY);

            new Handler()
                    .postDelayed(
                            () -> {
                                // fade out the main content
                                View mainContent = findViewById(R.id.main_content);
                                if (mainContent != null) {
                                    mainContent.animate().alpha(0).setDuration(MAIN_CONTENT_FADEOUT_DURATION);
                                }
                            },
                            POST_LAUNCH_FADE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private Toolbar getActionBarToolbar() {
        if (mActionBarToolbar == null) {
            mActionBarToolbar = findViewById(R.id.toolbar);
            if (mActionBarToolbar != null) {
                setSupportActionBar(mActionBarToolbar);
            }
        }
        return mActionBarToolbar;
    }

    @SuppressWarnings("UnusedAssignment")
    private View makeNavDrawerItem(final int itemId, ViewGroup container) {
        boolean selected = getSelfNavDrawerItem() == itemId;
        int layoutToInflate = 0;
        if (itemId == NAVDRAWER_ITEM_SEPARATOR) {
            layoutToInflate = R.layout.navdrawer_separator;
        } else if (itemId == NAVDRAWER_ITEM_SEPARATOR_SPECIAL) {
            layoutToInflate = R.layout.navdrawer_separator;
        } else {
            layoutToInflate = R.layout.navdrawer_item;
        }

        View view = getLayoutInflater().inflate(layoutToInflate, container, false);

        if (isSeparator(itemId)) {

            return view;
        }

        ImageView iconView = view.findViewById(R.id.icon);
        TextView titleView = view.findViewById(R.id.title);

        int iconId =
                itemId >= 0 && itemId < NAVDRAWER_ICON_RES_ID.length ? NAVDRAWER_ICON_RES_ID[itemId] : 0;
        int titleId =
                itemId >= 0 && itemId < NAVDRAWER_TITLE_RES_ID.length ? NAVDRAWER_TITLE_RES_ID[itemId] : 0;

        // set icon and text
        iconView.setVisibility(iconId > 0 ? View.VISIBLE : View.GONE);
        if (iconId > 0) {
            iconView.setImageResource(iconId);
        }
        titleView.setText(getString(titleId));

        formatNavDrawerItem(view, itemId, selected);

        view.setOnClickListener(v -> onNavDrawerItemClicked(itemId));

        return view;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private boolean isSeparator(int itemId) {
        return itemId == NAVDRAWER_ITEM_SEPARATOR || itemId == NAVDRAWER_ITEM_SEPARATOR_SPECIAL;
    }

    private void formatNavDrawerItem(View view, int itemId, boolean selected) {
        if (isSeparator(itemId)) {
            // not applicable
            return;
        }

        ImageView iconView = view.findViewById(R.id.icon);
        TextView titleView = view.findViewById(R.id.title);
        LinearLayout ll = view.findViewById(R.id.ll);

        if (selected) {
            view.setBackgroundResource(R.drawable.ll_ripple);
        }

        // configure its appearance according to whether or not it's selected
        titleView.setTextColor(
                getApplicationContext().getColor(R.color.navdrawer_item_text_color));
        iconView.setColorFilter(getApplicationContext().getColor(R.color.navdrawer_item_icon_color));
    }

    public LUtils getLUtils() {
        return mLUtils;
    }

    private boolean isSpecialItem(int itemId) {
        return itemId == NAVDRAWER_ITEM_INVALID;
    }

    protected abstract Context getContext();
}
