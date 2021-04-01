package com.example.vlcplayer;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<Boolean> musicPlaying = new MutableLiveData<>();
    private final MutableLiveData<Boolean> videoPlaying = new MutableLiveData<>();

    public void setMusicPlaying(boolean musicPlaying) {
        this.musicPlaying.setValue(musicPlaying);
    }

    public void setVideoPlaying(boolean videoPlaying) {
        this.videoPlaying.setValue(videoPlaying);
    }

    public LiveData<Boolean> isMusicPlaying() { return musicPlaying; }

    public LiveData<Boolean> isVideoPlaying() {
        return videoPlaying;
    }
}
