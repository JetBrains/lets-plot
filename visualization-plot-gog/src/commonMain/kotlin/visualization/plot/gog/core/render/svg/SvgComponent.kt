package jetbrains.datalore.visualization.plot.gog.core.render.svg

import jetbrains.datalore.base.gcommon.base.Preconditions
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.observable.event.EventHandler
import jetbrains.datalore.base.registration.CompositeRegistration
import jetbrains.datalore.base.registration.Registration
import jetbrains.datalore.visualization.base.svg.*
import kotlin.random.Random

abstract class SvgComponent {
    companion object {
        private val RANDOM = Random.Default
        private val USED_IDS = HashSet<String>()

        protected fun nextId(prefix: String): String {
            var id: String
            var l: Long = 0
            do {
                l += RANDOM.nextInt(10000).toLong()
                id = prefix + l
            } while (USED_IDS.contains(id))
            USED_IDS.add(id)
            return id
        }

        fun buildTransform(origin: DoubleVector, rotationAngle: Double): SvgTransform {
            val transformBuilder = SvgTransformBuilder()
            if (origin != DoubleVector.ZERO) {
                transformBuilder.translate(origin.x, origin.y)
            }
            if (rotationAngle != 0.0) {
                transformBuilder.rotate(rotationAngle)
            }
            return transformBuilder.build()
        }
    }


    private var myIsBuilt: Boolean = false
    private var myIsBuilding: Boolean = false
    private val myRootGroup = SvgGElement()
    private val myChildComponents = ArrayList<SvgComponent>()
    private var myOrigin = DoubleVector.ZERO
    private var myRotationAngle = 0.0
    private var myCompositeRegistration = CompositeRegistration()

    protected val childComponents: List<SvgComponent>
        get() {
            Preconditions.checkState(myIsBuilt, "Plot has not yet built")
            return ArrayList(myChildComponents)
        }

    val rootGroup: SvgGElement
        get() {
            if (!(myIsBuilt || myIsBuilding)) {
                buildComponentIntern()
            }
            return myRootGroup
        }

    protected fun ensureBuilt() {
        rootGroup
    }

    private fun buildComponentIntern() {
        try {
            myIsBuilding = true
            buildComponent()
        } finally {
            myIsBuilding = false
            myIsBuilt = true
        }
    }

    protected abstract fun buildComponent()

    protected fun <EventT> rebuildHandler(): EventHandler<in EventT> {
        return object : EventHandler<EventT> {
            override fun onEvent(event: EventT) {
                needRebuild()
            }
        }
    }

    protected fun needRebuild() {
        if (myIsBuilt) {
            clear()
            buildComponentIntern()
        }
    }

    protected fun reg(r: Registration) {
        myCompositeRegistration.add(r)
    }

    fun clear() {
        myIsBuilt = false
        for (child in myChildComponents) {
            child.clear()
        }
        myChildComponents.clear()
        myRootGroup.children().clear()
        myCompositeRegistration.remove()
        myCompositeRegistration = CompositeRegistration()
    }

    fun add(child: SvgComponent) {
        myChildComponents.add(child)
        add(child.rootGroup)
    }

    fun add(node: SvgNode) {
        myRootGroup.children().add(node)
    }

    fun moveTo(p: DoubleVector) {
        myOrigin = p
        myRootGroup.transform().set(buildTransform(myOrigin, myRotationAngle))
    }

    fun moveTo(x: Double, y: Double) {
        moveTo(DoubleVector(x, y))
    }

    /**
     * @param angle in degrees
     */
    fun rotate(angle: Double) {
        myRotationAngle = angle
        myRootGroup.transform().set(buildTransform(myOrigin, myRotationAngle))
    }

    fun toRelativeCoordinates(location: DoubleVector): DoubleVector {
        return rootGroup.pointToTransformedCoordinates(location)
    }

    fun toAbsoluteCoordinates(location: DoubleVector): DoubleVector {
        return rootGroup.pointToAbsoluteCoordinates(location)
    }

    protected fun defineClipPath(clipNode: SvgNode): SvgClipPathElement {
        val defs = SvgDefsElement()
        val clipPath = SvgClipPathElement()
        clipPath.id().set(nextId("clip"))
        clipPath.children().add(clipNode)
        defs.children().add(clipPath)
        add(defs)
        return clipPath
    }

    fun addClassName(className: String) {
        myRootGroup.addClass(className)
    }
}
