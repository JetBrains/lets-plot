/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import jetbrains.datalore.plot.parsePlotSpec

class LongTooltipText {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            example()
        )
    }

    private fun example(): MutableMap<String, Any> {
        val spec = """{
          "data": {
            "x": [
              0
            ],
           "name": [
              "The Lorem ipsum text"
            ],
            "type": [
              "Text"
            ],
            "description": [
                "A placeholder text commonly used to demonstrate the visual form of a document or a typeface without relying on meaningful content."
            ],
            "text": [
              "Lorem ipsum dolor sit amet, consectetur adipiscing elit. In ligula lacus, lobortis et metus in, consequat vulputate arcu. Morbi dui nibh, accumsan sit amet sagittis sed, aliquet nec ligula. Donec at metus sit amet tellus bibendum interdum quis ut lorem. Sed eget lectus et lectus porttitor laoreet eget et nunc. Nulla facilisi. Sed vehicula rhoncus velit quis tincidunt. Mauris varius efficitur quam, a dapibus quam maximus at. Nullam quis ullamcorper sem. Sed suscipit metus quis tempus bibendum. In nec sagittis mauris. Sed finibus tortor dignissim, hendrerit elit eget, mattis leo. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia curae; Nunc eget pretium orci, eget pulvinar velit. Vestibulum quam risus, gravida ut vestibulum non, semper sed est."
            ]
          },
          "mapping": {
            "x": "x"
          },
          "kind": "plot",
          "scales": [ {  "aesthetic" : "y", "limits" : [0, 1] } ],
          "layers": [
            {
              "geom": "point",
              "color": "#94ccd1",
              "tooltips": {
                "lines": [
                  "@|@type",
                  "@|@description",
                  "@|@text"
                ]
              }
            }
          ] 
        }""".trimIndent()
        return parsePlotSpec(spec)
    }
}