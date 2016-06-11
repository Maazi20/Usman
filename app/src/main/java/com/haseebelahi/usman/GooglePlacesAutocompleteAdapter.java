package com.haseebelahi.usman;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by Haseeb Elahi on 5/28/2016.
 */



public class GooglePlacesAutocompleteAdapter extends ArrayAdapter<GooglePlace> implements Filterable {

    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY = "AIzaSyBOlMzlTJxyiK4-RYEfkBllEnkrctmuPOU";
    private Context context;


    private ArrayList<GooglePlace> resultList;
    public GooglePlacesAutocompleteAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        this.context = context;
    }

    @Override
    public int getCount() {
        return resultList.size();
    }

    @Override
    public GooglePlace getItem(int index) {
        return resultList.get(index);
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    // Retrieve the autocomplete results.
                    resultList = autocomplete(constraint.toString());

                    // Assign the data to the FilterResults
                    filterResults.values = resultList;
                    filterResults.count = resultList.size();
                }

                return filterResults;
            }
            @Override
            protected void publishResults(CharSequence constraint, Filter.FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        GooglePlace g = resultList.get(position);

        if(convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.auto_complete, parent, false);
        }

        TextView desc = (TextView) convertView.findViewById(R.id.desc);
        desc.setText(g.getDesc());

        return convertView;
    }


    public static ArrayList<GooglePlace> autocomplete(String input) {
        ArrayList<GooglePlace> resultList = null;
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?input=" + URLEncoder.encode(input, "utf8"));
            sb.append("&sensor=" +"false");
            sb.append("&key=" + API_KEY);
            URL url = new URL(sb.toString());
            System.out.println("URL: "+url);
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {

                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            //Log.e("MapsActivity", "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
            //Log.e("MapsActivity", "Error connecting to Places API", e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {

            // Create a JSON object hierarchy from the results
            System.out.println(jsonResults.toString());
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList = new ArrayList<GooglePlace>(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                System.out.println(predsJsonArray.getJSONObject(i).getString("description"));
                System.out.println("============================================================");
                resultList.add(new GooglePlace(predsJsonArray.getJSONObject(i).getString("description"),predsJsonArray.getJSONObject(i).getString("place_id")));
            }
        } catch (JSONException e) {
            //Log.e("MapsActivity", "Cannot process JSON results", e);
        }

        return resultList;
    }

}


