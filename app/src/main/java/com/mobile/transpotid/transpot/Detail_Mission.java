package com.mobile.transpotid.transpot;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobile.transpotid.transpot.BackugroundTask.BackgroundTask;
import com.mobile.transpotid.transpot.ChangeFormat.ChangeFormat;
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
import java.util.HashMap;

public class Detail_Mission extends AppCompatActivity {

    private URL url;
    private HttpURLConnection connection;
    ImageView img;
    Bitmap bitmap;

    TextView tv_campaign,tv_desc;
    String save_id_mission, save_id_member, save_mission_status;

    private String strURL;
    private Toolbar toolbar;
    UserSessionManager userSessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail__mission);

//        #################String Connection URL####################
        strURL = getString(R.string.mission);
//        ##########################################################

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent a = getIntent();
        //Getting attached intent data
        save_id_mission = a.getStringExtra("PK");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tv_campaign = (TextView) findViewById(R.id.txtCampaign);
        tv_desc = (TextView) findViewById(R.id.txtDesc);

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new getDataTask().execute(strURL);
        } else {
            // Show a toast if the user clicks on an item
            Toast.makeText(Detail_Mission.this, "No network connection available", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    public void joinMission(View view){
        userSessionManager = new UserSessionManager(getApplicationContext());
        HashMap<String, String> member = userSessionManager.getUserDetails();

        save_id_member = member.get(UserSessionManager.KEY_ID);
        save_mission_status = "Unverified";

        String method = "joint";
        BackgroundTask backgroundTask = new BackgroundTask(this);
        backgroundTask.execute(method,save_id_mission,save_id_member,save_mission_status);
        finish();
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
            loading = ProgressDialog.show(Detail_Mission.this,"Please Wait","Connecting to Server..",true,true);
        }

        @SuppressLint("NewApi")
        @Override
        protected void onPostExecute(StringBuffer result) {


            JSONArray jArray;
            String pk, campaign, brand, desc, agent, from_date, to_date, photo, vehicle, from_dest, to_dest;
            Double price;

            try {
                jArray = new JSONArray(result.toString());
                JSONObject jObject;

                for (int i=0;i<jArray.length();i++){
                    jObject = jArray.getJSONObject(i);
                    pk = jObject.getString("id");
                    campaign = jObject.getString("name");
                    brand = jObject.getString("brand");
                    desc = jObject.getString("description");
                    agent = jObject.getString("iteration");
                    from_date = jObject.getString("from_date");
                    to_date = jObject.getString("to_date");
                    price = jObject.getDouble("price");
                    photo = jObject.getString("photo_url");
                    vehicle = jObject.getString("vehicle_type");
                    from_dest = jObject.getString("from_destination");
                    to_dest = jObject.getString("to_destination");


                    if(pk.equals(save_id_mission)){

                        // Campaign
                        TextView txtCampaign = (TextView) findViewById(R.id.txtCampaign);
                        txtCampaign.setText(campaign);

                        // Brand
                        TextView txtBrand = (TextView) findViewById(R.id.txtBrand);
                        txtBrand.setText(brand);

                        // Description
                        TextView txtDesc = (TextView) findViewById(R.id.txtDesc);
                        txtDesc.setText(desc);

                        //Agent
                        TextView txtAgent = (TextView) findViewById(R.id.txtAgent);
                        txtAgent.setText("Agent Join " + agent + "/50");

                        //Periode
                        TextView txtPeriode = (TextView) findViewById(R.id.txtPeriode);
                        txtPeriode.setText(ChangeFormat.formatDate(from_date) + " - " + ChangeFormat.formatDate(to_date));

                        //Price
                        TextView txtPrice = (TextView) findViewById(R.id.txtPrice);
                        txtPrice.setText(ChangeFormat.formatNumber(price));

                        //Photo
                        img = (ImageView)findViewById(R.id.imageCampaign1);
                        new LoadImage().execute(photo);

                        //Vehicle
                        TextView txtVehicle = (TextView) findViewById(R.id.txtVehicle);
                        txtVehicle.setText(vehicle);

                        //Route
                        TextView txtRoute = (TextView) findViewById(R.id.txtRoute);
                        txtRoute.setText(from_dest + " - " + to_dest);

                        //Rule
                        TextView txtRule = (TextView) findViewById(R.id.txtRule);
                        txtRule.setText("Rule : bla..bla..bla..bla..bla..bla..bla..bla..bla..");

                        //Image Campaign 2 and 3
                        ImageView imgCampagin2 = (ImageView) findViewById(R.id.imageCampaign2);
                        ImageView imgCampagin3 = (ImageView) findViewById(R.id.imageCampaign3);

                        imgCampagin2.setVisibility(View.VISIBLE);
                        imgCampagin3.setVisibility(View.VISIBLE);

                        //Button Joint
                        Button btnJoint = (Button) findViewById(R.id.joint);
                        btnJoint.setVisibility(View.VISIBLE);

                        break;
                    }
                }
            } catch (JSONException e) {
                Toast.makeText(getBaseContext(), "No data Found", Toast.LENGTH_LONG).show();
            }
            loading.dismiss();
        }
    }

    private class LoadImage extends AsyncTask<String, String, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... args) {
            try {
                bitmap = BitmapFactory.decodeStream((InputStream) new URL(args[0]).getContent());

            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap image) {

            if(image != null){
                img.setImageBitmap(image);
            }else{
                Toast.makeText(Detail_Mission.this, "Image Does Not exist or Network Error", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
