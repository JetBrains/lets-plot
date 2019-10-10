package jetbrains.datalore.vis.canvas

import java.awt.image.BufferedImage

interface GraphicsCanvasControl : CanvasControl {
    val image: BufferedImage?
}
