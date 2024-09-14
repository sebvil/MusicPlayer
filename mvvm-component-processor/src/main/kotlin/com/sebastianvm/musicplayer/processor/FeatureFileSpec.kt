package com.sebastianvm.musicplayer.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.symbol.KSFile
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview
import com.squareup.kotlinpoet.ksp.originatingKSFiles
import com.squareup.kotlinpoet.ksp.writeTo

class FeatureFileSpec(
    private val packageName: String,
    private val featurePrefix: String,
    viewModelParameters: List<Parameter>,
) {

    private val className = "Default${featurePrefix}Feature"

    private val featureParameters =
        viewModelParameters.filter { it.name != "arguments" && it.name != "props" }

    private val mvvmComponentParameters =
        if (viewModelParameters.any { it.name == "arguments" }) viewModelParameters
        else {
            listOf(
                Parameter(
                    "arguments",
                    ClassName(
                        packageName.replace("features", "features.api"),
                        "${featurePrefix}Arguments",
                    ),
                )
            ) + viewModelParameters
        }

    private val argumentsClassName = "${featurePrefix}Arguments"
    private val mvvmComponentInitializerSpec =
        MvvmComponentInitializerSpec(
            mvvmComponentName = "${featurePrefix}MvvmComponent",
            argumentsName = argumentsClassName,
            propsName = "${featurePrefix}Props",
            mvvmComponentParameters = mvvmComponentParameters,
        )
    private val argumentsClass = ClassName(baseInterfacePackage(), argumentsClassName)
    private val propsClass =
        if (mvvmComponentInitializerSpec.hasProps) {
            ClassName(packageName, "${featurePrefix}Props")
        } else {
            ClassName(MVVM_PACKAGE, "NoProps")
        }

    private val classSpec =
        TypeSpec.classBuilder(name = className)
            .apply {
                addAnnotation(ClassName("me.tatarka.inject.annotations", "Inject"))
                constructor()
                baseFeatureInterface()
                mvvmComponentInitializersProperty()
            }
            .build()

    private val fileSpec =
        FileSpec.builder(packageName = packageName, fileName = className)
            .apply {
                addImport(baseInterfacePackage(), argumentsClassName)
                if (!mvvmComponentInitializerSpec.hasProps) {
                    addImport(MVVM_PACKAGE, "NoProps")
                } else {
                    addImport(baseInterfacePackage(), "${featurePrefix}Props")
                }
                addType(classSpec)
            }
            .build()

    @OptIn(KotlinPoetKspPreview::class)
    fun writeTo(
        codeGenerator: CodeGenerator,
        aggregating: Boolean,
        originatingKSFiles: Iterable<KSFile> = fileSpec.originatingKSFiles(),
    ) {

        fileSpec.writeTo(codeGenerator, aggregating, originatingKSFiles)
    }

    private fun TypeSpec.Builder.constructor(): TypeSpec.Builder {
        return apply {
            primaryConstructor(
                FunSpec.constructorBuilder()
                    .addParameters(featureParameters.map { it.toParameterSpec() })
                    .build()
            )

            addProperties(featureParameters.map { it.toPropertySpec() })
        }
    }

    private fun TypeSpec.Builder.baseFeatureInterface(): TypeSpec.Builder {
        return apply {
            addSuperinterface(
                ClassName(packageName = baseInterfacePackage(), "${featurePrefix}Feature")
            )
        }
    }

    private fun baseInterfacePackage(): String {
        return packageName.replace("features", "features.api")
    }

    private fun TypeSpec.Builder.mvvmComponentInitializersProperty(): TypeSpec.Builder {
        return apply {
            addProperty(
                PropertySpec.builder(
                        "initializer",
                        type =
                            ClassName(MVVM_PACKAGE, MVVM_COMPONENT_CLASS, "Initializer")
                                .parameterizedBy(argumentsClass, propsClass),
                    )
                    .addModifiers(KModifier.OVERRIDE)
                    .initializer(
                        CodeBlock.builder()
                            .apply { add(mvvmComponentInitializerSpec.codeBlock) }
                            .build()
                    )
                    .build()
            )
        }
    }

    companion object {
        private const val MVVM_PACKAGE = "com.sebastianvm.musicplayer.core.ui.mvvm"
        private const val MVVM_COMPONENT_CLASS = "MvvmComponent"
    }
}
