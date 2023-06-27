/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.svg.dom

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.observable.property.Property

abstract class SvgGraphicsElement : SvgStylableElement() {

    companion object {

        private val POINTER_EVENTS: SvgAttributeSpec<PointerEvents> =
            SvgAttributeSpec.createSpec("pointer-events")
        val OPACITY: SvgAttributeSpec<Double> =
            SvgAttributeSpec.createSpec("opacity")
        val VISIBILITY: SvgAttributeSpec<Visibility> =
            SvgAttributeSpec.createSpec("visibility")
        val CLIP_PATH: SvgAttributeSpec<SvgIRI> =
            SvgAttributeSpec.createSpec("clip-path")

        // Only for JFX. Workaround for implementation complexity of CLIP_PATH.
        val CLIP_BOUNDS_JFX: SvgAttributeSpec<DoubleRectangle> =
            SvgAttributeSpec.createSpec("clip-bounds-jfx")
    }


    fun pointerEvents(): Property<PointerEvents?> {
        return getAttribute(POINTER_EVENTS)
    }

    fun opacity(): Property<Double?> {
        return getAttribute(OPACITY)
    }

    fun visibility(): Property<Visibility?> {
        return getAttribute(VISIBILITY)
    }

    fun clipPath(): Property<SvgIRI?> {
        return getAttribute(CLIP_PATH)
    }

    enum class PointerEvents(private val myAttributeString: String) {
        VISIBLE_PAINTED("visiblePainted"),
        VISIBLE_FILL("visibleFill"),
        VISIBLE_STROKE("visibleStroke"),
        VISIBLE("visible"),
        PAINTED("painted"),
        FILL("fill"),
        STROKE("stroke"),
        ALL("all"),
        NONE("none"),
        INHERIT("inherit");

        override fun toString(): String {
            return myAttributeString
        }
    }

    enum class Visibility(private val myAttrString: String) {
        VISIBLE("visible"),
        HIDDEN("hidden"),
        COLLAPSE("collapse"),
        INHERIT("inherit");

        override fun toString(): String {
            return myAttrString
        }
    }
}
