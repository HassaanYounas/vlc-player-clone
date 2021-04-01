package com.example.vlcplayer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AudioFragment extends Fragment {

    private List<String> mSongsTitleList;
    private MediaPlayer mMediaPlayer;
    private ListView mLvAudio;
    private PathAdapter mAdapter;
    private ArrayList<String> mPathList;
    private ImageView mIvPlayPause;
    private ImageView mIvNext;
    private ImageView mIvPrevious;
    private ImageView mIvLoop;
    private SeekBar mSeekBar;
    private Handler mHandler;
    private SharedViewModel mSharedViewModel;
    private TextView mTvSongTitle;

    private boolean loop = false;
    private boolean play = false;
    private int currentSongsIndex = -1;

    public AudioFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_audio, container, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onStart() {
        super.onStart();

        mSongsTitleList = new ArrayList<>();
        mSharedViewModel = ViewModelProviders.of(getActivity()).get(SharedViewModel.class);
        mHandler = new Handler();
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mIvPlayPause = getView().findViewById(R.id.btn_play_pause);
        mIvNext = getView().findViewById(R.id.btn_next);
        mIvPrevious = getView().findViewById(R.id.btn_previous);
        mIvLoop = getView().findViewById(R.id.btn_loop);
        mSeekBar = getView().findViewById(R.id.seek_bar);
        mTvSongTitle = getView().findViewById(R.id.tv_current_title);

        mPathList = new ArrayList<>();
        mLvAudio = getView().findViewById(R.id.lv_audio);
        mAdapter = new PathAdapter(getActivity().getApplicationContext(), new ArrayList<AudioVideoFile>());
        mLvAudio.setAdapter(mAdapter);

        if (getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        } gatherSongs();

        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
                mSeekBar.setMax(mMediaPlayer.getDuration());
                seekBarUpdater();
                mSharedViewModel.setMusicPlaying(true);
                mTvSongTitle.setText(mSongsTitleList.get(currentSongsIndex) + " - Playing");

                mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        play = false;
                        mIvPlayPause.setImageResource(R.drawable.ic_play);
                    }
                });
            }
        });

        mLvAudio.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    mMediaPlayer.reset();
                    mMediaPlayer.setDataSource(mPathList.get(position));
                    mMediaPlayer.prepareAsync();
                    if (currentSongsIndex == -1) changePlayPause();
                    if (!play) changePlayPause();
                    play = true;
                    currentSongsIndex = position;
                } catch (IOException e) {
                    Log.i("TAG", "Error");
                }
            }
        });

        mIvPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentSongsIndex == -1) {
                    Toast.makeText(getActivity().getApplicationContext(), "Please Select A Song To Play", Toast.LENGTH_SHORT).show();
                } else {
                    changePlayPause();
                    if (mMediaPlayer.isPlaying()) {
                        mMediaPlayer.pause();
                        mSharedViewModel.setMusicPlaying(false);
                        if (mMediaPlayer.isLooping()) mTvSongTitle.setText(mSongsTitleList.get(currentSongsIndex) + " - Paused & Looping");
                        else mTvSongTitle.setText(mSongsTitleList.get(currentSongsIndex) + " - Paused");
                    }
                    else {
                        mMediaPlayer.start();
                        seekBarUpdater();
                        mSharedViewModel.setMusicPlaying(true);
                        if (mMediaPlayer.isLooping()) mTvSongTitle.setText(mSongsTitleList.get(currentSongsIndex) + " - Playing & Looping");
                        else mTvSongTitle.setText(mSongsTitleList.get(currentSongsIndex) + " - Playing");
                    }
                }
            }
        });

        mIvLoop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentSongsIndex == -1) {
                    Toast.makeText(getActivity().getApplicationContext(), "Please Select A Song To Loop", Toast.LENGTH_SHORT).show();
                } else {
                    if (!loop) {
                        mMediaPlayer.setLooping(true);
                        mIvLoop.setColorFilter(getResources().getColor(R.color.colorLoopSelected));
                        loop = true;
                        if (mMediaPlayer.isPlaying()) mTvSongTitle.setText(mSongsTitleList.get(currentSongsIndex) + " - Playing & Looping");
                        else mTvSongTitle.setText(mSongsTitleList.get(currentSongsIndex) + " - Paused & Looping");
                    } else {
                        mMediaPlayer.setLooping(false);
                        mIvLoop.setColorFilter(null);
                        loop = false;
                        if (mMediaPlayer.isPlaying()) mTvSongTitle.setText(mSongsTitleList.get(currentSongsIndex) + " - Playing");
                        else mTvSongTitle.setText(mSongsTitleList.get(currentSongsIndex) + " - Paused");
                    }
                }
            }
        });

        mIvNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentSongsIndex == -1) {
                    Toast.makeText(getActivity().getApplicationContext(), "No Song Playing Currently", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        mMediaPlayer.reset();
                        if (currentSongsIndex == mPathList.size() - 1) {
                            mMediaPlayer.setDataSource(mPathList.get(0));
                            currentSongsIndex = 0;
                        } else mMediaPlayer.setDataSource(mPathList.get(++currentSongsIndex));
                        mMediaPlayer.prepareAsync();
                    } catch (IOException e) {
                        Log.i("TAG", "Error");
                    }
                    if (!play) changePlayPause();
                }
            }
        });

        mIvPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentSongsIndex == -1) {
                    Toast.makeText(getActivity().getApplicationContext(), "No Song Playing Currently", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        mMediaPlayer.reset();
                        if (currentSongsIndex == 0) {
                            mMediaPlayer.setDataSource(mPathList.get(mPathList.size() - 1));
                            currentSongsIndex = mPathList.size() - 1;
                        } else mMediaPlayer.setDataSource(mPathList.get(--currentSongsIndex));
                        mMediaPlayer.prepareAsync();
                    } catch (IOException e) {
                        Log.i("TAG", "Error");
                    }
                    if (!play) changePlayPause();
                }
            }
        });

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if ((mMediaPlayer.isPlaying() && fromUser) || (currentSongsIndex != -1 && fromUser)) mMediaPlayer.seekTo(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void seekBarUpdater() {
        if (mMediaPlayer.isPlaying()) {
            mSeekBar.setProgress(mMediaPlayer.getCurrentPosition());
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    seekBarUpdater();
                }
            }; mHandler.postDelayed(runnable, 500);
        }
    }

    private void gatherSongs() {
        ContentResolver contentResolver = getActivity().getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = contentResolver.query(uri, null, null, null, MediaStore.Audio.Media.TITLE);
        if (cursor == null) {
            Toast.makeText(getActivity().getApplicationContext(), "Something Went Wrong", Toast.LENGTH_SHORT).show();
        } else if (!cursor.moveToNext()) {
            Toast.makeText(getActivity().getApplicationContext(), "No Audio Files Found", Toast.LENGTH_SHORT).show();
        } else {
            int titleColumn = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int durationColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
            do {
                AudioVideoFile current = new AudioVideoFile(cursor.getString(titleColumn), cursor.getInt(durationColumn));
                mAdapter.add(current);
                mPathList.add(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
                mSongsTitleList.add(current.getTitle());
            } while (cursor.moveToNext());
        }
    }

    private void changePlayPause() {
        if (!play) {
            mIvPlayPause.setImageResource(R.drawable.ic_pause);
            play = true;
        } else {
            mIvPlayPause.setImageResource(R.drawable.ic_play);
            play = false;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMediaPlayer.stop();
        mMediaPlayer.release();
        mMediaPlayer = null;
    }
}