package com.example.votingsystem.screens

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.votingsystem.DataClasses.MenuItem
import com.example.votingsystem.R
import com.example.votingsystem.model.BallotViewModel
import com.example.votingsystem.model.NavDrawerViewmodel
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: NavDrawerViewmodel = viewModel(),  onLogout: () -> Unit) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Ballot ViewModel
    val ballotViewModel: BallotViewModel = viewModel()
    val submitState by ballotViewModel.submitState.collectAsState()

    Button(onClick = onLogout) {
        Text("Logout")
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                DrawerHeader()
                DrawerBody(
                    items = listOf(
                        MenuItem(id = "home", title = "Home", icon = Icons.Default.Home),
                        MenuItem(id = "info", title = "Help", icon = Icons.Default.Info),
                        MenuItem(
                            id = "settings",
                            title = "Settings",
                            icon = Icons.Default.Settings
                        ),
                        MenuItem(
                            id = "Logout",
                            title = "Sign Out",
                            icon = Icons.AutoMirrored.Filled.Logout
                        )
                    ),
                    onMenuItemClick = { menuItem ->
                        when (menuItem.id) {
                            "Logout" -> {
                                scope.launch {
                                    drawerState.close()
                                    viewModel.logout()
                                    onLogout()
                                }

                            }
                            else->{
                                Log.d("Electhub", "Clicked on: ${menuItem.title}")
                                scope.launch {
                                    drawerState.close()
                                }

                            }

                        }
                    }
                )
            }
        },
        content = {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(text = stringResource(R.string.app_name))
                        },
                        navigationIcon = {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu",
                                modifier = Modifier.clickable {
                                    scope.launch {
                                        drawerState.open()
                                    }
                                }
                            )
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            titleContentColor = MaterialTheme.colorScheme.onPrimary,
                            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                        )

                    )
                }
            ) { innerPadding ->
                BallotScreen ( viewModel = ballotViewModel,
                    onVoteComplete = {
                        ballotViewModel.loadBallot()

                    },
                    modifier = Modifier.padding(innerPadding)

                )
            }
        }
    )
}
