package com.example.bzubiaga.employitics;

import android.content.Context;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by bzubiaga on 11/28/15.
 */
public class Comment {
    private String content;
    private int votes;
    private String key;
    private String author;
    private String image;
    private String name;
    private String parentKey;
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

    public Comment() {
    }

    public Comment(String content, String author, Map date, String name, String image, String parentKey) {
        this.author = author;
        this.content = content;
        this.votes = 0;
        this.date = date;
        this.image = image;
        this.name = name;
        this.parentKey = parentKey;
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

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getParentKey() {
        return parentKey;
    }

    public void setParentKey(String parentKey) {
        this.parentKey = parentKey;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
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
        } else if (diff < 2 * WEEK_MILLIS){
            return diff / WEEK_MILLIS + " week ago";
        }else if (diff < 4 * WEEK_MILLIS){
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


}