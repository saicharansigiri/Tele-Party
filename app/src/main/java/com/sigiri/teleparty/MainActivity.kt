package com.sigiri.teleparty

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.sigiri.teleparty.ui.screens.PlayerScreen
import com.sigiri.teleparty.ui.theme.TelePartyTheme
import dagger.hilt.android.AndroidEntryPoint


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