/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.core.rendering.controls

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.geometry.DoubleVector.Companion.ZERO
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.vis.canvas.Context2d
import jetbrains.livemap.core.input.CursorStyle
import jetbrains.livemap.core.input.InputMouseEvent
import jetbrains.livemap.core.rendering.primitives.Frame
import jetbrains.livemap.core.rendering.primitives.Rectangle
import jetbrains.livemap.core.rendering.primitives.RenderBox
import jetbrains.livemap.ui.UiService

class Button(
    val name: String
) : RenderBox {
    override val origin get() = position
    override val dimension get() = buttonSize
    private lateinit var ui: UiService
    private lateinit var frame: RenderBox
    private var isDirty = true
        set(value) {
            if (value != field) {
                if (value) {
                    ui.repaint()
                }

                field = value
            }
        }

    var enabled: Boolean = true
        set(value) {
            if (field != value) {

                if (value) {
                    ui.setCursor(this, CursorStyle.POINTER)
                } else {
                    ui.defaultCursor(this)
                }

                field = value
                isDirty = true
            }
        }

    var enabledVisual: RenderBox? = null
        set(value) {
            field = value
            isDirty = true
        }

    var disabledVisual: RenderBox? = null
        set(value) {
            field = value
            isDirty = true
        }

    var position: DoubleVector = ZERO
        set(value) {
            field = value
            isDirty = true
        }

    var buttonSize: DoubleVector = ZERO
        set(value) {
            field = value
            isDirty = true
        }

    var onClick: ((InputMouseEvent) -> Unit)? = null
    var onDoubleClick: ((InputMouseEvent) -> Unit)? = null

    override fun render(ctx: Context2d) {
        if (isDirty) {
            rebuildFrame()
            isDirty = false
        }

        frame.render(ctx)
    }

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

    fun attach(ui: UiService) {
       this.ui = ui
    }

    private fun rebuildFrame() {
        frame = Frame.create(
            position,
            when(enabled) {
                true -> enabledVisual ?: blank(Color.LIGHT_GRAY)
                false -> disabledVisual ?: blank(Color.GRAY)
            }
        )
    }
    private fun blank(fill: Color) = Rectangle().apply {
        rect = DoubleRectangle(ZERO, buttonSize)
        color = fill
    }
}
