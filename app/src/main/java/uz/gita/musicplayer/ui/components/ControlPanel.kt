package uz.gita.musicplayer.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import uz.gita.musicplayer.R
import uz.gita.musicplayer.utils.MyEventBus

@Composable
fun ControlPanel(
    modefier: Modifier,
    prevClicked: () -> Unit,
    manageClicked: () -> Unit,
    nextClicked: () -> Unit
) {
    val mucisIsPlaying = MyEventBus.isPlaying.collectAsState()

    Row(
        modifier = modefier
    ) {
        IconButton(
            onClick = { prevClicked.invoke() }, modifier = Modifier
                .rotate(180f)
                .width(0.dp)
                .weight(1f)
                .size(70.dp)
        ) {
            Icon(painter = painterResource(id = R.drawable.img_next), contentDescription = null, tint = Color.White)
        }
        IconButton(
            onClick = { manageClicked.invoke() }, modifier = Modifier
                .width(0.dp)
                .weight(1f)
                .size(70.dp)
        ) {
            Icon(
                painter = painterResource(
                    id = if (mucisIsPlaying.value) R.drawable.img_pause
                    else R.drawable.img_play
                ), contentDescription = null, tint = Color.White
            )
        }
        IconButton(
            onClick = { nextClicked.invoke() }, modifier = Modifier
                .width(0.dp)
                .weight(1f)
                .size(70.dp)
        ) {
            Icon(painter = painterResource(id = R.drawable.img_next), contentDescription = null, tint = Color.White)
        }
    }
}