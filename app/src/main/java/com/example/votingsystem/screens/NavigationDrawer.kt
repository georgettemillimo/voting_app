package com.example.votingsystem.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.votingsystem.R
import com.example.votingsystem.model.NavDrawerViewmodel
import androidx.compose.foundation.clickable
import com.example.votingsystem.DataClasses.MenuItem

@Composable
fun DrawerHeader(viewModel: NavDrawerViewmodel = viewModel()){
    val username by viewModel.username
    val electionTitle by viewModel.electionTitle

    LaunchedEffect(Unit) {
        viewModel.loadUserData()
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp) // enough space for overlap
    ) {

        // 🔵 Header background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF8BBDEF),
                            Color(0xFF191970)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = electionTitle,
                style = TextStyle(fontSize = 20.sp, color = Color.White, fontWeight = FontWeight.Bold)
            )
        }

        // 🟡 Profile Image (OVERLAY)
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = 10.dp), // pushes it below header
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                painter = painterResource(id = R.drawable.logo), // your image
                contentDescription = "Profile",
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .border(3.dp, Color.White, CircleShape)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = username,
                style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium),
                color = Color.Black // Changed to Black for visibility against white bg
            )
        }
    }
}




@Composable

fun  DrawerBody(items : List<MenuItem>, onMenuItemClick: (MenuItem) -> Unit, modifier: Modifier = Modifier){
    Spacer(modifier = Modifier.height(16.dp))
    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        items(items){ item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onMenuItemClick(item) }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = item.icon,
                    contentDescription = item.title)

                Spacer(modifier = Modifier.width(10.dp))
                Text(text = item.title, style = TextStyle(fontSize = 18.sp))
            }

        }
    }
}



