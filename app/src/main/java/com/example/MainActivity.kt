package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.habitrise.ui.HabitViewModel
import com.example.habitrise.ui.MainTab
import com.example.habitrise.ui.components.AddEditHabitDialog
import com.example.habitrise.ui.components.DeleteHabitConfirmDialog
import com.example.habitrise.ui.components.ExportDataDialog
import com.example.habitrise.ui.components.LogSessionDialog
import com.example.habitrise.ui.components.ResetDataConfirmDialog
import com.example.habitrise.ui.screens.HabitDetailScreen
import com.example.habitrise.ui.screens.HomeScreen
import com.example.habitrise.ui.screens.InsightsScreen
import com.example.habitrise.ui.screens.LearnScreen
import com.example.habitrise.ui.screens.ProfileScreen
import com.example.habitrise.ui.screens.TrackerScreen
import com.example.habitrise.util.Localization
import com.example.habitrise.util.ThemeMode
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {
    private val viewModel: HabitViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val isDarkTheme = when (uiState.userSettings.themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }

            MyApplicationTheme(darkTheme = isDarkTheme) {
                val snackbarHostState = remember { SnackbarHostState() }

                LaunchedEffect(Unit) {
                    viewModel.snackbarMessage.collectLatest { msg ->
                        snackbarHostState.showSnackbar(msg)
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val isHindi = uiState.userSettings.isHindi

                    Scaffold(
                        snackbarHost = { SnackbarHost(snackbarHostState) },
                        bottomBar = {
                            if (uiState.selectedHabitDetailId == null) {
                                NavigationBar(
                                    modifier = Modifier
                                        .windowInsetsPadding(WindowInsets.navigationBars)
                                        .testTag("bottom_nav_bar")
                                ) {
                                    NavigationBarItem(
                                        selected = uiState.currentTab == MainTab.HOME,
                                        onClick = { viewModel.selectTab(MainTab.HOME) },
                                        icon = {
                                            Icon(
                                                imageVector = if (uiState.currentTab == MainTab.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                                                contentDescription = Localization.get("nav_home", isHindi)
                                            )
                                        },
                                        label = { Text(Localization.get("nav_home", isHindi)) },
                                        modifier = Modifier.testTag("nav_tab_home")
                                    )

                                    NavigationBarItem(
                                        selected = uiState.currentTab == MainTab.TRACKER,
                                        onClick = { viewModel.selectTab(MainTab.TRACKER) },
                                        icon = {
                                            Icon(
                                                imageVector = if (uiState.currentTab == MainTab.TRACKER) Icons.Filled.CalendarMonth else Icons.Outlined.CalendarMonth,
                                                contentDescription = Localization.get("nav_tracker", isHindi)
                                            )
                                        },
                                        label = { Text(Localization.get("nav_tracker", isHindi)) },
                                        modifier = Modifier.testTag("nav_tab_tracker")
                                    )

                                    NavigationBarItem(
                                        selected = uiState.currentTab == MainTab.INSIGHTS,
                                        onClick = { viewModel.selectTab(MainTab.INSIGHTS) },
                                        icon = {
                                            Icon(
                                                imageVector = if (uiState.currentTab == MainTab.INSIGHTS) Icons.Filled.BarChart else Icons.Outlined.BarChart,
                                                contentDescription = Localization.get("nav_insights", isHindi)
                                            )
                                        },
                                        label = { Text(Localization.get("nav_insights", isHindi)) },
                                        modifier = Modifier.testTag("nav_tab_insights")
                                    )

                                    NavigationBarItem(
                                        selected = uiState.currentTab == MainTab.LEARN,
                                        onClick = { viewModel.selectTab(MainTab.LEARN) },
                                        icon = {
                                            Icon(
                                                imageVector = if (uiState.currentTab == MainTab.LEARN) Icons.Filled.Lightbulb else Icons.Outlined.Lightbulb,
                                                contentDescription = Localization.get("nav_learn", isHindi)
                                            )
                                        },
                                        label = { Text(Localization.get("nav_learn", isHindi)) },
                                        modifier = Modifier.testTag("nav_tab_learn")
                                    )

                                    NavigationBarItem(
                                        selected = uiState.currentTab == MainTab.PROFILE,
                                        onClick = { viewModel.selectTab(MainTab.PROFILE) },
                                        icon = {
                                            Icon(
                                                imageVector = if (uiState.currentTab == MainTab.PROFILE) Icons.Filled.Person else Icons.Outlined.Person,
                                                contentDescription = Localization.get("nav_profile", isHindi)
                                            )
                                        },
                                        label = { Text(Localization.get("nav_profile", isHindi)) },
                                        modifier = Modifier.testTag("nav_tab_profile")
                                    )
                                }
                            }
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            if (uiState.selectedHabitDetailId != null) {
                                HabitDetailScreen(
                                    habitId = uiState.selectedHabitDetailId!!,
                                    uiState = uiState,
                                    viewModel = viewModel,
                                    onBack = { viewModel.closeHabitDetail() }
                                )
                            } else {
                                AnimatedContent(
                                    targetState = uiState.currentTab,
                                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                                    label = "tab_transition"
                                ) { tab ->
                                    when (tab) {
                                        MainTab.HOME -> HomeScreen(uiState = uiState, viewModel = viewModel)
                                        MainTab.TRACKER -> TrackerScreen(uiState = uiState, viewModel = viewModel)
                                        MainTab.INSIGHTS -> InsightsScreen(uiState = uiState, viewModel = viewModel)
                                        MainTab.LEARN -> LearnScreen(uiState = uiState, viewModel = viewModel)
                                        MainTab.PROFILE -> ProfileScreen(uiState = uiState, viewModel = viewModel)
                                    }
                                }
                            }

                            // Add / Edit Habit Dialog
                            if (uiState.isAddEditHabitDialogOpen) {
                                AddEditHabitDialog(
                                    habitToEdit = uiState.habitToEdit,
                                    isHindi = isHindi,
                                    onDismiss = { viewModel.closeAddEditHabitDialog() },
                                    onSave = { habit -> viewModel.saveHabit(habit) }
                                )
                            }

                            // Log Session Dialog (Reading, Exercise, Finance)
                            if (uiState.activeLogSessionHabit != null) {
                                LogSessionDialog(
                                    habit = uiState.activeLogSessionHabit!!,
                                    isHindi = isHindi,
                                    onDismiss = { viewModel.closeLogSessionDialog() },
                                    onLogReading = { title, pages, mins, notes ->
                                        viewModel.logReadingSession(
                                            uiState.activeLogSessionHabit!!.id,
                                            title,
                                            pages,
                                            mins,
                                            notes
                                        )
                                    },
                                    onLogExercise = { type, mins, notes ->
                                        viewModel.logExerciseSession(
                                            uiState.activeLogSessionHabit!!.id,
                                            type,
                                            mins,
                                            notes
                                        )
                                    },
                                    onLogFinance = { topic, mins, keyLesson ->
                                        viewModel.logFinanceSession(
                                            uiState.activeLogSessionHabit!!.id,
                                            topic,
                                            mins,
                                            keyLesson
                                        )
                                    }
                                )
                            }

                            // Delete Habit Confirmation Dialog
                            if (uiState.habitToDelete != null) {
                                DeleteHabitConfirmDialog(
                                    habit = uiState.habitToDelete!!,
                                    isHindi = isHindi,
                                    onDismiss = { viewModel.dismissDeleteDialog() },
                                    onConfirmDelete = { viewModel.confirmDeleteHabit() },
                                    onArchiveInstead = {
                                        viewModel.archiveHabit(uiState.habitToDelete!!.id, true)
                                        viewModel.dismissDeleteDialog()
                                    }
                                )
                            }

                            // Reset Confirmation Dialog
                            if (uiState.isResetConfirmOpen) {
                                ResetDataConfirmDialog(
                                    isHindi = isHindi,
                                    onDismiss = { viewModel.closeResetDialog() },
                                    onConfirmReset = { viewModel.confirmResetAllData() }
                                )
                            }

                            // Export Data Dialog
                            if (uiState.isExportDialogOpen) {
                                ExportDataDialog(
                                    jsonString = uiState.exportedJsonString,
                                    isHindi = isHindi,
                                    onDismiss = { viewModel.closeExportDialog() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
