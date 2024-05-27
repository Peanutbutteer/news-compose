package com.satjanut.news

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.satjanut.news.ui.search.SearchViewScreen
import com.satjanut.news.ui.theme.NewsApplicationTheme
import com.satjanut.news.ui.home.HomeScreen
import com.satjanut.news.ui.route.AppNavigator
import com.satjanut.news.ui.route.NavigationItem
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        CustomTabsClient.getPackageName(this, null)?.let { chromePackageName ->
            val isBound = CustomTabsClient.connectAndInitialize(this, chromePackageName)
            if (isBound) {
                // Successful
            } else {
                // Fallback, Can't connect and initialize the CCT
            }
        } ?: run {
            // Fallback, There's no Chrome installed
        }
        setContent {
            NewsApplicationTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    AppNavHost(navController = rememberNavController())
                }
            }
        }
    }
}


@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: String = NavigationItem.Home.route,
    navActions: AppNavigator = remember(navController) {
        AppNavigator(navController)
    }
) {
    NavHost(
        modifier = modifier, navController = navController, startDestination = startDestination
    ) {
        composable(NavigationItem.Home.route) {
            HomeScreen(onNewsItemClick = {
                runCatching {
                    val uri = Uri.parse(it.article.url)
                    val customTabsIntent: CustomTabsIntent = CustomTabsIntent.Builder().build()
                    customTabsIntent.launchUrl(navController.context, uri)
                }.getOrElse {

                }
            }, onOpenSearchPage = {
                navActions.openSearchPage()
            })
        }
        composable(NavigationItem.Search.route, enterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(700)
            )
        }, exitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Right, animationSpec = tween(700)
            )
        }) {
            SearchViewScreen({
                navController.popBackStack()
            }) {
                runCatching {
                    val uri = Uri.parse(it.article.url)
                    val customTabsIntent: CustomTabsIntent = CustomTabsIntent.Builder().build()
                    customTabsIntent.launchUrl(navController.context, uri)
                }.getOrElse {

                }
            }
        }
    }
}