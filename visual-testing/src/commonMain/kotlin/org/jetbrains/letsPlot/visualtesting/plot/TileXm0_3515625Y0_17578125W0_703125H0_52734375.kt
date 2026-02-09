package org.jetbrains.letsPlot.visualtesting.plot

import org.jetbrains.letsPlot.commons.encoding.Base64
import org.jetbrains.letsPlot.commons.intern.spatial.LonLat
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Rect


object TileXm0_3515625Y0_17578125W0_703125H0_52734375 {
    val rect = Rect.XYWH<LonLat>(-0.3515625, 0.17578125, 0.703125, 0.52734375)
    val data = """ATQIZF9sYW5kXzQAAAAAAAp3YXRlcl90aW55AAAAAAANd2F0ZXJ3YXlfOV8xMgAAAAAAC3R1bm5lbF85XzE0AAAAAAAKcm9hZF8xMF8xMgAAAAAAC2JyaWRnZV85XzE0AAAAAAAKYWRtaW5fZnVsbAAAAAAAB21hcmluZXMAAAAAABJ3YXRlcl9sYWJlbF9zaW1wbGUAAAAAABJjaXR5X3ZpbGxhZ2VfbGFiZWwAAAAAAA=="""
    val entry = rect to Base64.decode(data)
}