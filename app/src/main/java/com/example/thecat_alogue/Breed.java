package com.example.thecat_alogue;

public class Breed {
    private String id;
    private String name;

    // Getter methods
    public String getId() { return id; }
    public String getName() { return name; }

    // Validating for the Spinner
    @Override
    public String toString() {
        return name; // This is what the Spinner will display textually!
    }
}