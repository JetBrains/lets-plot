/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.observable.property

import org.jetbrains.letsPlot.commons.intern.function.Predicate
import org.jetbrains.letsPlot.commons.intern.function.Runnable
import org.jetbrains.letsPlot.commons.intern.function.Supplier
import org.jetbrains.letsPlot.commons.intern.function.Value
import org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionAdapter
import org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionItemEvent
import org.jetbrains.letsPlot.commons.intern.observable.collections.ObservableCollection
import org.jetbrains.letsPlot.commons.intern.observable.event.EventHandler
import org.jetbrains.letsPlot.commons.intern.observable.event.EventSource
import org.jetbrains.letsPlot.commons.registration.Registration
import kotlin.jvm.JvmOverloads

object Properties {
    val TRUE = constant(true)
    val FALSE = constant(false)

    fun not(prop: ReadableProperty<out Boolean?>): ReadableProperty<out Boolean?> {
        return map(prop) { value -> if (value == null) null else !value }
    }

    fun <ValueT> notNull(prop: ReadableProperty<out ValueT?>): ReadableProperty<out Boolean> {
        return map(prop) { value -> value != null }
    }

    fun <ValueT> isNull(prop: ReadableProperty<out ValueT?>): ReadableProperty<out Boolean> {
        return map(prop) { value -> value == null }
    }

    fun startsWith(
        string: ReadableProperty<String?>,
        prefix: ReadableProperty<String?>
    ): ReadableProperty<out Boolean> {

        return object : DerivedProperty<Boolean>(false, string, prefix) {
            override val propExpr: String
                get() = "startsWith(" + string.propExpr + ", " + prefix.propExpr + ")"

            override fun doGet(): Boolean {
                if (string.get() == null) return false
                return if (prefix.get() == null) false else string.get()!!.startsWith(prefix.get()!!)
            }
        }
    }

    fun isNullOrEmpty(prop: ReadableProperty<String?>): ReadableProperty<out Boolean> {
        return object : DerivedProperty<Boolean>(false, prop) {

            override val propExpr: String
                get() = "isEmptyString(" + prop.propExpr + ")"

            override fun doGet(): Boolean {
                val `val` = prop.get()
                return `val` == null || `val`.isEmpty()
            }
        }
    }

    fun and(op1: ReadableProperty<out Boolean?>, op2: ReadableProperty<out Boolean?>): ReadableProperty<out Boolean?> {
        return object : DerivedProperty<Boolean?>(null, op1, op2) {

            override val propExpr: String
                get() = "(" + op1.propExpr + " && " + op2.propExpr + ")"

            override fun doGet(): Boolean? {
                return and(op1.get(), op2.get())
            }
        }
    }

    private fun and(b1: Boolean?, b2: Boolean?): Boolean? {
        if (b1 == null) {
            return andWithNull(b2)
        }
        return if (b2 == null) {
            andWithNull(b1)
        } else b1 && b2
    }

    private fun andWithNull(b: Boolean?): Boolean? {
        return if (b == null || b) {
            null
        } else false
    }

    fun or(op1: ReadableProperty<out Boolean?>, op2: ReadableProperty<out Boolean?>): ReadableProperty<out Boolean?> {
        return object : DerivedProperty<Boolean?>(null, op1, op2) {

            override val propExpr: String
                get() = "(" + op1.propExpr + " || " + op2.propExpr + ")"

            override fun doGet(): Boolean? {
                return or(op1.get(), op2.get())
            }
        }
    }

    private fun or(b1: Boolean?, b2: Boolean?): Boolean? {
        if (b1 == null) {
            return orWithNull(b2)
        }
        return if (b2 == null) {
            orWithNull(b1)
        } else b1 || b2
    }

    private fun orWithNull(b: Boolean?): Boolean? {
        return if (b == null || !b) {
            null
        } else true
//        return !(b == null || !b)
    }

    fun add(p1: ReadableProperty<Int?>, p2: ReadableProperty<Int?>): ReadableProperty<Int?> {
        return object : DerivedProperty<Int?>(null, p1, p2) {

            override val propExpr: String
                get() = "(" + p1.propExpr + " + " + p2.propExpr + ")"

            override fun doGet(): Int? {
                return if (p1.get() == null || p2.get() == null) null else p1.get()!! + p2.get()!!
            }
        }
    }

