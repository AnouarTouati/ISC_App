package com.example.isc.Core;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.isc.R;

import java.util.ArrayList;

public class TagColleagueActivity extends Fragment {

    static ArrayList<String> taggedColleagues;
    ArrayList<MyUser> colleagues;
    TagColleagueAdapter tagColleagueAdapter;
    ListView colleagueListView;
    Button tagColleagueButton;
    SearchView searchColleague;


    View view;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.activity_tag_colleague,container,false);


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
        tagColleagueAdapter = new TagColleagueAdapter(getActivity().getApplicationContext(), R.layout.activity_tag_colleague_list_adapter, colleagues);
        colleagueListView = view.findViewById(R.id.colleagueListView);
        colleagueListView.setAdapter(tagColleagueAdapter);

        tagColleagueButton = view.findViewById(R.id.tagColleagueButton);
        tagColleagueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder tagColleague = new StringBuilder();
                for(int i=0; i<taggedColleagues.size(); i++){
                    tagColleague.append("@");
                    tagColleague.append(taggedColleagues.get(i));
                    tagColleague.append("\n");
                }
                ((CreatePostActivity)getActivity()).colleagues=tagColleague.toString();
                ((CreatePostActivity)getActivity()).returnViewToTheParentActivity();

            }
        });
        searchColleague =view.findViewById(R.id.searchColleague);
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

        return view;
    }


}
