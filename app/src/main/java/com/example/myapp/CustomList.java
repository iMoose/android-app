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

    // Calculate max size of cache
    static int maxMemory = (int) Runtime.getRuntime().maxMemory() / 1024;
    static int cacheSize = maxMemory / 8;

    private static LruCache<String, Bitmap> cache = new LruCache<String, Bitmap>(cacheSize) {
        @Override
        protected int sizeOf(String key, Bitmap bitmap) {
            // The cache size will be measured in kilobytes rather than
            // number of items.
            return bitmap.getByteCount() / 1024;
        }
    };

    public CustomList(Activity context,
                      ArrayList<String> imageDescriptions, ArrayList<String> imageURLS) {
        super(context, R.layout.list_single, imageDescriptions);
        this.context = context;
        this.imageDescriptions = imageDescriptions;
        this.imageURLS = imageURLS;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.list_single, null, true);
        TextView txtTitle = rowView.findViewById(R.id.txt);

        ImageView imageView = rowView.findViewById(R.id.img);
        txtTitle.setText(imageDescriptions.get(position));

        String imageURL = imageURLS.get(position);
        Bitmap cache = getBitmapFromMemCache(imageURL);

        if (cache == null) {
            new DownloadImageFromInternet(imageView).execute(imageURL);
        }
        else {
            imageView.setImageBitmap(cache);
        }
        return rowView;
    }

    private class DownloadImageFromInternet extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;

        public DownloadImageFromInternet(ImageView imageView) {
            this.imageView = imageView;
        }

        protected Bitmap doInBackground(String... urls) {
            String imageURL = urls[0];
            Bitmap bimage = null;
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