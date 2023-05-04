/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.server.config

import jetbrains.datalore.plot.config.Option.Meta.SeriesAnnotation.COLUMN
import jetbrains.datalore.plot.config.Option.Meta.SeriesAnnotation.DateTime.DATE_TIME
import jetbrains.datalore.plot.config.Option.Meta.SeriesAnnotation.TYPE
import jetbrains.datalore.plot.parsePlotSpec
import kotlin.test.Test

class DataMetaStoreFactorLevalsTest {

    @Test
    fun `simple facets`() {
        val plotSpecs = plotSpecs_No_Ordering_No_DateTime()
        val layerConfigs = ServerSideTestUtil.createLayerConfigs(plotSpecs)
        SingleLayerAssert.assertThat(layerConfigs)
            .hasDataMetaFacetLevels("chrom", listOf("chr1", "chr2", "chr4", "chr5"))
    }

    @Test
    fun `facets with as_discrete xy`() {
        val plotSpecs = plotSpecs_With_asDiscrete(listOf("x", "y"), order = true)
        val layerConfigs = ServerSideTestUtil.createLayerConfigs(plotSpecs)
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
        val layerConfigs = ServerSideTestUtil.createLayerConfigs(plotSpecs)
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
        val layerConfigs = ServerSideTestUtil.createLayerConfigs(plotSpecs)
        SingleLayerAssert.assertThat(layerConfigs)
            .hasDataMetaAesAsDiscrete("x")
            .hasDataMetaAesOrderOption("x")
            .noDataMetaFacetLevels("chrom") // mapped to aes x
    }

    @Test
    fun `facets with as_discrete y`() {
        val plotSpecs = plotSpecs_With_asDiscrete(listOf("y"), order = true)
        val layerConfigs = ServerSideTestUtil.createLayerConfigs(plotSpecs)
        SingleLayerAssert.assertThat(layerConfigs)
            .hasDataMetaAesAsDiscrete("y")
            .hasDataMetaAesOrderOption("y")
            .hasDataMetaFacetLevels("chrom", listOf("chr1", "chr2", "chr4", "chr5"))
    }

    @Test
    fun `facets with date_time xy`() {
        val plotSpecs = plotSpecs_With_DateTime(listOf("chrom", "arm", "y"))
        val layerConfigs = ServerSideTestUtil.createLayerConfigs(plotSpecs)
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
        val layerConfigs = ServerSideTestUtil.createLayerConfigs(plotSpecs)
        SingleLayerAssert.assertThat(layerConfigs)
            .hasDataMetaDateTime("chrom")
            .hasDataMetaDateTime("arm")
            .hasDataMetaDateTime("y")
            .hasDataMetaAesAsDiscrete("x")
            .hasDataMetaAesAsDiscrete("y")
            .hasDataMetaFacetLevels("chrom", listOf("chr1", "chr2", "chr4", "chr5"))
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
            val plotSpec = plotSpec(null, null)
            plotSpec["data"] = DATA_20
            return plotSpec
        }

        private fun plotSpecs_With_asDiscrete(aesList: List<String>, order: Boolean): MutableMap<String, Any> {
            val asDiscreteAnnotations = asDiscreteAnnotationsSpec(aesList, order)
            val plotSpec = plotSpec(asDiscreteAnnotations, null)
            plotSpec["data"] = DATA_20
            return plotSpec
        }

        private fun plotSpecs_With_DateTime(varList: List<String>): MutableMap<String, Any> {
            val dateTimeAnnotations = dateTimeAnnotationsSpec(varList)
            val plotSpec = plotSpec(null, dateTimeAnnotations)
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
            val plotSpec = plotSpec(asDiscreteAnnotations, dateTimeAnnotations)
            plotSpec["data"] = DATA_20
            return plotSpec
        }

        private fun plotSpec(
            asDiscreteAnnotationsSpec: String?,
            dateTimeAnnotationsSpec: String?
        ): MutableMap<String, Any> {
            val annotationSpecs = ArrayList<String>().apply {
                if (asDiscreteAnnotationsSpec != null) {
                    add("'mapping_annotations': [$asDiscreteAnnotationsSpec]")
                }
                if (dateTimeAnnotationsSpec != null) {
                    add("'series_annotations': [$dateTimeAnnotationsSpec]")
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
