package jetbrains.datalore.visualization.base.canvas

import jetbrains.datalore.visualization.base.canvas.CanvasControl

import java.awt.image.BufferedImage

interface GraphicsCanvasControl : CanvasControl {
    val image: BufferedImage?
}
