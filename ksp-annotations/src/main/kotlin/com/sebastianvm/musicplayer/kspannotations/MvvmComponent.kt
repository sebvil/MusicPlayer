package com.sebastianvm.musicplayer.kspannotations

import kotlin.reflect.KClass

@Target(AnnotationTarget.FUNCTION) annotation class MvvmComponent(val vmClass: KClass<*>)
