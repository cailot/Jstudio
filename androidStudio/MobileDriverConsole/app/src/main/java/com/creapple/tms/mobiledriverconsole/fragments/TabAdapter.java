package com.creapple.tms.mobiledriverconsole.fragments;

/**
 * Created by jinseo on 2016. 6. 25..
 */

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.view.ViewGroup;

import com.creapple.tms.mobiledriverconsole.R;
import com.creapple.tms.mobiledriverconsole.utils.Constants;

import java.util.HashMap;
import java.util.Map;


/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class TabAdapter extends FragmentPagerAdapter{

    Context mContext;

    Map<Integer, String> mTags;

    FragmentManager mFragementManager;

    TripOnFragment mTripOnFragment;

    FareFragment mFareFragment;

    SettingFragment mSettingFragment;

    public TabAdapter(FragmentManager fm) {
        super(fm);
    }

    public TabAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.mContext = context;
        mFragementManager = fm;
        mTripOnFragment = new TripOnFragment();
        mFareFragment = new FareFragment();
        mSettingFragment = new SettingFragment();
        mTags = new HashMap<Integer, String>();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Object object = super.instantiateItem(container, position);
        if(object instanceof Fragment){
            Fragment fragment = (Fragment) object;
            String tag = fragment.getTag();
//
//            Log.e("##################", tag);

            mTags.put(position, tag);
        }
        return object;
    }

    public Fragment getFragment(int positon){
        String tag = mTags.get(positon);
        if(tag==null){
            return null;
        }
        return mFragementManager.findFragmentByTag(tag);
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a DummySectionFragment (defined as a static inner class
        // below) with the page number as its lone argument.
        Fragment fragment = null;
        switch(position) {
            case Constants.TRIP_ON_FRAGMENT_TAB:
                fragment = mTripOnFragment;
                break;
            case Constants.FARE_FRAGMENT_TAB:
                fragment = mFareFragment;
                break;
            case Constants.SETTING_FRAGMENT_TAB:
                fragment = mSettingFragment;
            default:
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
//        switch (position) {
//            case Constants.TRIP_ON_FRAGMENT_TAB:
//                return mContext.getResources().getString(R.string.first_tab_name);
//            case Constants.FARE_FRAGMENT_TAB:
//                return mContext.getResources().getString(R.string.second_tab_name);
//            case Constants.SETTING_FRAGMENT_TAB:
//                return mContext.getResources().getString(R.string.third_tab_name);
//        }
//        return null;




//        SpannableStringBuilder sb;
//        ImageSpan span;
//        Drawable myDrawable;
//        switch (position) {
//            case Constants.TRIP_ON_FRAGMENT_TAB:
//                myDrawable = mContext.getResources().getDrawable(R.mipmap.ic_touch_app_black_24dp);
//                sb = new SpannableStringBuilder("  " + mContext.getResources().getString(R.string.first_tab_name)); // space added before text for convenience
//
//                myDrawable.setBounds(0, 0, myDrawable.getIntrinsicWidth(), myDrawable.getIntrinsicHeight());
//                span = new ImageSpan(myDrawable, ImageSpan.ALIGN_BASELINE);
//                sb.setSpan(span, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//                return sb;
//            case Constants.FARE_FRAGMENT_TAB:
//                myDrawable = mContext.getResources().getDrawable(R.mipmap.ic_touch_app_black_24dp);
//                sb = new SpannableStringBuilder("  " + mContext.getResources().getString(R.string.second_tab_name)); // space added before text for convenience
//
//                myDrawable.setBounds(0, 0, myDrawable.getIntrinsicWidth(), myDrawable.getIntrinsicHeight());
//                span = new ImageSpan(myDrawable, ImageSpan.ALIGN_BASELINE);
//                sb.setSpan(span, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//                return sb;
//            case Constants.SETTING_FRAGMENT_TAB:
//                myDrawable = mContext.getResources().getDrawable(R.mipmap.ic_touch_app_black_24dp);
//                sb = new SpannableStringBuilder("  " + mContext.getResources().getString(R.string.third_tab_name)); // space added before text for convenience
//
//                myDrawable.setBounds(0, 0, myDrawable.getIntrinsicWidth(), myDrawable.getIntrinsicHeight());
//                span = new ImageSpan(myDrawable, ImageSpan.ALIGN_BASELINE);
//                sb.setSpan(span, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//                return sb;
//
//        }
//        return null;








    Drawable myDrawable = null;
    String title = null;



            switch (position) {
                case Constants.TRIP_ON_FRAGMENT_TAB:
                    myDrawable = mContext.getResources().getDrawable(R.mipmap.ic_touch_app_black_24dp);
                    title = mContext.getResources().getString(R.string.first_tab_name); // space added before text for convenience
                    break;
                case Constants.FARE_FRAGMENT_TAB:
                    myDrawable = mContext.getResources().getDrawable(R.mipmap.ic_touch_app_black_24dp);
                    title = mContext.getResources().getString(R.string.second_tab_name); // space added before text for convenience
                    break;
                case Constants.SETTING_FRAGMENT_TAB:
                    myDrawable = mContext.getResources().getDrawable(R.mipmap.ic_touch_app_black_24dp);
                    title = mContext.getResources().getString(R.string.third_tab_name); // space added before text for convenience
                    break;
            }



    SpannableStringBuilder sb = new SpannableStringBuilder("   " + title); // space added before text for convenience
    try {
        myDrawable.setBounds(5, 5, myDrawable.getIntrinsicWidth(), myDrawable.getIntrinsicHeight());
        ImageSpan span = new ImageSpan(myDrawable, DynamicDrawableSpan.ALIGN_BASELINE);
        sb.setSpan(span, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    } catch (Exception e) {
        // TODO: handle exception
    }



    return sb;}




}