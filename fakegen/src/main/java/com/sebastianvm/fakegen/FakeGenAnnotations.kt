package com.sebastianvm.fakegen

@Target(AnnotationTarget.CLASS)
annotation class FakeClass

@Target(AnnotationTarget.FUNCTION)
annotation class FakeQueryMethod

@Target(AnnotationTarget.FUNCTION)
annotation class FakeCommandMethod
