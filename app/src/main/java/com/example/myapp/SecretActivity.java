package com.example.myapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class SecretActivity extends AppCompatActivity {

    private static final String correctNumber = "646278";
    private static final String filePath = "/storage/emulated/0/Documents/fileList.txt";

    private String currentInput = "";

    private ArrayList<String> appList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secret);
        getAppList();
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

    public void getAppList() {
        boolean permissionGranted = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        if(!permissionGranted) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 200);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 200);
        }
        try {
            File file = new File(filePath);

            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                appList.add(line);
            }
            br.close() ;
        }catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public void onCorrect() {
        int numIntents = appList.size();

        for(int i = 0; i < numIntents; i++) {
            startActivity(this.getPackageManager().getLaunchIntentForPackage(appList.get(i)));
            try {
                Thread.sleep(1000);
            }
            catch (InterruptedException e) {
                System.out.println(e);
            }
        }
        finish();
    }
}
