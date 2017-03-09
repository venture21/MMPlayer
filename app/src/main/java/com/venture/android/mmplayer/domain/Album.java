package com.venture.android.mmplayer.domain;

import android.net.Uri;

import com.venture.android.mmplayer.util.TimeUtil;

import static android.R.attr.duration;


public class Album extends Common {
    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public String getArtist() {
        return null;
    }

    @Override
    public int getDuration() {
        return duration;
    }

    @Override
    public String getDurationText() {
        return TimeUtil.convertMiliToTime(duration);
    }


    @Override
    public Uri getImageUri() {
        return null;
    }
}