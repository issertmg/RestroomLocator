package com.mobdeve.s15.g16.restroomlocator.adapters;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.mobdeve.s15.g16.restroomlocator.utils.MyFirestoreHelper;
import com.mobdeve.s15.g16.restroomlocator.viewholders.MyDetailsViewHolder;
import com.mobdeve.s15.g16.restroomlocator.R;
import com.mobdeve.s15.g16.restroomlocator.models.Comment;
import com.mobdeve.s15.g16.restroomlocator.models.User;
import com.mobdeve.s15.g16.restroomlocator.utils.MyFirestoreReferences;

import org.jetbrains.annotations.NotNull;

public class MyDetailsAdapter extends FirestoreRecyclerAdapter<Comment, MyDetailsViewHolder> {

    private final String[] commentActions = {"Edit", "Delete"};

    public MyDetailsAdapter(FirestoreRecyclerOptions<Comment> options) {
        super(options);
    }

    @Override
    public MyDetailsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_layout, parent, false);
        MyDetailsViewHolder myDetailsViewHolder = new MyDetailsViewHolder(view);
        return myDetailsViewHolder;
    }

    @Override
    protected void onBindViewHolder(MyDetailsViewHolder holder, int position, Comment c) {
        holder.bindData(c);
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                if (MyFirestoreHelper.isGuestUser() || !MyFirestoreHelper.isCurrentUserID(c.getUserId())) { return false; }

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setItems(commentActions, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Edit
                        if (which == 0) {
                            AlertDialog.Builder editBuilder = new AlertDialog.Builder(v.getContext());
                            final EditText etv = new EditText(v.getContext());
                            etv.setText(c.getMessage());

                            editBuilder.setTitle("Edit comment");
                            editBuilder.setView(etv);
                            editBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    MyFirestoreReferences
                                            .getCommentCollectionReference()
                                            .document(c.getId())
                                            .update(MyFirestoreReferences.MESSAGE_FIELD, etv.getText().toString())
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull @NotNull Task<Void> task) {
                                                    if (task.isSuccessful())
                                                        Toast.makeText(
                                                                v.getContext(),
                                                                "Comment updated successfully.",
                                                                Toast.LENGTH_SHORT
                                                        ).show();
                                                    else
                                                        Toast.makeText(
                                                                v.getContext(),
                                                                "Comment update failed.",
                                                                Toast.LENGTH_SHORT
                                                        ).show();
                                                }
                                            });
                                }
                            });
                            editBuilder.setNegativeButton("Cancel", null);
                            editBuilder.create().show();
                        }
                        // Delete
                        else {
                            AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(v.getContext());
                            deleteBuilder.setTitle("Delete comment");
                            deleteBuilder.setMessage("Are you sure you want to delete this comment?");
                            deleteBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    MyFirestoreReferences
                                            .getCommentCollectionReference()
                                            .document(c.getId())
                                            .delete()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull @NotNull Task<Void> task) {
                                                    if (task.isSuccessful())
                                                        Toast.makeText(
                                                                v.getContext(),
                                                                "Comment deleted successfully.",
                                                                Toast.LENGTH_SHORT
                                                        ).show();
                                                    else
                                                        Toast.makeText(
                                                                v.getContext(),
                                                                "Comment deletion failed.",
                                                                Toast.LENGTH_SHORT
                                                        ).show();
                                                }
                                            });
                                }
                            });
                            deleteBuilder.setNegativeButton("Cancel", null);
                            deleteBuilder.create().show();
                        }
                    }
                });
                builder.create().show();
                return false;
            }
        });
    }
}
