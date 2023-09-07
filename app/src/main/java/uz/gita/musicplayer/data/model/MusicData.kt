package uz.gita.musicplayer.data.model

import android.graphics.Bitmap
import android.net.Uri
import uz.gita.musicplayer.data.local.entity.MusicEntity
import java.io.ByteArrayOutputStream

data class MusicData(
    val id: Int,
    val artist: String?,
    val title: String?,
    val data: String?,
    val duration: Long,
    var albumArt: Bitmap? = null,
    val storagePosition: Int = 0
){


    fun toEntity(): MusicEntity {
        val albumArtByteArray = albumArt?.let {
            val outputStream = ByteArrayOutputStream()
            it.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.toByteArray()
        }

        return MusicEntity(
            id = id,
            artist = artist,
            title = title,
            data = data,
            duration = duration,
            albumArt = albumArtByteArray,
            storagePosition = storagePosition
        )
    }

//    fun toEntity() = MusicEntity(id, artist, title, data, duration, albumArt?.to, storagePosition)
}
