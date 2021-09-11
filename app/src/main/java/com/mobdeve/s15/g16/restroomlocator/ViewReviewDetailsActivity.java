package com.mobdeve.s15.g16.restroomlocator;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.mobdeve.s15.g16.restroomlocator.adapters.MyDetailsAdapter;
import com.mobdeve.s15.g16.restroomlocator.models.Comment;
import com.mobdeve.s15.g16.restroomlocator.utils.MyFirestoreReferences;

import java.util.HashMap;
import java.util.Map;

public class ViewReviewDetailsActivity extends AppCompatActivity {

    private static final String TAG = "ViewRevDetailsActivity";

    // Views needed
    private TextView tvDetailsUsername, TvDetailsNumReview, tvDetailsName,
            tvDetailsRemarksInfo, tvDetailsPaymentInfo, tvDetailsHoursInfo; //FIXME:
    private EditText etComment;
    private ImageButton ibAddComment;

    // RecyclerView Components
    private RecyclerView recyclerView;

    // Replacement of the base adapter view
    private MyDetailsAdapter myDetailsAdapter;

    // DB reference
    private FirebaseFirestore dbRef;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_review_details);

        // Get comments from the collection
        this.dbRef = FirebaseFirestore.getInstance();
        Query query = dbRef
                .collection(MyFirestoreReferences.COMMENTS_COLLECTION)
                .orderBy(MyFirestoreReferences.TIMESTAMP_FIELD);

        // RECYCLER VIEW
        // Define options
        FirestoreRecyclerOptions<Comment> options = new FirestoreRecyclerOptions.Builder<Comment>()
                .setQuery(query, Comment.class)
                .build();

        // Define adapter
        this.myDetailsAdapter = new MyDetailsAdapter(options);

        // Layout
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        this.recyclerView.setLayoutManager(linearLayoutManager);

        this.ibAddComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comment = etComment.getText().toString();

                // Ready the values of the message
                Map<String, Object> data = new HashMap<>();
                data.put(MyFirestoreReferences.MESSAGE_FIELD, comment);
                data.put(MyFirestoreReferences.TIMESTAMP_FIELD, FieldValue.serverTimestamp());

                // Send the data off to the Comment collection
                dbRef.collection(MyFirestoreReferences.COMMENTS_COLLECTION).add(data)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                                // "Reset" the comment in the EditText
                                etComment.setText("");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(Exception e) {
                                Log.w(TAG, "Error adding document", e);
                            }
                        });
            }
        });
    }

    // Inflate options menu located on ActionBar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.review_details_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit_review:
                // TODO action when edit is clicked
                return true;
            case R.id.action_delete_review:
                // TODO action when delete is clicked
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
