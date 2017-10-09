package com.example.sahko.androidtest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sahko on 10/8/2017.
 */

public class DieRollAdapter extends BaseAdapter {
    private ArrayList<DieRoll> dieRolls;
    private Context context;
    private LayoutInflater inflater;
    private ArrayAdapter<DieSize> dieSizeAdapter;
    public DieRollAdapter(Context context, List<DieRoll> objects) {
        super();
        this.context = context;
        dieRolls = new ArrayList<>(objects);
        inflater = LayoutInflater.from(context);

        dieSizeAdapter = new ArrayAdapter<>(this.context,
                android.R.layout.simple_spinner_item, DieSize.values());

    }

    public void add(DieRoll roll) {
        dieRolls.add(roll);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return dieRolls.size();
    }

    @Override
    public Object getItem(int position) {
        return dieRolls.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = inflater.inflate(R.layout.die_roll_item_layout, parent, false);
        EditText numDiceText = rowView.findViewById(R.id.numDiceText);
        Spinner dieSizeSpinner = rowView.findViewById(R.id.dieSizeSpinner);
        EditText rollModifierText = rowView.findViewById(R.id.rollModifierText);

        DieRoll rollItem = dieRolls.get(position);

        numDiceText.setText(Integer.toString(rollItem.GetNumDice()));
        dieSizeSpinner.setAdapter(dieSizeAdapter);
        dieSizeSpinner.setSelection(rollItem.GetDieSize().ordinal());
        rollModifierText.setText(Integer.toString(rollItem.GetModifier()));

        return rowView;
    }

}
