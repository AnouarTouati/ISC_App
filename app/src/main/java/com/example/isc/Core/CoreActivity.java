package com.example.isc.Core;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.isc.Core.Fragments.HomeFragment;
import com.example.isc.Core.Fragments.NotificationFragment;
import com.example.isc.Core.Fragments.ProfileFragment;
import com.example.isc.R;

import java.util.Objects;

public class CoreActivity extends AppCompatActivity {

    Fragment homeFragment, notificationFragment, profileFragment;
    ImageButton homeTabButton, notificationTabButton, profileTabButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_core);


        homeFragment = new HomeFragment();
        notificationFragment = new NotificationFragment();
        profileFragment = new ProfileFragment();


        homeTabButton = findViewById(R.id.homeTabButton);
        notificationTabButton = findViewById(R.id.notificationTabButton);
        profileTabButton = findViewById(R.id.profileTabButton);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.img0_vector);

        Intent intent = getIntent();
        try{
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
        }catch(NullPointerException e){
            setHome();
        }
    }

    public void switchFragment(View view){
        int buttonId = view.getId();
        switch (buttonId){
            case R.id.homeTabButton: // home
                setHome();
                break;
            case R.id.notificationTabButton: // notification
                setNotification();
                break;
            case R.id.profileTabButton: // profile
                setProfile();
                break;
        }
    }
    public void openFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentsFrame, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
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

    public void setHome(){
        openFragment(homeFragment);
        homeTabButton.setImageResource(R.drawable.ic_home_blue_24dp);
        notificationTabButton.setImageResource(R.drawable.ic_notifications_black_24dp);
        profileTabButton.setImageResource(R.drawable.ic_person_black_24dp);
        Objects.requireNonNull(getSupportActionBar()).setTitle(
                Html.fromHtml("<font color=\"#1976D2\"> Home </font>")
        );

    }
    public void setNotification(){
        openFragment(notificationFragment);
        homeTabButton.setImageResource(R.drawable.ic_home_black_24dp);
        notificationTabButton.setImageResource(R.drawable.ic_notifications_blue_24dp);
        profileTabButton.setImageResource(R.drawable.ic_person_black_24dp);
        Objects.requireNonNull(getSupportActionBar()).setTitle(
                Html.fromHtml("<font color=\"#1976D2\"> Notification </font>")
        );
        HomeFragment.index = HomeFragment.postListView.getFirstVisiblePosition();
        View v = HomeFragment.postListView.getChildAt(0);
        HomeFragment.top = (v == null) ? 0 : (v.getTop() - HomeFragment.postListView.getPaddingTop());
    }
    public void setProfile(){
        openFragment(profileFragment);
        homeTabButton.setImageResource(R.drawable.ic_home_black_24dp);
        notificationTabButton.setImageResource(R.drawable.ic_notifications_black_24dp);
        profileTabButton.setImageResource(R.drawable.ic_person_blue_24dp);
        Objects.requireNonNull(getSupportActionBar()).setTitle(
                Html.fromHtml("<font color=\"#1976D2\"> Profile </font>")
        );
        HomeFragment.index = HomeFragment.postListView.getFirstVisiblePosition();
        View v = HomeFragment.postListView.getChildAt(0);
        HomeFragment.top = (v == null) ? 0 : (v.getTop() - HomeFragment.postListView.getPaddingTop());
    }
}











