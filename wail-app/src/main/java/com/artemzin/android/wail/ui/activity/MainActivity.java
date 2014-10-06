package com.artemzin.android.wail.ui.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.ViewDragHelper;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.artemzin.android.wail.R;
import com.artemzin.android.wail.storage.WAILSettings;
import com.artemzin.android.wail.ui.TypefaceTextView;
import com.artemzin.android.wail.ui.fragment.main.MainFragment;
import com.artemzin.android.wail.ui.fragment.main.SettingsFragment;
import com.artemzin.android.wail.ui.fragment.main.TracksListFragment;
import com.artemzin.android.wail.util.Loggi;
import com.artemzin.android.wail.util.SleepIfRequiredAsyncTask;

import java.lang.reflect.Field;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;

public class MainActivity extends BaseActivity {

    private static final int REQUEST_CODE_NON_AUTHORIZED_ACTIVITY_INTENT = 1;

    @InjectView(R.id.main_drawer_layout)
    public DrawerLayout drawerLayout;

    @InjectView(R.id.main_left_drawer)
    public ListView drawerList;

    private ActionBarDrawerToggle actionBarDrawerToggle;

    private int lastItemSelected = -1;

    private Fragment[] navigationFragments = new Fragment[3];

    @OnItemClick(R.id.main_left_drawer)
    public void onItemsSelected(int position) {
        setSelectedDrawerItem(position);
        selectNavDrawerItem(position);

        SleepIfRequiredAsyncTask.newInstance(SystemClock.elapsedRealtime(), 150, new Runnable() {
            @Override
            public void run() {
                try {
                    closeNavigationDrawer();
                } catch (Exception e) {
                    Loggi.e("MainActivity closeNavigationDrawer() exception: " + e.getMessage());
                }
            }
        }).execute();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        if (!WAILSettings.isAuthorized(this)) {
            startActivityForResult(new Intent(this, NonAuthorizedActivity.class), REQUEST_CODE_NON_AUTHORIZED_ACTIVITY_INTENT);
        }

        // in landscape orientation on big screen there wont be drawer layout
        if (drawerLayout != null) {
            actionBarDrawerToggle = new ActionBarDrawerToggle(
                    this,
                    drawerLayout,
                    R.drawable.ic_drawer,
                    R.string.app_name,
                    R.string.app_name) {

                @Override
                public void onDrawerSlide(View drawerView, float slideOffset) {
                    if (lastItemSelected == -1) {
                        setSelectedDrawerItem(0);
                    }
                    super.onDrawerSlide(drawerView, slideOffset);
                }

                @Override
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                }

                @Override
                public void onDrawerClosed(View drawerView) {
                    super.onDrawerClosed(drawerView);
                }

                @Override
                public void onDrawerStateChanged(int newState) {
                    super.onDrawerStateChanged(newState);
                }
            };

            drawerLayout.setDrawerListener(actionBarDrawerToggle);

            drawerList.setAdapter(new ArrayAdapter<String>(
                    this,
                    R.layout.activity_main_drawer_item_layout,
                    getResources().getStringArray(R.array.drawer_items)
            ));

            getActionBar().setDisplayHomeAsUpEnabled(true);
            getActionBar().setHomeButtonEnabled(true);

            tryToIncreaseNavigationDrawerLeftSwipeZone(drawerLayout);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if (drawerLayout != null && actionBarDrawerToggle != null) {
            actionBarDrawerToggle.syncState();
        }

        navigationFragments[0] = new MainFragment();
        navigationFragments[1] = new TracksListFragment();
        navigationFragments[2] = new SettingsFragment();

        selectNavDrawerItem(0);

        if (WAILSettings.isFirstLaunch(this)) {
            WAILSettings.setIsFirstLaunch(this, false);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_NON_AUTHORIZED_ACTIVITY_INTENT) {
            if (resultCode == RESULT_CANCELED) {
                finish();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle != null && actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void selectNavDrawerItem(final int position) {
        final FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fragment_transaction_alpha_up, R.anim.fragment_transaction_alpha_down);
        fragmentTransaction.replace(R.id.main_content, navigationFragments[position]);
        fragmentTransaction.commit();
    }

    private void setSelectedDrawerItem(int position) {
        if (lastItemSelected != -1) {
            ((TypefaceTextView) drawerList.getChildAt(lastItemSelected))
                    .setTypefaceFromAssets("fonts/Roboto-Light.ttf");
        }
        ((TypefaceTextView) drawerList.getChildAt(position))
                .setTypefaceFromAssets("fonts/Roboto-Medium.ttf");
        lastItemSelected = position;
    }

    private void closeNavigationDrawer() {
        if (drawerLayout != null) {
            drawerLayout.closeDrawers();
        }
    }

    private void tryToIncreaseNavigationDrawerLeftSwipeZone(DrawerLayout drawerLayout) {
        try {
            Field mDragger = drawerLayout.getClass().getDeclaredField("mLeftDragger");

            mDragger.setAccessible(true);

            ViewDragHelper draggerObj = (ViewDragHelper) mDragger.get(drawerLayout);

            Field mEdgeSize = draggerObj.getClass().getDeclaredField("mEdgeSize");
            mEdgeSize.setAccessible(true);

            int edge = mEdgeSize.getInt(draggerObj);

            mEdgeSize.setInt(draggerObj, (int) (edge * 1.3)); // increasing drag zone * 1.3
        } catch (Exception e) {
            Loggi.w("MainActivity.tryToIncreaseNavigationDrawerLeftSwipeZone() exception: " + e);
        }
    }
}
