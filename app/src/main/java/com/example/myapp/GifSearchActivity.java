package com.example.myapp;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.VideoView;

import org.json.JSONArray;
import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


public class GifSearchActivity extends AppCompatActivity {

    static final String API_KEY = "z8vmmh70ACIXBL89eBiQZ5PxNPwKWh9V";
    static final String errorGif = "https://media.giphy.com/media/yIxNOXEMpqkqA/giphy.mp4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_gif);

        final TextView errorText = findViewById(R.id.errorText);

        Intent intent = getIntent();
        String message = intent.getStringExtra(GiffActivity.EXTRA_MESSAGE);
        String searchText = message.replaceAll(" ", "%20");
        TextView textView= findViewById(R.id.textView);
        textView.setText(message);

        // Note: Use webview to show gifs instead of mp4s.
        final VideoView videoView = findViewById(R.id.videoView);
        videoView.setVideoPath("https://media.giphy.com/media/3oEjI6SIIHBdRxXI40/giphy.mp4");
        videoView.start();
        videoView.canPause();
        videoView.canSeekBackward();
        videoView.canSeekForward();
        MediaPlayer.OnCompletionListener listener = new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                videoView.seekTo(0);
                videoView.start();
            }
        };
        videoView.setOnCompletionListener(listener);

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String giphyURL = "https://api.giphy.com/v1/gifs/search?api_key=" + API_KEY + "&q=" + searchText + "&limit=1";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, giphyURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Display the first 500 characters of the response string.
                videoView.setVideoPath(getGiphyUrl(response, errorText));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                videoView.setVideoPath(errorGif);
                errorText.setText("Whoops, something went wrong!");
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }


    static String getGiphyUrl(String JSONResponse, TextView errorText) {
        try {
            JSONObject json = new JSONObject(JSONResponse);
            JSONArray data = json.getJSONArray("data");
            if (data.length() > 0) {
                String gifUrl = data.getJSONObject(0).getJSONObject("images").getJSONObject("original").getString("mp4");
                return gifUrl;
            }
            errorText.setText("Please enter a valid search term");
            return errorGif;

        }
        catch (Exception e) {
            errorText.setText(e.toString());
            return errorGif;
        }
    }
}
