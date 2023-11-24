package com.rybarczykzsl.spacerowicz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SectionPagerAdapter pagerAdapter = new SectionPagerAdapter(getSupportFragmentManager());
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(pagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(pager);

    }

    private class SectionPagerAdapter extends FragmentPagerAdapter {
        public SectionPagerAdapter(FragmentManager fm){super(fm);};

        @Override
        public int getCount(){return 2;}
        @Override
        public Fragment getItem(int position){
            switch (position){
                case 0:
                    return new WalkDetailFragment();
                case 1:
                    return new WalkMeterFragment();
            }
            return null;
        }
        @Override
        public CharSequence getPageTitle(int position){
            switch(position) {
                case 0:
                    return "Przegląd";
                case 1:
                    return "Licznik";
            }
            return null;
        }

    }
}