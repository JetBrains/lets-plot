package jetbrains.datalore.visualization.base.svg

import jetbrains.datalore.base.observable.property.Property

interface SvgTransformable : SvgLocatable {

    fun transform(): Property<SvgTransform>

    companion object {
        val TRANSFORM = SvgAttributeSpec.createSpec("transform")
    }
}