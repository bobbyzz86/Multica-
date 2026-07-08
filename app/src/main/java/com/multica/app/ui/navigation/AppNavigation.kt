package com.multica.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.multica.app.ui.home.HomeScreen
import com.multica.app.ui.issues.IssueCreateScreen
import com.multica.app.ui.issues.IssueDetailScreen
import com.multica.app.ui.issues.IssueListScreen
import com.multica.app.ui.issues.IssueViewModel
import com.multica.app.ui.login.LoginScreen
import com.multica.app.ui.login.LoginViewModel
import com.multica.app.ui.notifications.NotificationScreen
import com.multica.app.ui.profile.ProfileScreen

sealed class Screen(val route: String, val title: String, val icon: ImageVector?) {
    object Login : Screen("login", "Login", null)
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Issues : Screen("issues", "Issues", Icons.Default.Home)
    object IssueDetail : Screen("issue/{issueId}", "Issue Detail", null) {
        fun createRoute(issueId: String) = "issue/$issueId"
    }
    object IssueCreate : Screen("issue/create", "Create Issue", null)
    object Notifications : Screen("notifications", "Notifications", Icons.Default.Notifications)
    object Profile : Screen("profile", "Profile", Icons.Default.Person)
}

data class BottomNavItem(val screen: Screen, val label: String, val icon: ImageVector)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val loginViewModel: LoginViewModel = hiltViewModel()
    val isLoggedIn by loginViewModel.isLoggedIn.collectAsState()

    val startDestination = if (isLoggedIn) Screen.Home.route else Screen.Login.route

    val bottomNavItems = listOf(
        BottomNavItem(Screen.Home, "Home", Icons.Default.Home),
        BottomNavItem(Screen.Issues, "Issues", Icons.Default.Home),
        BottomNavItem(Screen.Notifications, "Notifications", Icons.Default.Notifications),
        BottomNavItem(Screen.Profile, "Profile", Icons.Default.Person)
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = isLoggedIn && currentDestination?.route in bottomNavItems.map { it.screen.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            selected = currentDestination?.hierarchy?.any { it.route == item.screen.route } == true,
                            onClick = {
                                navController.navigate(item.screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToIssues = { navController.navigate(Screen.Issues.route) },
                    onNavigateToNotifications = { navController.navigate(Screen.Notifications.route) }
                )
            }
            composable(Screen.Issues.route) {
                IssueListScreen(
                    onIssueClick = { issueId ->
                        navController.navigate(Screen.IssueDetail.createRoute(issueId))
                    },
                    onCreateIssue = {
                        navController.navigate(Screen.IssueCreate.route)
                    }
                )
            }
            composable(
                route = Screen.IssueDetail.route,
                arguments = listOf(navArgument("issueId") { type = NavType.StringType })
            ) { backStackEntry ->
                val issueId = backStackEntry.arguments?.getString("issueId") ?: return@composable
                IssueDetailScreen(
                    issueId = issueId,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.IssueCreate.route) {
                IssueCreateScreen(onBack = { navController.popBackStack() })
            }
            composable(Screen.Notifications.route) {
                NotificationScreen()
            }
            composable(Screen.Profile.route) {
                ProfileScreen(
                    onLogout = {
                        loginViewModel.logout()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}
