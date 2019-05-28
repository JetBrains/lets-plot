package jetbrains.datalore.visualization.base.svg

import jetbrains.datalore.base.observable.property.Property

interface SvgTransformable : SvgLocatable {

    companion object {
        val TRANSFORM: SvgAttributeSpec<SvgTransform> = SvgAttributeSpec.createSpec("transform")
    }

    fun transform(): Property<SvgTransform?>
}