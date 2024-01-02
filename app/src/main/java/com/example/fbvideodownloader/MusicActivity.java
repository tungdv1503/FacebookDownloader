package com.example.fbvideodownloader;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class MusicActivity extends AppCompatActivity {
    ImageView img_close, img_next10, img_prev10, img_pause;
    VideoView videoView;
    MediaPlayer player;
    TextView tvCurrent, tvDuration, tvSpeed;
    Thread updateSeekBar;
    SeekBar seekBar;
    CountDownTimer count;
    int type = 0;
    String path = "";
    private Handler handler;
    float playbackSpeed = 1.0f;
    boolean a = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        setStatusBarGradiant(this);
        initViews();
        listener();

    }

    private void initViews() {
        img_close = findViewById(R.id.img_close);
        videoView = findViewById(R.id.video_view);
        seekBar = findViewById(R.id.seekbar);
        tvCurrent = findViewById(R.id.tv_current);
        tvDuration = findViewById(R.id.tv_duration);
        img_pause = findViewById(R.id.img_pause);
        img_next10 = findViewById(R.id.img_next);
        img_prev10 = findViewById(R.id.img_prev);
        tvSpeed = findViewById(R.id.tv_speedrun);
    }

    private void listener() {
        seekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        handler = new Handler(Looper.getMainLooper());
        img_close.setOnClickListener(v -> finish());
        Intent i = getIntent();
        type = i.getIntExtra("typeFile", 0);
        path = i.getStringExtra("pathFile");
        Uri uri = Uri.parse(path);
        if (type == 1) {
            videoView.setVisibility(View.VISIBLE);
            tvSpeed.setVisibility(View.GONE);
            playVideo(uri);
            updateSeekBar.start();
        } else if (type == 2) {
            videoView.setVisibility(View.GONE);
            tvSpeed.setVisibility(View.VISIBLE);
            playMusic(uri);
            updateSeekBar.start();
        }
        img_pause.setOnClickListener(v -> {
            if (type == 1) {
                if (videoView != null) {
                    if (videoView.isPlaying()) {
                        img_pause.setImageResource(R.drawable.icon_start);
                        videoView.pause();
                    } else {
                        img_pause.setImageResource(R.drawable.pause);
                        videoView.start();
                    }
                }
            } else if (type == 2) {
                if (player != null) {
                    if (player.isPlaying()) {
                        img_pause.setImageResource(R.drawable.icon_start);
                        player.pause();
                    } else {
                        img_pause.setImageResource(R.drawable.pause);
                        player.start();
                    }
                }
            }
        });
        img_next10.setOnClickListener(v -> {
            if (type == 1) {
                int currentPosition = videoView.getCurrentPosition();

                int newPosition = currentPosition + 10000;

                int videoDuration = (int) getVideoDuration(path);
                if (newPosition > videoDuration) {
                    newPosition = videoDuration;
                }

                videoView.seekTo(newPosition);
            } else if (type == 2) {
                int currentPosition = player.getCurrentPosition();

                int newPosition = currentPosition + 10000;

                int videoDuration = player.getDuration();
                if (newPosition > videoDuration) {
                    newPosition = videoDuration;
                }

                player.seekTo(newPosition);
            }


        });
        img_prev10.setOnClickListener(v -> {
            if (type == 1) {
                int currentPosition = videoView.getCurrentPosition();

                int newPosition = currentPosition - 10000;

                if (newPosition < 0) {
                    newPosition = 0;
                }

                videoView.seekTo(newPosition);
            } else if (type == 2) {
                int currentPosition = player.getCurrentPosition();

                int newPosition = currentPosition - 10000;


                if (newPosition < 0) {
                    newPosition = 0;
                }
                player.seekTo(newPosition);
            }
        });
        tvSpeed.setOnClickListener(v -> {
            if (type == 1) {

            } else if (type == 2) {
                tvSpeed.setVisibility(View.VISIBLE);
                if (playbackSpeed == 1.0) {
                    playbackSpeed = 1.5f;
                    setSpeedMusic(playbackSpeed);
                } else if (playbackSpeed == 1.5) {
                    playbackSpeed = 2.0f;
                    setSpeedMusic(playbackSpeed);
                } else if (playbackSpeed >= 2) {
                    playbackSpeed = 1.0f;
                    setSpeedMusic(playbackSpeed);
                }
            }

        });
    }

    private void playVideo(Uri uri) {
        videoView.setVideoURI(uri);
        videoView.start();
        displayTime(0, getVideoDuration(path));
        seekBar.setMax((int) getVideoDuration(path));

        updateSeekBar = new Thread() {
            @Override
            public void run() {
                super.run();
                int totalDuration = (int) getVideoDuration(path);
                int currentPosition = 0;
                while (totalDuration > currentPosition) {
                    try {
                        if (videoView != null) {
                            currentPosition = videoView.getCurrentPosition();
                            seekBar.setProgress(currentPosition);
                            int finalCurrentPosition = currentPosition;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    displayTime(finalCurrentPosition, videoView.getDuration());
                                }
                            });
                        }
                        sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    private void playMusic(Uri uri) {

        player = MediaPlayer.create(getApplicationContext(), uri);
        player.start();
        displayTime(0, player.getDuration());
        seekBar.setMax(player.getDuration());
        updateSeekBar = new Thread() {
            @Override
            public void run() {
                super.run();
                int totalDuration = player.getDuration();
                int currentPosition = 0;
                while (totalDuration > currentPosition) {
                    try {
                        if (player != null) {
                            currentPosition = player.getCurrentPosition();
                            seekBar.setProgress(currentPosition);
                            int finalCurrentPosition = currentPosition;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    displayTime(finalCurrentPosition, totalDuration);
                                }
                            });
                        }
                        sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    private void displayTime(long currentSeconds, long durationInSeconds) {
        tvCurrent.setText(formatTime(currentSeconds));
        tvDuration.setText(formatTime(durationInSeconds));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.stop();
        }
        if (videoView != null) {
            videoView.stopPlayback();
        }
    }

    private String formatTime(long millis) {
        String time = "";
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        if (seconds < 3600) {
            time = String.format("%02d:%02d",
                    minutes % 60,
                    seconds % 60);
        } else {
            time = String.format("%02d:%02d:%02d",
                    hours % 24,
                    minutes % 60,
                    seconds % 60);
        }


        return time;
    }

    private long getVideoDuration(String filePath) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();

        try {
            Uri videoUri = Uri.parse(filePath);
            retriever.setDataSource(this, videoUri);

            String durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

            if (durationStr != null) {
                return Long.parseLong(durationStr);
            } else {
                return -1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        } finally {
            try {
                retriever.release();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void setSpeedMusic(float playbackSpeed) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PlaybackParams params = new PlaybackParams();
            params.setSpeed(playbackSpeed); // Đặt tốc độ mới
            player.setPlaybackParams(params);
            tvSpeed.setText(playbackSpeed + "x");
        }
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
}