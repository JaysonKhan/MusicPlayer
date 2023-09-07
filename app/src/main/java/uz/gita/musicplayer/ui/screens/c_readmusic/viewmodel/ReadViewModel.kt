package uz.gita.musicplayer.ui.screens.c_readmusic.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import uz.gita.musicplayer.data.model.CommandEnum
import uz.gita.musicplayer.domain.AppRepository
import uz.gita.musicplayer.ui.screens.c_readmusic.ReadMusicContract
import uz.gita.musicplayer.ui.screens.c_readmusic.direction.ReadDirection
import javax.inject.Inject

@HiltViewModel
class ReadViewModel @Inject constructor(
    private val repository: AppRepository,
    private val direction:ReadDirection
) : ReadMusicContract.ViewModel, ViewModel() {
    override val container =
        container<ReadMusicContract.UIState, ReadMusicContract.SideEffect>(ReadMusicContract.UIState.UpdateState)

    override fun onEventDispatcher(intent: ReadMusicContract.Intent) {
        when (intent) {
            is ReadMusicContract.Intent.UserAction -> {
                viewModelScope.launch {
                    intent {
                        postSideEffect(ReadMusicContract.SideEffect.UserAction(intent.actionEnum))
                    }
                }
            }
            ReadMusicContract.Intent.Back -> {
                viewModelScope.launch {
                    direction.back()
                }
            }

            is ReadMusicContract.Intent.CheckMusic -> {
                repository.checkSavedMusic(intent.musicData).onEach {
                    intent { reduce { ReadMusicContract.UIState.CheckMusic(it != null) } }
                }.launchIn(viewModelScope)
            }


            is ReadMusicContract.Intent.DeleteMusic -> {
                repository.removeFromFavourite(intent.musicData.toEntity())
            }

            is ReadMusicContract.Intent.SaveMusic -> {
                repository.addToFavourite(intent.musicData.toEntity())
            }

            is ReadMusicContract.Intent.IsRepeated -> {
                intent { postSideEffect(ReadMusicContract.SideEffect.UserAction(CommandEnum.IS_REPEATED)) }
            }
        }
    }
}