package com.mobdeve.s15.g16.restroomlocator;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

        boolean isValidOldPassword = (oldPassword.length() >= 8);
        boolean isValidNewPassword = (newPassword.length() >= 8);


        if (!isValidOldPassword) {
            Toast.makeText(this, "Error: Old password must be at least 8 characters in length.",
                    Toast.LENGTH_SHORT).show();
        }
        else if (!isValidNewPassword) {
            Toast.makeText(this, "Error: New password must be at least 8 characters in length.",
                    Toast.LENGTH_SHORT).show();
        }
        else if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "Error: New password and confirm password fields do not match.",
                    Toast.LENGTH_SHORT).show();
        }
        else {
            //Reset password
            MyFirestoreReferences.resetAccountPassword(oldPassword, newPassword, this);
        }
    }

    public void clearFields() {
        etvOldPassword.setText("");
        etvNewPassword.setText("");
        etvConfirmPassword.setText("");
    }
}