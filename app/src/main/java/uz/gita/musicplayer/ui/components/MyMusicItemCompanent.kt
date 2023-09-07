package uz.gita.musicplayer.ui.components

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import uz.gita.musicplayer.R
import uz.gita.musicplayer.data.model.MusicData
import uz.gita.musicplayer.ui.theme.MusicPlayerTheme
import uz.gita.musicplayer.utils.MyEventBus
import android.graphics.Color as Color1

@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalUnitApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MusicItemComponent(
    musicData: MusicData,
    onClick: () -> Unit
) {
    val loading by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.currentmusic))

    val myColor:Color = if (musicData.albumArt!= null)
        getColorFromBitmap(musicData.albumArt!!) else Color(0xFF69AADF)
    Surface(modifier = Modifier
        .wrapContentHeight()
        .fillMaxWidth()
        .padding(vertical = 2.dp, horizontal = 4.dp)
        .clickable { onClick.invoke() },
    ) {
        Row(modifier = Modifier
            .wrapContentHeight()
            .background(myColor.copy(0.8f), RoundedCornerShape(16.dp)).padding(8.dp,16.dp)) {
            if (musicData.albumArt != null) {
                val image = musicData.albumArt!!.asImageBitmap()
                Image(
                    bitmap = image,
                    contentDescription = "Album Art",
                    modifier = Modifier
                        .width(56.dp)
                        .height(56.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.img_1),
                    contentDescription = "MusicDisk",
                    modifier = Modifier
                        .width(56.dp)
                        .height(56.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )

                // Album art is null, handle accordingly
            }


            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = musicData.title ?: "Unknown name",
                    color = Color.White,
                    fontSize = TextUnit(18f, TextUnitType.Sp),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = musicData.artist ?: "Unknown artist",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = TextUnit(14f, TextUnitType.Sp),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            }
            if (MyEventBus.currentMusicData.collectAsState().value==musicData){
                Box(modifier = Modifier
                    .width(56.dp)
                    .height(56.dp)) {
                    LottieAnimation(composition = loading , iterations = LottieConstants.IterateForever,
                        speed = if (MyEventBus.isPlaying.collectAsState().value) 0.7f else 0f)
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun MusicItemComponentPreview() {
    MusicPlayerTheme {
        val drawable: Drawable? =getDrawable(LocalContext.current,R.drawable.img)
        val bitmap: Bitmap? = drawable?.let { drawableToBitmap(it) }

        val musicDate = MusicData(
            id = 347,
            "Jahongir",
            data = "ss",
            duration = 100000,
            title = "Hello Uzbekistan",
            albumArt = bitmap
        )
        MusicItemComponent(
            musicData = musicDate,
            onClick = {}
        )
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
            redBucket += Color1.red(c).toLong()
            greenBucket += Color1.green(c).toLong()
            blueBucket += Color1.blue(c).toLong()
            // does alpha matter?
        }
    }

    return  Color(Color1.rgb(
        (redBucket / pixelCount).toInt(),
        (greenBucket / pixelCount).toInt(),
        (blueBucket / pixelCount).toInt())
    )
}

fun drawableToBitmap(drawable: Drawable): Bitmap {
    val bitmap: Bitmap = if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
        Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    } else {
        Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
    }

    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)

    return bitmap
}




