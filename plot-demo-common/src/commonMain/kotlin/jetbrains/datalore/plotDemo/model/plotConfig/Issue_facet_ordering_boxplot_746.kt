/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import jetbrains.datalore.plot.parsePlotSpec

/**
 * https://github.com/JetBrains/lets-plot/issues/746
 */
@Suppress("ClassName")
class Issue_facet_ordering_boxplot_746 {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            case0()
        )
    }

    private fun case0(): MutableMap<String, Any> {
        val spec = """
            {
                'kind': 'plot',
                'mapping':  {
                                'x': 'chrom',
                                'y': 'y'
                            },
                'layers':   [
                                {
                                    'geom': 'boxplot'
                                }
                            ],
                'facet':{ 'name': 'grid', 'y': 'arm', 'y_order': 1}                            
            }
        """.trimIndent()

        val plotSpec = HashMap(parsePlotSpec(spec))

        // Data (n=20)

//        {'data':    chrom         y arm
//                    2   chr1 -0.908024   q
//                    6   chr1  0.067528   q
//                    7   chr1 -1.424748   q
//                    9   chr2  0.110923   q
//                    13  chr4 -0.291694   q
//                    12  chr4 -0.600639   q
//                    11  chr4  0.375698   p
//                    10  chr4 -1.150994   p
//                    0   chr4 -1.012831   q
//                    4   chr4  1.465649   q
//                    3   chr4 -1.412304   q
//                    8   chr4 -0.544383   q
//                    19  chr4 -1.220844   p
//                    18  chr5  0.822545   p
//                    5   chr5 -0.225776   q
//                    1   chr5  0.314247   p
//                    14  chr5 -0.601707   q
//                    16  chr5 -0.013497   q
//                    17  chr5 -1.057711   p,

        val data = mapOf<String, List<Any>>(
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


        plotSpec["data"] = data
        return plotSpec
    }
}