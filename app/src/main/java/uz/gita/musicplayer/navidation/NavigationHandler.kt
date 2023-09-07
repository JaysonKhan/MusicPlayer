package uz.gita.musicplayer.navidation

import kotlinx.coroutines.flow.SharedFlow

interface NavigationHandler {
    val navigatorBuffer:SharedFlow<NavigationArg>
}