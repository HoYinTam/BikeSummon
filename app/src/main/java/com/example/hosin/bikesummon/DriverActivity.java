package com.example.hosin.bikesummon;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.baidu.mapapi.SDKInitializer;

public class DriverActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
    private int userID;
    private String curFragment;
    private Fragment isFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_driver);
        toolbar = (Toolbar) findViewById(R.id.driver_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Home");
        curFragment="Home";
        isFragment=getFragmentManager().findFragmentById(R.id.driverHomeFragment);

        //Get UserID
        Intent intent = getIntent();
        userID = intent.getIntExtra("userID", 0);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(DriverActivity.this);
            builder.setTitle("Exit");
            builder.setMessage("Do you want to Exit?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //TODO:LOGOUT
                    finish();
                }
            });
            builder.setNegativeButton("Cancel", null);
            builder.show();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        Log.d("toolbar", curFragment);
        if (curFragment.equals("Profile")) {
            getMenuInflater().inflate(R.menu.profile_toobar, menu);
        } else if (curFragment.equals("Home")) {
            getMenuInflater().inflate(R.menu.driver, menu);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.driver, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            AlertDialog aboutDialog = new AlertDialog.Builder(DriverActivity.this).setTitle("About").setMessage("Engineer is working hard!").setNegativeButton("OK", null).create();
            aboutDialog.show();
            return true;
        } else if (id == R.id.action_finish) {
            //TODO:upload new profile

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_profile) {
            //TODO: add profile fragment
            toolbar.setTitle("Profile");
            invalidateOptionsMenu();

            if (!curFragment.equals("Profile")) {
                ProfileFragment fragment = new ProfileFragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(isFragment.getId(), fragment);
                isFragment = fragment;
                transaction.addToBackStack(null);
                transaction.commit();
            }
            curFragment = "Profile";

        } else if (id == R.id.nav_orders) {
            //TODO: history orders
            toolbar.setTitle("My orders");
            curFragment = "orders";
            invalidateOptionsMenu();
        } else if (id == R.id.action_settings) {
            //TODO:settings
            toolbar.setTitle("Settings");
            curFragment = "setting";
            invalidateOptionsMenu();
        } else if (id == R.id.nav_home) {
            toolbar.setTitle("Home");

            invalidateOptionsMenu();
            if (!curFragment.equals("Home")) {
                driverHomeFragment fragment = new driverHomeFragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(isFragment.getId(), fragment);
                isFragment = fragment;
                transaction.addToBackStack(null);
                transaction.commit();
            }
            curFragment = "Home";
        } else if (id == R.id.action_logout) {
            AlertDialog.Builder builder = new AlertDialog.Builder(DriverActivity.this);
            builder.setTitle("Logout");
            builder.setMessage("Do you want to logout?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //TODO:LOGOUT
                    Intent intent = new Intent(DriverActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
            builder.setNegativeButton("Cancel", null);
            builder.show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
