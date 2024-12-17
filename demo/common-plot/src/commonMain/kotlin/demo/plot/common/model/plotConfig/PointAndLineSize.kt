/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.common.model.plotConfig

import demoAndTestShared.parsePlotSpec

class PointAndLineSize {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            checkSize(shape = "1"),
            checkSize(shape = "10"),
            checkSize(shape = "16"),
            checkSize(shape = "21"),
        )
    }

    private fun checkSize(shape: String, size: Double = 20.0): MutableMap<String, Any> {
        val spec = """{
  'ggtitle': { 'text': 'point shape = $shape, line size = $size' },            
  "theme": {
    "name": "classic",
    "axis": "blank"
  },
  "ggsize": {
    "width": 600.0,
    "height": 180.0
  },
  "scales": [ {  "aesthetic" : "y", "limits" : [0.9, 1.2] } ],
  "kind": "plot",
  "layers": [
    {
      "geom": "hline",
      "tooltips": "none",
      "yintercept": 1.0,
      "size": $size
    },

    {
      "geom": "text",
      "x": 0.5,
      "y": 1.1,
      "hjust": 0,
      "label": "size=${size / 2}\nstroke=${size / 2}"
    },
    {
      "geom": "point",
      "x": 0.5,
      "y": 1.0,
      "shape": $shape,
      "size": ${size / 2},
      "stroke": ${size / 2},
      "fill": "orange",
      "color": "green"
    },
    
    {
      "geom": "text",
      "x": 0.6,
      "y": 1.1,
      "label": "size=$size\nstroke=0",
      "hjust": 0.5
    },
    {
      "geom": "point",
      "x": 0.6,
      "y": 1.0,
      "shape": $shape,
      "size": $size,
      "stroke": 0.0,
      "fill": "orange",
      "color": "green"
    },
    
    {
      "geom": "text",
      "x": 0.7,
      "y": 1.1,
      "label": "size=0\nstroke=$size",
      "hjust": 1
    },    
    {
      "geom": "point",
      "x": 0.7,
      "y": 1.0,
      "shape": $shape,
      "size": 0.0,
      "stroke": $size,
      "fill": "orange",
      "color": "green"
    }
  ]
}"""
        return parsePlotSpec(spec)
    }
}