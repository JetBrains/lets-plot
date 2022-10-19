/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import jetbrains.datalore.plot.parsePlotSpec
import kotlin.math.pow
import kotlin.random.Random
import kotlin.random.nextInt

class TransformLog10 {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            issue292(),
            issue301(),
            issue284(),
            issue284_1(),
            scale_y_log10(),
            scale_y_log10_with_const_y(),
        )
    }

    private fun issue292(): MutableMap<String, Any> {
        // NPE on negative value in data and scale_xxx(trans='log10')

        // ggplot(data, aes(x='x', y='y')) + geom_point(aes(color='v')) + scale_color_gradient(trans='log10')

        val spec = """
            {
             'kind': 'plot',
             'mapping': {'x': 'x', 'y': 'y'},
             'scales': [{'aesthetic': 'color',
               'trans': 'log10',
               'scale_mapper_kind': 'color_gradient'}],
             'layers': [
             {'geom': 'point',
               'mapping': {'color': 'c'}
                }]}
        """.trimIndent()

        val plotSpec = HashMap(parsePlotSpec(spec))
        plotSpec["data"] = mapOf(
            "x" to listOf(0, 1, 2, 3, 4),
            "y" to listOf(0, 1, 4, 9, 12),
            "c" to listOf(-1, 0, 0.01, 1, 81),
        )
        return plotSpec
    }

    private fun issue301(): MutableMap<String, Any> {
        // Need to skip "bad" values during scale transformation #301

        //        np.random.seed(42)
        //
        //        n = 1000
        //        x = np.linspace(-10, 10, n)
        //        y = x ** 2 + np.random.normal(size=n)
        //        z = 2.0 ** x + np.random.normal(size=n)
        //        data = {'x': x, 'y': y, 'z': z}
        //
        //        p = (ggplot(data, aes(x='x', y='y')) +
        //                geom_point(aes(color='z'), tooltips=layer_tooltips().format("^color", ".3f")) +
        //                # scale_y_log10() + scale_color_gradient(low='red', high='green', trans='log10'))
        //        scale_y_log10() + scale_color_gradient(low='red', high='green', trans='log10', limits=[0.01, None]))
        //
        //        # scale_y_log10() + scale_color_gradient(low='red', high='green'))

        val spec = """
        {             
         'mapping': {'x': 'x', 'y': 'y'},
         'kind': 'plot',
         'scales': [
           {'aesthetic': 'y',
           'trans': 'log10'
           },
          {'aesthetic': 'color',
           'limits': [],
           'trans': 'log10',
           'low': 'red',
           'high': 'green',
           'scale_mapper_kind': 'color_gradient'}
           ],
         'layers': [
          {'geom': 'point',
           'mapping': {'color': 'z'},
           'tooltips': {'tooltip_formats': [{'field': '^color', 'format': '.3f'}]}
           }]
         }
         """.trimIndent()

        val n = 1000
        val step = 20.0 / n
        //        x = np.linspace(-10, 10, n)
        //        y = x ** 2 + np.random.normal(size=n)
        //        z = 2.0 ** x + np.random.normal(size=n)
        val x = generateSequence(-10.0) { it + step }.takeWhile { it <= 10 }.toList()
        val y = x.map { it.pow(2.0) + Random.nextDouble() * 10 }
        val z = x.map { 2.0.pow(it) + Random.nextDouble() * 10 }

        val plotSpec = HashMap(parsePlotSpec(spec))
        plotSpec["data"] = mapOf(
            "x" to x,
            "y" to y,
            "z" to z
        )
        return plotSpec
    }

    private fun issue284(): MutableMap<String, Any> {
        // Legend is broken when using scale_fill_brewer with 'trans' parameter #284

//        data = dict (
//            x = [v for v in range(10)],
//        y = [v for v in range(10)],
//        z = [2**v for v in range(10)],
//        )
//
//        (ggplot(data, aes('x', 'y')) + geom_point(aes(color="z"), size=10)
//                + scale_color_brewer(palette="YlOrBr", trans="log10")
//                )
        val spec = """
{'data': {'x': [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
  'y': [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
  'z': [1, 2, 4, 8, 16, 32, 64, 128, 256, 512]},
 'mapping': {'x': 'x', 'y': 'y'},
 'kind': 'plot',
        'scales': [{'aesthetic': 'color',
            'trans': 'log10',
            'palette': 'YlOrBr',
            'scale_mapper_kind': 'color_brewer'}],
 'layers': [{'geom': 'point',
   'mapping': {'color': 'z'},
   'size': 10}]
   }
         """.trimIndent()


        return parsePlotSpec(spec)
    }

    private fun issue284_1(): MutableMap<String, Any> {
        // Same as befoe but the scale is:
        // scale_color_continuous(trans="log10", guide='legend')
        val spec = """
{'data': {'x': [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
  'y': [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
  'z': [1, 2, 4, 8, 16, 32, 64, 128, 256, 512]},
 'mapping': {'x': 'x', 'y': 'y'},
 'kind': 'plot',
 'scales': [{'aesthetic': 'color',
   'trans': 'log10',
   'guide': 'legend'
   }],
 'layers': [{'geom': 'point',
   'mapping': {'color': 'z'},
   'size': 10}]
   }
         """.trimIndent()

        return parsePlotSpec(spec)
    }

    private fun scale_y_log10(): MutableMap<String, Any> {
        // ggplot({'x': x}, aes(x='x')) + geom_histogram() + scale_y_log10()

        val rnd = Random(0)
        val x = (1..100).map { rnd.nextInt(0..5) }.joinToString(transform = Int::toString)
        val spec = """
            {
              "data": {
                "x":  [$x]
              },
              "mapping": {
                "x": "x"
              },
              "kind": "plot",
              "scales": [
                {
                  "aesthetic": "y",
                  "trans": "log10"
                }
              ],
              "layers": [
                {
                  "geom": "histogram"
                }
              ]
            }""".trimIndent()

        return parsePlotSpec(spec)
    }

    @Suppress("FunctionName")
    private fun scale_y_log10_with_const_y(): MutableMap<String, Any> {
        // ggplot({'x': x}, aes(x='x')) + geom_histogram() + scale_y_log10()

        val rnd = Random(0)
        val x = (1..100).map { rnd.nextInt(0..5) }.joinToString(transform = Int::toString)
        val spec = """
            {
             'kind': 'plot',
             'scales': [{'aesthetic': 'y', 'trans': 'log10'}],
             'layers': [
                {   'geom': 'label',
                    'mapping': {'y': [8]},
                    'label': 'Data y=8'
                },
                {   'geom': 'label',
                    'label': 'Const y=10',
                    'y': 10}
               ]
             }
             """.trimIndent()

        return parsePlotSpec(spec)
    }
}