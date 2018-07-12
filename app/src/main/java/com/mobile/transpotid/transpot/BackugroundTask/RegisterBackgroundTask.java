package com.mobile.transpotid.transpot.BackugroundTask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;

import com.mobile.transpotid.transpot.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Random;

/**
 * Created by andro on 21/03/2016.
 */
public class RegisterBackgroundTask extends AsyncTask<String,Void,String> {
    String registerURL;
    Context ctx;
    ProgressDialog progressDialog;
    Activity activity;
    AlertDialog.Builder builder;
    String user = null;
    String phone, token;

    public RegisterBackgroundTask(Context ctx) {
        this.ctx = ctx;
        activity = (Activity)ctx;
    }

    @Override
    protected void onPreExecute() {
        builder = new AlertDialog.Builder(activity);
        progressDialog = new ProgressDialog(ctx);
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Process Save..");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    protected String doInBackground(String... params) {
        String method = params[0];

//        #################String Connection URL####################
        registerURL = ctx.getResources().getString(R.string.member);
//        ##########################################################

        if(method.equals("register")){
            String name = params[1];
            String email = params[2];
            String handphone = params[3];
            String username = "-";
            String pass = params[4];
            Random random = new Random();
            token = String.valueOf(random.nextInt(999999) + 100000);
            user = email;
            phone = handphone;
            try {

                URL url = new URL(registerURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
                String data =   URLEncoder.encode("name","UTF-8")+"="+URLEncoder.encode(name,"UTF-8")+"&"+
                                URLEncoder.encode("token","UTF-8")+"="+URLEncoder.encode(token,"UTF-8")+"&"+
                                URLEncoder.encode("email","UTF-8")+"="+URLEncoder.encode(email,"UTF-8")+"&"+
                                URLEncoder.encode("handphone","UTF-8")+"="+URLEncoder.encode(handphone,"UTF-8")+"&"+
                                URLEncoder.encode("username","UTF-8")+"="+URLEncoder.encode(username,"UTF-8")+"&"+
                                URLEncoder.encode("password","UTF-8")+"="+URLEncoder.encode(pass,"UTF-8");
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                String line = "";
                while((line=bufferedReader.readLine())!=null){
                    stringBuilder.append(line+"\n");
                }
                httpURLConnection.disconnect();
                return stringBuilder.toString().trim();

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
    protected void onPostExecute(String json) {
        progressDialog.dismiss();

//        ###########################################################################################
//        SmsManager smsManager = SmsManager.getDefault();
//        smsManager.sendTextMessage(phone,null,"Your Transpot verification code is "+token,null,null);
//        ###########################################################################################

//        Intent intent = new Intent(activity.getApplicationContext(), Activated.class);
//        intent.putExtra("ID_USER", user);
//        activity.startActivity(intent);
//        activity.finish();
        showDialog("Registration Success","Registration Success... Thank You......");
    }

    public void showDialog(String title, String message){
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                activity.finish();

            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
