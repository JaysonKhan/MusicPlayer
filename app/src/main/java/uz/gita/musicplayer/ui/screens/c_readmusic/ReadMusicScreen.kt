package uz.gita.musicplayer.ui.screens.c_readmusic

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.hilt.getViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import uz.gita.musicplayer.R
import uz.gita.musicplayer.data.model.CommandEnum
import uz.gita.musicplayer.data.model.CursorEnum
import uz.gita.musicplayer.navidation.MyScreen
import uz.gita.musicplayer.ui.components.ControlPanel
import uz.gita.musicplayer.ui.screens.c_readmusic.viewmodel.ReadViewModel
import uz.gita.musicplayer.ui.theme.MusicPlayerTheme
import uz.gita.musicplayer.utils.MyEventBus
import uz.gita.musicplayer.utils.getMusicDataByPosition
import uz.gita.musicplayer.utils.getMusicImage
import uz.gita.musicplayer.utils.startMusicService
import java.util.concurrent.TimeUnit

class ReadMusicScreen : MyScreen() {
    @Composable
    override fun Content() {
        val context = LocalContext.current
        val viewModel: ReadMusicContract.ViewModel = getViewModel<ReadViewModel>()
        viewModel.collectSideEffect { sideEffect ->
            when (sideEffect) {
                is ReadMusicContract.SideEffect.UserAction -> {
                    startMusicService(context, sideEffect.actionEnum)
                }
            }
        }
        MusicPlayerTheme {
            Surface(color = MaterialTheme.colorScheme.background) {
                val uiState = viewModel.collectAsState()
                PlayScreenContent(
                    uiState, viewModel::onEventDispatcher
                )
            }
        }
    }
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun PlayScreenContent(
    uiState: State<ReadMusicContract.UIState>,
    eventListener: (ReadMusicContract.Intent) -> Unit
) {
    val loading by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.musicplaying))
    val process by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.progress))

    var isSaved by remember { mutableStateOf(false) }

    val musicData = MyEventBus.currentMusicData.collectAsState(
        initial = if (MyEventBus.currentCursorEnum == CursorEnum.SAVED)
            MyEventBus.roomCursor!!.getMusicDataByPosition(MyEventBus.roomPos)
        else MyEventBus.storageCursor!!.getMusicDataByPosition(MyEventBus.storagePos.value)
    )
    musicData.value?.albumArt = MyEventBus.storageCursor!!.getMusicImage()

    eventListener(ReadMusicContract.Intent.CheckMusic(musicData.value!!))
    var duration: String
    musicData.value!!.duration.apply {
        val hours = TimeUnit.MILLISECONDS.toHours(this)
        val minutes = (this / 1000 / 60) % 60
        val seconds = (this / 1000) % 60
        duration = if (hours == 0L) "%02d:%02d".format(minutes, seconds)
        else "%02d:%02d:%02d".format(hours, minutes, seconds) // 03:45
    }

    val myColor: Color = if (musicData.value!!.albumArt != null)
        getColorFromBitmap(musicData.value!!.albumArt!!) else Color(0xFF69AADF)
    when (uiState.value) {
        ReadMusicContract.UIState.UpdateState -> {

        }

        is ReadMusicContract.UIState.CheckMusic -> {
            isSaved = (uiState.value as ReadMusicContract.UIState.CheckMusic).isSaved
        }
    }
    val seekBarState = MyEventBus.currentTimeFlow.collectAsState(initial = 0)
    var seekBarValue by remember { mutableStateOf(seekBarState.value) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(myColor.copy(alpha = 0.3f), RoundedCornerShape(5.dp))
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(0.dp)
                .weight(1.6f)
        ) {
            if (musicData.value!!.albumArt != null) {
                val image = musicData.value!!.albumArt?.asImageBitmap()
                Image(
                    bitmap = image!!,
                    contentDescription = "Album Art",
                    modifier = Modifier
                        .width(300.dp)
                        .height(300.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .fillMaxWidth()
                        .align(CenterHorizontally),
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.img_1),
                    contentDescription = "MusicDisk",
                    modifier = Modifier
                        .width(300.dp)
                        .height(300.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .fillMaxWidth()
                        .align(CenterHorizontally),
                    contentScale = ContentScale.Crop,
                    colorFilter = ColorFilter.tint(Color(0xFF69AADF))
                )
            }

            musicData.value?.let { music ->
                music.title?.let {
                    Text(
                        text = it,
                        color = Color.White,
                        fontSize = 24.sp,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 2,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )
                }

            }
            Text(
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                text = musicData.value!!.artist ?: "Unknown",
                fontSize = 18.sp,
                color = Color.White
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(0.dp)
                .weight(1f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(0.dp)
                    .weight(1f)
            ) {
                Box(modifier = Modifier.padding(horizontal = 8.dp), contentAlignment = Center) {
                    LottieAnimation(
                        composition = process,
                        iterations = if (MyEventBus.isPlaying.collectAsState().value) LottieConstants.IterateForever else 1,
                        modifier = Modifier.fillMaxWidth(),
                        speed = if (MyEventBus.isPlaying.collectAsState().value) 0.7f else 0f
                    )
                    Slider(
                        value = seekBarState.value.toFloat(),
                        onValueChange = { newState ->
                            seekBarValue = newState.toInt()
                            eventListener.invoke(ReadMusicContract.Intent.UserAction(CommandEnum.UPDATE_SEEKBAR))
                        },
                        onValueChangeFinished = {
                            MyEventBus.currentTime.value = seekBarValue
                            eventListener.invoke(ReadMusicContract.Intent.UserAction(CommandEnum.UPDATE_SEEKBAR))
                        },
                        valueRange = 0f..musicData.value!!.duration.toFloat(),
                        steps = 1000,
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFFa8dadc),
                            activeTickColor = Color(0xFFFFFFFF),
                            activeTrackColor = Color(0xFFCCC2C2),
                            inactiveTickColor = Color.Gray,
                            inactiveTrackColor = Color.Transparent
                        )
                    )
                }

                // 00:00
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        modifier = Modifier
                            .width(0.dp)
                            .weight(1f),
                        text = getTime(seekBarState.value / 1000),
                        color = Color.White
                    )
                    // 03:45
                    Text(
                        modifier = Modifier
                            .width(0.dp)
                            .weight(1f),
                        textAlign = TextAlign.End,
                        text = duration,
                        color = Color.White
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    contentAlignment = Center
                ) {

                    ControlPanel(modefier = Modifier.fillMaxWidth(), prevClicked = {
                        eventListener.invoke(
                            ReadMusicContract.Intent.UserAction(
                                CommandEnum.PREV
                            )
                        )
                        seekBarValue = 0
                    }, manageClicked = {
                        eventListener.invoke(
                            ReadMusicContract.Intent.UserAction(
                                CommandEnum.MANAGE
                            )
                        )
                    }) {
                        eventListener.invoke(
                            ReadMusicContract.Intent.UserAction(
                                CommandEnum.NEXT
                            )
                        )
                        seekBarValue = 0
                    }
                    LottieAnimation(
                        composition = loading,
                        iterations = if (MyEventBus.isPlaying.collectAsState().value) LottieConstants.IterateForever else 1,
                        speed = if (MyEventBus.isPlaying.collectAsState().value) 0.9f else 0f
                    )
                }
            }


        }
    }
}

fun getColorFromBitmap(musicPic: Bitmap): Color {
    var redBucket: Long = 0
    var greenBucket: Long = 0
    var blueBucket: Long = 0
    var pixelCount: Long = 0

    for (y in 0 until musicPic.height) {
        for (x in 0 until musicPic.width) {
            val c = musicPic.getPixel(x, y)

            pixelCount++
            redBucket += android.graphics.Color.red(c).toLong()
            greenBucket += android.graphics.Color.green(c).toLong()
            blueBucket += android.graphics.Color.blue(c).toLong()
            // does alpha matter?
        }
    }

    return Color(
        android.graphics.Color.rgb(
            (redBucket / pixelCount).toInt(),
            (greenBucket / pixelCount).toInt(),
            (blueBucket / pixelCount).toInt()
        )
    )

}

private fun getTime(time: Int): String {
    val hour = time / 3600
    val minute = (time % 3600) / 60
    val second = time % 60

    val hourText = if (hour > 0) {
        if (hour < 10) "0$hour:"
        else "$hour:"
    } else ""

    val minuteText = if (minute < 10) "0$minute:"
    else "$minute:"

    val secondText = if (second < 10) "0$second"
    else "$second"

    return "$hourText$minuteText$secondText"
}

