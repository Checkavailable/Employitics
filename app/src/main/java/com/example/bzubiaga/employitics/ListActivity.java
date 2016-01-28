package com.example.bzubiaga.employitics;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import com.firebase.client.AuthData;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

/**
 * Created by bzubiaga on 11/22/15.
 */
public class ListActivity extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private MainRowAdapter adapter = null;

    protected String url = "";
    protected String company = "";
    protected String userString = "";


    public ListActivity() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ListActivity newInstance(int sectionNumber) {
        ListActivity fragment = new ListActivity();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.list_main, container, false);
        final ListView listView = (ListView) rootView.findViewById(R.id.listView);
        List<Post> postList = new ArrayList<Post>();
        adapter = new MainRowAdapter(getActivity(), R.layout.mainrowadapter, postList);
        listView.setAdapter(adapter);
        url = getString(R.string.firebase_url);

        Firebase ref = new Firebase(getString(R.string.firebase_url));
        AuthData authData = ref.getAuth();


        if (authData != null) {
            SharedPreferences user = getContext().getSharedPreferences("User_Prefs", 0);
            company = user.getString("company", "null");
            userString = user.getString("userID", "null");

//            FOR POPULAR LIST
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
                        adapter.insert(post, 0);
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    System.out.println("The read failed: " + firebaseError.getMessage());
                }
            });
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent commentIntent = new Intent(getContext(), CommentActivity.class);
                Post p = adapter.getItem(position);
                commentIntent.putExtra("Post",p);
                commentIntent.putExtra("position",position);
                startActivityForResult(commentIntent,1);
            }
        });

        return rootView;


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                Log.w("Second Post","Second Post");
                Bundle bundle = data.getExtras();
                int position = data.getIntExtra("position",0);
                Post backPost = bundle.getParcelable("Post");
                Post p = adapter.getItem(position);
                p.setVotes(backPost.getVotes());
                //p.setLike(backPost.isLike("074c2729-595c-4396-a41b-7c2c1cb00e69"));
                p.setCommentInt(backPost.getCommentInt());
                adapter.notifyDataSetChanged();

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }



}
