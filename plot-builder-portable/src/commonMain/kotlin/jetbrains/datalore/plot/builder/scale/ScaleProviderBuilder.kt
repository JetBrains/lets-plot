/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.scale

import jetbrains.datalore.base.gcommon.base.Preconditions
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.Scale
import jetbrains.datalore.plot.base.Transform
import jetbrains.datalore.plot.base.data.DataFrameUtil
import jetbrains.datalore.plot.base.scale.Scales

/**
 * see ggplot2: discrete_scale(...) / continuous_scale(...)
 * http://docs.ggplot2.org/current/discrete_scale.html
 * http://docs.ggplot2.org/current/continuous_scale.html
 */
class ScaleProviderBuilder<T>(private val myAes: Aes<T>) {

    private var myMapperProvider: MapperProvider<T>? = null
    private var myName: String? = null
    private var myBreaks: List<*>? = null
    private var myLabels: List<String>? = null
    private var myMultiplicativeExpand: Double? = null
    private var myAdditiveExpand: Double? = null
    private var myLimits: List<*>? = null
    private var myTransform: Transform? = null

    private var myDiscreteDomain = false


    init {
        myMapperProvider = DefaultMapperProvider[myAes]
    }

    fun mapperProvider(mapperProvider: MapperProvider<T>): ScaleProviderBuilder<T> {
        myMapperProvider = mapperProvider
        return this
    }

    fun name(name: String): ScaleProviderBuilder<T> {
        myName = name
        return this
    }

    fun breaks(breaks: List<*>): ScaleProviderBuilder<T> {
        myBreaks = breaks
        return this
    }

    fun minorBreaks_NI(minorBreaks: List<Double>): ScaleProviderBuilder<T> {
        // continuous scale
        throw IllegalStateException("Not implemented")
    }

    fun labels(labels: List<String>): ScaleProviderBuilder<T> {
        myLabels = ArrayList(labels)
        return this
    }

    fun multiplicativeExpand(v: Double): ScaleProviderBuilder<T> {
        myMultiplicativeExpand = v
        return this
    }

    fun additiveExpand(v: Double): ScaleProviderBuilder<T> {
        myAdditiveExpand = v
        return this
    }

    fun limits(v: List<*>): ScaleProviderBuilder<T> {
        // Limits for continuous scale : list(min, max)
        // Limits for discrete scale : list ("a", "b", "c")
        myLimits = v
        return this
    }

    fun rescaler_NI(v: Any): ScaleProviderBuilder<T> {
        throw IllegalStateException("Not implemented")
    }

    fun oob_NI(v: Any): ScaleProviderBuilder<T> {
        throw IllegalStateException("Not implemented")
    }

    fun transform(v: Transform): ScaleProviderBuilder<T> {
        myTransform = v
        return this
    }

    fun guide_NI(v: Any): ScaleProviderBuilder<T> {
        // Name of guide object, or object itself.
        throw IllegalStateException("Not implemented")
    }

    fun discreteDomain(b: Boolean): ScaleProviderBuilder<T> {
        myDiscreteDomain = b
        return this
    }

    fun build(): ScaleProvider<T> {
        Preconditions.checkState(myMapperProvider != null, "Mapper Provider is not specified")
        return MyScaleProvider(this)
    }

    private class MyScaleProvider<T>(b: ScaleProviderBuilder<T>) :
        ScaleProvider<T> {

        private val myName: String? = b.myName
        private val myBreaks: List<*>? = if (b.myBreaks == null) null else ArrayList(b.myBreaks!!)
        private val myLabels: List<String>? = if (b.myLabels == null) null else ArrayList(b.myLabels!!)
        private val myMultiplicativeExpand: Double? = b.myMultiplicativeExpand
        private val myAdditiveExpand: Double? = b.myAdditiveExpand
        private val myLimits: List<*>? = if (b.myLimits == null) null else ArrayList(b.myLimits!!)
        private val myDiscreteDomain: Boolean = b.myDiscreteDomain
        private val myContinuousTransform: Transform? = b.myTransform

        private val myAes: Aes<T> = b.myAes
        private val myMapperProvider: MapperProvider<T>? = b.myMapperProvider

        private fun scaleName(variable: DataFrame.Variable): String {
            return myName ?: variable.label
        }

        override fun createScale(data: DataFrame, variable: DataFrame.Variable): Scale<T> {
            val name = scaleName(variable)
            var scale: Scale<T>
            if (myDiscreteDomain || !data.isNumeric(variable)) {
                // discrete domain
                val mapper = if (data.isEmpty(variable)) {
                    absentMapper(variable)
                } else {
                    { v -> myMapperProvider!!.createDiscreteMapper(data, variable).apply(v) }
                }

                val domainValues = DataFrameUtil.distinctValues(data, variable).filterNotNull()
                scale = Scales.discreteDomain(
                        name,
                        domainValues,
                        mapper
                )

                if (myLimits != null) {
                    scale = scale.with()
                            .limits(HashSet(myLimits))
                            .build()
                }

            } else {
                // continuous (numeric) domain
                var lowerLimit: Double? = null
                var upperLimit: Double? = null
                if (myLimits != null) {
                    var lower = true
                    for (limit in myLimits) {
                        if (limit is Number) {
                            val v = limit.toDouble()
                            if (v.isFinite()) {
                                if (lower) {
                                    lowerLimit = v
                                } else {
                                    upperLimit = v
                                }
                            }
                        }
                        lower = false
                    }
                }

                if (data.isEmpty(variable)) {
                    scale = Scales.continuousDomain(name, absentMapper(variable), false)
                } else {
                    val mapper = myMapperProvider!!.createContinuousMapper(data, variable, lowerLimit, upperLimit, myContinuousTransform)
                    val continuousRange = mapper.isContinuous || myAes.isNumeric

                    scale = Scales.continuousDomain(name, { v -> mapper.apply(v) }, continuousRange)

                    if (mapper is WithGuideBreaks) {
                        val guideBreaks = (mapper as WithGuideBreaks).guideBreaks
                        val breaks = ArrayList<Any>()
                        val labels = ArrayList<String>()
                        for (guideBreak in guideBreaks) {
                            breaks.add(guideBreak.domainValue!!)
                            labels.add(guideBreak.label)
                        }
                        scale = scale.with()
                                .breaks(breaks)
                                .labels(labels)
                                .build()
                    }
                }

                if (myContinuousTransform != null) {
                    scale = scale.with()
                            .continuousTransform(myContinuousTransform)
                            .build()
                }

                if (myLimits != null) {
                    val with = scale.with()
                    if (lowerLimit != null) {
                        with.lowerLimit(lowerLimit)
                    }
                    if (upperLimit != null) {
                        with.upperLimit(upperLimit)
                    }
                    scale = with.build()
                }
            }

            return completeScale(scale)
        }

        private fun completeScale(scale: Scale<T>): Scale<T> {
            val with = scale.with()
            if (myBreaks != null) {
                with.breaks(myBreaks)
            }
            if (myLabels != null) {
                with.labels(myLabels)
            }

            if (myMultiplicativeExpand != null) {
                with.multiplicativeExpand(myMultiplicativeExpand)
            }
            if (myAdditiveExpand != null) {
                with.additiveExpand(myAdditiveExpand)
            }
            return with.build()
        }

        private fun absentMapper(`var`: DataFrame.Variable): (Double?) -> T {
            // mapper for empty data is a special case - should never be used
            return { v -> throw IllegalStateException("Mapper for empty data series '" + `var`.name + "' was invoked with arg " + v) }
        }
    }
}
