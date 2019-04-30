package jetbrains.datalore.visualization.base.svgToDom

import jetbrains.datalore.visualization.base.svg.SvgImageElementEx
import jetbrains.datalore.visualization.base.svgToDom.domExtensions.setAlphaAt
import jetbrains.datalore.visualization.base.svgToDom.domExtensions.setBlueAt
import jetbrains.datalore.visualization.base.svgToDom.domExtensions.setGreenAt
import jetbrains.datalore.visualization.base.svgToDom.domExtensions.setRedAt
import org.khronos.webgl.Uint8ClampedArray
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.ImageData
import kotlin.browser.document

class RGBEncoderDom: SvgImageElementEx.RGBEncoder {

    override fun toDataUrl(width: Int, height: Int, argbValues: IntArray): String {
        val canvas: HTMLCanvasElement? = document.createElement("canvas") as HTMLCanvasElement?
        if (canvas == null) {
            throw IllegalStateException("Canvas is not supported.")
        }

        canvas.width = width
        canvas.height = height

        val context = canvas.getContext("2d") as CanvasRenderingContext2D
        val imageData = context.createImageData(width.toDouble(), height.toDouble())
        val dataArray = imageData.data

        for (y in 0..height) {
            for (x in 0..width) {
                setRgb(x, y, argbValues[y * width + x], imageData, dataArray)
            }
        }

        context.putImageData(imageData, 0.0, 0.0)
        return canvas.toDataURL("image/png")
    }

    private fun setRgb(x: Int, y: Int, argbValue: Int, imageData: ImageData, imageDataArray: Uint8ClampedArray) {
        imageData.setAlphaAt(imageDataArray, (argbValue shr 24) and 0xff, x, y)
        imageData.setRedAt(imageDataArray, (argbValue shr 16) and 0xff, x, y)
        imageData.setGreenAt(imageDataArray, (argbValue shr 8) and 0xff, x, y)
        imageData.setBlueAt(imageDataArray, argbValue and 0xff, x, y)
    }
}