/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.data

object Diamonds {
    data class Column<T>(val name: String, val data: List<T>)

    private val sequence: List<List<String>>
            by lazy {
                data.map { it.split(',') }
            }

    private fun int(column: Int): List<Int> {
        return sequence.map { it.get(column).toInt() }
    }

    private fun double(column: Int): List<Double> {
        return sequence.map { it.get(column).toDouble() }
    }

    private fun string(column: Int): List<String> {
        return sequence.map { it.get(column).toString() }
    }

    val carat
        get() = Column(
            "carat",
            double(0)
        )
    val cut
        get() = Column(
            "cut quality",
            string(1)
        )
    val color
        get() = Column(
            "diamond colour",     //from J (worst) to D (best)
            string(2)
        )
    val clarity
        get() = Column(
            "a measurement of how clear the diamond",
            string(3)
        )
    val depth
        get() = Column(
            "total depth percentage",
            double(4)
        )
    val table
        get() = Column(
            "width of top of diamond relative to widest point",
            double(5)
        )
    val price
        get() = Column(
            "price",
            int(6)
        )
    val x
        get() = Column(
            "length (mm)",
            double(7)
        )
    val y
        get() = Column(
            "width (mm)",
            double(8)
        )
    val z
        get() = Column(
            "depth (mm)",
            double(9)
        )
    val cutSet
        get() = listOf(
            "\"Fair\"",
            "\"Good\"",
            "\"Very Good\"",
            "\"Premium\"",
            "\"Ideal\""
        )
    val df
        get() = run {
            mapOf(
                carat.name to carat.data,
                cut.name to cut.data,
                color.name to color.data,
                clarity.name to clarity.data,
                depth.name to depth.data,
                table.name to table.data,
                price.name to price.data,
                x.name to x.data,
                y.name to y.data,
                z.name to z.data
            )
        }


    lateinit var data: List<String>
}

