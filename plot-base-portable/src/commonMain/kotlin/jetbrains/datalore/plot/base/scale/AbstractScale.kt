/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.scale

import jetbrains.datalore.base.gcommon.base.Preconditions
import jetbrains.datalore.plot.base.Scale
import jetbrains.datalore.plot.base.Transform

internal abstract class AbstractScale<DomainT, T> : Scale<T> {

    final override val name: String
    final override var mapper: ((Double?) -> T?)
        private set
    final override var multiplicativeExpand = 0.0
        protected set
    final override var additiveExpand = 0.0
        protected set

    private val myTransform: Transform?
    private var myBreaks: List<DomainT?>? = null
    private var myLabels: List<String>? = null

    override val isContinuous: Boolean
        get() = false

    override val isContinuousDomain: Boolean
        get() = false

    override var breaks: List<DomainT?>
        get() {
            Preconditions.checkState(hasBreaks(), "No breaks defined for scale $name")
            return myBreaks!!
        }
        protected set(breaks) {
            myBreaks = breaks
        }

    override val labels: List<String>
        get() {
            Preconditions.checkState(labelsDefined(), "No labels defined for scale $name")
            return myLabels!!
        }

    override val transform: Transform
        get() = myTransform ?: defaultTransform

    protected abstract val defaultTransform: Transform

    protected constructor(name: String, mapper: ((Double?) -> T?)) {
        this.name = name
        this.mapper = mapper
        myTransform = null
    }

    protected constructor(b: AbstractBuilder<DomainT, T>) {
        name = b.myName
        myBreaks = b.myBreaks
        myLabels = b.myLabels
        myTransform = b.myTransform
        mapper = b.myMapper

        multiplicativeExpand = b.myMultiplicativeExpand
        additiveExpand = b.myAdditiveExpand
    }

    override fun hasBreaks(): Boolean {
        return myBreaks != null
    }

    override fun hasLabels(): Boolean {
        return labelsDefined()
    }

    private fun labelsDefined(): Boolean {
        return myLabels != null
    }

    protected abstract class AbstractBuilder<DomainT, T>(scale: AbstractScale<DomainT, T>) : Scale.Builder<T> {
        internal val myName: String = scale.name
        internal var myTransform: Transform?

        internal var myBreaks: List<DomainT?>?
        internal var myLabels: List<String>?
        internal var myMapper: (Double?) -> T?

        internal var myMultiplicativeExpand: Double = 0.0
        internal var myAdditiveExpand: Double = 0.0

        init {
            myTransform = scale.myTransform
            myBreaks = scale.myBreaks
            myLabels = scale.myLabels
            myMapper = scale.mapper

            myMultiplicativeExpand = scale.multiplicativeExpand
            myAdditiveExpand = scale.additiveExpand
        }

        override fun breaks(l: List<*>): Scale.Builder<T> {
            myBreaks = ArrayList<DomainT>().let {
                for (any in l) {
                    @Suppress("UNCHECKED_CAST")
                    it.add(any as DomainT)
                }
                it
            }
            return this
        }

        override fun labels(l: List<String>): Scale.Builder<T> {
            myLabels = l
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

        protected fun transform(v: Transform): Scale.Builder<T> {
            myTransform = v
            return this
        }
    }
}
