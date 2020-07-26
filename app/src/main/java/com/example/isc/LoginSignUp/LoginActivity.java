package com.example.isc.LoginSignUp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.isc.Core.CoreActivity;
import com.example.isc.Entry.EntryActivity;
import com.example.isc.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    private EditText loginEmail, loginPassword;
    private Button loginButton;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
   private final int  PLAY_SERVICES_RESOLUTION_REQUEST=400;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    try{
        Objects.requireNonNull(getSupportActionBar()).hide();

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() != null){
            finish();
            startActivity(new Intent(getApplicationContext(), CoreActivity.class));
        }

        progressDialog = new ProgressDialog(this);
        loginEmail = findViewById(R.id.loginEmail);
        loginPassword = findViewById(R.id.loginPassword);
        loginButton = findViewById(R.id.loginButton);
        loginPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    loginUser(loginButton);
                }
                return false;
            }
        });
        checkGooglePlayServices();
    }  catch (Exception e){
        Log.v("AppLogic","Something went wrong "
                +"the cause: " +e.getCause()
                + "the message: "+e.getMessage()
        );
    }
    }

    private void  checkGooglePlayServices(){

        GoogleApiAvailability apiAvailability=GoogleApiAvailability.getInstance();
        if(!(apiAvailability.isGooglePlayServicesAvailable(this)== ConnectionResult.SUCCESS)){
         apiAvailability.getErrorDialog(this,apiAvailability.isGooglePlayServicesAvailable(this),PLAY_SERVICES_RESOLUTION_REQUEST).show();
        }
        else{
            Toast.makeText(this,"Play Services Up To Date",Toast.LENGTH_LONG).show();
        }
    }
    public void loginUser(View view){
        String email = loginEmail.getText().toString();
        String password  = loginPassword.getText().toString();


        if(!isEmailValid(email)){
            Toast.makeText(this,"Invalid Email",Toast.LENGTH_LONG).show();
            return;
        }

        if(password.isEmpty()){
            Toast.makeText(this,"Please enter password",Toast.LENGTH_LONG).show();
            return;
        }

       progressDialog.setMessage("Logging in...");
       progressDialog.show();

       firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
           @Override
           public void onComplete(@NonNull Task<AuthResult> task) {

               if(task.isSuccessful()){
                   startActivity(new Intent(getApplicationContext(),CoreActivity.class));
                   Log.v("ConnectivityFireBase","Successfully logged in");
               }

           }
       }).addOnFailureListener(new OnFailureListener() {
           @Override
           public void onFailure(@NonNull Exception e) {
               try{
                   throw Objects.requireNonNull(e);

               }

               catch (FirebaseAuthException ee){

                   switch (ee.getErrorCode()){

                       case  "ERROR_USER_DISABLED"  : Toast.makeText(getApplicationContext(),"Your account has been disabled",Toast.LENGTH_LONG).show(); break;
                       case  "ERROR_USER_NOT_FOUND" : Toast.makeText(getApplicationContext(),"This account doesn't exist",Toast.LENGTH_LONG).show();break;
                       case  "ERROR_WRONG_PASSWORD" : Toast.makeText(getApplicationContext(),"Invalid Password",Toast.LENGTH_LONG).show();break;
                       default: Toast.makeText(getApplicationContext(),"Something went wrong and we couldn't sign you in",Toast.LENGTH_LONG).show();
                           Log.v("ConnectivityFireBase",e.getMessage() +" the  cause is "+e.getCause() );break;

                   }
               }
               catch (Exception ee) {

                   Log.v("ConnectivityFireBase",ee.getMessage() +" the  cause is "+ee.getCause() );
                   Toast.makeText(getApplicationContext(),"Something went wrong and we couldn't sign you in",Toast.LENGTH_LONG).show();
               }

               progressDialog.dismiss();
           }
       });



    }

    public void toMain(View view){
        Intent intent = new Intent(getApplicationContext(), EntryActivity.class);
        intent.putExtra("interior", "true");
        startActivity(intent);
    }
    public void toSignUp(View view){
        startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
    }
    public void toTermsAndConditions(View view){

    }
    private static boolean isEmailValid(String email) {

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), EntryActivity.class);
        intent.putExtra("interior", "true");
        startActivity(intent);
    }
}
