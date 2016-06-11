package com.haseebelahi.usman;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
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
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class BookingsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{


    private SharedPreferences userLogin;
    private String logged_in;
    private ListView bookingList;
    private ArrayList<Booking> bookings;
    private BookingAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    /*private LayoutInflater inflater_;
    private ViewGroup container_;
    private Bundle saved;*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


/*
        inflater_ = inflater;
        container_ = container;
        saved = savedInstanceState;
*/

        userLogin = getContext().getSharedPreferences("user_login", 0);
        logged_in = userLogin.getString("logged_in", "");

        View view = inflater.inflate(R.layout.fragment_bookings, container, false);
        bookingList = (ListView) view.findViewById(R.id.bookings);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_layout);

        bookings = new ArrayList<>();
        adapter = new BookingAdapter(this.getContext(),R.layout.booking_layout,bookings);

        bookingList.setAdapter(adapter);

        bookingList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                boolean enable = false;
                if(bookingList != null && bookingList.getChildCount() > 0) {
                    boolean firstItemVis = bookingList.getFirstVisiblePosition() == 0;
                    boolean topOfFirstItemVis = bookingList.getChildAt(0).getTop() == 0;
                    enable = firstItemVis && topOfFirstItemVis;
                }
                swipeRefreshLayout.setEnabled(enable);
            }
        });



        swipeRefreshLayout.setOnRefreshListener(this);

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                getBookings();
            }
        });




        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getActivity().finish();
                startActivity(new Intent(getActivity(), PreBooking.class));
            }
        });

        bookingList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Booking b = bookings.get(position);

                    Intent intent = new Intent(getActivity(), bookingDetails.class);
                    intent.putExtra("id", b.getId());
                    intent.putExtra("src", b.getSource());
                    intent.putExtra("driver", b.getDriver());
                    intent.putExtra("dest", b.getDestination());
                    intent.putExtra("status",b.getStatus());
                    intent.putExtra("srcAdd", b.getSourceAddress());
                    intent.putExtra("destAdd", b.getDestAddress());
                    intent.putExtra("note", b.getNote());
                    intent.putExtra("fare", b.getFare());
                    intent.putExtra("service", b.getService());
                    intent.putExtra("gender", b.getGender());
                    intent.putExtra("car", b.getCar());
                    startActivity(intent);
            }
        });


        registerForContextMenu(bookingList);
        // Inflate the layout for this fragment
        return view;
    }


    @Override
    public void onRefresh() {
        bookings.clear();
        getBookings();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch(item.getItemId()){
            case R.id.mark:
                markComplete(info.position);
                //
                return true;
            default:
                return true;
        }

    }

    private void markComplete(int position) {
        //onCreateView(inflater_, container_, saved);
        //getBookings();
        //adapter.notifyDataSetChanged();
        Booking b = bookings.get(position);
        updateBooking(b);
    }

    public void updateBooking(final Booking b)
    {
        String url = "http://asfand.danglingpixels.com/taxi/app/updatebooking.php";
        //showing dialog box
        final ProgressDialog progressDialog = new ProgressDialog(this.getContext(),
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
                            Toast.makeText(getActivity(),"You cannot mark this complete",Toast.LENGTH_LONG).show();
                        else {
                            bookings.clear();
                            adapter.notifyDataSetChanged();
                            onRefresh();
                        }

                        //startActivity(new Intent(getActivity(),home.class));

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                // Error handling
                System.out.println("Something went wrong!");

                error.printStackTrace();
                progressDialog.dismiss();
                Toast.makeText(getActivity(), "FAILED TO CONNECT", Toast.LENGTH_LONG).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("booking_id",b.getId());
                params.put("driver_id","");
                params.put("type","user");
                return params;
            }
        };
        // Add the request to the queue
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        Volley.newRequestQueue(getContext()).add(stringRequest);
    }

    private void getBookings() {

        String url = "http://asfand.danglingpixels.com/taxi/app/get_bookings.php?user_id=" + logged_in;

        //showing dialog box
        /*final ProgressDialog progressDialog = new ProgressDialog(this.getActivity(),
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Updating...");
        progressDialog.show();*/


        // Request a string response
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        // Result handling
                        System.out.println(response);
                        swipeRefreshLayout.setRefreshing(false);
/*                        progressDialog.dismiss();*/

                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for(int i = 0; i<jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                Booking booking = new Booking();
                                if(!(jsonObject.getString("status").equals("3"))) {
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
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getContext(), "FAILED TO CONNECT",Toast.LENGTH_LONG).show();
            }
        });
        // Add the request to the queue
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        Volley.newRequestQueue(this.getContext()).add(stringRequest);
    }


}
