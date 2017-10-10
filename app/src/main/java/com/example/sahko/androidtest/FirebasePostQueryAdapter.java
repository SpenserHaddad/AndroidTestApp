package com.example.sahko.androidtest;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.example.sahko.androidtest.Models.Post;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sahko on 10/10/2017.
 */

public class FirebasePostQueryAdapter extends RecyclerView.Adapter<PostViewHolder>  {
    private final String TAG = "PostQueryAdapter";
    private List<String> postPaths;
    private OnSetupViewListener onSetupViewListener;

    public FirebasePostQueryAdapter(List<String> paths, OnSetupViewListener onSetupViewListener) {
        if (paths == null || paths.isEmpty()) {
            postPaths = new ArrayList<>();
        } else {
            postPaths = paths;
        }
        this.onSetupViewListener = onSetupViewListener;
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_item, parent, false);
        return new PostViewHolder(v);
    }

    public void setPaths(List<String> postPaths) {
        this.postPaths = postPaths;
        notifyDataSetChanged();
    }

    public void addItem(String path) {
        postPaths.add(path);
        notifyItemInserted(postPaths.size());
    }

    @Override
    public void onBindViewHolder(final PostViewHolder holder, int position) {
        DatabaseReference ref = FirebaseUtil.getPostsRef().child(postPaths.get(position));
        // TODO: Fix this so async event won't bind the wrong view post recycle.
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Post post = dataSnapshot.getValue(Post.class);
                Log.d(TAG, "post key: " + dataSnapshot.getKey());
                onSetupViewListener.onSetupView(holder, post, holder.getAdapterPosition(),
                        dataSnapshot.getKey());
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
                Log.e(TAG, "Error occurred: " + firebaseError.getMessage());
            }
        };
        ref.addValueEventListener(postListener);
        holder.postRef = ref;
        holder.postListener = postListener;
    }

    @Override
    public void onViewRecycled(PostViewHolder holder) {
        super.onViewRecycled(holder);
        holder.postRef.removeEventListener(holder.postListener);
    }

    @Override
    public int getItemCount() {
        return postPaths.size();
    }

    public interface OnSetupViewListener {
        void onSetupView(PostViewHolder holder, Post post, int position, String postKey);
    }
}
