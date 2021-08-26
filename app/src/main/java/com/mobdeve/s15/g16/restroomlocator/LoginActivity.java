package com.mobdeve.s15.g16.restroomlocator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText etvUsername, etvPassword;
    private Button btnLogin, btnSignUp, btnGuest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etvUsername = findViewById(R.id.etvUsername);
        etvPassword = findViewById(R.id.etvPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignUp = findViewById(R.id.btnSignUp);
        btnGuest = findViewById(R.id.btnGuest);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { startActivity(new Intent(LoginActivity.this, SignUpActivity.class)); }
        });

        btnGuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { startActivity(new Intent(LoginActivity.this, ViewRestroomsNearbyActivity.class)); }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        // Check if user is signed-in
        FirebaseUser user = MyFirestoreReferences.getAuthInstance().getCurrentUser();

        if (user != null) {
            startActivity(new Intent(LoginActivity.this, ViewRestroomsNearbyActivity.class));
            finish();
        }

    }

    private void login() {
        String username = etvUsername.getText().toString();
        String password = etvPassword.getText().toString();

        boolean isValidUsername = (username.length() >= 3 && username.length() <= 20);
        boolean isValidPassword = (password.length() >= 8);

        if (!isValidUsername) {
            Toast.makeText(this, "Error: Username should be 3 to 20 characters in length.",
                    Toast.LENGTH_SHORT).show();
        }
        else if (!isValidPassword) {
            Toast.makeText(this, "Error: Password must be at least 8 characters in length.",
                    Toast.LENGTH_SHORT).show();
        }
        else {
            // Sign in user
            MyFirestoreReferences.signInUserAccount(username, password, this);
        }
    }
}