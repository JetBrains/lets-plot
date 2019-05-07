package jetbrains.datalore.visualization.plot.gog.core.scale.transform

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.visualization.plot.gog.core.scale.ScaleBreaks
import jetbrains.datalore.visualization.plot.gog.core.scale.breaks.NumericBreakFormatter
import kotlin.math.log10
import kotlin.math.pow

internal class Log10Transform : FunTransform(F, F_INVERSE) {

    override fun generateBreaks(domainAfterTransform: ClosedRange<Double>, targetCount: Int): ScaleBreaks {
        val transformedBreaks = LinearBreaksGen().generateBreaks(domainAfterTransform, targetCount)
        val transformValues = transformedBreaks.domainValues
        val newDomainValues = ArrayList<Double>()
        for (transformValue in transformValues) {
            val domainValue = F_INVERSE(transformValue)
            newDomainValues.add(domainValue!!)
        }

        // format each tick with its own formatter
        val labels = ArrayList<String>()
        var step = 0.0
        val maxI = newDomainValues.size - 1
        for (i in 0..maxI) {
            val domainValue = newDomainValues[i]
            if (step == 0.0) {
                if (i < maxI) {
                    step = newDomainValues[i + 1] - domainValue
                }
            } else {
                step = domainValue - newDomainValues[i - 1]
            }
            val formatter = NumericBreakFormatter(domainValue, step, true)
            labels.add(formatter.apply(domainValue))
        }

        return ScaleBreaks(newDomainValues, transformValues, labels)
    }

    companion object {
        private val F: (Double?) -> Double? = { v ->
            if (v != null)
                log10(v)
            else
                null
        }
        private val F_INVERSE: (Double?) -> Double? = { v ->
            if (v != null)
                10.0.pow(v)
            else
                null
        }
    }
}
