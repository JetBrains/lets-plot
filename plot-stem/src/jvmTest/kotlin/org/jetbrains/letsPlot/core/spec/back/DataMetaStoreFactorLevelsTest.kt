/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.back

import org.jetbrains.letsPlot.core.spec.Option.Meta.SeriesAnnotation.COLUMN
import org.jetbrains.letsPlot.core.spec.Option.Meta.SeriesAnnotation.DateTime.DATE_TIME
import org.jetbrains.letsPlot.core.spec.Option.Meta.SeriesAnnotation.TYPE
import demoAndTestShared.parsePlotSpec
import kotlin.test.Test

class DataMetaStoreFactorLevelsTest {

    @Test
    fun `simple facets`() {
        val plotSpecs = plotSpecs_No_Ordering_No_DateTime()
        val layerConfigs = BackendTestUtil.createLayerConfigs(plotSpecs)
        SingleLayerAssert.assertThat(layerConfigs)
            .hasDataMetaFacetLevels("chrom", listOf("chr1", "chr2", "chr4", "chr5"))
    }

    @Test
    fun `facets with as_discrete xy`() {
        val plotSpecs = plotSpecs_With_asDiscrete(listOf("x", "y"), order = true)
        val layerConfigs = BackendTestUtil.createLayerConfigs(plotSpecs)
        SingleLayerAssert.assertThat(layerConfigs)
            .hasDataMetaAesAsDiscrete("x")
            .hasDataMetaAesOrderOption("x")
            .hasDataMetaAesAsDiscrete("y")
            .hasDataMetaAesOrderOption("y")
            .noDataMetaFacetLevels("chrom") // mapped to aes x
    }

    @Test
    fun `facets with as_discrete no order xy`() {
        val plotSpecs = plotSpecs_With_asDiscrete(listOf("x", "y"), order = false)
        val layerConfigs = BackendTestUtil.createLayerConfigs(plotSpecs)
        SingleLayerAssert.assertThat(layerConfigs)
            .hasDataMetaAesAsDiscrete("x")
            .hasDataMetaAesAsDiscrete("y")
            .hasDataMetaFacetLevels(
                "chrom",     // mapped to aes x but no order for x
                listOf("chr1", "chr2", "chr4", "chr5")
            )
    }

    @Test
    fun `facets with as_discrete x`() {
        val plotSpecs = plotSpecs_With_asDiscrete(listOf("x"), order = true)
        val layerConfigs = BackendTestUtil.createLayerConfigs(plotSpecs)
        SingleLayerAssert.assertThat(layerConfigs)
            .hasDataMetaAesAsDiscrete("x")
            .hasDataMetaAesOrderOption("x")
            .noDataMetaFacetLevels("chrom") // mapped to aes x
    }

    @Test
    fun `facets with as_discrete y`() {
        val plotSpecs = plotSpecs_With_asDiscrete(listOf("y"), order = true)
        val layerConfigs = BackendTestUtil.createLayerConfigs(plotSpecs)
        SingleLayerAssert.assertThat(layerConfigs)
            .hasDataMetaAesAsDiscrete("y")
            .hasDataMetaAesOrderOption("y")
            .hasDataMetaFacetLevels("chrom", listOf("chr1", "chr2", "chr4", "chr5"))
    }

    @Test
    fun `facets with date_time xy`() {
        val plotSpecs = plotSpecs_With_DateTime(listOf("chrom", "arm", "y"))
        val layerConfigs = BackendTestUtil.createLayerConfigs(plotSpecs)
        SingleLayerAssert.assertThat(layerConfigs)
            .hasDataMetaDateTime("chrom")
            .hasDataMetaDateTime("arm")
            .hasDataMetaDateTime("y")
            .hasDataMetaFacetLevels("chrom", listOf("chr1", "chr2", "chr4", "chr5"))
    }

