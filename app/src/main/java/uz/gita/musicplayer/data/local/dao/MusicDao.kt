package uz.gita.musicplayer.data.local.dao

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import uz.gita.musicplayer.data.local.entity.MusicEntity

@Dao
interface MusicDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addMusic(musicEntity: MusicEntity)


    @Query("SELECT * FROM musics WHERE data = :data")
    fun checkMusicSaved(data: String):Flow<MusicEntity?>

    @Delete
    fun deleteMusicSaved(musicEntity: MusicEntity)

    @Query("SELECT * FROM musics")
    fun getAllMusics(): Flow<List<MusicEntity>>

    @Query("SELECT * FROM musics")
    fun getSavedMusics():Cursor
}
