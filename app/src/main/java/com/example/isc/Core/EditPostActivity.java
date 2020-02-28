package com.example.isc.Core;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.isc.Core.Fragments.ProfileFragment;
import com.example.isc.R;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Objects;

public class EditPostActivity extends AppCompatActivity {

    EditText epEditText;
    ImageView epImage;
    Button editPostButton;
    ImageButton epShowPostLevelImageButton;
    LinearLayout epPhotoLL, epIncludeEventLL, epTagColleagueLL,
            epSpecifyDepartmentLL;
    TextView epPhotoLLTextView, epEventsTextView;

    static String epCheckedDepartments="none", epEvents="", epColleagues="none";

    public static final int PICK_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(
                Html.fromHtml("<font color=\"#1976D2\"> Edit post </font>")
        );
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_24dp);

        Intent intent = getIntent();

        epEventsTextView = findViewById(R.id.epEventsTextView);
        if(intent.getStringExtra("event") != null){
            epEventsTextView.setText(epEvents);
        }
        editPostButton = findViewById(R.id.editPostButton);
        editPostButton.setTextColor(Color.GRAY);
        epImage = findViewById(R.id.epImage);
        epShowPostLevelImageButton = findViewById(R.id.epShowPostLevelImageButton);
        epEditText = findViewById(R.id.epEditText);
        epEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() == 0 && epImage.getDrawable()==null){
                    editPostButton.setTextColor(Color.GRAY);
                }else{
                    editPostButton.setTextColor(Color.BLACK);
                }
            }
        });
        epPhotoLL = findViewById(R.id.epPhotoLL);
        epPhotoLLTextView = findViewById(R.id.photoLLTextView);
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

        if(intent.getStringExtra("position") != null){
            initializeUI(Integer.valueOf(Objects.requireNonNull(intent.getStringExtra("position"))));
        }
    }
    public void initializeUI(int position){
        MyPost myPost = ProfileFragment.postArrayList.get(position);
        epEditText.setText(myPost.getPostedText());
        epImage.setImageBitmap(myPost.getPostedImageBitmap());
        epColleagues = myPost.getMyPostTagColleague();
        epCheckedDepartments = myPost.getMyPostLevel();
        epEventsTextView.setText(myPost.getMyPostEvents());
    }

    public void post(View view){
        if(epEditText.getText().toString().length()>0 || epImage.getDrawable()!=null){
            Toast.makeText(getApplicationContext(), "Posted", Toast.LENGTH_LONG).show();
        }
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
    public boolean onSupportNavigateUp(){
        onBackPressed();
        return true;
    }

    public void selectImage(){
        if(epPhotoLLTextView.getText().equals("Photo")){
            Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
            getIntent.setType("image/*");

            Intent pickIntent = new Intent(Intent.ACTION_PICK);
            pickIntent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");

            Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

            startActivityForResult(chooserIntent, PICK_IMAGE);
        }else{
            epImage.setImageDrawable(null);
            epPhotoLLTextView.setText("Photo");
            if(epEditText.getText().toString().trim().length()==0){
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
                return;
            }
            InputStream inputStream = null;
            try {
                inputStream = getApplicationContext().getContentResolver().openInputStream(Objects.requireNonNull(data.getData()));
            } catch (FileNotFoundException e) {
                Toast.makeText(getApplicationContext(), "File not found exception", Toast.LENGTH_SHORT).show();
            }
            if(inputStream!=null){
                epImage.setImageBitmap(BitmapFactory.decodeStream(inputStream));
                epPhotoLLTextView.setText("Remove photo");
                editPostButton.setTextColor(Color.BLACK);
            }else{
                Toast.makeText(getApplicationContext(), "inputStream is null", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void includeEvent(){
        Intent intent = new Intent(getApplicationContext(), IncludeEventActivity.class);
        intent.putExtra("from", "edit");
        startActivity(intent);
    }

    public void tagColleague(){
        Intent intent = new Intent(getApplicationContext(), TagColleagueActivity.class);
        intent.putExtra("from", "edit");
        startActivity(intent);
    }

    public void specifyDepartment(){
        Intent intent = new Intent(getApplicationContext(), PostLevelActivity.class);
        intent.putExtra("from", "edit");
        startActivity(intent);
    }

    public void showPostLevel(View view){
        if (epCheckedDepartments.isEmpty()) {
            epCheckedDepartments = "None";
        }
        new AlertDialog.Builder(this)
                .setTitle("Your post is visible to the departments of:")
                .setMessage(epCheckedDepartments)
                .setPositiveButton("OK", null)
                .setNegativeButton(null, null)
                .show();
    }

    public void showTagColleague(View view){
        if(epColleagues.isEmpty()) epColleagues = "None";
        new AlertDialog.Builder(this)
                .setTitle("The following colleagues are tagged in your post:")
                .setMessage(epColleagues)
                .setPositiveButton("OK", null)
                .setNegativeButton(null, null)
                .show();
    }
}
