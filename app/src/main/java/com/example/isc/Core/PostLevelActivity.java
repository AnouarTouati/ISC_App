package com.example.isc.Core;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.isc.R;

import java.util.ArrayList;
import java.util.Objects;

public class PostLevelActivity extends AppCompatActivity {
    ArrayList<String> postLevelList;
    PostLevelAdapter adapter;
    ListView postLevelListView;
    Button postLevelButton;
    static ArrayList<String> includedDepartments;

    String from;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_level);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(
                Html.fromHtml("<font color=\"#1976D2\"> Departments </font>")
        );
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_24dp);

        if(getIntent().getStringExtra("from") != null){
            from = getIntent().getStringExtra("from");
        }

        postLevelList = new ArrayList<String>(){{
            add("Communication");
            add("Media");
            add("Technicals");
            add("Human Resources");
            add("Exterior Relations");
        }};
        includedDepartments = new ArrayList<>();
        adapter = new PostLevelAdapter(getApplicationContext(), R.layout.activity_post_level_list_adapter, postLevelList);
        postLevelListView = findViewById(R.id.postLevelListView);
        postLevelListView.setAdapter(adapter);

        postLevelButton = findViewById(R.id.postLevelButton);
        postLevelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder departments = new StringBuilder();
                for(int i=0; i<includedDepartments.size(); i++){
                    departments.append(includedDepartments.get(i));
                    departments.append("\n");
                }
                if(from.equals("create")){
                    CreatePostActivity.checkedDepartments = departments.toString();
                    startActivity(new Intent(getApplicationContext(), CreatePostActivity.class));
                }else if(from.equals("edit")){
                    EditPostActivity.epCheckedDepartments = departments.toString();
                    startActivity(new Intent(getApplicationContext(), EditPostActivity.class));
                }

            }
        });
    }
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Discard the selection")
                .setMessage("Are you sure you want to discard the selection?")
                .setPositiveButton("Discard", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(from.equals("create")){
                            startActivity(new Intent(getApplicationContext(), CreatePostActivity.class));
                        }else if(from.equals("edit")){
                            startActivity(new Intent(getApplicationContext(), EditPostActivity.class));
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    @Override
    public boolean onSupportNavigateUp(){
        onBackPressed();
        return true;
    }
}
