package com.example.pfdhelpinghand;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {


        int reqCode = intent.getIntExtra("alarmid",1);
        String medName = intent.getStringExtra("medname");

        // keep
        Intent newIntent = new Intent(context, CountdownDialog.class);
        newIntent.putExtra("medname", medName);
        newIntent.putExtra("reqCode", reqCode);
        newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);


        context.startActivity(newIntent);

    }
}
