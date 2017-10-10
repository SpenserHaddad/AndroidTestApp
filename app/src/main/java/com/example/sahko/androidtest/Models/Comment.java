package com.example.sahko.androidtest.Models;

/**
 * Created by sahko on 10/10/2017.
 */

public class Comment {

    private Author author;
    private String text;
    private Object timestamp;

    public Comment() {
        // empty default constructor, necessary for Firebase to be able to deserialize comments
    }

    public Comment(Author author, String text, Object timestamp) {
        this.author = author;
        this.text = text;
        this.timestamp = timestamp;
    }

    public Author getAuthor() {
        return author;
    }

    public String getText() {
        return text;
    }

    public Object getTimestamp() {
        return timestamp;
    }
}
