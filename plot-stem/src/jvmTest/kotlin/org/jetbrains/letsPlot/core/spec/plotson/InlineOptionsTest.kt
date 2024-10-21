/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.plotson

import org.assertj.core.api.Assertions.assertThat
import java.util.Map.entry
import kotlin.test.Test

class InlineOptionsTest {

    class Opts : Options() {
        var name: String? by map("NAME")
        var foo: InlineOptions? by map("FOO")
    }

    @Test
    fun simpleInlineOptions() {
        class InlineFoo : InlineOptions() {
            var fooType: String? by map("FOO_TYPE")
            var foo: Double? by map("FOO")
        }

        val opts = Opts().apply {
            name = "Demo"
            foo = InlineFoo().apply {
                fooType = "InlineFoo"
                foo = 42.0
            }
        }

        @Suppress("UNCHECKED_CAST")
        val json = toJson(opts.properties) as Map<String, Any?>

        assertThat(json).containsOnly(
            entry("NAME", "Demo"),
            entry("FOO_TYPE", "InlineFoo"),
            entry("FOO", 42.0)
        )
    }

    @Test
    fun inlineOptionsWithNested() {
        class Bar : Options() {
            var barType: String? by map("BAR_TYPE")
            var bar: Double? by map("BAR")
        }

        class FooInline : InlineOptions() {
            var fooType: String? by map("FOO_TYPE")
            var foo: Double? by map("FOO")
            var bar: Bar? by map("FOO.BAR")
        }

        val opts = Opts().apply {
            name = "Demo"
            foo = FooInline().apply {
                fooType = "InlineFoo"
                foo = 42.0
                bar = Bar().apply {
                    barType = "NestedBar"
                    bar = 24.0
                }
            }
        }

        @Suppress("UNCHECKED_CAST")
        val json = toJson(opts) as Map<String, Any?>

        assertThat(json).containsOnly(
            entry("NAME", "Demo"),
            entry("FOO_TYPE", "InlineFoo"),
            entry("FOO", 42.0),
            entry(
                "FOO.BAR", mapOf(
                    "BAR_TYPE" to "NestedBar",
                    "BAR" to 24.0
                )
            )
        )
    }

    @Test
    fun inlineWithInline() {
        class InlineBar : InlineOptions() {
            var barType: String? by map("BAR_TYPE")
            var bar: Double? by map("BAR")
        }

        class InlineFoo : InlineOptions() {
            var fooType: String? by map("FOO_TYPE")
            var foo: Double? by map("FOO")
            var inlineBar: InlineBar? by map("INLINE_BAR")
        }

        val opts = Opts().apply {
            name = "Demo"
            foo = InlineFoo().apply {
                fooType = "InlineFoo"
                foo = 42.0
                inlineBar = InlineBar().apply {
                    barType = "InlineBar"
                    bar = 24.0
                }
            }
        }

        @Suppress("UNCHECKED_CAST")
        val json = toJson(opts) as Map<String, Any?>

        assertThat(json).containsOnly(
            entry("NAME", "Demo"),
            entry("FOO_TYPE", "InlineFoo"),
            entry("FOO", 42.0),
            entry("BAR_TYPE", "InlineBar"),
            entry("BAR", 24.0)
        )
    }

    @Test
    fun inlineWithToSpecToString() {
        class InlineFoo : InlineOptions(toSpecDelegate = { "InlineFooString" })

        val opts = Opts().apply {
            name = "Demo"
            foo = InlineFoo()
        }

        @Suppress("UNCHECKED_CAST")
        val json = toJson(opts) as Map<String, Any?>

        assertThat(json).containsOnly(
            entry("NAME", "Demo"),
            entry("FOO", "InlineFooString"),
        )
    }

    @Test
    fun `in list InlineOptions behaves like regular Options and creates separate map`() {
        class InlineFoo : InlineOptions() {
            var fooType: String? by map("FOO_TYPE")
            var foo: Double? by map("FOO")
        }

        class Opts : Options() {
            var name: String? by map("NAME")
            var foos: List<InlineFoo>? by map("FOOS")
        }

        val opts = Opts().apply {
            name = "Demo"
            foos = listOf(
                InlineFoo().apply {
                    fooType = "InlineFoo"
                    foo = 42.0
                },
                InlineFoo().apply {
                    fooType = "InlineFoo"
                    foo = 777.0
                }
            )
        }

        @Suppress("UNCHECKED_CAST")
        val json = toJson(opts) as Map<String, Any?>

        assertThat(json).containsOnly(
            entry("NAME", "Demo"),
            entry(
                "FOOS", listOf(
                    mapOf(
                        "FOO_TYPE" to "InlineFoo",
                        "FOO" to 42.0
                    ),
                    mapOf(
                        "FOO_TYPE" to "InlineFoo",
                        "FOO" to 777.0
                    )
                )
            )
        )
    }
}
