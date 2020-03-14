package com.example.isc.Entry;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.isc.LoginSignUp.LoginActivity;
import com.example.isc.R;
public class SliderFragment extends Fragment {

    private  ViewPager viewPager;
    private  SliderAdapter imageAdapter;
    private  LinearLayout sliderDotsPanel;
    private  int dotsCount;
    private  ImageView[] dots;
    private  Button nextSignUpButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_slider, container, false);
        try {
            nextSignUpButton = fragmentView.findViewById(R.id.nextSignUpButton);
            nextSignUpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    nextLogin(v);
                }
            });

            viewPager = fragmentView.findViewById(R.id.viewPager);
            imageAdapter = new SliderAdapter(getContext());
            viewPager.setAdapter(imageAdapter);

            sliderDotsPanel = fragmentView.findViewById(R.id.SliderDots);
            dotsCount = imageAdapter.getCount();
            dots = new ImageView[dotsCount];

            for (int i = 0; i < dotsCount; i++) {
                dots[i] = new ImageView(getContext());
                dots[i].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.nonactive_dot));
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(8, 0, 8, 0);
                sliderDotsPanel.addView(dots[i], params);

            }
            dots[0].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.active_dot));

            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageSelected(int position) {
                    for (int i = 0; i < dotsCount; i++) {
                        dots[i].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.nonactive_dot));
                    }
                    dots[position].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.active_dot));
                }

                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    switch (viewPager.getCurrentItem()) {
                        case 0:
                            nextSignUpButton.setText(R.string.next);
                            break;
                        case 1:
                            nextSignUpButton.setText(R.string.next);
                            break;
                        case 2:
                            nextSignUpButton.setText(R.string.login);
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });
        }catch (Exception e){
            Log.v("AppLogic","Something went wrong "
                    +"the cause: " +e.getCause()
                    + "the message: "+e.getMessage()
            );
        }
        return fragmentView;
    }
    public void nextLogin(View view){
        switch(viewPager.getCurrentItem()){
            case 0:
                viewPager.setCurrentItem(1);
                nextSignUpButton.setText(R.string.next);
                break;
            case 1:
                viewPager.setCurrentItem(2);
                nextSignUpButton.setText(R.string.login);
                break;
            case 2:
                Intent i = new Intent(getContext(), LoginActivity.class);
                startActivity(i);
        }
    }

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SliderFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SliderFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SliderFragment newInstance(String param1, String param2) {
        SliderFragment fragment = new SliderFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

}
