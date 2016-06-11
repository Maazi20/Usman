package com.haseebelahi.usman;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class home extends AppCompatActivity {



    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    FragmentManager mFragmentManager;
    FragmentTransaction mFragmentTransaction;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);



        /**
         *Setup the DrawerLayout and NavigationView
         */

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mNavigationView = (NavigationView) findViewById(R.id.navigationview) ;

        /**
         * Lets inflate the very first fragment
         * Here , we are inflating the TabFragment as the first Fragment
         */




        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();

        SharedPreferences userLogin = getApplicationContext().getSharedPreferences("user_login", 0);
        String logged_in = userLogin.getString("logged_in", "");
        String driver = userLogin.getString("driver","");
        if(driver.equals("yes")){
            DriversBookingFragment fragment = (DriversBookingFragment) mFragmentManager.findFragmentById(R.id.containerView);
            if(fragment != null) {
                mFragmentTransaction.remove(fragment);
            }
            mFragmentTransaction.replace(R.id.containerView,new DriversBookingFragment());
        }

        else {
            BookingsFragment fragment = (BookingsFragment) mFragmentManager.findFragmentById(R.id.containerView);
            if(fragment != null) {
                mFragmentTransaction.remove(fragment);
            }
                mFragmentTransaction.replace(R.id.containerView, new BookingsFragment());

        }
        mFragmentTransaction.commit();


        /**
         * Setup click events on the Navigation View Items.
         */

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {


            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                mDrawerLayout.closeDrawers();

                SharedPreferences userLogin = getApplicationContext().getSharedPreferences("user_login", 0);
                String logged_in = userLogin.getString("logged_in", "");
                String driver = userLogin.getString("driver","");

                if (menuItem.getItemId() == R.id.nav_item_sent) {
                    FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                    fragmentTransaction.addToBackStack(null);
                    Fragment fragment = mFragmentManager.findFragmentById(R.id.containerView);
                    if(fragment != null) {
                        mFragmentTransaction.remove(fragment);
                    }
                    fragmentTransaction.replace(R.id.containerView,new UserProfile()).commit();

                }

                if (menuItem.getItemId() == R.id.nav_item_inbox) {
                    if(driver.equals("yes"))
                    {
                        FragmentTransaction xfragmentTransaction = mFragmentManager.beginTransaction();
                        xfragmentTransaction.addToBackStack(null);
                        Fragment fragment = mFragmentManager.findFragmentById(R.id.containerView);
                        if(fragment != null) {
                            mFragmentTransaction.remove(fragment);
                        }
                        xfragmentTransaction.replace(R.id.containerView, new DriversBookingFragment()).commit();
                    }
                    else {
                        FragmentTransaction xfragmentTransaction = mFragmentManager.beginTransaction();
                        xfragmentTransaction.addToBackStack(null);
                        Fragment fragment = mFragmentManager.findFragmentById(R.id.containerView);
                        if(fragment != null) {
                            mFragmentTransaction.remove(fragment);
                        }
                        xfragmentTransaction.replace(R.id.containerView, new BookingsFragment()).commit();
                    }

                }

                if(menuItem.getItemId() == R.id.nav_item_history) {
                    FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                    fragmentTransaction.addToBackStack(null);
                    Fragment fragment = mFragmentManager.findFragmentById(R.id.containerView);
                    if(fragment != null) {
                        mFragmentTransaction.remove(fragment);
                    }
                    if(driver.equals("yes")) {
                        fragmentTransaction.replace(R.id.containerView, new DriverHistory()).commit();
                    }
                    else {
                        fragmentTransaction.replace(R.id.containerView, new BookingsHistory()).commit();
                    }
                }

                if(menuItem.getItemId() == R.id.contact_us) {
                    FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                    fragmentTransaction.addToBackStack(null);
                    Fragment fragment = mFragmentManager.findFragmentById(R.id.containerView);
                    if(fragment != null) {
                        mFragmentTransaction.remove(fragment);
                    }
                    fragmentTransaction.replace(R.id.containerView,new SuggestionsFragment()).commit();
                }

                if(menuItem.getItemId() == R.id.logout) {
                    logOut();
                }

                return false;
            }

        });

        /**
         * Setup Drawer Toggle of the Toolbar
         */

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        if(!driver.equals("yes")){
            setSupportActionBar(toolbar);
        }
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout, toolbar,R.string.app_name,
                R.string.app_name);

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mDrawerToggle.syncState();




    }

    private void logOut()
    {
        SharedPreferences userLogin = getSharedPreferences("user_login",MODE_PRIVATE);
        SharedPreferences.Editor editor = userLogin.edit();
        editor.putString("logged_in","");
        editor.putString("driver","");
        if((userLogin.getString("status","")).equals("working"))
        {
            editor.putString("status","end");
        }
        editor.apply();
        Intent intent  = new Intent(home.this, login.class);
        startActivity(intent);
        finish();
    }


    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            confirm_dialog();
        } else {
            getFragmentManager().popBackStack();
        }
    }


    private void confirm_dialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Exit");
        builder.setMessage("Are you sure you want to exit?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.add_booking) {
            startActivity(new Intent(this, PreBooking.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
