package com.example.myapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

public class CustomList extends ArrayAdapter<String>{

    private final Activity context;
    private final ArrayList<String> imageDescriptions;
    private final ArrayList<String> imageURLS;
    private final ArrayList<String> videoIDS;
    public CustomList(Activity context,
                      ArrayList<String> imageDescriptions, ArrayList<String> imageURLS, ArrayList<String> videoIDS) {
        super(context, R.layout.list_single, imageDescriptions);
        this.context = context;
        this.imageDescriptions = imageDescriptions;
        this.imageURLS = imageURLS;
        this.videoIDS = videoIDS;
    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_single, null, true);
        TextView txtTitle = rowView.findViewById(R.id.txt);

        ImageView imageView = rowView.findViewById(R.id.img);
        txtTitle.setText(imageDescriptions.get(position));
        new DownloadImageFromInternet(imageView).execute(imageURLS.get(position));
        // imageView.setImageResource(imageId[position]);
        return rowView;
    }

    public String getVideoID(int position) {
        return videoIDS.get(position);
    }

    private class DownloadImageFromInternet extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;

        public DownloadImageFromInternet(ImageView imageView) {
            this.imageView = imageView;
            // Toast.makeText(context.getApplicationContext(), "Please wait, it may take a few minute...", Toast.LENGTH_SHORT).show();
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
            return bimage;
        }

        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }
    }
}