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
import java.util.HashSet;
import java.util.List;

import com.firebase.client.AuthData;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

/**
 * Created by bzubiaga on 11/22/15.
 */
public class NewNewList extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private MainRowAdapter adapter = null;

    public NewNewList() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static NewNewList newInstance(int sectionNumber) {
        NewNewList fragment = new NewNewList();
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

        // Get ListView object from xml
        final ListView listView = (ListView) rootView.findViewById(R.id.listView);

        List<Post> postList = new ArrayList<Post>();

        // Create a new Adapter
        adapter = new MainRowAdapter(getActivity(), R.layout.mainrowadapter, postList);

        // Assign adapter to ListView
        listView.setAdapter(adapter);


        // Use Firebase to populate the list.
        Firebase ref = new Firebase(getString(R.string.firebase_url));
        AuthData authData = ref.getAuth();
        final String url = getString(R.string.firebase_url);

        if (authData !=null) {
            SharedPreferences user = getContext().getSharedPreferences("User_Prefs", 0);
            final String company = user.getString("company", "null");
            String userString = user.getString("userID", "null");
            Firebase userRef = new Firebase("https://glaring-fire-1308.firebaseio.com" + "/users/"+userString+"/Favorites");
            // Attach an listener to read the data at our posts reference
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                HashSet<String> favorites = new HashSet<>();
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    for (DataSnapshot  postSnapshot: snapshot.getChildren()) {
                        favorites.add((String) postSnapshot.getKey());
                    }

                    new Firebase(url + "/" + company + "/Posts")
                            .addChildEventListener(new ChildEventListener() {
                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                    Post post = new Post();
                                    post.setContent((String) dataSnapshot.child("content").getValue());
                                    post.setVotes((int)(long) dataSnapshot.child("votes").getValue());
                                    post.setKey((String) dataSnapshot.getKey());
                                    post.setTime((long) dataSnapshot.child("date").getValue());
                                    post.setAuthor((String) dataSnapshot.child("author").getValue());
                                    post.setName((String) dataSnapshot.child("name").getValue());
                                    post.setCommentInt((int)(long) dataSnapshot.child("commentInt").getValue());
                                    String check = post.getKey();
                                    post.setsetLike(favorites.contains(check));
                                    adapter.insert(post,0);
                                }

                                public void onChildRemoved(DataSnapshot dataSnapshot) {
                                    //adapter.remove((String) dataSnapshot.child("Post").child("content").getValue());
                                }

                                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                                }

                                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                                }

                                public void onCancelled(FirebaseError firebaseError) {
                                }
                            });

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

        Log.w("First Post","First Post");

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                Log.w("Second Post","Second Post");
                Bundle bundle = data.getExtras();
                int position = data.getIntExtra("position",0);
                Post backPost = bundle.getParcelable("Post");
                Post p = adapter.getItem(position);
                p.setVotes(backPost.getVotes());
                p.setsetLike(backPost.isisLike());
                p.setCommentInt(backPost.getCommentInt());
                adapter.notifyDataSetChanged();

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//on
}