    fun <SourceT, TargetT> select(
        source: ReadableProperty<SourceT?>, `fun`: (SourceT?) -> ReadableProperty<TargetT?>
    ): ReadableProperty<TargetT?> {
        return select(source, `fun`, null)
    }

    fun <SourceT, TargetT> select(
        source: ReadableProperty<SourceT?>, `fun`: (SourceT?) -> ReadableProperty<TargetT?>,
        nullValue: TargetT?
    ): ReadableProperty<TargetT?> {

        val calc = object : Supplier<TargetT?> {
            override fun get(): TargetT? {
                val value = source.get() ?: return nullValue
                val prop = `fun`(value)
                return prop.get()
            }
        }

        return object : BaseDerivedProperty<TargetT?>(null) {
            //        return object : BaseDerivedProperty<TargetT?>() {
            private var myTargetProperty: ReadableProperty<out TargetT?>? = null

            private var mySourceRegistration: Registration? = null
            private var myTargetRegistration: Registration? = null

            override val propExpr: String
                get() = "select(" + source.propExpr + ", " + `fun` + ")"

            override fun doAddListeners() {
                myTargetProperty = if (source.get() == null) null else `fun`(source.get())

                val targetHandler = object : EventHandler<PropertyChangeEvent<out TargetT?>> {
                    override fun onEvent(event: PropertyChangeEvent<out TargetT?>) {
                        somethingChanged()
                    }
                }
                val sourceHandler = object : EventHandler<PropertyChangeEvent<out SourceT?>> {
                    override fun onEvent(event: PropertyChangeEvent<out SourceT?>) {
                        if (myTargetProperty != null) {
                            myTargetRegistration!!.remove()
                        }
                        val sourceValue = source.get()
                        if (sourceValue != null) {
                            myTargetProperty = `fun`(sourceValue)
                        } else {
                            myTargetProperty = null
                        }
                        if (myTargetProperty != null) {
                            myTargetRegistration = myTargetProperty!!.addHandler(targetHandler)
                        }
                        somethingChanged()
                    }
                }
                mySourceRegistration = source.addHandler(sourceHandler)
                if (myTargetProperty != null) {
                    myTargetRegistration = myTargetProperty!!.addHandler(targetHandler)
                }
            }

            override fun doRemoveListeners() {
                if (myTargetProperty != null) {
                    myTargetRegistration!!.remove()
                }
                mySourceRegistration!!.remove()
            }

            override fun doGet(): TargetT? {
                return calc.get()
            }
        }
    }

    fun <SourceT, TargetT> selectRw(
        source: ReadableProperty<SourceT>, `fun`: (SourceT) -> Property<TargetT?>
    ): Property<TargetT?> {
        val calc = object : Supplier<TargetT?> {
            override fun get(): TargetT? {
                val value = source.get() ?: return null
                val prop = `fun`(value)
                return prop.get()
            }
        }

        class MyProperty : BaseDerivedProperty<TargetT?>(calc.get()), Property<TargetT?> {
            //        class MyProperty : BaseDerivedProperty<TargetT?>(), Property<TargetT?> {
            private var myTargetProperty: Property<TargetT?>? = null

            private var mySourceRegistration: Registration? = null
            private var myTargetRegistration: Registration? = null

            override val propExpr: String
                get() = "select(" + source.propExpr + ", " + `fun` + ")"

            override fun doAddListeners() {
                myTargetProperty = if (source.get() == null) null else `fun`(source.get())

                val targetHandler = object : EventHandler<PropertyChangeEvent<out TargetT?>> {
                    override fun onEvent(event: PropertyChangeEvent<out TargetT?>) {
                        somethingChanged()
                    }
                }
                val sourceHandler = object : EventHandler<PropertyChangeEvent<out SourceT>> {
                    override fun onEvent(event: PropertyChangeEvent<out SourceT>) {
                        if (myTargetProperty != null) {
                            myTargetRegistration!!.remove()
                        }
                        val sourceValue = source.get()
                        if (sourceValue != null) {
                            myTargetProperty = `fun`(sourceValue)
                        } else {
                            myTargetProperty = null
                        }
                        if (myTargetProperty != null) {
                            myTargetRegistration = myTargetProperty!!.addHandler(targetHandler)
                        }
                        somethingChanged()
                    }
                }
                mySourceRegistration = source.addHandler(sourceHandler)
                if (myTargetProperty != null) {
                    myTargetRegistration = myTargetProperty!!.addHandler(targetHandler)
                }
            }

            override fun doRemoveListeners() {
                if (myTargetProperty != null) {
                    myTargetRegistration!!.remove()
                }
                mySourceRegistration!!.remove()
            }

            override fun doGet(): TargetT? {
                return calc.get()
            }

            override fun set(value: TargetT?) {
                if (myTargetProperty == null) return
                myTargetProperty!!.set(value)
            }
        }

        return MyProperty()
    }

