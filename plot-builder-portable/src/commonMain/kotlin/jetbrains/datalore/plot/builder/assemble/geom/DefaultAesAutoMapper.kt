/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.assemble.geom

import jetbrains.datalore.base.gcommon.collect.Iterables
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.GeomKind
import jetbrains.datalore.plot.base.data.DataFrameUtil
import jetbrains.datalore.plot.builder.assemble.AesAutoMapper
import jetbrains.datalore.plot.builder.map.GeoPositionField.POINT_X
import jetbrains.datalore.plot.builder.map.GeoPositionField.POINT_Y
import jetbrains.datalore.plot.builder.map.GeoPositionField.RECT_XMAX
import jetbrains.datalore.plot.builder.map.GeoPositionField.RECT_XMIN
import jetbrains.datalore.plot.builder.map.GeoPositionField.RECT_YMAX
import jetbrains.datalore.plot.builder.map.GeoPositionField.RECT_YMIN

class DefaultAesAutoMapper constructor(
    private val autoMappedAes: List<Aes<*>>,
    private val preferDiscreteValues: (Aes<*>) -> Boolean
) : AesAutoMapper {

    override fun createMapping(data: DataFrame): Map<Aes<*>, DataFrame.Variable> {
        val autoMapping = HashMap<Aes<*>, DataFrame.Variable>()
        val doneVars = HashSet<DataFrame.Variable>()

        var variables: Iterable<DataFrame.Variable> = data.variables()
        variables = DataFrameUtil.sortedCopy(variables)

        for (aes in autoMappedAes) {
            val discrete = preferDiscreteValues(aes)

            val predicate = { variable: DataFrame.Variable? ->
                if (doneVars.contains(variable)) {
                    false
                } else {
                    discrete && !data.isNumeric(variable!!) || !discrete && data.isNumeric(variable!!)
                }
            }

            var autoMapVar: DataFrame.Variable? = null

            if (AES_DEFAULT_LABELS.containsKey(aes)) {
                val defaultLabels = AES_DEFAULT_LABELS.get(aes)!!
                autoMapVar = Iterables.find(variables, { variable -> defaultLabels.contains(variable!!.name) }, null)
            }

            if (autoMapVar == null || !predicate(autoMapVar)) {
                autoMapVar = Iterables.find(variables, predicate, null)
            }

            if (autoMapVar != null) {
                autoMapping[aes] = autoMapVar
                doneVars.add(autoMapVar)
            }
        }
        return autoMapping
    }

    companion object {
        private val AES_DEFAULT_LABELS = mapOf(
            Aes.X to listOf(POINT_X),
            Aes.Y to listOf(POINT_Y),
            Aes.XMIN to listOf(RECT_XMIN),
            Aes.YMIN to listOf(RECT_YMIN),
            Aes.XMAX to listOf(RECT_XMAX),
            Aes.YMAX to listOf(RECT_YMAX)
        )

        fun forGeom(geomKind: GeomKind): DefaultAesAutoMapper {
            return when (geomKind) {
                GeomKind.POINT,
                GeomKind.PATH,
                GeomKind.LINE,
                GeomKind.SMOOTH,
                GeomKind.TILE,
                GeomKind.BIN_2D -> DefaultAesAutoMapper(
                    listOf(
                        Aes.X,
                        Aes.Y
                    )
                ) { false }

                GeomKind.BAR -> DefaultAesAutoMapper(listOf(Aes.X)) { true }
                GeomKind.HISTOGRAM -> DefaultAesAutoMapper(listOf(Aes.X)) { false }

                GeomKind.RECT -> DefaultAesAutoMapper(
                    listOf(
                        Aes.XMIN,
                        Aes.YMIN,
                        Aes.XMAX,
                        Aes.YMAX
                    )
                ) { false }

                GeomKind.ERROR_BAR,
                GeomKind.CROSS_BAR,
                GeomKind.LINE_RANGE,
                GeomKind.POINT_RANGE,
                GeomKind.CONTOUR,
                GeomKind.CONTOURF,
                GeomKind.POLYGON,
                GeomKind.MAP,
                GeomKind.AB_LINE,
                GeomKind.H_LINE,
                GeomKind.V_LINE,
                GeomKind.BOX_PLOT,
                GeomKind.RIBBON,
                GeomKind.AREA,
                GeomKind.DENSITY,
                GeomKind.DENSITY2D,
                GeomKind.DENSITY2DF,
                GeomKind.JITTER,
                GeomKind.FREQPOLY,
                GeomKind.STEP,
                GeomKind.SEGMENT,
                GeomKind.TEXT,
                GeomKind.LIVE_MAP,
                GeomKind.RASTER,
                GeomKind.IMAGE -> DefaultAesAutoMapper(emptyList()) { false }
            }
        }
    }
}
