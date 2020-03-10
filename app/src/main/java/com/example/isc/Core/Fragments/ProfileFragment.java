package com.example.isc.Core.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.isc.Common;
import com.example.isc.Core.MyPost;
import com.example.isc.Core.MyUser;
import com.example.isc.Core.NonScrollListView;
import com.example.isc.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProfileFragment extends Fragment {

    private ImageView profileImage;
    private Bitmap profileImageAsBitmap;
    private LinearLayout nameLayout, positionLayout, studentNumberLayout, emailLayout, postLayout;
    private TextView nameTextView,  studentNumberTextView, emailTextView,positionTextView;
    private Spinner editPositionSpinner;
    private EditText nameEditText;
    private ImageView editNameIV, editPositionIV, showProfilePostIV;
    private static final int PICK_IMAGE = 1;
    private MyUser myUser0;

    private Boolean n = false, p = false, s = false, e = false, postIsVisible = false;

    private FloatingActionButton profilePostsUpButton;
    private  ScrollView profileScrollView;
    private  ProgressDialog progressDialog;
    private  NonScrollListView profilePostsListView;
    private  ProfilePostsListAdapter adapter;
    public static ArrayList<MyPost> postArrayList = new ArrayList<>();

    private  FirebaseUser firebaseUser;
    private  FirebaseFirestore firebaseFirestore;
    private  FirebaseStorage firebaseStorage;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_profile, container, false);


        progressDialog=new ProgressDialog(getContext());
        initialize(fragmentView);
        firebaseStorage=FirebaseStorage.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        profilePostsListView = fragmentView.findViewById(R.id.profilePostsListView);


        postLayout = fragmentView.findViewById(R.id.profilePostLayout);
        postLayout.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {
                if (!postIsVisible) {
                    postIsVisible = true;
                    showProfilePostIV.setImageResource(R.drawable.ic_arrow_drop_up_24dp);
                    profilePostsListView.setVisibility(View.VISIBLE);
                    profilePostsUpButton.setVisibility(View.VISIBLE);
                } else {
                    postIsVisible = false;
                    showProfilePostIV.setImageResource(R.drawable.ic_arrow_drop_down_24dp);
                    profilePostsListView.setVisibility(View.GONE);
                    profilePostsUpButton.setVisibility(View.GONE);
                }
            }
        });
        profileScrollView = fragmentView.findViewById(R.id.profileScrollView);
        profilePostsUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileScrollView.fullScroll(ScrollView.FOCUS_UP);
            }
        });
         getUserProfile();
        return fragmentView;
    }

    private void initialize(View fragmentView) {

        profileImage = fragmentView.findViewById(R.id.profileImage);
        nameLayout = fragmentView.findViewById(R.id.profileNameLayout);
        positionLayout = fragmentView.findViewById(R.id.profilePositionLayout);
        studentNumberLayout = fragmentView.findViewById(R.id.profileStudentNumberLayout);
        emailLayout = fragmentView.findViewById(R.id.profileEmailLayout);
        postLayout = fragmentView.findViewById(R.id.profilePostLayout);

        nameTextView = fragmentView.findViewById(R.id.profileNameTextView);
        positionTextView = fragmentView.findViewById(R.id.profilePositionTextView);
        ArrayAdapter<String> spinnerAdapter=new ArrayAdapter<>(getActivity(),R.layout.support_simple_spinner_dropdown_item,Common.position);
        studentNumberTextView = fragmentView.findViewById(R.id.profileStudentNumberTextView);
        emailTextView = fragmentView.findViewById(R.id.profileEmailTextView);

        nameEditText = fragmentView.findViewById(R.id.nameEditText);
        editPositionSpinner = fragmentView.findViewById(R.id.editPositionSpinner);
        editPositionSpinner.setAdapter(spinnerAdapter);

        editNameIV = fragmentView.findViewById(R.id.editName);
        editPositionIV = fragmentView.findViewById(R.id.editPosition);

        showProfilePostIV = fragmentView.findViewById(R.id.showProfilePostIV);

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        nameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameTextView.setVisibility(View.GONE);
                nameEditText.setVisibility(View.VISIBLE);
                nameEditText.setText(nameTextView.getText());
                editNameIV.setImageResource(R.drawable.ic_add_gray_24dp);
                n = true;
            }
        });
        editNameIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (n) {
                    updateProfileName(nameEditText.getText().toString());
                    nameTextView.setVisibility(View.VISIBLE);
                    nameEditText.setVisibility(View.GONE);
                    editNameIV.setImageResource(R.drawable.ic_edit_black_24dp);
                    n = false;
                } else {
                    nameTextView.setVisibility(View.GONE);
                    nameEditText.setVisibility(View.VISIBLE);
                    nameEditText.setText(nameTextView.getText());
                    editNameIV.setImageResource(R.drawable.ic_add_gray_24dp);
                    n = true;
                }
            }
        });

        positionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                positionTextView.setVisibility(View.GONE);
                editPositionSpinner.setVisibility(View.VISIBLE);
                if(Common.convertPostionStringToInt(positionTextView.getText().toString())!=-1){
                    Log.v("AppLogic","Got index of club position");
                    editPositionSpinner.setSelection(Common.convertPostionStringToInt(positionTextView.getText().toString()));
                }else {
                   Toast.makeText(getContext(),"Something went wrong while displaying your postion in the club",Toast.LENGTH_LONG).show();
                    Log.v("AppLogic","Something went wrong while getting index of club position");
                }
                editPositionIV.setImageResource(R.drawable.ic_add_gray_24dp);
                p = true;
            }
        });
        editPositionIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (p) {
                    updateClubPosition(editPositionSpinner.getSelectedItemPosition());
                    positionTextView.setVisibility(View.VISIBLE);
                    editPositionSpinner.setVisibility(View.GONE);
                    editPositionIV.setImageResource(R.drawable.ic_edit_black_24dp);
                    p = false;
                } else {
                    positionTextView.setVisibility(View.GONE);
                    editPositionSpinner.setVisibility(View.VISIBLE);
                    if(Common.convertPostionStringToInt(positionTextView.getText().toString())!=-1){
                        Log.v("AppLogic","Got index of club position");
                        editPositionSpinner.setSelection(Common.convertPostionStringToInt(positionTextView.getText().toString()));
                    }else {
                        Toast.makeText(getContext(),"Something went wrong while displaying your postion in the club",Toast.LENGTH_LONG).show();
                        Log.v("AppLogic","Something went wrong while getting index of club position");
                    }
                    editPositionIV.setImageResource(R.drawable.ic_add_gray_24dp);
                    p = true;
                }
            }
        });

        profilePostsUpButton = fragmentView.findViewById(R.id.profileUpButton);
    }

    private  void updateClubPosition(final int newClubPosition){
        firebaseFirestore.collection("Profiles").document(firebaseUser.getUid()).update("position",newClubPosition).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
               if(task.isSuccessful()){
                   positionTextView.setText(Common.position[newClubPosition]);
                   myUser0.setPosition(newClubPosition);
               }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                try{
                    throw e;
                }
                catch (Exception ee){
                    Log.v("ConnectivityFireBase", "Something went wrong and we couldn't Update position" + ee.getMessage());
                }
            }
        });
    }
    private void getUserProfile(){
     firebaseFirestore = FirebaseFirestore.getInstance();

     firebaseFirestore.collection("Profiles").document(firebaseUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
         @Override
         public void onComplete(@NonNull Task<DocumentSnapshot> task) {
             if (task.isSuccessful()) {

                 //i will deal with the image later
                 myUser0 = new MyUser(firebaseUser.getUid(),null, (String) Objects.requireNonNull(task.getResult()).get("name"), Objects.requireNonNull(task.getResult().getLong("position")).intValue());
                 Log.v("ConnectivityFireBase", "Received profile successfully");

                 nameTextView.setText(myUser0.getFullName());
                 positionTextView.setText(myUser0.getPositionAsString());
                 studentNumberTextView.setText("Not Implemented yet");
                 emailTextView.setText(firebaseUser.getEmail());
                 //we check for valid reference inside getImageFromServer method
                 getImageFromServer(task.getResult().get("profileImageReferenceInStorage").toString(),-1);//-1 indicates profile image

             } else {
                 Log.v("ConnectivityFireBase", "Error receiving profile " + task.getException());
             }
         }
     }).addOnFailureListener(new OnFailureListener() {
         @Override
         public void onFailure(@NonNull Exception e) {
             try{
                 throw e;
             }
             catch (Exception ee){
                 Log.v("ConnectivityFireBase", "Something went wrong and we couldn't get user profile" + ee.getMessage());
             }
         }
     });


 }
    private void getUserPosts() {
        firebaseFirestore.collection("AllPosts").whereEqualTo("userID",firebaseUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    postArrayList.clear();
                    List<DocumentSnapshot> snapshots= Objects.requireNonNull(task.getResult()).getDocuments();
                    for (int i=0;i<snapshots.size();i++) {
                        String cpText = Objects.requireNonNull(snapshots.get(i).get("cpText")).toString();
                        String checkedDepartments = Objects.requireNonNull(snapshots.get(i).get("checkedDepartments")).toString();
                        String colleagues = Objects.requireNonNull(snapshots.get(i).get("colleagues")).toString();
                        String events = Objects.requireNonNull(snapshots.get(i).get("events")).toString();
                        String postID= Objects.requireNonNull(snapshots.get(i).get("postID")).toString();
                        if(!snapshots.get(i).get("imageReferenceInStorage").toString().equals("")){

                            postArrayList.add(new MyPost(postID,myUser0, i/*this i here is useless we just use it for home*/,cpText, true,null, checkedDepartments, colleagues, events));
                            String imageReferenceInStorage=snapshots.get(i).get("imageReferenceInStorage").toString();
                            getImageFromServer(imageReferenceInStorage,postArrayList.size()-1/*aka index where the image should be placed*/);
                        }else{
                            postArrayList.add(new MyPost(postID,myUser0, i/*this i here is useless we just use it for home*/,cpText, false,null, checkedDepartments, colleagues, events));
                        }


                    }
                    dataUpdatedNotifyListView();
                } else {
                    Log.v("ConnectivityFireBase", "Error receiving posts " + task.getException());

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                try{
                    throw e;
                }
                catch (Exception ee){
                    Log.v("ConnectivityFireBase", "Something went wrong and we couldn't get images" + ee.getMessage());
                }
            }
        });
    }

    private  void dataUpdatedNotifyListView() {
        adapter = new ProfilePostsListAdapter(getContext(), R.layout.activity_profile_post_list_adapter, postArrayList,this);
        profilePostsListView.setAdapter(adapter);
        profilePostsListView.setScrollContainer(false);
    }

    private void getImageFromServer(String storageReferencePath, final int postArrayListIndex){

       if(storageReferencePath!=null) {
           if (!storageReferencePath.equals("")) {

               StorageReference imageReference = firebaseStorage.getReference();
               imageReference = imageReference.child(storageReferencePath);

               imageReference.getBytes(6 * Common.ONE_MEGA_BYTE).addOnCompleteListener(new OnCompleteListener<byte[]>() {
                   @Override
                   public void onComplete(@NonNull Task<byte[]> task) {
                       if (task.isSuccessful()) {
                           if (postArrayListIndex != -1/*aka a post image*/) {
                               MyPost aPost = postArrayList.get(postArrayListIndex);
                               aPost.setPostedImage(BitmapFactory.decodeByteArray(task.getResult().clone(), 0, task.getResult().length));
                               postArrayList.remove(postArrayListIndex);
                               postArrayList.add(postArrayListIndex, aPost);
                               dataUpdatedNotifyListView();
                           } else {
                               profileImageAsBitmap = BitmapFactory.decodeByteArray(Objects.requireNonNull(task.getResult()).clone(), 0, task.getResult().length);
                               profileImage.setImageBitmap(profileImageAsBitmap);
                               //the below statements must stay in order and here since we want to use myUser0 to instantiate the post
                               myUser0.setProfileImageBitmap(profileImageAsBitmap);
                               getUserPosts();
                           }

                       } else {
                           Log.d("ConnectivityFireBase", "Something went wrong and we couldn't get images " + Objects.requireNonNull(task.getException()).toString());
                       }
                   }
               }).addOnFailureListener(new OnFailureListener() {
                   @Override
                   public void onFailure(@NonNull Exception e) {
                       try{
                           throw e;
                       }
                       catch (Exception ee){
                           Log.v("ConnectivityFireBase", "Something went wrong and we couldn't get images" + ee.getMessage());
                       }
                   }
               });
           } else{addPostToArrayListWithoutImage(postArrayListIndex);}
       }else {addPostToArrayListWithoutImage(postArrayListIndex);}
   }
    private  void addPostToArrayListWithoutImage(int postArrayListIndex){
    MyPost aPost = postArrayList.get(postArrayListIndex);
    aPost.setPostedImage(null);
    postArrayList.remove(postArrayListIndex);
    postArrayList.add(postArrayListIndex, aPost);
    dataUpdatedNotifyListView();
}
    private void selectImage() {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK);
        pickIntent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

        startActivityForResult(chooserIntent, PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                Toast.makeText(getContext(), "ERROR", Toast.LENGTH_SHORT).show();
                return;
            }
            InputStream inputStream = null;
            try {
                inputStream = Objects.requireNonNull(getContext()).getContentResolver().openInputStream(Objects.requireNonNull(data.getData()));
            } catch (FileNotFoundException e) {
                Toast.makeText(getContext(), "File not found exception", Toast.LENGTH_SHORT).show();
            }
            if (inputStream != null) {
                updateProfileImage(BitmapFactory.decodeStream(inputStream));
            //    profileImage.setImageBitmap(BitmapFactory.decodeStream(inputStream));
            } else {
                Toast.makeText(getContext(), "inputStream is null", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void deletePost(final int postPosition){

        firebaseFirestore.collection("AllPosts").document(postArrayList.get(postPosition).getPostID()).delete();
        StorageReference imageReference=firebaseStorage.getReference();
        imageReference=imageReference.child("images/"+firebaseUser.getUid()+"/"+postArrayList.get(postPosition).getPostID()+".JPEG");
        imageReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                postArrayList.remove(postPosition);
                dataUpdatedNotifyListView();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                try{
                    throw e;
                }
                catch (Exception ee){
                    Log.v("ConnectivityFireBase", "Something went wrong we couldn't Delete the post" + ee.getMessage());
                }
            }
        });
    }

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public ProfileFragment() {

    }

    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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

    private void updateProfileName(String newName){
        firebaseFirestore.collection("Profiles").document(firebaseUser.getUid()).update("name",newName).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                getUserProfile();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                try{
                    throw e;
                }
                catch (Exception ee){
                    Log.v("ConnectivityFireBase", "Something went wrong we couldn't Update Profile name" + ee.getMessage());
                }
            }
        });

    }
    private void updateProfileImage(final Bitmap imageToPush){
            progressDialog.setMessage("Uploading...");
            progressDialog.show();

            StorageReference imageReference = firebaseStorage.getReference();
            imageReference = imageReference.child("images/"+firebaseUser.getUid()+"/"+"ProfileImage"+".JPEG");
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            imageToPush.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] imageInBytes = byteArrayOutputStream.toByteArray();
            UploadTask uploadImageTask = imageReference.putBytes(imageInBytes);
            uploadImageTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "Done", Toast.LENGTH_LONG).show();
                        Log.v("ConnectivityFireBase", "Done Updating Profile image");
                        getUserProfile();
                    } else {

                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "Something went wrong we couldn't update your profile image", Toast.LENGTH_LONG).show();
                        Log.v("ConnectivityFireBase", "Something went wrong we couldn't Update Profile image" + "onComplete callback Update Profile Image" + task.getException().getMessage());
                        somethingWentWrongPleaseTryAgainImageProblem(imageToPush);

                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    try{
                        throw e;
                    }
                    catch (Exception ee){
                        Log.v("ConnectivityFireBase", "Something went wrong we couldn't Update Profile image" + "onComplete callback Update Profile Image" + ee.getMessage());
                    }
                }
            });

        }
    private void somethingWentWrongPleaseTryAgainImageProblem(final Bitmap imageToPush){

        AlertDialog.Builder alertDialogBuilder=  new AlertDialog.Builder(Objects.requireNonNull(getContext())).setTitle("Failed To Complete Sign UP")
                .setMessage("Something went wrong and we couldn't sign you up, let's give it another go shall we")
                .setPositiveButton("Retry", new DialogInterface.OnClickListener(){
                    public void onClick( DialogInterface dialog,int id){
                        updateProfileImage(imageToPush);
                    }
                }).setNegativeButton("Cancel", null);
        alertDialogBuilder.create().show();
    }
}