    fun <EventT, ValueT> selectEvent(
        prop: ReadableProperty<out ValueT>, selector: (ValueT) -> EventSource<EventT>
    ): EventSource<EventT> {
        return object : EventSource<EventT> {
            override fun addHandler(handler: EventHandler<EventT>): Registration {
                val esReg = Value(Registration.EMPTY)

                val update = object : Runnable {
                    override fun run() {
                        esReg.get().remove()
                        if (prop.get() != null) {
                            esReg.set(selector(prop.get()).addHandler(handler))
                        } else {
                            esReg.set(Registration.EMPTY)
                        }
                    }
                }

                update.run()

                val propReg = prop.addHandler(object : EventHandler<PropertyChangeEvent<out ValueT>> {
                    override fun onEvent(event: PropertyChangeEvent<out ValueT>) {
                        update.run()
                    }
                })

                return object : Registration() {
                    override fun doRemove() {
                        propReg.remove()
                        esReg.get().remove()
                    }
                }
            }
        }
    }

    fun <ValueT> same(prop: ReadableProperty<out ValueT?>, v: ValueT?): ReadableProperty<out Boolean> {
        return map(prop) { value -> value === v }
    }

    fun <ValueT> equals(prop: ReadableProperty<out ValueT?>, v: ValueT?): ReadableProperty<out Boolean> {
        return map(prop) { value -> value == v }
    }

    fun <ValueT> equals(
        p1: ReadableProperty<out ValueT?>,
        p2: ReadableProperty<out ValueT?>
    ): ReadableProperty<out Boolean> {
        return object : DerivedProperty<Boolean>(false, p1, p2) {

            override val propExpr: String
                get() = "equals(" + p1.propExpr + ", " + p2.propExpr + ")"

            override fun doGet(): Boolean {
                return p1.get() == p2.get()
            }
        }
    }

    fun <ValueT> notEquals(prop: ReadableProperty<out ValueT?>, value: ValueT?): ReadableProperty<out Boolean?> {
        return not(equals(prop, value))
    }

    fun <ValueT> notEquals(
        p1: ReadableProperty<out ValueT?>,
        p2: ReadableProperty<out ValueT?>
    ): ReadableProperty<out Boolean?> {
        return not(equals(p1, p2))
    }

    fun <SourceT, TargetT> map(
        prop: ReadableProperty<out SourceT>, f: (SourceT) -> TargetT
    ): ReadableProperty<out TargetT> {
        return object : DerivedProperty<TargetT>(f(prop.get()), prop) {

            override val propExpr: String
                get() = "transform(" + prop.propExpr + ", " + f + ")"

            override fun doGet(): TargetT {
                return f(prop.get())
            }
        }
    }

    fun <SourceT, TargetT> map(
        prop: Property<SourceT>, sToT: (SourceT?) -> TargetT,
        tToS: (TargetT) -> SourceT
    ): Property<TargetT> {
        class TransformedProperty : Property<TargetT> {

            override val propExpr: String
                get() = "transform(" + prop.propExpr + ", " + sToT + ", " + tToS + ")"

            override fun get(): TargetT {
                return sToT(prop.get())
            }

            override fun addHandler(handler: EventHandler<PropertyChangeEvent<out TargetT>>): Registration {
                return prop.addHandler(object : EventHandler<PropertyChangeEvent<out SourceT>> {
                    override fun onEvent(event: PropertyChangeEvent<out SourceT>) {
                        val oldValue = sToT(event.oldValue)
                        val newValue = sToT(event.newValue)

                        if (oldValue == newValue) return

                        handler.onEvent(PropertyChangeEvent(oldValue, newValue))
                    }
                })
            }

            override fun set(value: TargetT) {
                prop.set(tToS(value))
            }
        }

        return TransformedProperty()
    }

