package com.mobdeve.s11.alba.axel.styletransferapplication;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FirestoreHelper {
    private FirebaseFirestore db;
    private String TAG = "FIRESTOREHELPER";
    public static FirestoreHelper instance = null;

    public FirestoreHelper() {
        this.db = FirebaseFirestore.getInstance();
    }

    public static synchronized FirestoreHelper getInstance() {
        if (instance == null) {
            instance = new FirestoreHelper();
        }

        return instance;
    }

    public void addPostDocument(Post post) {
        // Create a new user with a first and last name
        Map<String, Object> document = new HashMap<>();

        // Build document of the post
        document.put("imageURL", post.getImageURI());
        document.put("username", post.getUsername());
        document.put("caption", post.getCaption());
        document.put("datePosted", post.getDatePosted());
        document.put("numLikes", Integer.toString(post.getNumLikes()));
        document.put("liked", post.getLiked());
        document.put("timestamp", post.getTimestamp());

        // Add a new document with a generated ID
        this.db.collection("posts")
                .add(document)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    interface readAllDataCallback {
        void result(ArrayList<Post> data);
    }

//    public void readAllData(readAllDataCallback cb) {
//        ArrayList<Post> data = new ArrayList<>();
//
//        this.db.collection("posts")
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            // Build ArrayList of Post object here
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                Map<String, Object> tempDocument = document.getData();
//                                //Log.d(TAG, document.getId() + " => " + tempDocument.get("username"));
//                                Log.d(TAG, "wa");
//                                data.add(new Post(
//                                        tempDocument.get("imageURL").toString(),
//                                        tempDocument.get("username").toString(),
//                                        tempDocument.get("caption").toString(),
//                                        tempDocument.get("datePosted").toString(),
//                                        Integer.parseInt(tempDocument.get("numLikes").toString()),
//                                        tempDocument.get("liked") == "true"));
//                            }
//
//                            // Callback
//                            cb.result(data);
//
//                        } else {
//                            Log.w(TAG, "Error getting documents.", task.getException());
//                        }
//                    }
//                });
//    }

    public void readAllDataListen (readAllDataCallback cb) {
        this.db.collection("posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Error getting documents.", e);
                            return;
                        } else {
                            Log.w(TAG, "readAllDataListen triggered.", e);
                            ArrayList<Post> data = new ArrayList<>();

                            for (QueryDocumentSnapshot document : value) {
                                //Map<String, Object> tempDocument = document.getData();
                                //Log.d(TAG, document.getId() + " => " + tempDocument.get("username"));
                                //Log.d(TAG, "wa");
                                data.add(new Post(
                                        document.getId(),
                                        document.get("imageURL").toString(),
                                        document.get("username").toString(),
                                        document.get("caption").toString(),
                                        document.get("datePosted").toString(),
                                        Integer.parseInt(document.get("numLikes").toString()),
                                        document.get("liked") == "true",
                                        (Long) document.get("timestamp")
                                ));
                            }

                            // Callback
                            cb.result(data);
                        }
                    }
                });
    }

    public void addLike(String firestoreID) {
        this.db.collection("posts")
                .document(firestoreID)
                .update("numLikes", FieldValue.increment(1))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });

    }

}
