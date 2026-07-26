package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.data.CompletionStatus
import com.example.data.GameEntity
import com.example.data.GameMetadataLookup
import com.example.data.Genres
import com.example.data.Platforms
import com.example.ui.theme.GamingViolet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGameDialog(
    onDismiss: () -> Unit,
    onAddGame: (GameEntity) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var platform by remember { mutableStateOf(Platforms.ALL.first()) }
    var genre by remember { mutableStateOf(Genres.ALL.first()) }
    var releaseYearStr by remember { mutableStateOf("2024") }
    var status by remember { mutableStateOf(CompletionStatus.BACKLOG.name) }
    var notes by remember { mutableStateOf("") }

    var platformExpanded by remember { mutableStateOf(false) }
    var genreExpanded by remember { mutableStateOf(false) }
    var statusExpanded by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("add_game_dialog")
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Add Game to Backlog",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Title Input
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Game Title *") },
                    placeholder = { Text("e.g. Cyberpunk 2077") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_game_title_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GamingViolet
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Platform Dropdown
                ExposedDropdownMenuBox(
                    expanded = platformExpanded,
                    onExpandedChange = { platformExpanded = !platformExpanded }
                ) {
                    OutlinedTextField(
                        value = platform,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Platform") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = platformExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = platformExpanded,
                        onDismissRequest = { platformExpanded = false }
                    ) {
                        Platforms.ALL.forEach { p ->
                            DropdownMenuItem(
                                text = { Text(p) },
                                onClick = {
                                    platform = p
                                    platformExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Genre Dropdown
                ExposedDropdownMenuBox(
                    expanded = genreExpanded,
                    onExpandedChange = { genreExpanded = !genreExpanded }
                ) {
                    OutlinedTextField(
                        value = genre,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Genre") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = genreExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = genreExpanded,
                        onDismissRequest = { genreExpanded = false }
                    ) {
                        Genres.ALL.forEach { g ->
                            DropdownMenuItem(
                                text = { Text(g) },
                                onClick = {
                                    genre = g
                                    genreExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Status & Release Year Row
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
                            label = { Text("Status") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
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
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = releaseYearStr,
                        onValueChange = { releaseYearStr = it.take(4) },
                        label = { Text("Year") },
                        modifier = Modifier.weight(0.7f),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Personal Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Personal Notes (Optional)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cancel")
                    }

                    Spacer(modifier = Modifier.padding(horizontal = 6.dp))

                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                val year = releaseYearStr.toIntOrNull() ?: 2024
                                val fallback = GameMetadataLookup.generateFallbackMetadata(title)
                                val newGame = GameEntity(
                                    title = title.trim(),
                                    platform = platform,
                                    genre = genre,
                                    releaseYear = year,
                                    completionStatus = status,
                                    userNotes = notes,
                                    coverUrl = fallback.coverUrl,
                                    hltbMainHours = fallback.hltbMainHours,
                                    hltbExtraHours = fallback.hltbExtraHours,
                                    hltbCompletionistHours = fallback.hltbCompletionistHours,
                                    openCriticScore = fallback.openCriticScore,
                                    openCriticTier = fallback.openCriticTier
                                )
                                onAddGame(newGame)
                                onDismiss()
                            }
                        },
                        enabled = title.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = GamingViolet),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("submit_add_game_button")
                    ) {
                        Text("Add Game")
                    }
                }
            }
        }
    }
}
