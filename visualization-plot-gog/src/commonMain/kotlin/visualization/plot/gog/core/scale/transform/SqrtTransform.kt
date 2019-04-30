package jetbrains.datalore.visualization.plot.gog.core.scale.transform

import jetbrains.datalore.base.function.Function
import jetbrains.datalore.base.function.Functions.function
import kotlin.math.sqrt

class SqrtTransform : FunTransform(F, F_INVERSE) {
    companion object {
        private val F: Function<Double, Double> = function { v ->
            //            if (v != null)
//                sqrt(v!!)
//            else
//                null
            sqrt(v)
        }
        private val F_INVERSE: Function<Double, Double> = function { v ->
            //            if (v != null)
//                v!! * v!!
//            else
//                null
            v * v
        }
    }
}