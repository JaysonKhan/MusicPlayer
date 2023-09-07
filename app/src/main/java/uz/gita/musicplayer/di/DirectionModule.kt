package uz.gita.musicplayer.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import uz.gita.musicplayer.ui.screens.b_home.direction.HomeDirection
import uz.gita.musicplayer.ui.screens.b_home.direction.HomeDirectionImpl
import uz.gita.musicplayer.ui.screens.c_readmusic.direction.ReadDirection
import uz.gita.musicplayer.ui.screens.c_readmusic.direction.ReadDirectionImpl

@Module
@InstallIn(ViewModelComponent::class)
interface DirectionModule {

    @Binds
    fun bindMusicListDirection(impl : HomeDirectionImpl) : HomeDirection

    @Binds
    fun bindPlayDirection(impl: ReadDirectionImpl) : ReadDirection
}