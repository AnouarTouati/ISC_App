package com.example.isc.Core;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class CreatePostViewPagerAdapter extends FragmentPagerAdapter {

    private  List<Fragment> fragmentsList=new ArrayList<>();

    public CreatePostViewPagerAdapter(FragmentManager fragmentManager){
        super(fragmentManager,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

    }
    void addFragment(Fragment fragment){
        fragmentsList.add(fragment);
    }
    @Override
    public Fragment getItem(int position) {
        return fragmentsList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentsList.size();
    }
}
