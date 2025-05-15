/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.render.svg

import org.jetbrains.letsPlot.commons.intern.observable.property.WritableProperty
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.commons.values.Font
import org.jetbrains.letsPlot.commons.values.FontFamily
import org.jetbrains.letsPlot.core.plot.base.render.svg.Text.HorizontalAnchor
import org.jetbrains.letsPlot.core.plot.base.render.svg.Text.VerticalAnchor
import org.jetbrains.letsPlot.core.plot.base.render.svg.Text.toDY
import org.jetbrains.letsPlot.core.plot.base.render.svg.Text.toTextAnchor
import org.jetbrains.letsPlot.core.plot.base.render.text.RichText
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgConstants
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTextElement


class MultilineLabel(
    val text: String,
    wrapWidth: Int = -1,
    markdown: Boolean = false
) : SvgComponent() {
    private var myLines: List<SvgTextElement> = RichText.toSvg(
        Font(
            family = FontFamily("Lucida Grande, sans-serif", false),
            size = 16,
            isBold = false,
            isItalic = false
        ),
        ::widthCalculator,
        text,
        wrapWidth,
        markdown = markdown
    )
    private var myTextColor: Color? = null
    private var myFontSize = 0.0
    private var myFontWeight: String? = null
    private var myFontFamily: String? = null
    private var myFontStyle: String? = null
    private var myLineHeight = 0.0
    private var myVerticalAnchor: VerticalAnchor? = null
    private var yStart = 0.0

    init {
        myLines.forEach(rootGroup.children()::add)
    }

    override fun buildComponent() {
    }

    override fun addClassName(className: String) {
        myLines.forEach { it.addClass(className) }
    }

    fun textColor(): WritableProperty<Color?> {
        return object : WritableProperty<Color?> {
            override fun set(value: Color?) {
                // set attribute for svg->canvas mapping to work
                myLines.forEach(SvgTextElement::fillColor)

                // duplicate in 'style' to override styles of container
                myTextColor = value
                updateStyleAttribute()
            }
        }
    }

    fun setHorizontalAnchor(anchor: HorizontalAnchor) {
        myLines.forEach {
            it.setAttribute(SvgConstants.SVG_TEXT_ANCHOR_ATTRIBUTE, toTextAnchor(anchor))
        }
    }

    fun setVerticalAnchor(anchor: VerticalAnchor) {
        myVerticalAnchor = anchor
        myLines.forEach {
            it.setAttribute(SvgConstants.SVG_TEXT_DY_ATTRIBUTE, toDY(anchor))
        }
        repositionLines()
    }

    fun setFontSize(px: Double) {
        myFontSize = px
        updateStyleAttribute()
    }

    /**
     * @param cssName : normal, bold, bolder, lighter
     */
    fun setFontWeight(cssName: String?) {
        myFontWeight = cssName
        updateStyleAttribute()
    }

    /**
     * @param cssName : normal, italic, oblique
     */
    fun setFontStyle(cssName: String?) {
        myFontStyle = cssName
        updateStyleAttribute()
    }

    /**
     * @param fontFamily : for example 'sans-serif' or 'Times New Roman'
     */
    fun setFontFamily(fontFamily: String?) {
        myFontFamily = fontFamily
        updateStyleAttribute()
    }

    fun setTextOpacity(value: Double?) {
        myLines.forEach { it.fillOpacity().set(value) }
    }

    private fun updateStyleAttribute() {
        val styleAttr = Text.buildStyle(
            myTextColor,
            myFontSize,
            myFontWeight,
            myFontFamily,
            myFontStyle
        )
        myLines.forEach { it.setAttribute(SvgConstants.SVG_STYLE_ATTRIBUTE, styleAttr) }
    }

    fun setX(x: Double) {
        myLines.forEach { it.x().set(x) }
    }

    fun setY(y: Double) {
        yStart = y
        repositionLines()
    }

    fun setLineHeight(v: Double) {
        myLineHeight = v
        repositionLines()
    }

    private fun repositionLines() {
        val totalHeightShift = myLineHeight * (myLines.size - 1)

        val adjustedYStart = yStart - when (myVerticalAnchor) {
            VerticalAnchor.TOP -> 0.0
            VerticalAnchor.CENTER -> totalHeightShift / 2
            VerticalAnchor.BOTTOM -> totalHeightShift
            else -> 0.0
        }

        myLines.forEachIndexed { index, elem ->
            elem.y().set(adjustedYStart + myLineHeight * index)
        }
    }

    fun linesCount() = myLines.size

    companion object {
        fun splitLines(text: String) = text.split('\n').map(String::trim)

        private const val FONT_SIZE_TO_GLYPH_WIDTH_RATIO_MONOSPACED = 0.6
        private const val FONT_WEIGHT_BOLD_TO_NORMAL_WIDTH_RATIO = 1.075
        private const val LABEL_PADDING = 0.0 //2;
        private const val FONT_WIDTH_SCALE_FACTOR = 0.85026 // See explanation here: font_width_scale_factor.md

        fun widthCalculator(text: String, font: Font): Double {
            return if (font.isMonospased) {
                // ToDo: should take in account font family adjustment parameters.
                monospacedWidth(text.length, font)
            } else {
                FONT_WIDTH_SCALE_FACTOR * TextWidthEstimator.textWidth(text, font)
            }.let {
                it * font.family.widthFactor
            }
        }

        private fun monospacedWidth(labelLength: Int, font: Font): Double {
            val ratio = FONT_SIZE_TO_GLYPH_WIDTH_RATIO_MONOSPACED
            val width = labelLength.toDouble() * font.size * ratio + 2 * LABEL_PADDING
            return if (font.isBold) {
                // ToDo: switch to new ratios.
                width * FONT_WEIGHT_BOLD_TO_NORMAL_WIDTH_RATIO
            } else {
                width
            }
        }
    }
}

