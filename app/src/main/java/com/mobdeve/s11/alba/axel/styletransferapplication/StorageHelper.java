package com.mobdeve.s11.alba.axel.styletransferapplication;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class StorageHelper {
    private StorageReference storageRef;
    private String TAG = "STORAGEHELPER";
    public static StorageHelper instance = null;

    public StorageHelper() {
        this.storageRef = FirebaseStorage.getInstance().getReference();
    }

    public static synchronized StorageHelper getInstance() {
        if (instance == null) {
            instance = new StorageHelper();
        }

        return instance;
    }

    public void putImage(String img_path) {

        Log.d(TAG, img_path);

        Uri file = Uri.fromFile(new File(img_path));
        StorageReference riversRef = this.storageRef.child("images/rivers.jpg");

        riversRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                //
                                Log.d(TAG, uri.toString());
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                        Log.d(TAG, "putFile failed");
                    }
                });
    }
}
