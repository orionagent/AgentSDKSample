package com.ainirobot.agent.sample.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import kotlinx.coroutines.delay

@Composable
fun AnimatedDots(
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle.Default
) {
    var dotsCount by remember { mutableStateOf(0) }
    
    LaunchedEffect(Unit) {
        while (true) {
            delay(500) // 每500毫秒改变一次
            dotsCount = (dotsCount + 1) % 4
        }
    }
    
    Row(modifier = modifier) {
        Text(
            text = ".".repeat(dotsCount),
            style = style
        )
    }
} 