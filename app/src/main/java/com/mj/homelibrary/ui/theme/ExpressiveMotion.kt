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
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
    val StandardDecelerate: Easing = CubicBezierEasing(0.0f, 0.0f, 0.2f, 1.0f)

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

    val MorphDpSpring: AnimationSpec<Dp> = spring(
        dampingRatio = 0.7f,
        stiffness = 400f
    )

    // --- Standard Duration Constants ---
    const val DurationShort = 200
    const val DurationMedium = 350
    const val DurationLong = 500
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
 * Creates a standard M3 dialog / sheet enter transition with scale and fade.
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
 * Creates a standard M3 dialog / sheet exit transition with scale and fade.
 */
fun m3DialogExitTransition(): ExitTransition {
    return scaleOut(
        animationSpec = tween(ExpressiveMotion.DurationShort, easing = ExpressiveMotion.EmphasizedAccelerate),
        targetScale = 0.92f
    ) + fadeOut(
        animationSpec = tween(ExpressiveMotion.DurationShort, easing = ExpressiveMotion.Emphasized)
    )
}
