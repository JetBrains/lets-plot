/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.android.view

import org.jetbrains.letsPlot.skia.view.SvgSkikoView
import org.jetbrains.skiko.SkiaLayer

class SvgSkikoViewAndroid : SvgSkikoView() {
    override fun updateSkiaLayerSize(width: Int, height: Int) {

    }

    override fun onHrefClick(href: String) {
        TODO("Not yet implemented")
    }

    override fun createSkiaLayer(view: SvgSkikoView): SkiaLayer {
        return SkiaLayer().apply {
            renderDelegate = view
        }
    }
}