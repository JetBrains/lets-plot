/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.server.config.transform
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.config.Option
import jetbrains.datalore.plot.config.Option.Mapping.toOption
import jetbrains.datalore.plot.config.Option.Meta
import jetbrains.datalore.plot.config.Option.Meta.SeriesAnnotation
import jetbrains.datalore.plot.config.Option.Meta.SeriesAnnotation.DISCRETE
import jetbrains.datalore.plot.config.Option.Plot
import jetbrains.datalore.plot.config.Option.PlotBase.MAPPING
import jetbrains.datalore.plot.config.read
import jetbrains.datalore.plot.config.sections
import jetbrains.datalore.plot.config.transform.SpecChangeContext
import jetbrains.datalore.plot.config.write
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DiscreteScaleFromAnnotationChangeTest {

    @Test
    fun withoutMetaShouldNotAddScale() {
        val varName = "drv"
        val plotSpec = dict {
            layers(
                dict {
                    write(MAPPING, toOption(Aes.COLOR)) { varName }
                }
            )
        }

        DiscreteScaleFromAnnotationChange().apply(plotSpec, dummyCtx)
        plotSpec.sections(Plot.SCALES).run { assertNull(this) }
    }

    @Test
    fun discreteWithoutScale() {
        val varName = "drv"
        val plotSpec = dict {
            layers(
                dict {
                    write(MAPPING, toOption(Aes.COLOR)) { varName }
                    write(Meta.DATA_META, SeriesAnnotation.TAG) {
                        list(
                            dict {
                                write(SeriesAnnotation.VARIABLE) { varName }
                                write(SeriesAnnotation.ANNOTATION) { DISCRETE }
                            }
                        )
                    }
                }
            )
        }
        DiscreteScaleFromAnnotationChange().apply(plotSpec, dummyCtx)

        plotSpec.sections(Plot.SCALES)!![0].run {
            read(Option.Scale.AES).run { assertEquals(toOption(Aes.COLOR), this) }
            read(Option.Scale.DISCRETE_DOMAIN).run { assertEquals(true, this) }
            read(Option.Scale.SCALE_MAPPER_KIND).run { assertNull(this) }
        }
    }

    @Test
    fun discreteWithExistingDiffrentAesScale_ShouldMergeScales() {
        val varName = "drv"
        val plotSpec = dict {
            scales(
                dict {
                    write(Option.Scale.AES) { toOption(Aes.ALPHA) }
                    write(Option.Scale.DISCRETE_DOMAIN) { true }
                }
            )
            layers(
                dict {
                    write(MAPPING, toOption(Aes.COLOR)) { varName }
                    write(Meta.DATA_META, SeriesAnnotation.TAG) {
                        list(
                            dict {
                                write(SeriesAnnotation.VARIABLE) { varName }
                                write(SeriesAnnotation.ANNOTATION) { DISCRETE }
                            }
                        )
                    }
                }
            )
        }
        DiscreteScaleFromAnnotationChange().apply(plotSpec, dummyCtx)

        with(plotSpec.sections(Plot.SCALES)!![0]) {
            read(Option.Scale.AES).run { assertEquals(toOption(Aes.ALPHA), this) }
            read(Option.Scale.DISCRETE_DOMAIN).run { assertEquals(true, this) }
            read(Option.Scale.SCALE_MAPPER_KIND).run { assertNull(this) }
        }

        with(plotSpec.sections(Plot.SCALES)!![1]) {
            read(Option.Scale.AES).run { assertEquals(toOption(Aes.COLOR), this) }
            read(Option.Scale.DISCRETE_DOMAIN).run { assertEquals(true, this) }
            read(Option.Scale.SCALE_MAPPER_KIND).run { assertNull(this) }
        }
    }
}


private fun dict(block: MutableMap<String, Any>.() -> Unit): MutableMap<String, Any> {
    return mutableMapOf<String, Any>().apply(block)
}

private fun MutableMap<String, Any>.layers(vararg layerBuilder: MutableMap<String, Any>) {
    this[Plot.LAYERS] = layerBuilder.toMutableList()
}

private fun MutableMap<String, Any>.scales(vararg scaleBuilder: MutableMap<String, Any>) {
    this[Plot.SCALES] = scaleBuilder.toMutableList()
}

private fun list(vararg items: Any): MutableList<Any> {
    return mutableListOf(*items)
}

private val dummyCtx = object : SpecChangeContext {
    override fun getSpecsAbsolute(vararg keys: String): List<Map<String, Any>> = error("Not expected to be invoked")
}

