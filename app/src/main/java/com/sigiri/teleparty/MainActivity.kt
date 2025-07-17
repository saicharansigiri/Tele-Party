package com.sigiri.teleparty

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.sigiri.teleparty.ui.screens.PlayerScreen
import com.sigiri.teleparty.ui.theme.TelePartyTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main activity for the TeleParty application
 * Hosts the navigation system and handles app-level setup
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TelePartyTheme {
                PlayerScreen()
            }
        }
    }
}