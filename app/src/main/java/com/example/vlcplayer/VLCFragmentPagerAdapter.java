package com.example.vlcplayer;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

public class VLCFragmentPagerAdapter extends android.support.v4.app.FragmentPagerAdapter {

    private Context mContext;

    public VLCFragmentPagerAdapter(Context context, FragmentManager fragmentManager) {
        super(fragmentManager);
        mContext = context;
    }

    @Override
    public Fragment getItem(int i) {
        if (i == 0) return new AudioFragment();
        else if (i == 1) return new VideoFragment();
        return null;
    }

    @Override
    public CharSequence getPageTitle(int i) {
        if (i == 0) return mContext.getString(R.string.tab_audio);
        else if (i == 1) return mContext.getString(R.string.tab_video);
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