    fun <ValueT> constant(value: ValueT): ReadableProperty<out ValueT> {
        return object : BaseReadableProperty<ValueT>() {
            override val propExpr: String
                get() = "constant($value)"

            override fun get(): ValueT {
                return value
            }

            override fun addHandler(handler: EventHandler<PropertyChangeEvent<out ValueT>>): Registration {
                return Registration.EMPTY
            }
        }
    }

    fun <ItemT> isEmpty(collection: ObservableCollection<ItemT>): ReadableProperty<out Boolean> {
        return object : SimpleCollectionProperty<ItemT, Boolean>(collection, collection.isEmpty()) {
//        return object : SimpleCollectionProperty<ItemT, Boolean>(collection) {

            override val propExpr: String
                get() = "isEmpty($collection)"

            override fun doGet(): Boolean {
                return collection.isEmpty()
            }
        }
    }

    fun <ItemT> size(collection: ObservableCollection<ItemT>): ReadableProperty<Int> {
        return object : SimpleCollectionProperty<ItemT, Int>(collection, collection.size) {

            override val propExpr: String
                get() = "size($collection)"

            override fun doGet(): Int {
                return collection.size
            }
        }
    }

    /*
        fun <ItemT> indexOf(
                collection: ObservableList<ItemT>,
                item: ReadableProperty<ItemT>): ReadableProperty<Int> {
            return simplePropertyWithCollection(collection, item, object : Supplier<Int> {
                override fun get(): Int {
                    return collection.indexOf(item.get())
                }
            })
        }

        fun <ItemT> contains(
                collection: ObservableCollection<ItemT>,
                item: ReadableProperty<out ItemT>): ReadableProperty<out Boolean> {
            return simplePropertyWithCollection(collection, item, object : Supplier<Boolean> {
                override fun get(): Boolean {
                    return collection.contains(item.get())
                }
            })
        }
    */

    /*
        fun <T> simplePropertyWithCollection(
                collection: ObservableCollection<*>,
                item: ReadableProperty<*>,
                supplier: Supplier<T>): ReadableProperty<T> {

    //        return object : BaseDerivedProperty<T>(supplier.get()) {
            return object : BaseDerivedProperty<T>() {
                private var myRegistration: Registration? = null
                private var myCollectionRegistration: Registration? = null

                override val propExpr: String
                    get() = "fromCollection($collection, $item, $supplier)"

                override fun doGet(): T {
                    return supplier.get()
                }

                override fun doAddListeners() {
                    myRegistration = item.addHandler(object : EventHandler<PropertyChangeEvent<*>> {
                        override fun onEvent(event: PropertyChangeEvent<*>) {
                            somethingChanged()
                        }
                    })
                    myCollectionRegistration = collection.addListener(Properties.simpleAdapter(object : Runnable {
                        override fun run() {
                            somethingChanged()
                        }
                    }))
                }

                protected override fun doRemoveListeners() {
                    myRegistration!!.remove()
                    myCollectionRegistration!!.remove()
                }
            }
        }
    */

    fun <ItemT> notEmpty(collection: ObservableCollection<ItemT>): ReadableProperty<out Boolean?> {
        return not(empty(collection) as ReadableProperty<out Boolean?>)
    }

    fun <ItemT> empty(collection: ObservableCollection<ItemT>): ReadableProperty<out Boolean> {
        return object : BaseDerivedProperty<Boolean>(collection.isEmpty()) {
            //        return object : BaseDerivedProperty<Boolean>() {
            private var myCollectionRegistration: Registration? = null

            override val propExpr: String
                get() = "empty($collection)"

            override fun doAddListeners() {
                myCollectionRegistration = collection.addListener(simpleAdapter(object : Runnable {
                    override fun run() {
                        somethingChanged()
                    }
                }))
            }

            override fun doRemoveListeners() {
                myCollectionRegistration!!.remove()
            }

            override fun doGet(): Boolean {
                return collection.isEmpty()
            }
        }
    }

