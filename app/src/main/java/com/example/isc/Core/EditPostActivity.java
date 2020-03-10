package com.example.isc.Core;

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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.isc.Core.Fragments.ProfileFragment;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EditPostActivity extends AppCompatActivity {

    private EditText epEditText;
    private String postID;
    private ImageView epImage;
    private Bitmap epImageAsBitmap;
    private Button editPostButton;
    private ImageButton epShowPostLevelImageButton;
    private LinearLayout epPhotoLL, epIncludeEventLL, epTagColleagueLL, epSpecifyDepartmentLL;

    private TextView epPhotoLLTextView, epEventsTextView;

    public String epCheckedDepartments = "", epEvents = "", epColleagues = "";
    private ProgressDialog progressDialog;
    private static final int PICK_IMAGE = 1;

    ArrayList<Object> postDataBeforeModification=new ArrayList<>();//just in case the image push fails we resend the previous data to database

    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage firebaseStorage;
    private FirebaseUser firebaseUser;

    private CreatePostViewPagerAdapter viewPagerAdapter;
    private ViewPager viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);



        viewPagerAdapter = new CreatePostViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new IncludeEventActivity());
        viewPagerAdapter.addFragment(new TagColleagueActivity());
        viewPagerAdapter.addFragment(new PostLevelActivity());
        viewPager=findViewById(R.id.editPostViewPager);
        viewPager.setAdapter(viewPagerAdapter);


        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        progressDialog = new ProgressDialog(this);


        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(
                Html.fromHtml("<font color=\"#1976D2\"> Edit post </font>")
        );
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_24dp);

        Intent intent = getIntent();

        epEventsTextView = findViewById(R.id.epEventsTextView);
        if (intent.getStringExtra("event") != null) {
            epEventsTextView.setText(epEvents);
        }
        editPostButton = findViewById(R.id.editPostButton);
        editPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editPost();
            }
        });
        editPostButton.setTextColor(Color.GRAY);
        epImage = findViewById(R.id.epImage);
        epShowPostLevelImageButton = findViewById(R.id.epShowPostLevelImageButton);
        epEditText = findViewById(R.id.epEditText);
        epEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0 && epImage.getDrawable() == null) {
                    editPostButton.setTextColor(Color.GRAY);
                } else {
                    editPostButton.setTextColor(Color.BLACK);
                }
            }
        });
        epPhotoLL = findViewById(R.id.epPhotoLL);
        epPhotoLLTextView = findViewById(R.id.epPhotoLLTextView);
        epIncludeEventLL = findViewById(R.id.epIncludeEventLL);
        epTagColleagueLL = findViewById(R.id.epTagColleagueLL);
        epSpecifyDepartmentLL = findViewById(R.id.epSpecifyDepartmentLL);

        epPhotoLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        epIncludeEventLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                includeEvent();
            }
        });
        epTagColleagueLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tagColleague();
            }
        });
        epSpecifyDepartmentLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                specifyDepartment();
            }
        });

        initializeUI(intent.getIntExtra("position",-1));
    }

    private void initializeUI(int position) {
        MyPost myPost = ProfileFragment.postArrayList.get(position);
        epEditText.setText(myPost.getPostedText());
        epImageAsBitmap=myPost.getPostedImageBitmap();
        epImage.setImageBitmap(epImageAsBitmap);
        epColleagues = myPost.getMyPostTagColleague();
        epCheckedDepartments = myPost.getMyPostLevel();
        epEventsTextView.setText(myPost.getMyPostEvents());

        postID=myPost.getPostID();

        postDataBeforeModification.clear();
        postDataBeforeModification.add(myPost.getPostedText());
        postDataBeforeModification.add(myPost.getPostedImageBitmap());
        postDataBeforeModification.add(myPost.getMyPostTagColleague());
        postDataBeforeModification.add(myPost.getMyPostLevel());
        postDataBeforeModification.add(myPost.getMyPostEvents());

        if(epImageAsBitmap!=null){
            epPhotoLLTextView.setText("Remove photo");
            editPostButton.setTextColor(Color.BLACK);
        }
    }

    public void editPost() {

        if (epEditText.getText().toString().length() > 0 || epImageAsBitmap != null) {

            progressDialog.setMessage("Updating post...");
            progressDialog.show();


            final Map<String, Object> map = new HashMap<>();
            map.put("userID",firebaseUser.getUid());
            map.put("checkedDepartments", epCheckedDepartments);
            map.put("events", epEvents);
            map.put("colleagues", epColleagues);
            map.put("cpText", epEditText.getText().toString());
            map.put("postID", postID);
            map.put("date", Timestamp.now());
            map.put("dateInMillis",Timestamp.now().toDate().getTime());
            if (epImageAsBitmap != null) {
                map.put("imageReferenceInStorage", "images/" + firebaseUser.getUid() + "/" + map.get("postID") + ".JPEG");
            } else {
                map.put("imageReferenceInStorage", "");
                //this means user chose to discard the image so we need to delete if from firestorage
                //should be delete it when successfully upload

            }


            firebaseFirestore.collection("AllPosts").document(map.get("postID").toString()).set(map).
                    addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                if (epImageAsBitmap == null) {
                                    deleteImageFromServer(map.get("postID").toString());
                                    progressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "Post Edited", Toast.LENGTH_LONG).show();
                                    Log.v("ConnectivityFireBase", "Post Edited Successfully");

                                } else {
                                    PushPostImageToServer(Objects.requireNonNull(map.get("imageReferenceInStorage")).toString(), epImageAsBitmap, Objects.requireNonNull(map.get("postID")).toString());
                                }
                                createNotificationDataOnServer(epCheckedDepartments, Objects.requireNonNull(map.get("postID")).toString());
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Something went wrong we couldn't edit post", Toast.LENGTH_LONG).show();
                                Log.v("ConnectivityFireBase", "Something went wrong we couldn't edit post" + "Edit Post Data" + Objects.requireNonNull(task.getException()).getMessage());
                            }
                        }

                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Something went wrong we couldn't edit post", Toast.LENGTH_LONG).show();

                    try {
                        throw e;
                    }
                    catch(Exception ee){
                        Log.v("ConnectivityFireBase", "Something went wrong we couldn't edit post" + "Edit Post Data" + e.getMessage());
                    }
                }
            });
        }
    }
    private void deleteImageFromServer(String postID){
        StorageReference imageReference=firebaseStorage.getReference();
        imageReference=imageReference.child("images/" + firebaseUser.getUid() + "/" + postID + ".JPEG");
        imageReference.delete().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                try {
                    throw e;
                }
                catch(Exception ee){
                    Log.v("ConnectivityFireBase", "Something went wrong we couldn't delete image from server" + e.getMessage());
                }
            }
        });
    }
    private void PushPostImageToServer(String imageReferenceInStorage, Bitmap imageToPush, final String postID) {
        StorageReference imageReference = firebaseStorage.getReference();
        imageReference = imageReference.child(imageReferenceInStorage);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        imageToPush.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageInBytes = byteArrayOutputStream.toByteArray();
        UploadTask uploadImageTask = imageReference.putBytes(imageInBytes);
        uploadImageTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Post Edited", Toast.LENGTH_LONG).show();
                    Log.v("ConnectivityFireBase", "Edited Post Successfully");
                    goBackToCoreActivity();
                } else {
                    revertPostChangesPushImageWasNotSuccessful();
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Something went wrong we couldn't edit post", Toast.LENGTH_LONG).show();
                    Log.v("ConnectivityFireBase", "Something went wrong we couldn't edit post" + "onComplete callback (edit) Push Image" + Objects.requireNonNull(task.getException()).getMessage());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Something went wrong we couldn't edit post", Toast.LENGTH_LONG).show();
                try{
                    throw e;
                }
                catch (StorageException ee){
                    Log.v("ConnectivityFireBase", "Something went wrong we couldn't edit post" + "onFailure callback (edit)Push Image" + "Error code" + ee.getErrorCode() + " http code" + ee.getHttpResultCode() + " cause" + ee.getCause());
                }
                catch (Exception eee){
                    Log.v("ConnectivityFireBase", "Something went wrong we couldn't edit post" + "onFailure callback (edit)Push Image" + e.toString());
                }

            }
        });

    }

    private void revertPostChangesPushImageWasNotSuccessful() {

        epEditText.setText(postDataBeforeModification.get(0).toString());
        epImageAsBitmap=(Bitmap) postDataBeforeModification.get(1);
        epImage.setImageBitmap(epImageAsBitmap);
        epColleagues = postDataBeforeModification.get(2).toString();
        epCheckedDepartments = postDataBeforeModification.get(3).toString();
        epEventsTextView.setText(postDataBeforeModification.get(4).toString());

        editPost();
    }

    private void goBackToCoreActivity() {
        Intent intent = new Intent(getApplicationContext(), CoreActivity.class);
        intent.putExtra("to", "profile");
        startActivity(intent);
    }

    private void createNotificationDataOnServer(final String checkedDepartments, final String postID) {
        firebaseFirestore.collection("AllPosts").document(/*aka users*/firebaseUser.getUid()).collection("userPosts").document(postID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Map<String, Object> map = new HashMap<>();
                map.put("userID", firebaseUser.getUid());
                map.put("notificationText", "has edited his post in the department of " + checkedDepartments);


                if (Objects.requireNonNull(task.getResult()).contains("date")) {
                    map.put("notificationTime", Objects.requireNonNull(task.getResult().getTimestamp("date")).toDate().toString());
                    map.put("notificationTimeInMillis",task.getResult().getLong("dateInMillis"));

                } else {
                    map.put("notificationTime", "");
                    map.put("notificationTimeInMillis",0);
                }

                firebaseFirestore.collection("Notifications").document(postID).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Notification was sent to all users", Toast.LENGTH_LONG).show();
                            goBackToCoreActivity();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        try {
                            throw e;
                        }
                        catch(Exception ee){
                            Log.v("ConnectivityFireBase", "Something went wrong we couldn't send notification" + e.getMessage());
                        }
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                try {
                    throw e;
                }
                catch(Exception ee){
                    Log.v("ConnectivityFireBase", "Something went wrong we couldn't create notification" + e.getMessage());
                }

            }
        });

    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Discard the post")
                .setMessage("Are you sure you want to discard the changes?")
                .setPositiveButton("Discard", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplicationContext(), CoreActivity.class);
                        intent.putExtra("to", "profile");
                        startActivity(intent);
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void selectImage() {

        if (epPhotoLLTextView.getText().equals("Photo")) {
            Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
            getIntent.setType("image/*");

            Intent pickIntent = new Intent(Intent.ACTION_PICK);
            pickIntent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");

            Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});
            startActivityForResult(chooserIntent, PICK_IMAGE);
        } else {
            epImage.setImageBitmap(null);
            epImageAsBitmap = null;
            epPhotoLLTextView.setText("Photo");
            if (epEditText.getText().toString().trim().length() == 0) {
                editPostButton.setTextColor(Color.GRAY);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                Toast.makeText(getApplicationContext(), "ERROR", Toast.LENGTH_SHORT).show();
                epImageAsBitmap = null;
                epImage.setImageBitmap(null);
                return;
            }
            InputStream inputStream = null;
            try {
                inputStream = getApplicationContext().getContentResolver().openInputStream(Objects.requireNonNull(data.getData()));
            } catch (FileNotFoundException e) {
                Toast.makeText(getApplicationContext(), "File not found exception", Toast.LENGTH_SHORT).show();
                epImageAsBitmap = null;
                epImage.setImageBitmap(null);
            }
            if (inputStream != null) {
                epImageAsBitmap = BitmapFactory.decodeStream(inputStream);
                epImage.setImageBitmap(epImageAsBitmap);

                epPhotoLLTextView.setText("Remove photo");
                editPostButton.setTextColor(Color.BLACK);
            } else {
                Toast.makeText(getApplicationContext(), "inputStream is null", Toast.LENGTH_SHORT).show();
                epImageAsBitmap = null;
                epImage.setImageBitmap(null);
            }
        }
        Log.d("AppLogic", "onActivityResult: ");
    }


    private void includeEvent() {
        viewPager.setCurrentItem(0);
        //   epOptionsLL.setVisibility(View.GONE);
        //   scrollView.setVisibility(View.GONE);
        viewPager.setVisibility(View.VISIBLE);

    }

    private void tagColleague() {
        viewPager.setCurrentItem(1);
        //   optionsLL.setVisibility(View.GONE);
        //   scrollView.setVisibility(View.GONE);
        viewPager.setVisibility(View.VISIBLE);

    }

    private void specifyDepartment() {
        viewPager.setCurrentItem(2);
        //     optionsLL.setVisibility(View.GONE);
        //     scrollView.setVisibility(View.GONE);
        viewPager.setVisibility(View.VISIBLE);
    }

    public void returnViewToTheParentActivity() {
        epEventsTextView.setText(epEvents);
        viewPager.setVisibility(View.GONE);
        //       optionsLL.setVisibility(View.VISIBLE);
        //     scrollView.setVisibility(View.VISIBLE);
    }

    public void showPostLevel(View view) {
        if (epCheckedDepartments == null) {
            epCheckedDepartments = "None";
        }
        new AlertDialog.Builder(this)
                .setTitle("Your post is visible to the departments of:")
                .setMessage(epCheckedDepartments)
                .setPositiveButton("OK", null)
                .setNegativeButton(null, null)
                .show();
    }

    public void showTagColleague(View view) {
        if (epColleagues.isEmpty()) epColleagues = "None";
        new AlertDialog.Builder(this)
                .setTitle("The following colleagues are tagged in your post:")
                .setMessage(epColleagues)
                .setPositiveButton("OK", null)
                .setNegativeButton(null, null)
                .show();
    }
}
