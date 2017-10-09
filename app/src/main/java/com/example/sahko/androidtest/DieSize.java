package com.example.sahko.androidtest;

/**
 * Created by sahko on 10/8/2017.
 */

public enum DieSize {
    d2  (2, "d2"),
    d4  (4, "d4"),
    d6  (6, "d6"),
    d8  (8, "d8"),
    d10 (10, "d10"),
    d12 (12, "d12"),
    d20 (20, "d20");

    public int Size;
    public String Name;

    DieSize(int size, String name) {
        Size = size;
        Name = name;
    }

    @Override
    public String toString() {
        return Name;
    }
}
