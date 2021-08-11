/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.scale

import jetbrains.datalore.plot.base.ContinuousTransform
import jetbrains.datalore.plot.base.Scale

internal abstract class AbstractScale<DomainT, T> : Scale<T> {

    private val definedBreaks: List<DomainT>?
    private val definedLabels: List<String>?

    final override val name: String
    final override val mapper: ((Double?) -> T?)
    final override var multiplicativeExpand = 0.0
        protected set
    final override var additiveExpand = 0.0
        protected set
    final override val labelFormatter: ((Any) -> String)?

    override val isContinuous: Boolean
        get() = false

    override val isContinuousDomain: Boolean
        get() = false

    protected constructor(name: String, mapper: ((Double?) -> T?), breaks: List<DomainT>? = null) {
        this.name = name
        this.mapper = mapper
        this.definedBreaks = breaks
        definedLabels = null
        labelFormatter = null
    }

    protected constructor(b: AbstractBuilder<DomainT, T>) {
        name = b.myName
        definedBreaks = b.myBreaks
        definedLabels = b.myLabels
        labelFormatter = b.myLabelFormatter
        mapper = b.myMapper

        multiplicativeExpand = b.myMultiplicativeExpand
        additiveExpand = b.myAdditiveExpand
    }

    override fun hasBreaks(): Boolean {
        return definedBreaks != null
    }

    protected open fun getBreaksIntern(): List<Any> {
        check(hasBreaks()) { "No breaks defined for scale $name" }
        @Suppress("UNCHECKED_CAST")
        return definedBreaks as List<Any>
    }

    protected open fun getLabelsIntern(): List<String> {
        check(definedLabels != null) { "No labels defined for scale $name" }
        return definedLabels
    }

    override fun applyTransform(source: List<*>, checkLimits: Boolean): List<Double?> {
        @Suppress("NAME_SHADOWING")
        var source: List<Any?> = source

        // Replace values outside 'scale limits' with null-s.
        if (checkLimits && hasDomainLimits()) {
            source = source.map { if (it == null || isInDomainLimits(it)) it else null }
        }

        // Replace values outside of domain of 'continuous transform' with null-s.
        if (transform is ContinuousTransform) {
            val continuousTransform = transform as ContinuousTransform
            if (continuousTransform.hasDomainLimits()) {
                source = source.map { if (continuousTransform.isInDomain(it as Double?)) it else null }
            }
        }

        return transform.apply(source)
    }

    override fun getScaleBreaks(): ScaleBreaks {
        if (!hasBreaks()) {
            return ScaleBreaks.EMPTY
        }

        val breakValuesIntern = getBreaksIntern()
        val labels = getLabels(breakValuesIntern)
        val transformed = applyTransform(breakValuesIntern, checkLimits = false)

        // drop NULLs which can occure after transform.
        val keepIndices: Set<Int> = transformed
            .mapIndexed { i, v -> if (v == null) null else i }
            .filterNotNull()
            .toSet()

        return ScaleBreaks(
            domainValues = breakValuesIntern.filterIndexed { i, _ -> i in keepIndices },
            transformedValues = transformed.filterNotNull(),
            labels = labels.filterIndexed { i, _ -> i in keepIndices }
        )
    }

    private fun getLabels(breaks: List<Any>): List<String> {
        if (definedLabels != null) {
            val labels = getLabelsIntern()
            return when {
                labels.isEmpty() -> List(breaks.size) { "" }
                breaks.size <= labels.size -> labels.subList(0, breaks.size)
                else -> List(breaks.size) { i -> labels[i % labels.size] }
            }
        }

        // generate labels
        val formatter: (Any) -> String = labelFormatter ?: { v: Any -> v.toString() }
        return breaks.map { formatter(it) }
    }

    protected abstract class AbstractBuilder<DomainT, T>(scale: AbstractScale<DomainT, T>) : Scale.Builder<T> {
        internal val myName: String = scale.name

        internal var myBreaks: List<DomainT>? = scale.definedBreaks
        internal var myLabels: List<String>? = scale.definedLabels
        internal var myLabelFormatter: ((Any) -> String)? = scale.labelFormatter
        internal var myMapper: (Double?) -> T? = scale.mapper

        internal var myMultiplicativeExpand: Double = scale.multiplicativeExpand
        internal var myAdditiveExpand: Double = scale.additiveExpand

        override fun breaks(l: List<Any>): Scale.Builder<T> {
            myBreaks = l.map {
                @Suppress("UNCHECKED_CAST")
                it as DomainT
            }
            return this
        }

        override fun labels(l: List<String>): Scale.Builder<T> {
            myLabels = l
            return this
        }

        override fun labelFormatter(v: (Any) -> String): Scale.Builder<T> {
            myLabelFormatter = v
            return this
        }

        override fun mapper(m: (Double?) -> T?): Scale.Builder<T> {
            myMapper = m
            return this
        }

        override fun multiplicativeExpand(v: Double): Scale.Builder<T> {
            myMultiplicativeExpand = v
            return this
        }

        override fun additiveExpand(v: Double): Scale.Builder<T> {
            myAdditiveExpand = v
            return this
        }
    }
}
