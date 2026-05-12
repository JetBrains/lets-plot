/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.visualtesting.plot

import org.jetbrains.letsPlot.commons.intern.json.JsonSupport.parseJson
import org.jetbrains.letsPlot.commons.values.Bitmap
import org.jetbrains.letsPlot.core.canvas.CanvasPeer
import org.jetbrains.letsPlot.visualtesting.ImageComparer

class PlotThemeTest(
    override val canvasPeer: CanvasPeer,
    override val imageComparer: ImageComparer,
) : PlotTestSuitBase() {
    init {
        registerTest(::plot_theme_bottomLegendWithMultilineText)
        registerTest(::plot_theme_bottomLegendContinuousScale)
        registerTest(::plot_theme_titleWithBlankLine)
        //registerTest(::plot_theme_alphaColorInTitles)
    }

    fun plot_theme_bottomLegendWithMultilineText(): Bitmap {
        val spec = """
            |{
            |  "data": {
            |    "Unnamed: 0": [
            |      1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0, 17.0, 18.0, 19.0, 20.0, 21.0, 22.0, 23.0, 24.0, 25.0, 26.0, 27.0, 28.0, 29.0, 30.0, 31.0, 32.0, 33.0, 34.0, 35.0, 36.0, 37.0, 38.0, 39.0, 40.0, 41.0, 42.0, 43.0, 44.0, 45.0, 46.0, 47.0, 48.0, 49.0, 50.0, 51.0, 52.0, 53.0, 54.0, 55.0, 56.0, 57.0, 58.0, 59.0, 60.0, 61.0, 62.0, 63.0, 64.0, 65.0, 66.0, 67.0, 68.0, 69.0, 70.0, 71.0, 72.0, 73.0, 74.0, 75.0, 76.0, 77.0, 78.0, 79.0, 80.0, 81.0, 82.0, 83.0, 84.0, 85.0, 86.0, 87.0, 88.0, 89.0, 90.0, 91.0, 92.0, 93.0, 94.0, 95.0, 96.0, 97.0, 98.0, 99.0, 100.0, 101.0, 102.0, 103.0, 104.0, 105.0, 106.0, 107.0, 108.0, 109.0, 110.0, 111.0, 112.0, 113.0, 114.0, 115.0, 116.0, 117.0, 118.0, 119.0, 120.0, 121.0, 122.0, 123.0, 124.0, 125.0, 126.0, 127.0, 128.0, 129.0, 130.0, 131.0, 132.0, 133.0, 134.0, 135.0, 136.0, 137.0, 138.0, 139.0, 140.0, 141.0, 142.0, 143.0, 144.0, 145.0, 146.0, 147.0, 148.0, 149.0, 150.0, 151.0, 152.0, 153.0, 154.0, 155.0, 156.0, 157.0, 158.0, 159.0, 160.0, 161.0, 162.0, 163.0, 164.0, 165.0, 166.0, 167.0, 168.0, 169.0, 170.0, 171.0, 172.0, 173.0, 174.0, 175.0, 176.0, 177.0, 178.0, 179.0, 180.0, 181.0, 182.0, 183.0, 184.0, 185.0, 186.0, 187.0, 188.0, 189.0, 190.0, 191.0, 192.0, 193.0, 194.0, 195.0, 196.0, 197.0, 198.0, 199.0, 200.0, 201.0, 202.0, 203.0, 204.0, 205.0, 206.0, 207.0, 208.0, 209.0, 210.0, 211.0, 212.0, 213.0, 214.0, 215.0, 216.0, 217.0, 218.0, 219.0, 220.0, 221.0, 222.0, 223.0, 224.0, 225.0, 226.0, 227.0, 228.0, 229.0, 230.0, 231.0, 232.0, 233.0, 234.0
            |    ],
            |    "manufacturer": [
            |      "audi", "audi", "audi", "audi", "audi", "audi", "audi", "audi", "audi", "audi", "audi", "audi", "audi", "audi", "audi", "audi", "audi", "audi", "chevrolet", "chevrolet", "chevrolet", "chevrolet", "chevrolet", "chevrolet", "chevrolet", "chevrolet", "chevrolet", "chevrolet", "chevrolet", "chevrolet", "chevrolet", "chevrolet", "chevrolet", "chevrolet", "chevrolet", "chevrolet", "chevrolet", "dodge", "dodge", "dodge", "dodge", "dodge", "dodge", "dodge", "dodge", "dodge", "dodge", "dodge", "dodge", "dodge", "dodge", "dodge", "dodge", "dodge", "dodge", "dodge", "dodge", "dodge", "dodge", "dodge", "dodge", "dodge", "dodge", "dodge", "dodge", "dodge", "dodge", "dodge", "dodge", "dodge", "dodge", "dodge", "dodge", "dodge", "ford", "ford", "ford", "ford", "ford", "ford", "ford", "ford", "ford", "ford", "ford", "ford", "ford", "ford", "ford", "ford", "ford", "ford", "ford", "ford", "ford", "ford", "ford", "ford", "ford", "honda", "honda", "honda", "honda", "honda", "honda", "honda", "honda", "honda", "hyundai", "hyundai", "hyundai", "hyundai", "hyundai", "hyundai", "hyundai", "hyundai", "hyundai", "hyundai", "hyundai", "hyundai", "hyundai", "hyundai", "jeep", "jeep", "jeep", "jeep", "jeep", "jeep", "jeep", "jeep", "land rover", "land rover", "land rover", "land rover", "lincoln", "lincoln", "lincoln", "mercury", "mercury", "mercury", "mercury", "nissan", "nissan", "nissan", "nissan", "nissan", "nissan", "nissan", "nissan", "nissan", "nissan", "nissan", "nissan", "nissan", "pontiac", "pontiac", "pontiac", "pontiac", "pontiac", "subaru", "subaru", "subaru", "subaru", "subaru", "subaru", "subaru", "subaru", "subaru", "subaru", "subaru", "subaru", "subaru", "subaru", "toyota", "toyota", "toyota", "toyota", "toyota", "toyota", "toyota", "toyota", "toyota", "toyota", "toyota", "toyota", "toyota", "toyota", "toyota", "toyota", "toyota", "toyota", "toyota", "toyota", "toyota", "toyota", "toyota", "toyota", "toyota", "toyota", "toyota", "toyota", "toyota", "toyota", "toyota", "toyota", "toyota", "toyota", "volkswagen", "volkswagen", "volkswagen", "volkswagen", "volkswagen", "volkswagen", "volkswagen", "volkswagen", "volkswagen", "volkswagen", "volkswagen", "volkswagen", "volkswagen", "volkswagen", "volkswagen", "volkswagen", "volkswagen", "volkswagen", "volkswagen", "volkswagen", "volkswagen", "volkswagen", "volkswagen", "volkswagen", "volkswagen", "volkswagen", "volkswagen"
            |    ],
            |    "model": [
            |      "a4", "a4", "a4", "a4", "a4", "a4", "a4", "a4 quattro", "a4 quattro", "a4 quattro", "a4 quattro", "a4 quattro", "a4 quattro", "a4 quattro", "a4 quattro", "a6 quattro", "a6 quattro", "a6 quattro", "c1500 suburban 2wd", "c1500 suburban 2wd", "c1500 suburban 2wd", "c1500 suburban 2wd", "c1500 suburban 2wd", "corvette", "corvette", "corvette", "corvette", "corvette", "k1500 tahoe 4wd", "k1500 tahoe 4wd", "k1500 tahoe 4wd", "k1500 tahoe 4wd", "malibu", "malibu", "malibu", "malibu", "malibu", "caravan 2wd", "caravan 2wd", "caravan 2wd", "caravan 2wd", "caravan 2wd", "caravan 2wd", "caravan 2wd", "caravan 2wd", "caravan 2wd", "caravan 2wd", "caravan 2wd", "dakota pickup 4wd", "dakota pickup 4wd", "dakota pickup 4wd", "dakota pickup 4wd", "dakota pickup 4wd", "dakota pickup 4wd", "dakota pickup 4wd", "dakota pickup 4wd", "dakota pickup 4wd", "durango 4wd", "durango 4wd", "durango 4wd", "durango 4wd", "durango 4wd", "durango 4wd", "durango 4wd", "ram 1500 pickup 4wd", "ram 1500 pickup 4wd", "ram 1500 pickup 4wd", "ram 1500 pickup 4wd", "ram 1500 pickup 4wd", "ram 1500 pickup 4wd", "ram 1500 pickup 4wd", "ram 1500 pickup 4wd", "ram 1500 pickup 4wd", "ram 1500 pickup 4wd", "expedition 2wd", "expedition 2wd", "expedition 2wd", "explorer 4wd", "explorer 4wd", "explorer 4wd", "explorer 4wd", "explorer 4wd", "explorer 4wd", "f150 pickup 4wd", "f150 pickup 4wd", "f150 pickup 4wd", "f150 pickup 4wd", "f150 pickup 4wd", "f150 pickup 4wd", "f150 pickup 4wd", "mustang", "mustang", "mustang", "mustang", "mustang", "mustang", "mustang", "mustang", "mustang", "civic", "civic", "civic", "civic", "civic", "civic", "civic", "civic", "civic", "sonata", "sonata", "sonata", "sonata", "sonata", "sonata", "sonata", "tiburon", "tiburon", "tiburon", "tiburon", "tiburon", "tiburon", "tiburon", "grand cherokee 4wd", "grand cherokee 4wd", "grand cherokee 4wd", "grand cherokee 4wd", "grand cherokee 4wd", "grand cherokee 4wd", "grand cherokee 4wd", "grand cherokee 4wd", "range rover", "range rover", "range rover", "range rover", "navigator 2wd", "navigator 2wd", "navigator 2wd", "mountaineer 4wd", "mountaineer 4wd", "mountaineer 4wd", "mountaineer 4wd", "altima", "altima", "altima", "altima", "altima", "altima", "maxima", "maxima", "maxima", "pathfinder 4wd", "pathfinder 4wd", "pathfinder 4wd", "pathfinder 4wd", "grand prix", "grand prix", "grand prix", "grand prix", "grand prix", "forester awd", "forester awd", "forester awd", "forester awd", "forester awd", "forester awd", "impreza awd", "impreza awd", "impreza awd", "impreza awd", "impreza awd", "impreza awd", "impreza awd", "impreza awd", "4runner 4wd", "4runner 4wd", "4runner 4wd", "4runner 4wd", "4runner 4wd", "4runner 4wd", "camry", "camry", "camry", "camry", "camry", "camry", "camry", "camry solara", "camry solara", "camry solara", "camry solara", "camry solara", "camry solara", "camry solara", "corolla", "corolla", "corolla", "corolla", "corolla", "land cruiser wagon 4wd", "land cruiser wagon 4wd", "toyota tacoma 4wd", "toyota tacoma 4wd", "toyota tacoma 4wd", "toyota tacoma 4wd", "toyota tacoma 4wd", "toyota tacoma 4wd", "toyota tacoma 4wd", "gti", "gti", "gti", "gti", "gti", "jetta", "jetta", "jetta", "jetta", "jetta", "jetta", "jetta", "jetta", "jetta", "new beetle", "new beetle", "new beetle", "new beetle", "new beetle", "new beetle", "passat", "passat", "passat", "passat", "passat", "passat", "passat"
            |    ],
            |    "displ": [
            |      1.8, 1.8, 2.0, 2.0, 2.8, 2.8, 3.1, 1.8, 1.8, 2.0, 2.0, 2.8, 2.8, 3.1, 3.1, 2.8, 3.1, 4.2, 5.3, 5.3, 5.3, 5.7, 6.0, 5.7, 5.7, 6.2, 6.2, 7.0, 5.3, 5.3, 5.7, 6.5, 2.4, 2.4, 3.1, 3.5, 3.6, 2.4, 3.0, 3.3, 3.3, 3.3, 3.3, 3.3, 3.8, 3.8, 3.8, 4.0, 3.7, 3.7, 3.9, 3.9, 4.7, 4.7, 4.7, 5.2, 5.2, 3.9, 4.7, 4.7, 4.7, 5.2, 5.7, 5.9, 4.7, 4.7, 4.7, 4.7, 4.7, 4.7, 5.2, 5.2, 5.7, 5.9, 4.6, 5.4, 5.4, 4.0, 4.0, 4.0, 4.0, 4.6, 5.0, 4.2, 4.2, 4.6, 4.6, 4.6, 5.4, 5.4, 3.8, 3.8, 4.0, 4.0, 4.6, 4.6, 4.6, 4.6, 5.4, 1.6, 1.6, 1.6, 1.6, 1.6, 1.8, 1.8, 1.8, 2.0, 2.4, 2.4, 2.4, 2.4, 2.5, 2.5, 3.3, 2.0, 2.0, 2.0, 2.0, 2.7, 2.7, 2.7, 3.0, 3.7, 4.0, 4.7, 4.7, 4.7, 5.7, 6.1, 4.0, 4.2, 4.4, 4.6, 5.4, 5.4, 5.4, 4.0, 4.0, 4.6, 5.0, 2.4, 2.4, 2.5, 2.5, 3.5, 3.5, 3.0, 3.0, 3.5, 3.3, 3.3, 4.0, 5.6, 3.1, 3.8, 3.8, 3.8, 5.3, 2.5, 2.5, 2.5, 2.5, 2.5, 2.5, 2.2, 2.2, 2.5, 2.5, 2.5, 2.5, 2.5, 2.5, 2.7, 2.7, 3.4, 3.4, 4.0, 4.7, 2.2, 2.2, 2.4, 2.4, 3.0, 3.0, 3.5, 2.2, 2.2, 2.4, 2.4, 3.0, 3.0, 3.3, 1.8, 1.8, 1.8, 1.8, 1.8, 4.7, 5.7, 2.7, 2.7, 2.7, 3.4, 3.4, 4.0, 4.0, 2.0, 2.0, 2.0, 2.0, 2.8, 1.9, 2.0, 2.0, 2.0, 2.0, 2.5, 2.5, 2.8, 2.8, 1.9, 1.9, 2.0, 2.0, 2.5, 2.5, 1.8, 1.8, 2.0, 2.0, 2.8, 2.8, 3.6
            |    ],
            |    "year": [
            |      1999.0, 1999.0, 2008.0, 2008.0, 1999.0, 1999.0, 2008.0, 1999.0, 1999.0, 2008.0, 2008.0, 1999.0, 1999.0, 2008.0, 2008.0, 1999.0, 2008.0, 2008.0, 2008.0, 2008.0, 2008.0, 1999.0, 2008.0, 1999.0, 1999.0, 2008.0, 2008.0, 2008.0, 2008.0, 2008.0, 1999.0, 1999.0, 1999.0, 2008.0, 1999.0, 2008.0, 2008.0, 1999.0, 1999.0, 1999.0, 1999.0, 2008.0, 2008.0, 2008.0, 1999.0, 1999.0, 2008.0, 2008.0, 2008.0, 2008.0, 1999.0, 1999.0, 2008.0, 2008.0, 2008.0, 1999.0, 1999.0, 1999.0, 2008.0, 2008.0, 2008.0, 1999.0, 2008.0, 1999.0, 2008.0, 2008.0, 2008.0, 2008.0, 2008.0, 2008.0, 1999.0, 1999.0, 2008.0, 1999.0, 1999.0, 1999.0, 2008.0, 1999.0, 1999.0, 1999.0, 2008.0, 2008.0, 1999.0, 1999.0, 1999.0, 1999.0, 1999.0, 2008.0, 1999.0, 2008.0, 1999.0, 1999.0, 2008.0, 2008.0, 1999.0, 1999.0, 2008.0, 2008.0, 2008.0, 1999.0, 1999.0, 1999.0, 1999.0, 1999.0, 2008.0, 2008.0, 2008.0, 2008.0, 1999.0, 1999.0, 2008.0, 2008.0, 1999.0, 1999.0, 2008.0, 1999.0, 1999.0, 2008.0, 2008.0, 2008.0, 2008.0, 2008.0, 2008.0, 2008.0, 1999.0, 1999.0, 2008.0, 2008.0, 2008.0, 2008.0, 1999.0, 2008.0, 2008.0, 1999.0, 1999.0, 1999.0, 2008.0, 1999.0, 2008.0, 2008.0, 1999.0, 1999.0, 1999.0, 2008.0, 2008.0, 2008.0, 2008.0, 1999.0, 1999.0, 2008.0, 1999.0, 1999.0, 2008.0, 2008.0, 1999.0, 1999.0, 1999.0, 2008.0, 2008.0, 1999.0, 1999.0, 2008.0, 2008.0, 2008.0, 2008.0, 1999.0, 1999.0, 1999.0, 1999.0, 2008.0, 2008.0, 2008.0, 2008.0, 1999.0, 1999.0, 1999.0, 1999.0, 2008.0, 2008.0, 1999.0, 1999.0, 2008.0, 2008.0, 1999.0, 1999.0, 2008.0, 1999.0, 1999.0, 2008.0, 2008.0, 1999.0, 1999.0, 2008.0, 1999.0, 1999.0, 1999.0, 2008.0, 2008.0, 1999.0, 2008.0, 1999.0, 1999.0, 2008.0, 1999.0, 1999.0, 2008.0, 2008.0, 1999.0, 1999.0, 2008.0, 2008.0, 1999.0, 1999.0, 1999.0, 1999.0, 2008.0, 2008.0, 2008.0, 2008.0, 1999.0, 1999.0, 1999.0, 1999.0, 1999.0, 1999.0, 2008.0, 2008.0, 1999.0, 1999.0, 2008.0, 2008.0, 1999.0, 1999.0, 2008.0
            |    ],
            |    "cyl": [
            |      4.0, 4.0, 4.0, 4.0, 6.0, 6.0, 6.0, 4.0, 4.0, 4.0, 4.0, 6.0, 6.0, 6.0, 6.0, 6.0, 6.0, 8.0, 8.0, 8.0, 8.0, 8.0, 8.0, 8.0, 8.0, 8.0, 8.0, 8.0, 8.0, 8.0, 8.0, 8.0, 4.0, 4.0, 6.0, 6.0, 6.0, 4.0, 6.0, 6.0, 6.0, 6.0, 6.0, 6.0, 6.0, 6.0, 6.0, 6.0, 6.0, 6.0, 6.0, 6.0, 8.0, 8.0, 8.0, 8.0, 8.0, 6.0, 8.0, 8.0, 8.0, 8.0, 8.0, 8.0, 8.0, 8.0, 8.0, 8.0, 8.0, 8.0, 8.0, 8.0, 8.0, 8.0, 8.0, 8.0, 8.0, 6.0, 6.0, 6.0, 6.0, 8.0, 8.0, 6.0, 6.0, 8.0, 8.0, 8.0, 8.0, 8.0, 6.0, 6.0, 6.0, 6.0, 8.0, 8.0, 8.0, 8.0, 8.0, 4.0, 4.0, 4.0, 4.0, 4.0, 4.0, 4.0, 4.0, 4.0, 4.0, 4.0, 4.0, 4.0, 6.0, 6.0, 6.0, 4.0, 4.0, 4.0, 4.0, 6.0, 6.0, 6.0, 6.0, 6.0, 6.0, 8.0, 8.0, 8.0, 8.0, 8.0, 8.0, 8.0, 8.0, 8.0, 8.0, 8.0, 8.0, 6.0, 6.0, 8.0, 8.0, 4.0, 4.0, 4.0, 4.0, 6.0, 6.0, 6.0, 6.0, 6.0, 6.0, 6.0, 6.0, 8.0, 6.0, 6.0, 6.0, 6.0, 8.0, 4.0, 4.0, 4.0, 4.0, 4.0, 4.0, 4.0, 4.0, 4.0, 4.0, 4.0, 4.0, 4.0, 4.0, 4.0, 4.0, 6.0, 6.0, 6.0, 8.0, 4.0, 4.0, 4.0, 4.0, 6.0, 6.0, 6.0, 4.0, 4.0, 4.0, 4.0, 6.0, 6.0, 6.0, 4.0, 4.0, 4.0, 4.0, 4.0, 8.0, 8.0, 4.0, 4.0, 4.0, 6.0, 6.0, 6.0, 6.0, 4.0, 4.0, 4.0, 4.0, 6.0, 4.0, 4.0, 4.0, 4.0, 4.0, 5.0, 5.0, 6.0, 6.0, 4.0, 4.0, 4.0, 4.0, 5.0, 5.0, 4.0, 4.0, 4.0, 4.0, 6.0, 6.0, 6.0
            |    ],
            |    "trans": [
            |      "auto(l5)", "manual(m5)", "manual(m6)", "auto(av)", "auto(l5)", "manual(m5)", "auto(av)", "manual(m5)", "auto(l5)", "manual(m6)", "auto(s6)", "auto(l5)", "manual(m5)", "auto(s6)", "manual(m6)", "auto(l5)", "auto(s6)", "auto(s6)", "auto(l4)", "auto(l4)", "auto(l4)", "auto(l4)", "auto(l4)", "manual(m6)", "auto(l4)", "manual(m6)", "auto(s6)", "manual(m6)", "auto(l4)", "auto(l4)", "auto(l4)", "auto(l4)", "auto(l4)", "auto(l4)", "auto(l4)", "auto(l4)", "auto(s6)", "auto(l3)", "auto(l4)", "auto(l4)", "auto(l4)", "auto(l4)", "auto(l4)", "auto(l4)", "auto(l4)", "auto(l4)", "auto(l6)", "auto(l6)", "manual(m6)", "auto(l4)", "auto(l4)", "manual(m5)", "auto(l5)", "auto(l5)", "auto(l5)", "manual(m5)", "auto(l4)", "auto(l4)", "auto(l5)", "auto(l5)", "auto(l5)", "auto(l4)", "auto(l5)", "auto(l4)", "manual(m6)", "auto(l5)", "auto(l5)", "auto(l5)", "manual(m6)", "manual(m6)", "auto(l4)", "manual(m5)", "auto(l5)", "auto(l4)", "auto(l4)", "auto(l4)", "auto(l6)", "auto(l5)", "manual(m5)", "auto(l5)", "auto(l5)", "auto(l6)", "auto(l4)", "auto(l4)", "manual(m5)", "manual(m5)", "auto(l4)", "auto(l4)", "auto(l4)", "auto(l4)", "manual(m5)", "auto(l4)", "manual(m5)", "auto(l5)", "auto(l4)", "manual(m5)", "manual(m5)", "auto(l5)", "manual(m6)", "manual(m5)", "auto(l4)", "manual(m5)", "manual(m5)", "auto(l4)", "manual(m5)", "auto(l5)", "auto(l5)", "manual(m6)", "auto(l4)", "manual(m5)", "auto(l4)", "manual(m5)", "auto(l4)", "manual(m5)", "auto(l5)", "auto(l4)", "manual(m5)", "manual(m5)", "auto(l4)", "auto(l4)", "manual(m6)", "manual(m5)", "auto(l5)", "auto(l5)", "auto(l4)", "auto(l4)", "auto(l5)", "auto(l5)", "auto(l5)", "auto(l5)", "auto(l4)", "auto(s6)", "auto(s6)", "auto(l4)", "auto(l4)", "auto(l4)", "auto(l6)", "auto(l5)", "auto(l5)", "auto(l6)", "auto(l4)", "manual(m5)", "auto(l4)", "auto(av)", "manual(m6)", "manual(m6)", "auto(av)", "auto(l4)", "manual(m5)", "auto(av)", "auto(l4)", "manual(m5)", "auto(l5)", "auto(s5)", "auto(l4)", "auto(l4)", "auto(l4)", "auto(l4)", "auto(s4)", "manual(m5)", "auto(l4)", "manual(m5)", "manual(m5)", "auto(l4)", "auto(l4)", "auto(l4)", "manual(m5)", "manual(m5)", "auto(l4)", "auto(s4)", "auto(s4)", "manual(m5)", "manual(m5)", "manual(m5)", "auto(l4)", "auto(l4)", "manual(m5)", "auto(l5)", "auto(l5)", "manual(m5)", "auto(l4)", "manual(m5)", "auto(l5)", "auto(l4)", "manual(m5)", "auto(s6)", "auto(l4)", "manual(m5)", "manual(m5)", "auto(s5)", "auto(l4)", "manual(m5)", "auto(s5)", "auto(l3)", "auto(l4)", "manual(m5)", "manual(m5)", "auto(l4)", "auto(l4)", "auto(s6)", "manual(m5)", "auto(l4)", "manual(m5)", "manual(m5)", "auto(l4)", "manual(m6)", "auto(l5)", "manual(m5)", "auto(l4)", "manual(m6)", "auto(s6)", "manual(m5)", "manual(m5)", "manual(m5)", "auto(l4)", "auto(s6)", "manual(m6)", "auto(s6)", "manual(m5)", "auto(l4)", "manual(m5)", "manual(m5)", "auto(l4)", "manual(m5)", "auto(l4)", "manual(m5)", "auto(s6)", "manual(m5)", "auto(l5)", "auto(s6)", "manual(m6)", "auto(l5)", "manual(m5)", "auto(s6)"
            |    ],
            |    "drv": [
            |      "f", "f", "f", "f", "f", "f", "f", "4", "4", "4", "4", "4", "4", "4", "4", "4", "4", "4", "r", "r", "r", "r", "r", "r", "r", "r", "r", "r", "4", "4", "4", "4", "f", "f", "f", "f", "f", "f", "f", "f", "f", "f", "f", "f", "f", "f", "f", "f", "4", "4", "4", "4", "4", "4", "4", "4", "4", "4", "4", "4", "4", "4", "4", "4", "4", "4", "4", "4", "4", "4", "4", "4", "4", "4", "r", "r", "r", "4", "4", "4", "4", "4", "4", "4", "4", "4", "4", "4", "4", "4", "r", "r", "r", "r", "r", "r", "r", "r", "r", "f", "f", "f", "f", "f", "f", "f", "f", "f", "f", "f", "f", "f", "f", "f", "f", "f", "f", "f", "f", "f", "f", "f", "4", "4", "4", "4", "4", "4", "4", "4", "4", "4", "4", "4", "r", "r", "r", "4", "4", "4", "4", "f", "f", "f", "f", "f", "f", "f", "f", "f", "4", "4", "4", "4", "f", "f", "f", "f", "f", "4", "4", "4", "4", "4", "4", "4", "4", "4", "4", "4", "4", "4", "4", "4", "4", "4", "4", "4", "4", "f", "f", "f", "f", "f", "f", "f", "f", "f", "f", "f", "f", "f", "f", "f", "f", "f", "f", "f", "4", "4", "4", "4", "4", "4", "4", "4", "4", "f", "f", "f", "f", "f", "f", "f", "f", "f", "f", "f", "f", "f", "f", "f", "f", "f", "f", "f", "f", "f", "f", "f", "f", "f", "f", "f"
            |    ],
            |    "cty": [
            |      18.0, 21.0, 20.0, 21.0, 16.0, 18.0, 18.0, 18.0, 16.0, 20.0, 19.0, 15.0, 17.0, 17.0, 15.0, 15.0, 17.0, 16.0, 14.0, 11.0, 14.0, 13.0, 12.0, 16.0, 15.0, 16.0, 15.0, 15.0, 14.0, 11.0, 11.0, 14.0, 19.0, 22.0, 18.0, 18.0, 17.0, 18.0, 17.0, 16.0, 16.0, 17.0, 17.0, 11.0, 15.0, 15.0, 16.0, 16.0, 15.0, 14.0, 13.0, 14.0, 14.0, 14.0, 9.0, 11.0, 11.0, 13.0, 13.0, 9.0, 13.0, 11.0, 13.0, 11.0, 12.0, 9.0, 13.0, 13.0, 12.0, 9.0, 11.0, 11.0, 13.0, 11.0, 11.0, 11.0, 12.0, 14.0, 15.0, 14.0, 13.0, 13.0, 13.0, 14.0, 14.0, 13.0, 13.0, 13.0, 11.0, 13.0, 18.0, 18.0, 17.0, 16.0, 15.0, 15.0, 15.0, 15.0, 14.0, 28.0, 24.0, 25.0, 23.0, 24.0, 26.0, 25.0, 24.0, 21.0, 18.0, 18.0, 21.0, 21.0, 18.0, 18.0, 19.0, 19.0, 19.0, 20.0, 20.0, 17.0, 16.0, 17.0, 17.0, 15.0, 15.0, 14.0, 9.0, 14.0, 13.0, 11.0, 11.0, 12.0, 12.0, 11.0, 11.0, 11.0, 12.0, 14.0, 13.0, 13.0, 13.0, 21.0, 19.0, 23.0, 23.0, 19.0, 19.0, 18.0, 19.0, 19.0, 14.0, 15.0, 14.0, 12.0, 18.0, 16.0, 17.0, 18.0, 16.0, 18.0, 18.0, 20.0, 19.0, 20.0, 18.0, 21.0, 19.0, 19.0, 19.0, 20.0, 20.0, 19.0, 20.0, 15.0, 16.0, 15.0, 15.0, 16.0, 14.0, 21.0, 21.0, 21.0, 21.0, 18.0, 18.0, 19.0, 21.0, 21.0, 21.0, 22.0, 18.0, 18.0, 18.0, 24.0, 24.0, 26.0, 28.0, 26.0, 11.0, 13.0, 15.0, 16.0, 17.0, 15.0, 15.0, 15.0, 16.0, 21.0, 19.0, 21.0, 22.0, 17.0, 33.0, 21.0, 19.0, 22.0, 21.0, 21.0, 21.0, 16.0, 17.0, 35.0, 29.0, 21.0, 19.0, 20.0, 20.0, 21.0, 18.0, 19.0, 21.0, 16.0, 18.0, 17.0
            |    ],
            |    "hwy": [
            |      29.0, 29.0, 31.0, 30.0, 26.0, 26.0, 27.0, 26.0, 25.0, 28.0, 27.0, 25.0, 25.0, 25.0, 25.0, 24.0, 25.0, 23.0, 20.0, 15.0, 20.0, 17.0, 17.0, 26.0, 23.0, 26.0, 25.0, 24.0, 19.0, 14.0, 15.0, 17.0, 27.0, 30.0, 26.0, 29.0, 26.0, 24.0, 24.0, 22.0, 22.0, 24.0, 24.0, 17.0, 22.0, 21.0, 23.0, 23.0, 19.0, 18.0, 17.0, 17.0, 19.0, 19.0, 12.0, 17.0, 15.0, 17.0, 17.0, 12.0, 17.0, 16.0, 18.0, 15.0, 16.0, 12.0, 17.0, 17.0, 16.0, 12.0, 15.0, 16.0, 17.0, 15.0, 17.0, 17.0, 18.0, 17.0, 19.0, 17.0, 19.0, 19.0, 17.0, 17.0, 17.0, 16.0, 16.0, 17.0, 15.0, 17.0, 26.0, 25.0, 26.0, 24.0, 21.0, 22.0, 23.0, 22.0, 20.0, 33.0, 32.0, 32.0, 29.0, 32.0, 34.0, 36.0, 36.0, 29.0, 26.0, 27.0, 30.0, 31.0, 26.0, 26.0, 28.0, 26.0, 29.0, 28.0, 27.0, 24.0, 24.0, 24.0, 22.0, 19.0, 20.0, 17.0, 12.0, 19.0, 18.0, 14.0, 15.0, 18.0, 18.0, 15.0, 17.0, 16.0, 18.0, 17.0, 19.0, 19.0, 17.0, 29.0, 27.0, 31.0, 32.0, 27.0, 26.0, 26.0, 25.0, 25.0, 17.0, 17.0, 20.0, 18.0, 26.0, 26.0, 27.0, 28.0, 25.0, 25.0, 24.0, 27.0, 25.0, 26.0, 23.0, 26.0, 26.0, 26.0, 26.0, 25.0, 27.0, 25.0, 27.0, 20.0, 20.0, 19.0, 17.0, 20.0, 17.0, 29.0, 27.0, 31.0, 31.0, 26.0, 26.0, 28.0, 27.0, 29.0, 31.0, 31.0, 26.0, 26.0, 27.0, 30.0, 33.0, 35.0, 37.0, 35.0, 15.0, 18.0, 20.0, 20.0, 22.0, 17.0, 19.0, 18.0, 20.0, 29.0, 26.0, 29.0, 29.0, 24.0, 44.0, 29.0, 26.0, 29.0, 29.0, 29.0, 29.0, 23.0, 24.0, 44.0, 41.0, 29.0, 26.0, 28.0, 29.0, 29.0, 29.0, 28.0, 29.0, 26.0, 26.0, 26.0
            |    ],
            |    "fl": [
            |      "p", "p", "p", "p", "p", "p", "p", "p", "p", "p", "p", "p", "p", "p", "p", "p", "p", "p", "r", "e", "r", "r", "r", "p", "p", "p", "p", "p", "r", "e", "r", "d", "r", "r", "r", "r", "r", "r", "r", "r", "r", "r", "r", "e", "r", "r", "r", "r", "r", "r", "r", "r", "r", "r", "e", "r", "r", "r", "r", "e", "r", "r", "r", "r", "r", "e", "r", "r", "r", "e", "r", "r", "r", "r", "r", "r", "r", "r", "r", "r", "r", "r", "r", "r", "r", "r", "r", "r", "r", "r", "r", "r", "r", "r", "r", "r", "r", "r", "p", "r", "r", "r", "p", "r", "r", "r", "c", "p", "r", "r", "r", "r", "r", "r", "r", "r", "r", "r", "r", "r", "r", "r", "d", "r", "r", "r", "e", "r", "r", "p", "p", "r", "r", "p", "r", "p", "r", "r", "r", "r", "r", "r", "r", "r", "r", "p", "p", "r", "r", "p", "r", "r", "p", "p", "r", "p", "r", "r", "p", "r", "r", "r", "p", "r", "p", "r", "r", "r", "r", "p", "r", "p", "r", "r", "r", "r", "r", "r", "r", "r", "r", "r", "r", "r", "r", "r", "r", "r", "r", "r", "r", "r", "r", "r", "r", "r", "r", "r", "r", "r", "r", "r", "r", "r", "r", "r", "r", "r", "r", "p", "p", "r", "d", "r", "r", "p", "p", "r", "r", "r", "r", "d", "d", "r", "r", "r", "r", "p", "p", "p", "p", "p", "p", "p"
            |    ],
            |    "class": [
            |      "compact", "compact", "compact", "compact", "compact", "compact", "compact", "compact", "compact", "compact", "compact", "compact", "compact", "compact", "compact", "midsize", "midsize", "midsize", "suv", "suv", "suv", "suv", "suv", "2seater", "2seater", "2seater", "2seater", "2seater", "suv", "suv", "suv", "suv", "midsize", "midsize", "midsize", "midsize", "midsize", "minivan", "minivan", "minivan", "minivan", "minivan", "minivan", "minivan", "minivan", "minivan", "minivan", "minivan", "pickup", "pickup", "pickup", "pickup", "pickup", "pickup", "pickup", "pickup", "pickup", "suv", "suv", "suv", "suv", "suv", "suv", "suv", "pickup", "pickup", "pickup", "pickup", "pickup", "pickup", "pickup", "pickup", "pickup", "pickup", "suv", "suv", "suv", "suv", "suv", "suv", "suv", "suv", "suv", "pickup", "pickup", "pickup", "pickup", "pickup", "pickup", "pickup", "subcompact", "subcompact", "subcompact", "subcompact", "subcompact", "subcompact", "subcompact", "subcompact", "subcompact", "subcompact", "subcompact", "subcompact", "subcompact", "subcompact", "subcompact", "subcompact", "subcompact", "subcompact", "midsize", "midsize", "midsize", "midsize", "midsize", "midsize", "midsize", "subcompact", "subcompact", "subcompact", "subcompact", "subcompact", "subcompact", "subcompact", "suv", "suv", "suv", "suv", "suv", "suv", "suv", "suv", "suv", "suv", "suv", "suv", "suv", "suv", "suv", "suv", "suv", "suv", "suv", "compact", "compact", "midsize", "midsize", "midsize", "midsize", "midsize", "midsize", "midsize", "suv", "suv", "suv", "suv", "midsize", "midsize", "midsize", "midsize", "midsize", "suv", "suv", "suv", "suv", "suv", "suv", "subcompact", "subcompact", "subcompact", "subcompact", "compact", "compact", "compact", "compact", "suv", "suv", "suv", "suv", "suv", "suv", "midsize", "midsize", "midsize", "midsize", "midsize", "midsize", "midsize", "compact", "compact", "compact", "compact", "compact", "compact", "compact", "compact", "compact", "compact", "compact", "compact", "suv", "suv", "pickup", "pickup", "pickup", "pickup", "pickup", "pickup", "pickup", "compact", "compact", "compact", "compact", "compact", "compact", "compact", "compact", "compact", "compact", "compact", "compact", "compact", "compact", "subcompact", "subcompact", "subcompact", "subcompact", "subcompact", "subcompact", "midsize", "midsize", "midsize", "midsize", "midsize", "midsize", "midsize"
            |    ]
            |  },
            |  "mapping": {
            |    "x": "displ",
            |    "y": "hwy"
            |  },
            |  "data_meta": {
            |    "series_annotations": [
            |      {
            |        "type": "int", "column": "Unnamed: 0"
            |      }, {
            |        "type": "str", "column": "manufacturer"
            |      }, {
            |        "type": "str", "column": "model"
            |      }, {
            |        "type": "float", "column": "displ"
            |      }, {
            |        "type": "int", "column": "year"
            |      }, {
            |        "type": "int", "column": "cyl"
            |      }, {
            |        "type": "str", "column": "trans"
            |      }, {
            |        "type": "str", "column": "drv"
            |      }, {
            |        "type": "int", "column": "cty"
            |      }, {
            |        "type": "int", "column": "hwy"
            |      }, {
            |        "type": "str", "column": "fl"
            |      }, {
            |        "type": "str", "column": "class"
            |      }
            |    ]
            |  },
            |  "theme": {
            |    "legend_position": "bottom"
            |  },
            |  "kind": "plot",
            |  "scales": [
            |    {
            |      "aesthetic": "color", "breaks": [
            |        35.0, 30.0, 25.0, 20.0, 15.0, 10.0
            |      ], "labels": [
            |        "35\n(mpg)", "30\n(mpg)", "25\n(mpg)", "20\n(mpg)", "15\n(mpg)", "10\n(mpg)"
            |      ], "scale_mapper_kind": "color_gradient"
            |    },
            |    {
            |      "aesthetic": "shape", "breaks": [
            |        "f", "r", "4"
            |      ], "labels": [
            |        "Front-wheel\ndrive", "Rear \n wheel \n drive", "4 wheel drive"
            |      ], "solid": true
            |    }
            |  ],
            |  "layers": [
            |    {
            |      "geom": "point", "mapping": {
            |        "color": "cty", "shape": "drv"
            |      }, "data_meta": {}, "size": 5.0
            |    }
            |  ],
            |  "metainfo_list": []
            |}
        """.trimMargin()

        val plotCanvasDrawable = createPlot(parseJson(spec))

        return paint(plotCanvasDrawable)
    }

    fun plot_theme_bottomLegendContinuousScale(): Bitmap {
        val spec = """
            |{
            |  "data": {
            |    "x": [
            |      0.0,
            |      1.0,
            |      2.0,
            |      3.0,
            |      4.0
            |    ]
            |  },
            |  "mapping": {
            |    "x": "x",
            |    "color": "x"
            |  },
            |  "data_meta": {
            |    "series_annotations": [
            |      {
            |        "type": "int",
            |        "column": "x"
            |      }
            |    ]
            |  },
            |  "theme": {
            |    "legend_position": "bottom"
            |  },
            |  "kind": "plot",
            |  "scales": [],
            |  "layers": [
            |    {
            |      "geom": "point",
            |      "mapping": {},
            |      "data_meta": {}
            |    }
            |  ],
            |  "metainfo_list": []
            |}
            """.trimMargin()

        val plotCanvasDrawable = createPlot(parseJson(spec))

        return paint(plotCanvasDrawable)
    }

    fun plot_theme_titleWithBlankLine(): Bitmap {
        val spec = """
            |{
            |  "mapping": {},
            |  "data_meta": {},
            |  "ggtitle": {
            |    "text": "A\n\nB"
            |  },
            |  "kind": "plot",
            |  "scales": [],
            |  "layers": [
            |    {
            |      "geom": "blank",
            |      "mapping": {},
            |      "inherit_aes": false,
            |      "tooltips": "none",
            |      "data_meta": {}
            |    }
            |  ],
            |  "metainfo_list": []
            |}
            """.trimMargin()

        val plotCanvasDrawable = createPlot(parseJson(spec))

        return paint(plotCanvasDrawable)
    }

    fun plot_theme_alphaColorInTitles(): Bitmap {
        val spec = """
            |{
            |  "kind": "plot",
            |  "layers": [ { "geom": "blank" } ],
            |  "ggtitle": { "text": "Title", "subtitle": "Subtitle" },
            |  "caption": { "text": "Caption" },
            |  "theme": {
            |    "plot_title": { "color": "", "blank": false },
            |    "plot_subtitle": { "color": "#00000000", "blank": false },
            |    "plot_caption": { "color": "rgba(123, 0, 222, 0)", "blank": false }
            |  }
            |}
            """.trimMargin()

        val plotCanvasDrawable = createPlot(parseJson(spec))

        return paint(plotCanvasDrawable)
    }
}