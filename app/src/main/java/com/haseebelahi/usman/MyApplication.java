package com.haseebelahi.usman;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

/**
 * Created by Haseeb Elahi on 5/14/2016.
 */
public class MyApplication extends MultiDexApplication {

    private double location_long, location_lat;
    private String user_id;

    public double getLocation_long()
    {
        return location_long;
    }
    public double getLocation_lat()
    {
        return location_lat;
    }
    public String getUser_id()
    {
        return user_id;
    }
    public void setLocation(double locationLong, double locationLat)
    {
        location_long = locationLong;
        location_lat = locationLat;
    }
    public void setUser_id(String id)
    {
        user_id = id;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(this);
    }
}

