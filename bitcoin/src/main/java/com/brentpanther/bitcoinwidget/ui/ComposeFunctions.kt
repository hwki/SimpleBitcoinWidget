package com.brentpanther.bitcoinwidget.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun appBarScrollColor(
    scrollBehavior: TopAppBarScrollBehavior,
    topAppBarColors: TopAppBarColors = TopAppBarDefaults.topAppBarColors()
): Color {
    val colorTransitionFraction by remember(scrollBehavior) {
        derivedStateOf {
            val overlappingFraction = scrollBehavior.state.overlappedFraction
            if (overlappingFraction > 0.01f) 1f else 0f
        }
    }
    return animateColorAsState(
        targetValue = lerp(
            topAppBarColors.containerColor,
            topAppBarColors.scrolledContainerColor,
            FastOutLinearInEasing.transform(colorTransitionFraction)
        ),
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow), label = ""
    ).value
}

