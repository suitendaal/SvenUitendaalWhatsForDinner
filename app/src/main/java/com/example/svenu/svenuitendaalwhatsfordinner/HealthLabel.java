package com.example.svenu.svenuitendaalwhatsfordinner;

/**
 * Created by svenu on 11-12-2017.
 */

public class HealthLabel {
    public String name;
    public boolean preference;

    public HealthLabel() {

    }

    public HealthLabel(String aName, boolean aPreference) {
        this.name = aName;
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
}
