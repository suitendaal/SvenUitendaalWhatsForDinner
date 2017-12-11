package com.example.svenu.svenuitendaalwhatsfordinner;

import java.util.ArrayList;

/**
 * Created by svenu on 4-12-2017.
 */

public class Recipe {

    public String userName;
    public String name; //label
    public String source; //source
    public String url; //url
    public String image; //image
    public ArrayList<String> labels; //dietLabels and healthLabels:
    // diet: balanced, high-protein, high-fiber, low-fat, low-carb, low-sodium
    // health: vegan, vegetarian, paleo, dairy-free, gluten-free, wheat-free, fat-free,
    // low-sugar, egg-free, peanut-free, tree-nut-free, soy-free, fish-free, shellfish-free
    public ArrayList<String> ingredients; //ingredients/text
    public float calories; //calories
    public boolean isPublic; //true if user set it public
    public String instructions; //for a user created recipe

    // Constructor for Firebase
    public Recipe() {}

    // Constructor for user created recipe
    public Recipe(String aUserName, String aName, ArrayList<String> aLabels, ArrayList<String> anIngredients, String anInstructions) {
        this.userName = aUserName;
        this.name = aName;
        this.labels = aLabels;
        this.ingredients = anIngredients;
        this.instructions = anInstructions;
        this.isPublic = false;
    }

    // Constructor for the api
    public Recipe(String aUserName, String aName, String aSource, String anUrl, String anImage, ArrayList<String> aLabels, ArrayList<String> anIngredients, float aCalories) {
        this.userName = aUserName;
        this.name = aName;
        this.source = aSource;
        this.url = anUrl;
        this.image = anImage;
        this.labels = aLabels;
        this.ingredients = anIngredients;
        this.calories = aCalories;
        this.isPublic = false;
    }

    public void setPublic(boolean bool) {
        this.isPublic = bool;
    }
}
