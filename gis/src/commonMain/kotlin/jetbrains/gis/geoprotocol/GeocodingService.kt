package jetbrains.gis.geoprotocol

import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.async.Asyncs
import jetbrains.datalore.base.function.Consumer
import jetbrains.gis.geoprotocol.GeoRequest.*
import jetbrains.gis.geoprotocol.GeoResponse.*
import jetbrains.gis.geoprotocol.GeoResponse.AmbiguousGeoResponse.AmbiguousFeature
import jetbrains.gis.geoprotocol.GeoResponse.AmbiguousGeoResponse.NamesakeParent
import jetbrains.gis.geoprotocol.GeoResponse.SuccessGeoResponse.GeocodedFeature


class GeocodingService(private val myTransport: GeoTransport) {

    fun execute(request: GeoRequest): Async<List<GeocodedFeature>> {
        val requestedStrings: List<String>
        if (request is ExplicitSearchRequest) {
            requestedStrings = request.getIds()
        } else if (request is GeocodingSearchRequest) {
            requestedStrings = request
                .getQueries().stream().flatMap(
                    { regionQuery -> regionQuery.getNames().stream() }
                )
                .collect(Collectors.toList())
        } else if (request is ReverseGeocodingSearchRequest) {
            requestedStrings = emptyList()
        } else {
            return Asyncs.failure(IllegalStateException("Unkown request type: " + request.getClass().getName()))
        }

        val duplicateStorage = DuplicateStorage(requestedStrings)
        return myTransport
            .send(request)
            .map(
                { response ->
                    if (response is SuccessGeoResponse) {
                        return@myTransport
                            .send(request)
                            .map duplicateStorage . restoreDuplicateList (response as SuccessGeoResponse).getFeatures(), Function<T, String>({ GeocodedFeature.getRequest() }))
                    } else if (response is AmbiguousGeoResponse) {
                        throw RuntimeException(createAmbiguousMessage((response as AmbiguousGeoResponse).getFeatures()))
                    } else if (response is ErrorGeoResponse) {
                        throw RuntimeException("GIS error: " + (response as ErrorGeoResponse).getMessage())
                    } else {
                        throw IllegalStateException("Unknown response status: " + response.getClass().getSimpleName())
                    }
                }
            )
    }

    internal class DuplicateStorage(objectKeys: List<String>) {
        private val myKeys: List<String>
        val uniqueObjectKeys: Set<String> = LinkedHashSet()

        init {
            objectKeys.forEach(Consumer<String> { uniqueObjectKeys.add(it) })
            myKeys = unmodifiableList<String>(objectKeys)
        }

        fun <T> restoreDuplicateList(uniqueObjectList: List<T>, getObjectKey: Function<T, String>): List<T> {
            val uniqueObjectMap = HashMap<String, T>()
            uniqueObjectList.forEach { t -> uniqueObjectMap[getObjectKey.apply(t)] = t }

            val duplicateObjectList = ArrayList<T>()
            if (myKeys.isEmpty()) {
                duplicateObjectList.addAll(uniqueObjectList)
            } else {
                myKeys.forEach { key ->
                    if (uniqueObjectMap.containsKey(key)) {
                        duplicateObjectList.add(uniqueObjectMap[key])
                    }
                }
            }
            return duplicateObjectList
        }
    }

    companion object {
        internal fun createAmbiguousMessage(ambiguousFeatures: List<AmbiguousFeature>): String {
            val message = StringBuilder().append("Geocoding errors:\n")
            ambiguousFeatures.forEach { ambiguousFeature ->
                if (ambiguousFeature.getNamesakeCount() === 1) {
                    return@ambiguousFeatures.forEach
                }

                if (ambiguousFeature.getNamesakeCount() > 1) {
                    message
                        .append("Multiple objects (" + ambiguousFeature.getNamesakeCount() + ") ")
                        .append("were found for '" + ambiguousFeature.getRequest() + "'")

                    val lines = ArrayList<String>()
                    ambiguousFeature.getNamesakes().forEach { namesake ->
                        val line = StringBuilder("- " + namesake.getName())
                        if (!namesake.getParents().isEmpty()) {
                            line.append(
                                " (" + String.join(
                                    ", ",
                                    Lists.transform(namesake.getParents(), ??? { NamesakeParent.getName() }))+")")
                        }
                        lines.add(line.toString())
                    }

                    lines.sort(Comparator.naturalOrder<String>())
                    message.append(if (lines.isEmpty()) "." else ":")
                    lines.forEach { line -> message.append("\n" + line) }
                } else {
                    message.append("No objects were found for '" + ambiguousFeature.getRequest() + "'.")
                }
                message.append("\n")
            }

            return message.toString()
        }
    }

}
