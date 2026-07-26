package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.CompletionStatus
import com.example.data.GameEntity
import com.example.data.GameMetadataLookup
import com.example.data.GameRepository
import com.example.data.OnlineGameSearchResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class SortOption(val label: String) {
    RECENT("Recently Updated"),
    TITLE("Title (A-Z)"),
    RATING("User Rating"),
    OPENTCRITIC("OpenCritic Score"),
    HLTB_MAIN("HLTB Main Story Length"),
    RELEASE_YEAR("Release Year")
}

private data class FilterState(
    val query: String,
    val platform: String?,
    val genre: String?,
    val status: String?,
    val sort: SortOption
)

class GameViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = GameRepository(application)

    val isCloudSyncing = repository.isCloudSyncing
    val isOfflineMode = repository.isOfflineMode
    val lastSyncTimestamp = repository.lastSyncTimestamp

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedPlatform = MutableStateFlow<String?>(null)
    val selectedPlatform: StateFlow<String?> = _selectedPlatform.asStateFlow()

    private val _selectedGenre = MutableStateFlow<String?>(null)
    val selectedGenre: StateFlow<String?> = _selectedGenre.asStateFlow()

    private val _selectedStatus = MutableStateFlow<String?>(null)
    val selectedStatus: StateFlow<String?> = _selectedStatus.asStateFlow()

    private val _sortOption = MutableStateFlow(SortOption.RECENT)
    val sortOption: StateFlow<SortOption> = _sortOption.asStateFlow()

    private val _searchResultsOnline = MutableStateFlow<List<OnlineGameSearchResult>>(emptyList())
    val searchResultsOnline: StateFlow<List<OnlineGameSearchResult>> = _searchResultsOnline.asStateFlow()

    private val _isSearchingOnline = MutableStateFlow(false)
    val isSearchingOnline: StateFlow<Boolean> = _isSearchingOnline.asStateFlow()

    val allGames: StateFlow<List<GameEntity>> = repository.allGames
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val filterState = combine(
        _searchQuery,
        _selectedPlatform,
        _selectedGenre,
        _selectedStatus,
        _sortOption
    ) { query, platform, genre, status, sort ->
        FilterState(query, platform, genre, status, sort)
    }

    val filteredGames: StateFlow<List<GameEntity>> = combine(
        repository.allGames,
        filterState
    ) { games, filter ->
        games.filter { game ->
            val matchesQuery = filter.query.isBlank() ||
                    game.title.contains(filter.query, ignoreCase = true) ||
                    game.genre.contains(filter.query, ignoreCase = true) ||
                    game.platform.contains(filter.query, ignoreCase = true)

            val matchesPlatform = filter.platform == null || game.platform.equals(filter.platform, ignoreCase = true)
            val matchesGenre = filter.genre == null || game.genre.equals(filter.genre, ignoreCase = true)
            val matchesStatus = filter.status == null || game.completionStatus.equals(filter.status, ignoreCase = true)

            matchesQuery && matchesPlatform && matchesGenre && matchesStatus
        }.sortedWith { a, b ->
            when (filter.sort) {
                SortOption.RECENT -> b.lastUpdated.compareTo(a.lastUpdated)
                SortOption.TITLE -> a.title.lowercase().compareTo(b.title.lowercase())
                SortOption.RATING -> b.userRating.compareTo(a.userRating)
                SortOption.OPENTCRITIC -> b.openCriticScore.compareTo(a.openCriticScore)
                SortOption.HLTB_MAIN -> a.hltbMainHours.compareTo(b.hltbMainHours)
                SortOption.RELEASE_YEAR -> b.releaseYear.compareTo(a.releaseYear)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedPlatform(platform: String?) {
        _selectedPlatform.value = if (_selectedPlatform.value == platform) null else platform
    }

    fun setSelectedGenre(genre: String?) {
        _selectedGenre.value = if (_selectedGenre.value == genre) null else genre
    }

    fun setSelectedStatus(status: String?) {
        _selectedStatus.value = if (_selectedStatus.value == status) null else status
    }

    fun setSortOption(sort: SortOption) {
        _sortOption.value = sort
    }

    fun searchOnline(query: String) {
        if (query.isBlank()) {
            _searchResultsOnline.value = emptyList()
            return
        }
        viewModelScope.launch {
            _isSearchingOnline.value = true
            val results = GameMetadataLookup.searchGamesOnline(query)
            _searchResultsOnline.value = results
            _isSearchingOnline.value = false
        }
    }

    fun addGame(game: GameEntity) {
        viewModelScope.launch {
            repository.addGame(game)
        }
    }

    fun addFromOnlineSearchResult(result: OnlineGameSearchResult, status: String = CompletionStatus.BACKLOG.name) {
        val newGame = GameEntity(
            title = result.title,
            platform = result.platform,
            genre = result.genre,
            releaseYear = result.releaseYear,
            completionStatus = status,
            coverUrl = result.coverUrl,
            hltbMainHours = result.hltbMainHours,
            hltbExtraHours = result.hltbExtraHours,
            hltbCompletionistHours = result.hltbCompletionistHours,
            openCriticScore = result.openCriticScore,
            openCriticTier = result.openCriticTier
        )
        addGame(newGame)
    }

    fun updateGame(game: GameEntity) {
        viewModelScope.launch {
            repository.updateGame(game)
        }
    }

    fun deleteGame(id: String) {
        viewModelScope.launch {
            repository.deleteGame(id)
        }
    }

    fun incrementPlaytime(game: GameEntity, hours: Float) {
        val updated = game.copy(userPlaytimeHours = (game.userPlaytimeHours + hours).coerceAtLeast(0f))
        updateGame(updated)
    }

    fun refetchStats(game: GameEntity) {
        viewModelScope.launch {
            val fetched = GameMetadataLookup.searchGamesOnline(game.title).firstOrNull()
            if (fetched != null) {
                val updated = game.copy(
                    hltbMainHours = fetched.hltbMainHours,
                    hltbExtraHours = fetched.hltbExtraHours,
                    hltbCompletionistHours = fetched.hltbCompletionistHours,
                    openCriticScore = fetched.openCriticScore,
                    openCriticTier = fetched.openCriticTier,
                    coverUrl = if (game.coverUrl.isBlank()) fetched.coverUrl else game.coverUrl
                )
                repository.updateGame(updated)
            }
        }
    }

    fun toggleOfflineMode() {
        repository.setOfflineMode(!isOfflineMode.value)
    }

    fun syncNow() {
        viewModelScope.launch {
            repository.syncWithCloud()
        }
    }
}
