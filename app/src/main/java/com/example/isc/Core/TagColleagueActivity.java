package com.example.isc.Core;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.isc.Common;
import com.example.isc.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TagColleagueActivity extends Fragment {

    static    ArrayList<String> taggedColleagues=new ArrayList<>();
    private   ArrayList<MyUser> colleagues=new ArrayList<>();
    private   TagColleagueAdapter tagColleagueAdapter;
    private   ListView colleagueListView;
    private   Button tagColleagueButton;
    private   SearchView searchColleague;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage firebaseStorage;

    View view;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.activity_tag_colleague,container,false);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();


      /*  taggedColleagues = new ArrayList<>();
        colleagues = new ArrayList<MyUser>(){{
            add(new MyUser("Garbage",null, "Zineddine", "Head"));
            add(new MyUser("Garbage",null, "Mohamed", "Head"));
            add(new MyUser("Garbage",null, "Islem", "Member"));
            add(new MyUser("Garbage",null, "Bettouche", "Head"));
            add(new MyUser("Garbage",null, "Adel", "Member"));
            add(new MyUser("Garbage",null, "Nedjem Eddine", "Head"));
            add(new MyUser("Garbage",null, "Saad Eddine", "Member"));
        }};*/
        tagColleagueAdapter = new TagColleagueAdapter(Objects.requireNonNull(getActivity()).getApplicationContext(), R.layout.activity_tag_colleague_list_adapter, colleagues);
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
                if(getActivity()!=null){
                    if(getActivity() instanceof  CreatePostActivity){
                        ((CreatePostActivity)getActivity()).colleagues=tagColleague.toString();
                        ((CreatePostActivity)getActivity()).returnViewToTheParentActivity();
                    }
                    else  if (getActivity() instanceof EditPostActivity){
                        ((EditPostActivity)getActivity()).epColleagues=tagColleague.toString();
                        ((EditPostActivity)getActivity()).returnViewToTheParentActivity();
                    }
                }

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
        getAllUsersProfiles();
        return view;
    }
    private void getAllUsersProfiles() {
        firebaseFirestore.collection("Profiles").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    colleagues.clear();
                    List<DocumentSnapshot> allProfiles = Objects.requireNonNull(task.getResult()).getDocuments();
                    for (int i = 0; i < allProfiles.size(); i++) {
                        MyUser user = new MyUser(allProfiles.get(i).getId(), null, Objects.requireNonNull(allProfiles.get(i).get("name")).toString(), Objects.requireNonNull(allProfiles.get(i).getLong("position")).intValue());
                        colleagues.add(user);
                        //we check for valid reference inside getImageFromServer method
                        getUserProfileImage(Objects.requireNonNull(allProfiles.get(i).get("profileImageReferenceInStorage")).toString(), i);
                        dataUpdatedNotifyListView();
                    }

                } else {
                    Log.d("ConnectivityFireBase", "Something went wrong couldn't load colleagues " + task.getException().toString());
                }

            }
        });
    }

    private void dataUpdatedNotifyListView() {
        tagColleagueAdapter = new TagColleagueAdapter(Objects.requireNonNull(getActivity()).getApplicationContext(), R.layout.activity_tag_colleague_list_adapter, colleagues);
        colleagueListView.setAdapter(tagColleagueAdapter);

    }


    void getUserProfileImage(String storageReferencePath, final int arrayListIndex){
        //if we don't have a valid path to storage we don't do anything since the Notification is there no exception will occur
        if(storageReferencePath!=null){
            if(!storageReferencePath.equals("")) {
                StorageReference imageReference=firebaseStorage.getReference();
                imageReference=imageReference.child(storageReferencePath);

                imageReference.getBytes(6* Common.ONE_MEGA_BYTE).addOnCompleteListener(new OnCompleteListener<byte[]>() {
                    @Override
                    public void onComplete(@NonNull Task<byte[]> task) {
                        if(task.isSuccessful()){

                            MyUser aUser =colleagues.get(arrayListIndex);
                           aUser.setProfileImageBitmap(BitmapFactory.decodeByteArray(task.getResult().clone(),0,task.getResult().length));
                            colleagues.remove(arrayListIndex);
                            colleagues.add(arrayListIndex,aUser);

                            dataUpdatedNotifyListView();
                        }

                        else{
                            Log.d("ConnectivityFireBase", "Something went wrong and we couldn't get profile images for colleagues "+task.getException().toString());
                        }
                    }
                });
            }
        }

    }

}
