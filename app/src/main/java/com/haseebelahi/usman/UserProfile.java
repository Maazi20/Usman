package com.haseebelahi.usman;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserProfile extends Fragment {

    private String logged_in,driver;
    private TextView name, username,email, cnic;

    public UserProfile() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        logged_in = getContext().getSharedPreferences("user_login", 0).getString("logged_in", "");
        driver = getContext().getSharedPreferences("user_login",0).getString("driver","");
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_user_profile, container, false);

        name = (TextView) v.findViewById(R.id.name);
        username = (TextView) v.findViewById(R.id.username);
        email = (TextView) v.findViewById(R.id.email);
        cnic = (TextView) v.findViewById(R.id.cninc);

        if(driver.equals("yes")) {
            getDriverDetails();
        }
        else {
            getUserDetails();
        }

        final Button change_pass = (Button) v.findViewById(R.id.change_pass);
        change_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ChangePassword.class));
            }
        });
        return v;
    }


    public void getUserDetails() {

        String url = "http://asfand.danglingpixels.com/taxi/app/get_user.php?user_id=" + logged_in;

        //showing dialog box
        final ProgressDialog progressDialog = new ProgressDialog(this.getActivity(),
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Updating...");
        progressDialog.show();


        // Request a string response
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        // Result handling
                        System.out.println(response);
                        progressDialog.dismiss();

                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            name.setText("Name: " + jsonObject.getString("name"));
                            username.setText("Username: " + jsonObject.getString("username"));
                            email.setText("Email: " + jsonObject.getString("email"));
                            cnic.setText("CNIC: " + jsonObject.getString("cnic"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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

    public void getDriverDetails() {

        String url = "http://asfand.danglingpixels.com/taxi/app/get_driver.php?user_id=" + logged_in;

        //showing dialog box
        final ProgressDialog progressDialog = new ProgressDialog(this.getActivity(),
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Updating...");
        progressDialog.show();


        // Request a string response
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        // Result handling
                        System.out.println(response);
                        progressDialog.dismiss();

                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            name.setText("Name: " + jsonObject.getString("name"));
                            username.setText("License: " + jsonObject.getString("license"));
                            email.setText("Mobile: " + jsonObject.getString("mobile"));
                            cnic.setText("");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
