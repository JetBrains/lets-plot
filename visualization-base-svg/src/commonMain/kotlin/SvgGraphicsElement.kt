package jetbrains.datalore.visualization.base.svg

import jetbrains.datalore.base.observable.property.Property

abstract class SvgGraphicsElement : SvgStylableElement() {

    companion object {

        private val POINTER_EVENTS: SvgAttributeSpec<PointerEvents> = SvgAttributeSpec.createSpec("pointer-events")
        private val OPACITY: SvgAttributeSpec<Double> = SvgAttributeSpec.createSpec("opacity")
        private val VISIBILITY: SvgAttributeSpec<Visibility> = SvgAttributeSpec.createSpec("visibility")
        private val CLIP_PATH: SvgAttributeSpec<SvgIRI> = SvgAttributeSpec.createSpec("clip-path")
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

    enum class PointerEvents private constructor(private val myAttributeString: String) {
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

    enum class Visibility private constructor(private val myAttrString: String) {
        VISIBLE("visible"),
        HIDDEN("hidden"),
        COLLAPSE("collapse"),
        INHERIT("inherit");

        override fun toString(): String {
            return myAttrString
        }
    }
}
