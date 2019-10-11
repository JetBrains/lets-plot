package jetbrains.datalore.plot.base.scale.transform

class ReverseTransform : FunTransform(
    F,
    F_INVERSE
) {
    companion object {
        private val F: (Double?) -> Double? = { v ->
            if (v != null)
                -v
            else
                null
        }
        private val F_INVERSE: (Double?) -> Double? = { v ->
            if (v != null)
                -v
            else
                null
        }
    }
}