package com.venture.android.mmplayer.domain;

import android.net.Uri;

import com.venture.android.mmplayer.util.TimeUtil;

import java.util.List;

import static android.R.attr.duration;

/**
 * Created by parkheejin on 2017. 2. 28..
 */

public class Artist extends Common{
    public int id;
    public String artist;
    public String artist_key;
    public int album_id;
    public Uri album_image_uri;
    public int number_of_tracks;
    public int number_of_albums;
    public List<Music> musics;

    @Override
    public String getTitle() {
        return artist;
    }

    @Override
    public String getArtist() {
        return "Tracks : " + number_of_tracks;
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
        return album_image_uri;
    }
}