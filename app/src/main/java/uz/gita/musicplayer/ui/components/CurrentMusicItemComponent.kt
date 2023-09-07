package uz.gita.musicplayer.ui.components

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import uz.gita.musicplayer.R
import uz.gita.musicplayer.data.model.CommandEnum
import uz.gita.musicplayer.data.model.CursorEnum
import uz.gita.musicplayer.ui.screens.b_home.HomeContract
import uz.gita.musicplayer.utils.MyEventBus
import uz.gita.musicplayer.utils.getMusicDataByPosition

@OptIn(ExperimentalUnitApi::class)
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun CurrentMusicItemComponent(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onEventDispatcher: (HomeContract.Intent) -> Unit,
) {

    val loading by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.musicdisc))

    var musicData = MyEventBus.currentMusicData.collectAsState().value!!

    LaunchedEffect(key1 = MyEventBus.storagePos.value) {
        musicData = MyEventBus.storageCursor!!.getMusicDataByPosition(MyEventBus.storagePos.value)
    }

    val musicIsPlaying = MyEventBus.isPlaying.collectAsState()

    val scrollState = rememberScrollState()
    var shouldAnimated by remember { mutableStateOf(true) }

    // Marque effect
    LaunchedEffect(key1 = shouldAnimated) {
        scrollState.animateScrollTo(
            scrollState.maxValue,
            animationSpec = tween(10000, 200, easing = CubicBezierEasing(0f, 0f, 0f, 0f))
        )
        scrollState.scrollTo(0)
        shouldAnimated = !shouldAnimated
    }

    Surface(
        color = Color(0xFF69AADF),
        modifier = modifier
            .clip(shape = RoundedCornerShape(12.dp))
            .fillMaxWidth()
            .clickable { onClick.invoke() }
            .height(72.dp)
    ) {
        Row(
            modifier = Modifier
                .wrapContentHeight()
        ) {
            Box(modifier = Modifier.width(72.dp).height(72.dp)){
                LottieAnimation(
                    composition = loading,
                    iterations = if (MyEventBus.isPlaying.collectAsState().value) LottieConstants.IterateForever else 1,
                    speed = if (MyEventBus.isPlaying.collectAsState().value) 0.7f else 0f
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
                    .align(Alignment.CenterVertically)
            ) {
                Text(
                    text = musicData.title ?: "-- -- --",
                    color = Color.White,
                    fontSize = TextUnit(18f, TextUnitType.Sp),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    modifier = Modifier.horizontalScroll(scrollState, false)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = musicData.artist ?: "Unknown artist",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = TextUnit(14f, TextUnitType.Sp),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            }

            Image(
                //colorFilter = ColorFilter.tint(Color.White),
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(8.dp)
                    .size(30.dp)
                    .padding(4.dp)
                    .clickable {
                        musicData =
                            MyEventBus.storageCursor!!.getMusicDataByPosition(if (MyEventBus.storagePos.value > 0) MyEventBus.storagePos.value - 1 else MyEventBus.storageCursor!!.count - 1)
                        onEventDispatcher(HomeContract.Intent.UserCommand(CommandEnum.PREV))
                    },
                painter = painterResource(
                    id = R.drawable.img_back
                ),
                contentDescription = null,
                colorFilter = ColorFilter.tint(Color.White)
            )

            Image(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(8.dp)
                    .size(30.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                        MyEventBus.currentCursorEnum = CursorEnum.STORAGE

                        onEventDispatcher(HomeContract.Intent.UserCommand(CommandEnum.MANAGE))
                    },
                painter = painterResource(
                    id = if (musicIsPlaying.value) R.drawable.img_pause
                    else R.drawable.img_play
                ),
                contentDescription = null,
                colorFilter = ColorFilter.tint(Color.White)
            )
            Image(
                //colorFilter = ColorFilter.tint(Color.White),
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(8.dp)
                    .size(30.dp)
                    .padding(4.dp)
                    .clickable {
                        musicData =
                            MyEventBus.storageCursor!!.getMusicDataByPosition(if (MyEventBus.storagePos.value < MyEventBus.storageCursor!!.count - 1) MyEventBus.storagePos.value + 1 else 0)
                        onEventDispatcher(HomeContract.Intent.UserCommand(CommandEnum.NEXT))
                    }
                    .rotate(180f),
                painter = painterResource(id = R.drawable.img_back),

                contentDescription = null,
                colorFilter = ColorFilter.tint(Color.White)
            )

        }
    }
}