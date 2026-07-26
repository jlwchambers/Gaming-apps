package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Query("SELECT * FROM games ORDER BY lastUpdated DESC")
    fun getAllGames(): Flow<List<GameEntity>>

    @Query("SELECT * FROM games WHERE id = :id")
    fun getGameById(id: String): Flow<GameEntity?>

    @Query("SELECT * FROM games WHERE id = :id")
    suspend fun getGameByIdDirect(id: String): GameEntity?

    @Query("SELECT * FROM games WHERE completionStatus = :status ORDER BY title ASC")
    fun getGamesByStatus(status: String): Flow<List<GameEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGame(game: GameEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGames(games: List<GameEntity>)

    @Update
    suspend fun updateGame(game: GameEntity)

    @Delete
    suspend fun deleteGame(game: GameEntity)

    @Query("DELETE FROM games WHERE id = :id")
    suspend fun deleteGameById(id: String)

    @Query("SELECT COUNT(*) FROM games")
    suspend fun getGameCount(): Int

    @Query("SELECT * FROM games WHERE syncedWithCloud = 0")
    suspend fun getUnsyncedGames(): List<GameEntity>
}
