package com.example.isc.Core.Fragments;

import android.content.Intent;
import android.graphics.Bitmap;
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
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomeFragment extends Fragment {

    private ListView postListView;
    private HomeListAdapter homeListAdapter;
    private ArrayList<MyPost> postArrayList = new ArrayList<>();
    private FloatingActionButton createPostButton;
    private SwipeRefreshLayout pullToRefresh;
    private ArrayList<MyUser> allUsersProfiles = new ArrayList<>();
    private static int index = 0, top = 0;
    private  final String CHANNEL_ID="The ID IS String";//used by notification for android 8 and up

    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage firebaseStorage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_home, container, false);

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


    private void getUserProfile(String userID, final int theIndexOfPostThatRequestedThisProfile) {

        boolean weAlreadyHaveThatProfile=false;
        for(int i=0;i<allUsersProfiles.size();i++){
            if(allUsersProfiles.get(i).getUserID().equals(userID)){
                weAlreadyHaveThatProfile=true;
                postArrayList.get(theIndexOfPostThatRequestedThisProfile).setMyUser(allUsersProfiles.get(i));
                break;
            }
        }
        if(!weAlreadyHaveThatProfile){
            firebaseFirestore.collection("Profiles").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    MyUser user = new MyUser(task.getResult().getId(), null, Objects.requireNonNull(task.getResult().get("name")).toString(), Common.position[Objects.requireNonNull(task.getResult().getLong("position")).intValue()]);
                    allUsersProfiles.add(user);
                    postArrayList.get(theIndexOfPostThatRequestedThisProfile).setMyUser(user);
                    getImageFromServer(Objects.requireNonNull(task.getResult().get("profileImageReferenceInStorage")).toString(), theIndexOfPostThatRequestedThisProfile, allUsersProfiles.size()-1, true);
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
                                Bitmap placeHolderImage = BitmapFactory.decodeResource(getResources(), R.drawable.post_image_placeholder);
                                postArrayList.add(new MyPost(postID,null, j, cpText, placeHolderImage, checkedDepartments, colleagues, events));
                                String imageReferenceInStorage = Objects.requireNonNull(snapshot.get("imageReferenceInStorage")).toString();
                                getImageFromServer(imageReferenceInStorage, postArrayList.size()-1/*aka index where the image should be placed*/, j, false);
                            }
                           else {
                                postArrayList.add(new MyPost(postID,null, j, cpText, null, checkedDepartments, colleagues, events));
                            }
                            getUserProfile(snapshot.get("userID").toString(),j);
                            dataUpdatedNotifyListView();
                        }

                    } else {
                        Log.v("ConnectivityFireBase", "Error receiving posts " + task.getException());

                    }
                }
            });

    }

    private void getImageFromServer(String storageReferencePath, final int postArrayListIndex, final int profileIndex, final boolean aProfileImage) {
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
                                    postArrayList.get(postArrayListIndex).setMyUser(allUsersProfiles.get(profileIndex));
                                    dataUpdatedNotifyListView();

                            }

                        } else {
                            Log.d("ConnectivityFireBase", "Something went wrong and we couldn't get images " + Objects.requireNonNull(task.getException()).toString());
                        }
                    }
                });
            }

    }
    }


    private void dataUpdatedNotifyListView() {
        homeListAdapter = new HomeListAdapter(getContext(), R.layout.activity_home_list_adapter, postArrayList);
        postListView.setAdapter(homeListAdapter);

    }


    private void createPost() {
        startActivity(new Intent(getContext(), CreatePostActivity.class));
    }

    private void refreshData() {
        Toast.makeText(getContext(), "Refreshed", Toast.LENGTH_SHORT).show();
    }


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public HomeFragment() {

    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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