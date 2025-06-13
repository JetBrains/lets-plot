import ImageMagick.TypeInfo
import kotlinx.cinterop.*
import org.jetbrains.letsPlot.core.canvas.Font
import platform.posix.size_tVar
import kotlin.test.Ignore
import kotlin.test.Test

/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

@Ignore
class MagickCanvasTest {
    private val imageComparer = ImageComparer()

    fun listKnownFonts(pattern: String = "*") {
        memScoped {
            val countVar = alloc<size_tVar>()
            val fontNamesPtr = ImageMagick.MagickQueryFonts(pattern, countVar.ptr)

            if (fontNamesPtr != null) {
                val count = countVar.value.toInt()
                for (i in 0 until count) {
                    val namePtr = fontNamesPtr[i]
                    if (namePtr != null) {
                        val fontName = namePtr.toKString()
                        println("Font #$i: $fontName")
                    }
                }
                ImageMagick.MagickRelinquishMemory(fontNamesPtr)
            } else {
                println("No fonts found for pattern: $pattern")
            }
        }
    }

    fun getTypeInfoByFamily(
        family: String,
        style: ImageMagick.StyleType,
        stretch: ImageMagick.StretchType,
        weight: Int
    ): CPointer<TypeInfo>? {
        memScoped {
            val ex = ImageMagick.AcquireExceptionInfo()

            val type = ImageMagick.GetTypeInfoByFamily(family, style, stretch, weight.convert(), ex)
            if (type == null) {
                println("Failed to get type info for family '$family': ${ex!!.pointed.description?.toKString() ?: "Unknown error"}")
                error("Failed to get type info for family '$family'")
            } else {
                return type
            }
        }
    }

    @Test
    fun unknownFont() {
        /*
        println("Helvetica font:")
        listKnownFonts("Helvetica*")

        println("Nimbus font:")
        listKnownFonts("Nimbus*")

        println("All known fonts:")
        listKnownFonts()

         */

        memScoped {

            val fixed = getTypeInfoByFamily(
                "fixed",
                ImageMagick.StyleType.NormalStyle,
                ImageMagick.StretchType.NormalStretch,
                400.convert()
            )
            if (fixed != null) {
                val o = fixed.pointed
                println("GetTypeInfoByFamily for 'fixed':")
                logTypeInfo(o)
                println("------------------------\n\n")
            } else {
                println("Failed to get type info for 'fixed'")
                println("------------------------\n\n")
            }

            val ex = alloc<ImageMagick.ExceptionInfo>()
            val info = ImageMagick.GetTypeInfo("Helvetica", ex.ptr)
            if (info == null) {
                println("Failed to get type info for 'Helvetica': ${ex.description?.toKString() ?: "Unknown error"}")
            } else {
                val o = info.pointed
                println("GetTypeInfo for 'Helvetica':")
                logTypeInfo(o)
                println("------------------------\n\n")
            }

            val arialInfo = ImageMagick.GetTypeInfo("Arial", ex.ptr)
            if (arialInfo == null) {
                println("Failed to get type info for 'Arial': ${ex.description?.toKString() ?: "Unknown error"}")
            } else {
                val o = arialInfo.pointed
                println("GetTypeInfo for 'Arial':")
                logTypeInfo(o)
                println("------------------------\n\n")
            }

            val countVar = alloc<size_tVar>()
            val fontsPtr = ImageMagick.GetTypeInfoList("Helvetica*", countVar.ptr, ex.ptr)
            if (fontsPtr != null) {
                val count = countVar.value.toInt()
                println("Found $count fonts matching 'Helvetica':")
                for (i in 0 until count) {
                    val fontInfo = fontsPtr[i]
                    if (fontInfo != null) {
                        val o = fontInfo.pointed
                        logTypeInfo(o)
                        println()
                    }
                }
                println("------------------------\n\n")
                ImageMagick.MagickRelinquishMemory(fontsPtr)
            } else {
                println("No fonts found for 'Helvetica': ${ex.description?.toKString() ?: "Unknown error"}")
            }



            val type = getTypeInfoByFamily("Helvetica", ImageMagick.StyleType.NormalStyle, ImageMagick.StretchType.NormalStretch, 400.convert())
            if (type == null) {
                println("Failed to get type info by family for 'Helvetica': ${ex.description?.toKString() ?: "Unknown error"}")
            } else {
                val o = type.pointed
                println("GetTypeInfoByFamily for 'Helvetica':")
                logTypeInfo(o)
                println("------------------------\n\n")
            }

            val purisa = getTypeInfoByFamily("purisa", ImageMagick.StyleType.NormalStyle, ImageMagick.StretchType.NormalStretch, 400.convert())
            if (purisa == null) {
                println("Failed to get type info by family for 'purisa': ${ex.description?.toKString() ?: "Unknown error"}")
            } else {
                val o = purisa.pointed
                println("GetTypeInfoByFamily for 'purisa':")
                logTypeInfo(o)
                println("------------------------\n\n")
            }


        }

        val (canvas, ctx) = createCanvas()
        ctx.setFont(Font(fontFamily = "Helvetica", fontSize = 28.0))
        ctx.fillText("Hello,", 0.0, 20.0)
        ctx.fillText("World!", 0.0, 48.0)

        // No assertion needed; the test passes if no exception is thrown.
        imageComparer.assertImageEquals("text_unknown_font.bmp", canvas.img)
    }

    private fun logTypeInfo(o: TypeInfo) {
        println(
            """
                        Font Info:
                        Description: ${o.description ?: "null"}
                        Encoding: ${o.encoding ?: "null"}
                        Face: ${o.face.toDouble()}
                        Family: ${o.family?.toKStringFromUtf8() ?: "null"}
                        Format: ${o.format ?: "null"}
                        Foundry: ${o.foundry?.toKStringFromUtf8() ?: "null"}
                        Glyphs: ${o.glyphs}
                        Metrics: ${o.metrics ?: "null"}
                        Name: ${o.name?.toKStringFromUtf8() ?: "null"}
                        Path: ${o.path?.toKStringFromUtf8() ?: "null"}
                        Signature: ${o.signature}
                        Stealth: ${o.stealth}
                        Stretch: ${o.stretch}
                        Style: ${o.style}
                        Weight: ${o.weight}
                    """.trimIndent()
        )
    }
}