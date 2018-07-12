package com.mobile.transpotid.transpot;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.mobile.transpotid.transpot.Tab.SlidingTabLayout;

public class List extends AppCompatActivity {

    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

//        mViewPager = (ViewPager)findViewById(R.id.vp_tabs);
//        mViewPager.setAdapter(new MissionSlideAdapter(getSupportFragmentManager(),this));
//
//        mSlidingTabLayout = (SlidingTabLayout)findViewById(R.id.stl_tabs);
//        mSlidingTabLayout.setDistributeEvenly(true);
//        mSlidingTabLayout.setBackgroundColor(getResources().getColor(R.color.headerColor));
//        mSlidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.accentColor));
//        mSlidingTabLayout.setCustomTabView(R.layout.tab_view_mission, R.id.tv_tab);
//        mSlidingTabLayout.setViewPager(mViewPager);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
