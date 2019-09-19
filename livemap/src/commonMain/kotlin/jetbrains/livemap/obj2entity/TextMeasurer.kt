package jetbrains.livemap.obj2entity

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.base.canvas.Context2d
import jetbrains.datalore.visualization.base.canvas.CssFontParser

class TextMeasurer(private val myContext2d: Context2d) {
    fun measure(label: String, font: String): DoubleVector {
        myContext2d.save()
        myContext2d.setFont(font)
        val width = myContext2d.measureText(label)
        myContext2d.restore()

        val parser =
            CssFontParser.create(font)
                ?: throw IllegalStateException("Could not parse css font string: $font")

        val fontSize = parser.fontSize
        val height = fontSize ?: 10.0

        return DoubleVector(width, height)
    }
}