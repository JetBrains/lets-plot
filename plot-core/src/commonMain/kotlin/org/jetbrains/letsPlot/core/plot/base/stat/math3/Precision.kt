/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.stat.math3

import kotlin.jvm.JvmOverloads
import kotlin.math.abs


/**
 * Utilities for comparing numbers.
 *
 * @since 3.0
 * @version $Id$
 */
object Precision {
    /** Offset to order signed double numbers lexicographically.  */
    private val SGN_MASK: Long = (1 shl 63).toLong()
    /** Offset to order signed double numbers lexicographically.  */
    private val SGN_MASK_FLOAT = 1.shl(31)

    /**
     * Compares two numbers given some amount of allowed error.
     *
     * @param x the first number
     * @param y the second number
     * @param eps the amount of error to allow when checking for equality
     * @return  * 0 if  [equals(x, y, eps)][.equals]
     *  * &lt; 0 if ![equals(x, y, eps)][.equals] &amp;&amp; x &lt; y
     *  * > 0 if ![equals(x, y, eps)][.equals] &amp;&amp; x > y
     */
    fun compareTo(x: Double, y: Double, eps: Double): Int {
        if (equals(x, y, eps)) {
            return 0
        } else if (x < y) {
            return -1
        }
        return 1
    }

    /**
     * Compares two numbers given some amount of allowed error.
     * Two float numbers are considered equal if there are `(maxUlps - 1)`
     * (or fewer) floating point numbers between them, i.e. two adjacent floating
     * point numbers are considered equal.
     * Adapted from [
 * Bruce Dawson](https://www.cygnus-software.com/papers/comparingfloats/comparingfloats.htm)
     *
     * @param x first value
     * @param y second value
     * @param maxUlps `(maxUlps - 1)` is the number of floating point
     * values between `x` and `y`.
     * @return  * 0 if  [equals(x, y, maxUlps)][.equals]
     *  * &lt; 0 if ![equals(x, y, maxUlps)][.equals] &amp;&amp; x &lt; y
     *  * > 0 if ![equals(x, y, maxUlps)][.equals] &amp;&amp; x > y
     */
    fun compareTo(x: Double, y: Double, maxUlps: Int): Int {
        if (equals(x, y, maxUlps)) {
            return 0
        } else if (x < y) {
            return -1
        }
        return 1
    }

    /**
     * Returns true if both arguments are NaN or neither is NaN and they are
     * equal as defined by [equals(x, y, 1)][.equals].
     *
     * @param x first value
     * @param y second value
     * @return `true` if the values are equal or both are NaN.
     * @since 2.2
     */
    fun equalsIncludingNaN(x: Float, y: Float): Boolean {
        return x.isNaN() && y.isNaN() || equals(x, y, 1)
    }

    /**
     * Returns true if both arguments are equal or within the range of allowed
     * error (inclusive).
     *
     * @param x first value
     * @param y second value
     * @param eps the amount of absolute error to allow.
     * @return `true` if the values are equal or within range of each other.
     * @since 2.2
     */
    fun equals(x: Float, y: Float, eps: Float): Boolean {
        return equals(x, y, 1) || abs(y - x) <= eps
    }

    /**
     * Returns true if both arguments are NaN or are equal or within the range
     * of allowed error (inclusive).
     *
     * @param x first value
     * @param y second value
     * @param eps the amount of absolute error to allow.
     * @return `true` if the values are equal or within range of each other,
     * or both are NaN.
     * @since 2.2
     */
    fun equalsIncludingNaN(x: Float, y: Float, eps: Float): Boolean {
        return equalsIncludingNaN(x, y) || abs(y - x) <= eps
    }

    /**
     * Returns true if both arguments are equal or within the range of allowed
     * error (inclusive).
     * Two float numbers are considered equal if there are `(maxUlps - 1)`
     * (or fewer) floating point numbers between them, i.e. two adjacent floating
     * point numbers are considered equal.
     * Adapted from [
 * Bruce Dawson](https://www.cygnus-software.com/papers/comparingfloats/comparingfloats.htm)
     *
     * @param x first value
     * @param y second value
     * @param maxUlps `(maxUlps - 1)` is the number of floating point
     * values between `x` and `y`.
     * @return `true` if there are fewer than `maxUlps` floating
     * point values between `x` and `y`.
     * @since 2.2
     */
    @JvmOverloads
    fun equals(x: Float, y: Float, maxUlps: Int = 1): Boolean {
        var xInt = x.toBits()
        var yInt = y.toBits()

        // Make lexicographically ordered as a two's-complement integer.
        if (xInt < 0) {
            xInt = SGN_MASK_FLOAT - xInt
        }
        if (yInt < 0) {
            yInt = SGN_MASK_FLOAT - yInt
        }

        val isEqual = abs(xInt - yInt) <= maxUlps

        return isEqual && !x.isNaN() && !y.isNaN()
    }

