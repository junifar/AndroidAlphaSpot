package com.mobile.transpotid.transpot.Adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;

import com.mobile.transpotid.transpot.Fragment.FragmentMission;
import com.mobile.transpotid.transpot.Fragment.FragmentMyMission;
import com.mobile.transpotid.transpot.R;

/**
 * Created by prasetia on 2/12/2016.
 */
public class MissionSlideAdapter extends FragmentPagerAdapter {
    private Context mContext;
    private String[] titles = {"Mission", "My Mission"};
    int[] icons = new int[]{R.mipmap.mission, R.mipmap.mymission};
    private int heightIcon;

    public MissionSlideAdapter(FragmentManager fm, Context c) {
        super(fm);

        mContext = c;
        double scale = c.getResources().getDisplayMetrics().density;
        heightIcon = (int)(24*scale+0.5f);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment frag = null;

        if (position == 0){
            frag = new FragmentMission();
        }else if(position == 1){
            frag = new FragmentMyMission();
        }

        Bundle b = new Bundle();
        b.putInt("position", position);

        frag.setArguments(b);
        return frag;
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Drawable d = mContext.getResources().getDrawable(icons[position]);
        d.setBounds(0,0,heightIcon,heightIcon);

        ImageSpan is = new ImageSpan(d);

        SpannableString sp = new SpannableString("   "+titles[position]);
        sp.setSpan(is,0,1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return(sp);
    }
}
