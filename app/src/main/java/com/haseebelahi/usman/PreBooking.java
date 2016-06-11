package com.haseebelahi.usman;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class PreBooking extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private Toolbar toolbar;

    @Override
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {


    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_booking);

        final Spinner s1 = (Spinner) findViewById(R.id.spinner);
        final Spinner s2 = (Spinner) findViewById(R.id.spinner2);
        final Spinner s3 = (Spinner) findViewById(R.id.spinner3);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.Genders, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.Service, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(this,
                R.array.Type, android.R.layout.simple_spinner_item);

// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner

        s1.setAdapter(adapter);
        s2.setAdapter(adapter2);
        s3.setAdapter(adapter3);
        Button b1 = (Button) findViewById(R.id.button);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String gender =  s1.getSelectedItem().toString();
                String Type = s2.getSelectedItem().toString();
                String service = s3.getSelectedItem().toString();

                //Toast.makeText(getBaseContext(),gender+driver+Vehicle,Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getBaseContext(), AddBookingMap.class).putExtra("gender",gender).putExtra("Type",service).putExtra("service",Type));
                finish();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
