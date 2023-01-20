/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.scale

import jetbrains.datalore.plot.base.Scale

internal abstract class AbstractScale<DomainT> : Scale {

    private val definedBreaks: List<DomainT>?
    private val definedLabels: List<String>?

    final override val name: String

    final override var multiplicativeExpand = 0.0
        protected set
    final override var additiveExpand = 0.0
        protected set
    final override val labelFormatter: ((Any) -> String)?

    protected constructor(name: String, breaks: List<DomainT>? = null) {
        this.name = name
        this.definedBreaks = breaks
        definedLabels = null
        labelFormatter = null
    }

    protected constructor(b: AbstractBuilder<DomainT>) {
        name = b.myName
        definedBreaks = b.myBreaks
        definedLabels = b.myLabels
        labelFormatter = b.myLabelFormatter

        multiplicativeExpand = b.myMultiplicativeExpand
        additiveExpand = b.myAdditiveExpand
    }

    override fun hasBreaks(): Boolean {
        return definedBreaks != null
    }

    protected fun hasDefinedBreaks() = definedBreaks != null

    protected open fun getBreaksIntern(): List<DomainT> {
        check(definedBreaks != null) { "No breaks defined for scale $name" }
        return definedBreaks
    }

    protected open fun getLabelsIntern(): List<String> {
        check(definedLabels != null) { "No labels defined for scale $name" }
        return definedLabels
    }

    override fun getScaleBreaks(): ScaleBreaks {
        if (!hasBreaks()) {
            return ScaleBreaks.EMPTY
        }

        val breakValuesIntern = getBreaksIntern()
        val labels = getLabels(breakValuesIntern)
        val transformCore = transform.unwrap() // make sure 'original' transform is used.
        val transformed = ScaleUtil.applyTransform(breakValuesIntern, transformCore)

        // drop NULLs which can occure after transform.
        val keepIndices: Set<Int> = transformed
            .mapIndexed { i, v -> if (v == null) null else i }
            .filterNotNull()
            .toSet()

        @Suppress("UNCHECKED_CAST")
        return ScaleBreaks(
            domainValues = breakValuesIntern.filterIndexed { i, _ -> i in keepIndices } as List<Any>,
            transformedValues = transformed.filterNotNull(),
            labels = labels.filterIndexed { i, _ -> i in keepIndices }
        )
    }

    private fun getLabels(breaks: List<DomainT>): List<String> {
        if (definedLabels != null) {
            val labels = getLabelsIntern()
            return when {
                labels.isEmpty() -> List(breaks.size) { "" }
                breaks.size <= labels.size -> labels.subList(0, breaks.size)
                else -> labels + List(breaks.size - labels.size) { "" }
            }
        }

        // generate labels
        val formatter: (Any) -> String = labelFormatter ?: { v: Any -> v.toString() }
        return breaks.map { formatter(it as Any) }
    }

    protected abstract class AbstractBuilder<DomainT>(scale: AbstractScale<DomainT>) : Scale.Builder {
        internal var myName: String = scale.name

        internal var myBreaks: List<DomainT>? = scale.definedBreaks
        internal var myLabels: List<String>? = scale.definedLabels
        internal var myLabelFormatter: ((Any) -> String)? = scale.labelFormatter

        internal var myMultiplicativeExpand: Double = scale.multiplicativeExpand
        internal var myAdditiveExpand: Double = scale.additiveExpand

        override fun name(v: String): Scale.Builder {
            myName = v
            return this
        }

        override fun breaks(l: List<Any>): Scale.Builder {
            myBreaks = l.map {
                @Suppress("UNCHECKED_CAST")
                it as DomainT
            }
            return this
        }

        override fun labels(l: List<String>): Scale.Builder {
            myLabels = l
            return this
        }

        override fun labelFormatter(v: (Any) -> String): Scale.Builder {
            myLabelFormatter = v
            return this
        }

        override fun multiplicativeExpand(v: Double): Scale.Builder {
            myMultiplicativeExpand = v
            return this
        }

        override fun additiveExpand(v: Double): Scale.Builder {
            myAdditiveExpand = v
            return this
        }
    }
}
