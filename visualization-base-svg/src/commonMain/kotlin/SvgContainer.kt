package jetbrains.datalore.visualization.base.svg

import jetbrains.datalore.base.observable.property.Property

interface SvgContainer {

    fun opacity(): Property<Double>
    fun clipPath(): Property<SvgIRI>

    companion object {
        val OPACITY = SvgAttributeSpec.createSpec("opacity")
        val CLIP_PATH = SvgAttributeSpec.createSpec("clip-path")
    }
}