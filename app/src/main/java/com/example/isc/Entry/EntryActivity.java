package com.example.isc.Entry;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.isc.Core.CoreActivity;
import com.example.isc.Core.CreatePostViewPagerAdapter;
import com.example.isc.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class EntryActivity extends AppCompatActivity {


     ViewPager viewPager;
     CreatePostViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);
   try{
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            finish();
            startActivity(new Intent(getApplicationContext(), CoreActivity.class));
        } else{
            viewPager=findViewById(R.id.entryActivityViewPager);
            viewPagerAdapter=new CreatePostViewPagerAdapter(getSupportFragmentManager());
            viewPagerAdapter.addFragment(new LogoFragment());
            viewPagerAdapter.addFragment(new SliderFragment());
            viewPager.setAdapter(viewPagerAdapter);


            Objects.requireNonNull(getSupportActionBar()).hide();


            Intent intent = getIntent();
            if (Objects.equals(intent.getStringExtra("interior"), "true")) {
                viewPager.setCurrentItem(1);

            } else {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        viewPager.setCurrentItem(1);
                    }
                }, 3000);
            }
        }}catch (Exception e){
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
}
