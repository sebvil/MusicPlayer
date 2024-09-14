package com.sebastianvm.musicplayer

import android.content.Context
import io.kotest.core.spec.style.FreeSpec
import org.koin.ksp.generated.module
import org.koin.test.verify.verify

class ApplicationModuleTest :
    FreeSpec({ "Koin" { ApplicationModule().module.verify(extraTypes = listOf(Context::class)) } })
