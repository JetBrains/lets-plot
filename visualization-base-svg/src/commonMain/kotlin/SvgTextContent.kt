package jetbrains.datalore.visualization.base.svg

import jetbrains.datalore.base.observable.property.Property
import jetbrains.datalore.base.observable.property.WritableProperty
import jetbrains.datalore.base.values.Color

interface SvgTextContent {

    val computedTextLength: Double

    fun fill(): Property<SvgColor>

    fun fillColor(): WritableProperty<Color>

    fun fillOpacity(): Property<Double>

    fun stroke(): Property<SvgColor>

    fun strokeColor(): WritableProperty<Color>

    fun strokeOpacity(): Property<Double>

    fun strokeWidth(): Property<Double>

    companion object {
        val FILL = SvgAttributeSpec.createSpec("fill")
        val FILL_OPACITY = SvgAttributeSpec.createSpec("fill-opacity")
        val STROKE = SvgAttributeSpec.createSpec("stroke")
        val STROKE_OPACITY = SvgAttributeSpec.createSpec("stroke-opacity")
        val STROKE_WIDTH = SvgAttributeSpec.createSpec("stroke-width")
    }
}