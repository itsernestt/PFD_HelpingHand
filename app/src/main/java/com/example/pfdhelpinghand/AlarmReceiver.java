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
        if (intent.hasExtra("medname")){

            int reqCode = intent.getIntExtra("alarmid",1);
            String medName = intent.getStringExtra("medname");

            Intent newIntent = new Intent(context, CountdownDialog.class);
            newIntent.putExtra("medname", medName);
            newIntent.putExtra("reqCode", reqCode);
            newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            context.startActivity(newIntent);

        }else if (intent.hasExtra("apptname")){

            int reqCode = intent.getIntExtra("alarmid",100);
            String apptName = intent.getStringExtra("apptname");

            Intent newIntent = new Intent(context, CountdownDialog.class);
            newIntent.putExtra("apptname", apptName);
            newIntent.putExtra("reqCode", reqCode);
            newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            context.startActivity(newIntent);
        }
    }
}