    @Test
    fun `facets with date_time and as_discrete xy`() {
        val plotSpecs = plotSpecs_With_DateTime_And_AsDisctere(
            varList = listOf("chrom", "arm", "y"),  // date-time
            aesList = listOf("x", "y"),             // as discrete
            order = false
        )
        val layerConfigs = BackendTestUtil.createLayerConfigs(plotSpecs)
        SingleLayerAssert.assertThat(layerConfigs)
            .hasDataMetaDateTime("chrom")
            .hasDataMetaDateTime("arm")
            .hasDataMetaDateTime("y")
            .hasDataMetaAesAsDiscrete("x")
            .hasDataMetaAesAsDiscrete("y")
            .hasDataMetaFacetLevels("chrom", listOf("chr1", "chr2", "chr4", "chr5"))
    }

    @Test
    fun `specified 'factor_levels' for variable`() {
        val plotSpecs = plotSpecs_With_FactorLevels(
            varListWithLevels = mapOf(
                "chrom" to listOf("chr5", "chr4", "chr2", "chr1"),
                "arm" to listOf("q", "p")
            )
        )
        val layerConfigs = BackendTestUtil.createLayerConfigs(plotSpecs)
        SingleLayerAssert.assertThat(layerConfigs)
            .hasDataMetaFacetLevels("chrom", listOf("chr5", "chr4", "chr2", "chr1"))
            .hasDataMetaFacetLevels("arm", listOf("q", "p"))
    }

    @Test
    fun `specified 'factor_levels' will be consistent with the actual dataset contents`() {
        val plotSpecs = plotSpecs_With_FactorLevels(
            varListWithLevels = mapOf(
                "chrom" to listOf("chr4", "chr5"), // should append "chr1", "chr2"
                "arm" to listOf("q")               // should append "p"
            )
        )
        val layerConfigs = BackendTestUtil.createLayerConfigs(plotSpecs)
        SingleLayerAssert.assertThat(layerConfigs)
            .hasDataMetaFacetLevels("chrom", listOf("chr4", "chr5", "chr1", "chr2"))
            .hasDataMetaFacetLevels("arm", listOf("q", "p"))
    }


