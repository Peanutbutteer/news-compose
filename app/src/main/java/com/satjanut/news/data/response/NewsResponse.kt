package com.satjanut.news.data.response


import com.google.gson.annotations.SerializedName

data class NewsResponse(
    @SerializedName("articles")
    val articles: List<Article?>? = null,
    @SerializedName("status")
    val status: String? = null,
    @SerializedName("totalResults")
    val totalResults: Int? = null
)