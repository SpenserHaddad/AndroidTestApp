package com.example.sahko.androidtest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sahko.androidtest.Models.Game;
import com.example.sahko.androidtest.Models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NewGameActivity extends AppCompatActivity {

    private EditText gameNameText;
    private static final String TAG = "NewGameActivity";
    private static final String TAG_TASK_FRAGMENT = "newPostUploadTaskFragment";

    private NewGameUploadTaskFragment taskFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);
        gameNameText = findViewById(R.id.game_name_text);
        gameNameText.setHint("New Game Name");

        FragmentManager fm = getSupportFragmentManager();
        taskFragment = (NewGameUploadTaskFragment) fm.findFragmentByTag(TAG_TASK_FRAGMENT);
    }

    public void addPlayer(View v) {
        Toast.makeText(NewGameActivity.this, "Not implemented at this time", Toast.LENGTH_SHORT).show();
        return;
    }


    public void createGame(View v) {
        String gameName = gameNameText.getText().toString();
        if (gameName == null) {
            Toast.makeText(NewGameActivity.this, "Enter a name for your game.", Toast.LENGTH_SHORT).show();
            return;
        }
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        User owner = new User(user.getDisplayName(), user.getPhotoUrl().toString(), user.getUid());
        Game newGame = new Game(gameName, owner);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();
        ref.child(gameName).setValue(newGame);
    }


}
