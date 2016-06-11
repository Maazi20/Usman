package com.haseebelahi.usman;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Haseeb Elahi on 4/12/2016.
 */
public class BookingAdapter extends ArrayAdapter<Booking> {
    Context context;
    int layoutResourceId;
    ArrayList<Booking> data = new ArrayList<Booking>();

    public BookingAdapter(Context context, int layoutResourceId,
                             ArrayList<Booking> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Booking t = data.get(position);

        if(convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(layoutResourceId, parent, false);
        }

        TextView driver = (TextView) convertView.findViewById(R.id.driver);
        driver.setText("Driver Request Pending");
        if(!t.getDriver().equals("")) {
            driver.setText("Driver: " + t.getDriver());
        }

        TextView booking_id = (TextView) convertView.findViewById(R.id.id);
        booking_id.setText(t.getId());

        TextView distance = (TextView) convertView.findViewById(R.id.distance);
        float fare = Float.parseFloat(t.getFare());
        float distance_f = (fare-50) / 13;
        distance.setText("Estimate Distance: "+ String.valueOf(distance_f)+ " Km");

        if(t.getService().equals("Rent") || t.getService().equals("Shopping"))
            distance.setText(t.getService() + "@13rs/Km");

        final TextView source_dest = (TextView) convertView.findViewById(R.id.fromto);
        source_dest.setText("From " + t.getSourceAddress() + " to "+ t.getDestAddress());

        TextView status = (TextView) convertView.findViewById(R.id.status);
        String status_s = t.getStatus();
        SharedPreferences user = context.getSharedPreferences("user_login", 0);
        String user_type = user.getString("driver","no");
        if(status_s.equals("0")) {
            if (user_type.equals(""))
                status.setText("Request Pending");
            else
                status.setText("Available");
        }
        else if(status_s.equals("1")) {
            status.setText("Currently Active");
        }
        else if(status_s.equals("2")) {
            if(user_type.equals("yes"))
                status.setText("User Confirmation Pending");
            else
                status.setText("Marked Complete by Driver");
        }
        /*
        else if(status_s.equals("3")){
            if(user_type.equals("no"))
                status.setText("Driver Confirmation Pending");
            else
                status.setText("Marked Complete by User");
        }*/
        else {
            status.setText("Completed");
        }

        TextView service = (TextView) convertView.findViewById(R.id.service);
        service.setText("Service: " + t.getService());

        /*final String[] src = new String[1];
        final String[] dest = new String[1];
        AsyncTask<Void,Void,Void> task = new AsyncTask<Void,Void,Void>(){

            protected void onPreExecute() {
                source_dest.setText("Getting Locations..");
            }
            protected Void doInBackground(Void... params){


                try {
                    src[0] = getAddress(t.getSource());
                    dest[0] = getAddress(t.getDestination());
                } catch(Exception ex) {
                    ex.printStackTrace();
                }

                return null;
            }

            protected void onPostExecute(Void result){
                source_dest.setText("From " + src[0] + " to " + dest[0]);
            }
        };


        task.execute();*/

        return convertView;

    }

    /*private String getAddress(String latLng) {
        String address = "";
        Geocoder geocoder;
        geocoder = new Geocoder(getContext(), Locale.getDefault());
        List<Address> addressList = null;
        Double lat = Double.parseDouble(latLng.substring(0, latLng.indexOf(",") - 1));
        Double lang = Double.parseDouble(latLng.substring(latLng.indexOf(",")+1));
        try {
            addressList = geocoder.getFromLocation(lat, lang, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addressList != null) {
            address = addressList.get(0).getAddressLine(0);
        }
        return address;
    }*/

}