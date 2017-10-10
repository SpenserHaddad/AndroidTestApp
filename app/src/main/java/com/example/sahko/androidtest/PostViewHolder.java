package com.example.sahko.androidtest;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;


/**
 * Created by sahko on 10/10/2017.
 */

public class PostViewHolder extends RecyclerView.ViewHolder {
    private final View mView;
    private PostClickListener listener;
    public DatabaseReference postRef;
    public ValueEventListener postListener;

    public enum LikeStatus { LIKED, NOT_LIKED }
    private final ImageView likeIcon;
    private static final int POST_TEXT_MAX_LINES = 6;
    private ImageView photoView;
    private ImageView iconView;
    private TextView authorView;
    private TextView postTextView;
    private TextView timestampView;
    private TextView numLikesView;
    public ValueEventListener mLikeListener;

    public PostViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
        photoView = itemView.findViewById(R.id.post_photo);
        iconView = mView.findViewById(R.id.post_author_icon);
        authorView = mView.findViewById(R.id.post_author_name);
        postTextView = itemView.findViewById(R.id.post_text);
        timestampView = itemView.findViewById(R.id.post_timestamp);
        numLikesView = (TextView) itemView.findViewById(R.id.post_num_likes);

        itemView.findViewById(R.id.post_comment_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.showComments();
            }
        });
        likeIcon = (ImageView) itemView.findViewById(R.id.post_like_icon);
        likeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.toggleLike();
            }
        });
    }

    public void setPhoto(String url) {
        GlideUtil.loadImage(url, photoView);
    }

    public void setIcon(String url, final String authorId) {
        GlideUtil.loadProfileIcon(url, iconView);
        iconView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUserDetail(authorId);
            }
        });
    }

    public void setAuthor(String author, final String authorId) {
        if (author == null || author.isEmpty()) {
            author = mView.getResources().getString(R.string.user_info_no_name);
        }
        authorView.setText(author);
        authorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUserDetail(authorId);
            }
        });
    }

    private void showUserDetail(String authorId) {
        Context context = mView.getContext();
        Intent userDetailIntent = new Intent(context, UserDetailActivity.class);
        userDetailIntent.putExtra(UserDetailActivity.USER_ID_EXTRA_NAME, authorId);
        context.startActivity(userDetailIntent);
    }


    public void setText(final String text) {
        if (text == null || text.isEmpty()) {
            postTextView.setVisibility(View.GONE);
            return;
        } else {
            postTextView.setVisibility(View.VISIBLE);
            postTextView.setText(text);
            postTextView.setMaxLines(POST_TEXT_MAX_LINES);
            postTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (postTextView.getMaxLines() == POST_TEXT_MAX_LINES) {
                        postTextView.setMaxLines(Integer.MAX_VALUE);
                    } else {
                        postTextView.setMaxLines(POST_TEXT_MAX_LINES);
                    }
                }
            });
        }
    }

    public void setTimestamp(String timestamp) {
        timestampView.setText(timestamp);
    }

    public void setNumLikes(long numLikes) {
        String suffix = numLikes == 1 ? " like" : " likes";
        numLikesView.setText(numLikes + suffix);
    }

    public void setPostClickListener(PostClickListener listener) {
        this.listener = listener;
    }

    public void setLikeStatus(LikeStatus status, Context context) {
        likeIcon.setImageDrawable(ContextCompat.getDrawable(context,
                status == LikeStatus.LIKED ? R.drawable.heart_full : R.drawable.heart_empty));
    }


    public interface PostClickListener {
        void showComments();
        void toggleLike();
    }
}
