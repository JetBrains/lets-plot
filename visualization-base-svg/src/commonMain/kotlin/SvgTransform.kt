package jetbrains.datalore.visualization.base.svg

class SvgTransform internal constructor(private val myTransform: String) {

    override fun toString(): String {
        return myTransform
    }

    companion object {
        val EMPTY = SvgTransform("")

        val MATRIX = "matrix"
        val ROTATE = "rotate"
        val SCALE = "scale"
        val SKEW_X = "skewX"
        val SKEW_Y = "skewY"
        val TRANSLATE = "translate"
    }
}