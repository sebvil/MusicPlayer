package com.sebastianvm.musicplayer.annotations

import me.tatarka.inject.annotations.Qualifier

@Qualifier
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.TYPE)
annotation class IoDispatcher
