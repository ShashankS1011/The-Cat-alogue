package com.example.thecat_alogue

import com.google.gson.annotations.SerializedName

data class CatFact(
    @SerializedName("fact")
    val fact: String? = null,

    @SerializedName("length")
    val length: Int? = null
)
