package uz.gita.musicplayer

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import uz.gita.musicplayer.navidation.NavigationHandler
import uz.gita.musicplayer.ui.screens.b_home.HomeScreen
import uz.gita.musicplayer.ui.screens.c_readmusic.ReadMusicScreen
import uz.gita.musicplayer.ui.theme.MusicPlayerTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var navigatorHandler: NavigationHandler
    @SuppressLint("FlowOperatorInvokedInComposition", "CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MusicPlayerTheme {
                Navigator(screen = HomeScreen(), onBackPressed = {
                    if (it==ReadMusicScreen()){
                        Log.d("AAA", "123456789")
                    }
                    true
                }) { navigator ->
                    navigatorHandler.navigatorBuffer
                        .onEach { it.invoke(navigator) }
                        .launchIn(lifecycleScope)
                    CurrentScreen()
                }
            }
        }
    }
}