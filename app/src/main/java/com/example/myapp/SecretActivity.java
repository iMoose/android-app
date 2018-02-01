package com.example.myapp;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class SecretActivity extends AppCompatActivity {

    private static final String correctNumber = "646278";

    private String currentInput = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secret);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_secret, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                Toast.makeText(this, "Reset", Toast.LENGTH_SHORT).show();
                currentInput = "";
                break;
            default:
                break;
        }

        return true;
    }

    public void onClick( View v ) {
        switch (v.getId()) {
            case (R.id.button1):
                clickNumber("1");
                break;
            case (R.id.button2):
                clickNumber("2");
                break;
            case (R.id.button3):
                clickNumber("3");
                break;
            case (R.id.button4):
                clickNumber("4");
                break;
            case (R.id.button5):
                clickNumber("5");
                break;
            case (R.id.button6):
                clickNumber("6");
                break;
            case (R.id.button7):
                clickNumber("7");
                break;
            case (R.id.button8):
                clickNumber("8");
                break;
            case (R.id.button9):
                clickNumber("9");
                break;
        }}

    public void clickNumber(String number) {
        currentInput += number;
        if (currentInput.equals(correctNumber)) {
            onCorrect();
        }
    }

    public void onCorrect() {
        Intent[] intents = new Intent[3];
        // Note: Put packages that have higher load times at the top(low index) so their load happens first and initialises before the sleep as some can't initialise between for some reason

        // Increases time by ~5s
        intents[0] = this.getPackageManager().getLaunchIntentForPackage("com.laurencedawson.reddit_sync.pro");
        intents[1] = this.getPackageManager().getLaunchIntentForPackage("com.lara.android.youtube");
        intents[2] = this.getPackageManager().getLaunchIntentForPackage("com.twitter.android");

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
        finish();
    }
}
