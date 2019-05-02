package jetbrains.datalore.visualization.base.canvas

import java.awt.image.BufferedImage

interface GraphicsCanvasControl : CanvasControl {
    val image: BufferedImage?
}
