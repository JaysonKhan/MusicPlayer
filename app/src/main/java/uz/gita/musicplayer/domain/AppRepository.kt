package uz.gita.musicplayer.domain

import android.database.Cursor
import kotlinx.coroutines.flow.Flow
import uz.gita.musicplayer.data.local.entity.MusicEntity
import uz.gita.musicplayer.data.model.MusicData

interface AppRepository {
    fun addToFavourite(musicEntity: MusicEntity)
    fun removeFromFavourite(musicEntity: MusicEntity)

    fun getFavouriteMusics(): Cursor

    fun checkSavedMusic(musicData: MusicData): Flow<MusicEntity?>

    fun getAllMusics(): Flow<List<MusicData>>
}