package uz.gita.musicplayer.domain

import android.database.Cursor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import uz.gita.musicplayer.data.local.dao.MusicDao
import uz.gita.musicplayer.data.local.entity.MusicEntity
import uz.gita.musicplayer.data.model.MusicData
import javax.inject.Inject

class AppRepositoryImpl @Inject constructor(private val dao: MusicDao) : AppRepository {
    override fun addToFavourite(musicEntity: MusicEntity) {
        return dao.addMusic(musicEntity.copy(storagePosition = 1))
    }

    override fun removeFromFavourite(musicEntity: MusicEntity) {
        return dao.deleteMusicSaved(musicEntity)
    }

    override fun getFavouriteMusics(): Cursor = dao.getSavedMusics()

    override fun checkSavedMusic(musicData: MusicData): Flow<MusicEntity?> =
        dao.checkMusicSaved(musicData.data ?: "").flowOn(Dispatchers.IO)


    override fun getAllMusics(): Flow<List<MusicData>> {
        return dao.getAllMusics().map { it.map { it.toMusicData() } }
    }
}