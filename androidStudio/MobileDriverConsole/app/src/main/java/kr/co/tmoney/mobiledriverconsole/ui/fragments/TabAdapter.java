package kr.co.tmoney.mobiledriverconsole.ui.fragments;

/**
 * Created by jinseo on 2016. 6. 25..
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.support.v4.app.FragmentPagerAdapter;

import kr.co.tmoney.mobiledriverconsole.R;
import kr.co.tmoney.mobiledriverconsole.utils.MDCUtils;


/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class TabAdapter extends FragmentPagerAdapter {

    private final String LOG_TAG = MDCUtils.getLogTag(TabAdapter.class);

    Context mContext;

    TripOnFragment mTripOnFragment;

    FareFragment mFareFragment;

    public TabAdapter(FragmentManager fm) {
        super(fm);
    }

    public TabAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.mContext = context;
        mTripOnFragment = new TripOnFragment();
        mFareFragment = new FareFragment();
    }

    public TabAdapter(FragmentManager fm, Context context, Fragment f1, Fragment f2) {
        super(fm);
        this.mContext = context;
        mTripOnFragment = (TripOnFragment) f1;
        mFareFragment = (FareFragment) f2;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a DummySectionFragment (defined as a static inner class
        // below) with the page number as its lone argument.
        Fragment fragment = null;
        switch(position) {
            case 0:
                //fragment = Fragment.instantiate(mContext, TripOnFragment.class.getName());
                fragment = mTripOnFragment;
                break;
            case 1:
//                fragment = Fragment.instantiate(mContext, FareFragment.class.getName());
                fragment = mFareFragment;
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