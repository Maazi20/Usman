package com.haseebelahi.usman;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

import java.util.HashMap;
import java.util.Map;

public class login extends AppCompatActivity {

    EditText login_id,password;
    Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences userLogin = getApplicationContext().getSharedPreferences("user_login", 0);
        String logged_in = userLogin.getString("logged_in", "");
        if(!logged_in.equals(""))
        {
            startNextActivity();
        }
        setContentView(R.layout.activity_login);

        login_id = (EditText) findViewById(R.id.input_email);
        password = (EditText) findViewById(R.id.input_password);
        loginButton = (Button) findViewById(R.id.btn_login);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validate(login_id.getText().toString(), password.getText().toString())) {
                    if (isNetworkAvailable()) {
                        login(login_id.getText().toString(), password.getText().toString());
                    }
                }
            }
        });

        final TextView sign_up = (TextView) findViewById(R.id.sign_up);
        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(login.this, sign_up.class);
                startActivity(intent);
            }
        });
    }

    private void login(final String emp_id, final String password)
    {
        String url = "http://asfand.danglingpixels.com/taxi/app/login_taxi.php";

        //showing dialog box
        final ProgressDialog progressDialog = new ProgressDialog(login.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();


        // Request a string response
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        // Result handling
                        System.out.println(response);
                        //text.setText(response);
                        progressDialog.dismiss();
                        //Toast.makeText(getApplicationContext(), "SUCCESS: "+response, Toast.LENGTH_LONG).show();
                        if(!response.equals("-1"))
                        {
                            SharedPreferences userLogin = getApplicationContext().getSharedPreferences("user_login", 0);
                            SharedPreferences.Editor editor = userLogin.edit();
                            String split[] = response.split(",");
                            if(split[0].equals("driver")) {
                                editor.putString("driver", "yes");
                                editor.putString("logged_in", split[1]);
                            }
                            else {
                                editor.putString("logged_in", response);
                            }
                            editor.putString("logOut","");
                            editor.apply();
                            startNextActivity();
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "INVALID ID OR PASSWORD", Toast.LENGTH_LONG).show();
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
                params.put("login_id",emp_id);
                params.put("pass",password);
                return params;
            }
        };
        // Add the request to the queue
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void startNextActivity()
    {
        Intent intent = new Intent(login.this, home.class);
        finish();
        startActivity(intent);
        //Toast.makeText(this, "Logged In", Toast.LENGTH_LONG);
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public boolean validate(String emp_idS, String passwordS) {
        boolean valid = true;

        if (emp_idS.isEmpty()) {
            login_id.setError("enter a valid employee id");
            valid = false;
        } else {
            login_id.setError(null);
        }

        if (passwordS.isEmpty() || passwordS.length() < 4 || passwordS.length() > 10) {
            password.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            password.setError(null);
        }

        return valid;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
