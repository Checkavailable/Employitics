package com.example.bzubiaga.employitics;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Button;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.Semaphore;

/**
 * Created by bzubiaga on 11/28/15.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class Post implements Parcelable {
    private String author;
    private int commentInt;
    private String content;
    private Map date;
    private int flagged;
    private String name;
    private int votes;
    private long time;
    private boolean like;

    private String key;
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;
    private static final long WEEK_MILLIS = 7 * DAY_MILLIS;
    private static final long MONTH_MILLIS = 4 * WEEK_MILLIS;
    private static final long YEAR_MILLIS = 12 * MONTH_MILLIS;

    public Post() {}


    public Post(String content, String author, String name, Map date) {
        this.author = author;
        this.content = content;
        this.name = name;
        this.date = date;
        this.votes = 0;
        this.commentInt = 0;
        this.flagged = 0;

    }

    public String getContent() {
        return content;
    }

    public void setContent(String name) {
        this.content = name;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public int getFlagged() {
        return flagged;
    }

    public void setFlagged(int votes) {
        this.flagged = votes;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getCommentInt() {
        return commentInt;
    }

    public void setCommentInt(int commentInt) {
        this.commentInt = commentInt;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name= name;
    }

    @JsonIgnore
    public Map getDate() {
        return date;
    }

    public boolean isLike(String userString, final String finalPostRef)  throws InterruptedException {

        final boolean[] likeValue = {false};

        Firebase ref = new Firebase("https://glaring-fire-1308.firebaseio.com" + "/users/"+userString+"/Favorites/"+finalPostRef);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                likeValue[0] = snapshot.exists();
                Log.w("like withiin Snapshot",String.valueOf(likeValue[0]));
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });

        Log.w("like returned",String.valueOf(likeValue[0]));
        return likeValue[0];
    }

    public boolean isisLike() {
        return like;
    }

    public void setsetLike(boolean like) {
        this.like = like;
    }


    @JsonIgnore
    public void setTime(long time){
        this.time = time;
    }

    @JsonIgnore
    public String getTimePast() {

        if (time< 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return "just now";
        }

        // TODO: localize
        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "Just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "A min ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " mins ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "An hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hrs ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "Yesterday";
        } else if (diff < 7 * DAY_MILLIS){
            return diff / DAY_MILLIS + " days ago";
        }else if (diff < 2 * WEEK_MILLIS){
            return diff / WEEK_MILLIS + " week ago";
        } else if (diff < 4 * WEEK_MILLIS){
            return diff / WEEK_MILLIS + " weeks ago";
        } else if (diff < 12 * MONTH_MILLIS) {
            return diff / MONTH_MILLIS + " months ago";
        } else if (diff < 2 * YEAR_MILLIS){
            return "A year ago";
        } else {
            return "A few years ago";
        }
    }

    public void setDate(Map date) {
        this.date = date;
    }

    @Override
    public int describeContents() {
        return hashCode();

    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(content);
        dest.writeInt(votes);
        dest.writeString(key);
        dest.writeString(author);
        dest.writeString(name);
        dest.writeInt(commentInt);
//        dest.writeMap(date);
        dest.writeLong(time);
        //dest.writeByte((byte) (like ? 1 : 0));     //if myBoolean == true, byte == 1
    }

    // We reconstruct the object reading from the Parcel data
    public Post(Parcel p) {
        content = p.readString();
        votes = p.readInt();
        key = p.readString();
        author = p.readString();
        name = p.readString();
        commentInt = p.readInt();
//        p.readMap(date,Map);
        time = p.readLong();
        //like = p.readByte() != 0;
    }

    // We need to add a Creator
    public static final Parcelable.Creator<Post> CREATOR = new Parcelable.Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel parcel) {
            return new Post(parcel);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };
}