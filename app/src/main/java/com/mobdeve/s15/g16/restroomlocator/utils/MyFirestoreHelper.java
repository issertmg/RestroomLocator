package com.mobdeve.s15.g16.restroomlocator.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryBounds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mobdeve.s15.g16.restroomlocator.AddRestroomActivity;
import com.mobdeve.s15.g16.restroomlocator.ChangePasswordActivity;
import com.mobdeve.s15.g16.restroomlocator.LoginActivity;
import com.mobdeve.s15.g16.restroomlocator.R;
import com.mobdeve.s15.g16.restroomlocator.SignUpActivity;
import com.mobdeve.s15.g16.restroomlocator.UserProfileActivity;
import com.mobdeve.s15.g16.restroomlocator.ViewRestroomsNearbyActivity;
import com.mobdeve.s15.g16.restroomlocator.models.Restroom;
import com.mobdeve.s15.g16.restroomlocator.models.Review;
import com.mobdeve.s15.g16.restroomlocator.models.User;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MyFirestoreHelper {

    public static void createUserAccount(String username, String password, SignUpActivity activity) {
        String email =  username + MyFirestoreReferences.DOMAIN;

        MyFirestoreReferences.getAuthInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign up successful
                            // Create document for User (write to db)
                            FirebaseUser user = MyFirestoreReferences.getAuthInstance().getCurrentUser();
                            MyFirestoreReferences.getUserCollectionReference()
                                    .document(user.getUid())
                                    .set(new User(username))
                                    .addOnSuccessListener(activity, new OnSuccessListener<Void>(){
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            FirebaseAuth.getInstance().signOut();
                                            Toast.makeText(activity, "Account successfully created.",
                                                    Toast.LENGTH_SHORT).show();
                                            activity.finish();
                                        }
                                    })
                                    .addOnFailureListener(activity, new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(activity, "Account creation failed. Please check your internet connection and try again.",
                                                    Toast.LENGTH_SHORT).show();
                                            user.delete();
                                        }
                                    });

                        } else {
                            // If sign up fails, display a message to the user.
                            String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();

                            switch (errorCode) {
                                case "ERROR_EMAIL_ALREADY_IN_USE":
                                    Toast.makeText(activity, "Account creation failed. Username is already taken.",
                                            Toast.LENGTH_SHORT).show();
                                    break;
                                case "ERROR_WEAK_PASSWORD":
                                    Toast.makeText(activity, "Account creation failed. Password is too weak.",
                                            Toast.LENGTH_SHORT).show();
                                    break;
                                case "ERROR_INVALID_EMAIL":
                                    Toast.makeText(activity, "Account creation failed. Email formatting is invalid.",
                                            Toast.LENGTH_SHORT).show();
                                    break;
                                default:
                                    Toast.makeText(activity, "Account creation failed. Please check your internet connection and try again.",
                                            Toast.LENGTH_SHORT).show();
                            }


                        }
                    }
                });
    }

    public static void signInUserAccount(String username, String password, LoginActivity activity) {
        String email =  username + MyFirestoreReferences.DOMAIN;

        MyFirestoreReferences.getAuthInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success
                            Toast.makeText(activity, "Login successful.",
                                    Toast.LENGTH_SHORT).show();
                            activity.startActivity(new Intent(activity, ViewRestroomsNearbyActivity.class));
                            activity.finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(activity, "Authentication failed. Try again later.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    public static void resetAccountPassword(String oldPassword, String newPassword, ChangePasswordActivity activity) {
        FirebaseUser user = MyFirestoreReferences.getAuthInstance().getCurrentUser();

        // Re-authenticate user
        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPassword);
        user.reauthenticate(credential)
                .addOnCompleteListener(activity, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Change password
                            user.updatePassword(newPassword)
                                    .addOnCompleteListener(activity, new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(activity,
                                                        "Password changed successfully.",
                                                        Toast.LENGTH_SHORT)
                                                        .show();
                                                activity.clearFields();

                                            }
                                        }
                                    });
                        }
                        else {
                            Toast.makeText(activity,
                                    "Password changed failed. Old password field may be incorrect.",
                                    Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                });
    }

    public static void createRestroomLocation(Restroom location,
                                              Review review,
                                              Boolean imgOneIsNull,
                                              Boolean imgTwoIsNull,
                                              Boolean imgThreeIsNull,
                                              Uri imageUriOne,
                                              Uri imageUriTwo,
                                              Uri imageUriThree,
                                              AddRestroomActivity activity) {

        String hash = GeoFireUtils.getGeoHashForLocation(new GeoLocation(location.getLatitude(), location.getLongitude()));
        location.setGeohash(hash);

        // Add Location first to db
        MyFirestoreReferences.getRestroomCollectionReference().add(location)
                .addOnCompleteListener(activity, new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {

                            // Set restroomId of review object
                            DocumentReference docRef = task.getResult();
                            review.setRestroomId(docRef.getId());

                            Intent result_intent = new Intent();
                            result_intent.putExtra(IntentKeys.RESTROOM_ID_KEY, docRef.getId());
                            result_intent.putExtra(IntentKeys.NAME_KEY, location.getName());
                            result_intent.putExtra(IntentKeys.LATITUDE_KEY, location.getLatitude());
                            result_intent.putExtra(IntentKeys.LONGITUDE_KEY, location.getLongitude());
                            activity.setResult(Activity.RESULT_OK, result_intent);
                            activity.finish();

                            // Add Review to db
                            createReview(
                                    review,
                                    imgOneIsNull,
                                    imgTwoIsNull,
                                    imgThreeIsNull,
                                    imageUriOne,
                                    imageUriTwo,
                                    imageUriThree,
                                    activity);
                        }
                        else {
                            Toast.makeText(activity,
                                    "Error adding restroom location to map.",
                                    Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                });
    }

    public static void createReview(Review review,
                                    Boolean imgOneIsNull,
                                    Boolean imgTwoIsNull,
                                    Boolean imgThreeIsNull,
                                    Uri imageUriOne,
                                    Uri imageUriTwo,
                                    Uri imageUriThree,
                                    AddRestroomActivity activity) {

        // Add Review to db
        MyFirestoreReferences.getReviewCollectionReference().add(review)
                .addOnSuccessListener(activity, new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        // at least one image was selected, upload images to Storage
                        if(!(imgOneIsNull && imgTwoIsNull && imgThreeIsNull)){
                            // upload the images
                            if(!imgOneIsNull){
                                StorageReference imageRefOne = MyFirestoreReferences.getStorageReferenceInstance()
                                        .child(MyFirestoreReferences.generateNewImagePath(documentReference, imageUriOne));
                                imageRefOne.putFile(imageUriOne)
                                        .addOnSuccessListener(activity, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                Log.w("ADD_REVIEW_ACTIVITY", "imgOne upload successful");
                                            }
                                        });
                            };

                            if(!imgTwoIsNull){
                                StorageReference imageRefTwo = MyFirestoreReferences.getStorageReferenceInstance()
                                        .child(MyFirestoreReferences.generateNewImagePath(documentReference, imageUriTwo));
                                imageRefTwo.putFile(imageUriTwo)
                                        .addOnSuccessListener(activity, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                Log.w("ADD_REVIEW_ACTIVITY", "imgTwo upload successful");
                                            }
                                        });
                            }

                            if(!imgThreeIsNull){
                                StorageReference imageRefThree = MyFirestoreReferences.getStorageReferenceInstance()
                                        .child(MyFirestoreReferences.generateNewImagePath(documentReference, imageUriThree));
                                imageRefThree.putFile(imageUriThree)
                                        .addOnSuccessListener(activity, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                Log.w("ADD_REVIEW_ACTIVITY", "imgThree upload successful");
                                            }
                                        });
                            }
                        }

                    }
                })
                .addOnFailureListener(activity, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(activity,
                                "Error adding review.",
                                Toast.LENGTH_SHORT)
                                .show();
                        Log.w("ADD_REVIEW_ACTIVITY", "Error adding document", e);
                    }
                });

    }

    public static void displayUserDetails(UserProfileActivity activity) {
        String userId = MyFirestoreReferences.getAuthInstance().getCurrentUser().getUid();

        MyFirestoreReferences.getUserCollectionReference().document(userId).get()
                .addOnCompleteListener(activity, new OnCompleteListener<DocumentSnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            User user = task.getResult().toObject(User.class);
                            String username = user.getUsername();
                            Date today = new Date();
                            Date date = user.getDateCreated();

                            LocalDate locToday = today.toInstant().atZone(ZoneId.of("UTC")).toLocalDate();
                            LocalDate locDate = date.toInstant().atZone(ZoneId.of("UTC")).toLocalDate();

                            Period p = Period.between(locDate,locToday);
                            int years = p.getYears();
                            int months = p.getMonths();
                            int days = p.getDays();

                            StringBuilder sb = new StringBuilder();

                            if (years != 0)
                                sb.append(Integer.toString(years) + "y ");
                            if (months != 0)
                                sb.append(Integer.toString(months) + "m ");
                            if (days != 0)
                                sb.append(Integer.toString(days) + "d");

                            if (years == 0 && months == 0 && days == 0) {
                                sb.append(Integer.toString(1) + "d");
                            }

                            activity.setTvUsername(username);
                            activity.setTvAgeValue(sb.toString().trim());
                        }
                    }
                });
    }

    public static void displayNearbyRestroomLocations(GeoPoint p, MapView map, ViewRestroomsNearbyActivity activity) {
        GeoLocation center = new GeoLocation(p.getLatitude(), p.getLongitude());
        double radiusInM = 500;

        // Each item in 'bounds' represents a startAt/endAt pair. We have to issue
        // a separate query for each pair. There can be up to 9 pairs of bounds
        // depending on overlap, but in most cases there are 4.
        List<GeoQueryBounds> bounds = GeoFireUtils.getGeoHashQueryBounds(center, radiusInM);
        final List<Task<QuerySnapshot>> tasks = new ArrayList<>();
        for (GeoQueryBounds b : bounds) {
            Query q = MyFirestoreReferences.getRestroomCollectionReference()
                    .orderBy("geohash")
                    .startAt(b.startHash)
                    .endAt(b.endHash);

            tasks.add(q.get());
        }

        // Collect all the query results together into a single list
        Tasks.whenAllComplete(tasks)
                .addOnCompleteListener(activity, new OnCompleteListener<List<Task<?>>>() {
                    @Override
                    public void onComplete(@NonNull Task<List<Task<?>>> t) {
                        List<Restroom> matchingDocs = new ArrayList<>();

                        for (Task<QuerySnapshot> task : tasks) {
                            QuerySnapshot snap = task.getResult();
                            for (DocumentSnapshot doc : snap.getDocuments()) {
                                Restroom r = doc.toObject(Restroom.class);

                                // We have to filter out a few false positives due to GeoHash
                                // accuracy, but most will match
                                GeoLocation docLocation = new GeoLocation(r.getLatitude(), r.getLongitude());
                                double distanceInM = GeoFireUtils.getDistanceBetween(docLocation, center);
                                if (distanceInM <= radiusInM) {
                                    matchingDocs.add(r);
                                }
                            }
                        }
                        // remove previous restroom pins
                        activity.removePreviousRestroomMarkers();

                        // matchingDocs contains the results
                        for (Restroom r : matchingDocs) {
                            Marker m = new Marker(map);
                            m.setIcon(activity.getResources().getDrawable(R.drawable.ic_restroom_location));
                            m.setPosition(new GeoPoint(r.getLatitude(),r.getLongitude()));
                            m.setVisible(true);
                            m.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                                @Override
                                public boolean onMarkerClick(Marker marker, MapView mapView) {
                                    //Intent i = new Intent(activity, ViewReviewDetailsActivity.class);
                                    //i.putExtra(IntentKeys.RESTROOM_ID_KEY, r.getId());
                                    //activity.startActivity(i);
                                    Toast.makeText(activity,
                                            r.getId(), Toast.LENGTH_LONG).show();
                                    return true;
                                }
                            });
                            activity.addMarkerToList(m);
                            map.getOverlayManager().add(m);
                        }
                        map.invalidate();
                    }
                });
    }

}
