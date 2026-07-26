package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.CompletionStatus
import com.example.data.GameEntity
import com.example.data.Genres
import com.example.data.Platforms
import com.example.ui.theme.GamingAmber
import com.example.ui.theme.GamingCyan
import com.example.ui.theme.GamingEmerald
import com.example.ui.theme.GamingRose
import com.example.ui.theme.GamingViolet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameDetailBottomSheet(
    game: GameEntity,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onUpdateGame: (GameEntity) -> Unit,
    onDeleteGame: (String) -> Unit,
    onRefetchStats: (GameEntity) -> Unit
) {
    var status by remember(game) { mutableStateOf(game.completionStatus) }
    var userRating by remember(game) { mutableFloatStateOf(game.userRating) }
    var userNotes by remember(game) { mutableStateOf(game.userNotes) }
    var playtime by remember(game) { mutableFloatStateOf(game.userPlaytimeHours) }
    var platform by remember(game) { mutableStateOf(game.platform) }
    var genre by remember(game) { mutableStateOf(game.genre) }
    var isFavorite by remember(game) { mutableStateOf(game.isFavorite) }

    var statusExpanded by remember { mutableStateOf(false) }
    var platformExpanded by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.background,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .testTag("game_detail_sheet")
        ) {
            // Header backdrop with cover image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                if (game.coverUrl.isNotBlank()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(game.coverUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = game.title,
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = ContentScale.Crop
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.5f),
                                    MaterialTheme.colorScheme.background
                                )
                            )
                        )
                )

                // Top Actions Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            isFavorite = !isFavorite
                            onUpdateGame(game.copy(isFavorite = isFavorite))
                        },
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                            .size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isFavorite) GamingRose else Color.White
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                            .size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White
                        )
                    }
                }

                // Title & Release Year over backdrop bottom
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Text(
                        text = game.title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Released ${game.releaseYear}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = GamingViolet,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Status Dropdown & Refetch Stats Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ExposedDropdownMenuBox(
                        expanded = statusExpanded,
                        onExpandedChange = { statusExpanded = !statusExpanded },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = status,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Completion Status") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                                .testTag("status_selector"),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GamingViolet,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = statusExpanded,
                            onDismissRequest = { statusExpanded = false }
                        ) {
                            CompletionStatus.values().forEach { st ->
                                DropdownMenuItem(
                                    text = { Text(st.name) },
                                    onClick = {
                                        status = st.name
                                        statusExpanded = false
                                        onUpdateGame(game.copy(completionStatus = st.name))
                                    }
                                )
                            }
                        }
                    }

                    OutlinedButton(
                        onClick = { onRefetchStats(game) },
                        modifier = Modifier
                            .height(56.dp)
                            .testTag("refetch_stats_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = GamingCyan
                        )
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Refetch Stats")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Sync Stats", fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // HowLongToBeat Breakdown Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Schedule, contentDescription = null, tint = GamingCyan)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "How Long To Beat (Approx. Length)",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            HltbMetricBox("Main Story", "${game.hltbMainHours.toInt()} hrs", GamingCyan, Modifier.weight(1f))
                            Spacer(modifier = Modifier.width(8.dp))
                            HltbMetricBox("Main + Extra", "${game.hltbExtraHours.toInt()} hrs", GamingViolet, Modifier.weight(1f))
                            Spacer(modifier = Modifier.width(8.dp))
                            HltbMetricBox("Completionist", "${game.hltbCompletionistHours.toInt()} hrs", GamingRose, Modifier.weight(1f))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // OpenCritic Rating Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "OpenCritic Rating",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Tier: ${game.openCriticTier}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = GamingAmber
                            )
                        }

                        Box(
                            modifier = Modifier
                                .background(
                                    when {
                                        game.openCriticScore >= 90 -> GamingEmerald
                                        game.openCriticScore >= 80 -> Color(0xFF3B82F6)
                                        else -> GamingAmber
                                    },
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "${game.openCriticScore} / 100",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Playtime Tracker
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Your Playtime",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${playtime.toInt()} hours played",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = GamingCyan
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = {
                                        playtime += 1f
                                        onUpdateGame(game.copy(userPlaytimeHours = playtime))
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = GamingCyan)
                                ) {
                                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Text(" 1h", fontSize = 12.sp)
                                }

                                Button(
                                    onClick = {
                                        playtime += 5f
                                        onUpdateGame(game.copy(userPlaytimeHours = playtime))
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = GamingViolet)
                                ) {
                                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Text(" 5h", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // User Rating Slider / Stars
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Your Rating",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = GamingAmber)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = String.format("%.1f / 10", userRating),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = GamingAmber
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Slider(
                        value = userRating,
                        onValueChange = {
                            userRating = (it * 2).toInt() / 2f // snap to 0.5 steps
                        },
                        onValueChangeFinished = {
                            onUpdateGame(game.copy(userRating = userRating))
                        },
                        valueRange = 0f..10f,
                        steps = 19,
                        colors = SliderDefaults.colors(
                            thumbColor = GamingAmber,
                            activeTrackColor = GamingAmber,
                            inactiveTrackColor = MaterialTheme.colorScheme.outline
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Personal Notes Field
                OutlinedTextField(
                    value = userNotes,
                    onValueChange = {
                        userNotes = it
                    },
                    label = { Text("Personal Notes & Journal") },
                    placeholder = { Text("Write your thoughts, Steam Deck settings, or boss strategies...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .testTag("personal_notes_input"),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GamingViolet,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Save Notes & Delete Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            onDeleteGame(game.id)
                            onDismiss()
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = GamingRose),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Delete")
                    }

                    Button(
                        onClick = {
                            onUpdateGame(
                                game.copy(
                                    completionStatus = status,
                                    userRating = userRating,
                                    userNotes = userNotes,
                                    userPlaytimeHours = playtime,
                                    isFavorite = isFavorite
                                )
                            )
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GamingViolet),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(2f)
                            .testTag("save_game_details_button")
                    ) {
                        Text("Save Changes", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun HltbMetricBox(label: String, hours: String, color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(color.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(8.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text(text = label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = hours, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = color)
        }
    }
}
