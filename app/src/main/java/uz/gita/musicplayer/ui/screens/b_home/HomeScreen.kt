package uz.gita.musicplayer.ui.screens.b_home

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.hilt.getViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import uz.gita.musicplayer.R
import uz.gita.musicplayer.data.model.CommandEnum
import uz.gita.musicplayer.data.model.CursorEnum
import uz.gita.musicplayer.navidation.MyScreen
import uz.gita.musicplayer.ui.components.CurrentMusicItemComponent
import uz.gita.musicplayer.ui.components.MusicItemComponent
import uz.gita.musicplayer.ui.screens.b_home.viewmodel.HomeViewModel
import uz.gita.musicplayer.ui.theme.MusicPlayerTheme
import uz.gita.musicplayer.utils.MyEventBus
import uz.gita.musicplayer.utils.checkPermissions
import uz.gita.musicplayer.utils.startMusicService

class HomeScreen : MyScreen() {
    @Composable
    override fun Content() {
        val context = LocalContext.current
        val viewModel: HomeContract.ViewModel = getViewModel<HomeViewModel>()

        viewModel.collectSideEffect { sideEffect ->
            when (sideEffect) {
                is HomeContract.SideEffect.StartMusicService -> {
                    MyEventBus.currentCursorEnum = CursorEnum.STORAGE
                    startMusicService(context, sideEffect.commandEnum)
                }

                HomeContract.SideEffect.OpenPermissionDialog -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        context.checkPermissions(
                            arrayListOf(
                                Manifest.permission.POST_NOTIFICATIONS,
                                Manifest.permission.READ_MEDIA_AUDIO
                            )
                        ) {
                            viewModel.onEventDispatcher(HomeContract.Intent.LoadMusics(context))
                        }
                    } else {
                        context.checkPermissions(
                            arrayListOf(
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            )
                        ) {
                            viewModel.onEventDispatcher(HomeContract.Intent.LoadMusics(context))
                        }
                    }
                }

                HomeContract.SideEffect.PlayMusicService -> {
                    MyEventBus.currentCursorEnum = CursorEnum.STORAGE
                    startMusicService(context, CommandEnum.PLAY)
                }
            }
        }

        MusicPlayerTheme {
            val uiState = viewModel.collectAsState()
            MusicListContent(uiState = uiState, eventListener = viewModel::onEventDispatcher)
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("StateFlowValueCalledInComposition")
    @Composable
    fun MusicListContent(
        uiState: State<HomeContract.UIState>,
        eventListener: (HomeContract.Intent) -> Unit
    ) {

        val scope = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob())
        val loading by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.musicloading))
        val car by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.car))

        when (uiState.value) {
            is HomeContract.UIState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    LottieAnimation(
                        composition = loading,
                        iterations = LottieConstants.IterateForever
                    )
                }
                eventListener(HomeContract.Intent.RequestPermission)

            }

            is HomeContract.UIState.PreparedData -> {
                val bool =
                    MyEventBus.storagePos.collectAsState().value != -1 || MyEventBus.storagePos.collectAsState().value >= MyEventBus.storageCursor!!.count
                val scrollBehavior =
                    TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
                val isCollapsed =
                    remember { derivedStateOf { scrollBehavior.state.collapsedFraction > 0.5 } }

                val collapsed = 20
                val expanded = 32
                val topAppBarTextSize =
                    (collapsed + (expanded - collapsed) * (1 - scrollBehavior.state.collapsedFraction)).sp

                val topAppBarElementColor = if (isCollapsed.value) {
                    Color(0xFF69AADF)
                } else {
                    Color.Black
                }
                Scaffold(
                    topBar = {
                        LargeTopAppBar(
                            title = {
                                Text(text = "My Playlist", fontSize = topAppBarTextSize)
                            },
                            colors = TopAppBarDefaults.largeTopAppBarColors(
                                containerColor = Color(0xFF69AADF),
                                scrolledContainerColor = Color.White,
                                navigationIconContentColor = topAppBarElementColor,
                                titleContentColor = topAppBarElementColor,
                                actionIconContentColor = topAppBarElementColor
                            ),
                            scrollBehavior = scrollBehavior,
                            navigationIcon = {
                                Box(
                                    modifier = Modifier
                                        .width(88.dp)
                                        .height(88.dp)
                                        .padding(16.dp)
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.khan),
                                        contentDescription = null,
                                        tint = if (!isCollapsed.value) Color.White else topAppBarElementColor
                                    )
                                }
                            },
                            windowInsets = TopAppBarDefaults.windowInsets
                        )
                            val animate by animateFloatAsState(targetValue = if (!isCollapsed.value) 300f else 0f, animationSpec = tween(800))
                            Box(modifier = Modifier
                                .fillMaxWidth()
                                .height(animate.dp)
                                .graphicsLayer {
                                    alpha = animate / 300
                                    translationX = 500f - animate
                                }, contentAlignment = Alignment.CenterEnd) {
                                LottieAnimation(
                                    composition = car,
                                    iterations = LottieConstants.IterateForever,
                                    speed = if (MyEventBus.isPlaying.collectAsState().value) 1f else 0f
                                )
                            }
                    },
                    modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                    bottomBar = {
                        if (bool) {
                            CurrentMusicItemComponent(
                                modifier = Modifier
                                    .background(Color(0xFF69AADF))
                                    .padding(vertical = 6.dp, horizontal = 16.dp),
                                onClick = { eventListener(HomeContract.Intent.OpenPlayScreen) },
                                eventListener
                            )
                        }
                    }
                ) {
                    val list = (uiState.value as HomeContract.UIState.PreparedData).list
                    LazyColumn(
                        Modifier
                            .fillMaxSize()
                            .padding(it)
                    ) {
                        items(list.size) {
                            MusicItemComponent(
                                musicData = list[it],
                                onClick = {
                                    scope.launch {
                                        MyEventBus.storagePos.emit(it)
                                    }
                                    eventListener.invoke(HomeContract.Intent.PlayMusic)

                                }
                            )
                        }
                    }

                }
            }

        }

    }
}
