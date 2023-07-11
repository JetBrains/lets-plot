/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.observable.property

import org.jetbrains.letsPlot.commons.intern.observable.event.EventHandler
import org.jetbrains.letsPlot.commons.intern.observable.event.EventSource
import org.hamcrest.CoreMatchers
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeDiagnosingMatcher

object EventMatchers {

    fun <EventT> setTestHandler(source: EventSource<EventT>): MatchingHandler<EventT> {
        val handler = MatchingHandler<EventT>()
        source.addHandler(handler)
        return handler
    }

    fun <EventT> noEvents(): Matcher<in MatchingHandler<out EventT>> {
        return object : TypeSafeDiagnosingMatcher<MatchingHandler<out EventT>>() {
            override fun matchesSafely(item: MatchingHandler<out EventT>, mismatchDescription: Description): Boolean {
                if (item.events.isEmpty()) {
                    return true
                } else {
                    mismatchDescription.appendText("events happened: " + item.events)
                    return false
                }
            }

            override fun describeTo(description: Description) {
                description.appendText("no events")
            }
        }
    }

    fun <EventT> anyEvents(): Matcher<in MatchingHandler<out EventT>> {
        return object : TypeSafeDiagnosingMatcher<MatchingHandler<out EventT>>() {
            override fun matchesSafely(item: MatchingHandler<out EventT>, mismatchDescription: Description): Boolean {
                if (item.events.isEmpty()) {
                    mismatchDescription.appendText("no events happened")
                    return false
                } else {
                    return true
                }
            }

            override fun describeTo(description: Description) {
                description.appendText("any events")
            }
        }
    }

    fun <EventT> singleEvent(valueMatcher: Matcher<in EventT>): Matcher<MatchingHandler<EventT>> {
        return object : TypeSafeDiagnosingMatcher<MatchingHandler<EventT>>() {
            override fun matchesSafely(item: MatchingHandler<EventT>, mismatchDescription: Description): Boolean {
                if (item.events.isEmpty()) {
                    mismatchDescription.appendText("no events happened")
                    return false
                } else if (item.events.size == 1) {
                    val value = item.events[0]
                    if (valueMatcher.matches(value)) {
                        return true
                    } else {
                        mismatchDescription.appendText("value ")
                        valueMatcher.describeMismatch(value, mismatchDescription)
                        return false
                    }
                } else {
                    mismatchDescription.appendText("few events happened: " + item.events)
                    return false
                }
            }

            override fun describeTo(description: Description) {
                description.appendText("only event ").appendDescriptionOf(valueMatcher)
            }
        }
    }

    fun <EventT> lastEvent(valueMatcher: Matcher<in EventT>): Matcher<MatchingHandler<EventT>> {
        return object : TypeSafeDiagnosingMatcher<MatchingHandler<EventT>>() {
            override fun matchesSafely(item: MatchingHandler<EventT>, mismatchDescription: Description): Boolean {
                if (item.events.isEmpty()) {
                    mismatchDescription.appendText("no events happened")
                    return false
                } else {
                    val value = item.events[item.events.size - 1]
                    if (valueMatcher.matches(value)) {
                        return true
                    } else {
                        mismatchDescription.appendText("last value ")
                        valueMatcher.describeMismatch(value, mismatchDescription)
                        return false
                    }
                }
            }

            override fun describeTo(description: Description) {
                description.appendText("last event ").appendDescriptionOf(valueMatcher)
            }
        }
    }

    fun <EventT> allEvents(valuesMatcher: Matcher<in List<EventT>>): Matcher<MatchingHandler<out EventT>> {
        return object : TypeSafeDiagnosingMatcher<MatchingHandler<out EventT>>() {
            override fun matchesSafely(item: MatchingHandler<out EventT>, mismatchDescription: Description): Boolean {
                if (valuesMatcher.matches(item.events)) {
                    return true
                } else {
                    mismatchDescription.appendText("handled events ")
                    valuesMatcher.describeMismatch(item.events, mismatchDescription)
                    return false
                }
            }

            override fun describeTo(description: Description) {
                description.appendText("events ").appendDescriptionOf(valuesMatcher)
            }
        }
    }

    fun <ValueT> newValue(valueMatcher: Matcher<in ValueT>): Matcher<PropertyChangeEvent<out ValueT>> {
        return object : TypeSafeDiagnosingMatcher<PropertyChangeEvent<out ValueT>>() {
            override fun matchesSafely(
                item: PropertyChangeEvent<out ValueT>,
                mismatchDescription: Description
            ): Boolean {
                if (valueMatcher.matches(item.newValue)) {
                    return true
                } else {
                    mismatchDescription.appendText("new value ")
                    valueMatcher.describeMismatch(item.newValue, mismatchDescription)
                    return false
                }
            }

            override fun describeTo(description: Description) {
                description.appendText("new value ").appendDescriptionOf(valueMatcher)
            }
        }
    }

    fun <ValueT> newValueIs(value: ValueT): Matcher<PropertyChangeEvent<out ValueT>> {
        return newValue(CoreMatchers.`is`(value))
    }

    fun <ValueT> oldValue(valueMatcher: Matcher<in ValueT>): Matcher<PropertyChangeEvent<out ValueT>> {
        return object : TypeSafeDiagnosingMatcher<PropertyChangeEvent<out ValueT>>() {
            override fun matchesSafely(
                item: PropertyChangeEvent<out ValueT>,
                mismatchDescription: Description
            ): Boolean {
                if (valueMatcher.matches(item.oldValue)) {
                    return true
                } else {
                    mismatchDescription.appendText("old value ")
                    valueMatcher.describeMismatch(item.oldValue, mismatchDescription)
                    return false
                }
            }

            override fun describeTo(description: Description) {
                description.appendText("old value ").appendDescriptionOf(valueMatcher)
            }
        }
    }

    fun <ValueT> oldValueIs(value: ValueT): Matcher<PropertyChangeEvent<out ValueT>> {
        return oldValue(CoreMatchers.`is`(value))
    }

    class MatchingHandler<EventT> : EventHandler<EventT> {
        internal val events = ArrayList<EventT>()

        override fun onEvent(event: EventT) {
            events.add(event)
        }
    }
}
