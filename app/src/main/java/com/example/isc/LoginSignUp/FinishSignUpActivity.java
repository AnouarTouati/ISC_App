package com.example.isc.LoginSignUp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.isc.Core.CoreActivity;
import com.example.isc.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FinishSignUpActivity extends AppCompatActivity {

      private  ImageView signUpProfileImage, addSignUpProfileImageIV;
      private  Bitmap signUpProfileImageBitmap=null;
      private  EditText signUpFullName,studentRegistrationNumberEV;
      private  Spinner departmentSpinner;
      private  RadioGroup positionRadio;

      private   ArrayList<String> departments;
      private static final int PICK_IMAGE = 1;

      private FirebaseAuth firebaseAuth;
      private FirebaseUser firebaseUser;
      private FirebaseStorage firebaseStorage;
      private   FirebaseFirestore firebaseFirestore;

      private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish_sign_up);
 try{
       signUpProfileImage = findViewById(R.id.signUpProfileImage);
       addSignUpProfileImageIV = findViewById(R.id.addSignUpProfileImageIV);

        progressDialog=new ProgressDialog(this);
        Objects.requireNonNull(getSupportActionBar()).hide();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseStorage=FirebaseStorage.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();

        signUpFullName = findViewById(R.id.signUpFullName);
        studentRegistrationNumberEV=findViewById(R.id.studentNumberFinishSignUp);
        departmentSpinner = findViewById(R.id.departmentSpinner);
        positionRadio = findViewById(R.id.position);

        departments = new ArrayList<>();
        departments.add("Media");
        departments.add("HR");
        departments.add("ER");
        departments.add("Tech");
        departments.add("Communication");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, departments);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        departmentSpinner.setAdapter(dataAdapter);

        signUpProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
                addSignUpProfileImageIV.setVisibility(View.GONE);
            }
        });
 } catch (Exception e){
     Log.v("AppLogic","Something went wrong "
             +"the cause: " +e.getCause()
             + "the message: "+e.getMessage()
     );
 }
    }

    public void signUpUser(View view){
        final String name = signUpFullName.getText().toString();
        final String department = departments.get(departmentSpinner.getSelectedItemPosition());
        final int positionSelected = (positionRadio.getCheckedRadioButtonId())-1;
        final String studentRegistrationNumber=studentRegistrationNumberEV.getText().toString();
        if(!isValidName(name)){
            Toast.makeText(this,"Invalid Full Name",Toast.LENGTH_LONG).show();
            return;
        }

        if(department == null){
            Toast.makeText(this,"Please select a department",Toast.LENGTH_LONG).show();
            return;
        }


        if(positionSelected != 0 && positionSelected != 1){
            Toast.makeText(this,"Please select a position "+positionSelected,Toast.LENGTH_LONG).show();
            return;
        }

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();
        progressDialog.setMessage("Completing Sign up ...");
        progressDialog.show();
        firebaseUser.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            addFullProfileToDataBase(name,positionSelected,department,firebaseUser.getEmail(),studentRegistrationNumber);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {


                Toast.makeText(getApplicationContext(),"Something went wrong and we couldn't sign you up",Toast.LENGTH_LONG).show();

                try {
                    throw e;
                }
                catch (Exception ee){
                    Log.v("ConnectivityFireBase",e.getMessage() +" the  cause is "+ee.getCause() );
                }
            }
        });

    }
  private void pushPostImageToServer(final String imageReferenceInStorage, final Bitmap imageToPush) {
        progressDialog.show();
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
                    Toast.makeText(getApplicationContext(), "Done Signing  up", Toast.LENGTH_LONG).show();
                    Log.v("ConnectivityFireBase", "Done Uploading Profile image");
                    startActivity(new Intent(getApplicationContext(), CoreActivity.class));
                } else {

                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Something went wrong we couldn't sign you up", Toast.LENGTH_LONG).show();
                    Log.v("ConnectivityFireBase", "Something went wrong we couldn't Uploading Profile image" + "onComplete callback Push Image" + task.getException().getMessage());
                    somethingWentWrongPleaseTryAgainImageProblem(imageReferenceInStorage,imageToPush);

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                try {
                    throw e;
                }
                catch (Exception ee){
                    Log.v("ConnectivityFireBase", "Something went wrong we couldn't Uploading Profile image" + "onComplete callback Push Image" + ee.getMessage());
                }
            }
        });

    }
    private void somethingWentWrongPleaseTryAgainImageProblem(final String imageReferenceInStorage, final Bitmap imageToPush){

     AlertDialog.Builder alertDialogBuilder=  new AlertDialog.Builder(this).setTitle("Failed To Complete Sign UP")
               .setMessage("Something went wrong and we couldn't sign you up, let's give it another go shall we")
               .setPositiveButton("Retry", new DialogInterface.OnClickListener(){
                   public void onClick( DialogInterface dialog,int id){
                       pushPostImageToServer(imageReferenceInStorage,imageToPush);
           }
       }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialog, int which) {
               deleteProfileAndGoBackToSignUpActivity();
           }
       });
      alertDialogBuilder.create().show();
   }
    private void deleteProfileAndGoBackToSignUpActivity(){
       firebaseFirestore.collection("Profiles").document(firebaseUser.getUid()).delete();
       firebaseUser.delete();
       firebaseAuth.signOut();
       startActivity(new Intent(getApplicationContext(),SignUpActivity.class));
       finish();
   }
    private  void addFullProfileToDataBase(String name,int position,String department,String email,String studentRegistrationNumber){
        firebaseFirestore=FirebaseFirestore.getInstance();
        final Map<String,Object> map =new HashMap<>();
        map.put("name",name);
        map.put("position",position);
        map.put("email",email);
        map.put("studentRegistrationNumber",studentRegistrationNumber);
        map.put("department",department);
        if(signUpProfileImageBitmap==null){
            map.put("profileImageReferenceInStorage","");
        }else{
            map.put("profileImageReferenceInStorage","images/" + firebaseUser.getUid() + "/" + "ProfileImage" + ".JPEG");
        }

        firebaseFirestore.collection("Profiles").document(firebaseUser.getUid()).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){

                    if(signUpProfileImageBitmap==null){
                        progressDialog.dismiss();
                        startActivity(new Intent(getApplicationContext(), CoreActivity.class));
                    }else{
                        pushPostImageToServer(map.get("profileImageReferenceInStorage").toString(),signUpProfileImageBitmap);
                    }


                }else{
                    Toast.makeText(getApplicationContext(), "Something went wrong we couldn't Finish SignUp", Toast.LENGTH_LONG).show();
                    Log.v("ConnectivityFireBase", "Something went wrong we couldn't Finish SignUp" + "onComplete callback addFullProfileToDataBase" + task.getException().getMessage());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Something went wrong we couldn't Finish SignUp", Toast.LENGTH_LONG).show();

                try {
                    throw e;
                }
                catch(Exception ee){
                    Log.v("ConnectivityFireBase", "Something went wrong we couldn't Finish SignUp" + "onComplete callback addFullProfileToDataBase" + ee.getMessage());
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
       deleteProfileAndGoBackToSignUpActivity();
    }

    public void toLogin(View view){
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
    }

    public void toTermsAndConditions(View view){

    }

    private static boolean isValidName(String str) {
        if(str.length()<3){
            return false;
        }
        String expression = "^[a-zA-Z\\s]+";
        return str.matches(expression);
    }

    private void selectImage(){
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK);
        pickIntent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

        startActivityForResult(chooserIntent, PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                Toast.makeText(getApplicationContext(), "ERROR", Toast.LENGTH_SHORT).show();
                signUpProfileImageBitmap=null;
                signUpProfileImage.setImageBitmap(null);
                return;
            }
            InputStream inputStream = null;
            try {
                inputStream = getApplicationContext().getContentResolver().openInputStream(Objects.requireNonNull(data.getData()));
            } catch (FileNotFoundException e) {
                Toast.makeText(getApplicationContext(), "File not found exception", Toast.LENGTH_SHORT).show();
                signUpProfileImageBitmap=null;
                signUpProfileImage.setImageBitmap(null);
            }
            if(inputStream!=null){
                signUpProfileImageBitmap= BitmapFactory.decodeStream(inputStream);
                signUpProfileImage.setImageBitmap(signUpProfileImageBitmap);
            }else{
                Toast.makeText(getApplicationContext(), "inputStream is null", Toast.LENGTH_SHORT).show();
                signUpProfileImageBitmap=null;
                signUpProfileImage.setImageBitmap(null);
            }
        }
    }
}
