/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.svg.dom

import org.jetbrains.letsPlot.commons.intern.observable.property.Property
import org.jetbrains.letsPlot.commons.intern.observable.property.WritableProperty
import org.jetbrains.letsPlot.commons.values.Color

interface SvgTextContent {

    companion object {
        val FILL: SvgAttributeSpec<SvgColor> =
            SvgAttributeSpec.createSpec("fill")
        val FILL_OPACITY: SvgAttributeSpec<Double> =
            SvgAttributeSpec.createSpec("fill-opacity")
        val STROKE: SvgAttributeSpec<SvgColor> =
            SvgAttributeSpec.createSpec("stroke")
        val STROKE_OPACITY: SvgAttributeSpec<Double> =
            SvgAttributeSpec.createSpec("stroke-opacity")
        val STROKE_WIDTH: SvgAttributeSpec<Double> =
            SvgAttributeSpec.createSpec("stroke-width")
        val TEXT_ANCHOR: SvgAttributeSpec<String> =
            SvgAttributeSpec.createSpec(SvgConstants.SVG_TEXT_ANCHOR_ATTRIBUTE)
        val TEXT_DY: SvgAttributeSpec<String> =
            SvgAttributeSpec.createSpec(SvgConstants.SVG_TEXT_DY_ATTRIBUTE)
    }

    val computedTextLength: Double

    fun fill(): Property<SvgColor?>

    fun fillColor(): WritableProperty<Color?>

    fun fillOpacity(): Property<Double?>

    fun stroke(): Property<SvgColor?>

    fun strokeColor(): WritableProperty<Color?>

    fun strokeOpacity(): Property<Double?>

    fun strokeWidth(): Property<Double?>

    fun textAnchor(): Property<String?>

    fun textDy(): Property<String?>
}