package jetbrains.datalore.visualization.base.canvas

import java.awt.Graphics

object CanvasUtil {
    private val DEVICE_PIXEL_RATIO_NAME = "DEVICE_PIXEL_RATIO"

    fun readDevicePixelRatio(defaultValue: Double): Double {
        return if (System.getProperties().containsKey(DEVICE_PIXEL_RATIO_NAME)) {
            java.lang.Double.parseDouble(System.getProperty(DEVICE_PIXEL_RATIO_NAME))
        } else defaultValue
    }

    fun drawGraphicsCanvasControl(graphicsCanvasControl: GraphicsCanvasControl, g: Graphics) {
        val image = graphicsCanvasControl.image
        if (image != null) {
            val size = graphicsCanvasControl.size
            g.drawImage(image, 0, 0, size.x, size.y) { img, infoflags, x, y, width, height -> true }
        }
    }
}
