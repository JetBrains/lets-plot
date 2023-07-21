/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.canvascontrols

import org.jetbrains.letsPlot.core.canvas.CanvasControl

internal class CanvasContentPresenter {
    lateinit var canvasControl: CanvasControl
    private var canvasContent: CanvasContent = EMPTY_CANVAS_CONTENT


    fun show(content: CanvasContent) {
        canvasContent.hide()
        canvasContent = content
        canvasContent.show(canvasControl)
    }

    fun clear() {
        show(EMPTY_CANVAS_CONTENT)
    }

    private class EmptyContent : CanvasContent {
        override fun show(parentControl: CanvasControl) {}

        override fun hide() {}
    }

    companion object {
        private val EMPTY_CANVAS_CONTENT = EmptyContent()
    }
}