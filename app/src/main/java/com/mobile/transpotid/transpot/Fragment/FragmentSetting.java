package com.mobile.transpotid.transpot.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.mobile.transpotid.transpot.R;
import com.mobile.transpotid.transpot.Session.UserSessionManager;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentSetting extends Fragment {

    Button btn_logout;
    UserSessionManager sessionManager;

    public FragmentSetting() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.activity_setting, container, false);

        sessionManager = new UserSessionManager(getActivity().getApplicationContext());
        btn_logout = (Button)view.findViewById(R.id.btn_logout);

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionManager.logoutUser();
                getActivity().finish();
            }
        });

        return view;

//        return inflater.inflate(R.layout.activity_setting, container, false);
    }

}
