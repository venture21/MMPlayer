package com.venture.android.mmplayer.domain;

import android.net.Uri;

/**
 * Created by parkheejin on 2017. 2. 28..
 */

public abstract class Common {
    public abstract String getTitle();
    public abstract String getArtist();
    public abstract int  getDuration();
    public abstract String getDurationText();
    public abstract Uri getImageUri();
}
