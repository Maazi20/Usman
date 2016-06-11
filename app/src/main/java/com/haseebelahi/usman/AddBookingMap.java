package com.haseebelahi.usman;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@SuppressWarnings("ResourceType")
public class AddBookingMap extends AppCompatActivity implements OnMapReadyCallback {

    private final float PER_M_FARE = 13;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private Location location = null;
    private Marker currLocMarker, srcMarker, destMarker;
    private LatLng srcLoc, destLoc;
    private Toolbar toolbar;
    private TextView distance, fare;
    private AutoCompleteTextView src, dest;
    private String currSelec = "src", logged_in;
    private Button book_ride;
    private Booking final_booking;
    private String gender,type,service;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_booking_map);


        SharedPreferences userLogin;
        userLogin = getSharedPreferences("user_login", 0);
        logged_in = userLogin.getString("logged_in", "");

        //setting toolbar on top of activity
        toolbar = (Toolbar) findViewById(R.id.toolbar_map);
        setSupportActionBar(toolbar);//using toolbar as the action bar
        toolbar.bringToFront();

        src = (AutoCompleteTextView) findViewById(R.id.source);
        dest = (AutoCompleteTextView) findViewById(R.id.dest);
        fare = (TextView) findViewById(R.id.fare);
        distance = (TextView) findViewById(R.id.distance);

        src.setAdapter(new GooglePlacesAutocompleteAdapter(this,R.layout.auto_complete));
        dest.setAdapter(new GooglePlacesAutocompleteAdapter(this,R.layout.auto_complete));

        book_ride = (Button) findViewById(R.id.book_ride);

        srcLoc = new LatLng(0, 0);
        destLoc = new LatLng(0, 0);

        srcMarker = null;
        destMarker = null;

        Intent my_intent = getIntent();
        if(my_intent==null)
        {
            //Haseeb please decide what to do
        }
        else
        {

            gender = my_intent.getStringExtra("gender").toString();
            service = my_intent.getStringExtra("service").toString();
            type = my_intent.getStringExtra("Type").toString();

            Toast.makeText(getBaseContext(),gender+service+type,Toast.LENGTH_SHORT).show();

            if(service.equals("Airport") || service.equals("Shopping") || service.equals("Rent"))
            {
                dest.setVisibility(View.GONE);
                if(service.equals("Airport")) {
                    //set Dest coordinates
                    LatLng LL = new LatLng(31.5213936,74.4022553);
                    destLoc = LL;
                }
            }

        }

        /*src.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {

            }
        });

        dest.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {

            }
        });*/

        src.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                currSelec = "src";
                if (srcMarker != null) {
                    srcMarker.setVisible(false);
                }
                src.setBackground(getResources().getDrawable(R.drawable.edit_text_bg_active));
                dest.setBackground(getResources().getDrawable(R.drawable.edit_text_bg));
                book_ride.setVisibility(View.GONE);
                distance.setVisibility(View.GONE);
                fare.setVisibility(View.GONE);
                return false;
            }
        });

        dest.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                currSelec = "dest";
                if (destMarker != null) {
                    destMarker.setVisible(false);
                }
                dest.setBackground(getResources().getDrawable(R.drawable.edit_text_bg_active));
                src.setBackground(getResources().getDrawable(R.drawable.edit_text_bg));
                book_ride.setVisibility(View.GONE);
                distance.setVisibility(View.GONE);
                fare.setVisibility(View.GONE);
                return false;
            }
        });


        src.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view,
                                    int position, long id) {


                currSelec = "src";
                if (srcMarker != null) {
                    srcMarker.setVisible(false);
                }
                srcLoc = new LatLng(0, 0);
                if (currLocMarker != null) {
                    currLocMarker.showInfoWindow();
                }

                GooglePlace googlePlace = (GooglePlace) parent.getItemAtPosition(position);
                Toast.makeText(AddBookingMap.this, googlePlace.getDesc(), Toast.LENGTH_SHORT).show();
                src.setText(googlePlace.getDesc());
                dropPin(googlePlace,"");

            }
        });
        dest.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view,
                                    int position, long id) {


                if (destMarker != null) {
                    destMarker.setVisible(false);
                }
                currSelec = "dest";
                destLoc = new LatLng(0, 0);

                GooglePlace googlePlace = (GooglePlace) parent.getItemAtPosition(position);
                Toast.makeText(AddBookingMap.this, googlePlace.getDesc(), Toast.LENGTH_SHORT).show();

                dest.setText(googlePlace.getDesc());
                dropPin(googlePlace, "");
            }
        });

        book_ride.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final_booking = new Booking();
                final_booking.setUser(logged_in);
                final_booking.setSource(String.valueOf(srcLoc.latitude) + "," + String.valueOf(srcLoc.longitude));
                final_booking.setDestination(String.valueOf(destLoc.latitude) + "," + String.valueOf(destLoc.longitude));
                final_booking.setDistance(String.valueOf(getDistance()));
                final_booking.setFare(String.valueOf(((getDistance() / 1000) * PER_M_FARE) + 50));
                final_booking.setSourceAddress(getAddress(srcLoc.latitude, srcLoc.longitude));
                if(destLoc.latitude != 0.0 && destLoc.longitude != 0.0) {
                    final_booking.setDestAddress(getAddress(destLoc.latitude, destLoc.longitude));
                }
                final_booking.setStatus("0");
                final_booking.setService(service);
                final_booking.setGender(gender);
                final_booking.setCar(type);
                write_note_dialog();
            }
        });


        currLocMarker = null;

        //getting current location
        LocationManager locationManager;
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean isGPSEnabled;

        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (isGPSEnabled) {

            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }


        setUpMapIfNeeded();
    }

    private void dropPin(GooglePlace googlePlace, String src_dest) {

        final String API_KEY = "AIzaSyA3_mqu0pFHdzD829TRX151eLiGgKHPKLE";

        String url = "https://maps.googleapis.com/maps/api/place/details/json?placeid=" + googlePlace.getReferenceId() +  "&key=" + API_KEY;

        //showing dialog box
        final ProgressDialog progressDialog = new ProgressDialog(AddBookingMap.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Booking Ride...");
        progressDialog.show();


        // Request a string response
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject geom = jsonObject.getJSONObject("result").getJSONObject("geometry").getJSONObject("location");
                            Double lat = Double.valueOf(geom.getString("lat"));
                            Double lng = Double.valueOf(geom.getString("lng"));
                            LatLng latLng = new LatLng(lat,lng);
                            String address = getAddress(latLng.latitude, latLng.longitude);
                            if (currSelec.equals("src")) {
                                srcLoc = latLng;
                                if (srcMarker != null) {
                                    srcMarker.setVisible(false);
                                }
                                srcMarker = mMap.addMarker(new MarkerOptions().position(latLng)
                                        .title(src.getText().toString())
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                            } else {
                                destLoc = latLng;
                                if (destMarker != null) {
                                    destMarker.setVisible(false);
                                }
                                destMarker = mMap.addMarker(new MarkerOptions().position(latLng)
                                        .title(dest.getText().toString())
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                            }

                            if (srcLoc.latitude != 0 && srcLoc.longitude != 0 && destLoc.latitude != 0 && destLoc.longitude != 0) {

                                book_ride.setVisibility(View.VISIBLE);
                                distance.setText("Estimated Distance: " + String.valueOf(getDistance() / 1000) + " Km");
                                fare.setText("Estimated Fare: " + String.valueOf(((getDistance() / 1000) * PER_M_FARE)+50) + "rs");
                                distance.setVisibility(View.VISIBLE);
                                fare.setVisibility(View.VISIBLE);
                            }
                            else if((service.equals("Shopping") || service.equals("Rent")) && (srcLoc.latitude != 0 && srcLoc.longitude != 0)) {

                                book_ride.setVisibility(View.VISIBLE);
                                //distance.setText("Estimated Distance: " + String.valueOf(getDistance() / 1000) + " Km");
                                //fare.setText("Estimated Fare: " + String.valueOf((PER_M_FARE)+50) + " rs");
                                fare.setText("Estimated Fare: 100 rs");
                                //distance.setVisibility(View.VISIBLE);
                                fare.setVisibility(View.VISIBLE);

                            }
                            CameraUpdate center =
                                    CameraUpdateFactory.newLatLng(latLng);
                            CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
                            mMap.moveCamera(center);
                            mMap.animateCamera(zoom);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        // Result handling
                        System.out.println(response);
                        progressDialog.dismiss();



                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                // Error handling
                System.out.println("Something went wrong!");
                error.printStackTrace();
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "FAILED TO CONNECT",Toast.LENGTH_LONG).show();
            }
        });
        // Add the request to the queue
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        Volley.newRequestQueue(this).add(stringRequest);



    }

    private void write_note_dialog() {


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter a private note for the driver:");


        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final_booking.setNote(input.getText().toString());
                insertBooking();
                dialog.cancel();
                //call add friend routine
            }
        });
        builder.setNegativeButton("Skip", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                insertBooking();
                dialog.cancel();
            }
        });

        builder.show();
    }


    private void insertBooking() {

        String url = "http://asfand.danglingpixels.com/taxi/app/insert_booking.php";

        //showing dialog box
        final ProgressDialog progressDialog = new ProgressDialog(AddBookingMap.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Booking Ride...");
        progressDialog.show();


        // Request a string response
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        // Result handling
                        System.out.println(response);
                        progressDialog.dismiss();
                        startNextActivity();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                // Error handling
                System.out.println("Something went wrong!");
                error.printStackTrace();
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "FAILED TO CONNECT",Toast.LENGTH_LONG).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user",final_booking.getUser());
                params.put("source",final_booking.getSource());
                params.put("dest",final_booking.getDestination());
                params.put("addressSource",final_booking.getSourceAddress());
                params.put("addressDestination",final_booking.getDestAddress());
                params.put("fare", final_booking.getFare());
                params.put("note", final_booking.getNote());
                params.put("completed",final_booking.getStatus());
                params.put("service",final_booking.getService());
                params.put("gender",final_booking.getGender());
                params.put("car",final_booking.getCar());
                return params;
            }
        };
        // Add the request to the queue
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        Volley.newRequestQueue(this).add(stringRequest);

    }


    private void startNextActivity() {
        finish();
        startActivity(new Intent(this, home.class));
    }
    private String getAddress(double lat, double lang) {
        String address = "";
        Geocoder geocoder;
        geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addressList = null;
        try {
            addressList = geocoder.getFromLocation(lat, lang, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addressList != null) {
            address = addressList.get(0).getAddressLine(0);
        }
        return address;
    }

    private float getDistance() {

        Location srcLocation = new Location("src");
        srcLocation.setLatitude(srcLoc.latitude);
        srcLocation.setLongitude(srcLoc.longitude);
        Location destLocation = new Location("dest");
        destLocation.setLatitude(destLoc.latitude);
        destLocation.setLongitude(destLoc.longitude);
        return srcLocation.distanceTo(destLocation);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
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
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        //mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {




        mMap = googleMap;


        //setting map focus to current location or a default location
        LatLng latLng;
        if (location != null) {
            latLng = new LatLng(location.getLatitude(), location.getLongitude());
            currLocMarker = mMap.addMarker(new MarkerOptions().position(latLng)
                    .title(getAddress(latLng.latitude, latLng.longitude))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            currLocMarker.showInfoWindow();
        } else {
            latLng = new LatLng(31.4695994, 74.2965671);
        }
        CameraUpdate center =
                CameraUpdateFactory.newLatLng(latLng);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
        mMap.moveCamera(center);
        mMap.animateCamera(zoom);

        //enabling using current location get settings
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            // Show rationale and request permission.
        }

        setUpMap();

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                String address = getAddress(latLng.latitude, latLng.longitude);
                if (currSelec.equals("src")) {
                    src.setText(address);
                    srcLoc = latLng;
                    if (srcMarker != null) {
                        srcMarker.setVisible(false);
                    }
                    srcMarker = mMap.addMarker(new MarkerOptions().position(latLng)
                            .title(address)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                } else {
                    dest.setText(address);
                    destLoc = latLng;
                    if (destMarker != null) {
                        destMarker.setVisible(false);
                    }
                    destMarker = mMap.addMarker(new MarkerOptions().position(latLng)
                            .title(address)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                }

                if (srcLoc.latitude != 0 && srcLoc.longitude != 0 && destLoc.latitude != 0 && destLoc.longitude != 0) {

                    book_ride.setVisibility(View.VISIBLE);
                    distance.setText("Estimated Distance: " + String.valueOf(getDistance() / 1000) + " Km");
                    fare.setText("Estimated Fare: " + String.valueOf(((getDistance() / 1000) * PER_M_FARE)+50) + "rs");
                    distance.setVisibility(View.VISIBLE);
                    fare.setVisibility(View.VISIBLE);
                }
                else if((service.equals("Shopping") || service.equals("Rent")) && (srcLoc.latitude != 0 && srcLoc.longitude != 0)) {

                    book_ride.setVisibility(View.VISIBLE);
                    //distance.setText("Estimated Distance: " + String.valueOf(getDistance() / 1000) + " Km");
                    //fare.setText("Estimated Fare: " + String.valueOf((PER_M_FARE)+50) + " rs");
                    fare.setText("Estimated Fare: 100 rs");
                    //distance.setVisibility(View.VISIBLE);
                    fare.setVisibility(View.VISIBLE);

                }

            }
        });


        //Setting info window

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            // Use default InfoWindow frame
            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            // Defines the contents of the InfoWindow
            @Override
            public View getInfoContents(Marker arg0) {

                // Getting view from the layout file info_window_layout
                View v = getLayoutInflater().inflate(R.layout.marker_info_window, null);

                TextView address = (TextView) v.findViewById(R.id.address);
                address.setText(getAddress(arg0.getPosition().latitude, arg0.getPosition().longitude));

                if(currLocMarker == null) {
                    return null;
                }
                if (!(arg0.getPosition().latitude == currLocMarker.getPosition().latitude && arg0.getPosition().longitude == currLocMarker.getPosition().longitude)) {
                    return null;
                }

                if (arg0.getPosition().latitude == srcLoc.latitude && arg0.getPosition().longitude == srcLoc.longitude) {
                    return null;
                }

                // Returning the view containing InfoWindow contents
                return v;

            }
        });


        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                if (marker.getPosition().latitude == currLocMarker.getPosition().latitude && marker.getPosition().longitude == currLocMarker.getPosition().longitude) {
                    src.setText(getAddress(marker.getPosition().latitude, marker.getPosition().longitude));
                    srcLoc = marker.getPosition();
                    marker.hideInfoWindow();
                    if (srcMarker != null) {
                        srcMarker.setVisible(false);
                    }

                    if (srcLoc.latitude != 0 && srcLoc.longitude != 0 && destLoc.latitude != 0 && destLoc.longitude != 0) {

                        book_ride.setVisibility(View.VISIBLE);
                        distance.setText("Estimated Distance: " + String.valueOf(getDistance() / 1000) + " Km");
                        fare.setText("Estimated Fare: " + String.valueOf(((getDistance() / 1000) * PER_M_FARE)+50) + "rs");
                        distance.setVisibility(View.VISIBLE);
                        fare.setVisibility(View.VISIBLE);
                    }
                    else if((service.equals("Shopping") || service.equals("Rent")) && (srcLoc.latitude != 0 && srcLoc.longitude != 0)) {

                        book_ride.setVisibility(View.VISIBLE);
                        //distance.setText("Estimated Distance: " + String.valueOf(getDistance() / 1000) + " Km");
                        //fare.setText("Estimated Fare: " + String.valueOf((PER_M_FARE)+50) + "rs");
                        fare.setText("Estimated Fare: 100 rs");
                        //distance.setVisibility(View.VISIBLE);
                        fare.setVisibility(View.VISIBLE);

                    }
                }
            }
        });
    }
}
