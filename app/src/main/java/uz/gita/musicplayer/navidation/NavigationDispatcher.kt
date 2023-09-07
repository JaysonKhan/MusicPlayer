package uz.gita.musicplayer.navidation

import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavigationDispatcher @Inject constructor() : AppNavigator, NavigationHandler {
    override val navigatorBuffer = MutableSharedFlow<NavigationArg>()

    private suspend fun navigate(arg: NavigationArg) {
        navigatorBuffer.emit(arg)
    }

    override suspend fun navigateTo(screen: MyScreen) = navigate {
        push(screen)
    }


    override suspend fun back() = navigate {
        pop()
    }

}