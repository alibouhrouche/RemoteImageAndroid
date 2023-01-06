package com.example.remoteimage;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.MainThread;

public abstract class MyAsyncTask<Params, Progress, Result> {
    Handler mHandler;
    @MainThread
    protected void onPreExecute() {}
    abstract protected Result doInBackground(Params ...params);
    @MainThread
    void onPostExecute(Result result) {}
    @MainThread
    protected void onProgressUpdate(Progress... values) {}
    @MainThread
    public final void execute(Params ...params) {
        mHandler = new Handler(Looper.getMainLooper());
        Thread thread = new Thread(() -> {
            Result result = doInBackground(params);
            mHandler.post(() -> MyAsyncTask.this.onPostExecute(result));
        });
        onPreExecute();
        thread.start();
    }

    public void publishProgress(Progress... values) {
        mHandler.post(() -> MyAsyncTask.this.onProgressUpdate(values));
    }
}
