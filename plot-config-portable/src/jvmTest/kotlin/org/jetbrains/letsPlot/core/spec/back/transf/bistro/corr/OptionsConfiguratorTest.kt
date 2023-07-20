/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.server.config.transform.bistro.corr

import org.jetbrains.letsPlot.core.spec.back.transf.bistro.corr.CorrPlotOptionsBuilder.LayerParams
import org.jetbrains.letsPlot.core.spec.back.transf.bistro.corr.OptionsConfigurator
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
internal class OptionsConfiguratorTest(
    private val tiles: LayerParams,
    private val points: LayerParams,
    private val labels: LayerParams,
    private val flipY: Boolean,
    private val expected: List<LayerParams>,
) {

    @Test
    fun configure() {
        OptionsConfigurator.configure(tiles, points, labels, flipY)

        Assert.assertEquals("<<tiles>>", expected[0], tiles)
        Assert.assertEquals("<<points>>", expected[1], points)
        Assert.assertEquals("<<labels>>", expected[2], labels)
    }

    companion object {

        @JvmStatic
        @Parameterized.Parameters
        fun params(): Collection<Array<Any?>> {
            return listOf<Array<Any?>>(
                arrayOf(
                    LayerParams(),
                    LayerParams(),
                    LayerParams(),
                    false,
                    listOf(
                        LayerParams(),
                        LayerParams(),
                        LayerParams(),
                    )
                ),
                arrayOf(
                    LayerParams().apply { type = null },
                    LayerParams(),
                    LayerParams(),
                    false,
                    listOf(
                        LayerParams().apply { type = "full"; diag = true },
                        LayerParams(),
                        LayerParams(),
                    )
                ),
                arrayOf(
                    LayerParams(),
                    LayerParams(),
                    LayerParams().apply { type = null },
                    false,
                    listOf(
                        LayerParams(),
                        LayerParams(),
                        LayerParams().apply { type = "full"; diag = true },
                    )
                ),
                arrayOf(
                    LayerParams().apply { type = "full" },
                    LayerParams(),
                    LayerParams().apply { type = null },
                    false,
                    listOf(
                        LayerParams().apply { type = "full"; diag = true },
                        LayerParams(),
                        LayerParams().apply { type = "full"; diag = true; color = "white" },
                    )
                ),
                arrayOf(
                    LayerParams().apply { type = null },
                    LayerParams().apply { type = null },
                    LayerParams(),
                    false,
                    listOf(
                        LayerParams().apply { type = "lower"; diag = false },
                        LayerParams().apply { type = "upper"; diag = false },
                        LayerParams(),
                    )
                ),
                arrayOf(
                    LayerParams().apply { type = null },
                    LayerParams().apply { type = null },
                    LayerParams(),
                    true,
                    listOf(
                        LayerParams().apply { type = "upper"; diag = false },
                        LayerParams().apply { type = "lower"; diag = false },
                        LayerParams(),
                    )
                ),
                arrayOf(
                    LayerParams().apply { type = null },
                    LayerParams().apply { type = null },
                    LayerParams().apply { type = null },
                    false,
                    listOf(
                        LayerParams().apply { type = "lower"; diag = false },
                        LayerParams().apply { type = "upper"; diag = false },
                        LayerParams().apply { type = "upper"; diag = false; color = "white"; mapSize = true },
                    )
                ),
            )
        }
    }
}
