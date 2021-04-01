package com.example.vlcplayer;

public class AudioVideoFile {

    private String mTitle;
    private long mDuration;

    public AudioVideoFile(String title, long duration) {
        this.mTitle = title;
        this.mDuration = duration;
    }

    public String getTitle() { return mTitle; }
    public long getDuration() { return mDuration; }

}