    private companion object {
        // See LP issue #746
        // Data (n=20)
        private val DATA_20 = mapOf<String, List<Any>>(
            "chrom" to listOf(
                "chr1",
                "chr1",
                "chr1",
                "chr2",
                "chr4",
                "chr4",
                "chr4",
                "chr4",
                "chr4",
                "chr4",
                "chr4",
                "chr4",
                "chr4",
                "chr5",
                "chr5",
                "chr5",
                "chr5",
                "chr5",
                "chr5",
            ),
            "y" to listOf(
                -0.90,
                0.06,
                -1.42,
                0.11,
                -0.29,
                -0.60,
                0.37,
                -1.15,
                -1.01,
                1.46,
                -1.41,
                -0.54,
                -1.22,
                0.82,
                -0.22,
                0.31,
                -0.60,
                -0.01,
                -1.05,
            ),
            "arm" to listOf(
                "q",
                "q",
                "q",
                "q",
                "q",
                "q",
                "p",
                "p",
                "q",
                "q",
                "q",
                "q",
                "p",
                "p",
                "q",
                "p",
                "q",
                "q",
                "p",
            ),
        )

        private fun plotSpecs_No_Ordering_No_DateTime(): MutableMap<String, Any> {
            val plotSpec = plotSpec(
                asDiscreteAnnotationsSpec = null,
                dateTimeAnnotationsSpec = null,
                factorLevelsAnnotationsSpec = null
            )
            plotSpec["data"] = DATA_20
            return plotSpec
        }

        fun plotSpecs_With_asDiscrete(aesList: List<String>, order: Boolean): MutableMap<String, Any> {
            val asDiscreteAnnotations = asDiscreteAnnotationsSpec(aesList, order)
            val plotSpec = plotSpec(
                asDiscreteAnnotations,
                dateTimeAnnotationsSpec = null,
                factorLevelsAnnotationsSpec = null
            )
            plotSpec["data"] = DATA_20
            return plotSpec
        }

        private fun plotSpecs_With_DateTime(varList: List<String>): MutableMap<String, Any> {
            val dateTimeAnnotations = dateTimeAnnotationsSpec(varList)
            val plotSpec = plotSpec(
                asDiscreteAnnotationsSpec = null,
                dateTimeAnnotationsSpec = dateTimeAnnotations,
                factorLevelsAnnotationsSpec = null
            )
            plotSpec["data"] = DATA_20
            return plotSpec
        }

        private fun plotSpecs_With_DateTime_And_AsDisctere(
            varList: List<String>,    // date-time variables
            aesList: List<String>,    // 'as discrete' aes
            order: Boolean
        ): MutableMap<String, Any> {
            val dateTimeAnnotations = dateTimeAnnotationsSpec(varList)
            val asDiscreteAnnotations = asDiscreteAnnotationsSpec(aesList, order)
            val plotSpec = plotSpec(
                asDiscreteAnnotations,
                dateTimeAnnotations,
                factorLevelsAnnotationsSpec = null
            )
            plotSpec["data"] = DATA_20
            return plotSpec
        }

        fun plotSpecs_With_FactorLevels(
            varListWithLevels: Map<String, List<Any>>
        ): MutableMap<String, Any> {
            val factorLevelsAnnotationsSpec = factorLevelsAnnotationsSpec(varListWithLevels)
            val plotSpec = plotSpec(
                asDiscreteAnnotationsSpec = null,
                dateTimeAnnotationsSpec = null,
                factorLevelsAnnotationsSpec = factorLevelsAnnotationsSpec
            )
            plotSpec["data"] = DATA_20
            return plotSpec
        }

        private fun plotSpec(
            asDiscreteAnnotationsSpec: String?,
            dateTimeAnnotationsSpec: String?,
            factorLevelsAnnotationsSpec: String?
        ): MutableMap<String, Any> {
            val annotationSpecs = ArrayList<String>().apply {
                if (asDiscreteAnnotationsSpec != null) {
                    add("'mapping_annotations': [$asDiscreteAnnotationsSpec]")
                }
                val seriesAnnotations = listOfNotNull(dateTimeAnnotationsSpec, factorLevelsAnnotationsSpec).joinToString()
                if (seriesAnnotations.isNotEmpty()) {
                    add("'series_annotations': [$seriesAnnotations]")
                }
            }

            val dataMetaSpecs = if (annotationSpecs.isEmpty()) {
                ""
            } else {
                val s = annotationSpecs.joinToString()
                """
                , 'data_meta': { $s }
                """.trimIndent()
            }

            val spec = """
            {
                'kind': 'plot',
                'layers':   [
                                {
                                    'geom': 'boxplot',
                                    'mapping': {'x': 'chrom', 'y': 'y'}
                                    $dataMetaSpecs
                                }
                            ],
                'facet':{ 'name': 'grid', 'y': 'arm', 'y_order': 1}                            
            }
            """.trimIndent()
            return HashMap(parsePlotSpec(spec))
        }

        private fun asDiscreteAnnotationsSpec(aesList: List<String>, order: Boolean): String {
            val orderOption = if (order) ", 'order': 1" else ""
            return aesList.joinToString { aes ->
                """
                {
                    'aes': '$aes',
                    'annotation': 'as_discrete',
                    'parameters': {'label': 'chrom' $orderOption}
                }                    
                """.trimIndent()
            }
        }

        private fun factorLevelsAnnotationsSpec(varListWithLevels: Map<String, List<Any>>): String {
            return varListWithLevels.toList().joinToString { (variable, factorLevels) ->
                """
                {
                    'column': '$variable',
                    'factor_levels': [ ${factorLevels.joinToString { "\'$it\'" }} ]
                }                    
                """.trimIndent()
            }
        }

        private fun dateTimeAnnotationsSpec(varList: List<String>): String {
            return varList.joinToString { variable ->
                """
                {
                    '$COLUMN': '$variable',
                    '$TYPE': '$DATE_TIME'
                }                    
                """.trimIndent()
            }
        }
    }
}
