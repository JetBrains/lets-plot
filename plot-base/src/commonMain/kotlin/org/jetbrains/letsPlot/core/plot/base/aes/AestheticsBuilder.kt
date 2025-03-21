/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.aes

import org.jetbrains.letsPlot.commons.intern.function.Function
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.ALPHA
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.ANGLE
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.COLOR
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.EXPLODE
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.FAMILY
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.FILL
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.FLOW
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.FONTFACE
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.FRAME
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.HEIGHT
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.HJUST
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.LABEL
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.LINEHEIGHT
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.LINETYPE
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.LINEWIDTH
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.MAP_ID
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.POINT_SIZE
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.RADIUS
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.SHAPE
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.SIZE
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.SIZE_END
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.SIZE_START
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.SLICE
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.SPEED
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.STROKE
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.STROKE_END
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.STROKE_START
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.VIOLINWIDTH
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.VJUST
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.WEIGHT
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.WIDTH
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.X
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.XMAX
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.XMIN
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.Y
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.YMAX
import org.jetbrains.letsPlot.core.plot.base.Aes.Companion.YMIN
import org.jetbrains.letsPlot.core.plot.base.Aesthetics
import org.jetbrains.letsPlot.core.plot.base.DataPointAesthetics
import org.jetbrains.letsPlot.core.plot.base.ScaleMapper
import org.jetbrains.letsPlot.core.plot.base.render.linetype.LineType
import org.jetbrains.letsPlot.core.plot.base.render.point.PointShape
import kotlin.jvm.JvmOverloads

class AestheticsBuilder @JvmOverloads constructor(private var myDataPointCount: Int = 0) {

    private val myIndexFunctionMap: MutableMap<Aes<*>, (Int) -> Any?>
    private var myGroup = constant(0)
    private val myConstantAes = HashSet(Aes.values())  // initially contains all Aes;
    private var myColorAes: Aes<Color> = COLOR
    private var myFillAes: Aes<Color> = FILL
    private var myResolutionByAes: MutableMap<Aes<*>, Double> = HashMap()

    init {
        myIndexFunctionMap = HashMap()
        for (aes in Aes.values()) {
            // Safe cast because AesInitValue.get(aes) is guaranteed to return correct type.
            myIndexFunctionMap[aes] =
                constant(
                    AesInitValue[aes]
                )
        }
    }

    fun dataPointCount(v: Int): AestheticsBuilder {
        myDataPointCount = v
        return this
    }

    fun x(v: (Int) -> Double?): AestheticsBuilder {
        return aes(X, v)
    }

    fun y(v: (Int) -> Double?): AestheticsBuilder {
        return aes(Y, v)
    }

    fun color(v: (Int) -> Color?): AestheticsBuilder {
        return aes(myColorAes, v)
    }

    fun fill(v: (Int) -> Color?): AestheticsBuilder {
        return aes(myFillAes, v)
    }

    fun alpha(v: (Int) -> Double?): AestheticsBuilder {
        return aes(ALPHA, v)
    }

    fun shape(v: (Int) -> PointShape?): AestheticsBuilder {
        return aes(SHAPE, v)
    }

    fun lineType(v: (Int) -> LineType?): AestheticsBuilder {
        return aes(LINETYPE, v)
    }

    fun size(v: (Int) -> Double?): AestheticsBuilder {
        return aes(SIZE, v)
    }

    fun stroke(v: (Int) -> Double?): AestheticsBuilder {
        return aes(STROKE, v)
    }

    fun linewidth(v: (Int) -> Double?): AestheticsBuilder {
        return aes(LINEWIDTH, v)
    }

    fun width(v: (Int) -> Double?): AestheticsBuilder {
        return aes(WIDTH, v)
    }

    fun height(v: (Int) -> Double?): AestheticsBuilder {
        return aes(HEIGHT, v)
    }

    fun violinwidth(v: (Int) -> Double?): AestheticsBuilder {
        return aes(VIOLINWIDTH, v)
    }

    fun weight(v: (Int) -> Double?): AestheticsBuilder {
        return aes(WEIGHT, v)
    }

    fun mapId(v: (Int) -> Any?): AestheticsBuilder {
        return aes(MAP_ID, v)
    }

    fun frame(v: (Int) -> String?): AestheticsBuilder {
        return aes(FRAME, v)
    }

    fun speed(v: (Int) -> Double?): AestheticsBuilder {
        return aes(SPEED, v)
    }

