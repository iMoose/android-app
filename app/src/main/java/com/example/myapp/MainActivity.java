package com.example.myapp;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.content.pm.PackageManager;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("My App");
    }

    public void gotoGif(View view) {
        Intent intent = new Intent(this, GiffActivity.class);
        startActivity(intent);
    }

    public void searchSongs(View view) {
        Intent intent = new Intent(this, SongActivity.class);
        startActivity(intent);
    }

    public void gotoPrivacy(View view) {
        startActivity(new Intent(Settings.ACTION_SETTINGS));
    }

    public boolean isPackageExisted(String targetPackage){
        PackageManager pm=getPackageManager();
        try {
            PackageInfo info=pm.getPackageInfo(targetPackage,PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
        return true;
    }

    public void loadBackground(View view) {
        Intent[] intents = new Intent[3];
        // Note: Put packages that have higher load times at the top(low index) so their load happens first and initialises before the sleep as some can't initialise between for some reason

        // Increases time by ~5s
        if (isPackageExisted("com.laurencedawson.reddit_sync.pro")) {
            intents[0] = this.getPackageManager().getLaunchIntentForPackage("com.laurencedawson.reddit_sync.pro");
        }
        else {
            Toast.makeText(getApplicationContext(),"Reddit already open", Toast.LENGTH_LONG).show();
        }
        if (isPackageExisted("com.lara.android.youtube")) {
            intents[1] = this.getPackageManager().getLaunchIntentForPackage("com.lara.android.youtube");
        }
        else {
            Toast.makeText(getApplicationContext(),"Youtube already open", Toast.LENGTH_LONG).show();
        }
        if (isPackageExisted("com.twitter.android")) {
            intents[2] = this.getPackageManager().getLaunchIntentForPackage("com.twitter.android");
        }
        else {
            Toast.makeText(getApplicationContext(),"Twitter already open", Toast.LENGTH_LONG).show();
        }

        int numIntents = intents.length;
        if (intents[0] != null)
            startActivity(intents[0]);

        for(int i = 1; i < numIntents; i++) {
            try {
                Thread.sleep(1000);
            }
            catch (InterruptedException e) {
                System.out.println(e);
            }
            if (intents[i] != null)
                startActivity(intents[i]);
        }
    }

    public void gotoSecret(View view) {
        Intent intent = new Intent(this, SecretActivity.class);
        startActivity(intent);
    }
}