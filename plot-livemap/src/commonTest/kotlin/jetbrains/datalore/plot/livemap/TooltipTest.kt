/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.livemap

import org.jetbrains.letsPlot.commons.event.MouseEvent.Companion.noButton
import org.jetbrains.letsPlot.commons.event.MouseEventSpec.MOUSE_MOVED
import org.jetbrains.letsPlot.commons.geometry.Vector
import kotlin.test.Test

class TooltipTest {
    @Test
    fun simple() {
        val liveMapAdapter = LiveMapTestAdapter(
            """
                    {
                      "data": {
                        "city": [ "Prague", "Petersburg", "Moscow", "Novosibirsk", "M\u00fcnchen", "Amsterdam", "Boston", "Marlton", "Foster City"],
                        "country": [ "CZ", "Russia", "Russia", "Russia", "Germany", "Netherlands", "US", "US", "US"],
                        "kind": [ "Headquarters", "R&D Center", "R&D Center", "R&D Center", "R&D Center", "R&D Center", "R&D Center", "Sales", "Sales"],
                        "size": [ 100, 1000, 100, 50, 200, 100, 10, 10, 10]
                      },
                      "kind": "plot",
                      "layers": [
                        {
                          "geom": "livemap",
                          "tiles": {
                            "kind": "chessboard"
                          },
                          "geocoding": {
                            "url": "http://10.0.0.127:3020/map_data/geocoding"
                          }
                        },
                        {
                          "geom": "point",
                          "mapping": {
                            "color": "kind",
                            "shape": "kind",
                            "size": "size"
                          },
                          "tooltips": {
                            "lines": [
                              "capacity: @size"
                            ],
                            "title": "@kind\n@city"
                          },
                          "map": {
                            "city": [ "Prague", "Petersburg", "Moscow", "Novosibirsk", "M\u00fcnchen", "Amsterdam", "Boston", "Marlton", "Foster City"],
                            "found name": [
                              "Praha",
                              "\u0421\u0430\u043d\u043a\u0442-\u041f\u0435\u0442\u0435\u0440\u0431\u0443\u0440\u0433",
                              "\u041c\u043e\u0441\u043a\u0432\u0430",
                              "\u041d\u043e\u0432\u043e\u0441\u0438\u0431\u0438\u0440\u0441\u043a",
                              "M\u00fcnchen",
                              "Amsterdam",
                              "Boston",
                              "Marlton",
                              "Foster City"
                            ],
                            "country": [ "CZ", "Russia", "Russia", "Russia", "Germany", "Netherlands", "US", "US", "US"],
                            "geometry": [
                              "{\"type\": \"Point\", \"coordinates\": [14.4464595291714, 50.0596285611391]}",
                              "{\"type\": \"Point\", \"coordinates\": [30.3806409956848, 59.9178470671177]}",
                              "{\"type\": \"Point\", \"coordinates\": [37.6254435341603, 55.7245371490717]}",
                              "{\"type\": \"Point\", \"coordinates\": [82.9515998639691, 54.9678142368793]}",
                              "{\"type\": \"Point\", \"coordinates\": [11.5258078608938, 48.1545735150576]}",
                              "{\"type\": \"Point\", \"coordinates\": [4.89797546594476, 52.3745398968458]}",
                              "{\"type\": \"Point\", \"coordinates\": [-71.0884755326693, 42.3110405355692]}",
                              "{\"type\": \"Point\", \"coordinates\": [-76.7826649049812, 38.7618731707335]}",
                              "{\"type\": \"Point\", \"coordinates\": [-122.265069166368, 37.5543051213026]}"
                            ]
                          },
                          "map_join": [["city"], ["city"]],
                          "size": 10,
                          "map_data_meta": {
                            "geodataframe": {
                              "geometry": "geometry"
                            }
                          }
                        }
                      ]
                    }
                    """
        )
        
        liveMapAdapter.dispatchTimerEvent(16)
        liveMapAdapter.dispatchTimerEvent(32)
        liveMapAdapter.dispatchMouseEvent(MOUSE_MOVED, noButton(Vector(200, 200)))
        liveMapAdapter.dispatchTimerEvent(48)
        liveMapAdapter.getHoverObjects()
    }
}

