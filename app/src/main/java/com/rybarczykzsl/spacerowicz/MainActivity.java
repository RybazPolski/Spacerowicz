package com.rybarczykzsl.spacerowicz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.navigation.NavigationView;
import androidx.appcompat.widget.Toolbar;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telecom.Call;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ViewPager pager;

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(WalkMeterFragment.NOTIFICATION_CHANNEL_PERMISSIONS, name, importance);
            channel.setDescription(description);
            // Register the channel with the system. You can't change the importance
            // or other notification behaviors after this.
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createNotificationChannel();

        setContentView(R.layout.activity_main);

        SectionPagerAdapter pagerAdapter = new SectionPagerAdapter(getSupportFragmentManager());
        pager = (ViewPager) findViewById(R.id.pager);
        if(pager!=null){
            pager.setAdapter(pagerAdapter);
            TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
            tabLayout.setupWithViewPager(pager);
        }else{
            WalkDetailFragment walkDetailFragment = new WalkDetailFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            walkDetailFragment.setWalk(0);
            ft.replace(R.id.walk_detail_container, walkDetailFragment);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.addToBackStack(null);
            ft.commit();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if(toolbar!=null && drawer!=null){
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
            if(pager!=null){
                ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
                WalkDetailFragment walkDetailFragment = (WalkDetailFragment) getSupportFragmentManager().getFragments().get(0);
                WalkMeterFragment walkMeterFragment = (WalkMeterFragment) getSupportFragmentManager().getFragments().get(1);
                if(item.getOrder()!=walkDetailFragment.getWalkId()){
                    walkMeterFragment.onClickReset();
                    walkDetailFragment.setWalkId(item.getOrder());
                    viewPager.setCurrentItem(0);
                    walkDetailFragment.updateView();
                }
            }else{
                WalkDetailFragment walkDetailFragment = new WalkDetailFragment();
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                walkDetailFragment.setWalk(item.getOrder());
                ft.replace(R.id.walk_detail_container, walkDetailFragment);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.addToBackStack(null);
                ft.commit();

                WalkMeterFragment walkMeterFragment = (WalkMeterFragment) getSupportFragmentManager().findFragmentById(R.id.walk_meter);
                if(walkMeterFragment != null){
                    walkMeterFragment.onClickReset();
                }
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

    private static class SectionPagerAdapter extends FragmentPagerAdapter {
        public SectionPagerAdapter(FragmentManager fm){super(fm);};

        @Override
        public int getCount(){return 2;}
        @Override
        @NonNull
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

//  POPULATES TARGETED MENU WITH ITEMS FROM WALKS STATIC ARRAY OF WALK CLASS
    private void populateMenu(Menu menu){
        for(int i=0; i<Walk.walks.length; i++){
            MenuItem item = menu.add(R.id.nav_group_walks, i, i, "• "+Walk.walks[i].getName());
            if(i==0){item.setChecked(true);}
        }
    }

    @Override
    // HANDLING NOTIFICATIONS WHEN PERMISSIONS ACCEPTED/REJECTED - CODE FROM E-BOOK
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        WalkMeterFragment walkMeterFragment = (WalkMeterFragment) getSupportFragmentManager().getFragments().get(1);
        if (requestCode==walkMeterFragment.PERMISSION_REQUEST_CODE) {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Intent intent = new Intent(this, ChronoodometerService.class);
                        this.bindService(intent, walkMeterFragment.connection, Context.BIND_AUTO_CREATE);
                    }
                }
    }

}