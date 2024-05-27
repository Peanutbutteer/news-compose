package com.satjanut.news.ui.route

enum class Screen {
    HOME, SEARCH,
}

sealed class NavigationItem(val route: String) {
    data object Home : NavigationItem(Screen.HOME.name)
    data object Search : NavigationItem(Screen.SEARCH.name)
}
