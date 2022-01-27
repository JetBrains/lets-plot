/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.core.graphics

import jetbrains.datalore.base.geometry.DoubleVector.Companion.ZERO
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.vis.canvas.Context2d
import jetbrains.livemap.core.input.CursorStyle
import jetbrains.livemap.core.input.InputMouseEvent

class Button(
    val name: String
) : RenderBox() {
    private val frame = Frame()

    var enabledVisual: RenderBox? by visualProp(null)
    var disabledVisual: RenderBox? by visualProp(null)
    var enabled: Boolean by visualProp(true) { newValue ->
        if (newValue) {
            graphics.setCursor(this, CursorStyle.POINTER)
        } else {
            graphics.defaultCursor(this)
        }
    }

    var onClick: ((InputMouseEvent) -> Unit)? = null
    var onDoubleClick: ((InputMouseEvent) -> Unit)? = null

    fun dispatchClick(e: InputMouseEvent) {
        if (enabled) {
            onClick?.invoke(e)
        }
    }

    fun dispatchDoubleClick(e: InputMouseEvent) {
        if (enabled) {
            onDoubleClick?.invoke(e)
        }
    }

    override fun onAttach() {
        frame.attach(graphics)
    }

    protected override fun updateState() {
        fun blankRect(fill: Color) = Rectangle().apply {
            this.origin = ZERO
            this.dimension = dimension
            this.color = fill
        }

        frame.origin = origin
        frame.children = listOf(
            when(enabled) {
                true -> enabledVisual ?: blankRect(Color.LIGHT_GRAY)
                false -> disabledVisual ?: blankRect(Color.GRAY)
            }
        )

    }

    protected override fun renderInternal(ctx: Context2d) {
        frame.render(ctx)
    }
}
