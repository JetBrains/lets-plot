/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.svg.dom

import org.jetbrains.letsPlot.commons.intern.observable.property.Property

interface SvgContainer {

    fun opacity(): Property<Double?>
    fun clipPath(): Property<SvgIRI?>

    companion object {
        val OPACITY: SvgAttributeSpec<Double> =
            SvgAttributeSpec.createSpec("opacity")
        val CLIP_PATH: SvgAttributeSpec<SvgIRI> =
            SvgAttributeSpec.createSpec("clip-path")
    }
}