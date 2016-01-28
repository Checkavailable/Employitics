package com.example.bzubiaga.employitics;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.ui.FirebaseRecyclerAdapter;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Demonstrates the use of {@link RecyclerView} with a {@link LinearLayoutManager} and a
 * {@link GridLayoutManager}.
 */
public class NewLatestList extends Fragment {

    protected String url = "";
    protected String company = "";
    protected String userString = "";

    protected RecyclerView mRecyclerView;
    protected LinearLayoutManager mLayoutManager;
    protected ArrayList<Post> mDataset = new ArrayList<Post>();
    protected ArrayList<String> favorites = new ArrayList<String>();
    FirebaseRecyclerAdapter <Post, MyAdapter> mAdapter;

    private AuthData mAuthData;
    private Firebase.AuthStateListener mAuthStateListener;

    private static final String ARG_SECTION_NUMBER = "section_number";

    public NewLatestList() {
    }


    public static NewLatestList newInstance(int sectionNumber) {
        NewLatestList fragment = new NewLatestList();
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
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycleList);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setReverseLayout(false);
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setLayoutManager(manager);


        if (authData != null) {
            SharedPreferences user = getContext().getSharedPreferences("User_Prefs", 0);
            company = user.getString("company", "null");
            userString = user.getString("userID", "null");

//FOR POPULAR LIST
//            Firebase ref = new Firebase(url + "/" + company + "/Posts");
//            Query queryRef = ref.orderByChild("votes");
//            queryRef

            Firebase mRef = new Firebase(url + "/" + company + "/Posts");


            mAdapter = new FirebaseRecyclerAdapter<Post, MyAdapter>(Post.class, R.layout.mainrowadapter, MyAdapter.class, mRef) {
                @Override
                protected void populateViewHolder(MyAdapter postView, final Post post, int i) {
                    postView.tt1.setText(post.getContent());
                    postView.tt3.setText(post.getTimePast());
                    String postRef = String.valueOf(mAdapter.getRef(i));
                    postRef = postRef.substring(postRef.lastIndexOf("/")+1);
                    String name = post.getName();
                    if (name == ""){postView.tt4.setText(" ");}
                    else{postView.tt4.setText(" from "+ name);}
                    postView.tt5.setText(Integer.toString(post.getCommentInt()));
                    postView.like.setText(Integer.toString(post.getVotes()));
                    try {
                        if (post.isLike(userString, postRef)){
                            postView.like.setCompoundDrawablesWithIntrinsicBounds(0,0,0,R.drawable.ic_favorite_red_600_18dp);
                            postView.like.setTextColor(Color.parseColor("#E53935"));
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    final String finalPostRef = postRef;
                    postView.like.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Button like = (Button) view.findViewById(R.id.like);
                            boolean handler = false;
                            try {
                                handler = post.isLike(userString, finalPostRef);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            Log.w("handler", String.valueOf(handler));
                            post.favoriteHandler(handler, userString, company, finalPostRef);
                            if(handler){
                                like.setCompoundDrawablesWithIntrinsicBounds(0,0,0,R.drawable.ic_favorite_outline_blue_grey_900_18dp);
                                like.setTextColor(Color.parseColor("#263238"));
                            }else{
                                like.setCompoundDrawablesWithIntrinsicBounds(0,0,0,R.drawable.ic_favorite_red_600_18dp);
                                like.setTextColor(Color.parseColor("#E53935"));

                            }

                        }
                    });
                }

            };
            mRecyclerView.setAdapter(mAdapter);
        }



        return rootView;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                Bundle bundle = data.getExtras();
                int position = data.getIntExtra("position",0);
                Post backPost = bundle.getParcelable("Post");
//                Post p = mAdapter.posts.get(position);
//                p.setVotes(backPost.getVotes());
//                p.setLike(backPost.isLike("074c2729-595c-4396-a41b-7c2c1cb00e69"));
//                p.setCommentInt(backPost.getCommentInt());
                mAdapter.notifyDataSetChanged();


            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }


    }
