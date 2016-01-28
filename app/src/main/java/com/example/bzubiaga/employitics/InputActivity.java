package com.example.bzubiaga.employitics;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;
import com.firebase.client.ValueEventListener;

import java.util.Map;

public class InputActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Post an update");
        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_clear_white_24dp);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_post, menu);
        final MenuItem mItem = menu.findItem(R.id.action_post);
        final EditText text = (EditText) findViewById(R.id.editText);
        final CheckBox checkbox = (CheckBox) findViewById(R.id.checkBox);

        mItem.setOnMenuItemClickListener(
                new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        SharedPreferences user = getSharedPreferences("User_Prefs", 0);
                        String company = user.getString("company", "null");
                        String userId = user.getString("userID", "null");
                        String name = user.getString("name", "null");
                        if (!checkbox.isChecked()) {
                            name = "";
                        }
                        PosttoPush post = new PosttoPush(text.getText().toString(), userId, name, ServerValue.TIMESTAMP);
                        Firebase fbPost = new Firebase(getString(R.string.firebase_url) + "/" + company + "/Posts");
                        fbPost = fbPost.push();
                        final String keyPost = fbPost.getKey();
                        fbPost.setValue(post);

                        Firebase ref = new Firebase(getString(R.string.firebase_url));
                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                SharedPreferences user = getSharedPreferences("User_Prefs", 0);
                                String userString = user.getString("userID", "null");
                                Firebase userRef = new Firebase("https://glaring-fire-1308.firebaseio.com" + "/users");
                                userRef.child(userString).child("Posts").child(keyPost).setValue("true");
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {
                            }
                        });


                        finish();
                        return false;
                    }

                });

        text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence str, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence str, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable str) {
                if (str.toString().trim().length() > 0) {
                    mItem.setIcon(R.drawable.ic_send_yellow_24dp);
                } else {
                    mItem.setIcon(R.drawable.ic_send_white_24dp);
                }
            }
        });


        return true;
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
            checkbox.setText("Post as " + name);

        } else {
            // Remove the meat
            checkbox.setText("Post anonymously");
        }

    }

    public class PosttoPush{
        private String author;

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public int getCommentInt() {
            return commentInt;
        }

        public void setCommentInt(int commentInt) {
            this.commentInt = commentInt;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public Map getDate() {
            return date;
        }

        public void setDate(Map date) {
            this.date = date;
        }

        public int getFlagged() {
            return flagged;
        }

        public void setFlagged(int flagged) {
            this.flagged = flagged;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getVotes() {
            return votes;
        }

        public void setVotes(int votes) {
            this.votes = votes;
        }

        private int commentInt;
        private String content;
        private Map date;
        private int flagged;
        private String name;
        private int votes;

        PosttoPush(){}

        public PosttoPush(String content, String author, String name, Map date) {
            this.author = author;
            this.content = content;
            this.name = name;
            this.date = date;
            this.votes = 0;
            this.commentInt = 0;
            this.flagged = 0;

        }

    }
}
