package com.sigiri.teleparty

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.sigiri.teleparty.ui.navigation.AppNavigation
import com.sigiri.teleparty.ui.theme.TelePartyTheme

/**
 * Main activity for the TeleParty application
 * Hosts the navigation system and handles app-level setup
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TelePartyTheme {
                AppNavigation()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppPreview() {
    TelePartyTheme {
        AppNavigation()
    }
}