package com.example.sahko.androidtest;

/**
 * Created by sahko on 10/8/2017.
 */

public class DieRoll {
    private com.example.sahko.androidtest.DieSize dieSize;
    private int numDice;
    private int modifier;

    DieRoll() {
        this(1, com.example.sahko.androidtest.DieSize.d20, 0);
    }

    DieRoll(int numDice, com.example.sahko.androidtest.DieSize dieSize, int modifier) {
        this.numDice = numDice;
        this.dieSize = dieSize;
        this.modifier = modifier;
    }

    public int GetNumDice() {
        return numDice;
    }

    public void SetNumDice(int value) {
        if (numDice <= 0){
            throw new IllegalArgumentException("NumDice must be greater than 0.");
        }
        numDice = value;
    }

    public com.example.sahko.androidtest.DieSize GetDieSize() {
        return dieSize;
    }

    public void SetDieSize(com.example.sahko.androidtest.DieSize value) {
        dieSize = value;
    }

    public int GetModifier() {
        return modifier;
    }

    public void SetModifier(int value) {
        modifier = value;
    }
}
