package com.project.smartfit.ui.model;

import java.io.Serializable;

public class UserActivity implements Serializable {
    private String id, type, activity, amount;
    private int calories;

    public UserActivity() {
    }

    public UserActivity(String id, String type, String activity, String amount, int calories) {
        this.id = id;
        this.type = type;
        this.activity = activity;
        this.amount = amount;
        this.calories = calories;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }
}