    private fun <ItemT> simpleAdapter(r: Runnable): org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionAdapter<ItemT> {
        return object : org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionAdapter<ItemT>() {
            override fun onItemAdded(event: org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionItemEvent<out ItemT>) {
                r.run()
            }

            override fun onItemRemoved(event: org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionItemEvent<out ItemT>) {
                r.run()
            }
        }
    }

    fun <ValueT> ifProp(
        cond: ReadableProperty<out Boolean>, ifTrue: ReadableProperty<out ValueT>, ifFalse: ReadableProperty<out ValueT>
    ): ReadableProperty<out ValueT?> {
        return object : DerivedProperty<ValueT?>(null, cond, ifTrue, ifFalse) {

            override val propExpr: String
                get() = "if(" + cond.propExpr + ", " + ifTrue.propExpr + ", " + ifFalse.propExpr + ")"

            override fun doGet(): ValueT {
                return if (cond.get()) ifTrue.get() else ifFalse.get()
            }
        }
    }

    fun <ValueT> ifProp(
        cond: ReadableProperty<out Boolean>,
        ifTrue: ValueT,
        ifFalse: ValueT
    ): ReadableProperty<out ValueT?> {
        return ifProp(cond, constant(ifTrue), constant(ifFalse))
    }

    fun <ValueT> ifProp(cond: WritableProperty<ValueT>, ifTrue: ValueT, ifFalse: ValueT): WritableProperty<Boolean> {
        return object : WritableProperty<Boolean> {
            override fun set(value: Boolean) {
                if (value) {
                    cond.set(ifTrue)
                } else {
                    cond.set(ifFalse)
                }
            }
        }
    }

    fun <ValueT> withDefaultValue(prop: ReadableProperty<out ValueT>, ifNull: ValueT): ReadableProperty<out ValueT> {
        return object : DerivedProperty<ValueT>(ifNull, prop) {
            override fun doGet(): ValueT {
                return if (prop.get() == null) {
                    ifNull
                } else {
                    prop.get()
                }
            }
        }
    }

    fun <ValueT> firstNotNull(vararg values: ReadableProperty<out ValueT?>): ReadableProperty<out ValueT?> {
        return object : DerivedProperty<ValueT?>(null, *values) {

            override val propExpr: String
                get() {
                    val result = StringBuilder()
                    result.append("firstNotNull(")

                    var first = true
                    for (v in values) {
                        if (first) {
                            first = false
                        } else {
                            result.append(", ")
                        }
                        result.append(v.propExpr)
                    }
                    result.append(")")
                    return result.toString()
                }

            override fun doGet(): ValueT? {
                for (v in values) {
                    if (v.get() != null) {
                        return v.get()
                    }
                }
                return null
            }
        }
    }

    fun <ValueT> isPropertyValid(
        source: ReadableProperty<out ValueT>,
        validator: Predicate<ValueT>
    ): ReadableProperty<out Boolean> {
        return object : DerivedProperty<Boolean>(false, source) {

            override val propExpr: String
                get() = "isValid(" + source.propExpr + ", " + validator + ")"

            override fun doGet(): Boolean {
                return validator(source.get())
            }
        }
    }

    fun <ValueT> validatedProperty(source: Property<ValueT?>, validator: Predicate<ValueT?>): Property<ValueT?> {

        class ValidatedProperty : DerivedProperty<ValueT?>(null, source), Property<ValueT?> {
            private var myLastValid: ValueT? = null

            override val propExpr: String
                get() = "validated(" + source.propExpr + ", " + validator + ")"

            override fun doGet(): ValueT? {
                val sourceValue = source.get()
                if (validator(sourceValue)) {
                    myLastValid = sourceValue
                }
                return myLastValid
            }

            override fun set(value: ValueT?) {
                if (!validator(value)) {
                    return
                }
                source.set(value)
            }
        }

        return ValidatedProperty()
    }

    @JvmOverloads
    fun toStringOf(p: ReadableProperty<*>, nullValue: String = "null"): ReadableProperty<String> {
        return object : DerivedProperty<String>(nullValue, p) {
            override fun doGet(): String {
                val value = p.get()
                return if (value != null) "" + value else nullValue
            }
        }
    }

