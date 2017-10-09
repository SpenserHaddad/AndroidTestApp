package com.example.sahko.androidtest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AddCharacterActionActivity extends AppCompatActivity {
    private DieRollAdapter diceRolls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_character_action);

        this.diceRolls = new DieRollAdapter(this, Arrays.asList(new DieRoll()));
        ListView dieRollsListView = findViewById(R.id.dieRollsListView);
        dieRollsListView.setAdapter(diceRolls);
        dieRollsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView list = (ListView)parent;

            }
        });
    }

    public void addRoll(View view) {
        this.diceRolls.add(new DieRoll());
    }
}
