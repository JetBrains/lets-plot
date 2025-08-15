/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.assemble.facet

import org.jetbrains.letsPlot.commons.formatting.string.wrap
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.builder.assemble.PlotFacets
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

class FacetWrap constructor(
    facets: List<String>,
    private val varNameAndLevelPairsByTile: List<List<Pair<String, Any>>>,
    nrow: Int?,
    ncol: Int?,
    private val direction: Direction,
    private val facetFormatters: List<(Any) -> String>,
    scales: FacetScales = FacetScales.FIXED,
    private val labWidths: List<Int>
) : PlotFacets() {

    override val isDefined: Boolean = true

    override val numTiles = varNameAndLevelPairsByTile.size
    private val shape = shape(numTiles, ncol, nrow, direction)
    override val colCount: Int = shape.first
    override val rowCount: Int = shape.second
    override val variables: List<String> = facets

    override val freeHScale: Boolean =
        scales == FacetScales.FREE || scales == FacetScales.FREE_X

    override val freeVScale: Boolean =
        scales == FacetScales.FREE || scales == FacetScales.FREE_Y

    /**
     * @return List of Dataframes, one Dataframe per tile.
     *          Tiles enumerated by rows, i.e.:
     *          the index is computed like: row * nCols + col
     */
    override fun dataByTile(data: DataFrame): List<DataFrame> {
        val levelTupleAndDataPairs = levelTupleAndDataPairs(
            data,
            varNameAndLevelPairsByTile
        )

        val dataByTile: MutableList<DataFrame> = ArrayList()
        for ((_, tileData) in levelTupleAndDataPairs) {
            dataByTile.add(tileData)
        }
        return dataByTile
    }

    /**
     * @return List of FacetTileInfo.
     *          Tiles enumerated by rows, i.e.:
     *          the index is computed like: row * nCols + col
     */
    override fun tileInfos(): List<FacetTileInfo> {

        val tileLabels = varNameAndLevelPairsByTile
            .map { it.map { pair -> pair.second } }  // Extract level values only
            .map {
                it.mapIndexed { i, level ->
                    wrap(facetFormatters[i](level), labWidths[i])
                }
            }

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

        fun isBottom(col: Int, row: Int): Boolean {
            val nextRowIndex = toIndex(col, row + 1)
            return (row + 1) % rowCount == 0 || nextRowIndex >= numTiles
        }

        val infos = ArrayList<FacetTileInfo>()
        for ((i, tileLabelTuple) in tileLabels.withIndex()) {
            val col = toCol(i)
            val row = toRow(i)
            val hasHAxis = isBottom(col, row) || freeHScale
            val hasVAxis = col == 0 || freeVScale

            infos.add(
                FacetTileInfo(
                    col, row,
                    colLabs = tileLabelTuple,
                    null,
                    hasHAxis = hasHAxis,
                    hasVAxis = hasVAxis,
                    isBottom = isBottom(col, row),
                    trueIndex = i
                )
            )
        }

        // Enumeration is always 'by row'.
        return infos.sortedWith(compareBy<FacetTileInfo> { it.row }.thenBy { it.col })
    }

    override fun adjustFreeDisctereHDomainsByTile(
        domainBeforeFacets: List<Any>,
        domainByTile: List<Collection<Any>>
    ): List<List<Any>> {
        check(freeHScale) { "Not applicable: freeHScale = $freeHScale " }
        check(domainByTile.size == numTiles) { "domainByTile.size = ${domainByTile.size} but numTiles = $numTiles" }

        // Keep all given domains, just ensure the original order.
        return domainByTile.map { domainBeforeFacets.intersect(it).toList() }
    }

    override fun adjustFreeDisctereVDomainsByTile(
        domainBeforeFacets: List<Any>,
        domainByTile: List<Collection<Any>>
    ): List<List<Any>> {
        check(freeVScale) { "Not applicable: freeVScale = $freeVScale " }
        check(domainByTile.size == numTiles) { "domainByTile.size = ${domainByTile.size} but numTiles = $numTiles" }

        // Keep all given domains, just ensure the original order.
        return domainByTile.map { domainBeforeFacets.intersect(it).toList() }
    }


    enum class Direction {
        H, V
    }

    companion object {

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
            return when (dir) {
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
        }
    }
}
