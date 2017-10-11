package com.example.sahko.androidtest.adapter;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.sahko.androidtest.Models.Game;
import com.example.sahko.androidtest.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sahko on 10/11/2017.
 */

public class GameAdapter extends FirestoreAdapter<GameAdapter.ViewHolder> {

    public interface OnGameSelectedListener {
        void onGameSelected(DocumentSnapshot game);
    }

    private OnGameSelectedListener listener;

    public GameAdapter(Query query, OnGameSelectedListener listener) {
        super(query);
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.game_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), this.listener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView gameNameView;
        TextView gameOwnerView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            gameNameView = itemView.findViewById(R.id.game_name_text);
            gameOwnerView = itemView.findViewById(R.id.game_owner_text);
        }

        public void bind(final DocumentSnapshot snapshot,
                         final GameAdapter.OnGameSelectedListener listener) {

            Game game = snapshot.toObject(Game.class);

            gameNameView.setText(game.getName());
            gameOwnerView.setText(game.getOwner().getFull_name());

            // Click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onGameSelected(snapshot);
                    }
                }
            });
        }

    }
}
