package jetbrains.datalore.visualization.plot.gog.core.scale.transform

import java.util.function.Function

class ReverseTransform : FunTransform(F, F_INVERSE) {
    companion object {
        private val F = Function<Double, Double> { v ->
            //            if (v != null)
//                -v
//            else
//                null
            -v
        }
        private val F_INVERSE = Function<Double, Double> { v ->
            //            if (v != null)
//                -v
//            else
//                null
            -v
        }
    }
}