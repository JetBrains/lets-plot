/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.transform

class PlotSpecTransform private constructor(builder: Builder) {

    /*
  private static boolean containSpecs(List<?> list) {
    return list.stream().anyMatch((Predicate<Object>) o -> o instanceof Map || o instanceof List);
  }
  */

    private val myMakeCleanCopy: Boolean
    private val mySpecChanges: MutableMap<SpecSelector, List<SpecChange>>

    init {
        myMakeCleanCopy = builder.myMakeCleanCopy
        mySpecChanges = HashMap()
        for ((key, list) in builder.mySpecChanges) {
            check(list.isNotEmpty())
            mySpecChanges[key] = list
        }
    }

    fun apply(spec: MutableMap<*, *>): MutableMap<String, Any> {
        val result: MutableMap<String, Any> = if (myMakeCleanCopy) {
            PlotSpecCleaner.apply(spec)
        } else {
            @Suppress("UNCHECKED_CAST")
            spec as MutableMap<String, Any>
        }

        val ctx = object : SpecChangeContext {
            override fun getSpecsAbsolute(vararg keys: String): List<Map<String, Any>> {
                val finder = SpecFinder(keys.toList())
                val list = finder.findSpecs(result)
                @Suppress("UNCHECKED_CAST")
                return list as List<Map<String, Any>>
            }
        }
        val rootSel = SpecSelector.root()
        applyChangesToSpec(rootSel, result, ctx)
        return result
    }

    private fun applyChangesToSpec(sel: SpecSelector, spec: MutableMap<String, Any>, ctx: SpecChangeContext) {
        // traverse depth-first all sub-options (including elements of lists)
        for (key in spec.keys) {
            val v = spec[key]!!
            val subSel = sel.with().part(key).build()
            applyChangesToValue(subSel, v, ctx)
        }

        // apply changes to this spec
        val specChanges = applicableSpecChanges(sel, spec)
        for (change in specChanges) {
            change.apply(spec, ctx)
        }
    }

    private fun applyChangesToValue(sel: SpecSelector, v: Any?, ctx: SpecChangeContext) {
        if (v is Map<*, *>) {
            @Suppress("UNCHECKED_CAST")
            val spec = v as MutableMap<String, Any>
            applyChangesToSpec(sel, spec, ctx)
        } else if (v is List<*>) {
            for (o in v) {
                applyChangesToValue(sel, o, ctx)
            }
        }
    }

    private fun applicableSpecChanges(path: SpecSelector, spec: Map<String, Any>): List<SpecChange> {
        if (mySpecChanges.containsKey(path)) {
            val result = ArrayList<SpecChange>()
            val changes = mySpecChanges[path]!!
            for (change in changes) {
                if (change.isApplicable(spec)) {
                    result.add(change)
                }
            }
            return result
        }

        return emptyList()
    }


    class Builder internal constructor(internal val myMakeCleanCopy: Boolean) {

        internal val mySpecChanges = HashMap<SpecSelector, MutableList<SpecChange>>()

        fun change(sel: SpecSelector, handler: SpecChange): Builder {
            if (!mySpecChanges.containsKey(sel)) {
                mySpecChanges[sel] = ArrayList()
            }
            mySpecChanges[sel]!!.add(handler)
            return this
        }

        fun build(): PlotSpecTransform {
            return PlotSpecTransform(this)
        }
    }

    companion object {
        fun builderForRawSpec(): Builder {
            return Builder(true)
        }

        fun builderForCleanSpec(): Builder {
            return Builder(false)
        }
    }
}
