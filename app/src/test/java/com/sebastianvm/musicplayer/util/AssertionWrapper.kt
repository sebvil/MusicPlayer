package com.sebastianvm.musicplayer.util

import io.kotest.matchers.shouldBe

/**
 * Helper class and methods to make kotest assertions type-safe, causing compiler errors instead
 * of runtime errors if types don't match
 */
data class AssertionWrapper<T>(val value: T)

fun <T> assertThat(value: T): AssertionWrapper<T> = AssertionWrapper(value)

infix fun <T> AssertionWrapper<T>.shouldBe(expected: T) = this.value shouldBe expected
