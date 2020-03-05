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

        getAllUsersProfiles();

        return fragmentView;
    }


    private void getAllUsersProfiles() {
        firebaseFirestore.collection("Profiles").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    allUsersProfiles.clear();
                    List<DocumentSnapshot> allProfiles = Objects.requireNonNull(task.getResult()).getDocuments();
                    for (int i = 0; i < allProfiles.size(); i++) {
                        MyUser user = new MyUser(allProfiles.get(i).getId(), null, Objects.requireNonNull(allProfiles.get(i).get("name")).toString(), Common.position[Objects.requireNonNull(allProfiles.get(i).getLong("position")).intValue()]);
                        allUsersProfiles.add(user);
                        //we check for valid reference inside getImageFromServer method
                        getImageFromServer(Objects.requireNonNull(allProfiles.get(i).get("profileImageReferenceInStorage")).toString(), allUsersProfiles.size() - 1, -999/*aka we wont need this parameter, it is profile image*/, true);
                    }
                    getUserPosts();
                } else {
                    Log.d("ConnectivityFireBase", "Something went wrong couldn't load usersProfiles " + task.getException().toString());
                }

            }
        });
    }

    private  void getUserPosts() {
        postArrayList.clear();//this shouldn't be  here since we dont know if we will receive new data solve it later
        for (int i = 0; i < allUsersProfiles.size(); i++) {
            final int finalI = i;
            firebaseFirestore.collection("AllPosts").document(allUsersProfiles.get(i).getUserID()).collection("userPosts").orderBy("dateInMillis", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        List<DocumentSnapshot> allPostByThisUser = Objects.requireNonNull(task.getResult()).getDocuments();
                        for (int j = 0; j < allPostByThisUser.size(); j++) {
                            DocumentSnapshot snapshot = allPostByThisUser.get(j);

                            String cpText = Objects.requireNonNull(snapshot.get("cpText")).toString();
                            String checkedDepartments = Objects.requireNonNull(snapshot.get("checkedDepartments")).toString();
                            String colleagues = Objects.requireNonNull(snapshot.get("colleagues")).toString();
                            String events = Objects.requireNonNull(snapshot.get("events")).toString();
                            String postID= Objects.requireNonNull(snapshot.get("postID")).toString();
                            if(!Objects.requireNonNull(snapshot.get("imageReferenceInStorage")).toString().equals("")){
                                Bitmap placeHolderImage = BitmapFactory.decodeResource(getResources(), R.drawable.post_image_placeholder);
                                postArrayList.add(new MyPost(postID,allUsersProfiles.get(finalI), j, cpText, placeHolderImage, checkedDepartments, colleagues, events));
                                String imageReferenceInStorage = Objects.requireNonNull(snapshot.get("imageReferenceInStorage")).toString();
                                getImageFromServer(imageReferenceInStorage, finalI/*aka index where the image should be placed*/, j, false);
                            }
                           else {
                                postArrayList.add(new MyPost(postID,allUsersProfiles.get(finalI), j, cpText, null, checkedDepartments, colleagues, events));
                            }

                            dataUpdatedNotifyListView();
                        }

                    } else {
                        Log.v("ConnectivityFireBase", "Error receiving posts " + task.getException());

                    }
                }
            });
        }

    }

    private void getImageFromServer(String storageReferencePath, final int allUsersProfilesIndex, final int indexOfPostForThisUser, final boolean aProfileImage) {
        if (storageReferencePath != null) {
            if (!storageReferencePath.equals("")) {

                StorageReference imageReference = firebaseStorage.getReference();
                imageReference = imageReference.child(storageReferencePath);

                imageReference.getBytes(6 * Common.ONE_MEGA_BYTE).addOnCompleteListener(new OnCompleteListener<byte[]>() {
                    @Override
                    public void onComplete(@NonNull Task<byte[]> task) {
                        if (task.isSuccessful()) {
                            if (!aProfileImage) {

                                int theExactPostFromTheExactUser = findIndexOfTheExactPost(allUsersProfiles.get(allUsersProfilesIndex), indexOfPostForThisUser);
                                if (theExactPostFromTheExactUser != -1) {
                                    MyPost aPost = postArrayList.get(theExactPostFromTheExactUser);
                                    aPost.setPostedImage(BitmapFactory.decodeByteArray(Objects.requireNonNull(task.getResult()).clone(), 0, task.getResult().length));
                                    postArrayList.remove(theExactPostFromTheExactUser);
                                    postArrayList.add(theExactPostFromTheExactUser, aPost);
                                    dataUpdatedNotifyListView();
                                } else {
                                    Log.v("ConnectivityFireBase", "Could not find the exact post of user");
                                }

                            } else {
                                ArrayList<Integer> postsIndexes = findIndexesOfThePostWithThisUserProfile(allUsersProfiles.get(allUsersProfilesIndex));
                                for (int i = 0; i < postsIndexes.size(); i++) {
                                    allUsersProfiles.get(allUsersProfilesIndex).setProfileImageBitmap((BitmapFactory.decodeByteArray(Objects.requireNonNull(task.getResult()).clone(), 0, task.getResult().length)));
                                    postArrayList.get(postsIndexes.get(i)).setMyUser(allUsersProfiles.get(allUsersProfilesIndex));
                                    dataUpdatedNotifyListView();

                                }

                            }

                        } else {
                            Log.d("ConnectivityFireBase", "Something went wrong and we couldn't get images " + Objects.requireNonNull(task.getException()).toString());
                        }
                    }
                });
            } else {
                if (!aProfileImage) {
                    addPostToArrayListWithoutImage(allUsersProfiles.get(allUsersProfilesIndex), indexOfPostForThisUser);
                }

            }

        } else {
            if (!aProfileImage) {
                addPostToArrayListWithoutImage(allUsersProfiles.get(allUsersProfilesIndex), indexOfPostForThisUser);
            }
        }

    }

    private void addPostToArrayListWithoutImage(MyUser aUser, int indexOfPostForThisUser) {
        int postIndex = findIndexOfTheExactPost(aUser, indexOfPostForThisUser);
        MyPost aPost = postArrayList.get(postIndex);
        aPost.setPostedImage(null);
        postArrayList.remove(postIndex);
        postArrayList.add(postIndex, aPost);
        dataUpdatedNotifyListView();
    }

    private ArrayList<Integer> findIndexesOfThePostWithThisUserProfile(MyUser aUser) {
        ArrayList<Integer> indexes = new ArrayList<>();
        for (int i = 0; i < postArrayList.size(); i++) {
            MyPost aPost = postArrayList.get(i);
            if (aPost.getMyUser().getUserID().equals(aUser.getUserID())) {
                indexes.add(i);
            }
        }
        return indexes;
    }

    private int findIndexOfTheExactPost(MyUser aUser, int indexOfPostForThisUser) {
        for (int i = 0; i < postArrayList.size(); i++) {
            MyPost aPost = postArrayList.get(i);
            if (aPost.getMyUser().getUserID().equals(aUser.getUserID()) && aPost.getIndexOfPostForThisUser() == indexOfPostForThisUser) {
                return i;
            }
        }
        return -1;//failed to find the post
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