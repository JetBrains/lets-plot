/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.scale

import org.jetbrains.letsPlot.core.plot.base.Scale

internal abstract class AbstractScale<DomainT> : Scale {

    final override val name: String

    protected val providedBreaks: List<DomainT>?
    protected val providedLabels: List<String>?
    protected val providedScaleBreaks: ScaleBreaks?
    protected val providedFormatter: ((Any) -> String)?
    protected val labelLengthLimit: Int
    protected val superscriptExponent: Boolean

    private var createdScaleBreaks: ScaleBreaks? = null
    private var createdScaleBreaksShortened: Boolean = false

    final override var multiplicativeExpand = 0.0
        protected set
    final override var additiveExpand = 0.0
        protected set

    protected constructor(name: String) {
        this.name = name
        providedBreaks = null
        providedLabels = null
        providedScaleBreaks = null
        labelLengthLimit = 0
        providedFormatter = null
        superscriptExponent = false
    }

    protected constructor(b: AbstractBuilder<DomainT>) {
        name = b.myName
        providedBreaks = b.providedBreaks
        providedLabels = b.providedLabels
        providedScaleBreaks = b.providedScaleBreaks
        providedFormatter = b.providedFormatter

        labelLengthLimit = b.myLabelLengthLimit
        superscriptExponent = b.mySuperscriptExponent

        multiplicativeExpand = b.myMultiplicativeExpand
        additiveExpand = b.myAdditiveExpand
    }

    override fun hasBreaks(): Boolean {
        return providedBreaks != null || providedScaleBreaks != null
    }

    final override fun getScaleBreaks(): ScaleBreaks {
        return getScaleBreaksIntern(shortenLabels = false)
    }

    final override fun getShortenedScaleBreaks(): ScaleBreaks {
        return getScaleBreaksIntern(shortenLabels = true)
    }

    private fun getScaleBreaksIntern(shortenLabels: Boolean): ScaleBreaks {
        return if (createdScaleBreaks != null && createdScaleBreaksShortened == shortenLabels) {
            createdScaleBreaks!!
        } else {
            createdScaleBreaks = createScaleBreaks(shortenLabels)
            createdScaleBreaksShortened = shortenLabels
            createdScaleBreaks!!
        }
    }

    protected abstract fun createScaleBreaks(shortenLabels: Boolean): ScaleBreaks


    companion object {
        fun alignLablesAndBreaks(breaks: List<Any>, labels: List<String>): List<String> {
            return when {
                labels.isEmpty() -> List(breaks.size) { "" }
                breaks.size <= labels.size -> labels.subList(0, breaks.size)
                else -> labels + List(breaks.size - labels.size) { "" }
            }
        }
    }

    protected abstract class AbstractBuilder<DomainT>(scale: AbstractScale<DomainT>) : Scale.Builder {
        internal var myName: String = scale.name

        internal var providedBreaks: List<DomainT>? = scale.providedBreaks
        internal var providedLabels: List<String>? = scale.providedLabels
        internal var providedScaleBreaks: ScaleBreaks? = scale.providedScaleBreaks
        internal var myLabelLengthLimit: Int = scale.labelLengthLimit
        internal var providedFormatter: ((Any) -> String)? = scale.providedFormatter
        internal var mySuperscriptExponent: Boolean = scale.superscriptExponent

        internal var myMultiplicativeExpand: Double = scale.multiplicativeExpand
        internal var myAdditiveExpand: Double = scale.additiveExpand

        override fun name(v: String): Scale.Builder {
            myName = v
            return this
        }

        override fun breaks(l: List<Any>): Scale.Builder {
            providedBreaks = l.map {
                @Suppress("UNCHECKED_CAST")
                it as DomainT
            }
            return this
        }

        override fun labels(l: List<String>): Scale.Builder {
            providedLabels = l
            return this
        }

        override fun scaleBreaks(v: ScaleBreaks): Scale.Builder {
            providedScaleBreaks = v
            return this
        }

        override fun labelLengthLimit(v: Int): Scale.Builder {
            myLabelLengthLimit = v
            return this
        }

        override fun labelFormatter(v: (Any) -> String): Scale.Builder {
            providedFormatter = v
            return this
        }

        override fun superscriptExponent(v: Boolean): Scale.Builder {
            mySuperscriptExponent = v
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
