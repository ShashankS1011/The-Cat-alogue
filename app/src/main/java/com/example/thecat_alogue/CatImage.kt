package com.example.thecat_alogue

import com.google.gson.annotations.SerializedName

data class CatImage(
    @SerializedName("id")
    var id: String? = null,

    @SerializedName("url")
    var url: String? = null,

    @SerializedName("width")
    var width: Int? = null,

    @SerializedName("height")
    var height: Int? = null
)
