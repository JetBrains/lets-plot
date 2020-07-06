/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.aes

import jetbrains.datalore.base.function.Function
import jetbrains.datalore.base.gcommon.base.Preconditions.checkArgument
import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.gcommon.collect.Iterables
import jetbrains.datalore.base.gcommon.collect.Sets
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.Aes.Companion.ALPHA
import jetbrains.datalore.plot.base.Aes.Companion.ANGLE
import jetbrains.datalore.plot.base.Aes.Companion.COLOR
import jetbrains.datalore.plot.base.Aes.Companion.FAMILY
import jetbrains.datalore.plot.base.Aes.Companion.FILL
import jetbrains.datalore.plot.base.Aes.Companion.FLOW
import jetbrains.datalore.plot.base.Aes.Companion.FONTFACE
import jetbrains.datalore.plot.base.Aes.Companion.FRAME
import jetbrains.datalore.plot.base.Aes.Companion.HEIGHT
import jetbrains.datalore.plot.base.Aes.Companion.HJUST
import jetbrains.datalore.plot.base.Aes.Companion.INTERCEPT
import jetbrains.datalore.plot.base.Aes.Companion.LABEL
import jetbrains.datalore.plot.base.Aes.Companion.LINETYPE
import jetbrains.datalore.plot.base.Aes.Companion.LOWER
import jetbrains.datalore.plot.base.Aes.Companion.MIDDLE
import jetbrains.datalore.plot.base.Aes.Companion.SHAPE
import jetbrains.datalore.plot.base.Aes.Companion.SIZE
import jetbrains.datalore.plot.base.Aes.Companion.SLOPE
import jetbrains.datalore.plot.base.Aes.Companion.SPEED
import jetbrains.datalore.plot.base.Aes.Companion.SYM_X
import jetbrains.datalore.plot.base.Aes.Companion.SYM_Y
import jetbrains.datalore.plot.base.Aes.Companion.UPPER
import jetbrains.datalore.plot.base.Aes.Companion.VJUST
import jetbrains.datalore.plot.base.Aes.Companion.WEIGHT
import jetbrains.datalore.plot.base.Aes.Companion.WIDTH
import jetbrains.datalore.plot.base.Aes.Companion.X
import jetbrains.datalore.plot.base.Aes.Companion.XEND
import jetbrains.datalore.plot.base.Aes.Companion.XINTERCEPT
import jetbrains.datalore.plot.base.Aes.Companion.XMAX
import jetbrains.datalore.plot.base.Aes.Companion.XMIN
import jetbrains.datalore.plot.base.Aes.Companion.Y
import jetbrains.datalore.plot.base.Aes.Companion.YEND
import jetbrains.datalore.plot.base.Aes.Companion.YINTERCEPT
import jetbrains.datalore.plot.base.Aes.Companion.YMAX
import jetbrains.datalore.plot.base.Aes.Companion.YMIN
import jetbrains.datalore.plot.base.Aes.Companion.Z
import jetbrains.datalore.plot.base.Aesthetics
import jetbrains.datalore.plot.base.DataPointAesthetics
import jetbrains.datalore.plot.base.render.linetype.LineType
import jetbrains.datalore.plot.base.render.point.PointShape
import jetbrains.datalore.plot.common.data.SeriesUtil
import kotlin.jvm.JvmOverloads

class AestheticsBuilder @JvmOverloads constructor(private var myDataPointCount: Int = 0) {

    private val myIndexFunctionMap: MutableMap<Aes<*>, (Int) -> Any?>
    private var myGroup = constant(0)
    private val myConstantAes = Sets.newHashSet(Aes.values())  // initially contains all Aes;
    private val myOverallRangeByNumericAes = HashMap<Aes<Double>, ClosedRange<Double>>()

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

    fun overallRange(aes: Aes<Double>, range: ClosedRange<Double>): AestheticsBuilder {
        // It's full range (length px) of x/y axis
        myOverallRangeByNumericAes[aes] = range
        return this
    }

    fun x(v: (Int) -> Double?): AestheticsBuilder {
        return aes(X, v)
    }

    fun y(v: (Int) -> Double?): AestheticsBuilder {
        return aes(Y, v)
    }

    fun color(v: (Int) -> Color?): AestheticsBuilder {
        return aes(COLOR, v)
    }

