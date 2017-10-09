package com.example.sahko.androidtest;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "androidTest";
    public static final String TAG = "MainActivity";
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        if (FirebaseUtil.getCurrentUserId() != null) {
            startActivity(new Intent(this, ProfileActivity.class));
        }
    }

    public void anonymousSignIn(View view) {
        auth.signInAnonymously().addOnSuccessListener(new OnSuccessListener<AuthResult>() {
        @Override
        public void onSuccess(AuthResult authResult) {
            Intent feedsIntent = new Intent(MainActivity.this, FeedsActivity.class);
            startActivity(feedsIntent);
            }
        }).addOnFailureListener( new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Unable to sign in anonymously.",
                        Toast.LENGTH_SHORT).show();
                Log.e(TAG, e.getMessage());
            }
        });
    }

    public void signIn(View view) {
        Intent signinIntent = new Intent(this, ProfileActivity.class);
        startActivity(signinIntent);
    }
}
