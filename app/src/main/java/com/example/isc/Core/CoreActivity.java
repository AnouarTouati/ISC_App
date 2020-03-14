package com.example.isc.Core;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.isc.Core.Fragments.HomeFragment;
import com.example.isc.Core.Fragments.NotificationFragment;
import com.example.isc.Core.Fragments.ProfileFragment;
import com.example.isc.R;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

public class CoreActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private TabItem homeTabItem, notificationTabItem, profileTabItem;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_core);
        try {
            homeTabItem = findViewById(R.id.homeTabItem);
            notificationTabItem = findViewById(R.id.notificationTabItem);
            profileTabItem = findViewById(R.id.profileTabItem);

            tabLayout = findViewById(R.id.coreActivityTabLayout);
            viewPager = findViewById(R.id.coreActivityViewPager);

            CreatePostViewPagerAdapter pagerAdapter = new CreatePostViewPagerAdapter(getSupportFragmentManager());
            pagerAdapter.addFragment(new HomeFragment());
            pagerAdapter.addFragment(new NotificationFragment());
            pagerAdapter.addFragment(new ProfileFragment());

            viewPager.setAdapter(pagerAdapter);
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int i, float v, int i1) {

                }

                @Override
                public void onPageSelected(int i) {

                    Objects.requireNonNull(tabLayout.getTabAt(i)).select();

                }

                @Override
                public void onPageScrollStateChanged(int i) {

                }
            });
            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    viewPager.setCurrentItem(tab.getPosition());
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });

            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.img0_vector);

            Intent intent = getIntent();
            try {
                switch (Objects.requireNonNull(intent.getStringExtra("to"))) {
                    case "notification":
                        setNotification();
                        break;
                    case "profile":
                        setProfile();
                        break;
                    default:
                        setHome();
                }
            } catch (NullPointerException e) {
                setHome();
            }
        }catch (Exception e){
            Log.v("AppLogic","Something went wrong "
           +"the cause: " +e.getCause()
           + "the message: "+e.getMessage()
            );
        }

    }
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void setHome() {
        viewPager.setCurrentItem(0);
    }

    private void setNotification() {
        viewPager.setCurrentItem(1);

    }
    private void setProfile() {
        viewPager.setCurrentItem(2);

    }
}











