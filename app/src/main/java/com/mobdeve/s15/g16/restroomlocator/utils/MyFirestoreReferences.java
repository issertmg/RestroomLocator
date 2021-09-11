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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mobdeve.s15.g16.restroomlocator.AddRestroomActivity;
import com.mobdeve.s15.g16.restroomlocator.ChangePasswordActivity;
import com.mobdeve.s15.g16.restroomlocator.LoginActivity;
import com.mobdeve.s15.g16.restroomlocator.R;
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

public class MyFirestoreReferences {
    // All instances of Firestore and Storage
    private static FirebaseFirestore firebaseFirestoreInstance = null;
    private static FirebaseAuth firebaseAuthInstance = null;

    private static StorageReference storageReferenceInstance = null;

    private static CollectionReference usersRef = null;
    private static CollectionReference restroomsRef = null;
    private static CollectionReference reviewsRef = null;
    private static CollectionReference commentsRef = null;

    public final static String NOIMAGE = "NOIMAGE";

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
    public static final String DOMAIN = "@restroom.locator.com";


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
}
