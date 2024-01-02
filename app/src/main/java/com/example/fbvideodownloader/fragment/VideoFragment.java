package com.example.fbvideodownloader.fragment;


import static com.example.fbvideodownloader.utils.FileHelper.getMp4FilesFromFolder;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;


import com.example.fbvideodownloader.R;
import com.example.fbvideodownloader.adapter.AdapterMP4;
import com.example.fbvideodownloader.model.MP4model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class VideoFragment extends Fragment {
    TextView tvNoFile;
    ImageView imgNoFile;
    GridView grDanhSach;
    ArrayList<MP4model> list;
    AdapterMP4 adapterMP4;

    public VideoFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_video, container, false);
        initView(v);
        listener(v);
        return v;
    }

    private void initView(View v) {
        tvNoFile = v.findViewById(R.id.no_file_download);
        imgNoFile = v.findViewById(R.id.img_nofile);
        grDanhSach = v.findViewById(R.id.gridView_ds);
    }

    private void listener(View v) {
        list = new ArrayList<>();
        String folderPath1 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + getString(R.string.app_name);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                List<File> mp4Files = getMp4FilesFromFolder(folderPath1);
                if (mp4Files.size() > 0) {
                    tvNoFile.setVisibility(View.GONE);
                    imgNoFile.setVisibility(View.GONE);
                } else {
                    tvNoFile.setVisibility(View.VISIBLE);
                    imgNoFile.setVisibility(View.VISIBLE);
                }
                for (File mp4File : mp4Files) {
                    list.add(new MP4model(mp4File.getName(), mp4File.getAbsolutePath(),mp4File.getParent()));
                    adapterMP4 = new AdapterMP4(getContext(), list);
                    grDanhSach.setAdapter(adapterMP4);
                    adapterMP4.notifyDataSetChanged();
                }
              int a =  mp4Files.size();
                Log.e("sizeMP4",a+"");
            } else {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        } else {

        }
    }
}