/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.core.graphics

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.commons.values.Color
import jetbrains.datalore.vis.canvas.Context2d
import jetbrains.livemap.core.graphics.Attribution.AttributionParts.SimpleLink
import jetbrains.livemap.core.graphics.Attribution.AttributionParts.SimpleText
import jetbrains.livemap.core.openLink
import jetbrains.livemap.core.util.Geometries.plus
import kotlin.math.max

class Attribution : RenderBox() {
    private val texts: MutableList<Text> = mutableListOf()
    private val clickHandler: MutableList<Registration> = mutableListOf()
    private val alignment = Alignment()
    var text: String by visualProp("")
    var padding: Double by visualProp(0.0)
    var background: Color by visualProp(Color.TRANSPARENT)
    var horizontalAlignment
        set(value) {
            alignment.horizontal = value
            isDirty = true
        }
        get() = alignment.horizontal

    var verticalAlignment
        set(value) {
            alignment.vertical = value
            isDirty = true
        }
        get() = alignment.vertical


    override fun updateState() {
        // cleanup
        clickHandler.onEach(Registration::remove).clear()

        AttributionParser.parse(text).forEach { part ->
            val c = if (part is SimpleLink) Color(26, 13, 171) else Color.BLACK

            val attributionText = Text()
            attributionText.attach(graphics)

            attributionText.setState {
                color = c
                fontFamily = CONTRIBUTORS_FONT_FAMILY
                fontSize = 11.0
                text = listOf(part.text)
            }

            if (part is SimpleLink) {
                clickHandler += graphics.onClick(attributionText) {
                    openLink(part.href)
                }
            }

            texts.add(attributionText)
        }


        texts.forEach {
            val dim = it.dimension

            it.origin = DoubleVector(
                dimension.x + padding,
                padding
            )

            dimension = DoubleVector(
                x = dimension.x + dim.x,
                y = max(dimension.y, dim.y)
            )
        }

        dimension = dimension.add(DoubleVector(padding * 2, padding * 2))
        origin = alignment.calculatePosition(origin, dimension)

        texts.forEach {
            it.origin += origin
        }
    }

    protected override fun renderInternal(ctx: Context2d) {
        ctx.setTransform(1.0, 0.0, 0.0, 1.0, 0.0, 0.0)

        ctx.save()
        ctx.setFillStyle(background)
        ctx.fillRect(
            origin.x,
            origin.y,
            dimension.x,
            dimension.y
        )
        ctx.restore()

        texts.forEach {
            renderPrimitive(ctx, it)
        }
    }

    private fun renderPrimitive(ctx: Context2d, primitive: RenderBox) {
        ctx.save()
        val origin = primitive.origin
        ctx.setTransform(1.0, 0.0, 0.0, 1.0, origin.x, origin.y)
        primitive.render(ctx)
        ctx.restore()
    }

    class Alignment {
        var horizontal = HorizontalAlignment.RIGHT
        var vertical = VerticalAlignment.TOP

        enum class HorizontalAlignment {
            RIGHT,
            CENTER,
            LEFT
        }

        enum class VerticalAlignment {
            TOP,
            CENTER,
            BOTTOM
        }

        fun calculatePosition(origin: DoubleVector, dimension: DoubleVector): DoubleVector {
            val horizontalShift = when (horizontal) {
                HorizontalAlignment.LEFT -> -dimension.x
                HorizontalAlignment.CENTER -> -dimension.x / 2
                HorizontalAlignment.RIGHT -> 0.0
            }

            val verticalShift = when (vertical) {
                VerticalAlignment.TOP -> 0.0
                VerticalAlignment.CENTER -> -dimension.y / 2
                VerticalAlignment.BOTTOM -> -dimension.y
            }

            return origin + DoubleVector(horizontalShift, verticalShift)
        }
    }

    interface AttributionParts {
        val text: String

        data class SimpleText(override val text: String) : AttributionParts
        data class SimpleLink(val href: String, override val text: String) : AttributionParts
    }

    object AttributionParser {
        private val regex = "(<a[^>]*>[^<]*<\\/a>|[^<]*)".toRegex()
        private val linkRegex = "href=\"([^\"]*)\"[^>]*>([^<]*)<\\/a>".toRegex()

        fun parse(rawAttribution: String): List<AttributionParts> {
            return ArrayList<AttributionParts>().apply {
                var result = regex.find(rawAttribution)

                while (result != null) {
                    if (result.value.isNotEmpty()) {
                        val part = if (result.value.startsWith("<a")) {
                            parseLink(result.value)
                        } else {
                            SimpleText(result.value)
                        }

                        add(part)
                    }

                    result = result.next()
                }
            }
        }

        private fun parseLink(link: String): AttributionParts {
            val result = linkRegex.find(link)

            return result?.destructured?.let {
                val (href, text) = it
                if (href.isEmpty()) {
                    null
                } else {
                    SimpleLink(href, text)
                }
            } ?: SimpleText(link)
        }
    }

    companion object {
        private const val CONTRIBUTORS_FONT_FAMILY =
            "-apple-system, BlinkMacSystemFont, \"Segoe UI\", Helvetica, Arial, sans-serif, " + "\"Apple Color Emoji\", \"Segoe UI Emoji\", \"Segoe UI Symbol\""
    }
}

