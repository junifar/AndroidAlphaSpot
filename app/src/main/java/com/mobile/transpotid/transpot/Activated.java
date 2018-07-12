package com.mobile.transpotid.transpot;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Activated extends AppCompatActivity {
    String Email;
    EditText code;
    Button btn_activate;
    AlertDialog.Builder builder;
    private String strURL;
    private URL url;
    private HttpURLConnection connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activated);

//        #################String Connection URL####################
        strURL = getString(R.string.member);
//        ##########################################################

        Intent a = getIntent();
        //Getting attached intent data
        Email = a.getStringExtra("ID_USER");

        code = (EditText) findViewById(R.id.txt_Activated);
        btn_activate = (Button) findViewById(R.id.btn_Activated);

        btn_activate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (code.getText().toString().equals("")){
                    builder = new AlertDialog.Builder(Activated.this);
                    builder.setTitle("Something went wrong..");
                    builder.setMessage("Please fill all the fields..");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                } else {
                    ConnectivityManager connMgr = (ConnectivityManager)
                            getSystemService(Context.CONNECTIVITY_SERVICE);

                    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                    if (networkInfo != null && networkInfo.isConnected()) {
                        new getDataTask().execute(strURL);

                    } else {
                        // Show a toast if the user clicks on an item

                        builder = new AlertDialog.Builder(Activated.this);
                        builder.setTitle("Something went wrong...");
                        builder.setMessage("No Network Connection Available..");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                code.setText("");
                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                }
            }
        });
    }

    private StringBuffer getJSONData(String URLLink) {
        BufferedReader reader = null;
        StringBuffer buffer = null;
        StringBuilder sb = null;
        try {
            url = new URL(URLLink);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            InputStream stream = connection.getInputStream();

            reader = new BufferedReader(new InputStreamReader(stream));

            buffer = new StringBuffer();
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return buffer;
    }

    private class getDataTask extends AsyncTask<String, Void, StringBuffer> {
        ProgressDialog loading;

        @Override
        protected StringBuffer doInBackground(String... urls) {
            return getJSONData(urls[0]);

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(Activated.this,"Please Wait","Connecting to Server..",true,true);
        }

        @SuppressLint("NewApi")
        @Override
        protected void onPostExecute(StringBuffer result) {

            JSONArray jArray;
            String email,sms;

            try {
                jArray = new JSONArray(result.toString());
                JSONObject jObject;

                for (int i=0;i<jArray.length();i++){
                    jObject = jArray.getJSONObject(i);

                    email = jObject.getString("email");


                    if (Email.equals(email)) {
                        sms = jObject.getString("token");
                        if (code.getText().toString().equals(sms)){
                            loading.dismiss();
                            builder = new AlertDialog.Builder(Activated.this);
                            builder.setTitle("Registration Success");
                            builder.setMessage("Registration Success... Thank You......");
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    finish();
                                }
                            });
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        } else {
                            loading.dismiss();
                            builder = new AlertDialog.Builder(Activated.this);
                            builder.setTitle("Something went Wrong..");
                            builder.setMessage("The Verification Code not Matching..");
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        }
                        break;
                    }
                }

            } catch (JSONException e) {
                Toast.makeText(getBaseContext(), "No data Found", Toast.LENGTH_LONG).show();
            }
        }
    }
}
