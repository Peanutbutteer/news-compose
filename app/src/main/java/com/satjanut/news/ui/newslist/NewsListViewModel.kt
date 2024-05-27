package com.satjanut.news.ui.newslist

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.satjanut.news.data.AppState
import com.satjanut.news.data.mapper.NewsListUiMapper
import com.satjanut.news.data.response.Article
import com.satjanut.news.di.NewsService
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class NewsListUiModel(
    val newsList: List<NewsListModel> = listOf(),
    val isRefreshing: Boolean = false,
    val status: AppState<*>,
)

data class NewsUiModel(
    val article: Article,
    var onItemClick: OnNewsItemClick? = null,
) : NewsListModel

class NewsListLoadingModel : NewsListModel

interface NewsListModel

@HiltViewModel(assistedFactory = NewsListViewModel.NewsListViewModelAssistedFactory::class)
class NewsListViewModel @AssistedInject constructor(
    @Assisted("country") private val country: String?,
    @Assisted("category") private val category: String?,
    @Assisted("query") private val query: String?,
    private val newsService: NewsService,
) : ViewModel(), DefaultLifecycleObserver {
    private val _state: MutableStateFlow<NewsListUiModel> = MutableStateFlow(
        NewsListUiModel(
            status = AppState.Initial
        )
    )
    val state: StateFlow<NewsListUiModel> = _state


    private var page = 0
    private var isEndOfList = false

    fun getNewsList(page: Int = this.page) {
        if (state.value.status is AppState.Loading || isEndOfList) {
            return
        }
        _state.value = _state.value.copy(status = AppState.Loading)
        viewModelScope.launch {
            runCatching {
                val newsResponse = withContext(Dispatchers.IO) {
                    if (query.isNullOrBlank()) {
                        newsService.topHeadLines(
                            page = page,
                            category = category
                        )
                    } else {
                        newsService.everything(
                            page = page + 1,
                            category = category,
                            query = query
                        )
                    }
                }

                val articles = newsResponse.articles
                this@NewsListViewModel.run {
                    this.isEndOfList = articles == null || articles.isEmpty() == true || articles.size < PAGE_LIMIT
                    this.page = page
                }


                val currentNewsList = if (_state.value.isRefreshing) {
                    emptyList()
                } else {
                    _state.value.newsList.filterNot {
                        it is NewsListLoadingModel
                    }
                }
                with(NewsListUiMapper()) {
                    _state.value = _state.value.copy(
                        newsList = currentNewsList.toMutableList().apply {
                            articles?.run {
                                addAll(filterNotNull().filterNot { it.title?.contains("[Removed]") == true }.map {
                                    it.toNewsUiModel()
                                })
                            }

                            if (!isEndOfList) {
                                add(NewsListLoadingModel())
                            }
                        }, status = AppState.Success(true), isRefreshing = false
                    )
                }

            }
                .getOrElse {
                    _state.value = _state.value.copy(
                        status = AppState.Error(it), isRefreshing = false
                    )
                }
        }
    }

    fun getNextPage() {
        getNewsList(page + 1)
    }


    fun refresh() {
        page = 0
        isEndOfList = false
        _state.value = _state.value.copy(
            status = AppState.Initial, isRefreshing = true
        )
        viewModelScope.launch {
            getNewsList()
        }
    }

    @AssistedFactory
    interface NewsListViewModelAssistedFactory {
        fun create(
            @Assisted("country") country: String?,
            @Assisted("category") category: String?,
            @Assisted("query") query: String?
        ): NewsListViewModel
    }

    companion object {
        private const val PAGE_LIMIT = 20
    }
}
