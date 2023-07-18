/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config.transform

import demoAndTestShared.parsePlotSpec
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class PlotSpecTransformTest {

    private fun assertTransformed(expected: String, input: String, transform: PlotSpecTransform) {
        val inputSpec = parsePlotSpec(input)
        assertTransformed(expected, inputSpec, transform)
    }

    private fun assertTransformed(expected: String, input: MutableMap<*, *>, transform: PlotSpecTransform) {
        val expectedSpec = parsePlotSpec(expected)
        assertTransformed(expectedSpec, input, transform)
    }

    private fun assertTransformed(expected: Map<*, *>, input: MutableMap<*, *>, transform: PlotSpecTransform) {
        val transformedSpec = transform.apply(input)
        assertEquals(expected, transformedSpec)
    }

    @Test
    fun transformDefault() {
        val input = "{" +
                "  'key_1_0': null," +
                "  'key_1_1': [" +
                "               {" +
                "                 'key_2_0':  {" +
                "                               'key_3_0': null" +
                "                             }" +
                "                  ," +
                "                 'key_2_1': null" +
                "               }" +
                "             ]" +
                "}"

        // add Integer
        val inputSpec = parsePlotSpec(input)
        //inputSpec.put("key_1_2", 0);

        // add none-string key
        val inputSpecWithNonStringKey = HashMap<Any, Any>(inputSpec)
        inputSpecWithNonStringKey[StringBuilder("key_1_2")] = "a"

        val expect = "{" +
                //"  'key_1_0': null," +  // gone
                "  'key_1_1': [" +
                "               {" +
                "                 'key_2_0':  {" +
                //"                               'key_3_0': null" +  // gone
                "                             }" +
                //"                  ," +
                //"                 'key_2_1': null" +                // gone
                "               }" +
                "             ]," +
                "  'key_1_2': 'a'" +     // key :  string

                "}"


        val transform = PlotSpecTransform.builderForRawSpec().build()
        assertTransformed(expect, inputSpecWithNonStringKey, transform)
    }

    @Test
    fun onlyApplicableApplied() {
        val input = "{" +
                "  'key_1_0': {}," +
                "  'key_1_1': { 'applicable': true,  'value': 'OK'}," +
                "  'key_1_2': { 'applicable': false, 'value': 'ERR'}" +
                "}"

        val handlingCount = intArrayOf(0)
        val handledValue = arrayOf<Any?>(null)
        val handler = object : SpecChange {
            override fun isApplicable(spec: Map<String, Any>): Boolean {
                return spec.containsKey("applicable") && spec["applicable"] as Boolean
            }

            override fun apply(spec: MutableMap<String, Any>, ctx: SpecChangeContext) {
                handlingCount[0] += 1
                handledValue[0] = spec["value"]
            }
        }

        val inputSpec = parsePlotSpec(input)
        val transform = PlotSpecTransform.builderForRawSpec()
                .change(SpecSelector.of("key_1_0"), handler)
                .change(SpecSelector.of("key_1_1"), handler)
                .change(SpecSelector.of("key_1_2"), handler)
                .build()

        transform.apply(inputSpec)
        assertEquals(1, handlingCount[0].toLong())
        assertEquals("OK", handledValue[0])
    }

    @Test
    fun specHandled() {

    }

    @Test
    fun oneHandler() {
        val input = "{" +
                "  'key_1_0': [" +
                "               {" +
                "                 'key_2_0':  'a'" +
                "                  ," +
                "                 'key_2_1': {" +
                "                              'key_3_0':  'a'" +
                "                               ," +
                "                              'key_3_1': null" +
                "                            }" +
                "               }" +
                "             ]" +
                "}"

        val sel = SpecSelector.of("key_1_0", "key_2_1")

        val transform = PlotSpecTransform.builderForRawSpec()
                .change(sel, object : SpecChange {
                    override fun apply(spec: MutableMap<String, Any>, ctx: SpecChangeContext) {
                        spec["key_3_0"] = spec.containsKey("key_3_0")
                    }
                })
                .build()

        val expect = "{" +
                "  'key_1_0': [" +
                "               {" +
                "                 'key_2_0':  'a'" +
                "                  ," +
                "                 'key_2_1': {" +
                "                              'key_3_0':  true" +
                //"                               ," +
                //"                              'key_3_1': null" +    // gone
                "                            }" +
                "               }" +
                "             ]" +
                "}"

        assertTransformed(expect, input, transform)
    }

    @Test
    fun depthFirstHandling() {
        val input = "{" +
                "  'key_1_0': [" +
                "               {" +
                "                 'key_2_0': {" +
                "                              'key_3_0': {" +
                "                                           'key_4_0': {" +
                "                                                        'key_5_0': 0.0" +
                "                                                      }" +
                "                                         }" +
                "                            }" +
                "               }" +
                "             ]" +
                "}"

        val outerSel = SpecSelector.of("key_1_0", "key_2_0", "key_3_0")
        val deepSel = SpecSelector.of("key_1_0", "key_2_0", "key_3_0", "key_4_0")
        val transform = PlotSpecTransform.builderForRawSpec()
                .change(outerSel, object : SpecChange {
                    override fun apply(spec: MutableMap<String, Any>, ctx: SpecChangeContext) {
                        if (spec["key_4_0"] is Map<*, *>) {
                            @Suppress("UNCHECKED_CAST")
                            val subSpec = spec["key_4_0"] as MutableMap<Any, Any>
                            val v = subSpec["key_5_0"]
                            if (v is Double) {
                                if (v == 1.0) {
                                    subSpec["key_5_0"] = 2.0
                                }
                            }
                        }
                    }
                })
                .change(deepSel, object : SpecChange {
                    override fun apply(spec: MutableMap<String, Any>, ctx: SpecChangeContext) {
                        val v = spec["key_5_0"]
                        if (v is Double) {
                            spec["key_5_0"] = v + 1.0
                        }
                    }
                })
                .build()

        val expect = "{" +
                "  'key_1_0': [" +
                "               {" +
                "                 'key_2_0': {" +
                "                              'key_3_0': {" +
                "                                           'key_4_0': {" +
                "                                                        'key_5_0': 2.0" +
                "                                                      }" +
                "                                         }" +
                "                            }" +
                "               }" +
                "             ]" +
                "}"

        assertTransformed(expect, input, transform)
    }

    @Test
    fun listTransformedByHandler() {
        val input = mutableMapOf(
                "key_1_0" to mapOf("data" to listOf(1, 2, 3)),
                "key_1_1" to mapOf("data" to listOf(1, 2, 3))
        )

        /*
    String expect = "{" +
        //"  'key_1_0': {'data' : [1.0, 2.0, 3.0]}," +       // transformed to doubles (default)
        "  'key_1_0': {'data' : [1, 2, 3]}," +       // transformed to doubles (default)
        "  'key_1_1': {'data' : '1,2,3'}" +                // transformed to str by handler
        "}";
    */

        val expect = mapOf(
                "key_1_0" to mapOf("data" to listOf(1, 2, 3)),
                "key_1_1" to mapOf("data" to "1,2,3")     // transformed to str by handler
        )

        val sel = SpecSelector.of("key_1_1")
        val transform = PlotSpecTransform.builderForRawSpec()
                .change(sel, object : SpecChange {
                    override fun apply(spec: MutableMap<String, Any>, ctx: SpecChangeContext) {
                        @Suppress("UNCHECKED_CAST")
                        val data = spec["data"] as List<Number>
                        val list = data.map { number -> number.toString() }
                        spec["data"] = list.joinToString(",")
                    }
                })
                .build()

        assertTransformed(expect, input, transform)
    }

    @Test
    fun listNotTouchedByDefaultTransformation() {
        val inputData = listOf(1, 2, 3)
        val input = mutableMapOf(
                "key_1_0" to mapOf("data" to inputData)
        )

        val transform = PlotSpecTransform.builderForRawSpec().build()

        val transformedSpec = transform.apply(input)

        val expectedSpec: Map<*, *> = mapOf(
                "key_1_0" to mapOf("data" to inputData)             // wasn't changed
        )

        assertEquals(expectedSpec, transformedSpec)

        val transformedValue = transformedSpec["key_1_0"] as Map<*, *>
        assertSame(inputData, transformedValue["data"])
    }
}