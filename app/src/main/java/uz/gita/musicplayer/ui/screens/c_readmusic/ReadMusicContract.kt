package uz.gita.musicplayer.ui.screens.c_readmusic

import org.orbitmvi.orbit.ContainerHost
import uz.gita.musicplayer.data.model.CommandEnum
import uz.gita.musicplayer.data.model.MusicData

interface ReadMusicContract {
    interface ViewModel : ContainerHost<UIState, SideEffect> {
        fun onEventDispatcher(intent: Intent)
    }

    sealed interface UIState {
        object UpdateState : UIState
        data class CheckMusic(val isSaved: Boolean) : UIState
    }

    sealed interface SideEffect {
        data class UserAction(val actionEnum: CommandEnum): SideEffect
    }

    sealed interface Intent {
        data class UserAction(val actionEnum: CommandEnum): Intent
        data class SaveMusic(val musicData: MusicData) : Intent
        data class DeleteMusic(val musicData: MusicData) : Intent
        data class CheckMusic(val musicData: MusicData) : Intent
        data class IsRepeated(val isRepeated:Boolean):Intent
        object Back : Intent
    }
}