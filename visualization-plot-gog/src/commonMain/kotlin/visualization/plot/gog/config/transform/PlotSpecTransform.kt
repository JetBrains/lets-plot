package jetbrains.datalore.visualization.plot.gog.config.transform

import jetbrains.datalore.base.gcommon.base.Preconditions.checkState

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
            checkState(list.isNotEmpty())
            mySpecChanges[key] = list
        }
    }

    fun apply(spec: Map<*, *>): Map<String, Any> {
        val result: MutableMap<String, Any>
        result = if (myMakeCleanCopy) {
            PlotSpecCleaner.apply(spec)
        } else {
            val properSpec = spec as MutableMap<String, Any> // must have been cleaned already
            properSpec
        }

        val ctx = object : SpecChangeContext {
            override fun getSpecsAbsolute(vararg keys: String): List<Map<String, Any>> {
                val finder = SpecFinder(keys.toList())
                val list = finder.findSpecs(result)
                return list as List<Map<String, Any>>
            }
        }
        val rootSel = SpecSelector.root()
        applyChangesToSpec(rootSel, result, ctx)
        return result
    }

    /*
  private Map<String, Object> cleanCopyOfMap(Map<?, ?> in) {
    // - drops key-value pair if value is null
    // - converts all keys to strings
    Map<String, Object> out = new HashMap<>();
    for (Object k : in.keySet()) {
      Object v = in.get(k);
      if (v != null) {
        String key = String.valueOf(k);
        out.put(key, cleanValue(v));
      }
    }
    return out;
  }
  */

    /*
  private Object cleanValue(Object v) {
    if (v instanceof Map) {
      return cleanCopyOfMap((Map<?, ?>) v);
    } else if (v instanceof List) {
      return cleanList((List<?>) v);
    }
    return v;
  }
  */

    /*
  private List<?> cleanList(List<?> in) {
    if (!containSpecs(in)) {
      // do not change data vectors
      return in;
    }
    List<Object> copy = new ArrayList<>(in.size());
    for (Object o : in) {
      copy.add(cleanValue(o));
    }
    return copy;
  }
  */

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

    private fun applyChangesToValue(sel: SpecSelector, v: Any, ctx: SpecChangeContext) {
        if (v is Map<*, *>) {
            val spec = v as MutableMap<String, Any>
            applyChangesToSpec(sel, spec, ctx)
        } else if (v is List<*>) {
            for (o in v) {
                applyChangesToValue(sel, o!!, ctx)
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
