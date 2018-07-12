package com.mobile.transpotid.transpot.BackugroundTask;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

/**
 * Created by andro on 21/03/2016.
 */
public class LoginBackgroundTask extends AsyncTask<String,Void,String>{

    Context ctx;
    Activity activity;

    public LoginBackgroundTask(Context ctx){
        this.ctx = ctx;
        activity = (Activity)ctx;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        
        return null;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String aVoid) {
        super.onPostExecute(aVoid);
    }

}
