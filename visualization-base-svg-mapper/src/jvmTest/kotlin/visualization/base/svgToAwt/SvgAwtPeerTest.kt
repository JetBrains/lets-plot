package jetbrains.datalore.visualization.base.svgToAwt

import jetbrains.datalore.visualization.base.svg.SvgNodeContainer
import jetbrains.datalore.visualization.base.svg.SvgSvgElement
import org.junit.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class SvgAwtPeerTest {
    private val root = SvgSvgElement()
    private val container: SvgNodeContainer = SvgNodeContainer(root)
    private val mapper: SvgRootDocumentMapper = SvgRootDocumentMapper(root)

    @Test
    fun documentInitNullPeer() {
        assertNull(container.getPeer())
    }

    @Test
    fun mapperAttachPeerSet() {
        mapper.attachRoot()
        assertNotNull(container.getPeer())
    }

    @Test
    fun mapperDetachRootNullPeer() {
        mapper.attachRoot()
        mapper.detachRoot()
        assertNull(container.getPeer())
    }
}
