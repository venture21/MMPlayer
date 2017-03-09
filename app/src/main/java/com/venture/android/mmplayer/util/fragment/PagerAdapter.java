package com.venture.android.mmplayer.util.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by parkheejin on 2017. 2. 27..
 */

public class PagerAdapter extends FragmentStatePagerAdapter{

    List<Fragment> fragments;

    public PagerAdapter(FragmentManager fm) {
            super(fm);
        this.fragments = new ArrayList<>();
    }

    public void add(Fragment fragment){
        fragments.add(fragment);
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }


    @Override
    public int getCount() {
            return fragments.size();
        }


}
