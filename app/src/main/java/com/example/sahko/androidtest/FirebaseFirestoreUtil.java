package com.example.sahko.androidtest;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.sahko.androidtest.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

/**
 * Created by sahko on 10/10/2017.
 */

public class FirebaseFirestoreUtil {
    private static final String TAG = "FirebaseFirestoreUtil";
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static FirebaseFirestore getDatabase() { return db; }

    public static User getCurrentUser() {
        final FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
        User user = new User(fbUser.getDisplayName(), fbUser.getPhotoUrl().toString(), fbUser.getUid());
        db.collection("users").document(user.getUid()).set(user);
        return user;
    }
}
