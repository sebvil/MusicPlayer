package com.sebastianvm.musicplayer.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.sebastianvm.musicplayer.annotations.MvvmComponent
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview
import com.squareup.kotlinpoet.ksp.toTypeName

class MvvmComponentProcessor(private val codeGenerator: CodeGenerator) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val composables =
            resolver
                .getSymbolsWithAnnotation(MvvmComponent::class.qualifiedName.orEmpty())
                .filterIsInstance<KSFunctionDeclaration>()

        composables.forEach { it.accept(Visitor(), Unit) }

        return emptyList()
    }

    inner class Visitor : KSVisitorVoid() {
        @OptIn(KotlinPoetKspPreview::class)
        override fun visitFunctionDeclaration(function: KSFunctionDeclaration, data: Unit) {
            val componentName = function.simpleName.getShortName()
            val viewModel =
                function.annotations
                    .filter { it.shortName.getShortName() == MvvmComponent::class.simpleName }
                    .first()
                    .arguments
                    .first()
                    .value as KSType

            val viewModelDeclaration = viewModel.declaration as KSClassDeclaration
            val viewModelConstructorParameters =
                viewModelDeclaration.primaryConstructor
                    ?.parameters
                    .orEmpty()
                    .filter { it.name?.asString() != "vmScope" }
                    .map { Parameter(it) }

            val className = "${componentName}MvvmComponent"

            val fileSpec =
                MvvmComponentFileSpec(
                    packageName = function.packageName.asString(),
                    componentName = componentName,
                    composableParameters = function.parameters.map { Parameter(it) },
                    className = className,
                    viewModelParameters = viewModelConstructorParameters,
                    viewModelData =
                        ViewModelData(
                            name = viewModelDeclaration.simpleName.asString(),
                            type = viewModel.toTypeName(),
                            baseViewModelTypeArguments =
                                (viewModelDeclaration.superTypes.first().toTypeName()
                                        as ParameterizedTypeName)
                                    .typeArguments,
                        ),
                )
            fileSpec.writeTo(codeGenerator = codeGenerator, aggregating = true)

            FeatureFileSpec(
                    function.packageName.asString(),
                    componentName,
                    viewModelConstructorParameters,
                )
                .writeTo(codeGenerator = codeGenerator, aggregating = true)
        }
    }
}
