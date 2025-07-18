package com.sigiri.teleparty.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun ErrorMessage(message: String) {
    Text(
        text = "Error: $message",
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    )
}
