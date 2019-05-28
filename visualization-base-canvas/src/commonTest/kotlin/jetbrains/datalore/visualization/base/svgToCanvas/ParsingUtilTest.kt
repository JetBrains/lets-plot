package jetbrains.datalore.visualization.base.svgToCanvas

import jetbrains.datalore.visualization.base.svgToCanvas.ParsingUtil.parsePath
import jetbrains.datalore.visualization.base.svgToCanvas.ParsingUtil.parseTransform
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ParsingUtilTest {

    @Test
    fun testParseTransforms() {
        assertTrue(parseTransform(null).isEmpty())
        assertTrue(parseTransform("").isEmpty())

        isEquivalent(0, parseTransform("translate(30)"), "translate", 30.0)
        isEquivalent(0, parseTransform("translate(10, 20)"), "translate", 10.0, 20.0)
        isEquivalent(0, parseTransform("scale(2)"), "scale", 2.0)
        isEquivalent(0, parseTransform("scale(2, 3)"), "scale", 2.0, 3.0)
        isEquivalent(0, parseTransform("rotate(45 50 50)"), "rotate", 45.0, 50.0, 50.0)
        isEquivalent(0, parseTransform("rotate(45)"), "rotate", 45.0)
        isEquivalent(0, parseTransform("skewX(30)"), "skewX", 30.0)
        isEquivalent(0, parseTransform("skewY(45)"), "skewY", 45.0)
        isEquivalent(0, parseTransform("matrix(1 0 0 1 10 10)"), "matrix", 1.0, 0.0, 0.0, 1.0, 10.0, 10.0)

        val transforms = parseTransform("translate(10,20) matrix(1,0,0,1,10,10) rotate( 45 )")
        assertEquals(3, transforms.size)
        isEquivalent(0, transforms, "translate", 10.0, 20.0)
        isEquivalent(1, transforms, "matrix", 1.0, 0.0, 0.0, 1.0, 10.0, 10.0)
        isEquivalent(2, transforms, "rotate", 45.0)
    }

    @Test
    fun testParsePath() {
        val path = parsePath(
                " M 0 0 m100 200 H0 h200 V20 v-190 L40 40 l20 40 " +
                        "C60 60 100 100 50 50 c10 20 30 40 50 60" +
                        "S80 80 -80 -80 s20 20 -20 -20 " +
                        "Q10 0 20 20 q190 -190 0 0 T120 120 t200 200 " +
                        "A 140 140 0 0 1 0 0 a100 100 0 0 0 -100 100 Z z ")
        assertEquals(20, path.size)
        isEquivalent(0, path, "M", 0.0, 0.0)
        isEquivalent(1, path, "m", 100.0, 200.0)
        isEquivalent(2, path, "H", 0.0)
        isEquivalent(3, path, "h", 200.0)
        isEquivalent(4, path, "V", 20.0)
        isEquivalent(5, path, "v", -190.0)
        isEquivalent(6, path, "L", 40.0, 40.0)
        isEquivalent(7, path, "l", 20.0, 40.0)
        isEquivalent(8, path, "C", 60.0, 60.0, 100.0, 100.0, 50.0, 50.0)
        isEquivalent(9, path, "c", 10.0, 20.0, 30.0, 40.0, 50.0, 60.0)
        isEquivalent(10, path, "S", 80.0, 80.0, -80.0, -80.0)
        isEquivalent(11, path, "s", 20.0, 20.0, -20.0, -20.0)
        isEquivalent(12, path, "Q", 10.0, 0.0, 20.0, 20.0)
        isEquivalent(13, path, "q", 190.0, -190.0, 0.0, 0.0)
        isEquivalent(14, path, "T", 120.0, 120.0)
        isEquivalent(15, path, "t", 200.0, 200.0)
        isEquivalent(16, path, "A", 140.0, 140.0, 0.0, 0.0, 1.0, 0.0, 0.0)
        isEquivalent(17, path, "a", 100.0, 100.0, 0.0, 0.0, 0.0, -100.0, 100.0)
        isEquivalent(18, path, "Z")
        isEquivalent(19, path, "z")
    }

    private fun isEquivalent(index: Int, list: List<ParsingUtil.Result>, name: String, vararg arr: Double) {
        val r = list[index]
        assertEquals(name, r.name)
        assertTrue(arr.toTypedArray() contentEquals r.params.toTypedArray())
    }
}