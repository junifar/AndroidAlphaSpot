package com.mobile.transpotid.transpot;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import com.mobile.transpotid.transpot.BackugroundTask.RegisterBackgroundTask;

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

public class Register extends AppCompatActivity {
    EditText Name, Email, Handphone, Password, PassConfirm;
    Button btn_reg;
    AlertDialog.Builder builder;
    private String strURL;
    String validate;
    private URL url;
    private HttpURLConnection connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

//        #################String Connection URL####################
        strURL = getString(R.string.member);
//        ##########################################################

        findViewById(R.id.textView34).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Name = (EditText) findViewById(R.id.txtName);
        Email = (EditText) findViewById(R.id.txtEmail);
        Handphone = (EditText) findViewById(R.id.txtPhone);
        Password = (EditText) findViewById(R.id.txtPass);
        PassConfirm = (EditText) findViewById(R.id.txtPassConfirm);
        btn_reg = (Button) findViewById(R.id.btnRegister);

        btn_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Name.getText().toString().equals("") | Email.getText().toString().equals("") | Handphone.getText().toString().equals("") | Password.getText().toString().equals("") | PassConfirm.getText().toString().equals("")) {
                    builder = new AlertDialog.Builder(Register.this);
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
                } else if (!(Password.getText().toString().equals(PassConfirm.getText().toString()))) {
                    builder = new AlertDialog.Builder(Register.this);
                    builder.setTitle("Something went wrong...");
                    builder.setMessage("The Password not Matching..");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Password.setText("");
                            PassConfirm.setText("");
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

                        builder = new AlertDialog.Builder(Register.this);
                        builder.setTitle("Something went wrong...");
                        builder.setMessage("No Network Connection Available..");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Password.setText("");
                                PassConfirm.setText("");
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
            loading = ProgressDialog.show(Register.this,"Please Wait","Connecting to Server..",true,true);
        }

        @SuppressLint("NewApi")
        @Override
        protected void onPostExecute(StringBuffer result) {

            JSONArray jArray;
            String email;

            try {
                jArray = new JSONArray(result.toString());
                JSONObject jObject;

                for (int i=0;i<jArray.length();i++){
                    jObject = jArray.getJSONObject(i);

                    email = jObject.getString("email");

                    if (Email.getText().toString().equals(email)) {
                        loading.dismiss();
                        builder = new AlertDialog.Builder(Register.this);
                        builder.setTitle("Something went wrong...");
                        builder.setMessage("Your Email are already registered..");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Password.setText("");
                                PassConfirm.setText("");
                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                        validate = "unsaved";
                        break;
                    }else{
                        validate = "save";
                    }
                }

            } catch (JSONException e) {
                Toast.makeText(getBaseContext(), "No data Found", Toast.LENGTH_LONG).show();
            }
            if (validate=="save"){
                loading.dismiss();

                RegisterBackgroundTask registerBackgroundTask = new RegisterBackgroundTask(Register.this);
                registerBackgroundTask.execute("register", Name.getText().toString(), Email.getText().toString(), Handphone.getText().toString(), Password.getText().toString());
            }
        }
    }

}
