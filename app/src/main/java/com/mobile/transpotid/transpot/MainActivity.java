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

import com.mobile.transpotid.transpot.Session.UserSessionManager;

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

public class MainActivity extends AppCompatActivity {
    EditText Email, Password;
    Button btn_login;
    AlertDialog.Builder builder;
    private String strURL;
    String validate;
    private URL url;
    private HttpURLConnection connection;

    //User Session Manager Class
    UserSessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

//        #################String Connection URL####################
        strURL = getString(R.string.member);
//        ##########################################################

        //User Session Manager
        session = new UserSessionManager(getApplicationContext());

        findViewById(R.id.textView5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Register.class));
            }
        });
        findViewById(R.id.textView2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Forget.class));
            }
        });

        Email = (EditText) findViewById(R.id.txtEmail);
        Password = (EditText) findViewById(R.id.txtPass);
        btn_login = (Button) findViewById(R.id.btnLogin);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Email.getText().toString().equals("") | Password.getText().toString().equals("")) {
                    builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Something went wrong...");
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

                        builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("Something went wrong...");
                        builder.setMessage("No Network Connection Available..");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Password.setText("");
                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }


//                    LoginBackgroundTask loginBackgroundTask = new LoginBackgroundTask(MainActivity.this);
//                    loginBackgroundTask.execute("login", Email.getText().toString(), Password.getText().toString());
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
            loading = ProgressDialog.show(MainActivity.this,"Please Wait","Connecting to Server..",true,true);
        }

        @SuppressLint("NewApi")
        @Override
        protected void onPostExecute(StringBuffer result) {

            JSONArray jArray;
            String id,name,password,token;
            try {
                jArray = new JSONArray(result.toString());
                JSONObject jObject;

                for (int i=0;i<jArray.length();i++){
                    jObject = jArray.getJSONObject(i);
                    id = jObject.getString("id");
                    name = jObject.getString("email");
                    password = jObject.getString("password");
                    token = jObject.getString("token");

                    if (Email.getText().toString().equals(name) && Password.getText().toString().equals(password)) {
                        loading.dismiss();
                        session.createUserLoginSession(id,name,token);

                        Intent intent = new Intent(getApplicationContext(), Dashboard.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                        validate = "login";
                        break;
                    }else{
                        validate = "register";
                    }
                }

            } catch (JSONException e) {
                Toast.makeText(getBaseContext(), "No data Found", Toast.LENGTH_LONG).show();
            }
            if (validate=="register"){
                loading.dismiss();
                builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Something went wrong...");
                builder.setMessage("Your users and password invalid..");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        }
    }
}
