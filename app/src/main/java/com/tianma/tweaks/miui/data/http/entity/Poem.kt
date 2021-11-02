package com.tianma.tweaks.miui.data.http.entity

import com.google.gson.annotations.SerializedName

/**
 * 诗
 */
data class Poem(
    @SerializedName("content")
    val content: String?,
    @SerializedName("origin")
    val origin: String?,
    @SerializedName("author")
    val author: String?,
    @SerializedName("category")
    val category: String?
)