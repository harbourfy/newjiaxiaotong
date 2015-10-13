package com.app.jiaxiaotong.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.app.jiaxiaotong.activity.BaseActivity;
import com.app.jiaxiaotong.fragment.ChildFragment;
import com.app.jiaxiaotong.model.ChildModel;

import java.util.List;

/**
 * Created by ekfans-com on 2015/9/11.
 */
public class ChildViewPagerAdapter extends FragmentPagerAdapter {
    private List<ChildFragment> childFragments;
    public ChildViewPagerAdapter(FragmentManager fm,List<ChildFragment> childFragments){
        super(fm);
        this.childFragments = childFragments;
    }
    @Override
    public int getCount() {
        return childFragments.size();
    }

    @Override
    public Fragment getItem(int position) {
        return childFragments.get(position);
    }

//    @Override
//    public Object instantiateItem(ViewGroup container, int position) {
//        ChildFragment childFragment = (ChildFragment) super.instantiateItem(container,position);
//        return childFragment;
//    }
}
