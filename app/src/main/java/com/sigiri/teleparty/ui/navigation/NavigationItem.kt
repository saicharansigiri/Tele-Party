package com.sigiri.teleparty.ui.navigation

/**
 * Represents a navigation destination in the bottom navigation bar
 */
sealed class NavigationItem(
    val route: String,
    val title: String,
    val icon: Int
) {
    /**
     * Player screen navigation item
     */
    data object Player : NavigationItem(
        route = "player",
        title = "Player",
        icon = com.sigiri.teleparty.R.drawable.ic_video_lib
    )
    
    /**
     * Metadata screen navigation item
     */
    data object Metadata : NavigationItem(
        route = "metadata",
        title = "Metadata",
        icon = com.sigiri.teleparty.R.drawable.ic_meta_data
    )
}
