/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.skia.shape

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

        val visBounds = LTRB(10f, 50f, 20f, 60f)

        with(doc) {
            val hiddenBounds = LTRB(5f, 20f, 15f, 25f)
            assertThat(element("hidden_g").screenBounds).isEqualTo(hiddenBounds)
            assertThat(element("hidden_sub_g").screenBounds).isEqualTo(hiddenBounds)
            assertThat(element("hidden_rect").screenBounds).isEqualTo(hiddenBounds)

            // visible elements
            assertThat(element("vis_g").screenBounds).isEqualTo(visBounds)
            assertThat(element("vis_rect").screenBounds).isEqualTo(visBounds)

            // outer group
            assertThat(element("root_g").screenBounds).isEqualTo(LTRB(5f, 20f, 20f, 60f))
        }

        doc.element("hidden_g").transform = AffineTransform.makeTranslation(60f, 20f)

        with(doc) {
            val hiddenBounds = LTRB(60f, 20f, 70f, 25f)
            assertThat(element("hidden_g").screenBounds).isEqualTo(hiddenBounds)
            assertThat(element("hidden_sub_g").screenBounds).isEqualTo(hiddenBounds)
            assertThat(element("hidden_rect").screenBounds).isEqualTo(hiddenBounds)

            // visible elements
            assertThat(element("vis_g").screenBounds).isEqualTo(visBounds)
            assertThat(element("vis_rect").screenBounds).isEqualTo(visBounds)

            // outer group
            assertThat(element("root_g").screenBounds).isEqualTo(LTRB(10f, 20f, 70f, 60f))
        }
    }
}
