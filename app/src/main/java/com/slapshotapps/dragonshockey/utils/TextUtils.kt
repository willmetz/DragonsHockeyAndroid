package com.slapshotapps.dragonshockey.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp

@Composable
fun measureTextWidth(text: String, style: androidx.compose.ui.text.TextStyle): Dp {
    val textMeasurer = rememberTextMeasurer()
    val widthInPixels = textMeasurer.measure(text = text, style = style).size.width
    return with(LocalDensity.current) { widthInPixels.toDp() }
}