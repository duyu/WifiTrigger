package com.anders.wifitrigger;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.anders.wifitrigger.fragments.ConfigureFragment;

public class ConfigureActivity extends Activity implements ActionBar.TabListener{
    private static final String LOG_TAG = ConfigureActivity.class.getSimpleName();

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
        final ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(false);
        }

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager);
        /*
        The pager adapter, which provides the pages to the view pager widget.
        */
        PagerAdapter mPagerAdapter = new ScreenSlidePagerAdapter(getFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }

    }

    public void setActionBar() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.config_menu, menu);

        final String key = mNetworkSSID + G.KEY_CONFIG_STATUS_POSTFIX;
        Switch actionSwitch = (Switch)menu.findItem(R.id.config_switch).getActionView().findViewById(R.id.switchForActionBar);
        actionSwitch.setChecked(((G) getApplication()).getConfigStatus(key));
        actionSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                ((G)getApplication()).setConfigStatus(key, isChecked);
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                finish();
                return true;
            case R.id.config_switch:
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

    /* The following are each of the ActionBar.TabListener callbacks */

    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        // Check if the fragment is already initialized
        /*if (mFragment == null) {
            // If not, instantiate and add it to the activity
            mFragment = Fragment.instantiate(mActivity, mClass.getName());
            ft.add(android.R.id.content, mFragment, mTag);
        } else {
            // If it exists, simply attach it in order to show it
            ft.attach(mFragment);
        }*/
        mPager.setCurrentItem(tab.getPosition());
    }

    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
        /*if (mFragment != null) {
            // Detach the fragment, because another one is being attached
            ft.detach(mFragment);
        }*/
    }

    public void onTabReselected(Tab tab, FragmentTransaction ft) {
        // User selected the already selected tab. Usually do nothing.
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
                    fragment = ConfigureFragment.newInstance(mNetworkSSID, true);
                    break;
                case PAGE_DISCONNECT:
                    fragment = ConfigureFragment.newInstance(mNetworkSSID, false);
                    break;
                default:
                    fragment = ConfigureFragment.newInstance(mNetworkSSID, true);
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case PAGE_CONNECTED:
                    return getResources().getString(R.string.fragment_connect_title);
                case PAGE_DISCONNECT:
                    return getResources().getString(R.string.fragment_disconnect_title);
            }
            return null;
        }
    }

    /*public void onFragmentInteraction(String id) {

    }*/
}
