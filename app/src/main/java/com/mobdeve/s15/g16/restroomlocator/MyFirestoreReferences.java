package com.mobdeve.s15.g16.restroomlocator;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MyFirestoreReferences {
    // All instances of Firestore and Storage
    private static FirebaseFirestore firebaseFirestoreInstance = null;
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
        COMMENTS_COLLECTION = "Comments";

    public static FirebaseFirestore getFirestoreInstance() {
        if(firebaseFirestoreInstance == null) {
            firebaseFirestoreInstance = FirebaseFirestore.getInstance();
        }
        return firebaseFirestoreInstance;
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

}
