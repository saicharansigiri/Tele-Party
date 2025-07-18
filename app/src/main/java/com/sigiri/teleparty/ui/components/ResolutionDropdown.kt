package com.sigiri.teleparty.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ResolutionDropdown(
    resolutions: List<String>,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf(resolutions.firstOrNull().orEmpty()) }

    Box(Modifier.fillMaxWidth().padding(8.dp)) {
        Text(
            text = "Resolution: $selected",
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
                .padding(12.dp)
        )

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            resolutions.forEach { res ->
                DropdownMenuItem(
                    text = { Text(res) },
                    onClick = {
                        selected = res
                        expanded = false
                        onSelected(res)
                    }
                )
            }
        }
    }
}