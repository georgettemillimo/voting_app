package com.example.votingsystem.interfaces.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DrawerHeader(modifier: Modifier = Modifier){
    Box(modifier = modifier.fillMaxWidth().padding(64.dp).background(brush = Brush.verticalGradient(
        listOf(Color(0xFFBBDEFB), Color(0xFF191970))))) {
        Text("Header", style = TextStyle(fontSize = 24.sp))
    }
}

@Composable

fun  DrawerBody(items : List<MenuItem>, onMenuItemClick: (MenuItem) -> Unit, modifier: Modifier = Modifier){
    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        items(items){ item ->
            Row(modifier = Modifier.padding(16.dp)) {
                Icon(imageVector = item.icon,
                    contentDescription = item.title)
            }

            Spacer(modifier = Modifier.width(12.dp))


            Text(text = item.title, style = TextStyle(fontSize = 18.sp))

        }
    }
}


