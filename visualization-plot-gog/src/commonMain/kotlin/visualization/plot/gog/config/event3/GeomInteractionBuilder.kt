package jetbrains.datalore.visualization.plot.gog.config.event3

import jetbrains.datalore.visualization.plot.gog.core.event3.GeomTargetLocator.LookupSpace
import jetbrains.datalore.visualization.plot.gog.core.event3.GeomTargetLocator.LookupStrategy
import jetbrains.datalore.visualization.plot.gog.core.render.Aes
import observable.collections.Collections.unmodifiableList

class GeomInteractionBuilder(private val mySupportedAesList: List<Aes<*>>) {
    var locatorLookupSpace: LookupSpace? = null
        private set
    var locatorLookupStrategy: LookupStrategy? = null
        private set
    private var myAxisTooltipVisibilityFromFunctionKind: Boolean = false
    private var myAxisTooltipVisibilityFromConfig: Boolean? = null
    private var myAxisAesFromFunctionKind: List<Aes<*>>? = null
    private var myAxisAesFromConfig: List<Aes<*>>? = null

    val axisAes: List<Aes<*>>?
        get() = if (myAxisAesFromConfig == null)
            myAxisAesFromFunctionKind
        else
            myAxisAesFromConfig

    val displayableAes: List<Aes<*>>
        get() = if (myAxisAesFromFunctionKind != null) {
            exclude(mySupportedAesList, myAxisAesFromFunctionKind!!)
        } else mySupportedAesList

    val isAxisTooltipEnabled: Boolean
        get() = if (myAxisTooltipVisibilityFromConfig == null)
            myAxisTooltipVisibilityFromFunctionKind
        else
            myAxisTooltipVisibilityFromConfig!!

    fun showAxisTooltip(isTrue: Boolean): GeomInteractionBuilder {
        myAxisTooltipVisibilityFromConfig = isTrue
        return this
    }

    fun multilayerLookupStrategy(): GeomInteractionBuilder {
        locatorLookupStrategy = LookupStrategy.NEAREST
        locatorLookupSpace = LookupSpace.XY
        return this
    }

    fun univariateFunction(lookupStrategy: LookupStrategy): GeomInteractionBuilder {
        myAxisAesFromFunctionKind = AES_X
        locatorLookupStrategy = lookupStrategy
        myAxisTooltipVisibilityFromFunctionKind = true
        locatorLookupSpace = LookupSpace.X

        return this
    }

    fun bivariateFunction(area: Boolean): GeomInteractionBuilder {
        myAxisAesFromFunctionKind = AES_XY

        if (area) {
            locatorLookupStrategy = LookupStrategy.HOVER
            myAxisTooltipVisibilityFromFunctionKind = false
        } else {
            locatorLookupStrategy = LookupStrategy.NEAREST
            myAxisTooltipVisibilityFromFunctionKind = true
        }
        locatorLookupSpace = LookupSpace.XY

        return this
    }

    fun none(): GeomInteractionBuilder {
        myAxisAesFromFunctionKind = ArrayList(mySupportedAesList)
        locatorLookupStrategy = LookupStrategy.NONE
        myAxisTooltipVisibilityFromFunctionKind = true
        locatorLookupSpace = LookupSpace.NONE

        return this
    }

    fun build(): GeomTargetInteraction {
        return GeomTargetInteraction(this)
    }

    fun axisAes(axisAes: List<Aes<*>>): GeomInteractionBuilder {
        myAxisAesFromConfig = unmodifiableList(ArrayList(axisAes))
        return this
    }

    companion object {
        val AREA_GEOM = true
        val NON_AREA_GEOM = false

        private val AES_X = listOf(Aes.X)
        private val AES_XY = listOf(Aes.X, Aes.Y)

        private fun exclude(list: List<Aes<*>>, itemsToRemove: List<Aes<*>>): List<Aes<*>> {
            val visibleAes = ArrayList(list)
            visibleAes.removeAll(itemsToRemove)

            return visibleAes
        }
    }
}
