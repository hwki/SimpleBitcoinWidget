package com.brentpanther.bitcoinwidget.ui

import android.appwidget.AppWidgetManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.brentpanther.bitcoinwidget.ui.home.HomeScreen
import com.brentpanther.bitcoinwidget.ui.selection.CoinSelectionScreen
import com.brentpanther.bitcoinwidget.ui.settings.SettingsScreen
import com.brentpanther.bitcoinwidget.ui.theme.SimpleBitcoinWidgetTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel: MainViewModel by viewModels()
        viewModel.widgetId =
            intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
        viewModel.removeOrphanedWidgets()

        setContent {
            val startDestination = viewModel.getStartDestination().collectAsState(null).value ?: return@setContent
            SimpleBitcoinWidgetTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = startDestination
                ) {
                    composable("home") { HomeScreen(navController) }
                    composable("create/{widgetId}",
                        arguments = listOf(
                            navArgument("widgetId") {
                                type = NavType.IntType
                                defaultValue = viewModel.widgetId
                            }
                        )
                    ) { navEntry ->
                        navEntry.arguments?.let {
                            CoinSelectionScreen(navController, it.getInt("widgetId"))
                        }
                    }
                    composable("setting/{widgetId}",
                        arguments = listOf(
                            navArgument("widgetId") {
                                type = NavType.IntType
                                defaultValue = viewModel.widgetId
                            }
                        )
                    ) { navEntry ->
                        navEntry.arguments?.let {
                            SettingsScreen(
                                navController,
                                it.getInt("widgetId")
                            )
                        }
                    }
                }
            }
        }
    }
}

