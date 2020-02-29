package com.example.isc.Core.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.isc.Common;
import com.example.isc.Core.MyPost;
import com.example.isc.Core.MyUser;
import com.example.isc.Core.NonScrollListView;
import com.example.isc.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProfileFragment extends Fragment {

    private ImageView profileImage;
    private Bitmap profileImageAsBitmap;
    private LinearLayout nameLayout, positionLayout, studentNumberLayout, emailLayout, postLayout;
    private TextView nameTextView, positionTextView, studentNumberTextView, emailTextView;
    private EditText nameEditText, positionEditText, studentNumberEditText, emailEditText;
    private ImageView editNameIV, editPositionIV, editStudentNumberIV, editEmailIV, showProfilePostIV;
    private static final int PICK_IMAGE = 1;

    MyUser myUser0;

    private Boolean n = false, p = false, s = false, e = false, postIsVisible = false;

    FloatingActionButton profilePostsUpButton;
    ScrollView profileScrollView;

    NonScrollListView profilePostsListView;
    ProfilePostsListAdapter adapter;
    public static ArrayList<MyPost> postArrayList = new ArrayList<>();

    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;
    FirebaseStorage firebaseStorage;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_profile, container, false);

        initialize(fragmentView);
        firebaseStorage=FirebaseStorage.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        firebaseFirestore.collection("Profiles").document(firebaseUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    //i will deal with the image later
                    myUser0 = new MyUser(firebaseUser.getUid(),null, (String) task.getResult().get("name"), Common.position[task.getResult().getLong("position").intValue()]);
                    Log.v("ConnectivityFireBase", "Received profile successfully");

                    nameTextView.setText(myUser0.getFullName());
                    positionTextView.setText(myUser0.getPosition());
                    studentNumberTextView.setText("Not Implemented yet");
                    emailTextView.setText(firebaseUser.getEmail());

                    getImageFromServer(task.getResult().get("profileImageReferenceInStorage").toString(),-1);//-1 indicates profile image

                } else {
                    Log.v("ConnectivityFireBase", "Error receiving profile " + task.getException());
                }
            }
        });


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

        return fragmentView;
    }

    void initialize(View fragmentView) {

        profileImage = fragmentView.findViewById(R.id.profileImage);
        nameLayout = fragmentView.findViewById(R.id.profileNameLayout);
        positionLayout = fragmentView.findViewById(R.id.profilePositionLayout);
        studentNumberLayout = fragmentView.findViewById(R.id.profileStudentNumberLayout);
        emailLayout = fragmentView.findViewById(R.id.profileEmailLayout);
        postLayout = fragmentView.findViewById(R.id.profilePostLayout);

        nameTextView = fragmentView.findViewById(R.id.profileNameTextView);
        positionTextView = fragmentView.findViewById(R.id.profilePositionTextView);
        studentNumberTextView = fragmentView.findViewById(R.id.profileStudentNumberTextView);
        emailTextView = fragmentView.findViewById(R.id.profileEmailTextView);

        nameEditText = fragmentView.findViewById(R.id.nameEditText);
        positionEditText = fragmentView.findViewById(R.id.positionEditText);
        studentNumberEditText = fragmentView.findViewById(R.id.studentNumberEditText);
        emailEditText = fragmentView.findViewById(R.id.emailEditText);

        editNameIV = fragmentView.findViewById(R.id.editName);
        editPositionIV = fragmentView.findViewById(R.id.editPosition);
        editStudentNumberIV = fragmentView.findViewById(R.id.editStudentNumber);
        editEmailIV = fragmentView.findViewById(R.id.editEmail);
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
                positionEditText.setVisibility(View.VISIBLE);
                positionEditText.setText(positionTextView.getText());
                editPositionIV.setImageResource(R.drawable.ic_add_gray_24dp);
                p = true;
            }
        });
        editPositionIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (p) {
                    positionTextView.setVisibility(View.VISIBLE);
                    positionEditText.setVisibility(View.GONE);
                    editPositionIV.setImageResource(R.drawable.ic_edit_black_24dp);
                    p = false;
                } else {
                    positionTextView.setVisibility(View.GONE);
                    positionEditText.setVisibility(View.VISIBLE);
                    positionEditText.setText(positionTextView.getText());
                    editPositionIV.setImageResource(R.drawable.ic_add_gray_24dp);
                    p = true;
                }
            }
        });

        studentNumberLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                studentNumberTextView.setVisibility(View.GONE);
                studentNumberEditText.setVisibility(View.VISIBLE);
                studentNumberEditText.setText(studentNumberTextView.getText());
                editStudentNumberIV.setImageResource(R.drawable.ic_add_gray_24dp);
                s = true;
            }
        });
        editStudentNumberIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (s) {
                    studentNumberTextView.setVisibility(View.VISIBLE);
                    studentNumberEditText.setVisibility(View.GONE);
                    editStudentNumberIV.setImageResource(R.drawable.ic_edit_black_24dp);
                    s = false;
                } else {
                    studentNumberTextView.setVisibility(View.GONE);
                    studentNumberEditText.setVisibility(View.VISIBLE);
                    studentNumberEditText.setText(studentNumberTextView.getText());
                    editStudentNumberIV.setImageResource(R.drawable.ic_add_gray_24dp);
                    s = true;
                }
            }
        });

        emailLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailTextView.setVisibility(View.GONE);
                emailEditText.setVisibility(View.VISIBLE);
                emailEditText.setText(emailTextView.getText());
                editEmailIV.setImageResource(R.drawable.ic_add_gray_24dp);
                e = true;
            }
        });
        editEmailIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (e) {
                    emailTextView.setVisibility(View.VISIBLE);
                    emailEditText.setVisibility(View.GONE);
                    editEmailIV.setImageResource(R.drawable.ic_edit_black_24dp);
                    e = false;
                } else {
                    emailTextView.setVisibility(View.GONE);
                    emailEditText.setVisibility(View.VISIBLE);
                    emailEditText.setText(emailTextView.getText());
                    editEmailIV.setImageResource(R.drawable.ic_add_gray_24dp);
                    e = true;
                }
            }
        });
        profilePostsUpButton = fragmentView.findViewById(R.id.profileUpButton);
    }


    void getUserPosts() {
        firebaseFirestore.collection("AllPosts").document(firebaseUser.getUid()).collection("userPosts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    postArrayList.clear();
                    List<DocumentSnapshot> snapshots=task.getResult().getDocuments();
                    for (int i=0;i<snapshots.size();i++) {
                        String cpText = snapshots.get(i).get("cpText").toString();
                        String checkedDepartments = snapshots.get(i).get("checkedDepartments").toString();
                        String colleagues = snapshots.get(i).get("colleagues").toString();
                        String events = snapshots.get(i).get("events").toString();
                        postArrayList.add(new MyPost(myUser0, cpText, null, checkedDepartments, colleagues, events));
                        String imageReferenceInStorage=snapshots.get(i).get("imageReferenceInStorage").toString();
                        getImageFromServer(imageReferenceInStorage,postArrayList.size()-1/*aka index where the image should be placed*/);
                    }
                    dataUpdatedNotifyListView();
                } else {
                    Log.v("ConnectivityFireBase", "Error receiving posts " + task.getException());

                }
            }
        });
    }

    void dataUpdatedNotifyListView() {
        adapter = new ProfilePostsListAdapter(getContext(), R.layout.activity_profile_post_list_adapter, postArrayList);
        profilePostsListView.setAdapter(adapter);
        profilePostsListView.setScrollContainer(false);
    }

   void getImageFromServer(String storageReferencePath, final int postArrayListIndex){

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
                           } else if (postArrayListIndex == -1/*aka the profile image*/) {
                               profileImageAsBitmap = BitmapFactory.decodeByteArray(task.getResult().clone(), 0, task.getResult().length);
                               profileImage.setImageBitmap(profileImageAsBitmap);
                               //the below statements must stay in order and here since we want to use myUser0 to instantiate the post
                               myUser0.setProfileImageBitmap(profileImageAsBitmap);
                               getUserPosts();
                           }

                       } else {
                           Log.d("ConnectivityFireBase", "Something went wrong and we couldn't get images " + task.getException().toString());
                       }
                   }
               });
           } else{addPostToArrayListWithoutImage(postArrayListIndex);}
       }else {addPostToArrayListWithoutImage(postArrayListIndex);}
   }
void addPostToArrayListWithoutImage(int postArrayListIndex){
    MyPost aPost = postArrayList.get(postArrayListIndex);
    aPost.setPostedImage(null);
    postArrayList.remove(postArrayListIndex);
    postArrayList.add(postArrayListIndex, aPost);
    dataUpdatedNotifyListView();
}
    public void selectImage() {
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
                profileImage.setImageBitmap(BitmapFactory.decodeStream(inputStream));
            } else {
                Toast.makeText(getContext(), "inputStream is null", Toast.LENGTH_SHORT).show();
            }
        }
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

}