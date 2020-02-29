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
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    public static ListView postListView;
    private HomeListAdapter homeListAdapter;
    private ArrayList<MyPost> postArrayList=new ArrayList<>();
    private FloatingActionButton createPostButton;
    private SwipeRefreshLayout pullToRefresh;
    private ArrayList<MyUser> allUsersProfiles=new ArrayList<>();
    public static int index=0, top=0;


   private  FirebaseFirestore firebaseFirestore;
   private FirebaseStorage firebaseStorage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_home, container, false);

        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseStorage=FirebaseStorage.getInstance();

/*
        MyUser myUser0 = new MyUser(R.drawable.img0, "Zineddine", "Head");
        MyUser myUser1 = new MyUser(R.drawable.img0, "Mohamed", "Member");
        MyUser myUser2 = new MyUser(R.drawable.img0, "Islem", "Head");
        MyUser myUser3 = new MyUser(R.drawable.img0, "Bettouche", "Member");*/
/*
        final MyPost myPost0 = new MyPost(
                myUser0,
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nunc eget rhoncus erat, ac tristique nisi. Praesent ipsum erat, pulvinar pellentesque arcu quis, pellentesque maximus elit. Curabitur ullamcorper blandit magna in aliquet. In sit amet ipsum nec odio semper vehicula eu ut tellus. Ut quam libero, posuere id nisl tristique, dignissim ornare libero. Vestibulum arcu metus, convallis eu ipsum ac, lobortis vehicula ex. Vivamus sit amet velit nibh. Fusce ac lectus at augue mollis fringilla non nec odio. Nunc elementum suscipit egestas. Sed scelerisque posuere placerat. Proin a iaculis urna, in sagittis tortor. Morbi orci urna, venenatis ac rhoncus vel, volutpat sed lacus.",
                R.drawable.img1,
                "Communication",
                "Anis",
                "#Injaz");
        final MyPost myPost1 = new MyPost(
                myUser1,
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nunc eget rhoncus erat, ac tristique nisi. Praesent ipsum erat, pulvinar pellentesque arcu quis, pellentesque maximus elit. Curabitur ullamcorper blandit magna in aliquet. In sit amet ipsum nec odio semper vehicula eu ut tellus. Ut quam libero, posuere id nisl tristique, dignissim ornare libero. Vestibulum arcu metus, convallis eu ipsum ac, lobortis vehicula ex. Vivamus sit amet velit nibh. Fusce ac lectus at augue mollis fringilla non nec odio. Nunc elementum suscipit egestas. Sed scelerisque posuere placerat. Proin a iaculis urna, in sagittis tortor. Morbi orci urna, venenatis ac rhoncus vel, volutpat sed lacus.",
                R.drawable.img2,
                "Communication",
                "Anis",
                "#Injaz");
        final MyPost myPost2 = new MyPost(
                myUser2,
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nunc eget rhoncus erat, ac tristique nisi. Praesent ipsum erat, pulvinar pellentesque arcu quis, pellentesque maximus elit. Curabitur ullamcorper blandit magna in aliquet. In sit amet ipsum nec odio semper vehicula eu ut tellus. Ut quam libero, posuere id nisl tristique, dignissim ornare libero. Vestibulum arcu metus, convallis eu ipsum ac, lobortis vehicula ex. Vivamus sit amet velit nibh. Fusce ac lectus at augue mollis fringilla non nec odio. Nunc elementum suscipit egestas. Sed scelerisque posuere placerat. Proin a iaculis urna, in sagittis tortor. Morbi orci urna, venenatis ac rhoncus vel, volutpat sed lacus.",
                R.drawable.img1,
                "Communication",
                "Anis",
                "#Injaz");
        final MyPost myPost3 = new MyPost(
                myUser3,
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nunc eget rhoncus erat, ac tristique nisi. Praesent ipsum erat, pulvinar pellentesque arcu quis, pellentesque maximus elit. Curabitur ullamcorper blandit magna in aliquet. In sit amet ipsum nec odio semper vehicula eu ut tellus. Ut quam libero, posuere id nisl tristique, dignissim ornare libero. Vestibulum arcu metus, convallis eu ipsum ac, lobortis vehicula ex. Vivamus sit amet velit nibh. Fusce ac lectus at augue mollis fringilla non nec odio. Nunc elementum suscipit egestas. Sed scelerisque posuere placerat. Proin a iaculis urna, in sagittis tortor. Morbi orci urna, venenatis ac rhoncus vel, volutpat sed lacus.",
                R.drawable.img2,
                "Communication",
                "@Anis",
                "#Injaz");

        postArrayList = new ArrayList<MyPost>(){{
            add(myPost0);
            add(myPost1);
            add(myPost2);
            add(myPost3);
        }};
*/
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


 void getAllUsersProfiles(){
        firebaseFirestore.collection("Profiles").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                  if(task.isSuccessful()){
                      allUsersProfiles.clear();
                    List<DocumentSnapshot> allProfiles=task.getResult().getDocuments();
                      for(int i=0;i<allProfiles.size();i++){
                          MyUser user=new MyUser(allProfiles.get(i).getId(),null,allProfiles.get(i).get("name").toString(), Common.position[allProfiles.get(i).getLong("position").intValue()]);
                          allUsersProfiles.add(user);
                         //we check for valid reference inside getImageFromServer method
                           getImageFromServer(allProfiles.get(i).get("profileImageReferenceInStorage").toString(),allUsersProfiles.size()-1,-999/*aka we wont need this parameter, it is profile image*/,true);
                      }
                      getUserPosts();
                  }else{
                      Log.d("ConnectivityFireBase", "Something went wrong couldn't load usersProfiles "+task.getException().toString());
                  }

            }
        });
 }

    void getUserPosts() {
        postArrayList.clear();//this shouldn't be  here since we dont know if we will receive new data solve it later
       for(int i=0;i<allUsersProfiles.size();i++){
           final int finalI = i;
           firebaseFirestore.collection("AllPosts").document(allUsersProfiles.get(i).getUserID()).collection("userPosts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
               @Override
               public void onComplete(@NonNull Task<QuerySnapshot> task) {
                   if (task.isSuccessful()) {
                       List<DocumentSnapshot> allPostByThisUser=task.getResult().getDocuments();
                    for(int j=0;j<allPostByThisUser.size();j++){
                        DocumentSnapshot snapshot=allPostByThisUser.get(j);

                        String cpText = snapshot.get("cpText").toString();
                        String checkedDepartments = snapshot.get("checkedDepartments").toString();
                        String colleagues = snapshot.get("colleagues").toString();
                        String events = snapshot.get("events").toString();
                        postArrayList.add(new MyPost(allUsersProfiles.get(finalI),j,cpText, null, checkedDepartments, colleagues, events));
                        String imageReferenceInStorage=snapshot.get("imageReferenceInStorage").toString();
                        getImageFromServer(imageReferenceInStorage,finalI/*aka index where the image should be placed*/,j,false);

                        dataUpdatedNotifyListView();
                    }

                   } else {
                       Log.v("ConnectivityFireBase", "Error receiving posts " + task.getException());

                   }
               }
           });
       }

    }
    void getImageFromServer(String storageReferencePath, final int allUsersProfilesIndex, final int indexOfPostForThisUser, final boolean aProfileImage){
        if(storageReferencePath!=null){
        if(!storageReferencePath.equals("")){

            StorageReference imageReference=firebaseStorage.getReference();
            imageReference=imageReference.child(storageReferencePath);

            imageReference.getBytes(6* Common.ONE_MEGA_BYTE).addOnCompleteListener(new OnCompleteListener<byte[]>() {
                @Override
                public void onComplete(@NonNull Task<byte[]> task) {
                    if(task.isSuccessful()){
                        if(!aProfileImage){

                            int theExactPostFromTheExactUser=findIndexOfTheExactPost(allUsersProfiles.get(allUsersProfilesIndex),indexOfPostForThisUser);
                            if(theExactPostFromTheExactUser!=-1){
                                MyPost aPost=postArrayList.get(theExactPostFromTheExactUser);
                                aPost.setPostedImage(BitmapFactory.decodeByteArray(task.getResult().clone(),0,task.getResult().length));
                                postArrayList.remove(theExactPostFromTheExactUser);
                                postArrayList.add(theExactPostFromTheExactUser,aPost);
                                dataUpdatedNotifyListView();
                            }else
                            {
                                Log.v("ConnectivityFireBase","Could not find the exact post of user");
                            }

                        }else{
                            ArrayList<Integer> postsIndexes= findIndexesOfThePostWithThisUserProfile(allUsersProfiles.get(allUsersProfilesIndex));
                            for(int i=0;i<postsIndexes.size();i++){
                                allUsersProfiles.get(allUsersProfilesIndex).setProfileImageBitmap((BitmapFactory.decodeByteArray(task.getResult().clone(),0,task.getResult().length)));
                                postArrayList.get(postsIndexes.get(i)).setMyUser(allUsersProfiles.get(allUsersProfilesIndex));
                                dataUpdatedNotifyListView();

                            }

                        }

                    }
                    else{
                        Log.d("ConnectivityFireBase", "Something went wrong and we couldn't get images "+task.getException().toString());
                    }
                }
            });
        }else{
            if(!aProfileImage){
                addPostToArrayListWithoutImage(allUsersProfiles.get(allUsersProfilesIndex),indexOfPostForThisUser);
            }

        }

        }else{
            if(!aProfileImage){
                addPostToArrayListWithoutImage(allUsersProfiles.get(allUsersProfilesIndex),indexOfPostForThisUser);
            }
        }

    }
 void addPostToArrayListWithoutImage(MyUser aUser,int indexOfPostForThisUser){
        int postIndex=findIndexOfTheExactPost(aUser,indexOfPostForThisUser);
     MyPost aPost=postArrayList.get(postIndex);
     aPost.setPostedImage(null);
     postArrayList.remove(postIndex);
     postArrayList.add(postIndex,aPost);
     dataUpdatedNotifyListView();
 }

    ArrayList<Integer> findIndexesOfThePostWithThisUserProfile(MyUser aUser){
        ArrayList<Integer> indexes=new ArrayList<>();
        for (int i=0;i<postArrayList.size();i++){
            MyPost aPost=postArrayList.get(i);
            if(aPost.getMyUser().getUserID().equals(aUser.getUserID())){
             indexes.add(i);
            }
        }
       return indexes;
    }
    int findIndexOfTheExactPost(MyUser aUser,int indexOfPostForThisUser){
        for (int i=0;i<postArrayList.size();i++){
            MyPost aPost=postArrayList.get(i);
            if(aPost.getMyUser().getUserID().equals(aUser.getUserID())&& aPost.getIndexOfPostForThisUser()==indexOfPostForThisUser){
               return i;
            }
        }
        return -1;//failed to find the post
    }
    void dataUpdatedNotifyListView() {
        homeListAdapter = new HomeListAdapter(getContext(), R.layout.activity_home_list_adapter, postArrayList);
        postListView.setAdapter(homeListAdapter);

    }



    public void createPost(){
        startActivity(new Intent(getContext(), CreatePostActivity.class));
    }
    public void refreshData(){
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