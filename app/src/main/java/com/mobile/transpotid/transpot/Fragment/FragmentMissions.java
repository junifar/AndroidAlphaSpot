package com.mobile.transpotid.transpot.Fragment;


import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobile.transpotid.transpot.Adapter.MissionSlideAdapter;
import com.mobile.transpotid.transpot.R;
import com.mobile.transpotid.transpot.Tab.SlidingTabLayout;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentMissions extends Fragment {


    private ViewPager mViewPager;
    private SlidingTabLayout mSlidingTabLayout;
    AlertDialog.Builder builder;

    public FragmentMissions() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_list, container, false);

        ConnectivityManager connMgr = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            mViewPager = (ViewPager)view.findViewById(R.id.vp_tabs);
            mViewPager.setAdapter(new MissionSlideAdapter(getChildFragmentManager(),view.getContext()));

            mSlidingTabLayout = (SlidingTabLayout)view.findViewById(R.id.stl_tabs);
            mSlidingTabLayout.setDistributeEvenly(true);
            mSlidingTabLayout.setBackgroundColor(getResources().getColor(R.color.headerColor));
            mSlidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.navTextNormal));
            mSlidingTabLayout.setCustomTabView(R.layout.tab_view_mission, R.id.tv_tab);
            mSlidingTabLayout.setViewPager(mViewPager);

        } else {

            builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Something went wrong...");
            builder.setMessage("No Network Connection Available..");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }


        return view;
//        return inflater.inflate(R.layout.activity_list, container, false);
    }

}
