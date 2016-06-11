package com.haseebelahi.usman;


import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class SuggestionsFragment extends Fragment {

    EditText fname, lname, email, phoneNum, address, sugg;

    public SuggestionsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_suggestions, container, false);

        fname = (EditText) v.findViewById(R.id.input_fname);
        lname = (EditText) v.findViewById(R.id.input_lname);
        email = (EditText) v.findViewById(R.id.input_email);
        phoneNum = (EditText) v.findViewById(R.id.input_phone);
        address = (EditText) v.findViewById(R.id.input_address);
        sugg = (EditText) v.findViewById(R.id.input_suggestion);


        Button submit = (Button) v.findViewById(R.id.btn_submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate())
                    submit_response();
            }
        });


        return v;
    }

    private void submit_response() {
        String url = "http://asfand.danglingpixels.com/taxi/app/insert_sugg.php";

        //showing dialog box
        final ProgressDialog progressDialog = new ProgressDialog(getActivity(),
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Sending...");
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
                            Toast.makeText(getActivity(),"Suggestion Sent",Toast.LENGTH_LONG).show();
                            fname.setText("");
                            lname.setText("");
                            email.setText("");
                            phoneNum.setText("");
                            address.setText("");
                            sugg.setText("");
                        }
                        else
                        {
                            Toast.makeText(getActivity(), "INVALID ID OR PASSWORD", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                // Error handling
                System.out.println("Something went wrong!");
                error.printStackTrace();
                progressDialog.dismiss();
                Toast.makeText(getActivity(), "FAILED TO CONNECT",Toast.LENGTH_LONG).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("fname",fname.getText().toString());
                params.put("lname",lname.getText().toString());
                params.put("email",email.getText().toString());
                params.put("phoneNum",phoneNum.getText().toString());
                params.put("address",address.getText().toString());
                params.put("sugg",sugg.getText().toString());
                return params;
            }
        };
        // Add the request to the queue
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        Volley.newRequestQueue(getActivity()).add(stringRequest);
    }

    public boolean validate() {
        boolean valid = true;

        if (fname.getText().toString().isEmpty()) {
            fname.setError("First Name is a required field");
            valid = false;
        } else {
            fname.setError(null);
        }
        if (lname.getText().toString().isEmpty()) {
            lname.setError("Last Name is a required field");
            valid = false;
        } else {
            lname.setError(null);
        }
        if (email.getText().toString().isEmpty()) {
            email.setError("Email is a required field");
            valid = false;
        } else {
            email.setError(null);
        }
        if (phoneNum.getText().toString().isEmpty()) {
            phoneNum.setError("Phone Number is a required field");
            valid = false;
        } else {
            phoneNum.setError(null);
        }
        if (sugg.getText().toString().isEmpty()) {
            sugg.setError("Suggestion is a required field");
            valid = false;
        } else {
            sugg.setError(null);
        }


        return valid;
    }


}
