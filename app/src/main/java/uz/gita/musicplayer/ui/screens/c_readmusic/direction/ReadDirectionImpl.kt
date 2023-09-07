package uz.gita.musicplayer.ui.screens.c_readmusic.direction

import uz.gita.musicplayer.navidation.AppNavigator
import javax.inject.Inject

class ReadDirectionImpl @Inject constructor(
    private val navigator: AppNavigator
)  :ReadDirection {
    override suspend fun back() {
        navigator.back()
    }

}