package com.example.isc.Core.Fragments;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.isc.Common;
import com.example.isc.Core.CreatePostActivity;
import com.example.isc.Core.MyPost;
import com.example.isc.Core.MyUser;
import com.example.isc.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class HomeFragment extends Fragment {

    private ListView postListView;
    private HomeListAdapter homeListAdapter;
    private ArrayList<MyPost> postArrayList = new ArrayList<>();
    private FloatingActionButton createPostButton;
    private SwipeRefreshLayout pullToRefresh;
    private ArrayList<MyUser> allUsersProfiles = new ArrayList<>();
    private Map<String,ArrayList<Integer>> userProfileIDsWeAlreadyRequestedAndTheRequestingPostsIndexes=new HashMap<>();
    private static int index = 0, top = 0;
    private  final String CHANNEL_ID="The ID IS String";//used by notification for android 8 and up

    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage firebaseStorage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_home, container, false);

        postArrayList.clear();
        allUsersProfiles.clear();

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        homeListAdapter = new HomeListAdapter(getContext(), R.layout.activity_home_list_adapter, postArrayList);
        postListView = fragmentView.findViewById(R.id.postListView);
        postListView.setAdapter(homeListAdapter);
        postListView.setSelectionFromTop(index, top);
        postListView.setClickable(true);
        postListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
        createPostButton = fragmentView.findViewById(R.id.createPostButton);
        createPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPost();
            }
        });

        pullToRefresh = fragmentView.findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData(); // your code
                pullToRefresh.setRefreshing(false);
            }
        });

        getUserPosts();

        return fragmentView;
    }


    private void getUserProfile(final String userID, final int theIndexOfPostThatRequestedThisProfile) {

        boolean weAlreadyHaveThatProfile=false;
        for(int i=0;i<allUsersProfiles.size();i++){
            if(allUsersProfiles.get(i).getUserID().equals(userID)){
                weAlreadyHaveThatProfile=true;
                postArrayList.get(theIndexOfPostThatRequestedThisProfile).setMyUser(allUsersProfiles.get(i));
                break;
            }
        }
        boolean haveWeRequestedThatProfileYet=false;
        if(userProfileIDsWeAlreadyRequestedAndTheRequestingPostsIndexes.containsKey(userID)){
            haveWeRequestedThatProfileYet=true;
            userProfileIDsWeAlreadyRequestedAndTheRequestingPostsIndexes.get(userID).add(theIndexOfPostThatRequestedThisProfile);
        }
        if(!weAlreadyHaveThatProfile && !haveWeRequestedThatProfileYet){
           ArrayList<Integer> postArrayListIndexesInitializer=new ArrayList<>();
           postArrayListIndexesInitializer.add(theIndexOfPostThatRequestedThisProfile);
           userProfileIDsWeAlreadyRequestedAndTheRequestingPostsIndexes.put(userID,postArrayListIndexesInitializer);

            firebaseFirestore.collection("Profiles").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    MyUser user = new MyUser(task.getResult().getId(), null, Objects.requireNonNull(task.getResult().get("name")).toString(), Objects.requireNonNull(task.getResult().getLong("position")).intValue(),task.getResult().getString("email")
                    ,task.getResult().getString("studentRegistrationNumber"));
                    allUsersProfiles.add(user);
                    for(int i=0;i<userProfileIDsWeAlreadyRequestedAndTheRequestingPostsIndexes.get(userID).size();i++){
                        postArrayList.get(userProfileIDsWeAlreadyRequestedAndTheRequestingPostsIndexes.get(userID).get(i)).setMyUser(allUsersProfiles.get(allUsersProfiles.size()-1));
                    }
                    getImageFromServer(Objects.requireNonNull(task.getResult().get("profileImageReferenceInStorage")).toString(),/*this one is useless here*/ theIndexOfPostThatRequestedThisProfile, allUsersProfiles.size()-1, true);
                 userProfileIDsWeAlreadyRequestedAndTheRequestingPostsIndexes.remove(userID);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    try{
                        throw e;
                    }
                    catch (Exception ee){
                        Log.v("ConnectivityFireBase", "Error getting profile  HomeFragment" + ee.getMessage());
                    }
                }
            });
}

    }

    private  void getUserPosts() {
        postArrayList.clear();
        allUsersProfiles.clear();
            firebaseFirestore.collection("AllPosts").orderBy("dateInMillis", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        postArrayList.clear();
                        List<DocumentSnapshot> allPosOfAllUsers = Objects.requireNonNull(task.getResult()).getDocuments();

                        for (int j = 0; j < allPosOfAllUsers.size(); j++) {
                            DocumentSnapshot snapshot = allPosOfAllUsers.get(j);

                            String cpText = Objects.requireNonNull(snapshot.get("cpText")).toString();
                            String checkedDepartments = Objects.requireNonNull(snapshot.get("checkedDepartments")).toString();
                            String colleagues = Objects.requireNonNull(snapshot.get("colleagues")).toString();
                            String events = Objects.requireNonNull(snapshot.get("events")).toString();
                            String postID= Objects.requireNonNull(snapshot.get("postID")).toString();

                            if(!Objects.requireNonNull(snapshot.get("imageReferenceInStorage")).toString().equals("")){

                                postArrayList.add(new MyPost(postID,null, j, cpText, true,null, checkedDepartments, colleagues, events));
                                String imageReferenceInStorage = Objects.requireNonNull(snapshot.get("imageReferenceInStorage")).toString();
                                getImageFromServer(imageReferenceInStorage, postArrayList.size()-1/*aka index where the image should be placed*/, j, false);
                            }
                           else {
                                postArrayList.add(new MyPost(postID,null, j, cpText, false,null, checkedDepartments, colleagues, events));
                            }
                            getUserProfile(snapshot.get("userID").toString(),j);
                            dataUpdatedNotifyListView();
                        }

                    } else {
                        Log.v("ConnectivityFireBase", "Error receiving posts  HomeFragment" + task.getException());

                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    try{
                        throw  e;
                    }
                    catch (Exception ee){
                        Log.v("ConnectivityFireBase", "Error receiving posts HomeFragment " + ee.getMessage());
                    }
                }
            });

    }

    private void getImageFromServer(final String storageReferencePath, final int postArrayListIndex, final int profileIndex, final boolean aProfileImage) {
        if (storageReferencePath != null) {
            if (!storageReferencePath.equals("")) {

                StorageReference imageReference = firebaseStorage.getReference();
                imageReference = imageReference.child(storageReferencePath);

                imageReference.getBytes(6 * Common.ONE_MEGA_BYTE).addOnCompleteListener(new OnCompleteListener<byte[]>() {
                    @Override
                    public void onComplete(@NonNull Task<byte[]> task) {
                        if (task.isSuccessful()) {
                            if (!aProfileImage) {

                                    MyPost aPost = postArrayList.get(postArrayListIndex);
                                    aPost.setPostedImage(BitmapFactory.decodeByteArray(Objects.requireNonNull(task.getResult()).clone(), 0, task.getResult().length));
                                    postArrayList.remove(postArrayListIndex);
                                    postArrayList.add(postArrayListIndex, aPost);
                                    dataUpdatedNotifyListView();

                            } else {
                                    allUsersProfiles.get(profileIndex).setProfileImageBitmap((BitmapFactory.decodeByteArray(Objects.requireNonNull(task.getResult()).clone(), 0, task.getResult().length)));
                                    dataUpdatedNotifyListView();
                            }

                        } else {
                            Log.v("ConnectivityFireBase", "Something went wrong and we couldn't get images  HomeFragment" + Objects.requireNonNull(task.getException()).toString());
                            Log.v("ConnectivityFireBase","Path cuasing the error is "+storageReferencePath);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        try{
                            throw e;
                        }
                        catch (Exception ee){
                            Log.v("ConnectivityFireBase", "Something went wrong and we couldn't get images  HomeFragment" + ee.getMessage());
                            Log.v("ConnectivityFireBase","Path cuasing the error is "+storageReferencePath);
                           // removePostSomethingWentWrong(postArrayListIndex);
                        }
                    }
                });
            } else if(aProfileImage) {
                allUsersProfiles.get(profileIndex).setProfileImageBitmap(null);
                dataUpdatedNotifyListView();
            }
    }else if(aProfileImage) {
            allUsersProfiles.get(profileIndex).setProfileImageBitmap(null);
            dataUpdatedNotifyListView();
        }
    }


    private void dataUpdatedNotifyListView() {
        homeListAdapter = new HomeListAdapter(getContext(), R.layout.activity_home_list_adapter, postArrayList);
        postListView.setAdapter(homeListAdapter);

    }


    private void createPost() {
        Intent intent=new Intent(getContext(), CreatePostActivity.class);
        startActivity(intent);
    }

    private void refreshData() {
        Toast.makeText(getContext(), "Refreshed", Toast.LENGTH_SHORT).show();
    }


}