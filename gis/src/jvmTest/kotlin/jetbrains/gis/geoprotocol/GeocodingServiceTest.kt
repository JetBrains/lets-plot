/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.gis.geoprotocol

import org.jetbrains.letsPlot.base.intern.async.SimpleAsync
import jetbrains.gis.geoprotocol.GeoResponse.SuccessGeoResponse.GeocodedFeature
import jetbrains.gis.geoprotocol.GeoResponseBuilder.GeocodingAnswerBuilder
import jetbrains.gis.geoprotocol.GeoResponseBuilder.NamesakeBuilder
import jetbrains.gis.geoprotocol.GeoResponseBuilder.SuccessResponseBuilder
import org.assertj.core.api.AbstractAssert
import org.assertj.core.api.Assertions.assertThat
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals

@Ignore
class GeocodingServiceTest {

    @Test
    fun createMessageForAmbiguousFeatureWithNamesakes() {
        val actual = GeocodingService.createAmbiguousMessage(listOf(FEATURE_WITH_UNORDERED_NAMESAKES))

        val expected = createErrorMessage()
        appendMultipleMessageHead(expected, FEATURE_WITH_UNORDERED_NAMESAKES)
        expected.append(":").append("\n")
        appendNamesake(expected, FIRST_NAMESAKE, SECOND_NAMESAKE, THIRD_NAMESAKE)

        assertEquals(expected.toString(), actual)
    }

    @Test
    fun createMessageForAmbiguousFeatureWithoutNamesakeExamples() {
        val actual = GeocodingService.createAmbiguousMessage(listOf(FEATURE_WITHOUT_NAMESAKE_EXAMPLES))

        val expected = createErrorMessage()
        appendMultipleMessageHead(expected, FEATURE_WITHOUT_NAMESAKE_EXAMPLES)
        expected.append(".").append("\n")

        assertEquals(expected.toString(), actual)
    }

    @Test
    fun createMessageForAmbiguousFeatureWithoutNamesakes() {
        val actual = GeocodingService.createAmbiguousMessage(listOf(FEATURE_WITHOUT_NAMESAKES))

        val expected = createErrorMessage()
        appendNoObjectMessageHead(expected, FEATURE_WITHOUT_NAMESAKES)
        expected.append(".").append("\n")

        assertEquals(expected.toString(), actual)
    }

    @Test
    fun createMessageForMultipleAmbiguousFeatures() {
        val actual = GeocodingService.createAmbiguousMessage(
            listOf(FEATURE_WITH_UNORDERED_NAMESAKES, FEATURE_WITHOUT_NAMESAKE_EXAMPLES, FEATURE_WITHOUT_NAMESAKES)
        )

        val expected = createErrorMessage()
        appendMultipleMessageHead(expected, FEATURE_WITH_UNORDERED_NAMESAKES)
        expected.append(":").append("\n")
        appendNamesake(expected, FIRST_NAMESAKE, SECOND_NAMESAKE, THIRD_NAMESAKE)
        appendMultipleMessageHead(expected, FEATURE_WITHOUT_NAMESAKE_EXAMPLES)
        expected.append(".").append("\n")
        appendNoObjectMessageHead(expected, FEATURE_WITHOUT_NAMESAKES)
        expected.append(".").append("\n")

        assertEquals(expected.toString(), actual)
    }

    @Test
    fun errorResponseTest() {
        val transport = Mockito.mock(GeoTransport::class.java)
        val a = SimpleAsync<GeoResponse>()

        val request = GeoRequestBuilder.ExplicitRequestBuilder()
            .setIds(listOf("1"))
            .addFeature(GeoRequest.FeatureOption.FRAGMENTS)
            .build()

        `when`(transport.send(request)).thenReturn(a)

        val f = GeocodingService(transport)
            .execute(request)
            .map<Any> { listOf("hello", "world") }

        val failure = AtomicBoolean()
        f.onFailure { failure.set(true) }

        a.success(GeoResponse.ErrorGeoResponse("blabla"))

        assertThat(failure).isTrue()
    }

    @Test
    fun simpleDuplicateRequestRestore() {
        assertThatRequest(BAR, FOO, FOO)
            .withResponse(BAR, FOO).produces(BAR, FOO, FOO)
    }

    @Test
    fun swapDuplicateRequestRestore() {
        assertThatRequest(FOO, FOO, FOO, BAR)
            .withResponse(BAR, FOO).produces(FOO, FOO, FOO, BAR)
    }

    @Test
    fun mixDuplicateRequestRestore() {
        assertThatRequest(FOO, FOO, BAR, FOO, BAR, BAZ)
            .withResponse(BAR, BAZ, FOO).produces(FOO, FOO, BAR, FOO, BAR, BAZ)
    }

    @Test
    fun skipDuplicateRequestRestore() {
        assertThatRequest(FOO, BAR, BAZ, FOO, BAR)
            .withResponse(BAZ, FOO).produces(FOO, BAZ, FOO)
    }

