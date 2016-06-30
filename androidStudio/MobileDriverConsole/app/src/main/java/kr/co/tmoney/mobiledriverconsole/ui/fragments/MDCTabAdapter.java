package kr.co.tmoney.mobiledriverconsole.ui.fragments;

/**
 * Created by jinseo on 2016. 6. 25..
 */

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import kr.co.tmoney.mobiledriverconsole.R;
import kr.co.tmoney.mobiledriverconsole.utils.MDCUtils;


/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class MDCTabAdapter extends FragmentPagerAdapter {

    private final String LOG_TAG = MDCUtils.getLogTag(MDCTabAdapter.class);

    Context mContext;

    public MDCTabAdapter(FragmentManager fm) {
        super(fm);
    }

    public MDCTabAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a DummySectionFragment (defined as a static inner class
        // below) with the page number as its lone argument.
        Fragment fragment = null;
        switch(position) {
            case 0:
                fragment = Fragment.instantiate(mContext, TripOnFragment.class.getName());
//                fragment = Fragment.instantiate(mContext, TripOffFragment.class.getName());
                break;
            case 1:
                fragment = Fragment.instantiate(mContext, FareFragment.class.getName());
//                fragment = Fragment.instantiate(mContext, TripOffFragment.class.getName());
                break;
            default:
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return mContext.getResources().getString(R.string.first_tab_name);
            case 1:
                return mContext.getResources().getString(R.string.second_tab_name);
        }
        return null;
    }
}