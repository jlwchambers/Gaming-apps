package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

enum class CompletionStatus(val label: String, val iconName: String) {
    BACKLOG("Backlog", "Bookmar"),
    PLAYING("Playing", "PlayArrow"),
    COMPLETED("Completed", "CheckCircle"),
    ABANDONED("Abandoned", "Cancel"),
    WISHLIST("Wishlist", "Star")
}

object Platforms {
    val ALL = listOf(
        "PC",
        "PlayStation 5",
        "PlayStation 4",
        "Xbox Series X/S",
        "Nintendo Switch",
        "Steam Deck",
        "Retro / Classic",
        "Mobile"
    )
}

object Genres {
    val ALL = listOf(
        "Action RPG",
        "Open World",
        "FPS / Shooter",
        "Platformer",
        "Strategy / Sim",
        "Indie",
        "Horror",
        "Adventure",
        "Fighting",
        "Puzzle",
        "JRPG"
    )
}

@Entity(tableName = "games")
data class GameEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val platform: String,
    val genre: String,
    val releaseYear: Int,
    val completionStatus: String = CompletionStatus.BACKLOG.name,
    val coverUrl: String = "",
    val hltbMainHours: Float = 0f,
    val hltbExtraHours: Float = 0f,
    val hltbCompletionistHours: Float = 0f,
    val openCriticScore: Int = 0,
    val openCriticTier: String = "Strong",
    val userRating: Float = 0f, // 0.0 to 10.0
    val userNotes: String = "",
    val userPlaytimeHours: Float = 0f,
    val isFavorite: Boolean = false,
    val lastUpdated: Long = System.currentTimeMillis(),
    val syncedWithCloud: Boolean = false
)
