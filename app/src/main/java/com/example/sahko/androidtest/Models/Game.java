package com.example.sahko.androidtest.Models;

/**
 * Created by sahko on 10/10/2017.
 */

public class Game {
    private String name;
    private User owner;
    // private List<User> players;

    public Game() {

    }

    public Game(String name, User owner) {
        this.name = name;
        this.owner = owner;
    }

    public String getName() { return name; }

    public User getOwner() { return owner; }

    public String getUid() { return name + "-" + owner.getUid(); }
}
