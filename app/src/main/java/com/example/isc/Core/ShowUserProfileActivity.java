package com.example.isc.Core;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.isc.Common;
import com.example.isc.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Objects;

public class ShowUserProfileActivity extends AppCompatActivity {

    private TextView userName, userPosition, userStudentNumber, userEmail;

    private ImageView profileImageIV;
    private FloatingActionButton userPostsUpButton;
    private ScrollView scrollView;

    private NonScrollListView userPostsListView;
    private ShowUserProfilePostsListAdapter adapter;
    private ArrayList<MyPost> postArrayList = new ArrayList<>();

    Boolean userPostIsVisible = false;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_user_profile);
        try {
            Intent intent = getIntent();
            String[] user = intent.getStringArrayExtra("user");

            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.img0_vector);

            Objects.requireNonNull(getSupportActionBar()).setTitle(
                    Html.fromHtml("<font color=\"#1976D2\">" + user[0] + "</font>")
            );
            userName = findViewById(R.id.userNameTextView);
            userPosition = findViewById(R.id.userPositionTextView);
            userName.setText(user[0]);
            userPosition.setText(user[1]);
            userStudentNumber = findViewById(R.id.userStudentNumberTextView);
            userStudentNumber.setText(user[4]);
            userEmail = findViewById(R.id.userEmailTextView);
            userEmail.setText(user[3]);
            userPostsUpButton = findViewById(R.id.userProfileUpButton);
            profileImageIV = findViewById(R.id.userProfileImage);
            if(Common.convertStringToBitmap(user[2])!=null){
                profileImageIV.setImageBitmap(Common.convertStringToBitmap(user[2]));
            }else{
                profileImageIV.setImageResource(R.drawable.ic_person_blue_24dp);
            }


/*
        final MyPost myPost0 = new MyPost(
                myUser0,
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nunc eget rhoncus erat, ac tristique nisi. Praesent ipsum erat, pulvinar pellentesque arcu quis, pellentesque maximus elit. Curabitur ullamcorper blandit magna in aliquet. In sit amet ipsum nec odio semper vehicula eu ut tellus. Ut quam libero, posuere id nisl tristique, dignissim ornare libero. Vestibulum arcu metus, convallis eu ipsum ac, lobortis vehicula ex. Vivamus sit amet velit nibh. Fusce ac lectus at augue mollis fringilla non nec odio. Nunc elementum suscipit egestas. Sed scelerisque posuere placerat. Proin a iaculis urna, in sagittis tortor. Morbi orci urna, venenatis ac rhoncus vel, volutpat sed lacus.",
                R.drawable.img1,
                "Communication",
                "Anis",
                "#Injaz");
        final MyPost myPost1 = new MyPost(
                myUser0,
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nunc eget rhoncus erat, ac tristique nisi. Praesent ipsum erat, pulvinar pellentesque arcu quis, pellentesque maximus elit. Curabitur ullamcorper blandit magna in aliquet. In sit amet ipsum nec odio semper vehicula eu ut tellus. Ut quam libero, posuere id nisl tristique, dignissim ornare libero. Vestibulum arcu metus, convallis eu ipsum ac, lobortis vehicula ex. Vivamus sit amet velit nibh. Fusce ac lectus at augue mollis fringilla non nec odio. Nunc elementum suscipit egestas. Sed scelerisque posuere placerat. Proin a iaculis urna, in sagittis tortor. Morbi orci urna, venenatis ac rhoncus vel, volutpat sed lacus.",
                R.drawable.img1,
                "Communication",
                "Anis",
                "#Injaz");
        final MyPost myPost2 = new MyPost(
                myUser0,
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nunc eget rhoncus erat, ac tristique nisi. Praesent ipsum erat, pulvinar pellentesque arcu quis, pellentesque maximus elit. Curabitur ullamcorper blandit magna in aliquet. In sit amet ipsum nec odio semper vehicula eu ut tellus. Ut quam libero, posuere id nisl tristique, dignissim ornare libero. Vestibulum arcu metus, convallis eu ipsum ac, lobortis vehicula ex. Vivamus sit amet velit nibh. Fusce ac lectus at augue mollis fringilla non nec odio. Nunc elementum suscipit egestas. Sed scelerisque posuere placerat. Proin a iaculis urna, in sagittis tortor. Morbi orci urna, venenatis ac rhoncus vel, volutpat sed lacus.",
                R.drawable.img1,
                "Communication",
                "Anis",
                "#Injaz");
        final MyPost myPost3 = new MyPost(
                myUser0,
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nunc eget rhoncus erat, ac tristique nisi. Praesent ipsum erat, pulvinar pellentesque arcu quis, pellentesque maximus elit. Curabitur ullamcorper blandit magna in aliquet. In sit amet ipsum nec odio semper vehicula eu ut tellus. Ut quam libero, posuere id nisl tristique, dignissim ornare libero. Vestibulum arcu metus, convallis eu ipsum ac, lobortis vehicula ex. Vivamus sit amet velit nibh. Fusce ac lectus at augue mollis fringilla non nec odio. Nunc elementum suscipit egestas. Sed scelerisque posuere placerat. Proin a iaculis urna, in sagittis tortor. Morbi orci urna, venenatis ac rhoncus vel, volutpat sed lacus.",
                R.drawable.img1,
                "Communication",
                "Anis",
                "#Injaz");
        postArrayList = new ArrayList<MyPost>(){{
            add(myPost0);
            add(myPost1);
            add(myPost2);
            add(myPost3);
        }};
*/
            adapter = new ShowUserProfilePostsListAdapter(getApplicationContext(), R.layout.activity_home_list_adapter, postArrayList);
            userPostsListView = findViewById(R.id.userPostsListView);
            userPostsListView.setAdapter(adapter);
            userPostsListView.setScrollContainer(false);


            scrollView = findViewById(R.id.userScrollView);
            userPostsUpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    scrollView.fullScroll(ScrollView.FOCUS_UP);
                }
            });
        } catch (Exception e){
            Log.v("AppLogic","Something went wrong "
                    +"the cause: " +e.getCause()
                    + "the message: "+e.getMessage()
            );
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), CoreActivity.class);
        intent.putExtra("to", "home");
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
