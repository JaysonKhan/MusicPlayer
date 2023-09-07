package uz.gita.musicplayer.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import uz.gita.musicplayer.data.local.dao.MusicDao
import uz.gita.musicplayer.data.local.entity.MusicEntity

@Database(entities = [MusicEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getMusicDao(): MusicDao
}