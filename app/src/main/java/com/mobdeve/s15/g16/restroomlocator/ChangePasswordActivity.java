package com.mobdeve.s15.g16.restroomlocator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText etvOldPassword, etvNewPassword, etvConfirmPassword;
    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        etvOldPassword = findViewById(R.id.etvOldPassword);
        etvNewPassword = findViewById(R.id.etvNewPassword);
        etvConfirmPassword = findViewById(R.id.etvConfirmPassword);

        btnSubmit = findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { changePassword(); }
        });
    }

    private void changePassword() {
        String oldPassword = etvOldPassword.getText().toString();
        String newPassword = etvNewPassword.getText().toString();
        String confirmPassword = etvConfirmPassword.getText().toString();

        // TODO: check if fields are empty and satisfies constraints


        if (newPassword.equals(confirmPassword)) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            // Re-authenticate user
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPassword);
            user.reauthenticate(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // Change password
                                user.updatePassword(newPassword)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(ChangePasswordActivity.this,
                                                            "Password changed successfully.",
                                                            Toast.LENGTH_SHORT)
                                                            .show();
                                                    etvOldPassword.setText("");
                                                    etvNewPassword.setText("");
                                                    etvConfirmPassword.setText("");
                                                }
                                            }
                                        });

                            }
                            else {
                                Toast.makeText(ChangePasswordActivity.this,
                                        "Password changed failed. Old password field may be incorrect.",
                                        Toast.LENGTH_SHORT)
                                        .show();
                            }
                        }
                    });
        }
        else {
            Toast.makeText(ChangePasswordActivity.this,
                    "New password and confirm password fields do not match.",
                    Toast.LENGTH_SHORT)
                    .show();
        }
    }
}