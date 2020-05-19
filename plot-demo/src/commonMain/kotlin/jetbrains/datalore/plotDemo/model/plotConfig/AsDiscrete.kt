/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import jetbrains.datalore.plot.parsePlotSpec
import jetbrains.datalore.plotDemo.model.PlotConfigDemoBase

class AsDiscrete : PlotConfigDemoBase() {
    fun plotSpecList(): List<Map<String, Any>> {
        return listOf(
            missingScale()
//            plotData_DiscreteGroup(),
//            fillFactor(),
//            fillAndColorFactor(),
//            fillFactorWithScaleColor(),
//            layerData_DiscreteGroup(),
//            smoothStatAsDiscrete(),
//            smoothStatWithGroup()
        )
    }

    private fun missingScale(): Map<String, Any> {
        val spec = """
            {
              "data": {
                "Unnamed: 0": [220, 56, 60, 208, 226, 11, 120, 144, 187, 59, 234, 146, 112, 90, 106, 38, 73, 230, 117, 96],
                "manufacturer": ["volkswagen", "dodge", "dodge", "volkswagen", "volkswagen", "audi", "hyundai", "nissan", "toyota", "dodge", "volkswagen", "nissan", "hyundai", "ford", "honda", "dodge", "dodge", "volkswagen", "hyundai", "ford"],
                "model": ["jetta", "dakota pickup 4wd", "durango 4wd", "gti", "new beetle", "a4 quattro", "tiburon", "altima", "camry solara", "durango 4wd", "passat", "altima", "sonata", "f150 pickup 4wd", "civic", "caravan 2wd", "ram 1500 pickup 4wd", "passat", "tiburon", "mustang"],
                "displ": [2.8, 5.2, 4.7, 2.0, 2.5, 2.0, 2.7, 2.5, 2.2, 4.7, 3.6, 3.5, 2.4, 5.4, 1.8, 2.4, 5.7, 2.0, 2.0, 4.6],
                "year": [1999, 1999, 2008, 1999, 2008, 2008, 2008, 2008, 1999, 2008, 2008, 2008, 2008, 2008, 2008, 1999, 2008, 2008, 1999, 1999],
                "cyl": [6, 8, 8, 4, 5, 4, 6, 4, 4, 8, 6, 6, 4, 8, 4, 4, 8, 4, 4, 8],
                "trans": [ "auto(l4)", "manual(m5)", "auto(l5)", "manual(m5)", "manual(m5)", "auto(s6)", "auto(l4)", "auto(av)", "auto(l4)", "auto(l5)", "auto(s6)", "manual(m6)", "manual(m5)", "auto(l4)", "auto(l5)", "auto(l3)", "auto(l5)", "auto(s6)", "manual(m5)", "manual(m5)"],
                "drv": [ "f", "4", "4", "f", "f", "4", "f", "f", "f", "4", "f", "f", "f", "4", "f", "f", "4", "f", "f", "r"],
                "cty": [ 16, 11, 9, 21, 20, 19, 17, 23, 21, 13, 17, 19, 21, 13, 25, 18, 13, 19, 19, 15],
                "hwy": [ 23, 17, 12, 29, 28, 27, 24, 31, 27, 17, 26, 27, 31, 17, 36, 24, 17, 28, 29, 22],
                "fl": [ "r", "r", "e", "r", "r", "p", "r", "r", "r", "r", "p", "p", "r", "r", "r", "r", "r", "p", "r", "r"],
                "class": [ "compact", "pickup", "suv", "compact", "subcompact", "compact", "subcompact", "midsize", "compact", "suv", "midsize", "midsize", "midsize", "pickup", "subcompact", "minivan", "pickup", "midsize", "subcompact", "subcompact"]
              },
              "mapping": {
                "x": "displ",
                "y": "hwy"
              },
              "kind": "plot",
              "layers": [
                {
                  "geom": "point",
                  "mapping": {
                    "color": "cyl"
                  }
                },
                {
                  "geom": "smooth",
                  "mapping": {
                    "color": "cyl"
                  },
                  "data_meta": {
                    "mapping_annotations": [
                      {
                        "aes": "color",
                        "annotation": "as_discrete",
                        "parameters": {
                          "label": "cyl"
                        }
                      }
                    ]
                  },
                  "method": "lm",
                  "deg": 2,
                  "size": 1
                }
              ]
            }
        """.trimIndent()
        return parsePlotSpec(spec)
    }

    private val data = """{
        |"x": [0, 5, 10, 15], 
        |"y": [0, 5, 10, 15], 
        |"a": [1, 2, 4, 6], 
        |"b": [10, 11, 12, 13], 
        |"c": ["a", "a", "b", "b"], 
        |"g": [0, 0, 1, 1]
|}""".trimMargin()

