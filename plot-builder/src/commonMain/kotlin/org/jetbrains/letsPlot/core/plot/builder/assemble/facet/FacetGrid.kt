/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.assemble.facet

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.builder.assemble.PlotFacets
import kotlin.math.max

class FacetGrid constructor(
    private val xVar: String?,
    private val yVar: String?,
    xLevels: List<Any>,
    yLevels: List<Any>,
    xOrder: Int,
    yOrder: Int,
    private val xFormatter: (Any) -> String = DEF_FORMATTER,
    private val yFormatter: (Any) -> String = DEF_FORMATTER,
    scales: FacetScales = FacetScales.FIXED,
    private val xLabWrapper: ((String) -> String)? = null,
    private val yLabWrapper: ((String) -> String)? = null
) : PlotFacets() {

    override val isDefined: Boolean = xVar != null || yVar != null
    override val colCount: Int = max(1, xLevels.size)
    override val rowCount: Int = max(1, yLevels.size)
    override val numTiles = colCount * rowCount
    override val variables: List<String>
        get() = listOfNotNull(xVar, yVar)

    override val freeHScale: Boolean =
        (scales == FacetScales.FREE || scales == FacetScales.FREE_X) && xVar != null

    override val freeVScale: Boolean =
        (scales == FacetScales.FREE || scales == FacetScales.FREE_Y) && yVar != null

    private val xLevels: List<Any> = reorderVarLevels(xVar, xLevels, xOrder)
    private val yLevels: List<Any> = reorderVarLevels(yVar, yLevels, yOrder)

    private val colLevels: List<Any?> get() = xLevels.ifEmpty { listOf(null) }
    private val rowLevels: List<Any?> get() = yLevels.ifEmpty { listOf(null) }


    /**
     * @return List of Dataframes, one Dataframe per tile.
     *          Tiles are enumerated by rows, i.e.:
     *          the index is computed like: row * nCols + col
     */
    override fun dataByTile(data: DataFrame): List<DataFrame> {
        require(isDefined) { "dataByTile() called on Undefined plot facets." }

        val dataByLevelTupleList = dataByLevelTuple(
            data,
            listOfNotNull(
                xVar,
                yVar,
            ),
            listOfNotNull(
                xVar?.let { xLevels },
                yVar?.let { yLevels },
            )
        )
        val dataByLevelTuple = dataByLevelTupleList.toMap()

        val dataByTile: MutableList<DataFrame> = ArrayList()
        // Enumerate tiles by-row.
        for (rowLevel in rowLevels) {
            for (colLevel in colLevels) {
                val levelTuple = listOfNotNull(colLevel, rowLevel)
                val tileData = dataByLevelTuple.getValue(levelTuple)
                dataByTile.add(tileData)
            }
        }

        return dataByTile
    }

    /**
     * @return List of FacetTileInfo.
     *          Tiles are enumerated by rows, i.e.:
     *          the index is computed like: row * nCols + col
     */
    override fun tileInfos(): List<FacetTileInfo> {
        val colLabels = (colLevels).map {
            it?.let {
                xFormatter(it).let { lab -> xLabWrapper?.invoke(lab) ?: lab }
            }
        }
        val rowLabels = (rowLevels).map {
            it?.let {
                yFormatter(it).let { lab -> yLabWrapper?.invoke(lab) ?: lab }
            }
        }

        val infos = ArrayList<FacetTileInfo>()
        for (row in 0 until rowCount) {
            val addColLab = row == 0
            val hasHAxis = row == rowCount - 1
            for (col in 0 until colCount) {
                val addRowLab = col == colCount - 1
                val hasVAxis = col == 0

                val colLabs = if (addColLab) {
                    colLabels[col]?.let { listOf(it) } ?: emptyList()
                } else {
                    emptyList<String>()
                }

                infos.add(
                    FacetTileInfo(
                        col, row,
                        colLabs,
                        if (addRowLab) rowLabels[row] else null,
                        hasHAxis = hasHAxis,
                        hasVAxis = hasVAxis,
                        isBottom = row == rowCount - 1,
                        trueIndex = infos.size  // no reordering
                    )
                )
            }
        }

        return infos
    }

    private fun colIndices(col: Int): List<Int> {
        return (rowLevels.indices).map { it * colLevels.size + col }.toList()
    }

    private fun rowIndices(row: Int): List<Int> {
        val start = row * colLevels.size
        return (start until start + colLevels.size).toList()
    }

    override fun adjustHDomains(domains: List<DoubleSpan?>): List<DoubleSpan?> {
        return if (freeHScale) {
            // same domain for all tiles in a column.
            val adjusted = MutableList<DoubleSpan?>(domains.size) { null }
            for (col in colLevels.indices) {
                val indices = colIndices(col)
                val union = indices.map { domains[it] }.reduce { d0, d1 -> SeriesUtil.span(d0, d1) }
                indices.forEach {
                    adjusted[it] = union
                }
            }
            adjusted
        } else {
            domains
        }
    }

    override fun adjustVDomains(domains: List<DoubleSpan?>): List<DoubleSpan?> {
        return if (freeVScale) {
            // same domain for all tiles in a row.
            val adjusted = MutableList<DoubleSpan?>(domains.size) { null }
            for (row in rowLevels.indices) {
                val indices = rowIndices(row)
                val union = indices.map { domains[it] }.reduce { d0, d1 -> SeriesUtil.span(d0, d1) }
                indices.forEach {
                    adjusted[it] = union
                }
            }
            adjusted
        } else {
            domains
        }
    }

    override fun adjustFreeDisctereHDomainsByTile(
        domainBeforeFacets: List<Any>,
        domainByTile: List<Collection<Any>>
    ): List<List<Any>> {
        check(freeHScale) { "Not applicable: freeHScale = $freeHScale " }
        check(domainByTile.size == numTiles) { "domainByTile.size = ${domainByTile.size} but numTiles = $numTiles" }

        val adjustedDomainByTile = List<List<Any>>(numTiles) { emptyList() }.toMutableList()

        // adjust scale domains but each column still shares domain (same domain for each tile in col)
        for (colIndex in colLevels.indices) {
            val colIndices = colIndices(colIndex)
            val colDomainByTile = domainByTile.slice(colIndices)
            val colJointDomain = joinDiscreteDomains(domainBeforeFacets, colDomainByTile)
            for (i in colIndices) {
                adjustedDomainByTile[i] = colJointDomain
            }
        }

        return adjustedDomainByTile
    }

    override fun adjustFreeDisctereVDomainsByTile(
        domainBeforeFacets: List<Any>,
        domainByTile: List<Collection<Any>>
    ): List<List<Any>> {
        check(freeVScale) { "Not applicable: freeVScale = $freeVScale " }
        check(domainByTile.size == numTiles) { "domainByTile.size = ${domainByTile.size} but numTiles = $numTiles" }

        val adjustedDomainByTile = List<List<Any>>(numTiles) { emptyList() }.toMutableList()

        // adjust scale domains but each row still shares domain (same domain for each tile in row)
        for (rowIndex in rowLevels.indices) {
            val rowIndices = rowIndices(rowIndex)
            val rowDomainByTile = domainByTile.slice(rowIndices)
            val rowJointDomain = joinDiscreteDomains(domainBeforeFacets, rowDomainByTile)
            for (i in rowIndices) {
                adjustedDomainByTile[i] = rowJointDomain
            }
        }

        return adjustedDomainByTile
    }

    private fun joinDiscreteDomains(
        domainBeforeFacets: List<Any>,
        domainByTile: List<Collection<Any>>
    ): List<Any> {
        val jointDomain = domainByTile.reduceOrNull { acc, elem -> acc.union(elem) }
            ?: emptyList()
        return domainBeforeFacets.intersect(jointDomain).toList()
    }
}
