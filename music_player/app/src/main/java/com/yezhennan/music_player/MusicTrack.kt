package com.yezhennan.music_player

data class MusicTrack(
    val id: Long,
    val title: String,
    val artist: String,
    val duration: Long,
    val uri: String
)
