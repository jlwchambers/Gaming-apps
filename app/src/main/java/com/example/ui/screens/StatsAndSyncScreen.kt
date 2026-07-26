package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CompletionStatus
import com.example.data.GameEntity
import com.example.ui.theme.GamingAmber
import com.example.ui.theme.GamingCyan
import com.example.ui.theme.GamingEmerald
import com.example.ui.theme.GamingRose
import com.example.ui.theme.GamingViolet
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun StatsAndSyncScreen(
    games: List<GameEntity>,
    isOffline: Boolean,
    isCloudSyncing: Boolean,
    lastSyncTime: Long,
    onToggleOffline: () -> Unit,
    onSyncNow: () -> Unit
) {
    val totalGames = games.size
    val completedGames = games.count { it.completionStatus == CompletionStatus.COMPLETED.name }
    val playingGames = games.count { it.completionStatus == CompletionStatus.PLAYING.name }
    val backlogGames = games.count { it.completionStatus == CompletionStatus.BACKLOG.name }

    val totalPlayedHours = games.sumOf { it.userPlaytimeHours.toDouble() }
    val totalHltbBacklogHours = games
        .filter { it.completionStatus == CompletionStatus.BACKLOG.name || it.completionStatus == CompletionStatus.PLAYING.name }
        .sumOf { it.hltbMainHours.toDouble() }

    val completionRate = if (totalGames > 0) (completedGames.toFloat() / totalGames.toFloat()) else 0f

    // Platform breakdown map
    val platformCounts = games.groupBy { it.platform }.mapValues { it.value.size }

    // Genre breakdown map
    val genreCounts = games.groupBy { it.genre }.mapValues { it.value.size }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("stats_and_sync_screen")
    ) {
        Text(
            text = "Backlog Analytics & Cloud Sync",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Cloud Database & Offline Mode Control Panel
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isOffline) Icons.Default.FlightTakeoff else Icons.Default.CloudDone,
                            contentDescription = null,
                            tint = if (isOffline) GamingAmber else GamingEmerald,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = if (isOffline) "Offline Travel Mode Active" else "Cloud Sync Enabled",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isOffline) "Using Room SQLite Local DB (Offline First)" else "Synced with Firestore Cloud Database",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Switch(
                        checked = isOffline,
                        onCheckedChange = { onToggleOffline() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = GamingAmber,
                            checkedTrackColor = GamingAmber.copy(alpha = 0.3f),
                            uncheckedThumbColor = GamingEmerald,
                            uncheckedTrackColor = GamingEmerald.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.testTag("offline_mode_switch")
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val dateFormatted = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(Date(lastSyncTime))
                    Text(
                        text = "Last cloud sync: $dateFormatted",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Button(
                        onClick = onSyncNow,
                        enabled = !isOffline && !isCloudSyncing,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GamingViolet),
                        modifier = Modifier.testTag("force_sync_button")
                    ) {
                        if (isCloudSyncing) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp))
                        } else {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Sync Now", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Backlog Completion Progress Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Backlog Completion Rate",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${(completionRate * 100).toInt()}% Finished",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = GamingEmerald
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { completionRate },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .clip(CircleShape),
                    color = GamingEmerald,
                    trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    StatPill("Total Games", "$totalGames", GamingViolet)
                    StatPill("Completed", "$completedGames", GamingEmerald)
                    StatPill("Playing", "$playingGames", GamingCyan)
                    StatPill("Backlog", "$backlogGames", GamingAmber)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Time Distribution Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Schedule, contentDescription = null, tint = GamingCyan)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Playtime & Estimated HLTB Hours",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Hours Logged Played", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${totalPlayedHours.toInt()} hrs", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = GamingCyan)
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text("Backlog Time Needed", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("~${totalHltbBacklogHours.toInt()} hrs", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = GamingViolet)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Platform Breakdown Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Games by Platform",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                platformCounts.forEach { (platform, count) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = platform, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                        Box(
                            modifier = Modifier
                                .background(GamingViolet.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 2.dp)
                        ) {
                            Text(text = "$count games", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GamingViolet)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun StatPill(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = color)
        Text(text = label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
