package uz.bellissimo.musicplayer.domain

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uz.gita.musicplayer.navidation.AppNavigator
import uz.gita.musicplayer.navidation.NavigationDispatcher
import uz.gita.musicplayer.navidation.NavigationHandler
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface NavigatorModule {

    @[Binds Singleton]
    fun bindAppNavigator(impl : NavigationDispatcher)  : AppNavigator

    @[Binds Singleton]
    fun bindNavigatorHandler(impl : NavigationDispatcher)  : NavigationHandler

}

