package com.artemzin.android.wail.ui.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;

import com.artemzin.android.wail.R;
import com.artemzin.android.wail.storage.WAILSettings;
import com.artemzin.android.wail.ui.TypefaceTextView;
import com.artemzin.android.wail.ui.fragment.main.MainFragment;
import com.artemzin.android.wail.ui.fragment.main.SettingsFragment;
import com.artemzin.android.wail.ui.fragment.main.TracksListFragment;
import com.artemzin.android.wail.util.Loggi;
import com.artemzin.android.wail.util.SleepIfRequiredAsyncTask;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import butterknife.Optional;

public class MainActivity extends BaseActivity {

    private static final int REQUEST_CODE_NON_AUTHORIZED_ACTIVITY_INTENT = 1;

    @InjectView(R.id.toolbar)
    public Toolbar toolbar;

    @Optional
    @InjectView(R.id.main_drawer_layout)
    public DrawerLayout drawerLayout;

    @Optional
    @InjectView(R.id.main_drawer_layout_land)
    public DrawerLayout drawerLayoutLand;

    @InjectView(R.id.main_left_drawer_list)
    public ListView drawerList;

    @InjectView(R.id.main_drawer)
    public FrameLayout drawer;

    private ActionBarDrawerToggle actionBarDrawerToggle;

    private Fragment[] navigationFragments = new Fragment[3];

    private int lastItemSelected = -1;

    @OnItemClick(R.id.main_left_drawer_list)
    public void onItemsSelected(int position) {
        selectNavDrawerItem(position);

        if (drawerLayout != null) {
            SleepIfRequiredAsyncTask.newInstance(SystemClock.elapsedRealtime(), 150, new Runnable() {
                @Override
                public void run() {
                    try {
                        drawerLayout.closeDrawers();
                    } catch (Exception e) {
                        Loggi.e("MainActivity closeNavigationDrawer() exception: " + e.getMessage());
                    }
                }
            }).execute();
        }

        setSelectedItem(position);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        setSupportActionBar(toolbar);

        if (!WAILSettings.isAuthorized(this)) {
            startActivityForResult(new Intent(this, NonAuthorizedActivity.class), REQUEST_CODE_NON_AUTHORIZED_ACTIVITY_INTENT);
        }

        ((TypefaceTextView) findViewById(R.id.main_left_drawer_title_main)).setText(WAILSettings.getLastfmUserName(this));
        ((TypefaceTextView) findViewById(R.id.main_left_drawer_title_secondary)).setText(
                getString(R.string.drawer_registered_at) + WAILSettings.getLastfmUserRegistered(this).split(" ")[0]
        );

        // in landscape orientation on big screen there wont be drawer layout
        if (drawerLayout != null) {
            drawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.primary_dark));

            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 56, displayMetrics);

            ViewGroup.LayoutParams params = drawer.getLayoutParams();
            params.width = displayMetrics.widthPixels - Math.round(px);
            drawer.setLayoutParams(params);

            actionBarDrawerToggle = new ActionBarDrawerToggle(
                    this,
                    drawerLayout,
                    R.string.app_name,
                    R.string.app_name
            );

            drawerLayout.setDrawerListener(actionBarDrawerToggle);

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        } else if (drawerLayoutLand != null) {
            drawerLayoutLand.setStatusBarBackgroundColor(getResources().getColor(R.color.primary_dark));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                R.layout.settings_ignored_players_item_layout,
                getResources().getStringArray(R.array.drawer_items)
        ) {
            @Override
            public View getView(int position, View view, ViewGroup parent) {
                ViewHolder holder;
                View rowView = view;

                if (rowView == null) {
                    LayoutInflater inflater = getLayoutInflater();
                    rowView = inflater.inflate(R.layout.activity_main_drawer_item_layout, null, true);
                    holder = new ViewHolder();
                    holder.background = rowView;
                    holder.textView = (TypefaceTextView) rowView.findViewById(R.id.activity_main_drawer_item_text);
                    holder.imageView = (ImageView) rowView.findViewById(R.id.activity_main_drawer_item_image);
                    rowView.setTag(holder);
                } else {
                    holder = (ViewHolder) rowView.getTag();
                }

                holder.textView.setText(getItem(position));

                switch (position) {
                    case 0:
                        holder.imageView.setImageResource(R.drawable.ic_home_grey600_24dp);
                        break;
                    case 1:
                        holder.imageView.setImageResource(R.drawable.ic_list_grey600_24dp);
                        break;
                    case 2:
                        holder.imageView.setImageResource(R.drawable.ic_settings_grey600_24dp);
                        break;
                }

                if (position == 0 && lastItemSelected == -1) {
                    holder.background.setBackgroundColor(getResources().getColor(R.color.drawer_item_selected_background));
                    lastItemSelected = 0;
                }

                return rowView;
            }

            class ViewHolder {
                View background;
                TypefaceTextView textView;
                ImageView imageView;
            }
        };

        drawerList.setAdapter(adapter);
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

    private void setSelectedItem(int position) {
        if (lastItemSelected != -1) {
            drawerList.getChildAt(lastItemSelected)
                    .setBackgroundColor(getResources().getColor(R.color.drawer_item_background));
        }

        drawerList.getChildAt(position)
                .setBackgroundColor(getResources().getColor(R.color.drawer_item_selected_background));

        lastItemSelected = position;
    }
}
