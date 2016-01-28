package com.example.bzubiaga.employitics;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;


public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder>{
    private static final String TAG = "CustomAdapter";
    List<Post> posts;
    Context mContext;
    private OnItemClickListener mOnItemClickListener;


    public PostAdapter(List<Post> posts, OnItemClickListener onItemClickListener){
        mOnItemClickListener = onItemClickListener;
        this.posts = posts;
    }



    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {

        TextView tt1;
        Button like;
        TextView tt3;
        TextView tt4;
        TextView tt5;
        View container;



        public PostViewHolder(View itemView) {
            super(itemView);

//            // Define click listener for the ViewHolder's View.
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Log.d(TAG, "Element " + getPosition() + " clicked.");
//                }
//            });

            container = itemView;
            tt1 = (TextView) itemView.findViewById(R.id.content);
            like= (Button) itemView.findViewById(R.id.like);
            tt3 = (TextView) itemView.findViewById(R.id.date);
            tt4 = (TextView) itemView.findViewById(R.id.name);
            tt5 = (TextView) itemView.findViewById(R.id.comments);

        }

        @Override
        public void onClick(View v) {

            if (v.getId() == like.getId()){

            } else {

            }

        }
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.mainrowadapter, parent, false);
        PostViewHolder pvh = new PostViewHolder(v);
        mContext = parent.getContext();
        return pvh;
    }



    @Override
    public void onBindViewHolder(PostViewHolder postViewHolder,final int i) {

        Log.d(TAG, "Element " + i + " set.");
        Post p = posts.get(i);

        postViewHolder.tt1.setText(posts.get(i).getContent());
        postViewHolder.tt3.setText(posts.get(i).getTimePast());
        String name = posts.get(i).getName();
        if (name == ""){postViewHolder.tt4.setText(" ");}
        else{postViewHolder.tt4.setText(" from "+ name);}
//        postViewHolder.tt5.setText(Integer.toString(posts.get(i).getCommentInt()));

        if (postViewHolder.like != null) {
            postViewHolder.like.setText(Integer.toString(posts.get(i).getVotes()));
            try {
                if (p.isLike("074c2729-595c-4396-a41b-7c2c1cb00e69","TODO")){//TODO pass post key
                    postViewHolder.like.setCompoundDrawablesWithIntrinsicBounds(0,0,0,R.drawable.ic_favorite_red_600_18dp);
                    postViewHolder.like.setTextColor(Color.parseColor("#E53935"));
                }else{
                    postViewHolder.like.setCompoundDrawablesWithIntrinsicBounds(0,0,0,R.drawable.ic_favorite_outline_blue_grey_900_18dp);
                    postViewHolder.like.setTextColor(Color.parseColor("#263238"));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        postViewHolder.container.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(v, i);
            }
        });

        postViewHolder.like.setTag(i);
        postViewHolder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = (Integer) view.getTag();
                Post p = posts.get(position);
                try {
                    if(!p.isLike("074c2729-595c-4396-a41b-7c2c1cb00e69","TODO")) {//TODO pass post key
                        //p.setLike(true);
                        Button like = (Button) view.findViewById(R.id.like);
                        like.setCompoundDrawablesWithIntrinsicBounds(0,0,0,R.drawable.ic_favorite_red_600_18dp);
                        like.setTextColor(Color.parseColor("#E53935"));
                        int votes = p.getVotes() + 1;
                        like.setText(Integer.toString(votes));
                        p.setVotes(votes);
                        final String postID = p.getKey();
                        SharedPreferences user = mContext.getSharedPreferences("User_Prefs", 0);
                        String company = user.getString("company", "null");
                        Firebase ref = new Firebase("https://glaring-fire-1308.firebaseio.com" + "/" + company + "/Posts/" + p.getKey());
                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                //add +1 to likes on database
                                int voteString = ((int) (long) snapshot.child("votes").getValue());
                                int votes = (voteString) + 1;
                                snapshot.getRef().child("votes").setValue(votes);

                                //add post ID to User's database of likes
                                SharedPreferences user = mContext.getSharedPreferences("User_Prefs", 0);
                                String userString = user.getString("userID", "null");
                                Firebase userRef = new Firebase("https://glaring-fire-1308.firebaseio.com" + "/users");
                                userRef.child(userString).child("Favorites").child(postID).setValue("true");
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {
                            }
                        });

                    }else{
                        //p.setLike(false);
                        Button like = (Button) view.findViewById(R.id.like);
                        like.setCompoundDrawablesWithIntrinsicBounds(0,0,0,R.drawable.ic_favorite_outline_blue_grey_900_18dp);
                        like.setTextColor(Color.parseColor("#263238"));
                        int votes = p.getVotes() - 1;
                        like.setText(Integer.toString(votes));
                        p.setVotes(votes);
                        final String postID = p.getKey();
                        SharedPreferences user = mContext.getSharedPreferences("User_Prefs", 0);
                        String company = user.getString("company", "null");
                        Firebase ref = new Firebase("https://glaring-fire-1308.firebaseio.com" + "/" + company + "/Posts/" + p.getKey());
                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                //add +1 to likes on database
                                int voteString = ((int) (long) snapshot.child("votes").getValue());
                                int votes = voteString - 1;
                                snapshot.getRef().child("votes").setValue(votes);

                                //add post ID to User's database of likes
                                SharedPreferences user = mContext.getSharedPreferences("User_Prefs", 0);
                                String userString = user.getString("userID", "null");
                                Firebase userRef = new Firebase("https://glaring-fire-1308.firebaseio.com" + "/users");
                                userRef.child(userString).child("Favorites").child(postID).setValue(null);
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

        });


    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }



}