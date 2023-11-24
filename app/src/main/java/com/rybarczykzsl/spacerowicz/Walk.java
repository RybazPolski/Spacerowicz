package com.rybarczykzsl.spacerowicz;

import androidx.annotation.NonNull;

public class Walk {
    private String name, description;
    private double distance;
    private int backgroundResourceId;

    private Walk(String name, String description, double distance, int backgroundResourceId) {
        this.name = name;
        this.description = description;
        this.distance = distance;
        this.backgroundResourceId = backgroundResourceId;
    };
    public static final Walk[] walks = {
            new Walk("Spacer w parku", "Opis 1", 3.5, R.drawable.walk_park),
            new Walk("Spacer w lesie", "Opis 2", 5.0, R.drawable.walk_forest),
            new Walk("Spacer w mieście", "Opis 3", 2.5, R.drawable.walk_city),
            new Walk("Spacer po górach", "Opis 4", 10.0, R.drawable.walk_mountains),
    };

    public String getName() { return name; }
    public String getDesc() { return description; }
    public double getDistance() { return distance; }
    public int getBackgroundResourceId() { return backgroundResourceId;}
    public String toString() {
        return this.name;
    }
}
