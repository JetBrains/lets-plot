/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.ui

import org.jetbrains.letsPlot.core.canvas.Canvas
import org.jetbrains.letsPlot.core.canvas.CanvasProvider

class ResourceManager(private val canvasProvider: CanvasProvider) {
    private val snapshotMap: MutableMap<String, Canvas.Snapshot> = HashMap()

    init {
        add(KEY_PLUS, BUTTON_PLUS)
        add(KEY_PLUS_DISABLED, BUTTON_PLUS_DISABLED)
        add(KEY_MINUS, BUTTON_MINUS)
        add(KEY_MINUS_DISABLED, BUTTON_MINUS_DISABLED)
        add(KEY_GET_CENTER, BUTTON_GET_CENTER)
        add(KEY_MAKE_GEOMETRY, BUTTON_MAKE_GEOMETRY)
        add(KEY_MAKE_GEOMETRY_ACTIVE, BUTTON_MAKE_GEOMETRY_ACTIVE)
        add(KEY_DROP_OPEN, BUTTON_DROP_OPEN)
        add(KEY_DROP_CLOSE, BUTTON_DROP_CLOSE)
        add(KEY_RESET_POSITION, BUTTON_RESET_POSITION)

    }

    private fun add(key: String, dataUrl: String): ResourceManager {
        canvasProvider.createSnapshot(dataUrl).onResult(
            { snapshot -> snapshotMap[key] = snapshot },
            { message -> error(message) })
        return this
    }

    operator fun get(key: String): Canvas.Snapshot {
        return snapshotMap[key]!!
    }

    fun isReady(): Boolean = listOf(
        KEY_PLUS,
        KEY_PLUS_DISABLED,
        KEY_MINUS,
        KEY_MINUS_DISABLED,
        KEY_GET_CENTER,
        KEY_MAKE_GEOMETRY,
        KEY_MAKE_GEOMETRY_ACTIVE,
        KEY_DROP_OPEN,
        KEY_DROP_CLOSE,
        KEY_RESET_POSITION,
    ).all { snapshotMap.containsKey(it) }

