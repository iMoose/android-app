package com.example.myapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class SongActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.myapp.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_song);
        setTitle("Enter Song/Video");
    }

    /** Called when the user taps the send button */
    public void sendMessage(View view) {
        Intent intent = new Intent(this, SongSearchActivity.class);
        EditText editText = findViewById(R.id.editText);
        String songName = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, songName);
        startActivity(intent);
    }
}
