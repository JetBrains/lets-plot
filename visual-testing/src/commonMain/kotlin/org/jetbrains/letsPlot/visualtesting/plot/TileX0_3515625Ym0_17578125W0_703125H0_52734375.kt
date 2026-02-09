package org.jetbrains.letsPlot.visualtesting.plot

import org.jetbrains.letsPlot.commons.encoding.Base64
import org.jetbrains.letsPlot.commons.intern.spatial.LonLat
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Rect


object TileX0_3515625Ym0_17578125W0_703125H0_52734375 {
    val rect = Rect.XYWH<LonLat>(0.3515625, -0.17578125, 0.703125, 0.52734375)
    val data = """AjEzCGRfbGFuZF80AAAAAAAKd2F0ZXJfdGlueQAAAAAADXdhdGVyd2F5XzlfMTIAAAAAAAt0dW5uZWxfOV8xNAAAAAAACnJvYWRfMTBfMTIAAAAAAAticmlkZ2VfOV8xNAAAAAAACmFkbWluX2Z1bGwAAAAAAAdtYXJpbmVzAAAAAAASd2F0ZXJfbGFiZWxfc2ltcGxlAAAAAAASY2l0eV92aWxsYWdlX2xhYmVsAAAAAAA="""
    val entry = rect to Base64.decode(data)
}