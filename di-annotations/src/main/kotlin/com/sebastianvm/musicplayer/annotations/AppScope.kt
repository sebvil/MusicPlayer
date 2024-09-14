package com.sebastianvm.musicplayer.annotations

import me.tatarka.inject.annotations.Scope

@Scope
@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY, AnnotationTarget.PROPERTY_GETTER)
annotation class AppScope
