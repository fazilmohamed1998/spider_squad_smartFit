package com.project.smartfit.ui.model;

import java.io.Serializable;
import java.util.List;

public class User implements Serializable {
    private String id, name;
    private int  age;
    private double weight, height, bmiValue;

    public User() {
    }

    public User(String id, String name, int age, double weight, double height, double bmiValue) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.weight = weight;
        this.height = height;
        this.bmiValue = bmiValue;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getBmiValue() {
        return bmiValue;
    }

    public void setBmiValue(double bmiValue) {
        this.bmiValue = bmiValue;
    }
}
