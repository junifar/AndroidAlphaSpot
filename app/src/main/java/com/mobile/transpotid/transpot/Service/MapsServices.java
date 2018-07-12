package com.mobile.transpotid.transpot.Service;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.mobile.transpotid.transpot.MapsActivity;
import com.mobile.transpotid.transpot.R;

/**
 * Created by andro on 25/05/2016.
 */
public class MapsServices extends Service {

    private LatLng Position1, Position2, CurrentPosition;
    private static final double DEFAULT_RADIUS = 1000;
    private int statusCheckIn;

    private LocationListener locationListener = new LocationListener() {
        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        public void onLocationChanged(Location location) {

            CurrentPosition = new LatLng(location.getLatitude(), location.getLongitude());

//        ########################## For Radius per Point ############################
            double newRadius1 = toRadiusMeters(Position1, CurrentPosition);
            double newRadius2 = toRadiusMeters(Position2, CurrentPosition);

            if (statusCheckIn == 0) {
                if (newRadius1 <= DEFAULT_RADIUS) {
                    statusCheckIn = 1;
                    Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                    PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
                    Notification notify = new Notification.Builder(getApplicationContext())
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
                    Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                    PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
                    Notification notify = new Notification.Builder(getApplicationContext())
                            .setTicker("Congrate!, Your Mission Done")
                            .setContentTitle("Transpot")
                            .setContentText("The Mission Complate")
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                            .setContentIntent(pendingIntent).getNotification();
                    notify.flags = Notification.FLAG_AUTO_CANCEL;
                    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    notificationManager.notify(0, notify);
                }
            }
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
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("MyService", "onStart : " + intent);

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        statusCheckIn = intent.getIntExtra("statusCheckIn",0);
        Position1 = intent.getParcelableExtra("Position1");
        Position2 = intent.getParcelableExtra("Position2");


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return START_NOT_STICKY;
        }
        locationManager.removeUpdates(locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,locationListener);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private static double toRadiusMeters(LatLng center, LatLng radius) {
        float[] result = new float[1];
        Location.distanceBetween(center.latitude, center.longitude,
                radius.latitude, radius.longitude, result);
        return result[0];
    }
}
