package com.example.isc.Core.Fragments;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.isc.Common;
import com.example.isc.Core.MyNotification;
import com.example.isc.Core.MyUser;
import com.example.isc.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationFragment extends Fragment {


    private ListView notificationListView;
    private NotificationListAdapter notificationListAdapter;
    private ArrayList<MyNotification> notificationArrayList=new ArrayList<>();
    private ArrayList<MyUser> allUsersProfiles = new ArrayList<>();
    private Map<String,ArrayList<Integer>> userProfileIDsWeAlreadyRequestedAndTheRequestingNotificationsIndexes=new HashMap<>();
    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage firebaseStorage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_notification, container, false);

        notificationArrayList.clear();
        allUsersProfiles.clear();

        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseStorage=FirebaseStorage.getInstance();

        notificationListAdapter = new NotificationListAdapter(getContext(), R.layout.activity_notification_list_adapter, notificationArrayList);
        notificationListView = fragmentView.findViewById(R.id.notificationListView);
        notificationListView.setAdapter(notificationListAdapter);
        notificationListView.setClickable(true);
        notificationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
        listenToNotificationFromFirestore();
        Log.v("AppLogic","OnCreateCalled");
        return fragmentView;
    }

    private  void listenToNotificationFromFirestore(){

     firebaseFirestore.collection("Notifications").orderBy("notificationTimeInMillis", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
         @Override
         public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                 notificationArrayList.clear();
                 List<DocumentSnapshot> notificationSnaps= queryDocumentSnapshots.getDocuments();
                 for(int i=0;i<notificationSnaps.size();i++){
                     String userID=notificationSnaps.get(i).get("userID").toString();
                     String notificationText=notificationSnaps.get(i).get("notificationText").toString();
                     String notificationTime=notificationSnaps.get(i).get("notificationTime").toString();
                     //we must add it first so we dont loose track of where this notification is//if we dont it will get messy trust me
                     MyNotification aNotification=new MyNotification(null,notificationText,notificationTime);
                     notificationArrayList.add(aNotification);
                     getUserProfileAndAddNotification(userID,notificationText,notificationTime,i);
                 }
         }
     });
    }
  void getUserProfileAndAddNotification(final String userID, final String notificationText, final String notificationTime, final int theIndexOfNotificationThatRequestedThisProfile){

      boolean weAlreadyHaveThatProfile=false;
      for(int i=0;i<allUsersProfiles.size();i++){
          if(allUsersProfiles.get(i).getUserID().equals(userID)){
              weAlreadyHaveThatProfile=true;
              notificationArrayList.get(theIndexOfNotificationThatRequestedThisProfile).setMyUser(allUsersProfiles.get(i));
              dataUpdatedNotifyListView();
              break;
          }
      }
    boolean haveWeRequestedThatProfileYet=false;
  /*   for(int i=0;i<userProfileIDsWeAlreadyRequested.size();i++){
        if(userProfileIDsWeAlreadyRequested.get(i).equals(userID)){
            haveWeRequestedThatProfileYet=true;
            break;
        }
      }*/
          if(userProfileIDsWeAlreadyRequestedAndTheRequestingNotificationsIndexes.containsKey(userID)){
              haveWeRequestedThatProfileYet=true;
              userProfileIDsWeAlreadyRequestedAndTheRequestingNotificationsIndexes.get(userID).add(theIndexOfNotificationThatRequestedThisProfile);
      }
      if(!weAlreadyHaveThatProfile && !haveWeRequestedThatProfileYet){

         ArrayList<Integer> notificationArrayListInitialize=new ArrayList<>();
         notificationArrayListInitialize.add(theIndexOfNotificationThatRequestedThisProfile);
        userProfileIDsWeAlreadyRequestedAndTheRequestingNotificationsIndexes.put(userID,notificationArrayListInitialize);
        firebaseFirestore.collection("Profiles").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
           if (task.isSuccessful()){

               MyUser user= new MyUser(userID,null, (String) task.getResult().get("name"), task.getResult().getLong("position").intValue());
               allUsersProfiles.add(user);//whenever we need to assign a user we must use this array to keep the refrence so that when we update the image it changes all over the place
               for(int i=0;i<userProfileIDsWeAlreadyRequestedAndTheRequestingNotificationsIndexes.get(userID).size();i++){
                   notificationArrayList.get(userProfileIDsWeAlreadyRequestedAndTheRequestingNotificationsIndexes.get(userID).get(i)).setMyUser(allUsersProfiles.get(allUsersProfiles.size()-1));
               }
               getUserProfileImage(task.getResult().get("profileImageReferenceInStorage").toString()/*,userProfileIDsWeAlreadyRequestedAndTheRequestingNotificationsIndexes.get(userID).get(i)*/,allUsersProfiles.size()-1);
               dataUpdatedNotifyListView();

            }
        }}).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                try{
                    throw e;
                }
                catch (Exception ee){
                    Log.v("ConnectivityFireBase", "Something went wrong getting profile " + ee.getMessage());
                }
            }
        });
      }
  }
  void getUserProfileImage(String storageReferencePath /*,final int theIndexOfNotificationThatRequestedThisProfile*/, final int profileIndex){
        //if we don't have a valid path to storage we don't do anything since the Notification is there no exception will occur
        if(storageReferencePath!=null){
            if(!storageReferencePath.equals("")) {
                StorageReference imageReference=firebaseStorage.getReference();
                imageReference=imageReference.child(storageReferencePath);

                imageReference.getBytes(6* Common.ONE_MEGA_BYTE).addOnCompleteListener(new OnCompleteListener<byte[]>() {
                    @Override
                    public void onComplete(@NonNull Task<byte[]> task) {
                        if(task.isSuccessful()){
                               allUsersProfiles.get(profileIndex).setProfileImageBitmap(BitmapFactory.decodeByteArray(task.getResult().clone(),0,task.getResult().length));
                        //        MyNotification aNotification =notificationArrayList.get(theIndexOfNotificationThatRequestedThisProfile);
                        //        aNotification.setMyUser(allUsersProfiles.get(profileIndex));
                                dataUpdatedNotifyListView();
                            }

                        else{
                            Log.d("ConnectivityFireBase", "Something went wrong and we couldn't get images "+task.getException().toString());
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        try{
                            throw e;
                        }
                        catch (Exception ee){
                            Log.v("ConnectivityFireBase", "Something went wrong and we couldn't get images " + ee.getMessage());
                        }
                    }
                });
            }
        }

  }
   void dataUpdatedNotifyListView(){
       notificationListAdapter = new NotificationListAdapter(getContext(), R.layout.activity_notification_list_adapter, notificationArrayList);
       notificationListView.setAdapter(notificationListAdapter);
   }

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    public NotificationFragment() {

    }
    public static NotificationFragment newInstance(String param1, String param2) {
        NotificationFragment fragment = new NotificationFragment();
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