package com.example.sahko.androidtest;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends BaseActivity implements
        View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "ProfileActivity";
    private ViewGroup profileUi;
    private ViewGroup signInUi;
    private FirebaseAuth auth;
    private CircleImageView profilePhoto;
    private TextView profileUsername;
    private GoogleApiClient googleApiClient;

    private static final int RC_SIGN_IN = 103;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        auth = FirebaseAuth.getInstance();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,
                        new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestEmail()
                            .requestIdToken(getString(R.string.default_web_client_id))
                            .build())
                .build();

        signInUi = findViewById(R.id.sign_in_ui);
        profileUi = findViewById(R.id.profile);

        profilePhoto = findViewById(R.id.profile_user_photo);
        profileUsername = findViewById(R.id.profile_user_name);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch(id) {
            case R.id.launch_sign_in:
                launchSignInIntent();
                break;
            case R.id.sign_out_button:
                auth.signOut();
                Auth.GoogleSignInApi.signOut(googleApiClient);
                showSignedOutUI();
                break;
            case R.idk.show_feeds_button:
                Intent feedsIntent = new Intent(this, FeedsActivity.class);
                startActivity(feedsIntent);
                break;
        }
    }

    private void launchSignInIntent() {
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleGoogleSignInResult(result);
        }
    }

    private void handleGoogleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            firebaseAuthWithGoogle(acct);
        } else {
            // Logging would go here
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        showProgressDialog(getString(R.string.profile_progress_message));
        auth.signInWithCredential(credential)
                .addOnSuccessListener(this, new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult result) {
                        handleFirebaseAuthResult(result);
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        FirebaseCrash.logcat(Log.ERROR, TAG, "auth:onFailure:" + e.getMessage());
                        handleFirebaseAuthResult(null);
                    }
                });
    }


    private void handleFirebaseAuthResult(AuthResult result) {
        dismissProgressDialog();
        if (result != null) {
            Log.d(TAG, "handleFirebaseAuthResult:SUCCESS");
            showSignedInUI(result.getUser());
        } else {
            Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show();
            showSignedOutUI();
        }
    }

    private void showSignedInUI(FirebaseUser firebaseUser) {
        Log.d(TAG, "Showing signed in UI");
        signInUi.setVisibility(View.GONE);
        profileUi.setVisibility(View.VISIBLE);
        profileUsername.setVisibility(View.VISIBLE);
        profilePhoto.setVisibility(View.VISIBLE);
        if (firebaseUser.getDisplayName() != null) {
            profileUsername.setText(firebaseUser.getDisplayName());
        }

        if (firebaseUser.getPhotoUrl() != null) {
            GlideUtil.loadProfileIcon(firebaseUser.getPhotoUrl().toString(), mProfilePhoto);
        }
        Map<String, Object> updateValues = new HashMap<>();
        updateValues.put("displayName", firebaseUser.getDisplayName() != null ? firebaseUser.getDisplayName() : "Anonymous");
        updateValues.put("photoUrl", firebaseUser.getPhotoUrl() != null ? firebaseUser.getPhotoUrl().toString() : null);

        FirebaseUtil.getPeopleRef().child(firebaseUser.getUid()).updateChildren(
                updateValues,
                new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError firebaseError, DatabaseReference databaseReference) {
                        if (firebaseError != null) {
                            Toast.makeText(ProfileActivity.this,
                                    "Couldn't save user data: " + firebaseError.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void showSignedOutUI() {
        Log.d(TAG, "Showing signed out UI");
        profileUsername.setText("");
        signInUi.setVisibility(View.VISIBLE);
        profileUi.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && !currentUser.isAnonymous()) {
            dismissProgressDialog();
            showSignedInUI(currentUser);
        } else {
            showSignedOutUI();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.w(TAG, "onConnectionFailed:" + connectionResult);
    }
}
