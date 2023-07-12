/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.core.graphics

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.canvas.Context2d
import jetbrains.livemap.core.animation.Animation
import jetbrains.livemap.core.animation.Animations
import kotlin.math.PI

class Spinner : RenderBox() {
    var size: Double by visualProp(24.0)

    private val mySpinner = Frame()
    private var mySpinnerArc = Arc()
    private var myAnimationReg: Registration? = null

    override fun updateState() {
        val outerDim = DoubleVector(size, size)
        val arcOrigin = DoubleVector(4.0, 4.0)
        val arcDim = outerDim.subtract(arcOrigin.mul(2.0))
        mySpinner.apply {
            children = listOf(
                Circle().apply {
                    origin = DoubleVector.ZERO
                    dimension = outerDim
                    fillColor = Color.WHITE
                    strokeColor = Color.LIGHT_GRAY
                    strokeWidth = 1.0
                },

                Circle().apply {
                    origin = arcOrigin
                    dimension = arcDim
                    fillColor = Color.TRANSPARENT
                    strokeColor = Color.LIGHT_GRAY
                    strokeWidth = 2.0
                },

                mySpinnerArc.apply {
                    origin = arcOrigin
                    dimension = arcDim
                    strokeColor = Color.parseHex("#70a7e3")
                    strokeWidth = 2.0
                    angle = PI / 4
                }
            )
        }

    }

    override fun onAttach() {
        mySpinnerArc.attach(graphics)
        mySpinner.attach(graphics)
    }

    fun runAnimation() {
        myAnimationReg?.remove()
        myAnimationReg = graphics.addAnimation(
            Animations.AnimationBuilder(1000.0)
                .setLoop(Animation.Loop.KEEP_DIRECTION)
                .setAnimator(Animations.DoubleAnimator(0.0, 2 * PI) {
                    mySpinnerArc.apply {
                        startAngle = it
                    }
                }).build()
        )
    }

    fun stopAnimation() {
        myAnimationReg?.remove()
        myAnimationReg = null
    }

    override fun renderInternal(ctx: Context2d) {
        mySpinner.render(ctx)
    }
}
