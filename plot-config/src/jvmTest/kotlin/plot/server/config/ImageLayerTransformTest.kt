package jetbrains.datalore.plot.server.config

import jetbrains.datalore.plot.config.Option.Geom.Image
import jetbrains.datalore.plot.config.Option.Geom.Image.Type
import jetbrains.datalore.plot.parsePlotSpec
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ImageLayerTransformTest {

    @Test
    fun specTranscode() {

        // 1 x 1 image
        val encoded = Base64.getEncoder().encodeToString(byteArrayOf(255.toByte()))
        val input = "{" +
                "   'kind': 'plot'," +
                "  'layers': [" +
                "              {" +
                "                  'geom': 'image'," +
                "                  'mapping': {" +
                "                            'xmin': [-0.5]," +
                "                            'ymin': [-0.5]," +
                "                            'xmax': [0.5]," +
                "                            'ymax': [0.5]" +
                "                             }" +
                "                  ," +
                "                  'image_spec': {" +
                "                                'width': 1," +
                "                                'height': 1," +
                "                                'type': '" + Type.GRAY + "'," +
                "                                'bytes': '" + encoded + "'" +
                "                                }" +
                "              }" +
                "            ]" +
                "}"

        val inputSpec = parsePlotSpec(input)
        val transformedSpec = PlotConfigServerSideJvm.processTransformWithEncoding(inputSpec)

        val geomSpec = (transformedSpec["layers"] as List<*>)[0] as Map<*, *>

        // Make sure transform 'image_spec' --> 'href' takes place.

        assertFalse(geomSpec.containsKey("image_spec"))
        assertTrue(geomSpec.containsKey(Image.HREF))

        val expectedDataUrl = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAAC0lEQVR42mP4DwQACfsD/Wj6HMwAAAAASUVORK5CYII="
        assertEquals(expectedDataUrl, geomSpec[Image.HREF])
    }
}