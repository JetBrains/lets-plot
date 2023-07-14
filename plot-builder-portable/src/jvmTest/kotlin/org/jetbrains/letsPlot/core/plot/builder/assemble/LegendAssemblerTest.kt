/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.assemble

import org.assertj.core.api.Assertions.assertThat
import kotlin.test.Test

class LegendAssemblerTest {

    @Test
    fun skipWrappingForShortText() {
        assertThat(LegendAssembler.wrap("abc", 7))
            .isEqualTo("abc")
        assertThat(LegendAssembler.wrap("abcdefg", 7))
            .isEqualTo("abcdefg")
        assertThat(LegendAssembler.wrap("ab cd e", 7))
            .isEqualTo("ab cd e")
    }

    @Test
    fun skipWrappingIfTextHaveDividers() {
        assertThat(
            LegendAssembler.wrap(
                "Lorem ipsum dolor sit amet,\n"
                        + "consectetur adipiscing elit.", 7, 7
            )
        )
            .isEqualTo("Lorem ipsum dolor sit amet,\nconsectetur adipiscing elit.")
    }

    @Test
    fun wrapWordsSimple() {
        assertThat(LegendAssembler.wrap("Lorem ipsum dolor sit amet", 10))
            .isEqualTo(
                """
                |Lorem
                |ipsum
                |dolor sit
                |amet
                """.trimMargin()
            )
        assertThat(LegendAssembler.wrap("Lorem ipsum dolor sit amet, consectetur adipiscing elit", 15))
            .isEqualTo(
                """
                |Lorem ipsum
                |dolor sit amet,
                |consectetur
                |adipiscing elit
                """.trimMargin()
            )
    }

    @Test
    fun wrapWithoutSpaces() {
        assertThat(LegendAssembler.wrap("abcdefghijklmnopqrstuvwxyz", 7))
            .isEqualTo(
                """
                |abcdefg
                |hijklmn
                |opqrstu
                |vwxyz
                """.trimMargin()
            )
    }

    @Test
    fun wrapWithLimitOverflow() {
        assertThat(LegendAssembler.wrap("abcdefghijklmnopqrstuvwxyz", 7, 3))
            .isEqualTo(
                """
                |abcdefg
                |hijklmn
                |opqrstu
                |...
                """.trimMargin()
            )
        assertThat(LegendAssembler.wrap("abcde fghijk lmno pqrstuv", 7, 3))
            .isEqualTo(
                """
                |abcde
                |fghijk
                |lmno
                |...
                """.trimMargin()
            )
    }

    @Test
    fun wrapLongWords() {
        assertThat(LegendAssembler.wrap("amet, consectetur adipiscing elit", 11))
            .isEqualTo(
                """
                |amet,
                |consectetur
                |adipiscing
                |elit
                """.trimMargin()
            )

        //If long word stay after space character, what have position index greater than string.length / 3,
        //then long word will be wrap at new line
        assertThat(LegendAssembler.wrap("Lorem ipsum abcdefghijklmnopqrstuvwxyz, abcdefghijklmnopqrstuvwxyz elit.", 20))
            .isEqualTo(
                """
                |Lorem ipsum abcdefgh
                |ijklmnopqrstuvwxyz,
                |abcdefghijklmnopqrst
                |uvwxyz elit.
                """.trimMargin()
            )
        //If long word stay after space character, what have position index lower than string.length / 3,
        //then long word will be cut and wrap at current line
        assertThat(LegendAssembler.wrap("Lorem abcdefghijklmnopqrstuvwxyz, abcdefghijklmnopqrstuvwxyz elit.", 20))
            .isEqualTo(
                """
                |Lorem abcdefghijklmn
                |opqrstuvwxyz, abcdef
                |ghijklmnopqrstuvwxyz
                |elit.
                """.trimMargin()
            )

        assertThat(
            LegendAssembler.wrap(
                "XYABAACYYDBASXXYAUAOKXYABAACYYXYABAACYYDBASXXYAUAOKDBASXXYAUAOK a scale ASSDASDASDASDASDASDASDASDASDAS benzimidazole QWQWQWQWQWEQEQEQWQWQEQEQWQWQEQ",
                30
            )
        )
            .isEqualTo(
                """
                |XYABAACYYDBASXXYAUAOKXYABAACYY
                |XYABAACYYDBASXXYAUAOKDBASXXYAU
                |AOK a scale
                |ASSDASDASDASDASDASDASDASDASDAS
                |benzimidazole
                |QWQWQWQWQWEQEQEQWQWQEQEQWQWQEQ
                """.trimMargin()
            )

        assertThat(
            LegendAssembler.wrap(
                "XYABAACYYDBASXXYAUAOKXYABAACYYXYABAACYYDBASXXYAUAOKDBASXXYAUAOK a scale ASSDASDASDASDASDASDASDASDASDASASDAS benzimidazole QWQWQWQWQWEQEQEQWQWQEQEQWQWQEQQWQWQWQWQWQWQW",
                30
            )
        )
            .isEqualTo(
                """
                |XYABAACYYDBASXXYAUAOKXYABAACYY
                |XYABAACYYDBASXXYAUAOKDBASXXYAU
                |AOK a scale ASSDASDASDASDASDAS
                |DASDASDASDASASDAS
                |benzimidazole QWQWQWQWQWEQEQEQ
                |WQWQEQEQWQWQEQQWQWQWQWQWQWQW
                """.trimMargin()
            )
        assertThat(
            LegendAssembler.wrap(
                "XYABAACYYDBASXXYAUAOKXYABAACYYXYABAACYYDBASXXYAUAOKDBASXXYAUAOK a scale ASSDASDASDASDASDASDASDASDASDASASDAS benzimidazole QWQWQWQWQWEQEQEQWQWQEQEQWQWQEQQWQWQWQWQWQWQW",
                31
            )
        )
            .isEqualTo(
                """
                |XYABAACYYDBASXXYAUAOKXYABAACYYX
                |YABAACYYDBASXXYAUAOKDBASXXYAUAO
                |K a scale ASSDASDASDASDASDASDAS
                |DASDASDASASDAS benzimidazole QW
                |QWQWQWQWEQEQEQWQWQEQEQWQWQEQQWQ
                |WQWQWQWQWQW
                """.trimMargin()
            )
        assertThat(
            LegendAssembler.wrap(
                "XYABAACYYDBASXXYAUAOKXYABAACYYXYABAACYYDBASXXYAUAOKDBASXXYAUAOK a scale ASSDASDASDASDASDASDASDASDASDASASDAS benzimidazole QWQWQWQWQWEQEQEQWQWQEQEQWQWQEQQWQWQWQWQWQWQW",
                31
            )
        )
            .isEqualTo(
                """
                |XYABAACYYDBASXXYAUAOKXYABAACYYX
                |YABAACYYDBASXXYAUAOKDBASXXYAUAO
                |K a scale ASSDASDASDASDASDASDAS
                |DASDASDASASDAS benzimidazole QW
                |QWQWQWQWEQEQEQWQWQEQEQWQWQEQQWQ
                |WQWQWQWQWQW
                """.trimMargin()
            )
    }

