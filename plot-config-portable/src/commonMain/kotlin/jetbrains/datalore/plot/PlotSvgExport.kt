/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot

import jetbrains.datalore.base.logging.PortableLogging
import jetbrains.datalore.vis.svgToString.SvgToString

object PlotSvgExport {
    private val LOG = PortableLogging.logger(PlotSvgExport::class)

    fun buildSvgImageFromRawSpecs(plotSpec: MutableMap<String, Any>): String {
        val list = MonolithicCommon.buildSvgImagesFromRawSpecs(
            plotSpec,
            null,
            SvgToString(rgbEncoder = null)          // data-frame --> rgb image is not supported
        ) { messages ->
            messages.forEach {
                LOG.info { "[when SVG generating] $it" }
            }
        }

        if (list.size == 1) {
            return list[0]
        }
        throw RuntimeException("GGBunch is not supported")
    }
}