    fun <ValueT> property(read: ReadableProperty<out ValueT>, write: WritableProperty<in ValueT>): Property<ValueT> {
        return object : Property<ValueT> {
            override val propExpr: String
                get() = read.propExpr

            override fun get(): ValueT {
                return read.get()
            }

            override fun addHandler(handler: EventHandler<PropertyChangeEvent<out ValueT>>): Registration {
                return read.addHandler(handler)
            }

            override fun set(value: ValueT) {
                write.set(value)
            }
        }
    }

    fun <ValueT> compose(vararg props: WritableProperty<in ValueT>): WritableProperty<ValueT> {
        return object : WritableProperty<ValueT> {
            override fun set(value: ValueT) {
                for (wp in props) {
                    wp.set(value)
                }
            }
        }
    }


    fun <ItemT> forSingleItemCollection(coll: ObservableCollection<ItemT>): Property<ItemT?> {
        if (coll.size > 1) {
            throw IllegalStateException("Collection $coll has more than one item")
        }

        return object : Property<ItemT?> {

            override val propExpr: String
                get() = "singleItemCollection($coll)"

            override fun get(): ItemT? {
                return if (coll.isEmpty()) {
                    null
                } else coll.iterator().next()
            }

            override fun set(value: ItemT?) {
                val current = get()
                if (current == value) return
                coll.clear()
                if (value != null) {
                    coll.add(value)
                }
            }

            override fun addHandler(handler: EventHandler<PropertyChangeEvent<out ItemT?>>): Registration {
                return coll.addListener(object : org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionAdapter<ItemT>() {
                    override fun onItemAdded(event: org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionItemEvent<out ItemT>) {
                        if (coll.size != 1) {
                            throw IllegalStateException()
                        }
                        handler.onEvent(PropertyChangeEvent(null, event.newItem))
                    }

                    override fun onItemSet(event: org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionItemEvent<out ItemT>) {
                        if (event.index != 0) {
                            throw IllegalStateException()
                        }
                        handler.onEvent(PropertyChangeEvent(event.oldItem, event.newItem))
                    }

                    override fun onItemRemoved(event: org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionItemEvent<out ItemT>) {
                        if (!coll.isEmpty()) {
                            throw IllegalStateException()
                        }
                        handler.onEvent(PropertyChangeEvent(event.oldItem, null))
                    }
                })
            }
        }
    }

    fun and(vararg props: ReadableProperty<out Boolean?>): ReadableProperty<out Boolean?> {
        if (props.isEmpty()) {
            throw IllegalArgumentException("No arguments")
        }
        return object : DerivedProperty<Boolean?>(null, *props) {

            override val propExpr: String
                get() {
                    val propExpr = StringBuilder("(")
                    propExpr.append(props[0].propExpr)
                    for (i in 1 until props.size) {
                        propExpr.append(" && ").append(props[i].propExpr)
                    }
                    return propExpr.append(")").toString()
                }

            override fun doGet(): Boolean? {
                var res: Boolean? = true
                for (prop in props) {
                    res = and(res, prop.get())
                }
                return res
            }
        }
    }

    fun or(vararg props: ReadableProperty<out Boolean?>): ReadableProperty<out Boolean?> {
        if (props.isEmpty()) {
            throw IllegalArgumentException("No arguments")
        }
        return object : DerivedProperty<Boolean?>(null, *props) {

            override val propExpr: String
                get() {
                    val propExpr = StringBuilder("(")
                    propExpr.append(props[0].propExpr)
                    for (i in 1 until props.size) {
                        propExpr.append(" || ").append(props[i].propExpr)
                    }
                    return propExpr.append(")").toString()
                }

            override fun doGet(): Boolean? {
                var res: Boolean? = false
                for (prop in props) {
                    res = or(res, prop.get())
                }
                return res
            }
        }
    }

    /*
        fun <EnumT : Enum<EnumT>> enumAsInteger(source: Property<EnumT>, enumClass: KClass<out EnumT>): Property<Int> {
            return property(map(source) { value -> value.ordinal },
                    object : WritableProperty<Int> {
                        override fun set(value: Int?) {
                            if (value == null) {
                                source.set(null)
                                return
                            }
                            source.set(enumClass.getEnumConstants()[value])
                        }
                    })
        }
    */
}
