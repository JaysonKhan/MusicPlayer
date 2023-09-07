package uz.gita.musicplayer.ui.screens.b_home

import android.content.Context
import org.orbitmvi.orbit.ContainerHost
import uz.gita.musicplayer.data.model.CommandEnum
import uz.gita.musicplayer.data.model.MusicData

interface HomeContract {
    interface ViewModel : ContainerHost<UIState, SideEffect> {
        fun onEventDispatcher(intent: Intent)
    }

    sealed interface UIState {
        object Loading : UIState
        data class PreparedData(val list: List<MusicData>) : UIState
    }

    sealed interface SideEffect {
        data class StartMusicService(val commandEnum: CommandEnum) : SideEffect
        object PlayMusicService : SideEffect
        object OpenPermissionDialog :SideEffect
    }

    sealed interface Intent {
        object OpenPlayScreen : Intent
        object RequestPermission : Intent
        data class LoadMusics(val context: Context) : Intent
        object PlayMusic : Intent
        object OpenReadMusicScreen : Intent
        data class UserCommand(val commandEnum: CommandEnum) : Intent
    }
}