package com.digitguard.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.digitguard.app.ui.auth.RoleSelectScreen
import com.digitguard.app.ui.education.EducationScreen
import com.digitguard.app.ui.guardian.GuardianDashboardScreen
import com.digitguard.app.ui.guardian.GuardianLinkScreen
import com.digitguard.app.ui.home.HomeScreen
import com.digitguard.app.ui.onboarding.OnboardingScreen
import com.digitguard.app.ui.onboarding.PermissionSetupScreen
import com.digitguard.app.ui.settings.SettingsScreen

object Routes {
    const val ONBOARDING = "onboarding"
    const val ROLE_SELECT = "role_select"
    const val GUARDIAN_LINK = "guardian_link"
    const val PERMISSION_SETUP = "permission_setup"
    const val HOME = "home"
    const val EDUCATION = "education"
    const val SETTINGS = "settings"
    const val GUARDIAN_DASHBOARD = "guardian_dashboard"
}

private val protectedMainScreens = listOf(Routes.HOME, Routes.EDUCATION, Routes.SETTINGS)
private val guardianMainScreens = listOf(Routes.GUARDIAN_DASHBOARD, Routes.SETTINGS)

@Composable
fun NavGraph(
    startFromOnboarding: Boolean = true,
    appViewModel: AppViewModel = hiltViewModel(),
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = currentRoute in protectedMainScreens || currentRoute in guardianMainScreens

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    when {
                        currentRoute in protectedMainScreens -> {
                            listOf(
                                Triple(Routes.HOME, Icons.Default.Home, "홈"),
                                Triple(Routes.EDUCATION, Icons.Default.MenuBook, "배우기"),
                                Triple(Routes.SETTINGS, Icons.Default.Settings, "설정"),
                            ).forEach { (route, icon, label) ->
                                NavigationBarItem(
                                    icon = { Icon(icon, contentDescription = label) },
                                    label = { Text(label, fontSize = 16.sp) },
                                    selected = currentRoute == route,
                                    onClick = {
                                        navController.navigate(route) {
                                            popUpTo(Routes.HOME) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                )
                            }
                        }
                        currentRoute in guardianMainScreens -> {
                            listOf(
                                Triple(Routes.GUARDIAN_DASHBOARD, Icons.Default.Dashboard, "대시보드"),
                                Triple(Routes.SETTINGS, Icons.Default.Settings, "설정"),
                            ).forEach { (route, icon, label) ->
                                NavigationBarItem(
                                    icon = { Icon(icon, contentDescription = label) },
                                    label = { Text(label, fontSize = 16.sp) },
                                    selected = currentRoute == route,
                                    onClick = {
                                        navController.navigate(route) {
                                            popUpTo(Routes.GUARDIAN_DASHBOARD) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = if (startFromOnboarding) Routes.ONBOARDING else Routes.HOME,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(Routes.ONBOARDING) {
                OnboardingScreen(
                    onComplete = {
                        navController.navigate(Routes.ROLE_SELECT) {
                            popUpTo(Routes.ONBOARDING) { inclusive = true }
                        }
                    }
                )
            }

            composable(Routes.ROLE_SELECT) {
                RoleSelectScreen(
                    onRoleSelected = { role ->
                        appViewModel.setUserRole(role)
                        when (role) {
                            "protected" -> navController.navigate(Routes.GUARDIAN_LINK) {
                                popUpTo(Routes.ROLE_SELECT) { inclusive = true }
                            }
                            "guardian" -> navController.navigate(Routes.GUARDIAN_DASHBOARD) {
                                popUpTo(Routes.ROLE_SELECT) { inclusive = true }
                            }
                        }
                    }
                )
            }

            composable(Routes.GUARDIAN_LINK) {
                GuardianLinkScreen(
                    onLinked = {
                        navController.navigate(Routes.PERMISSION_SETUP) {
                            popUpTo(Routes.GUARDIAN_LINK) { inclusive = true }
                        }
                    },
                    onSkip = {
                        navController.navigate(Routes.PERMISSION_SETUP) {
                            popUpTo(Routes.GUARDIAN_LINK) { inclusive = true }
                        }
                    },
                )
            }

            composable(Routes.PERMISSION_SETUP) {
                PermissionSetupScreen(
                    onComplete = {
                        appViewModel.completeOnboarding()
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.PERMISSION_SETUP) { inclusive = true }
                        }
                    }
                )
            }

            composable(Routes.HOME) { HomeScreen() }
            composable(Routes.EDUCATION) { EducationScreen() }
            composable(Routes.SETTINGS) { SettingsScreen() }
            composable(Routes.GUARDIAN_DASHBOARD) { GuardianDashboardScreen() }
        }
    }
}
