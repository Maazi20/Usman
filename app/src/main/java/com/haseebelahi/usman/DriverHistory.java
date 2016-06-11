package com.haseebelahi.usman;


import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class DriverHistory extends Fragment {

    private SharedPreferences userLogin;
    private String logged_in;
    private ListView bookingList;
    private ArrayList<Booking> bookings;
    private BookingAdapter adapter;



    public DriverHistory() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        userLogin = getActivity().getSharedPreferences("user_login", 0);
        logged_in = userLogin.getString("logged_in", "");

        View view = inflater.inflate(R.layout.fragment_driver_history, container, false);
        bookingList = (ListView) view.findViewById(R.id.bookings);

        bookings = new ArrayList<>();
        adapter = new BookingAdapter(this.getActivity(),R.layout.booking_layout,bookings);

        bookingList.setAdapter(adapter);

        getBookings();

        return view;
    }

    private void getBookings() {

        String url = "http://asfand.danglingpixels.com/taxi/app/driverBooking.php?user_id=" + logged_in;

        //showing dialog box
        final ProgressDialog progressDialog = new ProgressDialog(this.getActivity(),
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
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for(int i = 0; i<jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                Booking booking = new Booking();
                                if(jsonObject.getString("status").equals("3") && jsonObject.getString("driver").equals(logged_in)) {
                                    booking.setId(jsonObject.getString("bookingid"));
                                    booking.setUser(jsonObject.getString("user"));
                                    booking.setDriver(jsonObject.getString("driver"));
                                    booking.setSource(jsonObject.getString("source"));
                                    booking.setDestination(jsonObject.getString("destination"));
                                    booking.setFare(jsonObject.getString("fare"));
                                    booking.setSourceAddress(jsonObject.getString("addressSource"));
                                    booking.setDestAddress(jsonObject.getString("addressDestination"));
                                    booking.setStatus(jsonObject.getString("status"));
                                    booking.setService((jsonObject.getString("service")));
                                    booking.setGender((jsonObject.getString("gender")));
                                    booking.setCar((jsonObject.getString("car")));
                                    bookings.add(booking);
                                }

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        adapter.notifyDataSetChanged();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                // Error handling
                System.out.println("Something went wrong!");
                error.printStackTrace();
                progressDialog.dismiss();
                Toast.makeText(getContext(), "FAILED TO CONNECT", Toast.LENGTH_LONG).show();
            }
        });
        // Add the request to the queue
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        Volley.newRequestQueue(this.getContext()).add(stringRequest);
    }


}
