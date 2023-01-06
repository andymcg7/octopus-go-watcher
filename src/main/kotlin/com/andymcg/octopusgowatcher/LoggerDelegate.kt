package com.andymcg.octopusgowatcher

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty
import kotlin.reflect.full.companionObject

// From https://www.baeldung.com/kotlin-logging

class LoggerDelegate<in R : Any> : ReadOnlyProperty<R, Logger> {

    override fun getValue(thisRef: R, property: KProperty<*>): Logger =
        LoggerFactory.getLogger(unwrapCompanionObject(thisRef.javaClass))

    private fun <T : Any> unwrapCompanionObject(javaClass: Class<T>): Class<*> {
        return javaClass.enclosingClass?.takeIf { it.kotlin.companionObject?.java == javaClass }
            ?: javaClass
    }
}