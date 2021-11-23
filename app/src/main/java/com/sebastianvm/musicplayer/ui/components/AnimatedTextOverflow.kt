package com.sebastianvm.musicplayer.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import com.sebastianvm.musicplayer.ui.util.compose.AppDimensions
import com.sebastianvm.musicplayer.ui.util.compose.ScreenPreview


@Composable
fun AnimatedTextOverflow(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    style: TextStyle = LocalTextStyle.current
) {
    var shouldScroll by remember { mutableStateOf(false) }
    var displayText by remember { mutableStateOf(text)  }
    var width by remember { mutableStateOf(0) }
    val scrollState = rememberScrollState()
    LaunchedEffect(key1 = width, key2 = scrollState.maxValue) {
        scrollState.scrollTo(0)
        if (scrollState.maxValue != Int.MAX_VALUE) {
            scrollState.animateScrollTo(
                width,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 1 + scrollState.maxValue * 10,
                        easing = LinearEasing,
                        delayMillis = 2000
                    )
                )
            )
        }
    }

    if (displayText != text) {
        displayText = text
        shouldScroll = false
    }

    if (shouldScroll) {
        Text(
            text = text + " ".repeat(20) + text,
            modifier = modifier.horizontalScroll(scrollState, enabled = false),
            maxLines = 1,
            overflow = TextOverflow.Clip,
            color = color,
            fontSize = fontSize,
            fontStyle = fontStyle,
            fontWeight = fontWeight,
            fontFamily = fontFamily,
            letterSpacing = letterSpacing,
            textDecoration = textDecoration,
            textAlign = textAlign,
            lineHeight = lineHeight,
            style = style,
        )
    } else {
        Text(
            text = text + " ".repeat(20),
            modifier = modifier.wrapContentWidth(),
            maxLines = 1,
            overflow = TextOverflow.Visible,
            onTextLayout = {
//                Log.i(
//                    "SCROLL1",
//                    "${it.multiParagraph.maxIntrinsicWidth}, ${it.size.width}, ${it.getLineRight(0)}"
//                )
                val tempWidth = it.getHorizontalPosition(it.getLineEnd(0, true), usePrimaryDirection = true)
//                Log.i("SCROOL", "TempWidth: $tempWidth")
                if (it.multiParagraph.didExceedMaxLines) {
                    width = (it.multiParagraph.maxIntrinsicWidth).toInt()
                    shouldScroll = true
                }
            },
            color = color,
            fontSize = fontSize,
            fontStyle = fontStyle,
            fontWeight = fontWeight,
            fontFamily = fontFamily,
            letterSpacing = letterSpacing,
            textDecoration = textDecoration,
            textAlign = textAlign,
            lineHeight = lineHeight,
            style = style
        )

    }


}

@Preview
@Composable
fun AnimatedTextOverflowPreviews() {
    ScreenPreview {
        Column {


            Column(modifier = Modifier.padding(all = AppDimensions.spacing.large)) {
//            AnimatedTextOverflow(text = "Short text")
                AnimatedTextOverflow(
                    text = "Long text that I'm trying to animate, please work!",
                )
                AnimatedTextOverflow(
                    text = "Long text that                          ",
                )
//            AnimatedTextOverflow(text = "12345678901234567890")
            }
        }
    }
}

