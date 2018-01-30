package com.example.myapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.util.LruCache;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class SongSearchActivity extends AppCompatActivity {

    static final int MAX_RESULTS = 20;

    // Youtube
    static final String API_KEY = "AIzaSyAaAMAy4nOXI5bdtRTPzHHTblPDSMVwwEs";
    static final String INVALID_SEARCH = "http://www.clker.com/cliparts/0/7/e/a/12074327311562940906milker_X_icon.svg.hi.png";

    // Spotify
    static final String basicAuth64 = "NWUzZmJjNTc0YzNjNDQ3MDg5YmNjMzJmODEyOGRlYWM6OTA4ZThiN2UwYjAyNGY3OWIzMzJjOGM4Y2Y1MjBiMzY=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Set layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_song);

        // Get the search term
        Intent intent = getIntent();
        String message = intent.getStringExtra(GiffActivity.EXTRA_MESSAGE);
        String searchText = message.replaceAll(" ", "%20");

        // Display the search term
        setTitle(message);

        // Get items from layout
        final ListView listYouTube = findViewById(R.id.listYouTube);
        final ListView listSpotify = findViewById(R.id.listSpotify);
        final ToggleButton toggleView = findViewById(R.id.toggleView);

        // Configure toggling between YouTube and Spotify
        toggleView.setText("");
        toggleView.setTextOn("");
        toggleView.setTextOff("");

        toggleView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    // Button is on
                    listYouTube.setVisibility(View.INVISIBLE);
                    listSpotify.setVisibility(View.VISIBLE);
                }
                else {
                    // Button is off
                    listYouTube.setVisibility(View.VISIBLE);
                    listSpotify.setVisibility(View.INVISIBLE);
                }
            }
        });

        // Calculate max size of cache
        final int maxMemory = (int) Runtime.getRuntime().maxMemory() / 1024;
        final int cacheSize = maxMemory / 8;

        // Information holders for YouTube
        final ArrayList<String> videoDescriptions =new ArrayList<>();
        final ArrayList<String> youTubeImageURLS = new ArrayList<>();
        final ArrayList<String> videoIDS = new ArrayList<>();
        final LruCache<String, Bitmap> youTubeCache = new LruCache<>(cacheSize);
        final LruCache<String, Bitmap> spotifyCache = new LruCache<>(cacheSize);

        // Information holders for Spotify
        final ArrayList<String> songDescriptions =new ArrayList<>();
        final ArrayList<String> spotifyImageURLS = new ArrayList<>();
        final ArrayList<String> spotifyURLS = new ArrayList<>();


        // Adapters for holding the information for YouTube and Spotify
        final CustomList youTubeAdapter = new CustomList(SongSearchActivity.this, videoDescriptions, youTubeImageURLS, videoIDS, youTubeCache);
        final CustomList spotifyAdapter = new CustomList(SongSearchActivity.this, songDescriptions, spotifyImageURLS, spotifyURLS, spotifyCache);

        // Setting adapters to listViews so they display the right information
        listYouTube.setAdapter(youTubeAdapter);
        listSpotify.setAdapter(spotifyAdapter);

        // Set action listeners for clicking on an item in the listView
        listYouTube.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int itemPosition, long location) {
                String itemValue = videoIDS.get(itemPosition);
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getYoutubeURL(itemValue))));
            }
        });

        listSpotify.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int itemPosition, long location) {
                String itemValue = spotifyURLS.get(itemPosition);
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(itemValue)));
            }
        });

        // YouTube request URL
        String requestYouTubeURL = "https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults=" + MAX_RESULTS + "&type=video&q=" + searchText + "&key=" + API_KEY;

        // Setting up Spotify request URL
        String[] requestAccessTokenHeader = {"Authorization", "Basic " + basicAuth64};
        final String[] accessToken = {""};
        String requestAccessTokenURL = "https://accounts.spotify.com/api/token";
        String requestAccessTokenBody = "grant_type=client_credentials";

        String requestSpotifyURL = "https://api.spotify.com/v1/search?q=" + searchText + "&type=track";
        final String[] requestSpotifyHeader = {"Authorization",""};

        // Instantiate the RequestQueue.
        final RequestQueue queue = Volley.newRequestQueue(this);

        // Request a string response from the provided URL.
        StringRequest youTubeStringRequest = new StringRequest(Request.Method.GET, requestYouTubeURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    JSONArray items = json.getJSONArray("items");
                    int items_length = items.length();
                    if (items_length == 0) {
                        throw new Exception("No search results");
                    }
                    String[] tempString;
                    for(int i = 0; i < items_length; i++) {
                        tempString = getVideoInfo(items.getJSONObject(i));
                        videoIDS.add(tempString[0]);
                        videoDescriptions.add(tempString[1]);
                        youTubeImageURLS.add(tempString[2]);
                    }
                }
                catch (Exception e) {
                    videoIDS.add("-1");
                    videoDescriptions.add("No search results");
                    youTubeImageURLS.add(INVALID_SEARCH);
                }
                youTubeAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                videoIDS.add("-1");
                videoDescriptions.add(error.toString());
                youTubeImageURLS.add(INVALID_SEARCH);
            }
        });

        final StringRequestWithCookies spotifySearchStringRequest = new StringRequestWithCookies(requestSpotifyURL, requestSpotifyHeader, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    JSONArray items = json.getJSONObject("tracks").getJSONArray("items");
                    int items_length = items.length();
                    if (items_length == 0) {
                        throw new Exception("No search results");
                    }
                    String[] tempString;
                    for(int i = 0; i < items_length; i++) {
                        tempString = getSpotifyInfo(items.getJSONObject(i));
                        spotifyURLS.add(tempString[0]);
                        songDescriptions.add(tempString[1]);
                        spotifyImageURLS.add(tempString[2]);
                    }
                } catch (Exception e) {
                    spotifyURLS.add("-1");
                    songDescriptions.add(response.toString());
                    spotifyImageURLS.add(INVALID_SEARCH);
                }
                spotifyAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                spotifyURLS.add("-1");
                songDescriptions.add(error.toString());
                spotifyImageURLS.add(INVALID_SEARCH);
            }
        });

        StringRequestWithCookiesBody spotifyAccessTokenStringRequest = new StringRequestWithCookiesBody(requestAccessTokenURL, requestAccessTokenHeader, requestAccessTokenBody, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    accessToken[0] = json.getString("access_token");
                    requestSpotifyHeader[1] = "Bearer " + accessToken[0];
                    queue.add(spotifySearchStringRequest);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(),e.toString(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString(), Toast.LENGTH_LONG).show();
            }
        });


        // Add the request to the RequestQueue.
        queue.add(spotifyAccessTokenStringRequest);
        queue.add(youTubeStringRequest);
    }
    static String getYoutubeURL(String videoID) {
        return "https://www.youtube.com/watch?v=" + videoID;
    }
    static String[] getVideoInfo(JSONObject item) {
        String[] toReturn = new String[3];
        toReturn[0] = "Video ID";
        toReturn[1] = "Video Title";
        toReturn[2] = "Video Thumbnail";
        try {
            toReturn[0] = item.getJSONObject("id").getString("videoId");
            JSONObject snippet = item.getJSONObject("snippet");
            toReturn[1] = snippet.getString("title");
            toReturn[2] = snippet.getJSONObject("thumbnails").getJSONObject("default").getString("url");
        }
        catch (Exception e) {
            // Something went wrong - Most likely invalid search request
            toReturn[1] = e.toString();
        }
        return toReturn;
    }

    static String[] getSpotifyInfo(JSONObject item) {
        String[] toReturn = new String[3];
        toReturn[0] = "Spotify URL";
        toReturn[1] = "Song Title";
        toReturn[2] = "Song Thumbnail";
        try {
            toReturn[0] = item.getString("uri");
            toReturn[1] = item.getString("name");

            // Get the name of all the artists and format it properly
            JSONArray artists = item.getJSONArray("artists");
            int artists_length = artists.length();
            for (int i = 0; i < artists_length; i++) {
                toReturn[1] += ", " + artists.getJSONObject(i).getString("name");
            }

            // Get the thumbnail for the album which the song is part of
            toReturn[2] = item.getJSONObject("album").getJSONArray("images").getJSONObject(0).getString("url");
        }
        catch (Exception e) {
            toReturn[1] = e.toString();
        }
        return toReturn;
    }
}
