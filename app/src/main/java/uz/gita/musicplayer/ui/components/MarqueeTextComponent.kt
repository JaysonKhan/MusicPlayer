package uz.gita.musicplayer.ui.components

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import uz.gita.musicplayer.ui.theme.MusicPlayerTheme

@Composable
fun MarqueeTextComponent(
    myText: String
) {
    val scrollState = rememberScrollState()
    var shouldAnimated by remember { mutableStateOf(true) }

    LaunchedEffect(key1 = shouldAnimated) {
        scrollState.animateScrollTo(
            scrollState.maxValue,
            animationSpec = tween(10000, 200, easing = CubicBezierEasing(0f, 0f, 0f, 0f))
        )
        scrollState.scrollTo(0)
        shouldAnimated = !shouldAnimated
    }
    Text(
        text = myText,
        color = Color.White,
        fontSize = 24.sp,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth().horizontalScroll(scrollState, true)
    )
}


@Composable
@Preview(showBackground = true)
private fun MarqueeTextComponentPreview() {
    MusicPlayerTheme() {
        Box(modifier = Modifier.fillMaxSize()) {
            MarqueeTextComponent(myText = "Lorem Ipsum is simply dummy text of the printing and typesetting industry")
        }
    }
}
