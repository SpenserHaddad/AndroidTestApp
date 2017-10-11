package com.example.sahko.androidtest;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.example.sahko.androidtest.Models.User;
/**
 * Created by sahko on 10/10/2017.
 */

public class GameViewHolder extends RecyclerView.ViewHolder {
    private final View view;

    public GameViewHolder(View itemView) {
        super(itemView);
        view = itemView;

    }

    public void setGameNameText(String name) {

    }

    public void setOwnerText(User owner) {

    }
}
