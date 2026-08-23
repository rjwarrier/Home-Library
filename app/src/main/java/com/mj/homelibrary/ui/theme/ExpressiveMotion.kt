package com.mj.homelibrary.ui.theme

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp

/**
 * Material 3 Expressive Motion Tokens and Curves
 */
object ExpressiveMotion {

    // --- Easing Curves ---
    val Emphasized: Easing = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)
    val EmphasizedDecelerate: Easing = CubicBezierEasing(0.0f, 0.0f, 0.2f, 1.0f)
    val EmphasizedAccelerate: Easing = CubicBezierEasing(0.4f, 0.0f, 1.0f, 1.0f)
    val Standard: Easing = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)
    val StandardDecelerate: Easing = CubicBezierEasing(0.0f, 0.0f, 0.2f, 1.0f)
    val StandardAccelerate: Easing = CubicBezierEasing(0.3f, 0.0f, 1.0f, 1.0f)

    // --- Spring Specifications ---
    val ExpressiveSpring: AnimationSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMediumLow
    )

    val BouncyPressSpring: AnimationSpec<Float> = spring(
        dampingRatio = 0.58f,
        stiffness = 600f
    )

    val SoftSpring: AnimationSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessLow
    )

    val FastSpring: AnimationSpec<Float> = spring(
        dampingRatio = 0.75f,
        stiffness = 800f
    )

    val MorphDpSpring: AnimationSpec<Dp> = spring(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMediumLow,
    )

    // --- Standard Duration Constants ---
    const val DurationShort = 200
    const val DurationMedium = 350
    const val DurationLong = 500
    const val DurationExtraLong = 700
}

/**
 * Applies a micro-interaction scale effect on press using M3 Expressive bouncy spring physics.
 */
fun Modifier.expressiveClickable(
    enabled: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = null,
    pressedScale: Float = 0.95f,
    onClick: () -> Unit
): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) pressedScale else 1f,
        animationSpec = ExpressiveMotion.BouncyPressSpring,
        label = "expressiveClickableScale"
    )

    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .clickable(
            interactionSource = interactionSource,
            indication = androidx.compose.material3.ripple(),
            enabled = enabled,
            onClickLabel = onClickLabel,
            role = role,
            onClick = onClick
        )
}

/**
 * Applies tactile press-scaling animation state without click handler (for custom pointer input components).
 */
@Composable
fun expressivePressScale(
    isPressed: Boolean,
    pressedScale: Float = 0.95f
): Float {
    val scale by animateFloatAsState(
        targetValue = if (isPressed) pressedScale else 1f,
        animationSpec = ExpressiveMotion.BouncyPressSpring,
        label = "expressivePressScale"
    )
    return scale
}

/**
 * Creates M3 Shared Axis horizontal slide + fade transition for tab switching.
 */
fun m3TabTransition(isForward: Boolean): ContentTransform {
    val slideDistance = 60
    val enter = slideInHorizontally(
        animationSpec = tween(ExpressiveMotion.DurationMedium, easing = ExpressiveMotion.EmphasizedDecelerate),
        initialOffsetX = { if (isForward) slideDistance else -slideDistance }
    ) + fadeIn(
        animationSpec = tween(ExpressiveMotion.DurationShort, easing = ExpressiveMotion.Emphasized)
    ) + scaleIn(
        animationSpec = tween(ExpressiveMotion.DurationMedium, easing = ExpressiveMotion.EmphasizedDecelerate),
        initialScale = 0.96f
    )

    val exit = slideOutHorizontally(
        animationSpec = tween(ExpressiveMotion.DurationMedium, easing = ExpressiveMotion.EmphasizedAccelerate),
        targetOffsetX = { if (isForward) -slideDistance else slideDistance }
    ) + fadeOut(
        animationSpec = tween(ExpressiveMotion.DurationShort, easing = ExpressiveMotion.Emphasized)
    ) + scaleOut(
        animationSpec = tween(ExpressiveMotion.DurationMedium, easing = ExpressiveMotion.EmphasizedAccelerate),
        targetScale = 0.96f
    )

    return enter togetherWith exit
}

/**
 * Creates M3 Fade Through transition for neutral content switches.
 */
fun m3FadeThroughTransition(): ContentTransform {
    val enter = fadeIn(
        animationSpec = tween(ExpressiveMotion.DurationMedium, delayMillis = 50, easing = ExpressiveMotion.EmphasizedDecelerate)
    ) + scaleIn(
        animationSpec = tween(ExpressiveMotion.DurationMedium, delayMillis = 50, easing = ExpressiveMotion.EmphasizedDecelerate),
        initialScale = 0.94f
    )
    val exit = fadeOut(
        animationSpec = tween(ExpressiveMotion.DurationShort, easing = ExpressiveMotion.EmphasizedAccelerate)
    ) + scaleOut(
        animationSpec = tween(ExpressiveMotion.DurationShort, easing = ExpressiveMotion.EmphasizedAccelerate),
        targetScale = 0.96f
    )
    return enter togetherWith exit
}

/**
 * Creates a standard M3 dialog enter transition with scale and fade.
 */
fun m3DialogEnterTransition(): EnterTransition {
    return scaleIn(
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMediumLow),
        initialScale = 0.88f
    ) + fadeIn(
        animationSpec = tween(ExpressiveMotion.DurationShort, easing = ExpressiveMotion.EmphasizedDecelerate)
    )
}

/**
 * Creates a standard M3 dialog exit transition with scale and fade.
 */
fun m3DialogExitTransition(): ExitTransition {
    return scaleOut(
        animationSpec = tween(ExpressiveMotion.DurationShort, easing = ExpressiveMotion.EmphasizedAccelerate),
        targetScale = 0.92f
    ) + fadeOut(
        animationSpec = tween(ExpressiveMotion.DurationShort, easing = ExpressiveMotion.Emphasized)
    )
}

/**
 * Creates M3 bottom sheet / banner vertical slide and fade enter transition.
 */
fun m3SheetEnterTransition(): EnterTransition {
    return slideInVertically(
        animationSpec = tween(ExpressiveMotion.DurationMedium, easing = ExpressiveMotion.EmphasizedDecelerate),
        initialOffsetY = { it / 3 }
    ) + fadeIn(
        animationSpec = tween(ExpressiveMotion.DurationShort, easing = ExpressiveMotion.EmphasizedDecelerate)
    )
}

/**
 * Creates M3 bottom sheet / banner vertical slide and fade exit transition.
 */
fun m3SheetExitTransition(): ExitTransition {
    return slideOutVertically(
        animationSpec = tween(ExpressiveMotion.DurationShort, easing = ExpressiveMotion.EmphasizedAccelerate),
        targetOffsetY = { it / 3 }
    ) + fadeOut(
        animationSpec = tween(ExpressiveMotion.DurationShort, easing = ExpressiveMotion.EmphasizedAccelerate)
    )
}

/**
 * Creates M3 vertical expand transition.
 */
fun m3ExpandTransition(): EnterTransition {
    return expandVertically(
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMediumLow)
    ) + fadeIn(
        animationSpec = tween(ExpressiveMotion.DurationShort, easing = ExpressiveMotion.EmphasizedDecelerate)
    )
}

/**
 * Creates M3 vertical shrink transition.
 */
fun m3ShrinkTransition(): ExitTransition {
    return shrinkVertically(
        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMediumLow)
    ) + fadeOut(
        animationSpec = tween(ExpressiveMotion.DurationShort, easing = ExpressiveMotion.EmphasizedAccelerate)
    )
}
