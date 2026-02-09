package org.jetbrains.letsPlot.visualtesting.plot

import org.jetbrains.letsPlot.commons.encoding.Base64
import org.jetbrains.letsPlot.commons.intern.spatial.LonLat
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Rect


object TileX0_0Ym0_3515625W0_703125H0_3515625 {
    val rect = Rect.XYWH<LonLat>(0.0, -0.3515625, 0.703125, 0.3515625)
    val data = """AjEwCGRfbGFuZF80AAAAAAAKd2F0ZXJfdGlueQAAAAAADXdhdGVyd2F5XzlfMTIAAAAAAAt0dW5uZWxfOV8xNAAAAAAACnJvYWRfMTBfMTIAAAAAAAticmlkZ2VfOV8xNAAAAAAACmFkbWluX2Z1bGwAAAAAAAdtYXJpbmVzAAAAAAASd2F0ZXJfbGFiZWxfc2ltcGxlAAAAAAASY2l0eV92aWxsYWdlX2xhYmVsAAAAAAA="""
    val entry = rect to Base64.decode(data)
}