package com.example.fbvideodownloader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.fbvideodownloader.fragment.Mp3Fragment;
import com.example.fbvideodownloader.fragment.PinterestFragment;
import com.example.fbvideodownloader.fragment.VideoFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class DownloadActivity extends AppCompatActivity {
    private TabLayout tabLayout1;
    private ViewPager2 viewPager1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        tabLayout1 = findViewById(R.id.tab_layout);
        viewPager1 = findViewById(R.id.view_page);
        setStatusBarGradiant(this);
        CustomFragmentAdapter adapter = new CustomFragmentAdapter(this);
        viewPager1.setAdapter(adapter);
        new TabLayoutMediator(tabLayout1, viewPager1, new TabLayoutMediator.TabConfigurationStrategy( ) {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0 :{
                        tab.setText("Video ").setIcon(R.drawable.vector_video);
                        break;
                    }
                    case 1 :{
                        tab.setText(" Mp3 ").setIcon(R.drawable.vector_mp3);
                        break;
                    }
                    case 2 :{
                        tab.setText(" Pinterest ").setIcon(R.drawable.vector_pinterest);
                        break;
                    }
                }
            }
        }).attach();
        findViewById(R.id.linearLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    public class CustomFragmentAdapter extends FragmentStateAdapter {
        public CustomFragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:{
                    return new VideoFragment();
                }
                case 1:{
                    return new Mp3Fragment();
                }
                case 2:{
                    return  new PinterestFragment();
                }
            }
            return null;
        }

        @Override
        public int getItemCount() {
            return 3;
        }
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
}