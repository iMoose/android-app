package com.example.myapp;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
        final ArrayList<String> imageDescriptions =new ArrayList<>();
        final ArrayList<String> imageURLS = new ArrayList<>();
        final ArrayList<String> videoIDS = new ArrayList<>();

//        imageDescriptions.add("Testing please work");
//        imageURLS.add("https://www.sitebuilderreport.com/assets/facebook-stock-up-446fff24fb11820517c520c4a5a4c032.jpg");
//        videoIDS.add("Video id number");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_song);

        Intent intent = getIntent();
        String message = intent.getStringExtra(GiffActivity.EXTRA_MESSAGE);
        String searchText = message.replaceAll(" ", "%20");

        final TextView textView= findViewById(R.id.textView);
        textView.setText(message);

        final ListView listView = findViewById(R.id.list);

        final CustomList adapter = new CustomList(SongSearchActivity.this, imageDescriptions, imageURLS, videoIDS);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int itemPosition, long location) {
                String itemValue = videoIDS.get(itemPosition);
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getYoutubeURL(itemValue))));
            }
        });
        // Format layout

        // Instantiate the RequestQueue.
        final RequestQueue queue = Volley.newRequestQueue(this);
        String requestURL = "https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults=" + MAX_RESULTS + "&type=video&q=" + searchText + "&key=" + API_KEY;
        // String requestURL = "http://www.httpbin.org/get";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, requestURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Add a value to response
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
                        imageDescriptions.add(tempString[1]);
                        imageURLS.add(tempString[2]);
                    }
                }
                catch (Exception e) {
                    imageDescriptions.add("No search results");
                    imageURLS.add(INVALID_SEARCH);
                    videoIDS.add("-1");
                }
                adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

        String[] requestHeader2 = new String[2];
        final String[] accessToken = {"No"};
        String requestURL2 = "https://accounts.spotify.com/api/token";
        String requestBody = "grant_type=client_credentials";
        requestHeader2[0] = "Authorization";
        requestHeader2[1] = "Basic " + basicAuth64;

        String[] requestHeader = new String[2];
        // accessToken[0] = "BQD60P75hsJvTn8uaVpYAkp-pHPGaT01IZ76W_PkYKflRx4Y2f6GcjKjCpPr05y8mBnYwTtcOxAFs0VFTuQ";
        String requestURL1 = "https://api.spotify.com/v1/search?q=" + searchText + "&type=artist,album,track";
        requestHeader[0] = "Authorization";
        requestHeader[1] = "Bearer " + accessToken[0];

        final StringRequestWithCookies stringRequest1 = new StringRequestWithCookies(requestURL1, requestHeader, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    // TODO: This response is the search response from the API -> Format this into a listView
                    // Toast.makeText(getApplicationContext(),accessToken[0], Toast.LENGTH_LONG).show();
                    // Toast.makeText(getApplicationContext(),response, Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(),e.toString(), Toast.LENGTH_LONG).show();
                }
                adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),'a', Toast.LENGTH_LONG).show();
            }
        });

        StringRequestWithCookiesBody stringRequest2 = new StringRequestWithCookiesBody(requestURL2, requestHeader2, requestBody, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    accessToken[0] = json.getString("access_token");
                    queue.add(stringRequest1);
                    // Toast.makeText(getApplicationContext(),accessToken[0], Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(),e.toString(), Toast.LENGTH_LONG).show();
                }
                adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),'a', Toast.LENGTH_LONG).show();
            }
        });


        // Add the request to the RequestQueue.
        queue.add(stringRequest2);
        queue.add(stringRequest);
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
    static String getSpotifyLink(String temp) {
        return temp;
    }
    static String[] getSpotifyInfo(JSONObject item) {
        String[] toReturn = new String[3];
        toReturn[0] = "Something to link to spotify app";
        toReturn[1] = "Title";
        toReturn[2] = "Song thumbnail";
        return toReturn;
    }
}
