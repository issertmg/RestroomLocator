package com.mobdeve.s15.g16.restroomlocator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class SignUpActivity extends AppCompatActivity {

    private EditText etvUsername, etvPassword;
    private Button btnSignUp;
    private FirebaseAuth mAuth;

    public static String DOMAIN = "@restroom.locator.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        etvUsername = findViewById(R.id.etvUsername);
        etvPassword = findViewById(R.id.etvPassword);
        btnSignUp = findViewById(R.id.btnSignUp);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();


    }

    private void signUp() {
        // TODO check username & password input constraints (length)
        String username = etvUsername.getText().toString();
        String password = etvPassword.getText().toString();

        String email =  username + DOMAIN;

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign up successful
                            // Create document for User (write to db)
                            FirebaseUser user = mAuth.getCurrentUser();
                            MyFirestoreReferences.getUserCollectionReference()
                                    .document(user.getUid())
                                    .set(new User(username))
                                    .addOnSuccessListener(new OnSuccessListener<Void>(){
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            FirebaseAuth.getInstance().signOut();
                                            Toast.makeText(SignUpActivity.this, "Account successfully created.",
                                                    Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                        // TODO Show message if failed
                                        }
                                    });

                        } else {
                            // If sign up fails, display a message to the user.
                            Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

}