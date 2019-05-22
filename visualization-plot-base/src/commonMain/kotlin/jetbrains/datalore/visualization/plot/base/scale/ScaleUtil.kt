package jetbrains.datalore.visualization.plot.base.scale

import jetbrains.datalore.base.gcommon.base.Preconditions.checkArgument
import jetbrains.datalore.base.gcommon.base.Preconditions.checkState
import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.plot.base.CoordinateSystem

object ScaleUtil {

    fun labels(scale: Scale2<*>): List<String> {
        if (!scale.hasBreaks()) {
            return emptyList()
        }

        val breaks = scale.breaks
        if (scale.hasLabels()) {
            val labels = scale.labels

            if (breaks.size <= labels.size) {
                return labels.subList(0, breaks.size)
            }

            val result = ArrayList<String>()
            for (i in breaks.indices) {
                if (labels.isEmpty()) {
                    result.add("")
                } else {
                    result.add(labels[i % labels.size])
                }
            }
            return result
        }

        // generate labels
        val result = ArrayList<String>()
        for (o in breaks) {
            result.add(o.toString())
        }
        return result
    }

    fun labelByBreak(scale: Scale2<*>): Map<Any, String> {
        val result = HashMap<Any, String>()
        if (scale.hasBreaks()) {
            val breaks = scale.breaks.iterator()
            val labels = labels(scale).iterator()
            while (breaks.hasNext() && labels.hasNext()) {
                result[breaks.next()!!] = labels.next()
            }
        }
        return result
    }

    fun breaksAsNumbers(scale: Scale2<*>): List<Double> {
        val breaks = scale.breaks
        val numbers = ArrayList<Double>()
        for (o in breaks) {
            numbers.add(scale.asNumber(o)!!)
        }
        return numbers
    }

    fun breaksTransformed(scale: Scale2<*>): List<Double> {
        return transform(scale.breaks, scale).map { it!! }
    }

    fun axisBreaks(scale: Scale2<Double>, coord: CoordinateSystem, horizontal: Boolean): List<Double> {
        val scaleBreaks = transformAndMap(scale.breaks, scale)
        val axisBreaks = ArrayList<Double>()
        for (br in scaleBreaks) {
            val mappedBrPoint = if (horizontal)
                DoubleVector(br!!, 0.0)
            else
                DoubleVector(0.0, br!!)

            val axisBrPoint = coord.toClient(mappedBrPoint)
            val axisBr = if (horizontal)
                axisBrPoint.x
            else
                axisBrPoint.y

            axisBreaks.add(axisBr)
            if (!axisBr.isFinite()) {
                throw IllegalStateException("Illegal axis '" + scale.name + "' break position " + axisBr +
                        " at index " + (axisBreaks.size - 1) +
                        "\nsource breaks    : " + scale.breaks +
                        "\ntranslated breaks: " + scaleBreaks +
                        "\naxis breaks      : " + axisBreaks)
            }
        }
        return axisBreaks
    }

    fun <T> breaksAesthetics(scale: Scale2<T>): List<T?> {
        return transformAndMap(scale.breaks, scale)
    }

    fun map(range: ClosedRange<Double>, scale: Scale2<Double>): ClosedRange<Double> {
        return MapperUtil.map(range, scale.mapper)
    }

    fun <T> map(d: Double?, scale: Scale2<T>): T? {
        return scale.mapper(d)
    }

    fun <T> map(d: List<Double?>, scale: Scale2<T>): List<T?> {
        val result = ArrayList<T?>()
        for (t in d) {
            result.add(map(t, scale))
        }
        return result
    }

    private fun <T> transformAndMap(l: List<*>, scale: Scale2<T>): List<T?> {
        val tl = transform(l, scale)
        return map(tl, scale)
    }

    fun transform(l: List<*>, scale: Scale2<*>): List<Double?> {
        return scale.transform.apply(l)
    }

    fun inverseTransformToContinuousDomain(l: List<Double?>, scale: Scale2<*>): List<Double?> {
        checkState(scale.isContinuousDomain, "Not continuous numeric domain: $scale")
        return inverseTransform(l, scale) as List<Double?>
    }

    fun inverseTransform(l: List<Double?>, scale: Scale2<*>): List<*> {
        val transform = scale.transform
        val result = ArrayList<Any?>(l.size)
        for (v in l) {
            result.add(transform.applyInverse(v))
        }
        return result
    }

    fun transformedDefinedLimits(scale: Scale2<*>): List<Double> {
        val result = ArrayList<Double>()
        val domainLimits = transform(definedLimits(scale), scale)
        for (x in domainLimits) {
            if (x!!.isFinite()) {
                result.add(x)
            }
        }
        return result
    }

    private fun definedLimits(scale: Scale2<*>): List<Double> {
        checkArgument(scale.isContinuousDomain, "Continuous scale is expected (" + scale.name + ")")
        val result = ArrayList<Double>()
        val domainLimits = scale.domainLimits
        if (domainLimits.lowerEndpoint().isFinite()) {
            result.add(domainLimits.lowerEndpoint())
        }
        if (domainLimits.upperEndpoint().isFinite()) {
            result.add(domainLimits.upperEndpoint())
        }
        return result
    }
}
