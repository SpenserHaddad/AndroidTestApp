package com.example.sahko.androidtest;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sahko.androidtest.Models.Game;
import com.example.sahko.androidtest.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;

import java.util.HashMap;
import java.util.Map;

public class NewGameActivity extends AppCompatActivity {

    private EditText gameNameText;
    private static final String TAG = "NewGameActivity";
    private static final String projectId = "ec601testproject";
    private Game lastReadGame;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);
        gameNameText = findViewById(R.id.game_name_text);
        gameNameText.setHint("New Game Name");
        Query reference = db.collection("games").whereEqualTo("name", "AnotherTestGame");
    }

    public void addPlayer(View v) {
        Toast.makeText(NewGameActivity.this, "Not implemented at this time", Toast.LENGTH_SHORT).show();
        return;
    }


    public void createGame(View v) {
        String gameName = gameNameText.getText().toString();
        if (gameName.isEmpty()) {
            Toast.makeText(NewGameActivity.this, "Enter a name for your game.", Toast.LENGTH_SHORT).show();
            return;
        }
/*
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference ownerRef = dbRef.child("users").child(currentUser.getUid());
        ownerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User owner = dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        */
        User owner = FirebaseFirestoreUtil.getCurrentUser();


        Game game = new Game(gameName, owner);

        //dbRef.child("games").child(game.getUid()).setValue(game);


        db.collection("games").document(game.getUid()).set(game)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Intent intent = new Intent(NewGameActivity.this, FeedsActivity.class);
                        startActivity(intent);
                    }
                });

        db.collection("games").document(game.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                lastReadGame = documentSnapshot.toObject(Game.class);
            }
        });
        }
    }