    fun fill(v: (Int) -> Color?): AestheticsBuilder {
        return aes(FILL, v)
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

    fun width(v: (Int) -> Double?): AestheticsBuilder {
        return aes(WIDTH, v)
    }

    fun weight(v: (Int) -> Double?): AestheticsBuilder {
        return aes(WEIGHT, v)
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

    fun hjust(v: (Int) -> Any?): AestheticsBuilder {
        return aes(HJUST, v)
    }

    fun vjust(v: (Int) -> Any?): AestheticsBuilder {
        return aes(VJUST, v)
    }

    fun angle(v: (Int) -> Double?): AestheticsBuilder {
        return aes(ANGLE, v)
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

    fun symX(v: (Int) -> Double?): AestheticsBuilder {
        return aes(SYM_X, v)
    }

    fun symY(v: (Int) -> Double?): AestheticsBuilder {
        return aes(SYM_Y, v)
    }

    fun <T> constantAes(aes: Aes<T>, v: T): AestheticsBuilder {
        myConstantAes.add(aes)
        myIndexFunctionMap[aes] = constant(v)
        return this
    }

    fun <T> aes(aes: Aes<T>, v: (Int) -> T?): AestheticsBuilder {
        myConstantAes.remove(aes)
        myIndexFunctionMap[aes] = v
        return this
    }

    fun build(): Aesthetics {
        return MyAesthetics(this)
    }


    private class MyAesthetics internal constructor(b: AestheticsBuilder) : Aesthetics {
        private val myDataPointCount: Int = b.myDataPointCount
        private val myIndexFunctionMap =
            TypedIndexFunctionMap(b.myIndexFunctionMap)
        val group = b.myGroup
        private val myConstantAes: Set<Aes<*>>
        private val myOverallRangeByNumericAes: Map<Aes<Double>, ClosedRange<Double>>

        private val myResolutionByAes = HashMap<Aes<*>, Double>()
        private val myRangeByNumericAes = HashMap<Aes<Double>, ClosedRange<Double>?>()

        override val isEmpty: Boolean
            get() = myDataPointCount == 0

        init {
            myConstantAes = HashSet(b.myConstantAes)
            myOverallRangeByNumericAes = HashMap(b.myOverallRangeByNumericAes)
        }

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

        override fun range(aes: Aes<Double>): ClosedRange<Double>? {
            if (!myRangeByNumericAes.containsKey(aes)) {
                val r = when {
                    myDataPointCount <= 0 ->
                        ClosedRange.closed(0.0, 0.0)
                    myConstantAes.contains(aes) -> {
                        // constant should not be null
                        val v = numericValues(aes).iterator().next()!!
                        ClosedRange.closed(v, v)
                    }
                    else -> {
                        val values = numericValues(aes)
                        SeriesUtil.range(values)
                    }
                }
                myRangeByNumericAes[aes] = r
            }

            return myRangeByNumericAes[aes]
        }

        override fun overallRange(aes: Aes<Double>): ClosedRange<Double> {
            return myOverallRangeByNumericAes[aes] ?: error("Overall range is unknown for $aes")
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
            checkArgument(aes.isNumeric, "Numeric aes is expected: $aes")
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
        private val myIndex: Int?,
        private val myAesthetics: MyAesthetics
    ) : DataPointAesthetics {

        override fun index(): Int {
            return myIndex!!
        }

        override fun x(): Double? {
            return get(X)
        }

        override fun y(): Double? {
            return get(Y)
        }

        override fun z(): Double? {
            return get(Z)
        }

        override fun ymin(): Double? {
            return get(YMIN)
        }

        override fun ymax(): Double? {
            return get(YMAX)
        }

        override fun color(): Color {
            return get(COLOR)
        }

        override fun fill(): Color {
            return get(FILL)
        }

        override fun alpha(): Double? {
            return get(ALPHA)
        }

        override fun shape(): PointShape {
            return get(SHAPE)
        }

        override fun lineType(): LineType {
            return get(LINETYPE)
        }

        override fun size(): Double? {
            return get(SIZE)
        }

        override fun width(): Double? {
            return get(WIDTH)
        }

        override fun height(): Double? {
            return get(HEIGHT)
        }

        override fun weight(): Double? {
            return get(WEIGHT)
        }

        override fun intercept(): Double? {
            return get(INTERCEPT)
        }

        override fun slope(): Double? {
            return get(SLOPE)
        }

        override fun interceptX(): Double? {
            return get(XINTERCEPT)
        }

        override fun interceptY(): Double? {
            return get(YINTERCEPT)
        }

        override fun lower(): Double? {
            return get(LOWER)
        }

        override fun middle(): Double? {
            return get(MIDDLE)
        }

        override fun upper(): Double? {
            return get(UPPER)
        }

        override fun frame(): String {
            return get(FRAME)
        }

        override fun speed(): Double? {
            return get(SPEED)
        }

        override fun flow(): Double? {
            return get(FLOW)
        }

        override fun xmin(): Double? {
            return get(XMIN)
        }

        override fun xmax(): Double? {
            return get(XMAX)
        }

        override fun xend(): Double? {
            return get(XEND)
        }

        override fun yend(): Double? {
            return get(YEND)
        }

        override fun label(): Any? {
            return get(LABEL)
        }

        override fun family(): String {
            return get(FAMILY)
        }

        override fun fontface(): String {
            return get(FONTFACE)
        }

        override fun hjust(): Any {
            return get(HJUST)
        }

        override fun vjust(): Any {
            return get(VJUST)
        }

        override fun angle(): Double? {
            return get(ANGLE)
        }

        override fun symX(): Double? {
            return get(SYM_X)
        }

        override fun symY(): Double? {
            return get(SYM_Y)
        }

        override fun group(): Int? {
            return myAesthetics.group(myIndex!!)
        }

        override fun numeric(aes: Aes<Double>): Double? {
            return get(aes)
        }

        override fun <T> get(aes: Aes<T>): T {
            return myAesthetics.aes(aes)(myIndex!!)
        }
    }


    private class ArrayAes<ValueT> internal constructor(private val myVal: Array<out ValueT>) : Function<Int, ValueT> {
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
            return { value -> v[value] }
        }

//        fun <T> array(vararg v: T): (Int) -> T {
//            return { value -> v[value] }
//        }

        fun <T> collection(v: Collection<T>): (Int) -> T {
            return { value -> Iterables[v, value] }
        }

        fun <T> listMapper(v: List<Double?>, f: (Double?) -> T?): (Int) -> T? {
            return { value -> f(v[value]) }
        }
    }

}
