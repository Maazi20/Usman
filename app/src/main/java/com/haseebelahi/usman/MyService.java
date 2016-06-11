package com.haseebelahi.usman;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("ALL")
public class MyService extends Service implements LocationListener {

    //location variables
    long MIN_TIME_BW_UPDATES = 5000;
    float MIN_DISTANCE_CHANGE_FOR_UPDATES = 1;
    Location location = null;
    LocationManager locationManager;
    boolean dataPosted;


    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.
        //Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();

        dataPosted = false;
        //Getting Location
        setLocation();
        //sendData();

        return START_NOT_STICKY;
    }

    @SuppressLint("NewApi")
    private void sendData() {
        SharedPreferences userLogin = getApplicationContext().getSharedPreferences("user_login", 0);
        final String Eid = userLogin.getString("logged_in", "");
        final String status = userLogin.getString("status", "");
        if (Eid.equals("")) {
            Intent intent = new Intent(getApplicationContext(), login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            locationManager.removeUpdates(this);
            stopSelf();

        }
        final String location_s = String.valueOf(((MyApplication) this.getApplication()).getLocation_lat()) + " , " + String.valueOf(((MyApplication) this.getApplication()).getLocation_long());
        final Context mContext = this;

        if (isNetworkAvailable()) {
            sendVolleyRequest(Eid, location_s);
        }

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @SuppressLint("NewApi")
    private void setLocation() {
        Context mContext = getApplicationContext();
        locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
        boolean isGPSEnabled;

        // getting network status
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (isGPSEnabled) {

            locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, this, null);

            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this, null);

            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location != null) {
                ((MyApplication) this.getApplication()).setLocation(location.getLongitude(), location.getLatitude());
                if (!dataPosted) {
                    sendData();
                }
                dataPosted = true;
            } else {
                Log.v("LocationTracker", "Location is null");
            }
        }
    }


    private void sendVolleyRequest(final String Eid, final String location_) {
        String url = "http://asfand.danglingpixels.com/taxi/app/updateDriverLoc.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Result handling
                        System.out.println(response);
                        Toast.makeText(getBaseContext(), "UPDATE", Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Error handling
                System.out.println("Something went wrong!");
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", Eid);
                params.put("location", location_);
                return params;
            }
        };

// Add the request to the queue
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        Volley.newRequestQueue(this).add(stringRequest);
    }


    @Override
    public void onLocationChanged(Location location) {
        // TODO Auto-generated method stub
        ((MyApplication) this.getApplication()).setLocation(location.getLongitude(), location.getLatitude());
        if (!dataPosted) {
            sendData();
        }
        //Toast.makeText(getBaseContext(), "Inside Update: " + Double.toString(((MyApplication) this.getApplication()).getLocation_lat()),Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
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
}

