package com.yezhennan.music_player

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

class MusicService : Service() {
    companion object {
        const val ACTION_PLAY = "com.yezhennan.music_player.PLAY"
        const val ACTION_PAUSE = "com.yezhennan.music_player.PAUSE"
        const val ACTION_NEXT = "com.yezhennan.music_player.NEXT"
        const val ACTION_PREVIOUS = "com.yezhennan.music_player.PREVIOUS"
        private const val CHANNEL_ID = "music_playback"
        private const val NOTIFICATION_ID = 1001
    }

    private val binder = MusicBinder()
    private var player: MediaPlayer? = null
    private var playlist: List<MusicTrack> = emptyList()
    private var currentIndex = -1

    inner class MusicBinder : Binder() {
        fun setPlaylist(tracks: List<MusicTrack>) {
            playlist = tracks
            if (currentIndex !in playlist.indices) currentIndex = -1
        }

        fun play(index: Int = currentIndex) {
            if (index !in playlist.indices) return
            currentIndex = index
            playTrack(playlist[index])
        }

        fun togglePlayPause() {
            player?.let { if (it.isPlaying) pause() else resume() } ?: play()
        }

        fun pause() {
            player?.pause()
            updateNotification()
        }

        fun resume() {
            player?.start()
            updateNotification()
        }

        fun stop() {
            player?.stop()
            player?.release()
            player = null
            updateNotification()
        }

        fun next() {
            if (playlist.isNotEmpty()) play((currentIndex + 1).mod(playlist.size))
        }

        fun previous() {
            if (playlist.isNotEmpty()) play((currentIndex - 1 + playlist.size).mod(playlist.size))
        }

        fun seekTo(position: Int) {
            player?.seekTo(position)
        }

        fun currentTrack(): MusicTrack? = playlist.getOrNull(currentIndex)
        fun isPlaying(): Boolean = player?.isPlaying == true
        fun position(): Int = player?.currentPosition ?: 0
        fun duration(): Int = player?.duration ?: (currentTrack()?.duration?.toInt() ?: 0)
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, buildNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PLAY -> binder.togglePlayPause()
            ACTION_PAUSE -> binder.pause()
            ACTION_NEXT -> binder.next()
            ACTION_PREVIOUS -> binder.previous()
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onDestroy() {
        player?.release()
        player = null
        super.onDestroy()
    }

    private fun playTrack(track: MusicTrack) {
        player?.release()
        player = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            setDataSource(this@MusicService, Uri.parse(track.uri))
            setOnPreparedListener { it.start(); updateNotification() }
            setOnCompletionListener { binder.next() }
            setOnErrorListener { _, _, _ ->
                updateNotification()
                true
            }
            prepareAsync()
        }
        updateNotification()
    }

    private fun updateNotification() {
        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(NOTIFICATION_ID, buildNotification())
    }

    private fun buildNotification(): Notification {
        val track = binder.currentTrack()
        val contentIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setContentTitle(track?.title ?: getString(R.string.app_name))
            .setContentText(track?.artist ?: getString(R.string.no_music))
            .setContentIntent(contentIntent)
            .setOngoing(player?.isPlaying == true)
            .setOnlyAlertOnce(true)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getSystemService(NotificationManager::class.java).createNotificationChannel(
                NotificationChannel(
                    CHANNEL_ID,
                    getString(R.string.notification_channel_name),
                    NotificationManager.IMPORTANCE_LOW
                )
            )
        }
    }
}
