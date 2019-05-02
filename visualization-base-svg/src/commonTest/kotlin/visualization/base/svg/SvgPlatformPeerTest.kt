package jetbrains.datalore.visualization.base.svg

import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFailsWith

class SvgPlatformPeerTest {
    private var container: SvgNodeContainer? = null

    @BeforeTest
    fun setUp() {
        val root = SvgSvgElement()
        container = SvgNodeContainer(root)
    }

    @Test
    fun containerInit() {
        val c = container as SvgNodeContainer
//        assertNull(c.getPeer())
        assertFailsWith(NullPointerException::class) {
            c.getPeer()
        }
    }
}