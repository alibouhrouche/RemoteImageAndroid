package com.example.remoteimage;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.os.PowerManager;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

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

public class RemoteImage extends MyAsyncTask<String, Integer, Drawable> {
    WeakReference<ProgressBar> progressBarWeakReference;
    WeakReference<ImageView> imageViewWeakReference;
    Drawable broken;
    public RemoteImage(ProgressBar b, ImageView img,Drawable broken){
        progressBarWeakReference = new WeakReference<>(b);
        imageViewWeakReference = new WeakReference<>(img);
        this.broken = broken;
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
            if(fileLength == -1){
                publishProgress(-1);
                return Drawable.createFromStream(input, url.getFile());
            }
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
            bar.setIndeterminate(false);
            img.setScaleType(ImageView.ScaleType.FIT_CENTER);
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
            if(drawable == null){
                Snackbar.make(img,"Can't load image",Snackbar.LENGTH_LONG).show();
                img.setScaleType(ImageView.ScaleType.CENTER);
                drawable = broken;
                if(broken == null)
                    return;
            }
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
            if(values[0] == -1)
                bar.setIndeterminate(true);
            else
                bar.setProgress(values[0]);
        }
    }
}
