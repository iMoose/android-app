package com.example.myapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class GiffActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.myapp.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_gif);
        setTitle("Enter search");
    }

    /** Called when the user taps the send button */
    public void sendMessage(View view) {
        Intent intent = new Intent(this, GifSearchActivity.class);
        EditText editText = findViewById(R.id.editText);
        String gifName = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, gifName);
        startActivity(intent);
    }
}