    fun flow(v: (Int) -> Double?): AestheticsBuilder {
        return aes(FLOW, v)
    }

    fun group(v: (Int) -> Int): AestheticsBuilder {
        myGroup = v
        return this
    }

    fun label(v: (Int) -> Any?): AestheticsBuilder {
        return aes(LABEL, v)
    }

    fun family(v: (Int) -> String?): AestheticsBuilder {
        return aes(FAMILY, v)
    }

    fun fontface(v: (Int) -> String?): AestheticsBuilder {
        return aes(FONTFACE, v)
    }

    fun lineheight(v: (Int) -> Double?): AestheticsBuilder {
        return aes(LINEHEIGHT, v)
    }

    fun hjust(v: (Int) -> Any?): AestheticsBuilder {
        return aes(HJUST, v)
    }

    fun vjust(v: (Int) -> Any?): AestheticsBuilder {
        return aes(VJUST, v)
    }

    fun angle(v: (Int) -> Double?): AestheticsBuilder {
        return aes(ANGLE, v)
    }

    fun radius(v: (Int) -> Double?): AestheticsBuilder {
        return aes(RADIUS, v)
    }

    fun xmin(v: (Int) -> Double?): AestheticsBuilder {
        return aes(XMIN, v)
    }

    fun xmax(v: (Int) -> Double?): AestheticsBuilder {
        return aes(XMAX, v)
    }

    fun ymin(v: (Int) -> Double?): AestheticsBuilder {
        return aes(YMIN, v)
    }

    fun ymax(v: (Int) -> Double?): AestheticsBuilder {
        return aes(YMAX, v)
    }

    fun slice(v: (Int) -> Double?): AestheticsBuilder {
        return aes(SLICE, v)
    }

    fun explode(v: (Int) -> Double?): AestheticsBuilder {
        return aes(EXPLODE, v)
    }

    fun sizeStart(v: (Int) -> Double?): AestheticsBuilder {
        return aes(SIZE_START, v)
    }

    fun startEnd(v: (Int) -> Double?): AestheticsBuilder {
        return aes(SIZE_END, v)
    }

    fun strokeStart(v: (Int) -> Double?): AestheticsBuilder {
        return aes(STROKE_START, v)
    }

    fun strokeEnd(v: (Int) -> Double?): AestheticsBuilder {
        return aes(STROKE_END, v)
    }

    fun pointSize(v: (Int) -> Double?): AestheticsBuilder {
        return aes(POINT_SIZE, v)
    }

    fun <T> constantAes(aes: Aes<T>, v: T?): AestheticsBuilder {
        myConstantAes.add(aes)
        myIndexFunctionMap[aes] = constant(v)
        return this
    }

    fun <T> aes(aes: Aes<T>, v: (Int) -> T?): AestheticsBuilder {
        myConstantAes.remove(aes)
        myIndexFunctionMap[aes] = v
        return this
    }

    fun colorAes(aes: Aes<Color>): AestheticsBuilder {
        myColorAes = aes
        return this
    }

    fun fillAes(aes: Aes<Color>): AestheticsBuilder {
        myFillAes = aes
        return this
    }

    fun resolution(aes: Aes<Any>, v: Double): AestheticsBuilder {
        myResolutionByAes[aes] = v
        return this
    }

    fun build(): Aesthetics {
        return MyAesthetics(this)
    }


    private class MyAesthetics internal constructor(b: AestheticsBuilder) : Aesthetics {
        private val myDataPointCount: Int = b.myDataPointCount
        private val myIndexFunctionMap = TypedIndexFunctionMap(b.myIndexFunctionMap)
        val group = b.myGroup
        private val myConstantAes: Set<Aes<*>> = HashSet(b.myConstantAes)

        private val myResolutionByAes = HashMap<Aes<*>, Double>().also {
            it.putAll(b.myResolutionByAes)
        }
        private val myRangeByNumericAes = HashMap<Aes<Double>, DoubleSpan?>()

        val colorAes = b.myColorAes
        val fillAes = b.myFillAes

        override val isEmpty: Boolean
            get() = myDataPointCount == 0

        fun <T> aes(aes: Aes<T>): (Int) -> T {
            return myIndexFunctionMap[aes]
        }

        override fun dataPointAt(index: Int): DataPointAesthetics {
            return MyDataPointAesthetics(
                index,
                this
            )
        }

