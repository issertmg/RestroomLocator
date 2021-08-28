package com.mobdeve.s15.g16.restroomlocator;

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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MyFirestoreReferences {
    // All instances of Firestore and Storage
    private static FirebaseFirestore firebaseFirestoreInstance = null;
    private static FirebaseAuth firebaseAuthInstance = null;

    private static StorageReference storageReferenceInstance = null;

    private static CollectionReference usersRef = null;
    private static CollectionReference restroomsRef = null;
    private static CollectionReference reviewsRef = null;
    private static CollectionReference commentsRef = null;



    // Collection and document names
    public final static String
        USERS_COLLECTION = "Users",
        RESTROOM_COLLECTION = "Restrooms",
        REVIEWS_COLLECTION = "Reviews",
        COMMENTS_COLLECTION = "Comments",

        USERNAME_FIELD = "username",
        DATECREATED_FIELD = "dateCreated",
        USERID_FIELD = "userId",
        RESTROOMID_FIELD = "restroomId",
        STARTTIME_FIELD = "startTime",
        ENDTIME_FIELD = "endTime",
        FEE_FIELD = "fee",
        IMAGEURI1_FIELD = "imageUri1",
        IMAGEURI2_FIELD = "imageUri2",
        IMAGEURI3_FIELD = "imageUri3",
        REMARKS_FIELD = "remarks",
        REVIEWID_FIELD = "reviewId",
        MESSAGE_FIELD = "message",
        TIMESTAMP_FIELD = "timestamp",
        NAME_FIELD = "name",
        LATITUDE_FIELD = "latitude",
        LONGITUDE_FIELD = "longitude",
        GEOHASH_FIELD = "geohash";

    // Used in account creation
    private static final String DOMAIN = "@restroom.locator.com";


    public static FirebaseFirestore getFirestoreInstance() {
        if(firebaseFirestoreInstance == null) {
            firebaseFirestoreInstance = FirebaseFirestore.getInstance();
        }
        return firebaseFirestoreInstance;
    }

    public static FirebaseAuth getAuthInstance() {
        if(firebaseAuthInstance == null) {
            firebaseAuthInstance = FirebaseAuth.getInstance();
        }
        return firebaseAuthInstance;
    }

//    public static StorageReference getStorageReferenceInstance() {
//        if (storageReferenceInstance == null) {
//            storageReferenceInstance = FirebaseStorage.getInstance().getReference();
//        }
//        return storageReferenceInstance;
//    }

    public static CollectionReference getUserCollectionReference() {
        if(usersRef == null) {
            usersRef = getFirestoreInstance().collection(USERS_COLLECTION);
        }
        return usersRef;
    }

    public static CollectionReference getRestroomCollectionReference() {
        if(restroomsRef == null) {
            restroomsRef = getFirestoreInstance().collection(RESTROOM_COLLECTION);
        }
        return restroomsRef;
    }

    public static CollectionReference getReviewCollectionReference() {
        if(reviewsRef == null) {
            reviewsRef = getFirestoreInstance().collection(REVIEWS_COLLECTION);
        }
        return reviewsRef;
    }

    public static CollectionReference getCommentCollectionReference() {
        if(commentsRef == null) {
            commentsRef = getFirestoreInstance().collection(COMMENTS_COLLECTION);
        }
        return commentsRef;
    }

    public static StorageReference getStorageReferenceInstance() {
        if (storageReferenceInstance == null) {
            storageReferenceInstance = FirebaseStorage.getInstance().getReference();
        }
        return storageReferenceInstance;
    }

    public static String generateNewImagePath(DocumentReference reviewRef, Uri imageUri) {
        return "images/" + reviewRef.getId() + "-" + imageUri.getLastPathSegment();
    }

    public static void createUserAccount(String username, String password, Activity activity) {
        String email =  username + DOMAIN;

        getAuthInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign up successful
                            // Create document for User (write to db)
                            FirebaseUser user = getAuthInstance().getCurrentUser();
                            MyFirestoreReferences.getUserCollectionReference()
                                    .document(user.getUid())
                                    .set(new User(username))
                                    .addOnSuccessListener(new OnSuccessListener<Void>(){
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            FirebaseAuth.getInstance().signOut();
                                            Toast.makeText(activity, "Account successfully created.",
                                                    Toast.LENGTH_SHORT).show();
                                            activity.finish();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
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
        String email =  username + DOMAIN;

        getAuthInstance().signInWithEmailAndPassword(email, password)
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
        getRestroomCollectionReference().add(location)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
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
                            getReviewCollectionReference().add(review)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            // at least one image was selected, upload images to Storage
                                            if(!(imgOneIsNull && imgTwoIsNull && imgThreeIsNull)){
                                                // upload the images
                                                if(!imgOneIsNull){
                                                    StorageReference imageRefOne = MyFirestoreReferences.getStorageReferenceInstance()
                                                            .child(MyFirestoreReferences.generateNewImagePath(documentReference, imageUriOne));
                                                    imageRefOne.putFile(imageUriOne)
                                                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
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
                                                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
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
                                                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                                @Override
                                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                                    Log.w("ADD_REVIEW_ACTIVITY", "imgThree upload successful");
                                                                }
                                                            });
                                                }
                                            }

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
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
        getReviewCollectionReference().add(review)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        // at least one image was selected, upload images to Storage
                        if(!(imgOneIsNull && imgTwoIsNull && imgThreeIsNull)){
                            // upload the images
                            if(!imgOneIsNull){
                                StorageReference imageRefOne = MyFirestoreReferences.getStorageReferenceInstance()
                                        .child(MyFirestoreReferences.generateNewImagePath(documentReference, imageUriOne));
                                imageRefOne.putFile(imageUriOne)
                                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
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
                                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
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
                                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                Log.w("ADD_REVIEW_ACTIVITY", "imgThree upload successful");
                                            }
                                        });
                            }
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
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
        String userId = getAuthInstance().getCurrentUser().getUid();

        getUserCollectionReference().document(userId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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
            Query q = getRestroomCollectionReference()
                    .orderBy("geohash")
                    .startAt(b.startHash)
                    .endAt(b.endHash);

            tasks.add(q.get());
        }

        // Collect all the query results together into a single list
        Tasks.whenAllComplete(tasks)
                .addOnCompleteListener(new OnCompleteListener<List<Task<?>>>() {
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
                        Log.d("overlay count: ", "" + map.getOverlays().size());
                        map.invalidate();
                    }
                });
    }

}
