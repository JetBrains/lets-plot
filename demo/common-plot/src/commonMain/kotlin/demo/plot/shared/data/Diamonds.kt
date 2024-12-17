/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.shared.data

object Diamonds {
    data class Column<T>(val name: String, val data: List<T>)

    private val sequence: List<List<String>>
            by lazy {
                demo.plot.shared.data.Diamonds.data.map { it.split(',') }
            }

    private fun int(column: Int): List<Int> {
        return demo.plot.shared.data.Diamonds.sequence.map { it.get(column).toInt() }
    }

    private fun double(column: Int): List<Double> {
        return demo.plot.shared.data.Diamonds.sequence.map { it.get(column).toDouble() }
    }

    private fun string(column: Int): List<String> {
        return demo.plot.shared.data.Diamonds.sequence.map { it.get(column).toString() }
    }

    val carat
        get() = demo.plot.shared.data.Diamonds.Column(
            "carat",
            demo.plot.shared.data.Diamonds.double(0)
        )
    val cut
        get() = demo.plot.shared.data.Diamonds.Column(
            "cut quality",
            demo.plot.shared.data.Diamonds.string(1)
        )
    val color
        get() = demo.plot.shared.data.Diamonds.Column(
            "diamond colour",     //from J (worst) to D (best)
            demo.plot.shared.data.Diamonds.string(2)
        )
    val clarity
        get() = demo.plot.shared.data.Diamonds.Column(
            "a measurement of how clear the diamond",
            demo.plot.shared.data.Diamonds.string(3)
        )
    val depth
        get() = demo.plot.shared.data.Diamonds.Column(
            "total depth percentage",
            demo.plot.shared.data.Diamonds.double(4)
        )
    val table
        get() = demo.plot.shared.data.Diamonds.Column(
            "width of top of diamond relative to widest point",
            demo.plot.shared.data.Diamonds.double(5)
        )
    val price
        get() = demo.plot.shared.data.Diamonds.Column(
            "price",
            demo.plot.shared.data.Diamonds.int(6)
        )
    val x
        get() = demo.plot.shared.data.Diamonds.Column(
            "length (mm)",
            demo.plot.shared.data.Diamonds.double(7)
        )
    val y
        get() = demo.plot.shared.data.Diamonds.Column(
            "width (mm)",
            demo.plot.shared.data.Diamonds.double(8)
        )
    val z
        get() = demo.plot.shared.data.Diamonds.Column(
            "depth (mm)",
            demo.plot.shared.data.Diamonds.double(9)
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
                demo.plot.shared.data.Diamonds.carat.name to demo.plot.shared.data.Diamonds.carat.data,
                demo.plot.shared.data.Diamonds.cut.name to demo.plot.shared.data.Diamonds.cut.data,
                demo.plot.shared.data.Diamonds.color.name to demo.plot.shared.data.Diamonds.color.data,
                demo.plot.shared.data.Diamonds.clarity.name to demo.plot.shared.data.Diamonds.clarity.data,
                demo.plot.shared.data.Diamonds.depth.name to demo.plot.shared.data.Diamonds.depth.data,
                demo.plot.shared.data.Diamonds.table.name to demo.plot.shared.data.Diamonds.table.data,
                demo.plot.shared.data.Diamonds.price.name to demo.plot.shared.data.Diamonds.price.data,
                demo.plot.shared.data.Diamonds.x.name to demo.plot.shared.data.Diamonds.x.data,
                demo.plot.shared.data.Diamonds.y.name to demo.plot.shared.data.Diamonds.y.data,
                demo.plot.shared.data.Diamonds.z.name to demo.plot.shared.data.Diamonds.z.data
            )
        }


    lateinit var data: List<String>
}

