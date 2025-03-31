/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.raster.shape

import org.jetbrains.letsPlot.commons.values.Color

/*

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.skia.Color4f


internal val Color.asSkiaColor
    get() = Color4f(
        r = (red / 255.0).toFloat(),
        g = (green / 255.0).toFloat(),
        b = (blue / 255.0).toFloat(),
        a = (alpha / 255.0).toFloat()
    )

 */

internal val namedColors = mapOf(
    "coral" to Color(255, 127, 80),
    "lightsalmon" to Color(255, 160, 122),
    "salmon" to Color(250, 128, 114),
    "darksalmon" to Color(233, 150, 122),
    "lightcoral" to Color(240, 128, 128),
    "indianred" to Color(205, 92, 92),
    "crimson" to Color(220, 20, 60),
    "firebrick" to Color(178, 34, 34),
    "red" to Color(255, 0, 0),
    "darkred" to Color(139, 0, 0),
    "coral" to Color(255, 127, 80),
    "tomato" to Color(255, 99, 71),
    "orangered" to Color(255, 69, 0),
    "gold" to Color(255, 215, 0),
    "orange" to Color(255, 165, 0),
    "darkorange" to Color(255, 140, 0),

    "lightyellow" to Color(255, 255, 224),
    "lemonchiffon" to Color(255, 250, 205),
    "lightgoldenrodyellow" to Color(250, 250, 210),
    "papayawhip" to Color(255, 239, 213),
    "moccasin" to Color(255, 228, 181),
    "peachpuff" to Color(255, 218, 185),
    "palegoldenrod" to Color(238, 232, 170),
    "khaki" to Color(240, 230, 140),
    "darkkhaki" to Color(189, 183, 107),
    "yellow" to Color(255, 255, 0),


    "lawngreen" to Color(124, 252, 0),
    "chartreuse" to Color(127, 255, 0),
    "limegreen" to Color(50, 205, 50),
    "lime" to Color(0, 255, 0),
    "forestgreen" to Color(34, 139, 34),
    "green" to Color(0, 128, 0),
    "darkgreen" to Color(0, 100, 0),
    "greenyellow" to Color(173, 255, 47),
    "yellowgreen" to Color(154, 205, 50),
    "springgreen" to Color(0, 255, 127),
    "mediumspringgreen" to Color(0, 250, 154),
    "lightgreen" to Color(144, 238, 144),
    "palegreen" to Color(152, 251, 152),
    "darkseagreen" to Color(143, 188, 143),
    "mediumseagreen" to Color(60, 179, 113),
    "seagreen" to Color(46, 139, 87),
    "olive" to Color(128, 128, 0),
    "darkolivegreen" to Color(85, 107, 47),
    "olivedrab" to Color(107, 142, 35),


    "lightcyan" to Color(224, 255, 255),
    "cyan" to Color(0, 255, 255),
    "aqua" to Color(0, 255, 255),
    "aquamarine" to Color(127, 255, 212),
    "mediumaquamarine" to Color(102, 205, 170),
    "paleturquoise" to Color(175, 238, 238),
    "turquoise" to Color(64, 224, 208),
    "mediumturquoise" to Color(72, 209, 204),
    "darkturquoise" to Color(0, 206, 209),
    "lightseagreen" to Color(32, 178, 170),
    "cadetblue" to Color(95, 158, 160),
    "darkcyan" to Color(0, 139, 139),
    "teal" to Color(0, 128, 128),

    "powderblue" to Color(176, 224, 230),
    "lightblue" to Color(173, 216, 230),
    "lightskyblue" to Color(135, 206, 250),
    "skyblue" to Color(135, 206, 235),
    "deepskyblue" to Color(0, 191, 255),
    "lightsteelblue" to Color(176, 196, 222),
    "dodgerblue" to Color(30, 144, 255),
    "cornflowerblue" to Color(100, 149, 237),
    "steelblue" to Color(70, 130, 180),
    "royalblue" to Color(65, 105, 225),
    "blue" to Color(0, 0, 255),
    "mediumblue" to Color(0, 0, 205),
    "darkblue" to Color(0, 0, 139),
    "navy" to Color(0, 0, 128),
    "midnightblue" to Color(25, 25, 112),
    "mediumslateblue" to Color(123, 104, 238),
    "slateblue" to Color(106, 90, 205),
    "darkslateblue" to Color(72, 61, 139),

    "lavender" to Color(230, 230, 250),
    "thistle" to Color(216, 191, 216),
    "plum" to Color(221, 160, 221),
    "violet" to Color(238, 130, 238),
    "orchid" to Color(218, 112, 214),
    "fuchsia" to Color(255, 0, 255),
    "magenta" to Color(255, 0, 255),
    "mediumorchid" to Color(186, 85, 211),
    "mediumpurple" to Color(147, 112, 219),
    "blueviolet" to Color(138, 43, 226),
    "darkviolet" to Color(148, 0, 211),
    "darkorchid" to Color(153, 50, 204),
    "darkmagenta" to Color(139, 0, 139),
    "purple" to Color(128, 0, 128),
    "indigo" to Color(75, 0, 130),

    "pink" to Color(255, 192, 203),
    "lightpink" to Color(255, 182, 193),
    "hotpink" to Color(255, 105, 180),
    "deeppink" to Color(255, 20, 147),
    "palevioletred" to Color(219, 112, 147),
    "mediumvioletred" to Color(199, 21, 133),


    "white" to Color(255, 255, 255),
    "snow" to Color(255, 250, 250),
    "honeydew" to Color(240, 255, 240),
    "mintcream" to Color(245, 255, 250),
    "azure" to Color(240, 255, 255),
    "aliceblue" to Color(240, 248, 255),
    "ghostwhite" to Color(248, 248, 255),
    "whitesmoke" to Color(245, 245, 245),
    "seashell" to Color(255, 245, 238),
    "beige" to Color(245, 245, 220),
    "oldlace" to Color(253, 245, 230),
    "floralwhite" to Color(255, 250, 240),
    "ivory" to Color(255, 255, 240),
    "antiquewhite" to Color(250, 235, 215),
    "linen" to Color(250, 240, 230),
    "lavenderblush" to Color(255, 240, 245),
    "mistyrose" to Color(255, 228, 225),


    "gainsboro" to Color(220, 220, 220),
    "lightgray" to Color(211, 211, 211),
    "silver" to Color(192, 192, 192),
    "darkgray" to Color(169, 169, 169),
    "gray" to Color(128, 128, 128),
    "dimgray" to Color(105, 105, 105),
    "lightslategray" to Color(119, 136, 153),
    "slategray" to Color(112, 128, 144),
    "darkslategray" to Color(47, 79, 79),
    "black" to Color(0, 0, 0),


    "cornsilk" to Color(255, 248, 220),
    "blanchedalmond" to Color(255, 235, 205),
    "bisque" to Color(255, 228, 196),
    "navajowhite" to Color(255, 222, 173),
    "wheat" to Color(245, 222, 179),
    "burlywood" to Color(222, 184, 135),
    "tan" to Color(210, 180, 140),
    "rosybrown" to Color(188, 143, 143),
    "sandybrown" to Color(244, 164, 96),
    "goldenrod" to Color(218, 165, 32),
    "peru" to Color(205, 133, 63),
    "chocolate" to Color(210, 105, 30),
    "saddlebrown" to Color(139, 69, 19),
    "sienna" to Color(160, 82, 45),
    "brown" to Color(165, 42, 42),
    "maroon" to Color(128, 0, 0),
)


