package com.example.bzubiaga.employitics;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.List;

/**
 * Created by bzubiaga on 11/28/15.
 */
public class CommentRowAdapter extends ArrayAdapter<Comment> {

    public CommentRowAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public CommentRowAdapter(Context context, int resource, List<Comment> items) {
        super(context, resource, items);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.comment_row_adapter, null);
        }

        Comment p = getItem(position);

        if (p != null) {
            TextView tt1 = (TextView) v.findViewById(R.id.content);
            Button like= (Button) v.findViewById(R.id.like);
            TextView tt3 = (TextView) v.findViewById(R.id.date);
            TextView tt4 = (TextView) v.findViewById(R.id.name);
            ImageView tt5 = (ImageView) v.findViewById(R.id.imageProfile);

            if (tt1 != null) {
                tt1.setText(p.getContent());
            }

            if (like != null) {
                like.setText(p.getVotes());
                if (p.isLike()){
                    like.setCompoundDrawablesWithIntrinsicBounds(0,0,0,R.drawable.ic_favorite_red_600_18dp);
                    like.setTextColor(Color.parseColor("#E53935"));
                }else{
                    like.setCompoundDrawablesWithIntrinsicBounds(0,0,0,R.drawable.ic_favorite_outline_blue_grey_900_18dp);
                    like.setTextColor(Color.parseColor("#263238"));
                }
            }

            if (tt3 != null) {
                tt3.setText(p.getTimePast());
            }
            if (tt4 != null) {
                String name = p.getName();
                if (name == ""){tt4.setText(" ");}
                else{tt4.setText(" from "+ name);}

            }
            if (tt5 != null) {
                String image = p.getImage();
                    int id = 0;
                    try {
                        id = R.drawable.class.getField(image).getInt(null);
                        tt5.setImageResource(id);
                    } catch (IllegalAccessException e) {
                        tt5.setImageResource(R.drawable.bear_128px);
                        e.printStackTrace();
                    } catch (NoSuchFieldException e) {
                        tt5.setImageResource(R.drawable.bear_128px);
                        e.printStackTrace();
                    }
            }
        }

        Button like = (Button) v.findViewById(R.id.like);
        like.setTag(position);
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = (Integer) view.getTag();
                Comment p = getItem(position);
                if(!p.isLike()) {
                    p.setLike(true);
                    Button like = (Button) view.findViewById(R.id.like);
                    like.setCompoundDrawablesWithIntrinsicBounds(0,0,0,R.drawable.ic_favorite_red_600_18dp);
                    like.setTextColor(Color.parseColor("#E53935"));
                    int votes = Integer.parseInt(p.getVotes()) + 1;
                    like.setText(Integer.toString(votes));
                    p.setVotes(Integer.toString(votes));
                    final String postID = p.getKey();
                    SharedPreferences user = getContext().getSharedPreferences("User_Prefs", 0);
                    String company = user.getString("company", "null");
                    Firebase ref = new Firebase("https://glaring-fire-1308.firebaseio.com" + "/" + company + "/Posts/" + p.getParentKey()+ "/Comments/" + p.getKey()+ "/Comment");
                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            //add +1 to likes on database
                            String voteString = ((String) snapshot.child("votes").getValue());
                            int votes = Integer.parseInt(voteString) + 1;
                            voteString = Integer.toString(votes);
                            snapshot.getRef().child("votes").setValue(voteString);

                            //add post ID to User's database of likes
                            SharedPreferences user = getContext().getSharedPreferences("User_Prefs", 0);
                            String userString = user.getString("userID", "null");
                            Firebase userRef = new Firebase("https://glaring-fire-1308.firebaseio.com" + "/users");
                            userRef.child(userString).child("CommentFavorites").child(postID).setValue("true");
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                        }
                    });

                }else{
                    p.setLike(false);
                    Button like = (Button) view.findViewById(R.id.like);
                    like.setCompoundDrawablesWithIntrinsicBounds(0,0,0,R.drawable.ic_favorite_outline_blue_grey_900_18dp);
                    like.setTextColor(Color.parseColor("#263238"));
                    int votes = Integer.parseInt(p.getVotes()) - 1;
                    like.setText(Integer.toString(votes));
                    p.setVotes(Integer.toString(votes));
                    final String postID = p.getKey();
                    SharedPreferences user = getContext().getSharedPreferences("User_Prefs", 0);
                    String company = user.getString("company", "null");
                    Firebase ref = new Firebase("https://glaring-fire-1308.firebaseio.com" + "/" + company + "/Posts/" + p.getParentKey() + "/Comments/"+p.getKey()+ "/Comment");
                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            //add +1 to likes on database
                            String voteString = ((String) snapshot.child("votes").getValue());
                            int votes = Integer.parseInt(voteString) - 1;
                            voteString = Integer.toString(votes);
                            snapshot.getRef().child("votes").setValue(voteString);

                            //add post ID to User's database of likes
                            SharedPreferences user = getContext().getSharedPreferences("User_Prefs", 0);
                            String userString = user.getString("userID", "null");
                            Firebase userRef = new Firebase("https://glaring-fire-1308.firebaseio.com" + "/users");
                            userRef.child(userString).child("CommentFavorites").child(postID).setValue(null);
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                        }
                    });
                }

            }

        });


        return v;
    }

}
