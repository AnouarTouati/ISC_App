package com.example.isc.Core;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.isc.R;

import java.util.ArrayList;
import java.util.Objects;

public class TagColleagueActivity extends AppCompatActivity {

    static ArrayList<String> taggedColleagues;
    ArrayList<MyUser> colleagues;
    TagColleagueAdapter tagColleagueAdapter;
    ListView colleagueListView;
    Button tagColleagueButton;
    SearchView searchColleague;

    String from;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_colleague);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(
                Html.fromHtml("<font color=\"#1976D2\"> Tag a colleague </font>")
        );
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_24dp);

        if(getIntent().getStringExtra("from") != null){
            from = getIntent().getStringExtra("from");
        }

        taggedColleagues = new ArrayList<>();
        colleagues = new ArrayList<MyUser>(){{
            add(new MyUser("Garbage",null, "Zineddine", "Head"));
            add(new MyUser("Garbage",null, "Mohamed", "Head"));
            add(new MyUser("Garbage",null, "Islem", "Member"));
            add(new MyUser("Garbage",null, "Bettouche", "Head"));
            add(new MyUser("Garbage",null, "Adel", "Member"));
            add(new MyUser("Garbage",null, "Nedjem Eddine", "Head"));
            add(new MyUser("Garbage",null, "Saad Eddine", "Member"));
        }};
        tagColleagueAdapter = new TagColleagueAdapter(getApplicationContext(), R.layout.activity_tag_colleague_list_adapter, colleagues);
        colleagueListView = findViewById(R.id.colleagueListView);
        colleagueListView.setAdapter(tagColleagueAdapter);

        tagColleagueButton = findViewById(R.id.tagColleagueButton);
        tagColleagueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder tagColleague = new StringBuilder();
                for(int i=0; i<taggedColleagues.size(); i++){
                    tagColleague.append("@");
                    tagColleague.append(taggedColleagues.get(i));
                    tagColleague.append("\n");
                }
                if(from.equals("create")){
                    CreatePostActivity.colleagues = tagColleague.toString();
                    startActivity(new Intent(getApplicationContext(), CreatePostActivity.class));
                }else if(from.equals("edit")){
                    EditPostActivity.epColleagues = tagColleague.toString();
                    startActivity(new Intent(getApplicationContext(), CreatePostActivity.class));
                }

            }
        });
        searchColleague = findViewById(R.id.searchColleague);
        searchColleague.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                tagColleagueAdapter.filter(newText);
                return true;
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
