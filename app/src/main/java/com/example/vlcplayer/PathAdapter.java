package com.example.vlcplayer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class PathAdapter extends ArrayAdapter<AudioVideoFile> {

    private Context mContext;
    private List<AudioVideoFile> list;

    public PathAdapter(@NonNull Context context, ArrayList<AudioVideoFile> list) {
        super(context, R.layout.lv_layout, list);
        mContext = context;
        this.list = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) listItem = LayoutInflater.from(mContext).inflate(R.layout.lv_layout, parent, false);
        AudioVideoFile item = list.get(position);
        TextView tvTitle = listItem.findViewById(R.id.tv_title);
        TextView tvDuration = listItem.findViewById(R.id.tv_duration);
        tvTitle.setText(item.getTitle());

        String duration = "";
        long milliseconds = item.getDuration();
        long minutes = (milliseconds / 1000) / 60;
        long seconds = (milliseconds / 1000) % 60;

        if (minutes == 0) duration += "00:";
        else {
            String minString = Long.toString(minutes);
            if (minString.length() == 1) {
                duration += ("0" + minString + ":");
            } else duration += minString;
        }

        if (seconds == 0) duration += "00";
        else {
            String secString = Long.toString(seconds);
            if (secString.length() == 1) {
                duration += ("0" + secString);
            } else duration += secString;
        }

        tvDuration.setText(duration);
        return listItem;
    }
}
