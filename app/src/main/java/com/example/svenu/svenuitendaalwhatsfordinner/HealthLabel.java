package com.example.svenu.svenuitendaalwhatsfordinner;

/**
 * Created by svenu on 11-12-2017.
 * Class which contains health and dietlabels and the user preferences
 */

public class HealthLabel {
    public String name;
    public String category;
    public boolean preference;

    public HealthLabel() {

    }

    public HealthLabel(String aName, String aCategory, boolean aPreference) {
        this.name = aName;
        this.category = aCategory;
        this.preference = aPreference;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public void setPreference(boolean preference) {
        this.preference = preference;
    }
    public boolean getPreference() {
        return preference;
    }

    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
}
