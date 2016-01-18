package com.example.bzubiaga.employitics;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by bzubiaga on 11/28/15.
 */
public class Post implements Parcelable {
    private String content;
    private String votes;
    private String key;
    private String author;
    private String name;
    private String commentInt;
    private Map date;
    private long time;
    private boolean like = false;


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
        this.votes = "0";
        this.commentInt = "0";
        this.date = date;
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String name) {
        this.content = name;
    }

    public String getVotes() {
        return votes;
    }

    public void setVotes(String votes) {
        this.votes = votes;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getCommentInt() {
        return commentInt;
    }

    public void setCommentInt(String commentInt) {
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

    public Map getDate() {
        return date;
    }

    public boolean isLike() {
        return like;
    }

    public void setLike(boolean like) {
        this.like = like;
    }

    public void setTime(long time){
        this.time = time;
    }

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
        dest.writeString(votes);
        dest.writeString(key);
        dest.writeString(author);
        dest.writeString(name);
        dest.writeString(commentInt);
//        dest.writeMap(date);
        dest.writeLong(time);
        dest.writeByte((byte) (like ? 1 : 0));     //if myBoolean == true, byte == 1
    }

    // We reconstruct the object reading from the Parcel data
    public Post(Parcel p) {
        content = p.readString();
        votes = p.readString();
        key = p.readString();
        author = p.readString();
        name = p.readString();
        commentInt = p.readString();
//        p.readMap(date,Map);
        time = p.readLong();
        like = p.readByte() != 0;
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