    /**
     * Returns true if both arguments are NaN or if they are equal as defined
     * by [equals(x, y, maxUlps)][.equals].
     *
     * @param x first value
     * @param y second value
     * @param maxUlps `(maxUlps - 1)` is the number of floating point
     * values between `x` and `y`.
     * @return `true` if both arguments are NaN or if there are less than
     * `maxUlps` floating point values between `x` and `y`.
     * @since 2.2
     */
    fun equalsIncludingNaN(x: Float, y: Float, maxUlps: Int): Boolean {
        return x.isNaN() && y.isNaN() || equals(x, y, maxUlps)
    }

    /**
     * Returns true if both arguments are NaN or neither is NaN and they are
     * equal as defined by [equals(x, y, 1)][.equals].
     *
     * @param x first value
     * @param y second value
     * @return `true` if the values are equal or both are NaN.
     * @since 2.2
     */
    fun equalsIncludingNaN(x: Double, y: Double): Boolean {
        return x.isNaN() && y.isNaN() || equals(x, y, 1)
    }

    /**
     * Returns `true` if there is no double value strictly between the
     * arguments or the difference between them is within the range of allowed
     * error (inclusive).
     *
     * @param x First value.
     * @param y Second value.
     * @param eps Amount of allowed absolute error.
     * @return `true` if the values are two adjacent floating point
     * numbers or they are within range of each other.
     */
    fun equals(x: Double, y: Double, eps: Double): Boolean {
        return equals(x, y, 1) || abs(y - x) <= eps
    }

    /**
     * Returns true if both arguments are NaN or are equal or within the range
     * of allowed error (inclusive).
     *
     * @param x first value
     * @param y second value
     * @param eps the amount of absolute error to allow.
     * @return `true` if the values are equal or within range of each other,
     * or both are NaN.
     * @since 2.2
     */
    fun equalsIncludingNaN(x: Double, y: Double, eps: Double): Boolean {
        return equalsIncludingNaN(x, y) || abs(y - x) <= eps
    }

    /**
     * Returns true if both arguments are equal or within the range of allowed
     * error (inclusive).
     * Two float numbers are considered equal if there are `(maxUlps - 1)`
     * (or fewer) floating point numbers between them, i.e. two adjacent floating
     * point numbers are considered equal.
     * Adapted from [
 * Bruce Dawson](https://www.cygnus-software.com/papers/comparingfloats/comparingfloats.htm)
     *
     * @param x first value
     * @param y second value
     * @param maxUlps `(maxUlps - 1)` is the number of floating point
     * values between `x` and `y`.
     * @return `true` if there are fewer than `maxUlps` floating
     * point values between `x` and `y`.
     */
    @JvmOverloads
    fun equals(x: Double, y: Double, maxUlps: Int = 1): Boolean {
        var xInt = x.toBits()
        var yInt = y.toBits()

        // Make lexicographically ordered as a two's-complement integer.
        if (xInt < 0) {
            xInt = SGN_MASK - xInt
        }
        if (yInt < 0) {
            yInt = SGN_MASK - yInt
        }

        val isEqual = abs(xInt - yInt) <= maxUlps

        return isEqual && !x.isNaN() && !y.isNaN()
    }

    /**
     * Returns true if both arguments are NaN or if they are equal as defined
     * by [equals(x, y, maxUlps)][.equals].
     *
     * @param x first value
     * @param y second value
     * @param maxUlps `(maxUlps - 1)` is the number of floating point
     * values between `x` and `y`.
     * @return `true` if both arguments are NaN or if there are less than
     * `maxUlps` floating point values between `x` and `y`.
     * @since 2.2
     */
    fun equalsIncludingNaN(x: Double, y: Double, maxUlps: Int): Boolean {
        return x.isNaN() && y.isNaN() || equals(x, y, maxUlps)
    }


    /**
     * Computes a number `delta` close to `originalDelta` with
     * the property that <pre>`
     * x + delta - x
    `</pre> *
     * is exactly machine-representable.
     * This is useful when computing numerical derivatives, in order to reduce
     * roundoff errors.
     *
     * @param x Value.
     * @param originalDelta Offset value.
     * @return a number `delta` so that `x + delta` and `x`
     * differ by a representable floating number.
     */
    fun representableDelta(
        x: Double,
        originalDelta: Double
    ): Double {
        return x + originalDelta - x
    }
}
/**
 * Private constructor.
 */
/**
 * Returns true iff they are equal as defined by
 * [equals(x, y, 1)][.equals].
 *
 * @param x first value
 * @param y second value
 * @return `true` if the values are equal.
 */
/**
 * Returns true iff they are equal as defined by
 * [equals(x, y, 1)][.equals].
 *
 * @param x first value
 * @param y second value
 * @return `true` if the values are equal.
 */
/**
 * Rounds the given value to the specified number of decimal places.
 * The value is rounded using the [BigDecimal.ROUND_HALF_UP] method.
 *
 * @param x Value to round.
 * @param scale Number of digits to the right of the decimal point.
 * @return the rounded value.
 * @since 1.1 (previously in `MathUtils`, moved as of version 3.0)
 */
/**
 * Rounds the given value to the specified number of decimal places.
 * The value is rounded using the [BigDecimal.ROUND_HALF_UP] method.
 *
 * @param x Value to round.
 * @param scale Number of digits to the right of the decimal point.
 * @return the rounded value.
 * @since 1.1 (previously in `MathUtils`, moved as of version 3.0)
 */
