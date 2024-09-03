package com.sebastianvm.musicplayer.annotations

import kotlin.reflect.KClass

@Target(AnnotationTarget.FUNCTION) annotation class MvvmComponent(val vmClass: KClass<*>)
