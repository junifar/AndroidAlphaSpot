package com.mobile.transpotid.transpot.BackugroundTask;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.mobile.transpotid.transpot.R;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by andro on 24/02/2016.
 */
public class BackgroundTask extends AsyncTask<String,Void,String> {
    Context ctx;
    public BackgroundTask(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {

//        #################String Connection URL####################
        String reg_url = ctx.getResources().getString(R.string.mymission);
//        ##########################################################

        String method = params[0];
        if (method.equals("joint")){
            String id_mission = params[1];
            String id_member = params[2];
            String member_status = params[3];

            try {
                URL url = new URL(reg_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream OS = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));
                String data = URLEncoder.encode("mission","UTF-8") +"=" + URLEncoder.encode(id_mission,"UTF-8") + "&" +
                        URLEncoder.encode("member","UTF-8") +"=" + URLEncoder.encode(id_member,"UTF-8") + "&" +
                        URLEncoder.encode("mission_status","UTF-8") +"=" + URLEncoder.encode(member_status,"UTF-8");

                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                OS.close();
                InputStream IS = httpURLConnection.getInputStream();
                IS.close();
                return "Registration Success...";
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String result) {
        Toast.makeText(ctx,result,Toast.LENGTH_LONG).show();
    }

}
