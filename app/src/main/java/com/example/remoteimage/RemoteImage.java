package com.example.remoteimage;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.os.PowerManager;

import com.bumptech.glide.Glide;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;

import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;

public class RemoteImage extends AsyncTask<String, Integer, Drawable> {
    WeakReference<ProgressBar> progressBarWeakReference;
    WeakReference<ImageView> imageViewWeakReference;
    public RemoteImage(ProgressBar b, ImageView img){
        progressBarWeakReference = new WeakReference<>(b);
        imageViewWeakReference = new WeakReference<>(img);
    }

    @Override
    protected Drawable doInBackground(String... strings) {
        InputStream input = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(strings[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            // expect HTTP 200 OK, so we don't mistakenly save error report
            // instead of the file
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return null;
            }

            // this will be useful to display download percentage
            // might be -1: server did not report the length
            int fileLength = connection.getContentLength();

            // download the file
            input = connection.getInputStream();
            ProcessInputStream p = new ProcessInputStream(input, fileLength);
            p.addListener(percent -> {
                double d= percent * 100;
                publishProgress((int) d);
            });
            return Drawable.createFromStream(p, url.getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        ProgressBar bar = progressBarWeakReference.get();
        ImageView img = imageViewWeakReference.get();
        if(bar != null && img != null){
            bar.setProgress(0);
            img.setVisibility(View.INVISIBLE);
            bar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onPostExecute(Drawable drawable) {
        super.onPostExecute(drawable);
        ProgressBar bar = progressBarWeakReference.get();
        ImageView img = imageViewWeakReference.get();
        if(bar != null && img != null){
            try{
                Glide.with(img.getContext()).load(drawable).into(img);
                img.setVisibility(View.VISIBLE);
                bar.setVisibility(View.INVISIBLE);
            }catch (Exception e){

            }
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        ProgressBar bar = progressBarWeakReference.get();
        if(bar != null){
            bar.setProgress(values[0]);
        }
    }
}
