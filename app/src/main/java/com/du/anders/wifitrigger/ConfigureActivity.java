package com.du.anders.wifitrigger;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;

import com.du.anders.wifitrigger.fragments.ConnectedFragment;
import com.du.anders.wifitrigger.fragments.DisConnectFragment;

public class ConfigureActivity extends Activity {

    public static final String EXTRA_NETWORK = "network";
    /**
     * The number of pages (wizard steps) to show in this demo.
     */
    private static final int PAGE_CONNECTED = 0;
    private static final int PAGE_DISCONNECT = 1;
    private static final int NUM_PAGES = 2;

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;

    private String mNetworkSSID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure);
        mNetworkSSID = getIntent().getStringExtra(EXTRA_NETWORK);

        setTitle(mNetworkSSID);
        setActionBar();

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager);
        /*
        The pager adapter, which provides the pages to the view pager widget.
        */
        PagerAdapter mPagerAdapter = new ScreenSlidePagerAdapter(getFragmentManager());
        mPager.setAdapter(mPagerAdapter);

    }

    public void setActionBar() {
        final ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentPagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment;
            switch (position) {
                case PAGE_CONNECTED:
                    fragment = ConnectedFragment.newInstance(mNetworkSSID);
                    break;
                case PAGE_DISCONNECT:
                    fragment = DisConnectFragment.newInstance(mNetworkSSID);
                    break;
                default:
                    fragment = ConnectedFragment.newInstance(mNetworkSSID);
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

    /*public void onFragmentInteraction(String id) {

    }*/
}
