package com.mobile.transpotid.transpot;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.mobile.transpotid.transpot.Adapter.PathJSONParser;
import com.mobile.transpotid.transpot.Service.MapsServices;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMyLocationChangeListener,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,
        android.location.LocationListener {

    private GoogleMap mMap;
    private Marker myLocationMarker;
    private static BitmapDescriptor markerIconBitmapDescriptor;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    private static final double DEFAULT_RADIUS = 1000;
    private List<DraggableCircle> mCircles = new ArrayList<DraggableCircle>(1);
    private int mStrokeColor, mFillColor;
    private int statusCheckIn = 0;

    private LatLng Position1, Position2, CurrentPosition;
    String id_mission,from_dest,to_dest;
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(AppIndex.API).build();

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1000);

//############################################### Get Id MyMission ##########################################################
        Intent a = getIntent();
        //Getting attached intent data
        id_mission = a.getStringExtra("PK");
        from_dest = a.getStringExtra("from_dest");
        to_dest = a.getStringExtra("to_dest");
//###########################################################################################################################

//############################################### Action Bar ################################################################
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Check In/Out");
//###########################################################################################################################

        TextView route_maps = (TextView)findViewById(R.id.txt_Route_Maps);
        route_maps.setText(from_dest + " - " + to_dest);

        Button btnCancel = (Button) findViewById(R.id.btn_Cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder = new AlertDialog.Builder(MapsActivity.this);
                builder.setTitle("Attention !!");
                builder.setMessage("Are you want to quit this mission ?");
                builder.setPositiveButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationChangeListener(this);
        markerIconBitmapDescriptor = BitmapDescriptorFactory.fromResource(R.mipmap.ic_driver_check_in);

        mFillColor = Color.HSVToColor(50, new float[]{0, 1, 1});
        mStrokeColor = Color.BLACK;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onMyLocationChange(Location location) {
        CurrentPosition = new LatLng(location.getLatitude(), location.getLongitude());

        if (myLocationMarker != null) {
            myLocationMarker.remove();
        }

//        ########################## For Radius per Point ############################
        double newRadius1 = toRadiusMeters(Position1, CurrentPosition);
        double newRadius2 = toRadiusMeters(Position2, CurrentPosition);

        if (statusCheckIn == 0) {
            if (newRadius1 <= DEFAULT_RADIUS) {
                statusCheckIn = 1;
                Intent intent = new Intent(this, MapsActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
                Notification notify = new Notification.Builder(this)
                        .setTicker("The Mission Start Now")
                        .setContentTitle("Transpot")
                        .setContentText("The Mission Start Now")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                        .setContentIntent(pendingIntent).getNotification();
                notify.flags = Notification.FLAG_AUTO_CANCEL;
                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                notificationManager.notify(0, notify);
            }
        } else {
            if (newRadius2 <= DEFAULT_RADIUS) {
                statusCheckIn = 0;
                Intent intent = new Intent(this, MapsActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
                Notification notify = new Notification.Builder(this)
                        .setTicker("Congrate!, Your Mission Done")
                        .setContentTitle("Transpot")
                        .setContentText("The Mission Complate")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                        .setContentIntent(pendingIntent).getNotification();
                notify.flags = Notification.FLAG_AUTO_CANCEL;
                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                notificationManager.notify(0, notify);

                Intent intent2 = new Intent(this, MapsServices.class);
                stopService(intent2);

                Intent intent1 = new Intent(this,Congrate.class);
                startActivity(intent1);
                finish();
            }
        }
        if (statusCheckIn == 1) {
            myLocationMarker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(location.getLatitude(), location.getLongitude()))
                    .icon(markerIconBitmapDescriptor));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(CurrentPosition, 15), 2000, null);
        } else {
            myLocationMarker = null;
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(CurrentPosition, 15), 2000, null);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Sensor enabled
        String sensor = "sensor=false";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        return url;
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, (com.google.android.gms.location.LocationListener) this);
        } else {
            handleNewLocation(location);
        }
    }

    private void handleNewLocation(Location location) {

//        ##################### Line Route Transpot #########################################
//        Position2 = new LatLng(-6.121820, 106.913126);
//        Position1 = new LatLng(-6.165909, 106.872566);

        List<Address> addressList1 = null;
        Geocoder geocoder1 = new Geocoder(this);
        try {
            addressList1 = geocoder1.getFromLocationName(from_dest,1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Address address1 = addressList1.get(0);
        Position1 = new LatLng(address1.getLatitude(),address1.getLongitude());

        List<Address> addressList2 = null;
        Geocoder geocoder2 = new Geocoder(this);
        try {
            addressList2 = geocoder2.getFromLocationName(to_dest,1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Address address2 = addressList2.get(0);
        Position2 = new LatLng(address2.getLatitude(),address2.getLongitude());

        String url = getDirectionsUrl(Position1, Position2);
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(url);

        DraggableCircle circle1 = new DraggableCircle(Position1, DEFAULT_RADIUS, "1");
        mCircles.add(circle1);

        DraggableCircle circle2 = new DraggableCircle(Position2, DEFAULT_RADIUS, "2");
        mCircles.add(circle2);
//        ###################################################################################

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Position1, 15));

//        CameraPosition cameraPosition = new CameraPosition.Builder()
//                .target(Position1).zoom(15).build();
//        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),2000,null);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private class DownloadTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(s);
        }

        @Override
        protected String doInBackground(String... params) {
            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(params[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();
            // Connecting to url
            urlConnection.connect();
            // Reading data from url
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            br.close();
        } catch (Exception e) {
            Log.d("Exception downloading", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private class ParserTask extends AsyncTask<String, Integer, java.util.List<java.util.List<HashMap<String, String>>>> {
        @Override
        protected java.util.List<java.util.List<HashMap<String, String>>> doInBackground(String... jsonData) {
            JSONObject jObject;
            java.util.List<java.util.List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                PathJSONParser parser = new PathJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(java.util.List<java.util.List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                java.util.List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(5);
                lineOptions.color(Color.BLUE);
            }

            // Drawing polyline in the Google Map for the i-th route
            mMap.addPolyline(lineOptions);
        }
    }

    private class DraggableCircle {

        private Marker centerMarker = null;
        private Circle circle = null;
        private double radius;

        public DraggableCircle(LatLng latLng, double radius, String posisi) {
            this.radius = radius;
            if (posisi == "1") {
                centerMarker = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .draggable(true));
            } else {
                centerMarker = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .draggable(true)
                        .icon(BitmapDescriptorFactory.defaultMarker(
                                BitmapDescriptorFactory.HUE_AZURE)));
            }

            circle = mMap.addCircle(new CircleOptions()
                    .center(latLng)
                    .radius(radius)
                    .strokeWidth(1)
                    .strokeColor(mStrokeColor)
                    .fillColor(mFillColor));
        }
    }


    @Override
    protected void onStop() {
        super.onStop();

        Action viewAction = Action.newAction(
                Action.TYPE_VIEW,
                "Maps Page",
                Uri.parse("http://host/path"),
                Uri.parse("android-app://com.mobile.transpot.transpot/http/host/path")
        );
        AppIndex.AppIndexApi.end(mGoogleApiClient, viewAction);
        mGoogleApiClient.disconnect();
    }

    @Override
    protected void onStart() {
        super.onStart();


        mGoogleApiClient.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW,
                "Maps Page",
                Uri.parse("http://host/path"),
                Uri.parse("android-app://com.mobile.transpot.transpot/http/host/path")
        );
        AppIndex.AppIndexApi.start(mGoogleApiClient, viewAction);
    }

    private static double toRadiusMeters(LatLng center, LatLng radius) {
        float[] result = new float[1];
        Location.distanceBetween(center.latitude, center.longitude,
                radius.latitude, radius.longitude, result);
        return result[0];
    }

    @Override
    protected void onPause() {
        super.onPause();

        Intent intent = new Intent(this,MapsServices.class);
        intent.putExtra("statusCheckIn", statusCheckIn);
        intent.putExtra("Position1", Position1);
        intent.putExtra("Position2", Position2);
        startService(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(this,MapsServices.class);
        stopService(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(this,MapsServices.class);
        stopService(intent);
    }
}