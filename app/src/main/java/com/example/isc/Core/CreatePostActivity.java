package com.example.isc.Core;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.isc.Common;
import com.example.isc.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class CreatePostActivity extends AppCompatActivity {

    private  EditText cpEditText;
    private  ImageView cpImage;
    private  Bitmap cpImageAsBitmap = null;
    private  Button postButton;
    private  ImageButton showPostLevelImageButton;
    private  LinearLayout createPostLL, optionsLL, photoLL, includeEventLL, tagColleagueLL, specifyDepartmentLL;
    private  ScrollView scrollView;
    private  TextView photoLLTextView, eventsTextView;
    private static final int PICK_IMAGE = 1;

    public String checkedDepartments = "", events = "", colleagues = "",textToPost="";
    private ProgressDialog progressDialog;


    private  FirebaseFirestore firebaseFirestore;
    private FirebaseUser firebaseUser;

    private   CreatePostViewPagerAdapter viewPagerAdapter;
    private   ViewPager viewPager;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
try {
    scrollView = findViewById(R.id.createPostScrollView);
    viewPagerAdapter = new CreatePostViewPagerAdapter(getSupportFragmentManager());
    viewPagerAdapter.addFragment(new IncludeEventActivity());
    viewPagerAdapter.addFragment(new TagColleagueActivity());
    viewPagerAdapter.addFragment(new PostLevelActivity());
    viewPager = findViewById(R.id.createPostViewPager);
    viewPager.setAdapter(viewPagerAdapter);


    firebaseFirestore = FirebaseFirestore.getInstance();
    firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    progressDialog = new ProgressDialog(this);
    Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setTitle(
            Html.fromHtml("<font color=\"#1976D2\"> Create post </font>")
    );
    getSupportActionBar().setDisplayShowTitleEnabled(true);
    getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_24dp);


    eventsTextView = findViewById(R.id.eventsTextView);
    eventsTextView.setText(events);

    postButton = findViewById(R.id.postButton);
    postButton.setTextColor(Color.GRAY);

    cpImage = findViewById(R.id.cpImage);

    showPostLevelImageButton = findViewById(R.id.showPostLevelImageButton);

    cpEditText = findViewById(R.id.cpEditText);
    cpEditText.setText(textToPost);
    cpEditText.addTextChangedListener(new TextWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() == 0) {
                postButton.setTextColor(Color.GRAY);
            } else {
                postButton.setTextColor(Color.BLACK);
            }
            textToPost = s.toString();
        }
    });

    createPostLL = findViewById(R.id.createPostLL);
    optionsLL = findViewById(R.id.optionsLinearLayout);
    photoLL = findViewById(R.id.photoLinearLayout);
    photoLLTextView = findViewById(R.id.photoLLTextView);
    includeEventLL = findViewById(R.id.includeEventLinearLayout);
    tagColleagueLL = findViewById(R.id.tagColleagueLinearLayout);
    specifyDepartmentLL = findViewById(R.id.specifyDepartmentLinearLayout);

    photoLL.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            selectImage();
        }
    });
    includeEventLL.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            includeEvent();
        }
    });
    tagColleagueLL.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            tagColleague();
        }
    });
    specifyDepartmentLL.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            specifyDepartment();
        }
    });
}catch (Exception e){
    Log.v("AppLogic","Something went wrong "
            +"the cause: " +e.getCause()
            + "the message: "+e.getMessage()
    );
}

    }


    public void post(View view) {
        if (cpEditText.getText().toString().length() > 0 || cpImageAsBitmap != null) {

            progressDialog.setMessage("Posting...");
            progressDialog.show();


            final Map<String, Object> map = new HashMap<>();
            map.put("userID",firebaseUser.getUid());
            map.put("checkedDepartments", checkedDepartments);
            map.put("events", events);
            map.put("colleagues", colleagues);
            map.put("cpText", cpEditText.getText().toString());
            map.put("postID", UUID.randomUUID().toString());
            map.put("date", Timestamp.now());
            map.put("dateInMillis",Timestamp.now().toDate().getTime());
            if (cpImageAsBitmap != null) {
                map.put("imageReferenceInStorage", "images/" + firebaseUser.getUid() + "/" + map.get("postID") + ".JPEG");
            } else {
                map.put("imageReferenceInStorage", "");
            }

            firebaseFirestore.collection("AllPosts").document(map.get("postID").toString()).set(map).
                    addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                if (cpImageAsBitmap == null) {
                                    progressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "Posted", Toast.LENGTH_LONG).show();
                                    Log.v("ConnectivityFireBase", "Posted Successfully");

                                } else {
                                    PushPostImageToServer(Objects.requireNonNull(map.get("imageReferenceInStorage")).toString(), cpImageAsBitmap, Objects.requireNonNull(map.get("postID")).toString());
                                }
                                createNotificationDataOnServer(checkedDepartments, Objects.requireNonNull(map.get("postID")).toString());
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Something went wrong we couldn't post", Toast.LENGTH_LONG).show();
                                Log.v("ConnectivityFireBase", "Something went wrong we couldn't post" + "Post Data" + Objects.requireNonNull(task.getException()).getMessage());
                            }
                        }

                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Something went wrong we couldn't post", Toast.LENGTH_LONG).show();

                    try{
                        throw  e;
                    }
                    catch (Exception ee){
                        Log.v("ConnectivityFireBase", "Something went wrong we couldn't post" + "Post Data" + ee.getMessage());
                    }
                }
            });
        }
    }

    private  void createNotificationDataOnServer(final String checkedDepartments, final String postID){
        firebaseFirestore.collection("AllPosts").document(postID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Map<String,Object> map=new HashMap<>();
                map.put("userID",firebaseUser.getUid());
                map.put("notificationText","has posted in the department of "+checkedDepartments);


                if(Objects.requireNonNull(task.getResult()).contains("date")){
                    map.put("notificationTime", Objects.requireNonNull(task.getResult().getTimestamp("date")).toDate().toString());
                    map.put("notificationTimeInMillis",task.getResult().getLong("dateInMillis"));
                }
                else{
                    map.put("notificationTime","");
                    map.put("notificationTimeInMillis",0);
                }

                firebaseFirestore.collection("Notifications").document(postID).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getApplicationContext(),"Notification was sent to all users",Toast.LENGTH_LONG).show();
                            goBackToCoreActivity();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        try{
                            throw  e;
                        }
                         catch (Exception ee){
                            Log.v("ConnectivityFireBase", "Failed to send Notification"+ ee.toString());
                        }
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                try{
                    throw  e;
                }
                catch (Exception ee){
                    Log.v("ConnectivityFireBase", "Failed to create Notification"+ ee.toString());
                }
            }
        });

    }
    private void PushPostImageToServer(String imageReferenceInStorage, Bitmap imageToPush, final String postID) {
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference imageReference = firebaseStorage.getReference();
        imageReference = imageReference.child(imageReferenceInStorage);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        imageToPush.compress(Bitmap.CompressFormat.JPEG, Common.IMAGE_QUALITY, byteArrayOutputStream);
        byte[] imageInBytes = byteArrayOutputStream.toByteArray();
        UploadTask uploadImageTask = imageReference.putBytes(imageInBytes);
        uploadImageTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Posted", Toast.LENGTH_LONG).show();
                    Log.v("ConnectivityFireBase", "Posted Successfully");
                    goBackToCoreActivity();
                } else {
                    deletePostPushImageWasNotSuccessful(postID);
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Something went wrong we couldn't post", Toast.LENGTH_LONG).show();
                    Log.v("ConnectivityFireBase", "Something went wrong we couldn't post" + "onComplete callback Push Image" + Objects.requireNonNull(task.getException()).getMessage());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Something went wrong we couldn't post", Toast.LENGTH_LONG).show();
                try{
                    throw e;
                }
               catch(StorageException eee) {

                    Log.v("ConnectivityFireBase", "Something went wrong we couldn't post" + "onFailure callback Push Image" + "Error code" + eee.getErrorCode() + " http code" + eee.getHttpResultCode() + " cause" + eee.getCause());
                }
                catch (Exception ee){
                    Log.v("ConnectivityFireBase", "Something went wrong we couldn't post" + "onFailure callback Push Image" + ee.toString());
                }

            }
        });

    }

    private void deletePostPushImageWasNotSuccessful(String postID) {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore.collection("AllPosts").document(Objects.requireNonNull(firebaseUser).getUid()).collection("userPosts").document(postID).delete().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                try{
                    throw e;
                }
                catch (Exception ee){
                    Log.v("ConnectivityFireBase", "Failed to delete Post after image uploading failure" + ee.getMessage());
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Discard the post")
                .setMessage("Are you sure you want to discard this post?")
                .setPositiveButton("Discard", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplicationContext(), CoreActivity.class);
                        intent.putExtra("to", "home");
                        startActivity(intent);

                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    private  void goBackToCoreActivity() {
        Intent intent = new Intent(getApplicationContext(), CoreActivity.class);
        intent.putExtra("to", "home");
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        new AlertDialog.Builder(this)
                .setTitle("Discard the post")
                .setMessage("Are you sure you want to discard this post?")
                .setPositiveButton("Discard", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplicationContext(), CoreActivity.class);
                        intent.putExtra("to", "home");
                        startActivity(intent);
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
        return true;
    }

    private void selectImage() {
        if (photoLLTextView.getText().equals("Photo")) {
            Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
            getIntent.setType("image/*");

            Intent pickIntent = new Intent(Intent.ACTION_PICK);
            pickIntent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");

            Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});
            startActivityForResult(chooserIntent, PICK_IMAGE);
        } else {
            cpImage.setImageBitmap(null);
            cpImageAsBitmap = null;
            photoLLTextView.setText("Photo");
            if (cpEditText.getText().toString().trim().length() == 0) {
                postButton.setTextColor(Color.GRAY);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                Toast.makeText(getApplicationContext(), "ERROR", Toast.LENGTH_SHORT).show();
                cpImageAsBitmap=null;
                cpImage.setImageBitmap(null);
                return;
            }
            InputStream inputStream = null;
            try {
                inputStream = getApplicationContext().getContentResolver().openInputStream(Objects.requireNonNull(data.getData()));
            } catch (FileNotFoundException e) {
                Toast.makeText(getApplicationContext(), "File not found exception", Toast.LENGTH_SHORT).show();
                cpImageAsBitmap=null;
                cpImage.setImageBitmap(null);
            }
            if (inputStream != null) {
                cpImageAsBitmap = BitmapFactory.decodeStream(inputStream);
                cpImage.setImageBitmap(cpImageAsBitmap);

            /*    SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                editor.putString("cpImage", bitmapToString(cpImageAsBitmap));
                editor.apply();*/

                photoLLTextView.setText("Remove photo");
                postButton.setTextColor(Color.BLACK);
            } else {
                Toast.makeText(getApplicationContext(), "inputStream is null", Toast.LENGTH_SHORT).show();
                cpImageAsBitmap=null;
                cpImage.setImageBitmap(null);
            }
        }
        Log.d("AppLogic", "onActivityResult: ");
    }

    private void includeEvent() {
       viewPager.setCurrentItem(0);
        optionsLL.setVisibility(View.GONE);
        scrollView.setVisibility(View.GONE);
       viewPager.setVisibility(View.VISIBLE);

    }

    private void tagColleague() {
      viewPager.setCurrentItem(1);
        optionsLL.setVisibility(View.GONE);
        scrollView.setVisibility(View.GONE);
      viewPager.setVisibility(View.VISIBLE);

    }

    private void specifyDepartment() {
       viewPager.setCurrentItem(2);
        optionsLL.setVisibility(View.GONE);
        scrollView.setVisibility(View.GONE);
       viewPager.setVisibility(View.VISIBLE);
    }
  public void returnViewToTheParentActivity(){
      eventsTextView.setText(events);
      viewPager.setVisibility(View.GONE);
      optionsLL.setVisibility(View.VISIBLE);
      scrollView.setVisibility(View.VISIBLE);
  }
    public void showPostLevel(View view) {
        if (checkedDepartments == null) {
            checkedDepartments = "None";
        }
        new AlertDialog.Builder(this)
                .setTitle("Your post is visible to the departments of:")
                .setMessage(checkedDepartments)
                .setPositiveButton("OK", null)
                .setNegativeButton(null, null)
                .show();
    }

    public void showTagColleague(View view) {
        if (colleagues.isEmpty()) colleagues = "None";
        new AlertDialog.Builder(this)
                .setTitle("The following colleagues are tagged in your post:")
                .setMessage(colleagues)
                .setPositiveButton("OK", null)
                .setNegativeButton(null, null)
                .show();
    }


}
