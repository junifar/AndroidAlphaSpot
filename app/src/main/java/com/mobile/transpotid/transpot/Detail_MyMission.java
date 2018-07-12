package com.mobile.transpotid.transpot;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobile.transpotid.transpot.ChangeFormat.ChangeFormat;
import com.mobile.transpotid.transpot.Session.UserSessionManager;
import com.mobile.transpotid.transpot.ZXingIntegration.IntentIntegrator;
import com.mobile.transpotid.transpot.ZXingIntegration.IntentResult;

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

public class Detail_MyMission extends AppCompatActivity {

    private URL url;
    private HttpURLConnection connection;
    ImageView img;
    Bitmap bitmap;

    TextView tv_campaign,tv_desc;
    String save_id_mission, from_dest, to_dest;

    private String strURL;
    private Toolbar toolbar;

    private String token;
    UserSessionManager sessionManager;
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail__my_mission);

//        #################String Connection URL####################
        strURL = getString(R.string.mymission);
//        ##########################################################

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//################################### Get Id Mymission ######################################
        Intent a = getIntent();
        //Getting attached intent data
        save_id_mission = a.getStringExtra("PK");
//###########################################################################################

//        ################### GET TOKEN #####################
        sessionManager = new UserSessionManager(this);
        HashMap<String, String> user = sessionManager.getUserDetails();
        token = user.get(UserSessionManager.KEY_TOKEN);
//        ###################################################

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
            Toast.makeText(Detail_MyMission.this, "No network connection available", Toast.LENGTH_SHORT).show();
        }
//################################## Take Photo ##############################################
        findViewById(R.id.btn_takePhoto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Detail_MyMission.this, Verifikasi.class));
            }
        });
//############################################################################################
//################################## Check In ##############################################
        findViewById(R.id.btn_checkIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator scanIntegrator = new IntentIntegrator(Detail_MyMission.this);
                scanIntegrator.initiateScan();
            }
        });
//##########################################################################################
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
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
            loading = ProgressDialog.show(Detail_MyMission.this,"Please Wait","Connecting to Server..",true,true);
        }

        @SuppressLint("NewApi")
        @Override
        protected void onPostExecute(StringBuffer result) {

            JSONArray jArray;
            String pk, campaign, brand, desc, agent, from_date, to_date, photo, vehicle,mission_status;
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
                    mission_status = jObject.getString("mission_status");
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

                        //Button TakePhoto and CheckIn
                        Button btn_takePhoto = (Button) findViewById(R.id.btn_takePhoto);
                        Button btn_checkIn = (Button) findViewById(R.id.btn_checkIn);
                        if(mission_status.contentEquals("Unverified")){
                            btn_takePhoto.setVisibility(View.VISIBLE);
                            btn_checkIn.setVisibility(View.GONE);
                        }else if (mission_status.contentEquals("Verified")){
                            btn_takePhoto.setVisibility(View.GONE);
                            btn_checkIn.setVisibility(View.VISIBLE);
                        }

                        //Mission Status
                        TextView txt_status = (TextView) findViewById(R.id.txt_status);
                        txt_status.setText("Status: "+mission_status);

                        //Log Mission
                        TextView txt_logMission = (TextView) findViewById(R.id.txt_LogMission);
                        txt_logMission.setText("Log Mission : 70%");

                        //Route
                        TextView txt_Route = (TextView) findViewById(R.id.txtRoute);
                        txt_Route.setText(from_dest + " - " + to_dest);

                        //Image Campaign 2 and 3
                        ImageView img_Campaign2 = (ImageView) findViewById(R.id.imageCampaign2);
                        ImageView img_Campaign3 = (ImageView) findViewById(R.id.imageCampaign3);
                        img_Campaign2.setVisibility(View.VISIBLE);
                        img_Campaign3.setVisibility(View.VISIBLE);

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
                Toast.makeText(Detail_MyMission.this, "Image Does Not exist or Network Error", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (scanningResult != null){
//            String scanContent = scanningResult.getContents();
//            hslQRCode.setText(scanContent);

            if (scanningResult.getContents() != null){
                if (scanningResult.getContents().equals(token)){
                    Intent i = new Intent(getApplicationContext(), MapsActivity.class);
                    i.putExtra("PK",save_id_mission);
                    i.putExtra("from_dest",from_dest);
                    i.putExtra("to_dest",to_dest);
                    startActivity(i);
                } else {
                    builder = new AlertDialog.Builder(Detail_MyMission.this);
                    builder.setTitle("Something went wrong...");
                    builder.setMessage("Your Barcode no Valid !!");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            } else {
                builder = new AlertDialog.Builder(Detail_MyMission.this);
                builder.setTitle("Something went wrong...");
                builder.setMessage("Your Barcode no Valid !!");
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