object TextWidthEstimator {
    private const val DEFAULT_CHAR_WIDTH = 12.327791262135923
    private const val DEFAULT_FAMILY = "Lucida Grande"
    private const val DEFAULT_FONT_SIZE = 14

    // Symbols '-', '/', '\' and '|' were classified by our model as the Cluster-0 symbols (most narrow).
    // However, they appear to be substantially wider than it was expected on MacOS.
    // Wherefore, as a temporary workaround, they were moved to the Cluster-1 - i.e. to the cluster of slightly wider symbols.
    private val MISCLASSIFIED = listOf('-', '/', '\\', '|')

    private val CLUSTERS = listOf(
        listOf(
            ' ',
            '¸',
            '·',
            'ŕ',
            '´',
            '³',
            '²',
            'ŗ',
            '°',
            'ř',
            'ª',
            '¨',
            '¦',
            '¹',
            'ţ',
            '{',
            'ť',
            'ŧ',
            't',
            'r',
            'l',
            'j',
            'i',
            'f',
            '`',
            ']',
            '}',
            'º',
            'і',
            'ï',
            'î',
            'í',
            'ì',
            'Ĩ',
            'ĩ',
            'Ī',
            'ī',
            'Ĭ',
            'ĭ',
            'Į',
            'į',
            'ѓ',
            'İ',
            'ĵ',
            'ĺ',
            'ļ',
            'ľ',
            'ŀ',
            'ł',
            'Ï',
            'Î',
            'Í',
            'Ì',
            'ј',
            'ї',
            'ı',
            '[',
            '¡',
            ',',
            '.',
            'I',
            ')',
            '(',
            '\'',
            'Ї',
            'І',
            '"',
            '!',
            'J',
            'г',
            'ț',
            ':',
            'ȷ',
            'ſ',
            ';'
        ),
        MISCLASSIFIED +
                listOf(
                    'Ǐ',
                    'ň',
                    'ķ',
                    'ņ',
                    'ĸ',
                    'ў',
                    'Ĺ',
                    'ȋ',
                    'Ļ',
                    'ȑ',
                    'Ľ',
                    'ń',
                    'џ',
                    'ǁ',
                    'ǃ',
                    'Ŀ',
                    'ǐ',
                    'Ł',
                    'ș',
                    'ȓ',
                    'и',
                    'ĳ',
                    'ć',
                    'ғ',
                    'ĉ',
                    'ċ',
                    'ґ',
                    'č',
                    'Ґ',
                    'đ',
                    'ē',
                    'ĕ',
                    'ė',
                    '҈',
                    'ę',
                    'ě',
                    'ĝ',
                    'ğ',
                    'ġ',
                    'ǰ',
                    'ǻ',
                    'ģ',
                    'ĥ',
                    'ħ',
                    'ǿ',
                    'Ȉ',
                    'ȉ',
                    'Ȋ',
                    'ќ',
                    'Ĵ',
                    'ћ',
                    'ŏ',
                    'ǀ',
                    'ц',
                    'а',
                    'х',
                    'ũ',
                    'у',
                    'б',
                    'ū',
                    'в',
                    'т',
                    'ŭ',
                    'с',
                    'ů',
                    'р',
                    'ű',
                    'д',
                    'е',
                    'п',
                    'ų',
                    'з',
                    'о',
                    'н',
                    'ŷ',
                    'л',
                    'к',
                    'ź',
                    'й',
                    'ž',
                    'ƒ',
                    'ŋ',
                    'З',
                    'Ɨ',
                    'ō',
                    'ż',
                    'ő',
                    'ѕ',
                    'є',
                    'ȴ',
                    'ȶ',
                    'ɉ',
                    'ƭ',
                    'ƫ',
                    'ђ',
                    'ƪ',
                    'ɍ',
                    'Ѓ',
                    'ё',
                    'Ј',
                    'ś',
                    'я',
                    'Г',
                    'ŝ',
                    'э',
                    'ş',
                    'ь',
                    'š',
                    'ƚ',
                    'ą',
                    'ч',
                    'Ɩ',
                    'ă',
                    '҉',
                    '9',
                    '£',
                    '¤',
                    '¥',
                    '§',
                    '«',
                    '¯',
                    '±',
                    'µ',
                    '¶',
                    '¢',
                    '»',
                    '¿',
                    '8',
                    'Ӏ',
                    '7',
                    '6',
                    '5',
                    'ҭ',
                    '4',
                    '3',
                    'ӏ',
                    'z',
                    'y',
                    'x',
                    'ӷ',
                    'ӻ',
                    'F',
                    '?',
                    '^',
                    '_',
                    'a',
                    'b',
                    'c',
                    'd',
                    'e',
                    'g',
                    'h',
                    'k',
                    'n',
                    'o',
                    'p',
                    'q',
                    's',
                    'u',
                    'v',
                    '2',
                    '1',
                    'L',
                    'ó',
                    'é',
                    'ê',
                    '*',
                    'ë',
                    'ÿ',
                    'ð',
                    'ñ',
                    'þ',
                    'ò',
                    'è',
                    'ý',
                    'ҝ',
                    'û',
                    'ô',
                    'õ',
                    'ö',
                    'ú',
                    '÷',
                    '$',
                    'ø',
                    'ü',
                    'ç',
                    'ù',
                    'ß',
                    'ā',
                    '0',
                    'å',
                    'ã',
                    'ä',
                    'қ',
                    'à',
                    'â',
                    'á'
                ),
        listOf(
            'ԭ',
            'А',
            'Б',
            'Ё',
            'В',
            'ӽ',
            'Ǯ',
            'ǵ',
            'ɀ',
            'Ў',
            'Ɂ',
            'ӿ',
            'ɏ',
            'ԇ',
            'ԁ',
            'ԍ',
            'ǹ',
            'ɋ',
            'Ќ',
            'Є',
            'Ԁ',
            'ԑ',
            'ǯ',
            'Ɉ',
            'ɇ',
            'ɂ',
            'Ѕ',
            'Ԑ',
            'ȏ',
            'ȿ',
            'ȩ',
            'ȧ',
            'ȥ',
            'Ȥ',
            'ȣ',
            'Ȣ',
            'Д',
            'ȟ',
            'Ȝ',
            'Ț',
            'Ș',
            'ȗ',
            'ȕ',
            'ȍ',
            'ȝ',
            'ȫ',
            'ȭ',
            'ȯ',
            'ԩ',
            'Ⱦ',
            'ȁ',
            'ȃ',
            'Ƚ',
            'ȼ',
            'ȅ',
            'ȇ',
            'ԓ',
            'ԧ',
            'ԛ',
            'ԟ',
            'ȳ',
            'ԥ',
            'ȱ',
            'Ǻ',
            'Ҙ',
            'м',
            'Ӻ',
            'ң',
            'ҩ',
            'ҫ',
            'Ҭ',
            'Ү',
            'ү',
            'Ұ',
            'ҡ',
            'ұ',
            'ҵ',
            'ҷ',
            'ҹ',
            'һ',
            'ѝ',
            'ҽ',
            'ҿ',
            'ҳ',
            'ҟ',
            'ѣ',
            'Ҟ',
            'ҕ',
            'Қ',
            'Ғ',
            'ҏ',
            'ҍ',
            'Ҍ',
            'ҋ',
            'ǭ',
            '҂',
            'ҁ',
            'ѷ',
            'ѵ',
            'ѳ',
            'ѯ',
            'Ѯ',
            'ѧ',
            'Ҝ',
            'ӄ',
            'Е',
            'ӆ',
            'ӊ',
            'ӳ',
            'ӵ',
            'Я',
            'Э',
            'Ь',
            'Ч',
            'Х',
            'ӱ',
            'У',
            'С',
            'Р',
            'П',
            'Н',
            'Ӷ',
            'Л',
            'К',
            'Т',
            'ӯ',
            'ӭ',
            'ӫ',
            'ӌ',
            'ӑ',
            'ӓ',
            'ӗ',
            'ѐ',
            'ә',
            'ӛ',
            'ъ',
            'Ӟ',
            'ӟ',
            'Ӡ',
            'ҙ',
            'ӡ',
            'ӣ',
            'ӥ',
            'ӧ',
            'ө',
            'ӈ',
            'ǫ',
            'ԯ',
            'ǩ',
            'ư',
            'Ʈ',
            'Þ',
            'Ť',
            'Ŧ',
            'Ʃ',
            '¬',
            'Ũ',
            'Ý',
            'À',
            'Ū',
            'Á',
            'ƥ',
            'Ŭ',
            'ƨ',
            'Â',
            'Ţ',
            'Š',
            'X',
            'ƹ',
            'Ƹ',
            'Ś',
            'Y',
            'Ŝ',
            'Ā',
            'Z',
            'ƶ',
            'Ƶ',
            'ƴ',
            'Ă',
            'Ş',
            '~',
            'Ʒ',
            'Ù',
            'Ů',
            'Ã',
            'Ž',
            'Ƒ',
            'ƀ',
            'Ɛ',
            'Ñ',
            'ƍ',
            'Ë',
            'ƌ',
            'Ƌ',
            'Ƃ',
            'ƃ',
            'Ƅ',
            '×',
            'ƅ',
            'Ú',
            'ơ',
            'Ż',
            'Ź',
            'Ű',
            'Ä',
            'Ų',
            'Ü',
            'Å',
            'Û',
            'Ê',
            'ƞ',
            'Ŷ',
            'ƛ',
            'ƙ',
            'È',
            'Ÿ',
            'É',
            'Ç',
            'ƻ',
            'ƺ',
            'Ƽ',
            'ď',
            'ǖ',
            'Ř',
            'C',
            'ǘ',
            'Ķ',
            'ǚ',
            'B',
            'ǜ',
            'Ē',
            'ǝ',
            'Ĕ',
            'A',
            'E',
            'ǟ',
            'Ė',
            'Ę',
            'Ě',
            'ǡ',
            '=',
            '<',
            'Ħ',
            'Ĥ',
            '+',
            '&',
            'ǥ',
            'ǧ',
            '#',
            '>',
            'Ń',
            'ƈ',
            'Ņ',
            'ǂ',
            'Ŕ',
            'Ą',
            'ƾ',
            'T',
            'V',
            'ǔ',
            'S',
            'Ć',
            'R',
            'ƽ',
            'U',
            'Ċ',
            'Ĉ',
            'P',
            'Č',
            'N',
            'Ŗ',
            'ŉ',
            'ǎ',
            'K',
            'ǒ',
            'Ň',
            'H',
            'ǉ',
            'ƿ'
        ),
        listOf(
            'Ѻ',
            'Ğ',
            'Ң',
            'ѻ',
            'җ',
            'ѽ',
            'ѿ',
            'Ҁ',
            'Ĝ',
            'Ҕ',
            'Ҋ',
            'æ',
            'Ď',
            'Ҏ',
            'Ҡ',
            'Đ',
            'Ӈ',
            'Ҩ',
            'Ӭ',
            'Ӯ',
            'Ӱ',
            'Ӳ',
            'Q',
            'Ӵ',
            'O',
            'ӹ',
            'M',
            'Ӽ',
            'Ӿ',
            'G',
            'D',
            'ԅ',
            'Ԇ',
            'Ԍ',
            'Ԏ',
            'Ԭ',
            'Ԩ',
            'Ԧ',
            'Ԥ',
            'ԣ',
            'Ԟ',
            'Ӫ',
            'ԝ',
            'ԙ',
            'ԗ',
            'Ԗ',
            'ԕ',
            'Ԓ',
            'ԏ',
            'Ԛ',
            'Ө',
            'Ӧ',
            'Ӥ',
            'Ҿ',
            'Ҽ',
            'Һ',
            'Ҹ',
            'Ҷ',
            'Ð',
            'ӂ',
            'Ҳ',
            'Ó',
            'Ô',
            'Õ',
            'Ö',
            'Ҫ',
            'Ø',
            'Ò',
            'ҥ',
            'Ӄ',
            'Ѷ',
            'Ӣ',
            'm',
            'w',
            'ӝ',
            '©',
            'Ӛ',
            'Ӆ',
            'Ә',
            'Ӗ',
            'Ӓ',
            'Ӑ',
            'ӎ',
            'Ӌ',
            'Ӊ',
            '®',
            'Ġ',
            'Ѣ',
            'Ģ',
            'Ȩ',
            'Ȫ',
            'Ȭ',
            'Ȯ',
            'Ȱ',
            'Ȳ',
            'ȵ',
            'Ⱥ',
            'Ȼ',
            'Ƴ',
            'Ʋ',
            'Ʊ',
            'Ƀ',
            'Ʉ',
            'Ȧ',
            'Ʌ',
            'Ư',
            'Ɋ',
            'Ƭ',
            'Ѵ',
            'Ɍ',
            'Ɏ',
            'Ѐ',
            'Ƨ',
            'Ђ',
            'Ʀ',
            'Ƥ',
            'ƣ',
            'Ћ',
            'Ơ',
            'Ɇ',
            'Ѝ',
            'ǈ',
            'Ƞ',
            'Ǭ',
            'Ǩ',
            'Ǧ',
            'Ǵ',
            'Ǥ',
            'Ƿ',
            'Ǹ',
            'ǽ',
            'Ǿ',
            'Ǡ',
            'Ȁ',
            'Ȃ',
            'Ԯ',
            'Ȅ',
            'ȡ',
            'Ȇ',
            'Ǚ',
            'Ȍ',
            'Ǘ',
            'Ȏ',
            'Ȑ',
            'Ǖ',
            'Ȓ',
            'Ȕ',
            'Ǔ',
            'Ȗ',
            'Ǒ',
            'Ǎ',
            'ǌ',
            'Ȟ',
            'Ǜ',
            'Ɵ',
            'Ǟ',
            'Ǫ',
            'Ц',
            'Ɗ',
            'ѥ',
            'Ѧ',
            'Ъ',
            'Ĳ',
            'Ɖ',
            'Џ',
            'ю',
            'Ѫ',
            'ѡ',
            'щ',
            'Ƈ',
            'ш',
            'Ɔ',
            'Ɓ',
            'ж',
            'ŵ',
            'ѫ',
            'ф',
            'ы',
            'Ǝ',
            'Ф',
            'Ő',
            'Ɲ',
            'Ѳ',
            'Ƙ',
            'Ŋ',
            'њ',
            'И',
            'Ə',
            'Ɣ',
            'Й',
            'љ',
            'Ō',
            'ѱ',
            'М',
            'Ѱ',
            'О',
            'Ŏ',
            'Ɠ',
            'ѩ'
        ),
        listOf(
            'Ѩ',
            'ԋ',
            'Ԋ',
            'ԉ',
            'Ѥ',
            'Ԉ',
            '@',
            'Æ',
            'Ѭ',
            'Ѹ',
            'ԫ',
            'Ԫ',
            '%',
            'ѹ',
            'Ǳ',
            'ǲ',
            'ǳ',
            'Ԣ',
            'ԡ',
            'Ԡ',
            'Ƕ',
            'Ѽ',
            'ѭ',
            'Ԝ',
            'ǣ',
            'Ǣ',
            'Ԙ',
            'Ѿ',
            'Ԕ',
            'Ǽ',
            'ǋ',
            'Ԅ',
            'Ӝ',
            'Щ',
            'Ш',
            'Ӕ',
            '¼',
            '½',
            '¾',
            'Ы',
            'Ҵ',
            'ƕ',
            'Ƣ',
            'Љ',
            'Њ',
            'Ж',
            'Ɯ',
            'Ӂ',
            'Ӎ',
            'Ѡ',
            'ҧ',
            'Ю',
            'ԃ',
            'Ԃ',
            'Җ',
            'Œ',
            'œ',
            'Ӹ',
            'Ǌ',
            'Ҧ',
            'Ǉ',
            'ǅ',
            'Ǆ',
            'Ŵ',
            'W',
            'Ҥ',
            'ȸ',
            'ȹ',
            'ǆ',
            'ӕ'
        )
    )

