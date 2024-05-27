package com.satjanut.news.data.mapper

import com.satjanut.news.data.response.Article
import com.satjanut.news.ui.newslist.NewsUiModel

class NewsListUiMapper {
    fun Article.toNewsUiModel(): NewsUiModel {
        return NewsUiModel(
            article = this
        )
    }
}
