/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.assemble.facet

import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.builder.assemble.PlotFacets
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

class FacetWrap(
    private val facets: List<String>,
    private val levels: List<List<Any>>,
    private val nrow: Int?,
    private val ncol: Int?,
    private val direction: Direction
) : PlotFacets() {

    override val isDefined: Boolean = true
    override val numTiles = numTiles(facets, levels)
    private val shape = shape(numTiles, ncol, nrow, direction)
    override val colCount: Int = shape.first
    override val rowCount: Int = shape.second
    override val variables: List<String> = facets

    /**
     * @return List of Dataframes, one Dataframe per tile.
     *          Tiles are enumerated by rows, i.e.:
     *          the index is computed like: row * nCols + col
     */
    override fun dataByTile(data: DataFrame): List<DataFrame> {
        val dataByLevelTuple = dataByLevelTuple(
            data,
            variables,
            levels
        )

        val dataByTile: MutableList<DataFrame> = ArrayList()
        for ((_, tileData) in dataByLevelTuple) {
            dataByTile.add(tileData)
        }
        return dataByTile
    }

    /**
     * @return List of FacetTileInfo.
     *          Tiles are enumerated by rows, i.e.:
     *          the index is computed like: row * nCols + col
     */
    override fun tileInfos(): List<FacetTileInfo> {

        val levelTuples = createNameLevelTuples(facets, levels)
        val tileLabels = levelTuples
            .map { it.map { it.second } }                    // get rid of 'pair'
            .map { it.map { it.toString() } }                // to string tuples

        fun toCol(index: Int): Int {
            return when (direction) {
                Direction.H -> index % colCount
                Direction.V -> index / rowCount
            }
        }

        fun toRow(index: Int): Int {
            return when (direction) {
                Direction.H -> index / colCount
                Direction.V -> index % rowCount
            }
        }

        fun toIndex(col: Int, row: Int): Int {
            return when (direction) {
                Direction.H -> row * colCount + col
                Direction.V -> col * rowCount + row
            }
        }

        val infos = ArrayList<FacetTileInfo>()
        for ((i, tileLabelTuple) in tileLabels.withIndex()) {
            val col = toCol(i)
            val row = toRow(i)
            val nextRowIndex = toIndex(col, row + 1)
            val hasXAxis = nextRowIndex >= numTiles
            val hasYAxis = col == 0

            infos.add(
                FacetTileInfo(
                    col, row,
                    colLabs = tileLabelTuple,
                    null,
                    hasXAxis, hasYAxis
                )
            )
        }

        return infos
    }

    enum class Direction {
        H, V
    }

    companion object {
        private fun numTiles(
            facets: List<String>,
            levels: List<List<Any>>,
        ): Int {
            require(facets.isNotEmpty()) { "List of facets is empty." }
            require(facets.distinct().size == facets.size) { "Duplicated values in the facets list: $facets" }
            check(facets.size == levels.size)
            return createNameLevelTuples(facets, levels).size
        }

        private fun shape(tilesCount: Int, ncol: Int?, nrow: Int?, dir: Direction): Pair<Int, Int> {
            require(ncol?.let { ncol > 0 } ?: true) { "'ncol' must be positive, was $ncol" }
            require(nrow?.let { nrow > 0 } ?: true) { "'nrow' must be positive, was $nrow" }
            val shape = when {
                ncol != null -> {
                    val ncolActual = min(ncol, tilesCount)
                    val nrowActual = ceil(tilesCount.toDouble() / ncolActual).toInt()
                    ncolActual to max(1, nrowActual)
                }
                nrow != null -> {
                    val nrowActual = min(nrow, tilesCount)
                    val ncolActual = ceil(tilesCount.toDouble() / nrowActual).toInt()
                    ncolActual to max(1, nrowActual)
                }
                else -> {
                    val w = min(4, max(1, tilesCount / 2))
                    val h = max(1, ceil(tilesCount.toDouble() / w).toInt())
                    w to h
                }
            }

            val (w, h) = shape
            val shapeAdjusted = when (dir) {
                Direction.H -> {
                    // filling by rows
                    Pair(
                        w,
                        ceil(tilesCount.toDouble() / w).toInt()
                    )
                }
                Direction.V -> {
                    // filling by cols
                    Pair(
                        ceil(tilesCount.toDouble() / h).toInt(),
                        h
                    )
                }
            }
            return shapeAdjusted
        }
    }
}
