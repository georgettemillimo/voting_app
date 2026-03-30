package com.example.votingsystem.screens


import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import com.example.votingsystem.DataClasses.*
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.votingsystem.R
import com.example.votingsystem.model.BallotViewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.lazy.items
import com.example.votingsystem.model.BallotUiState
import com.example.votingsystem.model.SubmitState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BallotScreen(
    viewModel: BallotViewModel = viewModel(),
    onVoteComplete: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val ballotState by viewModel.ballotState.collectAsState()
    val selectedCandidates by viewModel.selectedCandidates.collectAsState()
    val submitState by viewModel.submitState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadBallot()
    }

    // Handle submit success
    LaunchedEffect(submitState) {
        if (submitState is SubmitState.Success) {
            onVoteComplete()
        }
    }


    Scaffold(
        modifier = modifier,
        bottomBar = {
            SubmitBar(
                submitState = submitState,
                onSubmit = { viewModel.submitVote() },
                enabled = selectedCandidates.isNotEmpty()
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (ballotState) {
                is BallotUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is BallotUiState.AlreadyVoted -> {
                    AlreadyVotedMessage(
                        message = (ballotState as BallotUiState.AlreadyVoted).message
                    )
                }
                is BallotUiState.Error -> {
                    ErrorMessage(
                        message = (ballotState as BallotUiState.Error).message,
                        onRetry = { viewModel.loadBallot() }
                    )
                }
                is BallotUiState.Success -> {
                    val positions = (ballotState as BallotUiState.Success).positions

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(positions) { position ->
                            PositionCard(
                                position = position,
                                selectedCandidates = selectedCandidates[position.slug] ?: emptyList(),
                                isSelected = { candidateId ->
                                    viewModel.isCandidateSelected(position.slug, candidateId)
                                },
                                onCandidateToggle = { candidateId ->
                                    viewModel.toggleCandidate(position.slug, candidateId, position.maxVote)
                                },
                                onReset = {
                                    viewModel.resetPosition(position.slug)
                                }
                            )
                        }

                        item { Spacer(modifier = Modifier.height(80.dp)) }
                    }
                }
            }
        }
    }
    // Error dialog for submit
    if (submitState is SubmitState.Error) {
        AlertDialog(
            onDismissRequest = { viewModel.resetSubmitState() },
            title = { Text("Error") },
            text = { Text((submitState as SubmitState.Error).message) },
            confirmButton = {
                TextButton(onClick = { viewModel.resetSubmitState() }) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
fun PositionCard(
    position: Position,
    selectedCandidates: List<Int>,
    isSelected: (Int) -> Boolean,
    onCandidateToggle: (Int) -> Unit,
    onReset: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = position.description,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                TextButton(
                    onClick = onReset,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reset",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Reset")
                }
            }

            Text(
                text = position.instruction,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            position.candidates.forEach { candidate ->
                CandidateRow(
                    candidate = candidate,
                    isSelected = isSelected(candidate.id),
                    selectionType = if (position.maxVote > 1) "checkbox" else "radio",
                    onToggle = { onCandidateToggle(candidate.id) }
                )
            }
        }
    }
}

@Composable
fun CandidateRow(
    candidate: Candidate,
    isSelected: Boolean,
    selectionType: String,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Selection indicator (Radio or Checkbox)
        when (selectionType) {
            "radio" -> {
                RadioButton(
                    selected = isSelected,
                    onClick = onToggle
                )
            }
            else -> {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = { onToggle() }
                )
            }
        }

        // Candidate Photo - ROUNDED
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(candidate.photo)
                .crossfade(true)
                .build(),
            contentDescription = candidate.fullname,
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape) // ROUNDED IMAGE
                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(R.drawable.logo),
            error = painterResource(R.drawable.logo)
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Candidate Info
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = candidate.fullname,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )

            // Platform button
            OutlinedButton(
                onClick = { /* Show platform dialog */ },
                modifier = Modifier.padding(top = 4.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Platform", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
fun SubmitBar(
    submitState: SubmitState,
    onSubmit: () -> Unit,
    enabled: Boolean
) {
    Surface(
        shadowElevation = 8.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            when (submitState) {
                is SubmitState.Submitting -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    Button(
                        onClick = onSubmit,
                        enabled = enabled && submitState !is SubmitState.Submitting,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Submit Your Vote")
                    }
                }
            }
        }
    }
}

@Composable
fun AlreadyVotedMessage(message: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ErrorMessage(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}