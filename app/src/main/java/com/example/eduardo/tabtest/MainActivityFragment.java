package com.example.eduardo.tabtest;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private static final int NUM_ITEMS = 4;

    private static final int MAP_ITEM = 0;
    private static final int MESSAGES_ITEM = 1;
    private static final int GROUPS_ITEM = 2;
    private static final int CONTACTS_ITEM = 3;

    private static final String[] TAB_TITLES = {"MAP", "MESSAGES", "GROUPS", "CONTACTS"};

    MyAdapter mAdapter;

    ViewPager viewPager;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        viewPager = (ViewPager) rootView.findViewById(R.id.view_pager);
        mAdapter = new MyAdapter(getActivity().getSupportFragmentManager());
        viewPager.setAdapter(mAdapter);

        TabLayout tabLayout = (TabLayout) rootView.findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        return rootView;
    }


    public static class MyAdapter extends FragmentPagerAdapter {
        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case MAP_ITEM: return new MapTabFragment();
                case MESSAGES_ITEM: return new ContactTabFragment();
                case GROUPS_ITEM: return new GroupTabFragment();
                case CONTACTS_ITEM: return new ContactTabFragment();
            }
            return new ContactTabFragment();
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public CharSequence getPageTitle(int position){
            if(position < TAB_TITLES.length ) return TAB_TITLES[position];

            return "Error";
        }
    }
}
