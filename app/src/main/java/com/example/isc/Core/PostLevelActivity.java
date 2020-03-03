package com.example.isc.Core;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.isc.R;

import java.util.ArrayList;
import java.util.Objects;

public class PostLevelActivity extends Fragment {
    ArrayList<String> postLevelList;
    PostLevelAdapter adapter;
    ListView postLevelListView;
    Button postLevelButton;
    static ArrayList<String> includedDepartments;



    View view;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       view=inflater.inflate(R.layout.activity_post_level,container,false);

        postLevelList = new ArrayList<String>(){{
            add("Communication");
            add("Media");
            add("Technicals");
            add("Human Resources");
            add("Exterior Relations");
        }};
        includedDepartments = new ArrayList<>();
        adapter = new PostLevelAdapter(Objects.requireNonNull(getActivity()).getApplicationContext(), R.layout.activity_post_level_list_adapter, postLevelList);
        postLevelListView = view.findViewById(R.id.postLevelListView);
        postLevelListView.setAdapter(adapter);

        postLevelButton = view.findViewById(R.id.postLevelButton);
        postLevelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder departments = new StringBuilder();
                for(int i=0; i<includedDepartments.size(); i++){
                    departments.append(includedDepartments.get(i));
                    departments.append("\n");
                }
                if(getActivity()!=null){
                    if(getActivity() instanceof CreatePostActivity){
                        ((CreatePostActivity)getActivity()).checkedDepartments=departments.toString();
                        ((CreatePostActivity)getActivity()).returnViewToTheParentActivity();
                    }
                    else if(getActivity() instanceof EditPostActivity){
                        ((EditPostActivity)getActivity()).epCheckedDepartments=departments.toString();
                        ((EditPostActivity)getActivity()).returnViewToTheParentActivity();
                    }
                }



            }
        });
        return view;
    }



}
