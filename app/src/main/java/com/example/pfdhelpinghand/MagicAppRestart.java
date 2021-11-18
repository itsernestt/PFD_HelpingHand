package com.example.pfdhelpinghand;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class MagicAppRestart extends MainActivity {
    // Do not forget to add it to AndroidManifest.xml
    // <activity android:name="your.package.name.MagicAppRestart"/>
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.exit(0);
    }
    public static void doRestart(Activity anyActivity) {
        anyActivity.startActivity(new Intent(anyActivity.getApplicationContext(), MagicAppRestart.class));
    }
}
