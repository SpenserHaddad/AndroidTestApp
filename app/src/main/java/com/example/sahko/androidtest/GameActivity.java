package com.example.sahko.androidtest;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ShareCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.example.sahko.androidtest.Models.Game;
import com.example.sahko.androidtest.adapter.GameAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class GameActivity extends AppCompatActivity implements GameAdapter.OnGameSelectedListener {
    private static final String TAG = "GameActivity";
    private static final int LIMIT = 20;

    private FirebaseFirestore firestore;
    private Query query;
    private GameAdapter adapter;

    private List<Game> ownedGames;

    RecyclerView gameRecycler;
    FloatingActionButton createGameButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        gameRecycler = findViewById(R.id.games_recycler);
        createGameButton = findViewById(R.id.create_game_button);

        firestore = FirebaseFirestore.getInstance();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        query = firestore.collection("games")
                .whereEqualTo("owner.uid", uid)
                .limit(LIMIT);

        this.adapter = new GameAdapter(query, this) {
            @Override
            protected void onDataChanged() {
                int count = getItemCount();
                if(count == 0) {
                    gameRecycler.setVisibility(View.GONE);
                } else {
                    gameRecycler.setVisibility(View.VISIBLE);
                }
            }

            @Override
            protected void onError(FirebaseFirestoreException e) {
                Log.w("Error in adapter", e);
            }
        };
        gameRecycler.setLayoutManager(new LinearLayoutManager(this));
        gameRecycler.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null)
            adapter.stopListening();
    }

    @Override
    public void onGameSelected(DocumentSnapshot game) {
        Intent intent = new Intent(this, FeedsActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.create_game_button)
    public void onCreateGameButtonClicked() {
        Intent intent = new Intent(this, NewGameActivity.class);
        startActivity(intent);
    }
}
