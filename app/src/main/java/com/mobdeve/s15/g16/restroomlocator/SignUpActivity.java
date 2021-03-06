package com.mobdeve.s15.g16.restroomlocator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mobdeve.s15.g16.restroomlocator.utils.MyFirestoreHelper;
import com.mobdeve.s15.g16.restroomlocator.utils.MyFirestoreReferences;

public class SignUpActivity extends AppCompatActivity {

    private EditText etvUsername, etvPassword, etvConfirmPassword;
    private Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().setTitle("Sign Up");

        etvUsername = findViewById(R.id.etvUsername);
        etvPassword = findViewById(R.id.etvPassword);
        etvConfirmPassword = findViewById(R.id.etvConfirmPassword);
        btnSignUp = findViewById(R.id.btnSignUp);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });

    }

    private void signUp() {
        String username = etvUsername.getText().toString();
        String password = etvPassword.getText().toString();
        String confirmPassword = etvConfirmPassword.getText().toString();

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
        else if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Error: Password and confirm password fields do not match.",
                    Toast.LENGTH_SHORT).show();
        }
        else {
            // Create user account
            MyFirestoreHelper.createUserAccount(username, password, this);
        }
    }
}