package com.osminin.sensorpuckdemo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.crash.FirebaseCrash;
import com.osminin.sensorpuckdemo.App;
import com.osminin.sensorpuckdemo.R;
import com.osminin.sensorpuckdemo.ui.base.BaseFragment;
import com.osminin.sensorpuckdemo.ui.fragments.SPListFragment;
import com.osminin.sensorpuckdemo.ui.views.SPListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.osminin.sensorpuckdemo.Constants.REQUEST_ENABLE_BT;
import static com.osminin.sensorpuckdemo.Constants.SETTINGS_REQUEST_CODE;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, FragmentManager.OnBackStackChangedListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private SPListView mHomeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseCrash.logcat(Log.DEBUG, TAG, "onCreate");
        App.getAppComponent(this).inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);

        DrawerLayout drawer = ButterKnife.findById(this, R.id.drawer_layout);
        mActionBarDrawerToggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(mActionBarDrawerToggle);
        mActionBarDrawerToggle.setToolbarNavigationClickListener(view -> MainActivity.this.onBackPressed());
        setBurgerButtonState();

        NavigationView navigationView = ButterKnife.findById(this, R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        getSupportFragmentManager().addOnBackStackChangedListener(this);
        showHomeScreen();
    }

    private void showHomeScreen() {
        FirebaseCrash.logcat(Log.DEBUG, TAG, "showHomeScreen");
        getSupportFragmentManager().popBackStack();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        SPListFragment spListFragment = new SPListFragment();
        transaction.replace(R.id.content_main, spListFragment);
        transaction.commit();
        mHomeView = spListFragment;
    }

    @Override
    public void onBackPressed() {
        FirebaseCrash.logcat(Log.DEBUG, TAG, "onBackPressed");
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        FirebaseCrash.logcat(Log.DEBUG, TAG, "onNavigationItemSelected " + item.getTitle());
        int id = item.getItemId();

        if (id == R.id.nav_settings) {
            getSupportFragmentManager().popBackStack();
            mHomeView.showSettingsFragment();
        } else if (id == R.id.nav_about) {
            //TODO: about screen
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackStackChanged() {
        FirebaseCrash.logcat(Log.DEBUG, TAG, "onBackStackChanged");
        FragmentManager fragmentManager = getSupportFragmentManager();
        List<Fragment> fragments = new ArrayList<>();
        for (Fragment fragment : fragmentManager.getFragments()) {
            if (fragment != null) {
                fragments.add(fragment);
            }
        }
        int size = fragments.size();
        BaseFragment lastFragment = (BaseFragment) fragments.get(size - 1);
        if (lastFragment != null) {
            mToolbar.setTitle(lastFragment.getTitle());
        } else {
            String title = getString(R.string.app_name);
            mToolbar.setTitle(title);
        }
        setBurgerButtonState();
    }

    public void restartConfiguration() {
        App.clearAppComponent(this);
        App.getAppComponent(this).inject(this);
        showHomeScreen();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        FirebaseCrash.logcat(Log.DEBUG, TAG, "onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SETTINGS_REQUEST_CODE && resultCode == RESULT_OK) {
            restartActivity();
        }
    }

    private void setBurgerButtonState() {
        FirebaseCrash.logcat(Log.DEBUG, TAG, "setBurgerButtonState");
        int count = getSupportFragmentManager().getBackStackEntryCount();
        mActionBarDrawerToggle.setDrawerIndicatorEnabled(count == 0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(count > 0);
        mActionBarDrawerToggle.syncState();
    }

    private void restartActivity() {
        FirebaseCrash.logcat(Log.DEBUG, TAG, "restartActivity");
        App.clearAppComponent(this);
        Handler handler = new Handler();
        //let activity be resumed before recreating
        handler.post(() -> recreate());
    }
}
