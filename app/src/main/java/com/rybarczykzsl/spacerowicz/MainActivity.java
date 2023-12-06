package com.rybarczykzsl.spacerowicz;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.navigation.NavigationView;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telecom.Call;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SectionPagerAdapter pagerAdapter = new SectionPagerAdapter(getSupportFragmentManager());
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(pagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(pager);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.nav_open_drawer, R.string.nav_close_drawer);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Menu menuDrawer = navigationView.getMenu();
        MenuItem itemNavWalks = menuDrawer.findItem(R.id.nav_walks);
        Menu menuNavWalks = itemNavWalks.getSubMenu();
        populateMenu(menuNavWalks);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        for (Walk walk:
             Walk.walks) {
            menu.add(walk.getName());
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int groupId = item.getGroupId();
        int itemId = item.getItemId();
        Fragment fragment = null;
        Intent intent = null;

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Menu menuDrawer = navigationView.getMenu();
        for(int i=0;i<menuDrawer.size();i++){
            menuDrawer.getItem(i).setChecked(false);
        }
        MenuItem itemNavWalks = menuDrawer.findItem(R.id.nav_walks);
        Menu menuNavWalks = itemNavWalks.getSubMenu();
        for(int i=0;i<menuNavWalks.size();i++){
            menuNavWalks.getItem(i).setChecked(false);
        }

        item.setChecked(true);


        if(groupId == R.id.nav_group_walks){
            ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
            WalkDetailFragment walkDetailFragment = (WalkDetailFragment) getSupportFragmentManager().getFragments().get(0);
            WalkMeterFragment walkMeterFragment = (WalkMeterFragment) getSupportFragmentManager().getFragments().get(1);
            if(item.getOrder()!=walkDetailFragment.getWalkId()){
                walkMeterFragment.onClickReset();
                walkDetailFragment.setWalkId(item.getOrder());
                viewPager.setCurrentItem(0);
                walkDetailFragment.updateView();
            }

        } else if (groupId == R.id.nav_group_others) {
            if(itemId == R.id.nav_saved_walks){
                // TODO: REDIRECT TO SAVED WALKS ACTIVITY
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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

//    OPENS URL AFTER CLICKING ON IMAGE
    public void imageGoToUrl(View view){
        String url = view.getTag().toString();
        Uri uriUrl = Uri.parse(url);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        startActivity(launchBrowser);
    }

//  POPULATES TARGETTED MENU WITH ITEMS FROM WALKS STATIC ARRAY OF WALK CLASS
    private void populateMenu(Menu menu){
        for(int i=0; i<Walk.walks.length; i++){
            MenuItem item = menu.add(R.id.nav_group_walks, i, i, "• "+Walk.walks[i].getName());
            if(i==0){item.setChecked(true);}
        }
    }

}