package com.haseebelahi.usman;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by Haseeb Elahi on 4/19/2016.
 */
public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Intent i = new Intent(context, MyService.class);
        context.getApplicationContext().startService(i);
        Toast.makeText(context.getApplicationContext(), "IN ALARAM RECEIVER", Toast.LENGTH_LONG).show();
    }
}
