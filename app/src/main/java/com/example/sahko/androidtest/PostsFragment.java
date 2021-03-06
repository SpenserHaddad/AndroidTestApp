package com.example.sahko.androidtest;

import android.content.Context;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sahko.androidtest.Models.Author;
import com.example.sahko.androidtest.Models.Post;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.example.sahko.androidtest.Models.*;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Created by sahko on 10/10/2017.
 */

public class PostsFragment extends Fragment {
    public static final String TAG = "PostsFragment";
    private static final String KEY_LAYOUT_POSITION = "layoutPosition";
    private static final String KEY_TYPE = "type";
    public static final int TYPE_HOME = 1001;
    public static final int TYPE_FEED = 1002;
    private int recyclerViewPosition = 0;
    private OnPostSelectedListener listener;


    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter<PostViewHolder> adapter;

    public PostsFragment() {
        // Required empty public constructor
    }

    public static PostsFragment newInstance(int type) {
        PostsFragment fragment = new PostsFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_posts, container, false);
        rootView.setTag(TAG);

        mRecyclerView = rootView.findViewById(R.id.my_recycler_view);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        if (savedInstanceState != null) {
            // Restore saved layout manager type.
            recyclerViewPosition = (int) savedInstanceState
                    .getSerializable(KEY_LAYOUT_POSITION);
            mRecyclerView.scrollToPosition(recyclerViewPosition);
            // TODO: RecyclerView only restores position properly for some tabs.
        }

