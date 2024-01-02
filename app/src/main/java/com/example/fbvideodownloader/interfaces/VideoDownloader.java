package com.example.fbvideodownloader.interfaces;

import android.os.AsyncTask;

public interface VideoDownloader {

    String createDirectory();

    String getVideoId(String link);

    void DownloadVideo();
}
