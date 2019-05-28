package jetbrains.datalore.visualization.base.svgToDom.domExtensions

import org.khronos.webgl.Uint8ClampedArray
import org.khronos.webgl.set
import org.w3c.dom.ImageData

fun ImageData.setAlphaAt(data: Uint8ClampedArray, alpha: Int, x: Int, y: Int) =
        setColorAt(data, alpha, x, y, ImageDataConsts.OFFSET_ALPHA)

fun ImageData.setBlueAt(data: Uint8ClampedArray, blue: Int, x: Int, y: Int) =
        setColorAt(data, blue, x, y, ImageDataConsts.OFFSET_BLUE)

fun ImageData.setGreenAt(data: Uint8ClampedArray, green: Int, x: Int, y: Int) =
        setColorAt(data, green, x, y, ImageDataConsts.OFFSET_GREEN)

fun ImageData.setRedAt(data: Uint8ClampedArray, red: Int, x: Int, y: Int) =
        setColorAt(data, red, x, y, ImageDataConsts.OFFSET_RED)

fun ImageData.setColorAt(data: Uint8ClampedArray, color: Int, x: Int, y: Int, offset: Int) {
    data[ImageDataConsts.NUM_COLORS * (x + y * this.width) + offset] = color.toByte()
}

object ImageDataConsts {
    const val NUM_COLORS = 4
    const val OFFSET_RED = 0
    const val OFFSET_GREEN = 1
    const val OFFSET_BLUE = 2
    const val OFFSET_ALPHA = 3
}