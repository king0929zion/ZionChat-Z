package io.github.king0929zion.zchat.data.model

data class LeaderboardModel(
    val rank: Int,
    val model: String,
    val score: Int,
    val organization: String,
    val link: String,
)

data class Leaderboard(
    val text: List<LeaderboardModel>,
    val vision: List<LeaderboardModel>,
)
