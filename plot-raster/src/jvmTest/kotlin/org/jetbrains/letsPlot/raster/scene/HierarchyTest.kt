/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.raster.scene

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.letsPlot.commons.geometry.AffineTransform
import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle.Companion.LTRB
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgGraphicsElement.Visibility.HIDDEN
import org.junit.Test

class HierarchyTest {
    @Test
    fun treeChangesShouldUpdateScreenBounds() {
        val doc = mapSvg {
            svgDocument(width = 400, height = 300) {
                g(id = "root_g") {
                    g(translate(x = 5, y = 20), HIDDEN, id = "hidden_g") {
                        g(id = "hidden_sub_g") {
                            rect(x = 0, y = 0, width = 10, height = 5, id = "hidden_rect")
                        }
                    }

                    g(translate(x = 10, y = 50), id = "vis_g") {
                        rect(x = 0, y = 0, width = 10, height = 10, id = "vis_rect")
                    }
                }
            }
        }

        val visBounds = LTRB(9.5f, 49.5f, 20.5f, 60.5f)

        with(doc) {
            val hiddenBounds = LTRB(4.5f, 19.5f, 15.5f, 25.5f)
            assertThat(findElement("hidden_g").bBoxGlobal).isEqualTo(hiddenBounds)
            assertThat(findElement("hidden_sub_g").bBoxGlobal).isEqualTo(hiddenBounds)
            assertThat(findElement("hidden_rect").bBoxGlobal).isEqualTo(hiddenBounds)

            // visible elements
            assertThat(findElement("vis_g").bBoxGlobal).isEqualTo(visBounds)
            assertThat(findElement("vis_rect").bBoxGlobal).isEqualTo(visBounds)

            // outer group
            assertThat(findElement("root_g").bBoxGlobal).isEqualTo(LTRB(4.5f, 19.5f, 20.5f, 60.5f))
        }

        doc.findElement("hidden_g").transform = AffineTransform.makeTranslation(60f, 20f)

        with(doc) {
            val hiddenBounds = LTRB(59.5f, 19.5f, 70.5f, 25.5f)
            assertThat(findElement("hidden_g").bBoxGlobal).isEqualTo(hiddenBounds)
            assertThat(findElement("hidden_sub_g").bBoxGlobal).isEqualTo(hiddenBounds)
            assertThat(findElement("hidden_rect").bBoxGlobal).isEqualTo(hiddenBounds)

            // visible elements
            assertThat(findElement("vis_g").bBoxGlobal).isEqualTo(visBounds)
            assertThat(findElement("vis_rect").bBoxGlobal).isEqualTo(visBounds)

            // outer group
            assertThat(findElement("root_g").bBoxGlobal).isEqualTo(LTRB(9.5f, 19.5f, 70.5f, 60.5f))
        }
    }
}