        override fun dataPointCount(): Int {
            return myDataPointCount
        }

        override fun dataPoints(): Iterable<DataPointAesthetics> {
            val self = this
            return object : Iterable<DataPointAesthetics> {
                override fun iterator(): Iterator<DataPointAesthetics> =
                    MyDataPointsIterator(
                        myDataPointCount,
                        self
                    )
            }
        }

        override fun range(aes: Aes<Double>): DoubleSpan? {
            if (!myRangeByNumericAes.containsKey(aes)) {
                val r = when {
                    myDataPointCount <= 0 -> null
                    myConstantAes.contains(aes) -> {
                        val v = numericValues(aes).iterator().next()
                        if (v != null && v.isFinite()) {
                            DoubleSpan(v, v)
                        } else null
                    }

                    else -> {
                        val values = numericValues(aes)
                        DoubleSpan.encloseAllQ(values)
                    }
                }
                myRangeByNumericAes[aes] = r
            }

            return myRangeByNumericAes[aes]
        }

        override fun resolution(aes: Aes<Double>, naValue: Double): Double {
            if (!myResolutionByAes.containsKey(aes)) {
                val resolution: Double =
                    when {
                        myConstantAes.contains(aes) -> 0.0
                        else -> {
                            val values = numericValues(aes)
                            SeriesUtil.resolution(values, naValue)
                        }
                    }
                myResolutionByAes[aes] = resolution
            }

            return myResolutionByAes[aes]!!
        }

        override fun numericValues(aes: Aes<Double>): Iterable<Double?> {
            require(aes.isNumeric) { "Numeric aes is expected: $aes" }
            return object : Iterable<Double> {
                override fun iterator(): Iterator<Double> {
                    return AesIterator(
                        myDataPointCount,
                        aes(aes)
                    )
                }
            }
        }

        override fun groups(): Iterable<Int> {
            return object : Iterable<Int> {
                override fun iterator(): Iterator<Int> {
                    return AesIterator(
                        myDataPointCount,
                        group
                    )
                }
            }
        }
    }

    private class MyDataPointsIterator internal constructor(
        private val myLength: Int,
        private val myAesthetics: MyAesthetics
    ) : Iterator<DataPointAesthetics> {
        private var myIndex = 0

        override fun hasNext(): Boolean {
            return myIndex < myLength
        }

        override fun next(): DataPointAesthetics {
            if (hasNext()) {
                return myAesthetics.dataPointAt(myIndex++)
            }
            throw NoSuchElementException("index=$myIndex")
        }
    }

    private class AesIterator<T> internal constructor(private val myLength: Int, private val myAes: (Int) -> T) :
        Iterator<T> {
        private var myIndex = 0

        override fun hasNext(): Boolean {
            return myIndex < myLength
        }

        override fun next(): T {
            if (hasNext()) {
                return myAes(myIndex++)
            }
            throw NoSuchElementException("index=$myIndex")
        }
    }


    private class MyDataPointAesthetics(
        private val myIndex: Int,
        private val myAesthetics: MyAesthetics
    ) : DataPointAesthetics() {

        override fun index(): Int {
            return myIndex
        }

        override fun group(): Int {
            return myAesthetics.group(myIndex)
        }

        override fun <T> get(aes: Aes<T>): T? {
            return myAesthetics.aes(aes)(myIndex)
        }

        override val colorAes: Aes<Color> = myAesthetics.colorAes

        override val fillAes: Aes<Color> = myAesthetics.fillAes
    }


    private class ArrayAes<ValueT> internal constructor(
        private val myVal: Array<out ValueT>
    ) : Function<Int, ValueT> {
        override fun apply(value: Int): ValueT {
            return myVal[value]
        }
    }

    private class MapperAes<ValueT> internal constructor(
        private val myL: List<Double>,
        private val myF: ((Double) -> ValueT)
    ) : Function<Int, ValueT> {
        override fun apply(value: Int): ValueT {
            return myF(myL[value])
        }
    }

    companion object {
        fun <T> constant(v: T): (Int) -> T = { v }

        fun <T> array(v: Array<T>): (Int) -> T {
            return { index -> v[index] }
        }

        fun <T> list(v: List<T>): (Int) -> T {
            return { index -> v[index] }
        }

        fun <T> listMapper(v: List<Double?>, f: ScaleMapper<T>): (Int) -> T? {
            return { value -> f(v[value]) }
        }
    }
}
