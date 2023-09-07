package uz.gita.musicplayer.ui.screens.b_home.direction

import uz.gita.musicplayer.navidation.AppNavigator
import uz.gita.musicplayer.ui.screens.c_readmusic.ReadMusicScreen
import javax.inject.Inject

class HomeDirectionImpl @Inject constructor(
    private val navigator: AppNavigator
):HomeDirection {
    override suspend fun navigateToREadMusic() {
        navigator.navigateTo(ReadMusicScreen())
    }
    override suspend fun openPlayScreen() {
        navigator.navigateTo(ReadMusicScreen())
    }

}