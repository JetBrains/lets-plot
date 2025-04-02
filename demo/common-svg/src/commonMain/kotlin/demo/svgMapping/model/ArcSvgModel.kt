/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.svgMapping.model

import org.jetbrains.letsPlot.datamodel.svg.dom.SvgColors
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgPathDataBuilder
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgSvgElement

object ArcSvgModel {
    fun createModel(): SvgSvgElement {
        return svgDocument(500.0, 500.0) {
            path(
                stroke = SvgColors.DARK_GREEN,
                strokeOpacity = 0.3,
                fillOpacity = 0.0,
                strokeWidth = 10,
                pathData = SvgPathDataBuilder()
                    .moveTo(145.0, 190.0)
                    .ellipticalArc(
                        rx = 150.0,
                        ry = 120.0,
                        xAxisRotation = 0.0,
                        largeArc = true,
                        sweep = true,
                        x = 296.0,
                        y = 314.0
                    )
                    .build()
            )

            path(
                stroke = SvgColors.DARK_BLUE,
                strokeOpacity = 0.3,
                fillOpacity = 0.0,
                strokeWidth = 10,
                pathData = SvgPathDataBuilder()
                    .moveTo(145.0, 190.0)
                    .ellipticalArc(
                        rx = 150.0,
                        ry = 120.0,
                        xAxisRotation = 90.0,
                        largeArc = true,
                        sweep = true,
                        x = 296.0,
                        y = 314.0
                    )
                    .build()
            )
        }
    }

}