/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.imagick.canvas

import MagickWand.*
import kotlinx.cinterop.*
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.canvas.Context2d
import org.jetbrains.letsPlot.core.canvas.Context2dDelegate
import org.jetbrains.letsPlot.core.canvas.Font


class MagickContext2d(
    private val wand: CPointer<MagickWand>?
) : Context2d by Context2dDelegate() {

    var currentPath: MagickPath = MagickPath()

    private val state = MagickState()

    override fun setFont(f: Font) {
        val size = f.fontSize
        val family = f.fontFamily
        val style = f.fontStyle
        val weight = f.fontWeight

        // Set font size
        //DrawSetFontSize(drawingWand, size)
    }

    override fun setFillStyle(color: Color?) {
        state.current().fillColor = color?.toCssColor() ?: Color.BLACK.toCssColor()
    }

    override fun setStrokeStyle(color: Color?) {
        state.current().strokeColor = color?.toCssColor() ?: Color.BLACK.toCssColor()
    }

    override fun setLineWidth(lineWidth: Double) {
        state.current().strokeWidth = lineWidth
    }

    override fun fillText(text: String, x: Double, y: Double) {
        memScoped {
            state.current().withFillWand { fillWand ->
                val textCStr = text.cstr.ptr.reinterpret<UByteVar>()
                DrawAnnotation(fillWand, x, y, textCStr)
                MagickDrawImage(wand, fillWand)
            }
        }
    }

    override fun strokeText(text: String, x: Double, y: Double) {
        memScoped {
            state.current().withStrokeWand { strokeWand ->
                val textCStr = text.cstr.ptr.reinterpret<UByteVar>()
                DrawAnnotation(strokeWand, x, y, textCStr)
                MagickDrawImage(wand, strokeWand)
            }
        }
    }

    override fun beginPath() {
        currentPath = MagickPath()
    }

    override fun moveTo(x: Double, y: Double) {
        currentPath.moveTo(x, y)
    }

    override fun lineTo(x: Double, y: Double) {
        currentPath.lineTo(x, y)
    }

    override fun closePath() {
        currentPath.closePath()
    }

    override fun stroke() {
        state.current().withStrokeWand { strokeWand ->
            currentPath.draw(strokeWand)
            DrawPathFinish(strokeWand)
            MagickDrawImage(wand, strokeWand)
        }
    }

    override fun fill() {
        state.current().withFillWand { fillWand ->
            currentPath.draw(fillWand)
            DrawPathFinish(fillWand)
            MagickDrawImage(wand, fillWand)
        }
    }

    override fun fillRect(x: Double, y: Double, w: Double, h: Double) {
        state.current().withFillWand { fillWand ->
            DrawRectangle(fillWand, x, y, x + w, y + h)
            MagickDrawImage(wand, fillWand)
        }
    }

    override fun arc(
        x: Double,
        y: Double,
        radius: Double,
        startAngle: Double,
        endAngle: Double,
        anticlockwise: Boolean
    ) {
        currentPath.arc(x, y, radius, startAngle, endAngle, anticlockwise)
    }

    override fun save() {
        state.push()
    }

    override fun restore() {
        state.pop()
    }
}
