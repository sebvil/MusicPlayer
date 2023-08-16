package com.sebastianvm.fakegen

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

class FakeProcessorProvider : SymbolProcessorProvider {

    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return FakeProcessor(
            options = environment.options,
            logger = environment.logger,
            codeGenerator = environment.codeGenerator
        )
    }
}
