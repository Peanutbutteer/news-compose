package com.satjanut.news.ui.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.satjanut.news.data.model.Section
import com.satjanut.news.ui.home.viewmodel.HomeUiModel
import com.satjanut.news.ui.home.viewmodel.HomeViewModel
import com.satjanut.news.ui.newslist.OnNewsItemClick
import com.satjanut.news.ui.newslist.NewsList
import com.satjanut.news.ui.util.ObserveLifecycleEvents
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel<HomeViewModel>(), onNewsItemClick: OnNewsItemClick?,
    onOpenSearchPage: () -> Unit,
) {
    viewModel.ObserveLifecycleEvents(LocalLifecycleOwner.current.lifecycle)
    val uiState by viewModel.uiModel.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background,
                titleContentColor = MaterialTheme.colorScheme.primary,
            ), title = {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Icon(
                        Icons.Rounded.Search,
                        contentDescription = "",
                        modifier = Modifier.padding(12.dp).clickable {
                            onOpenSearchPage()
                        }
                    )
                    Text("Headline", modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.width(48.dp))
                }
            })
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding),
        ) {
            uiState.IfSuccess {
                NewsListScreen(it.apply {
                    this.onNewsItemClick = onNewsItemClick
                })
            }
        }
    }
}

//
//class ArticleProvider : PreviewParameterProvider<Article> {
//    override val values = sequenceOf(Article("th", "🇹🇭", "Thailand"))
//}
//
//@Composable
//fun CountryItem(@PreviewParameter(ArticleProvider::class) country: Article, onItemClick: (Article) -> Unit) {
//    Box(modifier = Modifier
//        .clickable { onItemClick(country) }
//        .padding(horizontal = 16.dp, vertical = 8.dp)) {
//        Row(
//            modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
//        ) {
//            Text(
//                text = country.flag, fontSize = 24.sp, modifier = Modifier.padding(end = 8.dp)
//            )
//            Text(
//                text = country.name,
//                fontSize = 16.sp,
//            )
//        }
//    }
//}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TabBar(pagerState: PagerState, sectors: List<Section>) {
    val scope = rememberCoroutineScope()
    ScrollableTabRow(selectedTabIndex = pagerState.currentPage, divider = {
        Spacer(modifier = Modifier.height(5.dp))
    }, indicator = { tabPositions ->
        TabRowDefaults.Indicator(
            modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]), height = 2.dp, color = MaterialTheme.colorScheme.primary
        )
    }, edgePadding = 20.dp, modifier = Modifier
        .fillMaxWidth()
        .padding(top = 0.dp)
        .wrapContentHeight(), tabs = {
        sectors.forEachIndexed { index, s ->
            Tab(selected = pagerState.currentPage == index, onClick = {
                scope.launch {
                    pagerState.animateScrollToPage(index)
                }
            }, text = {
                Text(text = s.name)
            })
        }
    })
}


class HomeUiModelProvider : PreviewParameterProvider<HomeUiModel> {
    override val values = sequenceOf(
        HomeUiModel(
            sections = listOf(
                Section(name = "Latest", ""),
                Section(name = "U.S.", "")
            )
        )
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Preview
@Composable
fun NewsListScreen(
    @PreviewParameter(HomeUiModelProvider::class) uiModel: HomeUiModel,
) {
    val pagerState = rememberPagerState(pageCount = {
        uiModel.sections.size
    })
    TabBar(pagerState = pagerState, sectors = uiModel.sections)
    HorizontalPager(modifier = Modifier.fillMaxHeight(), state = pagerState) { page ->
        NewsList(
            onNewsItemClick = {
                uiModel.onNewsItemClick?.invoke(it)
            },
            country = uiModel.sections[page].country,
            category = uiModel.sections[page].category,
            query = null
        )
    }
}