    @Test
    fun duplicateTemplateListForDuplicateRequestRestore() {
        assertThatRequest(FOO, BAR, BAZ, FOO, BAR)
            .withResponse(FOO, BAR, BAZ, FOO, BAZ).produces(FOO, BAR, BAZ, FOO, BAR)
    }

    @Test
    fun simpleEmptyRequestRestore() {
        assertThatRequest()
            .withResponse(FOO, BAR, BAZ).produces(FOO, BAR, BAZ)
    }

    @Test
    fun emptyRequestRestore() {
        assertThatRequest()
            .withResponse(FOO, FOO, BAR, BAZ, BAR).produces(FOO, FOO, BAR, BAZ, BAR)
    }

    internal class RequestReponseLeftJoinAssert(requestedString: List<String>) :
        AbstractAssert<RequestReponseLeftJoinAssert, List<String>>(requestedString, RequestReponseLeftJoinAssert::class.java) {
        private lateinit var responsedStrings: List<String>

        fun withResponse(vararg values: String): RequestReponseLeftJoinAssert {
            isNotNull()

            responsedStrings = listOf(*values)

            return this
        }

        fun produces(vararg values: String): RequestReponseLeftJoinAssert {
            isNotNull()

            val transport = Mockito.mock(GeoTransport::class.java)
            val request = GeoRequestBuilder.ExplicitRequestBuilder().setIds(actual).build()
            val responseAsync = SimpleAsync<GeoResponse>()

            `when`(transport.send(request)).thenReturn(responseAsync)

            GeocodingService(transport)
                .execute(request)
                .onSuccess {
                    assertThat(it.map(GeocodedFeature::id)).containsExactly(*values)
                }

            responseAsync.success(
                SuccessResponseBuilder().apply {
                    responsedStrings
                    .map { GeocodedFeature(it, it, null, null, null, null, null, null, null)}
                    .forEach { addGeocodingAnswer(GeocodingAnswerBuilder().addGeocodedFeature(it).build()) }
            }.build()

            )
            return this
        }
    }

    companion object {
        private fun NamesakeBuilder.addParent(name: String, level: FeatureLevel): NamesakeBuilder {
            return addParentName(name).addParentLevel(level)
        }

        private val FIRST_NAMESAKE = NamesakeBuilder()
            .setName("ABC Greenville")
            .addParent("USA", FeatureLevel.STATE)
            .addParent("Utah", FeatureLevel.STATE)
            .build()
        private val SECOND_NAMESAKE = NamesakeBuilder()
            .setName("Greenville")
            .addParent("USA", FeatureLevel.STATE)
            .addParent("Alaska", FeatureLevel.STATE)
            .build()
        private val THIRD_NAMESAKE = NamesakeBuilder()
            .setName("Greenville")
            .addParent("USA", FeatureLevel.STATE)
            .addParent("Texas", FeatureLevel.STATE)
            .build()
        private val FEATURE_WITH_UNORDERED_NAMESAKES = GeoResponseBuilder.AmbiguousFeatureBuilder()
            .setQuery("Greenville")
            .setTotalNamesakeCount(49)
            .addNamesakeExample(THIRD_NAMESAKE)
            .addNamesakeExample(FIRST_NAMESAKE)
            .addNamesakeExample(SECOND_NAMESAKE)
            .build()

        private val FEATURE_WITHOUT_NAMESAKE_EXAMPLES = GeoResponseBuilder.AmbiguousFeatureBuilder()
            .setQuery("Springfield")
            .setTotalNamesakeCount(15)
            .build()

        private val FEATURE_WITHOUT_NAMESAKES = GeoResponseBuilder.AmbiguousFeatureBuilder()
            .setQuery("Noname City")
            .setTotalNamesakeCount(0)
            .build()
        private const val FOO = "foo"
        private const val BAR = "bar"
        private const val BAZ = "baz"

        private fun createErrorMessage(): StringBuilder {
            return StringBuilder().append("Geocoding errors:\n")
        }

        private fun appendMultipleMessageHead(string: StringBuilder, ambiguousFeature: GeoResponse.AmbiguousGeoResponse.AmbiguousFeature) {
            string
                .append("Multiple objects (" + ambiguousFeature.namesakeCount + ") ")
                .append("were found for '" + ambiguousFeature.request + "'")
        }

        private fun appendNoObjectMessageHead(string: StringBuilder, ambiguousFeature: GeoResponse.AmbiguousGeoResponse.AmbiguousFeature) {
            string.append("No objects were found for '" + ambiguousFeature.request + "'")
        }

        private fun appendNamesake(string: StringBuilder, vararg namesakes: GeoResponse.AmbiguousGeoResponse.Namesake) {
            for (namesake in namesakes) {
                string
                    .append("- " + namesake.name)
                    .append(namesake.parents.joinToString(prefix = "(", transform = {it.name}, separator = ", ", postfix = ")" ))
                    .append("\n")
            }
        }

        private fun assertThatRequest(vararg values: String): RequestReponseLeftJoinAssert {
            return RequestReponseLeftJoinAssert(listOf(*values))
        }
    }

}