    private val CLUSTERING: Map<Char, Int> = CLUSTERS.mapIndexed { id, cluster -> cluster.map { Pair(id, it) } }
        .flatten()
        .associate { it.second to it.first }

    private val CLUSTER_WIDTH = listOf(
        6.440506329113925,
        10.181218274111677,
        12.583512544802868,
        14.536683417085428,
        17.714285714285715
    )

    private val FAMILY_COEFFICIENT = mapOf(
        "Arial" to 0.08777509389956582,
        "Calibri" to -0.7568924686338481,
        "Garamond" to -1.2341292120659895,
        "Geneva" to 0.08777509389956582,
        "Georgia" to 0.06628876951083008,
        "Helvetica" to 0.08777509389956582,
        "Lucida Grande" to 0.08777509389956582,
        "Rockwell" to 0.41710660522332965,
        "Times New Roman" to -1.2007569745330333,
        "Verdana" to 1.3042084025015728
    )

    private const val SIZE_COEFFICIENT = 0.9843304096547842

    private fun getCharWidth(char: Char): Double {
        val clusterId = CLUSTERING.getOrElse(char) { -1 }
        return if (clusterId != -1) CLUSTER_WIDTH[clusterId] else DEFAULT_CHAR_WIDTH
    }

    private fun getFamilyAdditive(font: Font): Double {
        val fontFamily = font.family.toString()
        val defaultFamilyCoefficient = FAMILY_COEFFICIENT[DEFAULT_FAMILY] ?: 0.0
        return FAMILY_COEFFICIENT[fontFamily] ?: defaultFamilyCoefficient
    }

    private fun getFaceAdditive(font: Font): Double {
        return when {
            font.isBold && font.isItalic -> 0.9172120995070999
            font.isBold && !font.isItalic -> 0.6908238890181602
            !font.isBold && font.isItalic -> 0.1783188620736738
            else -> 0.0
        }
    }

    private fun getSizeCoefficient(font: Font): Double {
        return SIZE_COEFFICIENT * font.size / DEFAULT_FONT_SIZE
    }

    private fun correctPrediction(predictedWidth: Double, textLength: Int, font: Font): Double {
        return (
                predictedWidth + textLength * (getFamilyAdditive(font) + getFaceAdditive(font))
                ) * getSizeCoefficient(font)
    }

    fun textWidth(text: String, font: Font): Double {
        if (text.isEmpty()) return 0.0
        return correctPrediction(text.map(this::getCharWidth).sum(), text.length, font)
    }
}