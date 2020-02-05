/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout

internal abstract class PlotLayoutBase : PlotLayout {
    protected var paddingTop: Double = 0.0
        private set
    protected var paddingRight: Double = 0.0
        private set
    protected var paddingBottom: Double = 0.0
        private set
    protected var paddingLeft: Double = 0.0
        private set

    override fun setPadding(top: Double, right: Double, bottom: Double, left: Double) {
        paddingTop = top
        paddingRight = right
        paddingBottom = bottom
        paddingLeft = left
    }
}
