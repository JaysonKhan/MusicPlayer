package uz.gita.musicplayer.utils

import android.database.Cursor
import androidx.compose.runtime.State
import kotlinx.coroutines.flow.MutableStateFlow
import uz.gita.musicplayer.data.model.CursorEnum
import uz.gita.musicplayer.data.model.MusicData

object MyEventBus {
    var storagePos = MutableStateFlow(-1)
    var roomPos: Int = -1
    var storageCursor: Cursor? = null
    var roomCursor: Cursor? = null
    var currentCursorEnum: CursorEnum? = null
    var isRepeated = false
    var isRepeatedFlow = MutableStateFlow(false)
    var currentTime = MutableStateFlow(0)
    var isNewMusic:Boolean = true
    val currentMusicData = MutableStateFlow<MusicData?>(null)
    val currentTimeFlow = MutableStateFlow<Int>(0)
    var totalTime: Int = 0
    var isPlaying = MutableStateFlow(false)
}
