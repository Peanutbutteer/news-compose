package com.satjanut.news.data.repository

import com.satjanut.news.data.model.Section
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class MasterDataRepository @Inject constructor() {
    suspend fun getSections(): List<Section> = withContext(Dispatchers.IO) {
        return@withContext listOf(
            Section(name = "Latest"),
            Section(name = "Business", category = "business"),
            Section(name = "Technology", category = "technology"),
            Section(name = "Entertainment", category = "entertainment"),
            Section(name = "Sports", category = "sports"),
            Section(name = "Science", category = "science"),
            Section(name = "Health", category = "health"),
        )
    }
}
