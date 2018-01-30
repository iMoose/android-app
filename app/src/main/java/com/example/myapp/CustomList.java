package com.example.myapp;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;

public class CustomList extends ArrayAdapter<String> {

    private final Activity context;
    private final ArrayList<String> imageDescriptions;
    private final ArrayList<String> imageURLS;
    private final ArrayList<String> videoIDS;
    private LruCache<String, Bitmap> cache;

    public CustomList(Activity context,
                      ArrayList<String> imageDescriptions, ArrayList<String> imageURLS, ArrayList<String> videoIDS, LruCache<String, Bitmap> cache) {
        super(context, R.layout.list_single, imageDescriptions);
        this.context = context;
        this.imageDescriptions = imageDescriptions;
        this.imageURLS = imageURLS;
        this.videoIDS = videoIDS;
        this.cache = cache;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.list_single, null, true);
        TextView txtTitle = rowView.findViewById(R.id.txt);

        ImageView imageView = rowView.findViewById(R.id.img);
        txtTitle.setText(imageDescriptions.get(position));
        new DownloadImageFromInternet(imageView).execute(imageURLS.get(position));
        return rowView;
    }

    public String getVideoID(int position) {
        return videoIDS.get(position);
    }

    private class DownloadImageFromInternet extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;

        public DownloadImageFromInternet(ImageView imageView) {
            this.imageView = imageView;
        }

        protected Bitmap doInBackground(String... urls) {
            String imageURL = urls[0];
            Bitmap bimage = getBitmapFromMemCache(imageURL);
            if(bimage != null) {
                return bimage;
            }
            try {
                InputStream in = new java.net.URL(imageURL).openStream();
                bimage = BitmapFactory.decodeStream(in);

            } catch (Exception e) {
                Log.e("Error Message", e.getMessage());
                e.printStackTrace();
            }
            addBitmapToMemoryCache(imageURL,bimage);
            return bimage;
        }

        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            cache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return cache.get(key);
    }
}