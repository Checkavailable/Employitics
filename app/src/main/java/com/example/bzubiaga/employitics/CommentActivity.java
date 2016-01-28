package com.example.bzubiaga.employitics;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.AuthData;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class CommentActivity extends AppCompatActivity implements AbsListView.OnScrollListener {

    private int lastTopValue = 0;
    private TextView textView;
    private TextView nameView;
    private TextView likeView;
    private Post p;
    private SharedPreferences user;
    private String company;
    private String userString;
    private String name;
    private int position;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Post");
        Bundle bundle = getIntent().getExtras();
        p = (Post) bundle.getParcelable("Post");
        position = bundle.getInt("position");
        String content= p.getContent();
        final boolean like = p.isisLike();
        final String key_post = p.getKey();
        user = this.getSharedPreferences("User_Prefs", 0);
        company = user.getString("company", "null");
        userString = user.getString("userID", "null");
        name = user.getString("name", "null");

        // Get ListView object from xml
        final ListView listView = (ListView) findViewById(R.id.listView);
        List<Comment> postList = new ArrayList<Comment>();
        // Create a new Adapter
        final CommentRowAdapter adapter = new CommentRowAdapter(this, R.layout.comment_row_adapter, postList);
        final EditText text = (EditText) findViewById(R.id.commentText);

        // Assign adapter to ListView
        listView.setAdapter(adapter);
        View headerList = LayoutInflater.from(this).inflate(R.layout.comment_header, listView, false);
        textView = (TextView) headerList.findViewById(R.id.postTitle);
        textView.setText(content);
        nameView = (TextView) headerList.findViewById(R.id.name);

        if (p.getName()== ""){
            nameView.setText("");
        }else{
            nameView.setText(" from "+ p.getName());
        }

        likeView = (TextView) headerList.findViewById(R.id.likes);
        likeView.setText(Integer.toString(p.getVotes()));
        listView.addHeaderView(headerList);
        listView.setOnScrollListener(this);

        // Use Firebase to populate the list.
        Firebase ref = new Firebase(getString(R.string.firebase_url));
        AuthData authData = ref.getAuth();
        final String url = getString(R.string.firebase_url);

        if (authData !=null) {
            Firebase userRef = new Firebase("https://glaring-fire-1308.firebaseio.com" + "/users/"+userString+"/CommentFavorites");
            // Attach an listener to read the data at our posts reference
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                HashSet<String> favorites = new HashSet<>();
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    for (DataSnapshot  postSnapshot: snapshot.getChildren()) {
                        favorites.add((String) postSnapshot.getKey());
                    }

                    new Firebase(url + "/" + company + "/Posts/"+key_post+"/Comments")
                            .addChildEventListener(new ChildEventListener() {
                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                    Comment comment = new Comment();
                                    comment.setContent((String) dataSnapshot.child("content").getValue());
                                    comment.setVotes((int)(long) dataSnapshot.child("votes").getValue());
                                    comment.setKey((String) dataSnapshot.getKey());
                                    comment.setParentKey((String) dataSnapshot.child("parentKey").getValue());
                                    comment.setTime((long) dataSnapshot.child("date").getValue());
                                    comment.setAuthor((String) dataSnapshot.child("author").getValue());
                                    comment.setName((String) dataSnapshot.child("name").getValue());
                                    comment.setImage((String) dataSnapshot.child("image").getValue());
                                    String check = comment.getKey();
                                    comment.setLike(favorites.contains(check));
                                    adapter.add(comment);

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



//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            public void onItemClick(AdapterView<?> parent, View view,
//                                    int position, long id) {
//                Intent commentIntent = new Intent(getContext(), CommentActivity.class);
//                Post p = adapter.getItem(position);
//                Bundle bundle = new Bundle();
//                bundle.putString("key", p.getKey());
//                bundle.putString("content", p.getContent());
//                bundle.putString("like",Boolean.toString(p.isLike()));
//                bundle.putString("votes",p.getVotes());
//                commentIntent.putExtras(bundle);
//                startActivity(commentIntent);
//            }
//        });

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_post);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox checkBox = (CheckBox ) findViewById(R.id.checkBox);
                Firebase ref = new Firebase(getString(R.string.firebase_url)+"/"+ company + "/Posts/"+key_post+"/Comments");
                String userId = user.getString("userID", "null");
                ImageGenerator imageString = new ImageGenerator();
                String image = "";

                if (!checkBox.isChecked()){
                    RandomNameGenerator rnd = new RandomNameGenerator();
                    name = rnd.next();
                    image = imageString.randomImage();
                }else{
                    name = user.getString("name", "null");
                    String firstLetter = String.valueOf(name.charAt(0)).toLowerCase();
                    char c = firstLetter.charAt(0);
                    if (Character.isLetter(c)){image = firstLetter;
                    } else{
                        image = "hashtag";
                    }
                }


                Comment comment = new Comment(text.getText().toString(),userId, ServerValue.TIMESTAMP,name,image,key_post);
                ref = ref.push();
                final String keyComment = ref.getKey();
                ref.setValue(comment);
                text.setText("");
                listView.smoothScrollToPosition(adapter.getCount());

                //Add comment to User database
                Firebase updateUser = new Firebase(getString(R.string.firebase_url));
                updateUser.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        SharedPreferences user = getSharedPreferences("User_Prefs", 0);
                        String userString = user.getString("userID", "null");
                        Firebase userRef = new Firebase("https://glaring-fire-1308.firebaseio.com" + "/users");
                        userRef.child(userString).child("Comments").child(keyComment).setValue("true");
                    }
                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                    }
                });

                //add +1 to  comments on database
                int comments = p.getCommentInt() + 1;
                p.setCommentInt(comments);
                final String postID = p.getKey();
                Firebase postRef = new Firebase("https://glaring-fire-1308.firebaseio.com" + "/" + company + "/Posts/" + p.getKey());
                postRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        //add +1 to  comments on database
                        int commentVote = ((int) (long)snapshot.child("commentInt").getValue());
                        int commentsInt = (commentVote) + 1;
                        snapshot.getRef().child("commentInt").setValue(commentsInt);
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                    }
                });

            }
        });

        text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence str, int start, int count, int after)    {

            }

            @Override
            public void onTextChanged(CharSequence str, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable str) {
                if(str.toString().trim().length()>0){
                    fab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFC107")));
                }else{
                    fab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#e0e0e0")));
                }
            }
        });


    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_favorite:
                try {
                    if(!p.isLike("074c2729-595c-4396-a41b-7c2c1cb00e69", "todo")) {//TODO pass post key
                        //p.setLike(true);
                        item.setIcon(R.drawable.ic_favorite_white_24dp);
                        int votes = p.getVotes() + 1;
                        //like.setText(Integer.toString(votes));
                        p.setVotes(votes);
                        likeView.setText(Integer.toString(votes));
                        final String postID = p.getKey();
                        Firebase ref = new Firebase("https://glaring-fire-1308.firebaseio.com" + "/" + company + "/Posts/" + p.getKey());
                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                //add +1 to likes on database
                                int voteString = ((int)(long) snapshot.child("votes").getValue());
                                int votes = voteString+ 1;
                                snapshot.getRef().child("votes").setValue(votes);

                                //add post ID to User's database of likes
                                Firebase userRef = new Firebase("https://glaring-fire-1308.firebaseio.com" + "/users");
                                userRef.child(userString).child("Favorites").child(postID).setValue("true");
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {
                            }
                        });

                    }else{
                        //p.setLike(false);
                        item.setIcon(R.drawable.ic_favorite_outline_white_24dp);
                        int votes = p.getVotes() - 1;
                        p.setVotes(votes);
                        likeView.setText(Integer.toString(votes));
                        final String postID = p.getKey();
                        Firebase ref = new Firebase("https://glaring-fire-1308.firebaseio.com" + "/" + company + "/Posts/" + p.getKey());
                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                //add +1 to likes on database
                                int voteString = ((int)(long) snapshot.child("votes").getValue());
                                int votes = voteString - 1;
                                snapshot.getRef().child("votes").setValue(votes);

                                //add post ID to User's database of likes
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
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public boolean onSupportNavigateUp(){
        Intent returnIntent = new Intent();
        returnIntent.putExtra("position",position);
        returnIntent.putExtra("Post",p);
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("position",position);
        returnIntent.putExtra("Post",p);
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_comment, menu);
        MenuItem item = menu.findItem(R.id.action_favorite);
        Bundle bundle = getIntent().getExtras();
        Post post = (Post) bundle.getParcelable("Post");
        try {
            if(post.isLike("074c2729-595c-4396-a41b-7c2c1cb00e69","todo")){item.setIcon(R.drawable.ic_favorite_white_24dp);}//TODO passpost key
            else{item.setIcon(R.drawable.ic_favorite_outline_white_24dp);}
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        Rect rect = new Rect();
        textView.getLocalVisibleRect(rect);
        if (lastTopValue != rect.top) {
            lastTopValue = rect.top;
            textView.setY((float) (rect.top / 2.0));
       }
    }

    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        CheckBox checkbox = (CheckBox) findViewById(R.id.checkBox);
        boolean checked = checkbox.isChecked();

        // Check which checkbox was clicked
        if (checked) {
            // Put some meat on the sandwich
            SharedPreferences user = getSharedPreferences("User_Prefs", 0);
            String name = user.getString("name", "null");
            checkbox.setText("Post as "+ name);

        }else{
            // Remove the meat
            checkbox.setText("Post anonymously");
        }

    }


}
