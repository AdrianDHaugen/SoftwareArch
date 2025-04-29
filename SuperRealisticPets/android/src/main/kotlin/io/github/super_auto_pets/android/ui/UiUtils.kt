package io.github.super_auto_pets.android.ui

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.math.Interpolation

/**
 * Subtle scale-up on hover (or touch enter/exit).
 * Recomputes origin each time so it always uses the current width/height.
 */
fun Actor.enableHoverPop(
    scaleAmount: Float = 1.1f,    // ← note the comma here
    duration:    Float = 0.1f     // ← now this is a real second parameter
) {
    addListener(object : ClickListener() {
        override fun enter(
            event: InputEvent?, x: Float, y: Float,
            pointer: Int, fromActor: Actor?
        ) {
            setOrigin(width / 2, height / 2)
            addAction(Actions.scaleTo(scaleAmount, scaleAmount, duration, Interpolation.fade))
        }
        override fun exit(
            event: InputEvent?, x: Float, y: Float,
            pointer: Int, toActor: Actor?
        ) {
            setOrigin(width / 2, height / 2)
            addAction(Actions.scaleTo(1f, 1f, duration, Interpolation.fade))
        }
    })
}
