package com.example.vlcplayer;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoActivity extends AppCompatActivity {

    private VideoView mVideoView;
    private int mCurrentPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme_Video);
        setContentView(R.layout.activity_video);
        getSupportActionBar().setTitle(getIntent().getStringExtra("Title"));

        mVideoView = findViewById(R.id.vv_video);

        MediaController mediaController = new MediaController(this);
        mediaController.setMediaPlayer(mVideoView);
        mVideoView.setMediaController(mediaController);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initializePlayer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mVideoView.pause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        releasePlayer();
    }

    private void initializePlayer() {
        mVideoView.setVideoPath(getIntent().getStringExtra("Path"));
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                if (mCurrentPosition > 0) mVideoView.seekTo(mCurrentPosition);
                else mVideoView.seekTo(1);
                mVideoView.start();
            }
        });

        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mVideoView.seekTo(0);
            }
        });
    }

    private void releasePlayer() { mVideoView.stopPlayback(); }

}
