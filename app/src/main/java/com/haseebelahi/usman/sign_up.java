package com.haseebelahi.usman;

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

import java.util.HashMap;
import java.util.Map;

public class sign_up extends AppCompatActivity {

    private EditText fname, u_id, password, passwordC, email, contact, cnic, age;
    private Button signup_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        fname = (EditText) findViewById(R.id.input_fname);

        u_id = (EditText) findViewById(R.id.input_id);
        password = (EditText) findViewById(R.id.input_password);
        passwordC = (EditText) findViewById(R.id.input_passwordC);

        email = (EditText) findViewById(R.id.input_email);
        contact = (EditText) findViewById(R.id.input_phone);
        cnic = (EditText) findViewById(R.id.input_cnic);

        /*age added*/
        age = (EditText) findViewById(R.id.input_age);

        signup_button = (Button) findViewById(R.id.btn_signup);

        signup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    //continue with sign up logic
                    registerUser();
                    Toast.makeText(getBaseContext(), "You have been registered successfully", Toast.LENGTH_LONG).show();

                }
            }
        });
    }


    private void registerUser() {

        String url = "http://asfand.danglingpixels.com/taxi/app/sign_up_taxi.php";

        //showing dialog box
        final ProgressDialog progressDialog = new ProgressDialog(sign_up.this,
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
                        progressDialog.dismiss();
                        //Toast.makeText(getApplicationContext(), "SUCCESS: "+response, Toast.LENGTH_LONG).show();
                        if(!response.equals("-1"))
                        {
                            SharedPreferences userLogin = getApplicationContext().getSharedPreferences("user_login", 0);
                            SharedPreferences.Editor editor = userLogin.edit();
                            editor.putString("logged_in", response);
                            editor.putString("logOut", "");
                            editor.apply();
                            //startNextActivity();
                            Intent intent = new Intent(getBaseContext(), home.class);
                            startActivity(intent);
                            finish();
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "Username already exists!", Toast.LENGTH_LONG).show();
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
                params.put("name",fname.getText().toString());
                params.put("username",u_id.getText().toString());
                params.put("pass",password.getText().toString());
                params.put("email",email.getText().toString());
                params.put("mobile",contact.getText().toString());
                params.put("cnic",cnic.getText().toString());
                /*age added*/
                params.put("age",age.getText().toString());
                return params;
            }
        };
        // Add the request to the queue
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        Volley.newRequestQueue(this).add(stringRequest);
    }

    private boolean validate() {
        boolean valid = true;

        if (fname.getText().toString().isEmpty()) {
            fname.setError("First Name is a required field");
            valid = false;
        } else {
            fname.setError(null);
        }
        if (u_id.getText().toString().isEmpty()) {
            u_id.setError("User Id is a required field");
            valid = false;
        } else {
            u_id.setError(null);
        }

        if(password.getText().toString().isEmpty()) {
            password.setError("Password is a required field");
            valid = false;
        }
        else {
            password.setError(null);
        }

        if(passwordC.getText().toString().isEmpty()) {
            passwordC.setError("Confirm Password is a required field");
            valid = false;
        }
        else {
            passwordC.setError(null);
        }

        if((!passwordC.getText().toString().isEmpty()) && (!password.getText().toString().isEmpty())) {
            if(password.getText().toString().length() < 6)
            {
                password.setError("Password length should not be less than 6 characters");
                valid = false;
            }
            if(!password.getText().toString().equals(passwordC.getText().toString()))
            {
                Toast.makeText(getBaseContext(), "Passwords do not match", Toast.LENGTH_LONG).show();
                valid = false;
            }
        }
        else {
            passwordC.setError(null);
        }

        if(email.getText().toString().isEmpty()) {
            email.setError("Email is a required field.");
            valid = false;
        }
        else {
            email.setError(null);
        }

        //other required fields can be added too similarly.

        return valid;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign_up, menu);
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