        switch (getArguments().getInt(KEY_TYPE)) {
            case TYPE_FEED:
                Log.d(TAG, "Restoring recycler view position (all): " + recyclerViewPosition);
                Query allPostsQuery = FirebaseUtil.getPostsRef();
                adapter = getFirebaseRecyclerAdapter(allPostsQuery);
                adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                    @Override
                    public void onItemRangeInserted(int positionStart, int itemCount) {
                        super.onItemRangeInserted(positionStart, itemCount);
                        // TODO: Refresh feed view.
                    }
                });
                break;
            case TYPE_HOME:
                Log.d(TAG, "Restoring recycler view position (following): " + recyclerViewPosition);

                FirebaseUtil.getCurrentUserRef().child("following").addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(final DataSnapshot followedUserSnapshot, String s) {
                        String followedUserId = followedUserSnapshot.getKey();
                        String lastKey = "";
                        if (followedUserSnapshot.getValue() instanceof String) {
                            lastKey = followedUserSnapshot.getValue().toString();
                        }
                        Log.d(TAG, "followed user id: " + followedUserId);
                        Log.d(TAG, "last key: " + lastKey);
                        FirebaseUtil.getPeopleRef().child(followedUserId).child("posts")
                                .orderByKey().startAt(lastKey).addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(final DataSnapshot postSnapshot, String s) {
                                HashMap<String, Object> addedPost = new HashMap<String, Object>();
                                addedPost.put(postSnapshot.getKey(), true);
                                FirebaseUtil.getFeedRef().child(FirebaseUtil.getCurrentUserId())
                                        .updateChildren(addedPost).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        FirebaseUtil.getCurrentUserRef().child("following")
                                                .child(followedUserSnapshot.getKey())
                                                .setValue(postSnapshot.getKey());
                                    }
                                });
                            }

                            @Override
                            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                            }

                            @Override
                            public void onChildRemoved(DataSnapshot dataSnapshot) {

                            }

                            @Override
                            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                FirebaseUtil.getFeedRef().child(FirebaseUtil.getCurrentUserId())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                final List<String> postPaths = new ArrayList<>();
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    Log.d(TAG, "adding post key: " + snapshot.getKey());
                                    postPaths.add(snapshot.getKey());
                                }
                                adapter = new FirebasePostQueryAdapter(postPaths,
                                        new FirebasePostQueryAdapter.OnSetupViewListener() {
                                            @Override
                                            public void onSetupView(PostViewHolder holder, Post post, int position, String postKey) {
                                                setupPost(holder, post, position, postKey);
                                            }
                                        });
                            }
                            @Override
                            public void onCancelled(DatabaseError firebaseError) {

                            }
                        });
                break;
            default:
                throw new RuntimeException("Illegal post fragment type specified.");
        }
        mRecyclerView.setAdapter(adapter);
    }

    private FirebaseRecyclerAdapter<Post, PostViewHolder> getFirebaseRecyclerAdapter(Query query) {
        return new FirebaseRecyclerAdapter<Post, PostViewHolder>(
                Post.class, R.layout.post_item, PostViewHolder.class, query) {
            @Override
            public void populateViewHolder(final PostViewHolder postViewHolder,
                                           final Post post, final int position) {
                setupPost(postViewHolder, post, position, null);
            }

            @Override
            public void onViewRecycled(PostViewHolder holder) {
                super.onViewRecycled(holder);
            }
        };
    }

    private FirebaseRecyclerAdapter<Game, GameViewHolder> getFirebaseGameRecyclerAdapter(DatabaseReference ref) {
        return new FirebaseRecyclerAdapter<Game, GameViewHolder>(Game.class, R.layout.game_item, GameViewHolder.class, ref) {
            @Override
            protected void populateViewHolder(GameViewHolder viewHolder, Game model, int position) {
                setupGame(viewHolder, model, position, null);
            }
        };
    }

    private void setupGame(GameViewHolder viewHolder, Game game, int position, String inPostKey) {
        viewHolder.setGameNameText(game.getName());
        viewHolder.setOwnerText(game.getOwner());

        final String postKey;
        if (adapter instanceof  FirebaseRecyclerAdapter) {
            postKey = ((FirebaseRecyclerAdapter) adapter).getRef(position).getKey();
        } else {
            postKey = inPostKey;
        }
    }

    private void setupPost(final PostViewHolder postViewHolder, final Post post, final int position, final String inPostKey) {
        postViewHolder.setPhoto(post.getThumb_url());
        postViewHolder.setText(post.getText());
        postViewHolder.setTimestamp(DateUtils.getRelativeTimeSpanString(
                (long) post.getTimestamp()).toString());
        final String postKey;
        if (adapter instanceof FirebaseRecyclerAdapter) {
            postKey = ((FirebaseRecyclerAdapter) adapter).getRef(position).getKey();
        } else {
            postKey = inPostKey;
        }

        Author author = post.getAuthor();
        postViewHolder.setAuthor(author.getFull_name(), author.getUid());
        postViewHolder.setIcon(author.getProfile_picture(), author.getUid());

        ValueEventListener likeListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                postViewHolder.setNumLikes(dataSnapshot.getChildrenCount());
                if (dataSnapshot.hasChild(FirebaseUtil.getCurrentUserId())) {
                    postViewHolder.setLikeStatus(PostViewHolder.LikeStatus.LIKED, getActivity());
                } else {
                    postViewHolder.setLikeStatus(PostViewHolder.LikeStatus.NOT_LIKED, getActivity());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        FirebaseUtil.getLikesRef().child(postKey).addValueEventListener(likeListener);
        postViewHolder.mLikeListener = likeListener;

        postViewHolder.setPostClickListener(new PostViewHolder.PostClickListener() {
            @Override
            public void showComments() {
                Log.d(TAG, "Comment position: " + position);
                listener.onPostComment(postKey);
            }

            @Override
            public void toggleLike() {
                Log.d(TAG, "Like position: " + position);
                listener.onPostLike(postKey);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (adapter != null && adapter instanceof FirebaseRecyclerAdapter) {
            ((FirebaseRecyclerAdapter) adapter).cleanup();
        }
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save currently selected layout manager.
        int recyclerViewScrollPosition = getRecyclerViewScrollPosition();
        Log.d(TAG, "Recycler view scroll position: " + recyclerViewScrollPosition);
        savedInstanceState.putSerializable(KEY_LAYOUT_POSITION, recyclerViewScrollPosition);
        super.onSaveInstanceState(savedInstanceState);
    }

    private int getRecyclerViewScrollPosition() {
        int scrollPosition = 0;
        // TODO: Is null check necessary?
        if (mRecyclerView != null && mRecyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }
        return scrollPosition;
    }
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     */
    public interface OnPostSelectedListener {
        void onPostComment(String postKey);
        void onPostLike(String postKey);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPostSelectedListener) {
            listener = (OnPostSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnPostSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}
