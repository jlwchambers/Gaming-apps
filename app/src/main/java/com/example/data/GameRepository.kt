package com.example.data

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class GameRepository(context: Context) {
    private val database = AppDatabase.getInstance(context)
    private val dao = database.gameDao()

    private val _isCloudSyncing = MutableStateFlow(false)
    val isCloudSyncing: StateFlow<Boolean> = _isCloudSyncing.asStateFlow()

    private val _isOfflineMode = MutableStateFlow(false)
    val isOfflineMode: StateFlow<Boolean> = _isOfflineMode.asStateFlow()

    private val _lastSyncTimestamp = MutableStateFlow(System.currentTimeMillis())
    val lastSyncTimestamp: StateFlow<Long> = _lastSyncTimestamp.asStateFlow()

    val allGames: Flow<List<GameEntity>> = dao.getAllGames()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            // Check if database needs initial prepopulation
            if (dao.getGameCount() == 0) {
                dao.insertGames(PrepopulatedData.sampleGames)
            }
        }
    }

    fun getGameById(id: String): Flow<GameEntity?> = dao.getGameById(id)

    suspend fun addGame(game: GameEntity) {
        dao.insertGame(game.copy(lastUpdated = System.currentTimeMillis(), syncedWithCloud = false))
        syncWithCloud()
    }

    suspend fun updateGame(game: GameEntity) {
        dao.updateGame(game.copy(lastUpdated = System.currentTimeMillis(), syncedWithCloud = false))
        syncWithCloud()
    }

    suspend fun deleteGame(id: String) {
        val game = dao.getGameByIdDirect(id)
        dao.deleteGameById(id)
        if (game != null) {
            deleteFromCloud(id)
        }
    }

    suspend fun syncWithCloud() {
        if (_isOfflineMode.value) return

        _isCloudSyncing.value = true
        try {
            val firestore = FirebaseFirestore.getInstance()
            val unsynced = dao.getUnsyncedGames()

            for (game in unsynced) {
                val gameMap = hashMapOf(
                    "id" to game.id,
                    "title" to game.title,
                    "platform" to game.platform,
                    "genre" to game.genre,
                    "releaseYear" to game.releaseYear,
                    "completionStatus" to game.completionStatus,
                    "coverUrl" to game.coverUrl,
                    "hltbMainHours" to game.hltbMainHours,
                    "hltbExtraHours" to game.hltbExtraHours,
                    "hltbCompletionistHours" to game.hltbCompletionistHours,
                    "openCriticScore" to game.openCriticScore,
                    "openCriticTier" to game.openCriticTier,
                    "userRating" to game.userRating,
                    "userNotes" to game.userNotes,
                    "userPlaytimeHours" to game.userPlaytimeHours,
                    "isFavorite" to game.isFavorite,
                    "lastUpdated" to game.lastUpdated
                )

                firestore.collection("backlog_games")
                    .document(game.id)
                    .set(gameMap)
                    .await()

                dao.updateGame(game.copy(syncedWithCloud = true))
            }
            _lastSyncTimestamp.value = System.currentTimeMillis()
        } catch (e: Exception) {
            e.printStackTrace()
            // Gracefully handle offline / cloud error without crashing
        } finally {
            _isCloudSyncing.value = false
        }
    }

    private suspend fun deleteFromCloud(gameId: String) {
        try {
            val firestore = FirebaseFirestore.getInstance()
            firestore.collection("backlog_games").document(gameId).delete().await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setOfflineMode(offline: Boolean) {
        _isOfflineMode.value = offline
        if (!offline) {
            CoroutineScope(Dispatchers.IO).launch {
                syncWithCloud()
            }
        }
    }
}
