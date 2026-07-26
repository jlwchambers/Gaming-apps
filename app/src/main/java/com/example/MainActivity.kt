package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.GameEntity
import com.example.ui.GameViewModel
import com.example.ui.components.AddGameDialog
import com.example.ui.components.GameDetailBottomSheet
import com.example.ui.screens.BacklogDashboardScreen
import com.example.ui.screens.OnlineSearchScreen
import com.example.ui.screens.StatsAndSyncScreen
import com.example.ui.theme.GamingBacklogTheme
import com.example.ui.theme.GamingCyan
import com.example.ui.theme.GamingViolet
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val viewModel: GameViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            GamingBacklogTheme {
                val games by viewModel.filteredGames.collectAsStateWithLifecycle()
                val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
                val selectedPlatform by viewModel.selectedPlatform.collectAsStateWithLifecycle()
                val selectedGenre by viewModel.selectedGenre.collectAsStateWithLifecycle()
                val selectedStatus by viewModel.selectedStatus.collectAsStateWithLifecycle()
                val sortOption by viewModel.sortOption.collectAsStateWithLifecycle()

                val searchResultsOnline by viewModel.searchResultsOnline.collectAsStateWithLifecycle()
                val isSearchingOnline by viewModel.isSearchingOnline.collectAsStateWithLifecycle()

                val isOffline by viewModel.isOfflineMode.collectAsStateWithLifecycle()
                val isCloudSyncing by viewModel.isCloudSyncing.collectAsStateWithLifecycle()
                val lastSyncTimestamp by viewModel.lastSyncTimestamp.collectAsStateWithLifecycle()

                var selectedTab by remember { mutableIntStateOf(0) }
                var showAddDialog by remember { mutableStateOf(false) }
                var selectedGameForDetail by remember { mutableStateOf<GameEntity?>(null) }

                val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
                val snackbarHostState = remember { SnackbarHostState() }
                val scope = rememberCoroutineScope()

                Scaffold(
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    bottomBar = {
                        NavigationBar(
                            containerColor = com.example.ui.theme.GeoSurfaceNav,
                            modifier = Modifier.testTag("bottom_navigation_bar")
                        ) {
                            NavigationBarItem(
                                selected = selectedTab == 0,
                                onClick = { selectedTab = 0 },
                                icon = { Icon(imageVector = Icons.Default.Gamepad, contentDescription = "Backlog") },
                                label = { Text("Backlog", fontWeight = FontWeight.Bold) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = androidx.compose.material3.MaterialTheme.colorScheme.onPrimaryContainer,
                                    selectedTextColor = androidx.compose.material3.MaterialTheme.colorScheme.onPrimaryContainer,
                                    indicatorColor = androidx.compose.material3.MaterialTheme.colorScheme.primaryContainer
                                ),
                                modifier = Modifier.testTag("tab_backlog")
                            )

                            NavigationBarItem(
                                selected = selectedTab == 1,
                                onClick = { selectedTab = 1 },
                                icon = { Icon(imageVector = Icons.Default.CloudDownload, contentDescription = "Finder") },
                                label = { Text("Finder", fontWeight = FontWeight.Bold) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = androidx.compose.material3.MaterialTheme.colorScheme.onPrimaryContainer,
                                    selectedTextColor = androidx.compose.material3.MaterialTheme.colorScheme.onPrimaryContainer,
                                    indicatorColor = androidx.compose.material3.MaterialTheme.colorScheme.primaryContainer
                                ),
                                modifier = Modifier.testTag("tab_finder")
                            )

                            NavigationBarItem(
                                selected = selectedTab == 2,
                                onClick = { selectedTab = 2 },
                                icon = { Icon(imageVector = Icons.Default.Analytics, contentDescription = "Stats & Sync") },
                                label = { Text("Stats & Sync", fontWeight = FontWeight.Bold) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = androidx.compose.material3.MaterialTheme.colorScheme.onPrimaryContainer,
                                    selectedTextColor = androidx.compose.material3.MaterialTheme.colorScheme.onPrimaryContainer,
                                    indicatorColor = androidx.compose.material3.MaterialTheme.colorScheme.primaryContainer
                                ),
                                modifier = Modifier.testTag("tab_stats")
                            )
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (selectedTab) {
                            0 -> BacklogDashboardScreen(
                                games = games,
                                searchQuery = searchQuery,
                                onSearchQueryChange = { viewModel.setSearchQuery(it) },
                                selectedPlatform = selectedPlatform,
                                onPlatformSelect = { viewModel.setSelectedPlatform(it) },
                                selectedGenre = selectedGenre,
                                onGenreSelect = { viewModel.setSelectedGenre(it) },
                                selectedStatus = selectedStatus,
                                onStatusSelect = { viewModel.setSelectedStatus(it) },
                                currentSort = sortOption,
                                onSortSelect = { viewModel.setSortOption(it) },
                                onGameClick = { selectedGameForDetail = it },
                                onAddClick = { showAddDialog = true },
                                isOffline = isOffline
                            )

                            1 -> OnlineSearchScreen(
                                results = searchResultsOnline,
                                isSearching = isSearchingOnline,
                                onSearch = { viewModel.searchOnline(it) },
                                onAddGame = { result, status ->
                                    viewModel.addFromOnlineSearchResult(result, status)
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Added ${result.title} to $status!")
                                    }
                                }
                            )

                            2 -> StatsAndSyncScreen(
                                games = viewModel.allGames.collectAsStateWithLifecycle().value,
                                isOffline = isOffline,
                                isCloudSyncing = isCloudSyncing,
                                lastSyncTime = lastSyncTimestamp,
                                onToggleOffline = { viewModel.toggleOfflineMode() },
                                onSyncNow = {
                                    viewModel.syncNow()
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Cloud Sync trigger complete.")
                                    }
                                }
                            )
                        }
                    }

                    // Dialog for manually adding game
                    if (showAddDialog) {
                        AddGameDialog(
                            onDismiss = { showAddDialog = false },
                            onAddGame = { newGame ->
                                viewModel.addGame(newGame)
                                scope.launch {
                                    snackbarHostState.showSnackbar("Added ${newGame.title} to backlog!")
                                }
                            }
                        )
                    }

                    // Bottom sheet for game details, notes, rating, playtime, HLTB & OpenCritic stats
                    selectedGameForDetail?.let { game ->
                        GameDetailBottomSheet(
                            game = game,
                            sheetState = sheetState,
                            onDismiss = { selectedGameForDetail = null },
                            onUpdateGame = { updated ->
                                viewModel.updateGame(updated)
                            },
                            onDeleteGame = { id ->
                                viewModel.deleteGame(id)
                                scope.launch {
                                    snackbarHostState.showSnackbar("Deleted game from backlog")
                                }
                            },
                            onRefetchStats = { g ->
                                viewModel.refetchStats(g)
                                scope.launch {
                                    snackbarHostState.showSnackbar("Syncing latest stats for ${g.title}...")
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
