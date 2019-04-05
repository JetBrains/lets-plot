package jetbrains.datalore.visualization.base.svg

import com.google.gwt.core.shared.GwtIncompatible
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.observable.property.Property
import jetbrains.datalore.base.observable.property.WritableProperty
import jetbrains.datalore.base.values.Color

import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.Base64

object SvgUtils {
    private val OPACITY_TABLE: DoubleArray

    init {
        OPACITY_TABLE = DoubleArray(256)
        for (alpha in 0..255) {
            OPACITY_TABLE[alpha] = alpha / 255.0
        }
    }

    fun opacity(c: Color): Double {
        return OPACITY_TABLE[c.getAlpha()]
    }

    fun alpha2opacity(colorAlpha: Int): Double {
        return OPACITY_TABLE[colorAlpha]
    }

    fun toARGB(c: Color): Int {
        return toARGB(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha())
    }

    fun toARGB(c: Color, alpha: Double): Int {
        return toARGB(c.getRed(), c.getGreen(), c.getBlue(), max(0.0, min(255.0, alpha * 255)).toInt())
    }

    fun toARGB(r: Int, g: Int, b: Int, alpha: Int): Int {
        val rgb = (r shl 16) + (g shl 8) + b
        return (alpha shl 24) + rgb
    }

    @GwtIncompatible
    @Throws(IOException::class)
    fun buildDataUrl(bufferedImage: BufferedImage): String {
        var bytes: ByteArray
        ByteArrayOutputStream().use({ baos ->
            ImageIO.write(bufferedImage, "png", baos)
            bytes = baos.toByteArray()
        })
        val base64String = Base64.getEncoder().encodeToString(bytes)
        return pngDataURI(base64String)
    }

    fun pngDataURI(base64EncodedPngImage: String): String {
        return StringBuffer("data:image/png;base64,")
                .append(base64EncodedPngImage)
                .toString()
    }

    internal fun colorAttributeTransform(color: Property<SvgColor>, opacity: Property<Double>): WritableProperty<Color> {
        return { value ->
            color.set(SvgColor.create(value))
            if (value != null) {
                opacity.set(opacity(value!!))
            } else {
                opacity.set(1.0)
            }
        }
    }

    fun transformMatrix(element: SvgTransformable, a: Double, b: Double, c: Double, d: Double, e: Double, f: Double) {
        element.transform().set(SvgTransformBuilder().matrix(a, b, c, d, e, f).build())
    }

    fun transformTranslate(element: SvgTransformable, x: Double, y: Double) {
        element.transform().set(SvgTransformBuilder().translate(x, y).build())
    }

    fun transformTranslate(element: SvgTransformable, vector: DoubleVector) {
        transformTranslate(element, vector.x, vector.y)
    }

    fun transformTranslate(element: SvgTransformable, x: Double) {
        element.transform().set(SvgTransformBuilder().translate(x).build())
    }

    fun transformScale(element: SvgTransformable, x: Double, y: Double) {
        element.transform().set(SvgTransformBuilder().scale(x, y).build())
    }

    fun transformScale(element: SvgTransformable, x: Double) {
        element.transform().set(SvgTransformBuilder().scale(x).build())
    }

    fun transformRotate(element: SvgTransformable, a: Double, x: Double, y: Double) {
        element.transform().set(SvgTransformBuilder().rotate(a, x, y).build())
    }

    fun transformRotate(element: SvgTransformable, a: Double) {
        element.transform().set(SvgTransformBuilder().rotate(a).build())
    }

    fun transformSkewX(element: SvgTransformable, a: Double) {
        element.transform().set(SvgTransformBuilder().skewX(a).build())
    }

    fun transformSkewY(element: SvgTransformable, a: Double) {
        element.transform().set(SvgTransformBuilder().skewY(a).build())
    }

    fun copyAttributes(source: SvgElement, target: SvgElement) {
        for (attributeSpec in source.getAttributeKeys()) {
            val spec = attributeSpec as SvgAttributeSpec<Any>
            target.setAttribute(spec, source.getAttribute(attributeSpec).get())
        }
    }
}
