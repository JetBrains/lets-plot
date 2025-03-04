/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.svg.dom

import org.jetbrains.letsPlot.commons.values.Color

interface SvgColor

enum class SvgColors(private val literal: String) : SvgColor {
    ALICE_BLUE("aliceblue"),
    ANTIQUE_WHITE("antiquewhite"),
    AQUA("aqua"),
    AQUAMARINE("aquamarine"),
    AZURE("azure"),
    BEIGE("beige"),
    BISQUE("bisque"),
    BLACK("black"),
    BLANCHED_ALMOND("blanchedalmond"),
    BLUE("blue"),
    BLUE_VIOLET("blueviolet"),
    BROWN("brown"),
    BURLY_WOOD("burlywood"),
    CADET_BLUE("cadetblue"),
    CHARTREUSE("chartreuse"),
    CHOCOLATE("chocolate"),
    CORAL("coral"),
    CORNFLOWER_BLUE("cornflowerblue"),
    CORNSILK("cornsilk"),
    CRIMSON("crimson"),
    CYAN("cyan"),
    DARK_BLUE("darkblue"),
    DARK_CYAN("darkcyan"),
    DARK_GOLDEN_ROD("darkgoldenrod"),
    DARK_GRAY("darkgray"),
    DARK_GREEN("darkgreen"),
    DARK_GREY("darkgrey"),
    DARK_KHAKI("darkkhaki"),
    DARK_MAGENTA("darkmagenta"),
    DARK_OLIVE_GREEN("darkolivegreen"),
    DARK_ORANGE("darkorange"),
    DARK_ORCHID("darkorchid"),
    DARK_RED("darkred"),
    DARK_SALMON("darksalmon"),
    DARK_SEA_GREEN("darkseagreen"),
    DARK_SLATE_BLUE("darkslateblue"),
    DARK_SLATE_GRAY("darkslategray"),
    DARK_SLATE_GREY("darkslategrey"),
    DARK_TURQUOISE("darkturquoise"),
    DARK_VIOLET("darkviolet"),
    DEEP_PINK("deeppink"),
    DEEP_SKY_BLUE("deepskyblue"),
    DIM_GRAY("dimgray"),
    DIM_GREY("dimgrey"),
    DODGER_BLUE("dodgerblue"),
    FIRE_BRICK("firebrick"),
    FLORAL_WHITE("floralwhite"),
    FOREST_GREEN("forestgreen"),
    FUCHSIA("fuchsia"),
    GAINSBORO("gainsboro"),
    GHOST_WHITE("ghostwhite"),
    GOLD("gold"),
    GOLDEN_ROD("goldenrod"),
    GRAY("gray"),
    GREY("grey"),
    GREEN("green"),
    GREEN_YELLOW("greenyellow"),
    HONEY_DEW("honeydew"),
    HOT_PINK("hotpink"),
    INDIAN_RED("indianred"),
    INDIGO("indigo"),
    IVORY("ivory"),
    KHAKI("khaki"),
    LAVENDER("lavender"),
    LAVENDER_BLUSH("lavenderblush"),
    LAWN_GREEN("lawngreen"),
    LEMON_CHIFFON("lemonchiffon"),
    LIGHT_BLUE("lightblue"),
    LIGHT_CORAL("lightcoral"),
    LIGHT_CYAN("lightcyan"),
    LIGHT_GOLDEN_ROD_YELLOW("lightgoldenrodyellow"),
    LIGHT_GRAY("lightgray"),
    LIGHT_GREEN("lightgreen"),
    LIGHT_GREY("lightgrey"),
    LIGHT_PINK("lightpink"),
    LIGHT_SALMON("lightsalmon"),
    LIGHT_SEA_GREEN("lightseagreen"),
    LIGHT_SKY_BLUE("lightskyblue"),
    LIGHT_SLATE_GRAY("lightslategray"),
    LIGHT_SLATE_GREY("lightslategrey"),
    LIGHT_STEEL_BLUE("lightsteelblue"),
    LIGHT_YELLOW("lightyellow"),
    LIME("lime"),
    LIME_GREEN("limegreen"),
    LINEN("linen"),
    MAGENTA("magenta"),
    MAROON("maroon"),
    MEDIUM_AQUA_MARINE("mediumaquamarine"),
    MEDIUM_BLUE("mediumblue"),
    MEDIUM_ORCHID("mediumorchid"),
    MEDIUM_PURPLE("mediumpurple"),
    MEDIUM_SEAGREEN("mediumseagreen"),
    MEDIUM_SLATE_BLUE("mediumslateblue"),
    MEDIUM_SPRING_GREEN("mediumspringgreen"),
    MEDIUM_TURQUOISE("mediumturquoise"),
    MEDIUM_VIOLET_RED("mediumvioletred"),
    MIDNIGHT_BLUE("midnightblue"),
    MINT_CREAM("mintcream"),
    MISTY_ROSE("mistyrose"),
    MOCCASIN("moccasin"),
    NAVAJO_WHITE("navajowhite"),
    NAVY("navy"),
    OLD_LACE("oldlace"),
    OLIVE("olive"),
    OLIVE_DRAB("olivedrab"),
    ORANGE("orange"),
    ORANGE_RED("orangered"),
    ORCHID("orchid"),
    PALE_GOLDEN_ROD("palegoldenrod"),
    PALE_GREEN("palegreen"),
    PALE_TURQUOISE("paleturquoise"),
    PALE_VIOLET_RED("palevioletred"),
    PAPAYA_WHIP("papayawhip"),
    PEACH_PUFF("peachpuff"),
    PERU("peru"),
    PINK("pink"),
    PLUM("plum"),
    POWDER_BLUE("powderblue"),
    PURPLE("purple"),
    RED("red"),
    ROSY_BROWN("rosybrown"),
    ROYAL_BLUE("royalblue"),
    SADDLE_BROWN("saddlebrown"),
    SALMON("salmon"),
    SANDY_BROWN("sandybrown"),
    SEA_GREEN("seagreen"),
    SEASHELL("seashell"),
    SIENNA("sienna"),
    SILVER("silver"),
    SKY_BLUE("skyblue"),
    SLATE_BLUE("slateblue"),
    SLATE_GRAY("slategray"),
    SLATE_GREY("slategrey"),
    SNOW("snow"),
    SPRING_GREEN("springgreen"),
    STEEL_BLUE("steelblue"),
    TAN("tan"),
    TEAL("teal"),
    THISTLE("thistle"),
    TOMATO("tomato"),
    TRANSPARENT("transparent"),
    TURQUOISE("turquoise"),
    VIOLET("violet"),
    WHEAT("wheat"),
    WHITE("white"),
    WHITE_SMOKE("whitesmoke"),
    YELLOW("yellow"),
    YELLOW_GREEN("yellowgreen"),

    NONE("none"),
    CURRENT_COLOR("currentColor");

    override fun toString(): String {
        return literal
    }

    companion object {
        private val svgColorList = createSvgColorList()

        private fun createSvgColorList(): Map<String, SvgColor> {
            val colorList = HashMap<String, SvgColor>()
            values().forEach { colorList[it.toString().lowercase()] = it }
            return colorList
        }

        fun isColorName(colorName: String): Boolean {
            return svgColorList.containsKey(colorName.lowercase())
        }

        fun forName(colorName: String): SvgColor {
            return svgColorList[colorName.lowercase()] ?: throw IllegalArgumentException()
        }

        fun create(r: Int, g: Int, b: Int): SvgColor {
            return SvgColorRgb(r, g, b)
        }

        fun create(color: Color?): SvgColor {
            return if (color == null) {
                NONE
            } else SvgColorRgb(color.red, color.green, color.blue)
        }

        private data class SvgColorRgb(private val myR: Int, private val myG: Int, private val myB: Int) :
            SvgColor {
            override fun toString(): String {
                return "rgb($myR,$myG,$myB)"
            }
        }
    }
}