package jetbrains.datalore.plot.server.config.transform

import jetbrains.datalore.plot.config.Option.Geom.Image.HREF
import jetbrains.datalore.plot.config.Option.Geom.Image.SPEC
import jetbrains.datalore.plot.config.Option.Geom.Image.Spec.BYTES
import jetbrains.datalore.plot.config.Option.Geom.Image.Spec.HEIGHT
import jetbrains.datalore.plot.config.Option.Geom.Image.Spec.TYPE
import jetbrains.datalore.plot.config.Option.Geom.Image.Spec.WIDTH
import jetbrains.datalore.plot.config.Option.Geom.Image.Type
import jetbrains.datalore.plot.config.Option.GeomName
import jetbrains.datalore.plot.config.Option.Layer.GEOM
import jetbrains.datalore.plot.config.Option.Plot
import jetbrains.datalore.plot.config.transform.SpecChange
import jetbrains.datalore.plot.config.transform.SpecChangeContext
import jetbrains.datalore.plot.config.transform.SpecSelector
import jetbrains.datalore.vis.svg.SvgUtils
import jetbrains.datalore.vis.svg.buildDataUrl
import java.awt.image.BufferedImage
import java.io.IOException
import java.lang.Byte.toUnsignedInt
import java.util.*

internal actual class ImageTranscodeSpecChange : SpecChange {

    override fun isApplicable(spec: Map<String, Any>): Boolean {
        return GeomName.IMAGE == spec[GEOM] && spec.containsKey(SPEC)
    }

    override fun apply(spec: MutableMap<String, Any>, ctx: SpecChangeContext) {
        val imageSpec = spec[SPEC]
        if (imageSpec is Map<*, *>) {
            @Suppress("UNCHECKED_CAST")
            imageSpec as Map<String, Any>

            val width = (imageSpec[WIDTH] as Number).toInt()
            val height = (imageSpec[HEIGHT] as Number).toInt()
            val type = imageSpec[TYPE] as String
            val bytes = imageSpec[BYTES] as String
            val href =
                transcodeToDataUrl(
                    width,
                    height,
                    type,
                    bytes
                )

            // replace 'image_spec' with 'href'
            spec.remove(SPEC)
            spec[HREF] = href
        }
    }

    companion object {
        fun specSelector(): SpecSelector {
            return SpecSelector.of(Plot.LAYERS)
        }

        private fun transcodeToDataUrl(width: Int, height: Int, type: String, base64: String): String {
            val bytes = Base64.getDecoder().decode(base64)
            val channels: Int = when (type) {
                Type.GRAY -> 1
                Type.RGB -> 3
                Type.RGBA -> 4
                else -> throw IllegalArgumentException("Unknown image type: '$type'")
            }

            val bufferedImage = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
            var i = 0
            for (y in 0 until height) {
                for (x in 0 until width) {
                    val r = toUnsignedInt(bytes[i])
                    var g = r
                    var b = r
                    var a = 255
                    if (channels > 1) {
                        g = toUnsignedInt(bytes[i + 1])
                        b = toUnsignedInt(bytes[i + 2])
                    }
                    if (channels > 3) {
                        a = toUnsignedInt(bytes[i + 3])
                    }
                    bufferedImage.setRGB(x, y, SvgUtils.toARGB(r, g, b, a))
                    i += channels
                }
            }

            try {
                return SvgUtils.buildDataUrl(bufferedImage)
            } catch (e: IOException) {
                throw IllegalArgumentException("Can't build image $width X $height", e)
            }

        }
    }
}
