package com.rollncode.basement.interfaces

import kotlin.annotation.AnnotationTarget.*

/**
 *
 * @author Tregub Artem tregub.artem@gmail.com
 * @since 2017.09.22
 */
@Repeatable
@Target(CLASS, FUNCTION)
annotation class TestImplemented(@Suppress("unused") val pathToTest: String)