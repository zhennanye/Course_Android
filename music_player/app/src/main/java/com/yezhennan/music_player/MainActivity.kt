package com.yezhennan.music_player

import android.Manifest
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.compose.ui.res.stringResource
import com.yezhennan.music_player.ui.theme.Music_playerTheme
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

class MainActivity : ComponentActivity() {
    private var musicService: MusicService.MusicBinder? = null
    private var bound = false
    private val tracks = mutableStateOf<List<MusicTrack>>(emptyList())
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        if (result.values.any { it }) loadTracks()
    }

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            musicService = service as MusicService.MusicBinder
            bound = true
            musicService?.setPlaylist(tracks.value)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            musicService = null
            bound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Music_playerTheme {
                PlayerScreen(
                    tracks = tracks.value,
                    service = musicService,
                    onTrackSelected = { musicService?.play(it) },
                    onRefresh = ::loadTracks
                )
            }
        }
        requestMediaPermissions()
        val serviceIntent = Intent(this, MusicService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
        bindService(serviceIntent, connection, BIND_AUTO_CREATE)
    }

    override fun onDestroy() {
        if (bound) unbindService(connection)
        super.onDestroy()
    }

    private fun requestMediaPermissions() {
        val permissions = buildList {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                add(Manifest.permission.READ_MEDIA_AUDIO)
                add(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
        if (permissions.any { ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED }) {
            permissionLauncher.launch(permissions.toTypedArray())
        } else {
            loadTracks()
        }
    }

    private fun loadTracks() {
        val result = mutableListOf<MusicTrack>()
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION
        )
        contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            "${MediaStore.Audio.Media.IS_MUSIC} != 0",
            null,
            "${MediaStore.Audio.Media.TITLE} COLLATE NOCASE ASC"
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                result += MusicTrack(
                    id = id,
                    title = cursor.getString(titleColumn),
                    artist = cursor.getString(artistColumn),
                    duration = cursor.getLong(durationColumn),
                    uri = "${MediaStore.Audio.Media.EXTERNAL_CONTENT_URI}/$id"
                )
            }
        }
        tracks.value = result
        musicService?.setPlaylist(result)
    }
}

@androidx.compose.runtime.Composable
private fun PlayerScreen(
    tracks: List<MusicTrack>,
    service: MusicService.MusicBinder?,
    onTrackSelected: (Int) -> Unit,
    onRefresh: () -> Unit
) {
    var position by remember { mutableIntStateOf(0) }
    var duration by remember { mutableIntStateOf(0) }
    var isPlaying by remember { mutableStateOf(false) }
    var currentTitle by remember { mutableStateOf("") }
    val unselectedMusic = stringResource(R.string.unselected_music)

    LaunchedEffect(service, tracks) {
        while (true) {
            position = service?.position() ?: 0
            duration = service?.duration() ?: 0
            isPlaying = service?.isPlaying() == true
            currentTitle = service?.currentTrack()?.title ?: unselectedMusic
            delay(500.milliseconds)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(stringResource(R.string.local_music), style = MaterialTheme.typography.headlineMedium)
        Text(currentTitle, style = MaterialTheme.typography.titleLarge)
        if (duration > 0) {
            Slider(
                value = position.coerceIn(0, duration).toFloat(),
                onValueChange = { position = it.toInt() },
                onValueChangeFinished = { service?.seekTo(position) },
                valueRange = 0f..duration.toFloat(),
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                stringResource(
                    R.string.progress_format,
                    position / 60_000,
                    position / 1_000 % 60,
                    duration / 60_000,
                    duration / 1_000 % 60
                )
            )
        } else {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { service?.previous() }) {
                Icon(
                    Icons.Default.SkipPrevious,
                    contentDescription = stringResource(R.string.previous_track)
                )
            }
            IconButton(onClick = { service?.togglePlayPause() }) {
                Icon(
                    if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = stringResource(
                        if (isPlaying) R.string.pause else R.string.play
                    )
                )
            }
            IconButton(onClick = { service?.stop() }) {
                Icon(Icons.Default.Stop, contentDescription = stringResource(R.string.stop))
            }
            IconButton(onClick = { service?.next() }) {
                Icon(
                    Icons.Default.SkipNext,
                    contentDescription = stringResource(R.string.next_track)
                )
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                stringResource(R.string.track_list),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
            Button(onClick = onRefresh) { Text(stringResource(R.string.refresh)) }
        }
        if (tracks.isEmpty()) {
            Text(stringResource(R.string.no_music_permission))
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                itemsIndexed(tracks) { index, track ->
                    Row(
                        modifier = Modifier.fillMaxWidth().clickable { onTrackSelected(index) }.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(track.title, style = MaterialTheme.typography.bodyLarge)
                            Text(track.artist, style = MaterialTheme.typography.bodySmall)
                        }
                        Text(
                            stringResource(
                                R.string.time_format,
                                track.duration.toInt() / 60_000,
                                track.duration.toInt() / 1_000 % 60
                            )
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
    }
}
