package com.example.thecat_alogue;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

public class CatFact {

    @SerializedName("fact")
    private String fact;

    @SerializedName("length")
    private int length;

    @Nullable
    public String getFact() {
        return fact;
    }

    public int getLength() {
        return length;
    }
}
