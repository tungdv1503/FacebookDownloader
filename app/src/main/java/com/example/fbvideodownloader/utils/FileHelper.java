package com.example.fbvideodownloader.utils;

import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileHelper {

    public static List<File> getMp4FilesFromFolder(String folderPath) {
        List<File> mp4Files = new ArrayList<>();
        String a = folderPath;
        // Tạo một đối tượng File đại diện cho thư mục
        File folder = new File(folderPath);

        // Kiểm tra xem thư mục có tồn tại không và có phải là thư mục không
        if (folder.exists() && folder.isDirectory()) {
            // Lấy danh sách tất cả các file trong thư mục
            File[] files = folder.listFiles();

            // Lặp qua từng file để kiểm tra và thêm vào danh sách nếu là file MP4
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.getName().toLowerCase().endsWith(".mp4")) {
                        mp4Files.add(file);
                    }
                }
            }
        } else {
            Log.e("Folder", "Null");
        }

        return mp4Files;
    }

    public static List<File> getMp3FilesFromFolder(String folderPath) {
        List<File> mp4Files = new ArrayList<>();
        String a = folderPath;
        // Tạo một đối tượng File đại diện cho thư mục
        File folder = new File(folderPath);

        // Kiểm tra xem thư mục có tồn tại không và có phải là thư mục không
        if (folder.exists() && folder.isDirectory()) {
            // Lấy danh sách tất cả các file trong thư mục
            File[] files = folder.listFiles();

            // Lặp qua từng file để kiểm tra và thêm vào danh sách nếu là file MP4
            if (files != null) {
                for (File file : files) {
                    Log.e("file", file.getName() + "-" + file.getParent() + "-");
                    if (file.isFile() && file.getName().toLowerCase().endsWith(".mp3")) {
                        mp4Files.add(file);
                    }
                }
            }
        } else {
            Log.e("Folder", "Null");
        }

        return mp4Files;
    }

    public static String getFileSize(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            if (file.length() >= 1048576) {
                return file.length() / (1024 * 1024)+" MB";
            } else if (file.length() >= 1024) {
                return file.length() / 1024+" KB";
            } else {
                return file.length() + " B";
            }

        }
        return 0+" B";
    }
}

