package com.example.fbvideodownloader;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
//        getSupportActionBar().hide();
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setStatusBarGradiant(this);
        findViewById(R.id.btn_paste).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogLoading1();
            }
        });
        findViewById(R.id.btn_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogDownload();
            }
        });
        findViewById(R.id.btn_help).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                FragmentManager fragmentManager = getSupportFragmentManager();
//                fragmentManager
//                        .beginTransaction()
//                        .replace(R.id.layout_main,new HelpsFragment())
//                        .commit();
                Intent intent = new Intent(MainActivity.this, HelpsActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);

            }
        });
        findViewById(R.id.btn_download).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,DownloadActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//                builder.setView(R.layout.);
            }
        });

    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setStatusBarGradiant(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            Drawable background = activity.getResources().getDrawable(R.color.white);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(android.R.color.transparent));
//            window.setNavigationBarColor(activity.getResources().getColor(android.R.color.transparent));
            window.setBackgroundDrawable(background);
        }
    }

    private void DialogLoading(){
        ProgressDialog pd = new ProgressDialog(MainActivity.this);
        pd.setMessage("loading");
//        pd.setProgressDrawable();
        pd.show();
        Window view1=pd.getWindow();
        view1.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
// to get rounded corners and border for dialog window
        view1.setBackgroundDrawableResource(R.drawable.boder_progressdialog);
    }

    public void DialogDownload(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_download, null);
        builder.setView(view);
        AlertDialog dialog  = builder.create();
        dialog.show();
//        BottomSheetDialog dialog1 = new BottomSheetDialog(MainActivity.this);
//        dialog1.setContentView(R.layout.dialog_download);
//        dialog1.show();
        Window view1=dialog.getWindow();
        view1.setGravity(Gravity.BOTTOM);
        view1.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
// to get rounded corners and border for dialog window
        view1.setBackgroundDrawableResource(R.drawable.boder_progressdialog);
    }
    public void DialogLoading1(){
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.progress_dialog);
        dialog.show();
        Window view1=dialog.getWindow();
        view1.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
// to get rounded corners and border for dialog window
        view1.setBackgroundDrawableResource(R.drawable.boder_progressdialog);
    }
}