package com.example.administrator.qtravel.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.ViewGroup;

import com.example.administrator.qtravel.R;
import com.example.administrator.qtravel.global.MyApplication;
import com.example.administrator.qtravel.ui.search.VenueSearchFragment;

/**
 * Created by Administrator on 2018/4/9.
 */

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private final int PAGE_COUNT = 5;
    private VenueSearchFragment fragment1;
    private VenueSearchFragment fragment2;
    private VenueSearchFragment fragment3;
    private VenueSearchFragment fragment4;
    private VenueSearchFragment fragment5;

    public ViewPagerAdapter(FragmentManager fragmentManager){
        super(fragmentManager);
        fragment1 = VenueSearchFragment.newInstance(MyApplication.getContext().getString(R.string.fragment1));
        fragment2 = VenueSearchFragment.newInstance(MyApplication.getContext().getString(R.string.fragment2));
        fragment3 = VenueSearchFragment.newInstance(MyApplication.getContext().getString(R.string.fragment3));
        fragment4 = VenueSearchFragment.newInstance(MyApplication.getContext().getString(R.string.fragment4));
        fragment5 = VenueSearchFragment.newInstance(MyApplication.getContext().getString(R.string.fragment5));
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return MyApplication.getContext().getString(R.string.fragment1);
            case 1:
                return MyApplication.getContext().getString(R.string.fragment2);
            case 2:
                return MyApplication.getContext().getString(R.string.fragment3);
            case 3:
                return MyApplication.getContext().getString(R.string.fragment4);
            case 4:
                return MyApplication.getContext().getString(R.string.fragment5);
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position){
            case 0:
                fragment = fragment1;
                break;
            case 1:
                fragment = fragment2;
                break;
            case 2:
                fragment = fragment3;
                break;
            case 3:
                fragment = fragment4;
                break;
            case 4:
                fragment = fragment5;
                break;
            default:
                break;
        }
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        Log.d("fragment", "destroyItem: "+position);
        super.destroyItem(container, position, object);
    }
}