    companion object {
        const val KEY_PLUS = "img_plus"
        const val KEY_PLUS_DISABLED = "img_plus_disable"
        const val KEY_MINUS = "img_minus"
        const val KEY_MINUS_DISABLED = "img_minus_disable"
        const val KEY_GET_CENTER = "img_get_center"
        const val KEY_MAKE_GEOMETRY = "img_create_geometry"
        const val KEY_MAKE_GEOMETRY_ACTIVE = "img_create_geometry_active"
        const val KEY_DROP_OPEN = "img_drop_open"
        const val KEY_DROP_CLOSE = "img_drop_close"
        const val KEY_RESET_POSITION = "img_reset_position"

        private const val BUTTON_PLUS =
            ("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADQAAAA0CAMAAADypuvZAAAAUVBMVEUAAADf39/f39/n5+fk5OTk5OTl5e"
                    + "Xl5eXk5OTm5ubl5eXl5eXm5uYAAAAQEBAgICCfn5+goKDl5eXo6Oj29vb39/f4+Pj5+fn9/f3+/v7///8nQ8gkAAAADXRSTlMAECAgX2B/gL+/z9/fDLiFVAAAAKJJREFUeNrt1tEOwi"
                    + "AMheGi2xQ2KBzc3Hj/BxXv5K41MTHKf/+lCSRNichcLMS5gZ6dF6iaTxUtyPejSFszZkMjciXy9oyJHNaiaoMloOjaAT0qHXX0WRQDJzVi74Ma+drvoBj8S5xEiH1TEKHQIhahyM2g9I"
                    + "//1L4hq1HkkPqO6OgL0aFHFpvO3OBo0h9UA5kFeZWTLWN+80isjU5OrpMhegCRuP2dffXKGwAAAABJRU5ErkJggg==")

        private const val BUTTON_MINUS =
            ("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADQAAAA0CAMAAADypuvZAAAAUVBMVEUAAADf39/f39/n5+fk5OTk5OTl5"
                    + "eXl5eXk5OTm5ubl5eXl5eXm5uYAAAAQEBAgICCfn5+goKDl5eXo6Oj29vb39/f4+Pj5+fn9/f3+/v7///8nQ8gkAAAADXRSTlMAECAgX2B/gL+/z9/fDLiFVAAAAI1JREFUeNrt1rEOw"
                    + "jAMRdEXaAtJ2qZ9JqHJ/38oYqObzYRQ7n5kS14MwN081YUB764zTcULgJnyrE1bFkaHkVKboUM4ITA3U4UeZLN1kHbUOuqoo19E27p8lHYVSsupVYXWM0q69dJp0N6P21FHf4OqHXkWm"
                    + "3kwYLI/VAPcTMl6UoTx2ycRGIOe3CcHvAAlagACEKjXQgAAAABJRU5ErkJggg==")

        private const val BUTTON_MINUS_DISABLED =
            ("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADQAAAA0CAYAAADFeBvrAAAABmJLR0QA/wD/AP+gvaeTAAAA"
                    + "CXBIWXMAABYlAAAWJQFJUiTwAAAAB3RJTUUH4wYTDA80Pt7fQwAAAaRJREFUaN7t2jFqAkEUBuB/xt1XiKwGwWqLbBBSWecEtltEG61yg+QCabyBrZU2Wm2jp0gn2McUCxJBcEUXdpQx"
                    + "RbIJadJo4WzeX07x4OPNNMMv8JX5fF4ioqcgCO4dx6nBgMRx/Or7fsd13UF6JgBgsVhcTyaTFyKqwMAopZb1ev3O87w3AQC9Xu+diCpSShQKBViWBSGECRDsdjtorVPUrQzD8CHFlEol"
                    + "2LZtBAYAiAjFYhFSShBRhYgec9VqNbBt+yrdjGkRQsCyLCRJgul0Wpb5fP4m1ZqaXC4HAHAcpyaRgUj5w8gE6BeOQQxiEIMYxCAGMYhBDGIQg/4p6CyfCMPhEKPR6KQZrVYL7Xb7MjZ0"
                    + "KuZcM/gN/XVdLmEGAIh+v38EgHK5bPRmVqsVXzkGMYhBDGIQgxjEIAYxiEEMyiToeDxmA7TZbGYAcDgcjEUkSQLgs24mG41GAADb7dbILWmtEccxAMD3/Y5USnWVUkutNdbrNZRSxkD2"
                    + "+z2iKPqul7muO8hmATBNGIYP4/H4OW1oXXqiKJo1m81AKdX1PG8NAB90n6KaLrmkCQAAAABJRU5ErkJggg==")

        private const val BUTTON_PLUS_DISABLED =
            ("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADQAAAA0CAYAAADFeBvrAAAABmJLR0QA/wD/AP+gvaeTAAAAC"
                    + "XBIWXMAABYlAAAWJQFJUiTwAAAAB3RJTUUH4wYTDBAFolrR5wAAAdlJREFUaN7t2j9v2kAYBvDnDvsdEDJUSEwe6gipU+Z+AkZ7KCww5Rs0XyBLvkFWJrIckxf8KbohZS8dLKFGQsIIL"
                    + "PlAR4fE/adEaiWScOh9JsuDrZ/v7hmsV+Axs9msQUSXcRx/8jzvHBYkz/OvURRd+75/W94TADCfz98nSfKFiFqwMFrr+06n8zEIgm8CAIbD4XciakkpUavV4DgOhBA2QLDZbGCMKVEfZ"
                    + "JqmFyWm0WjAdV0rMABARKjX65BSgohaRPS50m63Y9d135UrY1uEEHAcB0VRYDqdNmW1Wj0rtbamUqkAADzPO5c4gUj5i3ESoD9wDGIQgxjEIAYxyCKQUgphGCIMQyil7AeNx+Mnr3nLM"
                    + "YhBDHqVHOQnglLqnxssDMMn7/f7fQwGg+NYoUPU8aEqnc/Qc9vlGJ4BAGI0Gu0BoNlsvsgX+/vMJEnyIu9ZLBa85RjEIAa9Aej3Oj5UNb9pbb9WuLYZxCAGMYhBDGLQf4D2+/1pgFar1"
                    + "R0A7HY7axFFUQB4GDeT3W43BoD1em3lKhljkOc5ACCKomuptb7RWt8bY7BcLqG1tgay3W6RZdnP8TLf929PcwCwTJqmF5PJ5Kqc0Dr2ZFl21+v1Yq31TRAESwD4AcX3uBFfeFCxAAAAA"
                    + "ElFTkSuQmCC")

        private const val BUTTON_GET_CENTER =
            ("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADQAAAA0CAYAAADFeBvrAAAABmJLR0QA/wD/AP+gvaeTAAAACXBI"
                    + "WXMAABYlAAAWJQFJUiTwAAAAB3RJTUUH4wYcCCsV3DWWMQAAAc9JREFUaN7tmkGu2jAQhv+xE0BsEjYsgAW5Ae8Ej96EG7x3BHIDeoSepNyg3CAsQtgGNkFGeLp4hNcu2kIaXnE6vxQp"
                    + "ika2P2Xs8YyGcFaSJGGr1XolomdmnsINrZh5MRqNvpQfCAC22+2Ymb8y8xhuam2M+RRF0ZoAIMuyhJnHWmv0ej34vg8ieniKw+GA3W6H0+lUQj3pNE1nAGZaa/T7fXie5wQMAHieh263"
                    + "i6IowMyh1vqgiOgFAIIgcAbkRymlEIbh2/4hmioAEwDodDpwVb7vAwCYearQACn1jtEIoJ/gBKgpQHEcg4iueuI4/vDxLjeFzWbDADAYDH5veOORzswfOl6WZbKHrtZ8Pq/Fpooqu9yf"
                    + "XOCvF3bjfOJyAiRAAiRAv4wb94ohdcx3dRx6dEkcEiABEiAB+n9qCrfk+FVVdb5KCR4RwVrbnATv3tmq7CEBEiAB+vdA965tV16X1LabWFOow7bu8aSmIMe2ANUM9Mg36JuAiGgJAMYY"
                    + "ZyGKoihfV4qZlwCQ57mTf8lai/1+X3rZgpIkCdvt9reyvSwIAif6fqy1OB6PyPP80l42HA6jZjYAlkrTdHZuN5u4QMHMSyJaGmM+R1GUA8B3Hdvtjp1TGh0AAAAASUVORK5CYII=")


        private const val BUTTON_MAKE_GEOMETRY =
            "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADQAAAA0CAMAAADypuvZAAAAQlBMVEUAAADf39/n5+fm5ubm5ubm5ubm5u" +
                    "YAAABvb29wcHB/f3+AgICPj4+/v7/f39/m5ubv7+/w8PD8/Pz9/f3+/v7////uOQjKAAAAB3RSTlMAICCvw/H3O5ZWYwAAAK" +
                    "ZJREFUeAHt1sEOgyAQhGEURMWFsdR9/1ctddPepwlJD/z3LyRzIOvcHCKY/NTMArJlch6PS4nqieCAqlRPxIaUDOiPBhooix" +
                    "QWpbWVOFTWu0whMST90WaoMCiZOZRAb7OLZCVQ+jxCIDMcMsMhMwTKItttCPQdmkDFzK4MEkPSH2VDhUJ62Awc0iKS//Q3Gm" +
                    "igiIsztaGAszLmOuF/OxLd7CkSw+RetQbMcCdSSXgAAAAASUVORK5CYII="

        private const val BUTTON_MAKE_GEOMETRY_ACTIVE =
            "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADQAAAA0CAMAAADypuvZAAAAflBMVEUAAACfv9+fv+eiv+aiwOajwOajv+" +
                    "ajwOaiv+ajv+akwOakweaiv+aoxu+ox++ox/Cx0fyy0vyz0/2z0/601P+92f++2v/G3v/H3v/H3//U5v/V5//Z6f/Z6v/a6f" +
                    "/a6v/d7P/f7f/n8f/o8f/o8v/s9P/6/P/7/P/7/f////8N3bWvAAAADHRSTlMAICCvr6/Dw/Hx9/cE8gnKAAABCUlEQVR42t" +
                    "XW2U7DMBAFUIcC6TJ0i20oDnRNyvz/DzJtJCJxkUdTqUK5T7Gs82JfTezcQzkjS54KMRMyZly4R1pU3pDVnEpHtPKmrGkqyB" +
                    "tDNBgUmy9mrrtFLZ+/VoeIKArJIm4joBNriBtArKP2T+QzYck/olqSMf2+frmblKK1EVuWfNpQ5GveTCh16P3+aN+hAChz5N" +
                    "u+S/0+XC6aXUqvSiPA1JYaodERGh2h0ZH0bQ9GaXl/0ErLsW87w9yD6twRbbBvOvIfeAw68uGnb5BbBsvQhuVZ/wEganR0AB" +
                    "TOGmoDIB+OWdQ2YUhPAjuaUWUzS0ElzZcWU73Q6IZH4uTytByZyPS5cN9XNuQXxwNiAAAAAABJRU5ErkJggg=="

        private const val BUTTON_DROP_OPEN =
            "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADQAAAA0CAIAAABKGoy8AAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAAGdSURBVGhD7dntjYJAFIXhaYAKtCopBKsyNEAF2Mcm1sDHH8Pucc+5JiascgcSJpt5/6gXyDwRTEwmsKIozudzXddfu9Y0zel0kokdj8fr9arjCdS27eFwEI6y2+02juM0Td/71fc9GPThZoayLCnbl/Xsfr/TV1VVwG3GO5B1MIGGYQAJP4CAF6RxGuHLoypFHKIq4/xRlXH+qMo4f1RlnD+qMs4fVRnnj6p/hNNf5xD02ZOuXHYtVZE4pNGydM1vGr2Nqngc0vRTOtvS9G1UuZ85rWBp+nc6z9L0U1TF/CC0jqXpXDrD0nRBVMXgkFazNH1NxyxNl0VVJA5pTUtTS1NL08VRFY9DWtnSdLUMUbUKh7S+NTuJiKq1OCTFXDrDH1Ub4JAsr+lYVFRtg0MSWZrGRtVmOCTXahmiakvchlGVcf6oyjh/VGWcP6oyzh9VGeePqozzR5VwiewtPaMqcBtzGAaNE6jrOpCapglVVeFdghtzj43XoiieW5r7bs+BhRtIWdu2+uOa9GYwK8uS25s7drlc8Jg9toFRCD/ctmQxVDQOVgAAAABJRU5ErkJggg=="

        private const val BUTTON_DROP_CLOSE =
            "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADQAAAA0CAIAAABKGoy8AAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAAHHSURBVGhD7dk/isJAGIfh/EEFMZVYaWHtBbTxAraxsNbreIVYW9l6AUG8QFoLtVMbiagQ3F/8hmVxd13nmyHJwryFzsSID5MEA7GtR57njUajTqfTbrdpSyaFYRgEwWw2o2mCazQa0+m0Xq/TpszbbreDwWC32yW4xWIBmeu6tVqtVCrZtljO9Dufz4fDIY5j+Hq9nuv7fr/fhwzrVygUMpQhACqVShRFOM2u16s7Ho+xYKhYLIpdMs1xHEjgwzLZ6/Uam5rNJn2Wh3BYN5sNBg7NcxXOMRrkEfeZwXEzOG4Gx83guBkcN4PjphOX3IE9EnPltOG+mnT59OC+a7T4NOB+c6j7VHFPgvv9LkaPFH1KuB9lGn183Is10+Vj4l7IKC0+Du5PGaXuk8a9KaMUfXI4KRml4uNfEO/IqPf3fIqJk/09nk8Oh9+gxFwm8U2Z7/IPawoZHDeD42Zw3AyOm8Fx+w84qf/j1HJWqxXeLpcLzfNQFEV4DcPQWS6XGO33+5wsXhzHx+MRgyAIbM/z5vM5PdKsVqvlcpl2Sj+wbrcblokeaXa73eSOPtcPgynf94fDYavVEvMswjmGa2AymZxOJ8uyPgB5hsE2P1wizgAAAABJRU5ErkJggg=="

        private const val BUTTON_RESET_POSITION =
            "data:image/png;base64,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"

    }
}
