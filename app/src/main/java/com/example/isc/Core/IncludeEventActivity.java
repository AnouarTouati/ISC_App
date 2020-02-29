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

public class IncludeEventActivity extends  Fragment {

    ArrayList<String> eventsList;
    IncludeEventAdapter includeEventAdapter;
    ListView eventsListView;
    Button includeEventButton;
    static ArrayList<String> includedEvents;

    View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.activity_include_event,container,false);


        eventsList = new ArrayList<String>(){{
            add("INETech");
            add("Welcome Day");
            add("Innovation Day");
            add("Indjaz");
        }};
        includedEvents = new ArrayList<>();
        includeEventAdapter = new IncludeEventAdapter(getActivity().getApplicationContext(), R.layout.activity_include_event_list_adapter, eventsList);
        eventsListView = view.findViewById(R.id.eventsListView);
        eventsListView.setAdapter(includeEventAdapter);

        includeEventButton = view.findViewById(R.id.includeEventButton);
        includeEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder events = new StringBuilder();
                for(int i=0; i<includedEvents.size(); i++){
                    events.append("#");
                    events.append(includedEvents.get(i));
                    events.append(" ");
                }
                ((CreatePostActivity)getActivity()).events=events.toString();
                ((CreatePostActivity)getActivity()).returnViewToTheParentActivity();
            }
        });
        return view;
    }


}
