/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.scale

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

    override val breaks: List<Any>
        get() {
            check(hasBreaks()) { "No breaks defined for scale $name" }
            @Suppress("UNCHECKED_CAST")
            return definedBreaks as List<Any>
        }

    override val labels: List<String>
        get() {
            check(labelsDefined()) { "No labels defined for scale $name" }
            return definedLabels!!
        }

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

    override fun hasLabels(): Boolean {
        return labelsDefined()
    }

    private fun labelsDefined(): Boolean {
        return definedLabels != null
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
