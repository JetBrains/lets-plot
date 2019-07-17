package jetbrains.datalore.visualization.base.svg

class SvgTransform internal constructor(private val myTransform: String) {

    override fun toString(): String {
        return myTransform
    }

    companion object {
        val EMPTY = SvgTransform("")

        const val MATRIX = "matrix"
        const val ROTATE = "rotate"
        const val SCALE = "scale"
        const val SKEW_X = "skewX"
        const val SKEW_Y = "skewY"
        const val TRANSLATE = "translate"
    }
}