/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.svg.dom

import org.jetbrains.letsPlot.commons.intern.observable.property.Property

interface SvgTransformable : SvgLocatable {

    companion object {
        val TRANSFORM: SvgAttributeSpec<SvgTransform> =
            SvgAttributeSpec.createSpec("transform")
    }

    fun transform(): Property<SvgTransform?>
}