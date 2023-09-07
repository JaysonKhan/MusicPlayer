package uz.gita.musicplayer.data.local.entity

import android.graphics.BitmapFactory
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import uz.gita.musicplayer.data.model.MusicData

@Entity(tableName = "musics")
data class MusicEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val artist: String?,
    val title: String?,
    val data: String?,
    val duration: Long,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    val albumArt: ByteArray? = null,
    val storagePosition: Int
) {

    fun toMusicData(): MusicData {
        val albumArtBitmap = albumArt?.let { BitmapFactory.decodeByteArray(it, 0, it.size) }
        return MusicData(
            id = id,
            artist = artist,
            title = title,
            data = data,
            duration = duration,
            albumArt = albumArtBitmap,
            storagePosition = storagePosition
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MusicEntity

        if (id != other.id) return false
        if (artist != other.artist) return false
        if (title != other.title) return false
        if (data != other.data) return false
        if (duration != other.duration) return false
        if (albumArt != null) {
            if (other.albumArt == null) return false
            if (!albumArt.contentEquals(other.albumArt)) return false
        } else if (other.albumArt != null) return false
        if (storagePosition != other.storagePosition) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + (artist?.hashCode() ?: 0)
        result = 31 * result + (title?.hashCode() ?: 0)
        result = 31 * result + (data?.hashCode() ?: 0)
        result = 31 * result + duration.hashCode()
        result = 31 * result + (albumArt?.contentHashCode() ?: 0)
        result = 31 * result + storagePosition
        return result
    }
//    fun toMusicData() =
//        MusicData(id, artist, title, data, duration, imageData., storagePosition = storagePosition)

}
