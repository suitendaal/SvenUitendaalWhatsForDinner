package com.example.svenu.svenuitendaalwhatsfordinner;

import java.util.ArrayList;

/**
 * Created by svenu on 8-12-2017.
 */

public class UserData {
    // diet: balanced, high-protein, high-fiber, low-fat, low-carb, low-sodium
    // health: vegan, vegetarian, paleo, dairy-free, gluten-free, wheat-free, fat-free,
    // low-sugar, egg-free, peanut-free, tree-nut-free, soy-free, fish-free, shellfish-free
    public String userName;
    public String userUid;

    public ArrayList<HealthLabel> labels = new ArrayList<>();

    public UserData() {
    }

    public UserData(String aUserName, String aUserUid, ArrayList<HealthLabel> healthLabels) {
        this.userName = aUserName;
        this.userUid = aUserUid;
        this.labels = healthLabels;
    }
}
