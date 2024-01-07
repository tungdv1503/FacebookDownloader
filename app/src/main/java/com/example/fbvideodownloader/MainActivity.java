package com.example.fbvideodownloader;

import static com.example.fbvideodownloader.utils.FileHelper.getMp4FilesFromFolder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fbvideodownloader.adapter.AdapterMP4_1;
import com.example.fbvideodownloader.adapter.AdapterVip;
import com.example.fbvideodownloader.admob.GoogleMobileAdsConsentManager;
import com.example.fbvideodownloader.admob.MyApplication;
import com.example.fbvideodownloader.model.FbVideoDownloader;
import com.example.fbvideodownloader.model.MP3model;
import com.example.fbvideodownloader.model.MP4model;
import com.example.fbvideodownloader.model.Vipmodel;
import com.example.fbvideodownloader.utils.NetworkUtils;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.gms.ads.admanager.AdManagerAdView;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity {
    TextInputEditText edtsetLink;
    private Dialog dialog;
    private ListView lv_JustDownload;
    List<MP4model> itemMp4;
    private Handler handler;
    private static final int DELAY_MILLIS = 1000;
    boolean isceck = false;
    int index = 0;
    private Disposable disposable;
    String VideoTitle;
    private GoogleMobileAdsConsentManager googleMobileAdsConsentManager;
    private final AtomicBoolean initialLayoutComplete = new AtomicBoolean(false);
    static final String AD_UNIT = "/30497360/adaptive_banner_test_iu/backfill";
    private static final String TAG = "MyActivity";
    private AdManagerAdView adView;
    private final AtomicBoolean isMobileAdsInitializeCalled = new AtomicBoolean(false);
    private static final long COUNTER_TIME_MILLISECONDS = 5000;
    private FrameLayout adContainerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        setStatusBarGradiant(this);
        DialogLoading1(this);
        edtsetLink = findViewById(R.id.edt_setLink);
        lv_JustDownload = findViewById(R.id.lv_just_download);
        adContainerView = findViewById(R.id.ad_view_container);
        edtsetLink.setSingleLine(true);
        listener();
        if (isStoragePermissionGranted()) {
            File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), getString(R.string.app_name));
            if (!directory.exists()) {
                directory.mkdirs();
            }
        }
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
        googleMobileAdsConsentManager =
                GoogleMobileAdsConsentManager.getInstance(getApplicationContext());
        googleMobileAdsConsentManager.gatherConsent(
                this,
                consentError -> {
                    if (consentError != null) {
                        Log.w(
                                "LOG_TAG",
                                String.format(
                                        "%s: %s", consentError.getErrorCode(), consentError.getMessage()));
                    }

                    if (googleMobileAdsConsentManager.canRequestAds()) {
                        initializeMobileAdsSdk();
                        initializeMobileAdsSdkBanner();
                    }

                });
        if (googleMobileAdsConsentManager.canRequestAds()) {
            initializeMobileAdsSdk();
            initializeMobileAdsSdkBanner();
        }
        adContainerView
                .getViewTreeObserver()
                .addOnGlobalLayoutListener(
                        () -> {
                            if (!initialLayoutComplete.getAndSet(true)
                                    && googleMobileAdsConsentManager.canRequestAds()) {
                                loadBanner();
                            }
                        });

        MobileAds.setRequestConfiguration(
                new RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("ABCDEF012345")).build());
        createTimer(COUNTER_TIME_MILLISECONDS);
    }

    private void listener() {
        itemMp4 = new ArrayList<>();
        handler = new Handler();
        findViewById(R.id.btn_paste).setOnClickListener(view -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            String pasteData;

            if (!(clipboard.hasPrimaryClip())) {
            } else if (!(clipboard.getPrimaryClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN))) {
            } else {
                ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
                pasteData = item.getText().toString();
                edtsetLink.setText(pasteData);
            }
        });
        String destination = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + getString(R.string.app_name) + "/";
        findViewById(R.id.btn_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isStoragePermissionGranted()) {
                    if (!NetworkUtils.isWifiConnected(MainActivity.this) && !NetworkUtils.isMobileDataConnected(MainActivity.this)) {
                        Toast.makeText(MainActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    } else {
                        String url = edtsetLink.getText().toString();
                        if (!url.isEmpty() && url.contains("fb") || url.contains("facebook")) {
                            new Data().execute(url);

                        } else {
                            Toast.makeText(MainActivity.this, "Enter a Valid URL!!", Toast.LENGTH_SHORT).show();
                        }

//                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse("https://video.xx.fbcdn.net/v/t42.1790-2/10000000_1353704461931107_5523603705264710712_n.mp4?_nc_cat=109&ccb=1-7&_nc_sid=55d0d3&efg=eyJybHIiOjQzOSwicmxhIjoxMjk1LCJ2ZW5jb2RlX3RhZyI6InN2ZV9zZCJ9&_nc_ohc=XtOg_0GTeiwAX-6DlnL&_nc_rml=0&rl=439&vabr=244&_nc_ht=video.fhan15-1.fna&oh=00_AfDR3eFlSLbtr17fwKN0EpvTl9cnWADUzmNhpEK4Hz8lpw&oe=659F6BBA"));
//                        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
//                        request.setTitle(getString(R.string.download));
//                        request.setDescription("VideoNew.mp4");
//                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//                        request.setTitle(getString(R.string.download)+""+"VideoNew.mp4");
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                            final Uri uri = Uri.parse("file://" + destination + "VideoNew.mp4");
//                            request.setDestinationUri(uri);
//                            DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
//                            manager.enqueue(request);
//                        }
                    }
                }
            }
        });
        findViewById(R.id.btn_help).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, HelpsActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);

            }
        });
        findViewById(R.id.btn_download).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DownloadActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            }
        });
        findViewById(R.id.btn_vip).setOnClickListener(v -> {
            DialogVip(this);
        });

    }

    private void DialogVip(Context context) {
        List<Vipmodel> list = new ArrayList<>();
        list.add(new Vipmodel("Tuần", 12000, 7));
        list.add(new Vipmodel("Tháng", 26000, 30));
        list.add(new Vipmodel("Năm", 115000, 365));
        list.add(new Vipmodel("Vĩnh viễn", 232000, 36500));
        Dialog dialog1 = new Dialog(context);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog1.setContentView(R.layout.dialog_vip);
//        dialog1.setCancelable(false);
//        dialog1.setCanceledOnTouchOutside(false);
        //
        View decorView = dialog1.getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        //
        Window window = dialog1.getWindow();
        if (window == null) {
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.CENTER;
        window.setAttributes(windowAttributes);

        AdapterVip adapterVip = new AdapterVip(context, list);
        ListView listView = dialog1.findViewById(R.id.lv_vip);
        ImageView imagClose = dialog1.findViewById(R.id.img_close);
        Button btnBuy = dialog1.findViewById(R.id.btn_buy);
        listView.setAdapter(adapterVip);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            adapterVip.setSelectedItem(position);
            adapterVip.notifyDataSetChanged();
            index = position;

        });
        btnBuy.setOnClickListener(v -> {

        });
        imagClose.setOnClickListener(v -> dialog1.dismiss());
        dialog1.show();
    }

    public static void setStatusBarGradiant(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            Drawable background = activity.getResources().getDrawable(R.color.black);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(android.R.color.transparent));
            window.setBackgroundDrawable(background);
        }
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else {
            return true;
        }
    }

    public void DialogLoading1(Context context) {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.progress_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        Window view1 = dialog.getWindow();
        view1.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        view1.setBackgroundDrawableResource(R.drawable.boder_progressdialog);
    }

    private void startDownloadStatusBroadcastLoop(long downloadID, String namefile) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isceck) {
                    sendDownloadStatusBroadcast(downloadID, namefile);
                    handler.postDelayed(this, DELAY_MILLIS);
                }

            }
        }, DELAY_MILLIS);
    }

    private void stopDownloadStatusBroadcastLoop() {
        dialog.dismiss();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            isceck = false;
        }
    }

    private void sendDownloadStatusBroadcast(long downloadId, String namefile) {
        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        if (downloadManager != null) {
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(downloadId);
            try (Cursor cursor = downloadManager.query(query)) {
                if (cursor.moveToFirst()) {
                    int statusIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                    int status = cursor.getInt(statusIndex);

                    handleDownloadStatus(status, namefile);
                }
            }
        }
    }

    private void handleDownloadStatus(int status, String namefile1) {
        if (status == DownloadManager.STATUS_SUCCESSFUL) {

            if (namefile1.contains(".")) {
                String result = namefile1.substring(namefile1.lastIndexOf(".") + 1);
                System.out.println(result);

                String folderPath1 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + getString(R.string.app_name);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        if (result.equals("mp4")) {
                            itemMp4 = new ArrayList<>();
                            List<File> mp4Files = getMp4FilesFromFolder(folderPath1);
                            for (File mp4File : mp4Files) {
                                if (mp4File.getName().equals(namefile1)) {
                                    itemMp4.add(new MP4model(mp4File.getName(), mp4File.getAbsolutePath(), mp4File.getParent()));
                                    AdapterMP4_1 adapterMP3 = new AdapterMP4_1(MainActivity.this, itemMp4);
                                    lv_JustDownload.setAdapter(adapterMP3);
                                }
                            }
                        }
                    } else {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    }
                }
            }
            stopDownloadStatusBroadcastLoop();
        } else {
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class Data extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection;
            BufferedReader reader;
            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();


                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                String buffer = "No URL";
                String Line;
                while ((Line = reader.readLine()) != null) {
                    if (Line.contains("og:video:url")) {
                        Line = Line.substring(Line.indexOf("og:video:url"));
                        if (Line.contains("og:title")) {
                            VideoTitle = Line.substring(Line.indexOf("og:title"));
                            VideoTitle = VideoTitle.substring(ordinalIndexOf(VideoTitle, "\"", 1) + 1, ordinalIndexOf(VideoTitle, "\"", 2));
                        }
                        Line = Line.substring(ordinalIndexOf(Line, "\"", 1) + 1, ordinalIndexOf(Line, "\"", 2));
                        if (Line.contains("amp;")) {
                            Line = Line.replace("amp;", "");
                        }
                        if (!Line.contains("https")) {
                            Line = Line.replace("http", "https");
                        }
                        buffer = Line;
                        break;
                    } else {
                        buffer = "No URL";
                    }
                }
                return buffer;
            } catch (IOException e) {
                return "No URL";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            Log.e("onPostExecute:s", s);
            String destination = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + "FB Video Downloader" + "/";
            File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), getString(R.string.app_name));

            if (!directory.exists()) {
                directory.mkdirs();
            }
            if (!s.contains("No URL")) {
                if (VideoTitle == null || VideoTitle.equals("")) {
                    VideoTitle = "CT_fbVideo" + System.currentTimeMillis() + ".mp4";
                } else {
                    VideoTitle = VideoTitle + ".mp4";
                }
//                File newFile = new File(path, VideoTitle);
                try {
                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(s));
                    request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
                    request.setTitle(getString(R.string.download));
                    request.setDescription(VideoTitle);
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setTitle(getString(R.string.download) + "" + VideoTitle);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        final Uri uri = Uri.parse("file://" + destination + VideoTitle);
                        request.setDestinationUri(uri);
                    } else {
                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS + "/" + "FB Video Downloader", VideoTitle);
                    }
                    DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                    long DownLoadID = manager.enqueue(request);
                    startDownloadStatusBroadcastLoop(DownLoadID, VideoTitle);
                    isceck = true;
                    dialog.show();
                } catch (Exception e) {
                    Looper.prepare();
                    Looper.loop();
                    dialog.dismiss();
                }

            } else {
                Looper.prepare();
                Looper.loop();
                dialog.dismiss();
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Looper.prepare();
            Looper.loop();
            dialog.dismiss();
        }
    }

    private static int ordinalIndexOf(String str, String substr, int n) {
        int pos = -1;
        do {
            pos = str.indexOf(substr, pos + 1);
        } while (n-- > 0 && pos != -1);
        return pos;
    }

    private void initializeMobileAdsSdk() {
        if (isMobileAdsInitializeCalled.getAndSet(true)) {
            return;
        }

        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this);

        // Load an ad.
        Application application = getApplication();
        ((MyApplication) application).loadAd(this);
    }

    private void initializeMobileAdsSdkBanner() {
        if (isMobileAdsInitializeCalled.getAndSet(true)) {
            return;
        }

        MobileAds.initialize(
                this,
                new OnInitializationCompleteListener() {
                    @Override
                    public void onInitializationComplete(InitializationStatus initializationStatus) {
                    }
                });

        // Load an ad.
        if (initialLayoutComplete.get()) {
            loadBanner();
        }
    }

    private void loadBanner() {
        adView = new AdManagerAdView(this);
        adView.setAdUnitId(AD_UNIT);
        adView.setAdSize(getAdSize());

        adContainerView.removeAllViews();
        adContainerView.addView(adView);

        AdManagerAdRequest adRequest = new AdManagerAdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    private AdSize getAdSize() {

        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float density = outMetrics.density;

        float adWidthPixels = adContainerView.getWidth();

        if (adWidthPixels == 0) {
            adWidthPixels = outMetrics.widthPixels;
        }

        int adWidth = (int) (adWidthPixels / density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);
    }

    private void createTimer(long time) {

        CountDownTimer countDownTimer =
                new CountDownTimer(time, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                    }

                    @Override
                    public void onFinish() {
                        showInterstitialAds();
                    }
                };
        countDownTimer.start();
    }

    private void showInterstitialAds() {
        Application application = getApplication();
        ((MyApplication) application)
                .showAdIfAvailable(
                        this,
                        new MyApplication.OnShowAdCompleteListener() {
                            @Override
                            public void onShowAdComplete() {
                                if (googleMobileAdsConsentManager.canRequestAds()) {

                                }
                            }
                        });
    }
}