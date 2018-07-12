package com.mobile.transpotid.transpot;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.mobile.transpotid.transpot.Fragment.FragmentDashboard;
import com.mobile.transpotid.transpot.Fragment.FragmentMissions;
import com.mobile.transpotid.transpot.Fragment.FragmentProfile;
import com.mobile.transpotid.transpot.Fragment.FragmentSetting;
import com.mobile.transpotid.transpot.Fragment.FragmentWallet;
import com.mobile.transpotid.transpot.Session.UserSessionManager;

import java.util.HashMap;

public class Dashboard extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private Toolbar toolbar;

    UserSessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        session = new UserSessionManager(getApplicationContext());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        fragmentManager = getSupportFragmentManager();

        if (session.checkLogin()){
            finish();
        }

        HashMap<String, String> user = session.getUserDetails();

        String email = user.get(UserSessionManager.KEY_EMAIL);

        TextView profile = (TextView) findViewById(R.id.txtHomeProfile);

        profile.setText(email);

//        if(savedInstanceState==null){
//            SelectedItem(2131558701);
//        }
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

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.dashboard, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        SelectedItem(id);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void SelectedItem(int position){
        int id = position;
        if (id == R.id.nav_dashboard) {
            toolbar.setTitle("Dashboard");
            FragmentDashboard fragmentDashboard = new FragmentDashboard();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_holder,fragmentDashboard);
            fragmentTransaction.commit();
        } else if (id == R.id.nav_mission) {
            toolbar.setTitle("Mission");
            FragmentMissions fragmentMissions = new FragmentMissions();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_holder,fragmentMissions);
            fragmentTransaction.commit();
        } else if (id == R.id.nav_profile) {
            toolbar.setTitle("Profile");
            FragmentProfile fragmentProfile = new FragmentProfile();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_holder,fragmentProfile);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_wallet) {
            toolbar.setTitle("Wallet");
            FragmentWallet fragmentWallet = new FragmentWallet();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_holder,fragmentWallet);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_setting) {
            toolbar.setTitle("Setting");
            FragmentSetting fragmentSetting = new FragmentSetting();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_holder,fragmentSetting);
            fragmentTransaction.commit();
        }
    }
}
