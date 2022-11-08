package com.example.mypersonal_portfolio;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Sign_up extends AppCompatActivity {



    public static final  String TAG = "TAG";
    EditText mFullName, mEmail,mPassword,mPhone;
    Button mRegisterBtn,mLoginBtn;
    ProgressBar progressBar;

    FirebaseFirestore fstore;
    String userID;
    FirebaseAuth fAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

    mFullName=findViewById(R.id.fullname);
    mEmail=findViewById(R.id.email);
    mPassword=findViewById(R.id.password);
    mPhone=findViewById(R.id.phone);
    mRegisterBtn=findViewById(R.id.registerBtn);
    mLoginBtn=findViewById(R.id.createText);
    progressBar=findViewById(R.id.progressBar);

    fAuth=FirebaseAuth.getInstance();
    fstore=FirebaseFirestore.getInstance();


    if (fAuth.getCurrentUser()!=null){
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        finish();
    }


    mLoginBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            startActivity(new Intent(getApplicationContext(),Login.class));
        }
    });

    mRegisterBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final String email = mEmail.getText().toString().trim();
            String password = mPassword.getText().toString().trim();
            final String fullName = mFullName.getText().toString().trim();
            final String phone = mPhone.getText().toString().trim();

            if (TextUtils.isEmpty(email)){
                mEmail.setError("Email is Required!");
                return;
            }
            if (TextUtils.isEmpty(password)){
                mPassword.setError("Password is required!");
                return;
            }
            if (password.length()<8) {
                mPassword.setError("Password must be greater than 8 character");
                return;
            }
            progressBar.setVisibility(View.VISIBLE);

            fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        FirebaseUser fuser= fAuth.getCurrentUser();
                        fuser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(getApplicationContext(),"Register Successful!", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG,"Onfaliure: Email Not Sent" + e.getMessage());
                            }
                        });

                        Toast.makeText(getApplicationContext(),"User Created",Toast.LENGTH_SHORT).show();
                        userID = fAuth.getCurrentUser().getUid();
                        DocumentReference documentReference = fstore.collection("user").document(userID);
                        Map<String,Object> user = new HashMap<>();
                        user.put("fName",fullName);
                        user.put("email",email);
                        user.put("phone",phone);
                        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d(TAG,"onsucces: user profile is created for"+ userID);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG,"onfaliure:"+e.toString());
                            }
                        });

                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    }
                    else {
                        Toast.makeText(Sign_up.this, "Error" + task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                }
            });

        }
    });


    }
}