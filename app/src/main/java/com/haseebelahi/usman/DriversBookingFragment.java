package com.haseebelahi.usman;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.Request;

public class DriversBookingFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    private SharedPreferences userLogin;
    private String logged_in;
    private ListView bookingList;
    private ArrayList<Booking> bookings;
    private BookingAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    /**************************************/
    //AlarmManager//
    private AlarmManager mAlarmManager;
    private Intent receiverIntent;
    private PendingIntent receiverPendingIntent;
    private static final long INITIAL_ALARM_DELAY = 60 * 1000L;
    protected static final long JITTER = 1000L;
    /**************************************/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        userLogin = getContext().getSharedPreferences("user_login", 0);
        logged_in = userLogin.getString("logged_in", "");

        View view = inflater.inflate(R.layout.fragment_drivers_booking, container, false);
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
                if (bookingList != null && bookingList.getChildCount() > 0) {
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




        bookingList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                SharedPreferences busy = getContext().getSharedPreferences("busy", 0);
                int s = busy.getInt("busy", 0);

                if (s != 1) {
                    Booking b = bookings.get(position);

                    if(b.getStatus().equals("0")) {
                        Intent intent = new Intent(getActivity(), bookingDetails.class);
                        intent.putExtra("id", b.getId());
                        intent.putExtra("src", b.getSource());
                        intent.putExtra("dest", b.getDestination());
                        intent.putExtra("srcAdd", b.getSourceAddress());
                        intent.putExtra("destAdd", b.getDestAddress());
                        intent.putExtra("note", b.getNote());
                        intent.putExtra("fare", b.getFare());
                        intent.putExtra("service", b.getService());
                        intent.putExtra("status",b.getStatus());
                        intent.putExtra("gender", b.getGender());
                        intent.putExtra("car", b.getCar());
                        startActivity(intent);
                        getActivity().finish();
                    }

                } else {
                    Toast.makeText(getContext(), "COMPLETE CURRENT ORDER FIRST", Toast.LENGTH_LONG).show();
                }
            }
        });


        registerForContextMenu(bookingList);

        startService();

        // Inflate the layout for this fragment
        return view;
    }

    private void setRepeatingAlarm()
    {
        mAlarmManager = (AlarmManager) getActivity().getSystemService(getActivity().ALARM_SERVICE);
        // Create an Intent to broadcast to the AlarmNotificationReceiver
        receiverIntent = new Intent(getActivity(),AlarmReceiver.class);
        // Create an PendingIntent that holds the NotificationReceiverIntent
        receiverPendingIntent = PendingIntent.getBroadcast(getActivity(), 0, receiverIntent, 0);
        mAlarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() +
                INITIAL_ALARM_DELAY + JITTER, /*AlarmManager.INTERVAL_FIFTEEN_MINUTES*/600 * 1000, receiverPendingIntent);
    }


    private void startService()
    {
        getActivity().startService(new Intent(getActivity(), MyService.class));
        setRepeatingAlarm();
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
                //Toast.makeText(getActivity(),"You cannot mark this complete",Toast.LENGTH_LONG).show();
                return true;
            default:
                return true;
        }

    }

    private void checkDelivery() {


        Toast.makeText(getActivity(),logged_in,Toast.LENGTH_LONG).show();
        String url = "http://asfand.danglingpixels.com/taxi/app/checkDriver.php?user_id=" + logged_in;

        //showing dialog box
        /*final ProgressDialog progressDialog = new ProgressDialog(this.getActivity(),
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Updating...");
        progressDialog.show();*/


        // Request a string response
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        // Result handling
                        System.out.println(response);
                        //progressDialog.dismiss();
                        if(!response.equals("0")) {
                            SharedPreferences busy = getActivity().getSharedPreferences("busy", Context.MODE_PRIVATE);
                            String booking_id = busy.getString("b_id","");
                            for(int i = 0; i<bookings.size(); i++) {
                                if (booking_id.equals(bookings.get(i).getId())) {
                                    Intent intent = new Intent(getActivity(), bookingDetails.class);
                                    intent.putExtra("id", bookings.get(i).getId());
                                    intent.putExtra("src", bookings.get(i).getSource());
                                    intent.putExtra("dest", bookings.get(i).getDestination());
                                    intent.putExtra("srcAdd", bookings.get(i).getSourceAddress());
                                    intent.putExtra("destAdd", bookings.get(i).getDestAddress());
                                    intent.putExtra("note", bookings.get(i).getNote());
                                    intent.putExtra("status", bookings.get(i).getStatus());
                                    intent.putExtra("fare", bookings.get(i).getFare());
                                    intent.putExtra("service", bookings.get(i).getService());
                                    intent.putExtra("gender", bookings.get(i).getGender());
                                    intent.putExtra("car", bookings.get(i).getCar());
                                    startActivity(intent);
                                    getActivity().finish();
                                }
                            }
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                // Error handling
                System.out.println("Something went wrong!");
                error.printStackTrace();
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getContext(), "FAILED TO CONNECT", Toast.LENGTH_LONG).show();
            }
        });
        // Add the request to the queue
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        Volley.newRequestQueue(this.getContext()).add(stringRequest);
       /* SharedPreferences busy = getActivity().getSharedPreferences("busy", Context.MODE_PRIVATE);
        if(busy.getInt("busy",0) == 1) {

            }
        }*/
    }

    private void markComplete(int position) {

        Booking b = bookings.get(position);
        updateBooking(b);
    }

    private void getBookings() {

        String url = "http://asfand.danglingpixels.com/taxi/app/driverBooking.php?user_id=" + logged_in;

        //showing dialog box
        /*final ProgressDialog progressDialog = new ProgressDialog(this.getActivity(),
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Updating...");
        progressDialog.show();*/


        // Request a string response
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        // Result handling
                        System.out.println(response);
                        //progressDialog.dismiss();
                        swipeRefreshLayout.setRefreshing(false);
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
                            checkDelivery();

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
                Toast.makeText(getContext(), "FAILED TO CONNECT", Toast.LENGTH_LONG).show();
            }
        });
        // Add the request to the queue
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        Volley.newRequestQueue(this.getContext()).add(stringRequest);
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
                        else
                        {
                            SharedPreferences busy = getContext().getSharedPreferences("busy", 0);
                            SharedPreferences.Editor editor = busy.edit();
                            editor.putInt("busy",0);
                            editor.apply();
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
                params.put("type","driver");
                return params;
            }
        };
        // Add the request to the queue
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        Volley.newRequestQueue(getContext()).add(stringRequest);
    }

}
