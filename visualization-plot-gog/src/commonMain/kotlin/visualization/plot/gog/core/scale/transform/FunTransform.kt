package jetbrains.datalore.visualization.plot.gog.core.scale.transform

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.visualization.plot.gog.core.scale.BreaksGenerator
import jetbrains.datalore.visualization.plot.gog.core.scale.MapperUtil
import jetbrains.datalore.visualization.plot.gog.core.scale.ScaleBreaks
import jetbrains.datalore.visualization.plot.gog.core.scale.Transform

open class FunTransform(
        private val myFun: (Double) -> Double,
        private val myInverse: (Double) -> Double) :
        Transform, BreaksGenerator {

    override fun apply(rawData: List<*>): List<Double> {
        val result = ArrayList<Double>()
        for (d in rawData) {
            result.add(myFun(d as Double))
        }
        return result
    }

    override fun applyInverse(v: Double): Any? {
        return myInverse(v)
    }

    override fun generate(domainAfterTransform: ClosedRange<Double>, targetCount: Int): ScaleBreaks {
        val domainBeforeTransform = MapperUtil.map(domainAfterTransform) { myInverse(it) }
        val originalBreaks = LinearBreaksGen().generate(domainBeforeTransform, targetCount)
        val domainValues = originalBreaks.domainValues
        val transformValues = ArrayList<Double>()
        for (domainValue in domainValues) {
            val transformed = myFun(domainValue)
            transformValues.add(transformed)
        }

        return ScaleBreaks(domainValues, transformValues, originalBreaks.labels)
    }
}
