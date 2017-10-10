package com.example.sahko.androidtest.Models;

/**
 * Created by sahko on 10/10/2017.
 */

public class Game {
    private String name;
    private final User owner;
    // private List<User> players;

    public Game(String name, User owner) {
        this.name = name;
        this.owner = owner;
    }

    public String getName() { return name; }
    public void setName(String newName) { name = newName; }

    public User getOwner() { return owner; }
}
