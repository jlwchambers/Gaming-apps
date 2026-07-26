package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Games
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.CompletionStatus
import com.example.data.GameEntity
import com.example.data.Genres
import com.example.data.Platforms
import com.example.ui.SortOption
import com.example.ui.components.GameCard
import com.example.ui.theme.GamingAmber
import com.example.ui.theme.GamingCyan
import com.example.ui.theme.GamingEmerald
import com.example.ui.theme.GamingViolet

@Composable
fun BacklogDashboardScreen(
    games: List<GameEntity>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedPlatform: String?,
    onPlatformSelect: (String?) -> Unit,
    selectedGenre: String?,
    onGenreSelect: (String?) -> Unit,
    selectedStatus: String?,
    onStatusSelect: (String?) -> Unit,
    currentSort: SortOption,
    onSortSelect: (SortOption) -> Unit,
    onGameClick: (GameEntity) -> Unit,
    onAddClick: () -> Unit,
    isOffline: Boolean
) {
    var sortMenuExpanded by remember { mutableStateOf(false) }

    // Quick Stats Calculation
    val totalBacklogHours = games
        .filter { it.completionStatus == CompletionStatus.BACKLOG.name || it.completionStatus == CompletionStatus.PLAYING.name }
        .sumOf { it.hltbMainHours.toDouble() }
    val completedCount = games.count { it.completionStatus == CompletionStatus.COMPLETED.name }
    val currentlyPlayingGame = games.find { it.completionStatus == CompletionStatus.PLAYING.name }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .padding(bottom = 16.dp, end = 8.dp)
                    .testTag("add_game_fab")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Game")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Prominent Top Search Bar & Sort Button Row
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChange,
                        placeholder = { Text("Filter titles, genres, platforms...") },
                        leadingIcon = { 
                            Icon(
                                imageVector = Icons.Default.Search, 
                                contentDescription = "Search icon",
                                tint = MaterialTheme.colorScheme.primary
                            ) 
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(
                                    onClick = { onSearchQueryChange("") },
                                    modifier = Modifier.testTag("clear_search_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Clear, 
                                        contentDescription = "Clear search"
                                    )
                                }
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("search_games_input"),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        )
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Box {
                        IconButton(
                            onClick = { sortMenuExpanded = true },
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(16.dp))
                                .size(56.dp)
                                .testTag("sort_menu_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Sort, 
                                contentDescription = "Sort", 
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }

                        DropdownMenu(
                            expanded = sortMenuExpanded,
                            onDismissRequest = { sortMenuExpanded = false }
                        ) {
                            SortOption.values().forEach { option ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = option.label,
                                            fontWeight = if (option == currentSort) FontWeight.Bold else FontWeight.Normal,
                                            color = if (option == currentSort) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                        )
                                    },
                                    onClick = {
                                        onSortSelect(option)
                                        sortMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Active Search / Filter State Indicator Banner
                if (searchQuery.isNotBlank() || selectedStatus != null || selectedPlatform != null || selectedGenre != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Matching ${games.size} game${if (games.size != 1) "s" else ""}" + 
                                    if (searchQuery.isNotBlank()) " for \"$searchQuery\"" else "",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Text(
                            text = "Clear filters",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier
                                .clickable {
                                    onSearchQueryChange("")
                                    onStatusSelect(null)
                                    onPlatformSelect(null)
                                    onGenreSelect(null)
                                }
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                                .testTag("clear_all_filters")
                        )
                    }
                }
            }

            // Scrollable Filter Chips Row (Status, Platform, Genre)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // "All" Reset Chip
                val isAllSelected = selectedStatus == null && selectedPlatform == null && selectedGenre == null
                FilterChip(
                    selected = isAllSelected,
                    onClick = {
                        onStatusSelect(null)
                        onPlatformSelect(null)
                        onGenreSelect(null)
                    },
                    label = { Text("All Games", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                // Status Filters
                CompletionStatus.values().forEach { st ->
                    val isSelected = selectedStatus == st.name
                    FilterChip(
                        selected = isSelected,
                        onClick = { onStatusSelect(if (isSelected) null else st.name) },
                        label = { Text(st.name, fontSize = 12.sp, fontWeight = FontWeight.Medium) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                // Platform Filters
                Platforms.ALL.forEach { pl ->
                    val isSelected = selectedPlatform == pl
                    FilterChip(
                        selected = isSelected,
                        onClick = { onPlatformSelect(if (isSelected) null else pl) },
                        label = { Text(pl, fontSize = 12.sp, fontWeight = FontWeight.Medium) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                // Genre Filters
                Genres.ALL.forEach { g ->
                    val isSelected = selectedGenre == g
                    FilterChip(
                        selected = isSelected,
                        onClick = { onGenreSelect(if (isSelected) null else g) },
                        label = { Text(g, fontSize = 12.sp, fontWeight = FontWeight.Medium) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onTertiaryContainer,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Dashboard Banner Header with Stats - Geometric Balance Hero Container
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_game_banner_1785097163874),
                        contentDescription = "Banner",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Black.copy(alpha = 0.4f),
                                        Color.Black.copy(alpha = 0.85f)
                                    )
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Gaming Backlog",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                                Text(
                                    text = if (isOffline) "Offline Mode (Traveling)" else "Cloud Synced",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isOffline) GamingAmber else GamingEmerald
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .background(GamingViolet.copy(alpha = 0.85f), RoundedCornerShape(12.dp))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "${games.size} Games",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = Color.White
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Backlog Hours
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = GamingCyan,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Column {
                                    Text("Backlog Length", fontSize = 10.sp, color = Color.LightGray)
                                    Text(
                                        text = "~${totalBacklogHours.toInt()} hrs",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }

                            // Currently Playing
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.PlayCircleFilled,
                                    contentDescription = null,
                                    tint = GamingCyan,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Column {
                                    Text("Playing Now", fontSize = 10.sp, color = Color.LightGray)
                                    Text(
                                        text = currentlyPlayingGame?.title ?: "None",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        maxLines = 1
                                    )
                                }
                            }

                            // Completed
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Games,
                                    contentDescription = null,
                                    tint = GamingEmerald,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Column {
                                    Text("Completed", fontSize = 10.sp, color = Color.LightGray)
                                    Text(
                                        text = "$completedCount finished",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Main Games Grid
            if (games.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No games found",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Try clearing search filters or add a new title to your backlog!",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 165.dp),
                    contentPadding = PaddingValues(bottom = 80.dp, start = 16.dp, end = 16.dp, top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(games, key = { it.id }) { game ->
                        GameCard(
                            game = game,
                            onClick = { onGameClick(game) }
                        )
                    }
                }
            }
        }
    }
}
