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
        var foo: InlineOptions? = null
    }

    @Test
    fun simpleInlineOptions() {
        class InlineFoo(holder: Options) : InlineOptions(holder.properties) {
            var fooType: String? by map("FOO_TYPE")
            var foo: Double? by map("FOO")
        }

        val opts = Opts().apply {
            name = "Demo"
            foo = InlineFoo(this).apply {
                fooType = "InlineFoo"
                foo = 42.0
            }
        }

        @Suppress("UNCHECKED_CAST")
        val json = toJson(opts) as Map<String, Any?>

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

        class FooInline(holder: Options) : InlineOptions(holder.properties) {
            var fooType: String? by map("FOO_TYPE")
            var foo: Double? by map("FOO")
            var bar: Bar? by map("FOO.BAR")
        }

        val opts = Opts().apply {
            name = "Demo"
            foo = FooInline(this).apply {
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
            entry("FOO.BAR", mapOf(
                "BAR_TYPE" to "NestedBar",
                "BAR" to 24.0
            ))
        )
    }

    @Test
    fun inlineWithInline() {
        class InlineBar(holder: Options) : InlineOptions(holder.properties) {
            var barType: String? by map("BAR_TYPE")
            var bar: Double? by map("BAR")
        }

        class InlineFoo(holder: Options) : InlineOptions(holder.properties) {
            var fooType: String? by map("FOO_TYPE")
            var foo: Double? by map("FOO")
            var inlineBar: InlineBar? = null
        }

        val opts = Opts().apply {
            name = "Demo"
            foo = InlineFoo(this).apply {
                fooType = "InlineFoo"
                foo = 42.0
                inlineBar = InlineBar(this).apply {
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
}
