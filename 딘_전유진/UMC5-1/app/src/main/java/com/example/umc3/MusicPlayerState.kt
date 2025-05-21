package com.example.umc3

object MusicPlayerState {
    var isPlaying: Boolean = false
    var currentPosition: Int = 0
    val totalDuration = 60000

    var listener: (() -> Unit)? = null

    fun togglePlayPause() {
        isPlaying = !isPlaying
        listener?.invoke()
    }

    fun registerListener(action: () -> Unit) {
        listener = action
    }
}