    @Test
    fun wrapLongWordsWhenLineLengthEqualLimit() {
        assertThat(
            LegendAssembler.wrap(
                "XYABAACYYDBASXXYAUAOKXYABAACYYXYABAACYYDBASXXYAUAOKDBASXXYAUAOK a scale ASSDASDASDASDASDASDASDASDASDASASDA benzimidazole QWQWQWQWQWEQEQEQWQWQEQEQWQWQEQQWQWQWQWQWQWQW",
                30
            )
        )
            .isEqualTo(
                """
                |XYABAACYYDBASXXYAUAOKXYABAACYY
                |XYABAACYYDBASXXYAUAOKDBASXXYAU
                |AOK a scale ASSDASDASDASDASDAS
                |DASDASDASDASASDA benzimidazole
                |QWQWQWQWQWEQEQEQWQWQEQEQWQWQEQ
                |QWQWQWQWQWQWQW
                """.trimMargin()
            )
    }

    @Test
    fun wrapRealText() {
        assertThat(
            LegendAssembler.wrap(
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, " +
                        "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, " +
                        "quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute " +
                        "irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. " +
                        "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia " +
                        "deserunt mollit anim id est laborum.", 30, 5
            )
        )
            .isEqualTo(
                """
                |Lorem ipsum dolor sit amet,
                |consectetur adipiscing elit,
                |sed do eiusmod tempor
                |incididunt ut labore et dolore
                |magna aliqua. Ut enim ad minim
                |...
                """.trimMargin()
            )
        assertThat(
            LegendAssembler.wrap(
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, " +
                        "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, " +
                        "quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute " +
                        "irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. " +
                        "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia " +
                        "deserunt mollit anim id est laborum.", 50, 5
            )
        )
            .isEqualTo(
                """
                |Lorem ipsum dolor sit amet, consectetur adipiscing
                |elit, sed do eiusmod tempor incididunt ut labore
                |et dolore magna aliqua. Ut enim ad minim veniam,
                |quis nostrud exercitation ullamco laboris nisi ut
                |aliquip ex ea commodo consequat. Duis aute irure
                |...
                """.trimMargin()
            )
        assertThat(
            LegendAssembler.wrap(
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, " +
                        "https://github.com/JetBrains/lets-plot/issues/315, quis nostrud exercitation " +
                        "ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute " +
                        "irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. " +
                        "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia " +
                        "deserunt mollit anim id est laborum.", 35, 5
            )
        )
            .isEqualTo(
                """
                |Lorem ipsum dolor sit amet,
                |consectetur adipiscing elit, https:
                |//github.com/JetBrains/lets-plot/is
                |sues/315, quis nostrud exercitation
                |ullamco laboris nisi ut aliquip ex
                |...
                """.trimMargin()
            )
    }
}