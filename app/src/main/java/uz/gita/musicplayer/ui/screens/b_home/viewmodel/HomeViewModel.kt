package uz.gita.musicplayer.ui.screens.b_home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import uz.gita.musicplayer.data.model.MusicData
import uz.gita.musicplayer.ui.screens.b_home.HomeContract
import uz.gita.musicplayer.ui.screens.b_home.direction.HomeDirection
import uz.gita.musicplayer.utils.MyEventBus
import uz.gita.musicplayer.utils.getMusicCursor
import uz.gita.musicplayer.utils.getMusicDataByPosition
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val direction: HomeDirection
):HomeContract.ViewModel, ViewModel() {
    override val container = container<HomeContract.UIState, HomeContract.SideEffect>(HomeContract.UIState.Loading)
    override fun onEventDispatcher(intent: HomeContract.Intent) {
        when(intent){
            is HomeContract.Intent.LoadMusics -> {
                val list = arrayListOf<MusicData>()
                intent {
                    reduce {
                        HomeContract.UIState.Loading
                    }
                }
                intent.context.getMusicCursor().onEach {
                    MyEventBus.storageCursor = it
                    for (pos in 0 until it.count) {
                        list.add(it.getMusicDataByPosition(pos))
                    }
                    intent{
                        reduce {
                            HomeContract.UIState.PreparedData(list)
                        }
                    }
                }.launchIn(viewModelScope)
            }
            is HomeContract.Intent.PlayMusic -> {
                intent {
                    postSideEffect(HomeContract.SideEffect.PlayMusicService)
                }
            }
            is HomeContract.Intent.OpenReadMusicScreen -> {
                viewModelScope.launch {
                    direction.navigateToREadMusic()
                }
            }

            is HomeContract.Intent.UserCommand -> {
                intent {
                    postSideEffect(HomeContract.SideEffect.StartMusicService(intent.commandEnum))
                }
            }

            HomeContract.Intent.RequestPermission -> {
                intent { postSideEffect(HomeContract.SideEffect.OpenPermissionDialog) }
            }

            HomeContract.Intent.OpenPlayScreen ->{
                viewModelScope.launch {
                    direction.openPlayScreen()
                }
            }
        }
    }
}