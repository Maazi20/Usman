package com.haseebelahi.usman;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.HashMap;
import java.util.Map;

import static java.security.AccessController.getContext;

public class ChangePassword extends AppCompatActivity {

    private EditText curr_pass, new_pass, new_pass_r;
    String logged_in, driver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        logged_in = getSharedPreferences("user_login", 0).getString("logged_in", "");
        driver = getSharedPreferences("user_login",0).getString("driver","");

        curr_pass = (EditText) findViewById(R.id.curr_pass);
        new_pass = (EditText) findViewById(R.id.new_pass);
        new_pass_r = (EditText) findViewById(R.id.new_pass_r);



        Button change_pass_btn = (Button) findViewById(R.id.change_pass_btn);
        change_pass_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String s1 = new_pass.getText().toString();
                String s2 = new_pass_r.getText().toString();

                if (s1.equals(s2)) {
                    if (s1.length() >= 6) {

                        checkOld();

                    } else {
                        Toast.makeText(getApplicationContext(), "password too short", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Password mismatch", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    public boolean update_password() {

        String url = "";
        if(driver.equals("yes")) {
            url = "http://asfand.danglingpixels.com/taxi/app/changePassDriver.php";
        }
        else {
            url = "http://asfand.danglingpixels.com/taxi/app/changePass.php";
        }
        // Request a string response
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        // Result handling
                        System.out.println(response);
                        //text.setText(response);

                        //Toast.makeText(getApplicationContext(), "SUCCESS: "+response, Toast.LENGTH_LONG).show();
                        if(!response.equals("-1"))
                        {
                            finish();
                            Toast.makeText(getApplicationContext(), "Suceess change pass", Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_LONG).show();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                // Error handling
                System.out.println("Something went wrong!");
                error.printStackTrace();

                Toast.makeText(getApplicationContext(), "FAILED TO CONNECT",Toast.LENGTH_LONG).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();

                params.put("username", logged_in);
                params.put("newpass" , new_pass.getText().toString());
                return params;
            }
        };
        // Add the request to the queue
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        Volley.newRequestQueue(this).add(stringRequest);

        return true;
    }

    public void checkOld() {

        String url = "";
        if(driver.equals("yes")) {
            url = "http://asfand.danglingpixels.com/taxi/app/checkOldPassDriver.php";
        }
        else {
            url = "http://asfand.danglingpixels.com/taxi/app/checkOldPass.php";
        }
        //showing dialog box
        final ProgressDialog progressDialog = new ProgressDialog(ChangePassword.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Connecting...");
        progressDialog.show();


        // Request a string response
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        // Result handling
                        System.out.println(response);
                        //text.setText(response);

                        //Toast.makeText(getApplicationContext(), "SUCCESS: "+response, Toast.LENGTH_LONG).show();
                        if(!response.equals("-1"))
                        {
                            update_password();
                            progressDialog.dismiss();
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "Previous Password is incorrect!", Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }

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

                params.put("username", logged_in);
                params.put("password", curr_pass.getText().toString());

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflaate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_change_password, menu);
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