package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.CompletionStatus
import com.example.data.GameEntity
import com.example.ui.theme.GamingAmber
import com.example.ui.theme.GamingCyan
import com.example.ui.theme.GamingEmerald
import com.example.ui.theme.GamingRose
import com.example.ui.theme.GamingViolet

@Composable
fun GameCard(
    game: GameEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val statusColor = when (game.completionStatus) {
        CompletionStatus.PLAYING.name -> GamingCyan
        CompletionStatus.COMPLETED.name -> GamingEmerald
        CompletionStatus.BACKLOG.name -> GamingViolet
        CompletionStatus.ABANDONED.name -> GamingRose
        else -> GamingAmber
    }

    val openCriticColor = when {
        game.openCriticScore >= 90 -> GamingEmerald
        game.openCriticScore >= 80 -> Color(0xFF3B82F6)
        game.openCriticScore >= 70 -> GamingAmber
        else -> GamingRose
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("game_card_${game.id}")
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
                    )
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Image cover section with gradient overlay and badges
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.6f)
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                if (game.coverUrl.isNotBlank()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(game.coverUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = game.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(GamingViolet.copy(alpha = 0.4f), GamingCyan.copy(alpha = 0.3f))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = game.title.take(1),
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                // Dark gradient overlay for bottom text contrast
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.85f)
                                ),
                                startY = 40f
                            )
                        )
                )

                // Top right favorite icon
                if (game.isFavorite) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .size(28.dp)
                            .background(Color.Black.copy(alpha = 0.6f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Favorite",
                            tint = GamingRose,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Top left OpenCritic score badge
                if (game.openCriticScore > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                            .background(openCriticColor, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${game.openCriticScore}",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 11.sp,
                                color = Color.White
                            )
                            Text(
                                text = " OC",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                }

                // Bottom badges on cover
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Status Badge
                    Box(
                        modifier = Modifier
                            .background(statusColor.copy(alpha = 0.9f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val icon = when (game.completionStatus) {
                                CompletionStatus.PLAYING.name -> Icons.Default.PlayArrow
                                CompletionStatus.COMPLETED.name -> Icons.Default.CheckCircle
                                CompletionStatus.ABANDONED.name -> Icons.Outlined.Cancel
                                else -> Icons.Outlined.BookmarkBorder
                            }
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = game.completionStatus,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    // HLTB Main Story Pill
                    if (game.hltbMainHours > 0) {
                        Box(
                            modifier = Modifier
                                .background(Color.Black.copy(alpha = 0.75f), RoundedCornerShape(6.dp))
                                .border(0.5.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = "HLTB",
                                    tint = GamingCyan,
                                    modifier = Modifier.size(11.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "${game.hltbMainHours.toInt()}h",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            // Card Body Info
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text(
                    text = game.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${game.platform} • ${game.genre}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text = "${game.releaseYear}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = GamingViolet
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Rating & Playtime status row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (game.userRating > 0) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Rating",
                                tint = GamingAmber,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = String.format("%.1f/10", game.userRating),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = GamingAmber
                            )
                        }
                    } else {
                        Text(
                            text = "Unrated",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }

                    if (game.userPlaytimeHours > 0) {
                        Text(
                            text = "Played: ${game.userPlaytimeHours.toInt()}h",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = GamingCyan
                        )
                    }
                }
            }
        }
    }
}
