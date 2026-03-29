package com.example.votingsystem.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.votingsystem.model.BallotViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BallotScreen(viewModel: BallotViewModel = viewModel(),onVoteComplete:() -> Unit = {}){

    val ballotState by viewModel.ballotState.collectAsState()
    val selectedCandidates by viewModel.selectedCandidates.collectAsState()
    val submitState by viewModel.submitState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadBallot()
    }
//Handle Successful submission

    LaunchedEffect(submitState) {
        if (submitState is BallotViewModel.SubmitState.Success) {
            onVoteComplete()

        }
    }

    Scaffold (topBar = { TopAppBar(title = { Text("Cast Your Vote") },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,))

    }, bottomBar = {
        SubmitBar(submitState = submitState,
            onSubmit = { viewModel.submitVote() },
            enabled = selectedCandidates.isNotEmpty())
    }
        ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (ballotState) {
                is BallotViewModel.BallotUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }
        }
    }


}
