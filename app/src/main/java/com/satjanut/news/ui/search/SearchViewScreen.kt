package com.satjanut.news.ui.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.satjanut.news.ui.newslist.NewsList
import com.satjanut.news.ui.newslist.OnNewsItemClick
import com.satjanut.news.ui.util.CommonFunc

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchViewScreen(
    onBackPressed: CommonFunc,
    onNewsItemClick: OnNewsItemClick
) {
    var searchText by rememberSaveable { mutableStateOf("") }
    var finalSearchText by rememberSaveable { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.padding(bottom = 12.dp),
                title = {
                    ProvideTextStyle(value = MaterialTheme.typography.bodyLarge) {
                        SearchBar(
                            query = searchText,
                            onQueryChange = {
                                searchText = it
                            },
                            onSearch = {
                                finalSearchText = it
                                keyboardController?.hide()
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Rounded.Search,
                                    contentDescription = "search",
                                )
                            },
                            active = false,
                            onActiveChange = { },
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {

                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack, contentDescription = "Back"
                        )
                    }
                }

            )
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = paddingValues.calculateTopPadding(),
                        bottom = paddingValues.calculateBottomPadding()
                    )
            ) {
                if (finalSearchText.isNotEmpty()) {
                    key(finalSearchText) {
                        NewsList(
                            onNewsItemClick = onNewsItemClick,
                            country = "us",
                            category = null,
                            query = finalSearchText
                        )
                    }
                }
            }

        }
    )
}
