package com.osminin.sensorpuckdemo.ui;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.osminin.sensorpuckdemo.App;
import com.osminin.sensorpuckdemo.R;
import com.osminin.sensorpuckdemo.ui.base.BaseFragment;
import com.osminin.sensorpuckdemo.ui.fragments.SPListFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, FragmentManager.OnBackStackChangedListener {

    private ActionBarDrawerToggle mActionBarDrawerToggle;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        App.getAppComponent(this).inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);

        DrawerLayout drawer = ButterKnife.findById(this, R.id.drawer_layout);
        mActionBarDrawerToggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(mActionBarDrawerToggle);
        mActionBarDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.onBackPressed();
            }
        });
        setBurgerButtonState();

        NavigationView navigationView = ButterKnife.findById(this, R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        getSupportFragmentManager().addOnBackStackChangedListener(this);
        if (savedInstanceState == null) {
            BaseFragment spListFragment = new SPListFragment();
            showHomeScreen(spListFragment);
        }
    }

    public void showHomeScreen(BaseFragment fragment) {
        getSupportFragmentManager().popBackStack();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.content_main, fragment);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_settings) {
            showSettingsFragment();
        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackStackChanged() {
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

    private void setBurgerButtonState() {
        int count = getSupportFragmentManager().getBackStackEntryCount();
        mActionBarDrawerToggle.setDrawerIndicatorEnabled(count == 0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(count > 0);
        mActionBarDrawerToggle.syncState();
    }

    private void showSettingsFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        SettingsFragment settingsFragment = new SettingsFragment();
        transaction.add(R.id.content_main, settingsFragment);
        transaction.addToBackStack("settings");
        transaction.commit();
    }
}
