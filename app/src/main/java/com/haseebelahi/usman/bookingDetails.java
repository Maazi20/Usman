package com.haseebelahi.usman;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Map;

public class bookingDetails extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private Location locationSrc = null, locationDest = null, locationDriver = null;
    private Marker currLocMarker, srcMarker, destMarker;
    private Booking b;
    private Circle driverLoc;
    private SharedPreferences busy;
    private String logged_in, driver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences userLogin = getSharedPreferences("user_login", 0);
        logged_in = userLogin.getString("logged_in", "");
        driver = userLogin.getString("driver","");
        setContentView(R.layout.activity_booking_details);

        TextView route = (TextView) findViewById(R.id.route);
        TextView distance = (TextView) findViewById(R.id.distance);
        TextView fare = (TextView) findViewById(R.id.fare);

        Button select_booking = (Button) findViewById(R.id.choose_booking);
        final Button cancel_booking = (Button) findViewById(R.id.cancel_booking);

        cancel_booking.setVisibility(View.GONE);

        Intent intent = getIntent();
        b = new Booking();
        if(intent != null) {
            b.setId(intent.getStringExtra("id"));
            b.setSource(intent.getStringExtra("src"));
            b.setDestination(intent.getStringExtra("dest"));
            b.setDriver(intent.getStringExtra("driver"));
            b.setSourceAddress(intent.getStringExtra("srcAdd"));
            b.setDestAddress(intent.getStringExtra("destAdd"));
            b.setFare(intent.getStringExtra("fare"));
            b.setNote(intent.getStringExtra("note"));
            b.setService((intent.getStringExtra("service")));
            b.setGender((intent.getStringExtra("gender")));
            b.setCar((intent.getStringExtra("car")));
            b.setStatus((intent.getStringExtra("status")));
        }

        route.setText("From " + b.getSourceAddress() + " to "+b.getDestAddress());
        Double distance_d = (Double.valueOf(b.getFare()) - 50) / 13;
        distance.setText("Distance: " + distance_d.toString() + " Km");
        fare.setText("Fare: " + b.getFare() + "Rs");

        if(b.getService().equals("Rent") || b.getService().equals("Shopping")) {
            distance.setText(b.getService() + " Service");
            fare.setText("13rs/Km");
        }

        if(!driver.equals("yes"))
        {
            switch (b.getStatus()) {
                case "1":
                    select_booking.setText("Refresh");
                    break;
                case "0":
                    select_booking.setVisibility(View.GONE);
                    break;
                case "2":
                    select_booking.setText("Mark Complete");
                    break;
            }
            if(b.getStatus().equals("0") || b.getStatus().equals("1")) {
                cancel_booking.setVisibility(View.VISIBLE);
            }
        }

        String srcCoords[] = b.getSource().split(",");
        String destCoords[] = b.getDestination().split(",");

        locationSrc = new Location("");
        locationSrc.setLatitude(Double.valueOf(srcCoords[0]));
        locationSrc.setLongitude(Double.valueOf(srcCoords[1]));

        locationDest = new Location("");
        locationDest.setLatitude(Double.valueOf(destCoords[0]));
        locationDest.setLongitude(Double.valueOf(destCoords[1]));

        if(driver.equals("yes")) {
            busy = getSharedPreferences("busy", 0);
            int s = busy.getInt("busy", 0);
            if(s == 1) {
                select_booking.setText("Mark Complete");
            }
        }

        select_booking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (driver.equals("yes")) {
                    busy = getSharedPreferences("busy", 0);
                    int s = busy.getInt("busy", 0);
                    if (s == 0) //isnt busy
                    {
                        SharedPreferences.Editor editor = busy.edit();
                        editor.putInt("busy", 1);
                        editor.putString("b_id", b.getId());
                        editor.apply();
                        updateBooking(b.getId(), logged_in, "1");
                    } else {
                        updateBooking(b);
                    }
                } else {

                    if(b.getStatus().equals("2")) {
                        updateBooking(b.getId(),"","");
                    }
                    else if(b.getStatus().equals("1")) {
                        if (driverLoc != null) {
                            driverLoc.remove();
                        }
                        getDriverLoc();
                    }
                }

            }
        });

        cancel_booking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelBooking(b);
            }
        });

        if(!driver.equals("yes")) {
            if(!b.getDriver().equals("")) {
                getDriverLoc();
            }
            else{
                setUpMapIfNeeded();
            }
        }
        else {
            setUpMapIfNeeded();
        }
    }


    public void updateBooking(final Booking b)
    {
        String url = "http://asfand.danglingpixels.com/taxi/app/updatebooking.php";
        //showing dialog box
        final ProgressDialog progressDialog = new ProgressDialog(this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Updating...");
        progressDialog.show();


        // Request a string response
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        // Result handling
                        System.out.println(response);
                        progressDialog.dismiss();
                        if(response.equals("-2"))
                            Toast.makeText(bookingDetails.this,"You cannot mark this complete",Toast.LENGTH_LONG).show();
                        else
                        {
                            SharedPreferences busy = bookingDetails.this.getSharedPreferences("busy", 0);
                            SharedPreferences.Editor editor = busy.edit();
                            editor.putInt("busy",0);
                            editor.apply();
                            startActivity(new Intent(bookingDetails.this, home.class));
                            finish();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                // Error handling
                System.out.println("Something went wrong!");
                error.printStackTrace();
                progressDialog.dismiss();
                Toast.makeText(bookingDetails.this, "FAILED TO CONNECT", Toast.LENGTH_LONG).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("booking_id",b.getId());
                params.put("driver_id","");
                params.put("type","driver");
                return params;
            }
        };
        // Add the request to the queue
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        Volley.newRequestQueue(this).add(stringRequest);
    }

    public void cancelBooking(final Booking b)
    {
        String url = "http://asfand.danglingpixels.com/taxi/app/cancelbooking.php";
        //showing dialog box
        final ProgressDialog progressDialog = new ProgressDialog(this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Updating...");
        progressDialog.show();


        // Request a string response
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        // Result handling
                        System.out.println(response);
                        progressDialog.dismiss();
                        if(response.equals("1")) {
                            SharedPreferences busy = bookingDetails.this.getSharedPreferences("busy", 0);
                            SharedPreferences.Editor editor = busy.edit();
                            editor.putInt("busy",0);
                            editor.apply();
                            startActivity(new Intent(bookingDetails.this, home.class));
                            finish();
                        }
                        else if(response.equals("-2")) {
                            Toast.makeText(bookingDetails.this,"Cannot Cancel this Booking",Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                // Error handling
                System.out.println("Something went wrong!");
                error.printStackTrace();
                progressDialog.dismiss();
                Toast.makeText(bookingDetails.this, "FAILED TO CONNECT", Toast.LENGTH_LONG).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("booking_id",b.getId());
                return params;
            }
        };
        // Add the request to the queue
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        Volley.newRequestQueue(this).add(stringRequest);
    }


    public void getDriverLoc() {
        String url = "http://asfand.danglingpixels.com/taxi/app/get_driver_loc.php";

        //showing dialog box
        final ProgressDialog progressDialog = new ProgressDialog(bookingDetails.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Updating...");
        progressDialog.show();


        // Request a string response
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        // Result handling
                        System.out.println(response);

                        if(!response.equals("-1")) {
                            String driverCoords[] = response.split(",");
                            locationDriver = new Location("");
                            if(!response.equals(",")) {
                                locationDriver.setLatitude(Double.valueOf(driverCoords[0]));
                                locationDriver.setLongitude(Double.valueOf(driverCoords[1]));
                            }
                        }
                        progressDialog.dismiss();
                        setUpMapIfNeeded();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                // Error handling
                System.out.println("Something went wrong!");
                error.printStackTrace();
                progressDialog.dismiss();
                Toast.makeText(getBaseContext(), "FAILED TO CONNECT", Toast.LENGTH_LONG).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("driver_id",b.getDriver());
                return params;
            }
        };
        // Add the request to the queue
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        Volley.newRequestQueue(bookingDetails.this).add(stringRequest);
    }


    public void updateBooking(final String booking_id, final String driver_id, final String status_)
    {
        String url = "http://asfand.danglingpixels.com/taxi/app/updatebooking.php";

        //showing dialog box
        final ProgressDialog progressDialog = new ProgressDialog(bookingDetails.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Updating...");
        progressDialog.show();


        // Request a string response
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        // Result handling
                        System.out.println(response);
                        progressDialog.dismiss();
                        startActivity(new Intent(bookingDetails.this,home.class));

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                // Error handling
                System.out.println("Something went wrong!");
                error.printStackTrace();
                progressDialog.dismiss();
                Toast.makeText(getBaseContext(), "FAILED TO CONNECT", Toast.LENGTH_LONG).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("booking_id",booking_id);
                if(driver.equals("yes")) {
                    params.put("driver_id",driver_id);
                    params.put("type","driver");
                }
                else {
                    params.put("driver_id","");
                    params.put("type","user");
                }

                return params;
            }
        };
        // Add the request to the queue
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        Volley.newRequestQueue(bookingDetails.this).add(stringRequest);
    }
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            SupportMapFragment supportMapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
            supportMapFragment.getMapAsync(this);

            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
        if(mMap != null)
            setUpMap();
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        //mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
        LatLng latLng = null;
        if((!driver.equals("yes")) && (!b.getDriver().equals(""))) {
            latLng = new LatLng(locationDriver.getLatitude(), locationDriver.getLongitude());
            driverLoc = mMap.addCircle(new CircleOptions()
                            .center(latLng)
                            .radius(3)
                            .strokeColor(Color.RED)
                            .fillColor(Color.RED)

            );
        }
        if(latLng != null) {
            CameraUpdate center =
                    CameraUpdateFactory.newLatLng(latLng);
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
            mMap.moveCamera(center);
            mMap.animateCamera(zoom);
        }


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        //setting map focus to current location or a default location
        LatLng latLng;
        latLng = new LatLng(locationDest.getLatitude(), locationDest.getLongitude());
        destMarker = mMap.addMarker(new MarkerOptions().position(latLng)
                .title(b.getDestAddress() + " Pick Up Location")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        //destMarker.showInfoWindow();


        latLng = new LatLng(locationSrc.getLatitude(), locationSrc.getLongitude());
        srcMarker = mMap.addMarker(new MarkerOptions().position(latLng)
                .title(b.getSourceAddress() + " Drop Location")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        //destMarker.showInfoWindow();

        if((!driver.equals("yes")) && (!b.getDriver().equals(""))) {
            latLng = new LatLng(locationDriver.getLatitude(), locationDriver.getLongitude());
            driverLoc = mMap.addCircle(new CircleOptions()
                            .center(latLng)
                            .radius(3)
                            .strokeColor(Color.RED)
                            .fillColor(Color.RED)

            );
        }
        CameraUpdate center =
                CameraUpdateFactory.newLatLng(latLng);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(11);
        mMap.moveCamera(center);
        mMap.animateCamera(zoom);

        //enabling using current location get settings
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            // Show rationale and request permission.
        }

        //setUpMap();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_booking_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
