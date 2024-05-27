package com.satjanut.news.ui.newslist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.satjanut.news.R
import com.satjanut.news.data.AppState
import com.satjanut.news.data.response.Article
import com.satjanut.news.extension.toDateTimeString
import com.satjanut.news.ui.home.viewmodel.HomeUiModel
import com.satjanut.news.ui.util.ErrorView
import com.satjanut.news.ui.util.shimmerBrush
import kotlinx.coroutines.launch

typealias OnNewsItemClick = (NewsUiModel) -> Unit

class NewsUiModelProvider : PreviewParameterProvider<NewsUiModel> {
    override val values = sequenceOf(
        NewsUiModel(
            Article(
                "th",
                "🇹🇭",
                "Thailand",
                title = "Google issues Google TV and Android TV OS update",
                urlToImage = "https://cdn.broadbandtvnews.com/wp-content/uploads/2024/05/24135212/Google-Android-TV-14.jpg"
            )
        )
    )
}


@Preview
@Composable
fun NewsListRow(
    @PreviewParameter(NewsUiModelProvider::class) news: NewsUiModel
) {
    Box {
        Column(modifier = Modifier
            .fillMaxWidth()
            .clickable {
                news.onItemClick?.invoke(news)
            }
            .padding(
                vertical = 16.dp,
                horizontal = 20.dp,
            )) {
            AsyncImage(
                model = news.article.urlToImage.orEmpty(),
                placeholder = ColorPainter(colorResource(id = R.color.grey)),
                error = ColorPainter(colorResource(id = R.color.grey)),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(3f / 2),
                contentDescription = null,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                news.article.title.orEmpty(),
                fontSize = 16.sp,
                maxLines = 4,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    news.article.publishedAt.toDateTimeString(),
                    fontSize = 12.sp,
                    maxLines = 1
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NewsList(
    onNewsItemClick: OnNewsItemClick,
    country: String?,
    category: String?,
    query: String?,
    viewModel: NewsListViewModel = hiltViewModel(
        key = "${NewsListViewModel::class.java.name}$country$category$query",
        creationCallback = { factory: NewsListViewModel.NewsListViewModelAssistedFactory ->
            factory.create(country = country, category = category, query = query)
        }),
) {
    val scope = rememberCoroutineScope()
    val uiState by viewModel.state.collectAsState()
    val pullRefreshState = rememberPullRefreshState(
        refreshing = uiState.isRefreshing, onRefresh = viewModel::refresh
    )
    val listState = rememberLazyListState()
    LaunchedEffect(Unit) {
        if (uiState.newsList.isEmpty()) {
            viewModel.getNewsList()
        }
    }

    if (uiState.newsList.isEmpty()) {
        if (uiState.status is AppState.Error) {
            Box(modifier = Modifier.fillMaxSize()) {
                ErrorView(onRetry = {
                    scope.launch {
                        viewModel.getNextPage()
                    }
                })
            }
        } else {
            Column {
                for (i in 1..10) {
                    NewsLoadingItem()
                    Divider(modifier = Modifier.padding(horizontal = 20.dp), color = Color.LightGray.copy(0.2f))
                }
            }
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pullRefresh(pullRefreshState)
        ) {
            LazyColumn(state = listState, modifier = Modifier.fillMaxWidth()) {
                itemsIndexed(uiState.newsList) { index, item ->
                    if (index == uiState.newsList.size - 1) {
                        LaunchedEffect(Unit) {
                            scope.launch {
                                viewModel.getNextPage()
                            }
                        }
                    }
                    if (item is NewsUiModel) {
                        item.onItemClick = onNewsItemClick
                        NewsListRow(item)
                        Divider(modifier = Modifier.padding(horizontal = 20.dp), color = Color.LightGray.copy(0.2f))
                    }
                    if (item is NewsListLoadingModel) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(vertical = 8.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }

            PullRefreshIndicator(
                refreshing = uiState.isRefreshing,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter),
                backgroundColor = Color.White,
            )
        }
    }
}


@Preview
@Composable
fun NewsLoadingItem() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 24.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(shimmerBrush(showShimmer = true))
            )
            Spacer(modifier = Modifier.height(20.dp))
            Box(
                modifier = Modifier
                    .width(140.dp)
                    .height(16.dp)
                    .background(shimmerBrush(showShimmer = true))
            )
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(16.dp)
                    .background(shimmerBrush(showShimmer = true))
            )
        }
    }
}