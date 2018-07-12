package com.mobile.transpotid.transpot.Session;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.mobile.transpotid.transpot.MainActivity;

import java.util.HashMap;

/**
 * Created by andro on 29/04/2016.
 */
public class UserSessionManager {


    //Shared Preferences reference
    SharedPreferences pref;

    //Editor reference for shared preferences
    SharedPreferences.Editor editor;

    //Context
    Context _context;

    //Share pref mode
    int PRIVATE_MODE = 0;

    //Sharedpref file name
    private static final String PREFER_NAME = "AndroidExamplePref";

    //All Shared Preference Keys
    private static final String IS_USER_LOGIN = "IsUserLoggedIn";

    //Username (make variable public to access from outside)
    public static final String KEY_ID = "id";

    //Email address (make variable public to access from outside)
    public static final String KEY_EMAIL = "email";

    //Token (make variable public to access from outside)
    public static final String KEY_TOKEN = "token";

    //Constructor
    public UserSessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREFER_NAME,PRIVATE_MODE);
        editor = pref.edit();
    }

    //Create login session
    public void createUserLoginSession(String id, String email, String token){
        //Storing login value as TRUE
        editor.putBoolean(IS_USER_LOGIN, true);

        //Storing id in pref
        editor.putString(KEY_ID, id);

        //Storing email in pref
        editor.putString(KEY_EMAIL,email);

        //Storing token in pref
        editor.putString(KEY_TOKEN,token);

        //Commit Changes
        editor.commit();
    }

    /**
     * Check login method will check user login status
     * if false it will redirect user to login page
     * Else do anything
     */
    public boolean checkLogin(){
        //Check Login Status
        if(!this.isUserLoggedIn()){
            //User is not logged in redirect hom to Login Activity
            Intent i = new Intent(_context,MainActivity.class);

            //Closing all the Activities from stack
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            //Add new flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            //Staring login activity
            _context.startActivity(i);

            return true;
        }
        return false;
    }

    /**
     * Get Stored session data
     */
    public HashMap<String, String> getUserDetails(){
        //User Hashmap to store user credentials
        HashMap<String, String> user = new HashMap<String, String>();

        //Username
        user.put(KEY_ID, pref.getString(KEY_ID,null));

        //Token
        user.put(KEY_TOKEN, pref.getString(KEY_TOKEN,null));

        //Email id
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL,null));

        //return user
        return user;
    }

    public void logoutUser(){
        //clearing all user data from Shared Preferences
        editor.clear();
        editor.commit();

        //After logout redirect user to Login Activity
        Intent i = new Intent(_context, MainActivity.class);

        //Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        //Add new flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        //Staring Login Activity
        _context.startActivity(i);

        //
    }

    /**
     * Quick check for login
     * **/
    // Get Login State
    public boolean isUserLoggedIn(){
        return pref.getBoolean(IS_USER_LOGIN, false);
    }

}
