package jetbrains.datalore.visualization.base.svg

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.observable.property.Property

class SvgClipPathElement : SvgGraphicsElement(), SvgTransformable {

    val elementName: String
        get() = "clipPath"

    val bBox: DoubleRectangle
        get() = container().getPeer().getBBox(this)

    fun clipPathUnits(): Property<ClipPathUnits> {
        return getAttribute(CLIP_PATH_UNITS)
    }

    fun transform(): Property<SvgTransform> {
        return getAttribute(TRANSFORM)
    }

    fun pointToTransformedCoordinates(point: DoubleVector): DoubleVector {
        return container().getPeer().invertTransform(this, point)
    }

    fun pointToAbsoluteCoordinates(point: DoubleVector): DoubleVector {
        return container().getPeer().applyTransform(this, point)
    }

    enum class ClipPathUnits private constructor(private val myAttributeString: String) {
        USER_SPACE_ON_USE("userSpaceOnUse"),
        OBJECT_BOUNDING_BOX("objectBoundingBox");


        override fun toString(): String {
            return myAttributeString
        }
    }

    companion object {

        private val CLIP_PATH_UNITS = SvgAttributeSpec.createSpec("clipPathUnits")
    }

}
