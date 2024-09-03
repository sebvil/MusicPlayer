package com.sebastianvm.musicplayer.processor

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSValueParameter
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.sebastianvm.musicplayer.annotations.MvvmComponent
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.writeTo

class MvvmComponentProcessor(
    private val options: Map<String, String>,
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val composables =
            resolver
                .getSymbolsWithAnnotation(MvvmComponent::class.qualifiedName!!)
                .filterIsInstance<KSFunctionDeclaration>()

        composables.forEach {
            logger.warn("Visiting $it", it)

            it.accept(Visitor(), Unit)
        }

        return emptyList()
    }

    inner class Visitor : KSVisitorVoid() {
        @OptIn(KotlinPoetKspPreview::class, KspExperimental::class)
        override fun visitFunctionDeclaration(function: KSFunctionDeclaration, data: Unit) {
            val shortName = function.simpleName.getShortName()
            val viewModel =
                function.annotations
                    .filter { it.shortName.getShortName() == MvvmComponent::class.simpleName }
                    .first()
                    .arguments
                    .first()
                    .value as KSType

            val viewModelDeclaration = viewModel.declaration as KSClassDeclaration
            val viewModelConstructorParameters =
                viewModelDeclaration.primaryConstructor!!.parameters.filter {
                    it.name!!.asString() != "vmScope"
                }

            val constructor =
                FunSpec.constructorBuilder()
                    .apply {
                        viewModelConstructorParameters.forEach {
                            addParameter(it.name!!.asString(), it.type.toTypeName())
                        }
                    }
                    .build()

            val componentName = "${shortName}MvvmComponent"
            val builder =
                FileSpec.builder(
                        packageName = function.packageName.asString(),
                        fileName = componentName,
                    )
                    .apply {
                        addImport("com.sebastianvm.musicplayer.core.ui.mvvm", "viewModels")
                        addType(
                            TypeSpec.classBuilder(name = componentName)
                                .apply {
                                    primaryConstructor(constructor)
                                    viewModelConstructorParameters.forEach {
                                        addProperty(
                                            PropertySpec.builder(
                                                    it.name!!.asString(),
                                                    it.type.toTypeName(),
                                                    KModifier.PRIVATE,
                                                )
                                                .initializer(it.name!!.asString())
                                                .build()
                                        )
                                    }
                                    val vmTypeArguments =
                                        (viewModelDeclaration.superTypes.first().toTypeName()
                                                as ParameterizedTypeName)
                                            .typeArguments
                                    superclass(
                                        ClassName(
                                                packageName =
                                                    "com.sebastianvm.musicplayer.core.ui.mvvm",
                                                "MvvmComponent",
                                            )
                                            .parameterizedBy(
                                                vmTypeArguments + viewModel.toTypeName()
                                            )
                                    )
                                    addProperty(
                                        PropertySpec.builder(
                                                "viewModel",
                                                viewModel.toTypeName(),
                                                KModifier.OVERRIDE,
                                            )
                                            .delegate(
                                                viewModelInitialization(
                                                    viewModelDeclaration,
                                                    viewModelConstructorParameters,
                                                )
                                            )
                                            .build()
                                    )

                                    addFunction(
                                        FunSpec.builder("Content")
                                            .apply {
                                                addModifiers(KModifier.OVERRIDE)
                                                addAnnotation(
                                                    AnnotationSpec.builder(
                                                            ClassName(
                                                                "androidx.compose.runtime",
                                                                "Composable",
                                                            )
                                                        )
                                                        .build()
                                                )
                                                function.parameters.forEach {
                                                    addParameter(
                                                        it.name!!.asString(),
                                                        it.type.toTypeName(),
                                                    )
                                                }
                                                addCode(
                                                    CodeBlock.builder()
                                                        .apply {
                                                            add(function.simpleName.asString())
                                                            add("(\n")
                                                            indent()
                                                            function.parameters.forEach {
                                                                val parameterName =
                                                                    it.name!!.asString()
                                                                add(
                                                                    "$parameterName = $parameterName,\n"
                                                                )
                                                            }
                                                            unindent()
                                                            add(")")
                                                        }
                                                        .build()
                                                )
                                            }
                                            .build()
                                    )
                                }
                                .build()
                        )
                    }

            builder.build().writeTo(codeGenerator = codeGenerator, aggregating = false)
            println("Found function: ${function.simpleName.asString()}")
        }
    }

    private fun viewModelInitialization(
        viewModelClass: KSClassDeclaration,
        constructorParameters: List<KSValueParameter>,
    ): CodeBlock {
        return CodeBlock.builder()
            .apply {
                beginControlFlow("viewModels")
                add("${viewModelClass.simpleName.asString()}(\n")
                indent()
                constructorParameters.forEach {
                    val propertyName = it.name!!.asString()
                    add("$propertyName = $propertyName,\n")
                }
                unindent()
                add(")\n")
                endControlFlow()
            }
            .build()
    }
}
