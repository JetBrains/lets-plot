/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.shared.data

object Diamonds {
    data class Column<T>(val name: String, val data: List<T>)

    private val sequence: List<List<String>>
            by lazy {
                data.map { it.split(',') }
            }

    private fun int(column: Int): List<Int> {
        return sequence.map { it[column].toInt() }
    }

    private fun double(column: Int): List<Double> {
        return sequence.map { it[column].toDouble() }
    }

    private fun string(column: Int): List<String> {
        return sequence.map { it[column] }
    }

    val carat get() = Column("carat", double(0))
    val cut get() = Column("cut quality", string(1))
    val color get() = Column("diamond colour", string(2)) //from J (worst) to D (best)
    val clarity get() = Column("a measurement of how clear the diamond", string(3))
    val depth get() = Column("total depth percentage", double(4))
    val table get() = Column("width of top of diamond relative to widest point", double(5))
    val price get() = Column("price", int(6))
    val x get() = Column("length (mm)", double(7))
    val y get() = Column("width (mm)", double(8))
    val z get() = Column("depth (mm)", double(9))
    val cutSet get() = listOf("\"Fair\"", "\"Good\"", "\"Very Good\"", "\"Premium\"", "\"Ideal\"")
    val df
        get() = run {
            mapOf(
                Diamonds.carat.name to Diamonds.carat.data,
                Diamonds.cut.name to Diamonds.cut.data,
                Diamonds.color.name to Diamonds.color.data,
                Diamonds.clarity.name to Diamonds.clarity.data,
                Diamonds.depth.name to Diamonds.depth.data,
                Diamonds.table.name to Diamonds.table.data,
                Diamonds.price.name to Diamonds.price.data,
                Diamonds.x.name to Diamonds.x.data,
                Diamonds.y.name to Diamonds.y.data,
                Diamonds.z.name to Diamonds.z.data
            )
        }


    lateinit var data: List<String>
}

