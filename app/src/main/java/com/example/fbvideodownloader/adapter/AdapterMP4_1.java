package com.example.fbvideodownloader.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;


import com.example.fbvideodownloader.R;
import com.example.fbvideodownloader.model.MP4model;
import com.example.fbvideodownloader.utils.FileHelper;

import java.io.File;
import java.util.List;

public class AdapterMP4_1 extends BaseAdapter {
    private Context context;
    private List<MP4model> list;
    private PopupWindow popupWindow;
    public AdapterMP4_1(Context context, List<MP4model> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;

        if (view == null) {
            // Nếu convertView không được tái sử dụng, inflate một layout mới
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.item_mp4_1, viewGroup, false);

            // Tạo một ViewHolder để lưu trữ các thành phần của layout
            holder = new ViewHolder();
            holder.tvNameFile = view.findViewById(R.id.tv_name_music);
            holder.tvSizeFile = view.findViewById(R.id.tv_sizefile);
            holder.imgMenu = view.findViewById(R.id.img_menu);
            holder.btnStartVideo = view.findViewById(R.id.btn_start);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        MP4model item = list.get(i);
        String title = item.getTitle();
        int maxLength = 10;
        if (title.length() > maxLength) {
            title = title.substring(0, maxLength) + "...";
        }
        holder.tvNameFile.setText(title);
        holder.tvSizeFile.setText(FileHelper.getFileSize(item.getPath()));
        holder.imgMenu.setOnClickListener(view1 -> {
            showPopup(view1,item);
        });
        holder.btnStartVideo.setOnClickListener(v -> {
            startVideo(item.getTitle(), item.getParent());
        });
        return view;
    }

    static class ViewHolder {
        TextView tvNameFile, tvSizeFile;
        ImageView imgMenu;
        ConstraintLayout btnStartVideo;
    }

    private void deleteFile(String nameFile, String path) {
        File file = new File(path, nameFile);
        if (file.exists()) {
            boolean deleted = file.delete();
            if (deleted) {
                Log.e("Delete1", "File đã được xóa");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    list.removeIf(mp4model -> mp4model.getTitle().equals(nameFile) && mp4model.getParent().equals(path));
                }
                notifyDataSetChanged();
            } else {
                Log.e("Delete1", "Không thể xóa file");
            }
        } else {
            Log.e("Delete1", "File không tồn tại");
        }
    }

    private void shareFile(String nameFile, String path) {
        File file = new File(path, nameFile);
        if (file.exists()) {
            Uri fileUri = Uri.fromFile(file);
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("*/*");
            shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
            context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.share_file)));

        } else {
            Log.e("Share1", "File không tồn tại");
        }
    }

    private void startVideo(String fileName, String folderPath) {
        File file = new File(folderPath, fileName);
        if (file.exists()) {
            Uri videoUri = Uri.fromFile(file);
            Intent videoIntent = new Intent(Intent.ACTION_VIEW);
            videoIntent.setDataAndType(videoUri, "video/*");
            videoIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            try {
                // Mở trình xem video
                context.startActivity(videoIntent);
            } catch (Exception e) {
                Log.e("PlayVideo", "Không có ứng dụng hỗ trợ xem video");
            }
            onItemClicked(file);

        } else {
            Log.e("PlayVideo", "File video không tồn tại");
        }
    }

    public void onItemClicked(File file) {
        Uri uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, context.getContentResolver().getType(uri));
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(intent);
    }

    public void showPopup(View anchorView,MP4model model) {
        // Inflate layout
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.custom_menu, null);
        int widthInDp = 200;
        int heightInDp = 100;
        int widthInPx = dpToPx(widthInDp);
        int heightInPx = dpToPx(heightInDp);
        popupWindow = new PopupWindow(popupView, widthInPx, heightInPx, true);

        LinearLayout imgShare = popupView.findViewById(R.id.img_share);
        LinearLayout imgDelete = popupView.findViewById(R.id.img_delete);

        popupWindow.showAsDropDown(anchorView);
        imgShare.setOnClickListener(v -> {
            shareFile(model.getTitle(), model.getParent());
            dismissPopup();
        });
        imgDelete.setOnClickListener(v -> {
            deleteFile(model.getTitle(), model.getParent());
            dismissPopup();
        });
    }

    public void dismissPopup() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }
    private int dpToPx(int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }
}