    private val smoothData = """
        |{
        |    "x": [0, 2, 5, 8, 9, 12, 16, 20, 40],
        |    "y": [3, 1, 2, 7, 8, 9, 10, 10, 10],
        |    "g": [0, 0, 0, 1, 1, 1, 2, 2, 2],
        |    "d": ['0', '0', '0', '1', '1', '1', '2', '2', '2']
        |}
    """.trimMargin()


    private fun plotData_DiscreteGroup(): Map<String, Any> {
        val spec = """
            {
              "kind": "plot",
              "data": $data,
              "mapping": {
                "x": "x",
                "y": "y",
                "color": "g"
              },
              "data_meta": {
                "mapping_annotations": [
                  {
                    "aes": "color",
                    "annotation": "as_discrete",
                    "parameters": {"label": "clr"}
                  }
                ]
              },
              "layers": [
                {
                  "geom": "line",
                  "mapping": {
                  },
                  "size": 3
                }
              ]
            }
        """.trimIndent()
        return parsePlotSpec(spec)
    }

    private fun layerData_DiscreteGroup(): Map<String, Any> {
        val spec = """
            {
              "kind": "plot",
              "layers": [
                {
                  "data": $data,
                  "geom": "line",
                  "mapping": {
                    "x": "x",
                    "y": "y",
                    "color": "g"
                  },
                  "data_meta": {
                    "mapping_annotations": [
                      {
                        "aes": "color",
                        "annotation": "as_discrete"
                      }
                    ]
                  },
                  "size": 3
                }
              ]
            }
        """.trimIndent()
        return parsePlotSpec(spec)
    }

    private fun smoothStatAsDiscrete(): Map<String, Any> {
        val spec = """
            {
              "mapping": {
                "x": "x",
                "y": "y"
              },
              "kind": "plot",
              "layers": [
                {
                  "data": $smoothData,
                  "geom": "smooth",
                  "mapping": {
                    "color": "g"
                  },
                  "data_meta": {
                    "mapping_annotations": [
                      {
                        "aes": "color",
                        "annotation": "as_discrete"
                      }
                    ]
                  },
                  "se": false
                }
              ]
            }
        """.trimIndent()
        return parsePlotSpec(spec)
    }
    private fun smoothStatWithGroup(): Map<String, Any> {
        val spec = """
            {
              "data": $smoothData,
              "mapping": {
                "x": "x",
                "y": "y"
              },
              "kind": "plot",
              "layers": [
                {
                  "geom": "smooth",
                  "mapping": {
                    "color": "g",
                    "group": "g"
                  },
                  "data_meta": {
                    "mapping_annotations": [
                      {
                        "aes": "color",
                        "annotation": "as_discrete"
                      }
                    ]
                  },
                  "se": false
                }
              ]
            }
        """.trimIndent()
        return parsePlotSpec(spec)
    }


    private fun fillFactor(): Map<String, Any> {

        val spec = """
{
    "ggtitle": { "text": "... fill=as_discrete(a) ..."}, 
    "kind": "plot", 
    "layers": [{
        "geom": "point", 
        "data": $data,
        "mapping": {"x": "x", "y": "y", "fill": "a", "color": "b"}, 
        "data_meta": {"mapping_annotations": [{"aes": "fill", "annotation": "as_discrete"}]},
        "shape": 21, 
        "size": 9
    }]
}        """.trimIndent()
        return parsePlotSpec(spec)
    }


    private fun fillFactorWithScaleColor(): Map<String, Any> {

        val spec = """
{
    "ggtitle": { "text": "... fill=as_discrete(a), scale_color_discrete() ..."}, 
    "data": $data, 
    "mapping": {"x": "x", "y": "y"}, 
    "kind": "plot", 
    "scales": [{"aesthetic": "color", "discrete": true}],
    "layers": [{
        "geom": "point", 
        "mapping": {"fill": "a", "color": "b"}, 
        "data_meta": {"mapping_annotations": [{"aes": "fill", "annotation": "as_discrete"}]},
        "shape": 21, 
        "size": 9
    }]
}        """.trimIndent()
        return parsePlotSpec(spec)
    }


    private fun fillAndColorFactor(): Map<String, Any> {

        val spec = """
{
    "ggtitle": { "text": "... fill=as_discrete(a), color=as_discrete(b) ..."}, 
    "data": $data, 
    "mapping": {"x": "x", "y": "y"}, 
    "kind": "plot", 
    "layers": [{
        "geom": "point", 
        "mapping": {"fill": "a", "color": "b"}, 
        "data_meta": {"mapping_annotations": [
            {"aes": "fill", "annotation": "as_discrete"}, 
            {"aes": "color", "annotation": "as_discrete"}
        ]},
        "shape": 21, 
        "size": 9
    }]
}        """.trimIndent()
        return parsePlotSpec(spec)
    }

}