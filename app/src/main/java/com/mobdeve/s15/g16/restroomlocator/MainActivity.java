package com.mobdeve.s15.g16.restroomlocator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DocumentReference docRef = FirebaseFirestore.getInstance()
                .collection(MyFirestoreReferences.USERS_COLLECTION)
                .document("UEcwY6fcawPYbtU6zbrYl2DvQ7r1");
        docRef.get().addOnCompleteListener(this, new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot ref = task.getResult();
                    Log.d("ID", ref.getId());
                    Log.d("USERNAME", ref.get(MyFirestoreReferences.USERNAME_FIELD).toString());
                } else {

                }
            }
        });


        //FOR TESTING:
        Intent i = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(i);
        finish();

    }
}