package com.satjanut.news.ui.route

import androidx.navigation.NavHostController

class AppNavigator(private val navController: NavHostController) {
    fun openSearchPage() {
        navController.navigate(
            NavigationItem.Search.route
        )
    }
}
