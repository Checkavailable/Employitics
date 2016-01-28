package com.example.bzubiaga.employitics;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RadioButton;


import com.firebase.client.AuthData;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Demonstrates the use of {@link RecyclerView} with a {@link LinearLayoutManager} and a
 * {@link GridLayoutManager}.
 */
public class LatestList extends Fragment {

    private static final String TAG = "RecyclerViewFragment";
    private static final String KEY_LAYOUT_MANAGER = "layoutManager";
    private static final int SPAN_COUNT = 2;
    private static final int DATASET_COUNT = 60;
    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 5;
    int firstVisibleItem, visibleItemCount, totalItemCount;
    protected HashSet<String> favorites = new HashSet<>();
    protected String url = "";
    protected String company = "";
    protected String userString = "";


    protected RecyclerView mRecyclerView;
    protected PostAdapter mAdapter;
    protected LinearLayoutManager mLayoutManager;
    protected SynchronizedInit init = new SynchronizedInit();
    protected ArrayList<Post> mDataset = new ArrayList<Post>();

    private AuthData mAuthData;
    /* Listener for Firebase session changes */
    private Firebase.AuthStateListener mAuthStateListener;

    private static final String ARG_SECTION_NUMBER = "section_number";

    public LatestList() {
    }


    public static LatestList newInstance(int sectionNumber) {
        LatestList fragment = new LatestList();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.list_latest, container, false);
        url = getString(R.string.firebase_url);

        Firebase ref = new Firebase(getString(R.string.firebase_url));
        AuthData authData = ref.getAuth();

        if (authData != null) {
            SharedPreferences user = getContext().getSharedPreferences("User_Prefs", 0);
            company = user.getString("company", "null");
            userString = user.getString("userID", "null");

//FOR POPULAR LIST
//            Firebase ref = new Firebase(url + "/" + company + "/Posts");
//            Query queryRef = ref.orderByChild("votes");
//            queryRef

            Firebase mRef = new Firebase(url + "/" + company + "/Posts");

            mRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        Post post = new Post();
                        post.setContent((String) postSnapshot.child("content").getValue());
                        post.setVotes((int) (long) postSnapshot.child("votes").getValue());
                        post.setKey((String) postSnapshot.getKey());
                        post.setTime((long) postSnapshot.child("date").getValue());
                        post.setAuthor((String) postSnapshot.child("author").getValue());
                        post.setName((String) postSnapshot.child("name").getValue());
                        post.setCommentInt((int) (long) postSnapshot.child("commentInt").getValue());
                        mDataset.add(0,post);
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    System.out.println("The read failed: " + firebaseError.getMessage());
                }
            });
        }

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycleList);
        mLayoutManager = new LinearLayoutManager(getContext());


        // LinearLayoutManager is used here, this will layout the elements in a similar fashion
        // to the way ListView would layout elements. The RecyclerView.LayoutManager defines how
        // elements are laid out.
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);


        mAdapter = new PostAdapter(mDataset, new PostAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent commentIntent = new Intent(getContext(), CommentActivity.class);
                Post p = mAdapter.posts.get(position);
                commentIntent.putExtra("Post",p);
                commentIntent.putExtra("position",position);
                startActivityForResult(commentIntent,1);
            }

        });


        // Set CustomAdapter as the adapter for RecyclerView.
        mRecyclerView.setAdapter(mAdapter);


        return rootView;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                Bundle bundle = data.getExtras();
                int position = data.getIntExtra("position",0);
                Post backPost = bundle.getParcelable("Post");
                Post p = mAdapter.posts.get(position);
                p.setVotes(backPost.getVotes());
                //p.setLike(backPost.isLike("074c2729-595c-4396-a41b-7c2c1cb00e69"));
                p.setCommentInt(backPost.getCommentInt());
                mAdapter.notifyDataSetChanged();


            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    public class SynchronizedInit{

        private synchronized void initFavoritesSync() {
            Firebase ref = new Firebase(getString(R.string.firebase_url));
            AuthData authData = ref.getAuth();
            url = getString(R.string.firebase_url);

            if (authData != null) {
                SharedPreferences user = getContext().getSharedPreferences("User_Prefs", 0);
                company = user.getString("company", "null");
                String userString = user.getString("userID", "null");
                Firebase userRef = new Firebase("https://glaring-fire-1308.firebaseio.com" + "/users/" + userString + "/Favorites");
                // Attach an listener to read the data at our posts reference
                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    HashSet<String> favorites = new HashSet<>();

                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            favorites.add((String) postSnapshot.getKey());
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                        System.out.println("The read failed: " + firebaseError.getMessage());
                    }
                });
            }

        }
        private synchronized void initDatasetSync() {

            Firebase ref = new Firebase(url + "/" + company + "/Posts");

            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                        Post post = new Post();
                        post.setContent((String) postSnapshot.child("content").getValue());
                        post.setVotes((int) (long) postSnapshot.child("votes").getValue());
                        post.setKey((String) postSnapshot.getKey());
                        post.setTime((long) postSnapshot.child("date").getValue());
                        post.setAuthor((String) postSnapshot.child("author").getValue());
                        post.setName((String) postSnapshot.child("name").getValue());
                        post.setCommentInt((int) (long) postSnapshot.child("commentInt").getValue());
                        String check = post.getKey();
                        //post.setLike(favorites.contains(check));
                        mDataset.add(0,post);
                    }
                    mAdapter.notifyDataSetChanged();
                }
                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    System.out.println("The read failed: " + firebaseError.getMessage());
                }
            });


        }

        private synchronized void initArraySync(){


        }


    }


}