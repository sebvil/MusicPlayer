package com.sebastianvm.musicplayer.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSValueParameter
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview
import com.squareup.kotlinpoet.ksp.originatingKSFiles
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.writeTo

data class Parameter(val name: String, val type: TypeName) {
    @OptIn(KotlinPoetKspPreview::class)
    constructor(
        kspParameter: KSValueParameter
    ) : this(name = kspParameter.name?.asString().orEmpty(), type = kspParameter.type.toTypeName())

    fun toParameterSpec() = ParameterSpec.builder(name = name, type = type).build()

    fun toPropertySpec() =
        PropertySpec.builder(name = name, type = type, KModifier.PRIVATE).initializer(name).build()
}

data class ViewModelData(
    val name: String,
    val type: TypeName,
    val baseViewModelTypeArguments: List<TypeName>,
)

class MvvmComponentFileSpec(
    private val packageName: String,
    private val componentName: String,
    private val composableParameters: List<Parameter>,
    private val className: String,
    private val viewModelParameters: List<Parameter>,
    private val viewModelData: ViewModelData,
) {

    val classSpec =
        TypeSpec.classBuilder(name = className)
            .apply {
                constructor()
                baseViewModelSuperClass()
                viewModelProperty()
                contentComposable()
            }
            .build()

    private val fileSpec =
        FileSpec.builder(packageName = packageName, fileName = className)
            .apply {
                addImport("com.sebastianvm.musicplayer.core.ui.mvvm", "viewModels")
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
            val paramsMinusArgs =
                viewModelParameters
                    .toMutableList()
                    .apply { removeIf { it.name == "arguments" } }
                    .toList()
            val argsParameter =
                Parameter(
                    "arguments",
                    ClassName(
                        packageName = packageName.replace("features", "features.api"),
                        "${componentName}Arguments",
                    ),
                )
            primaryConstructor(
                FunSpec.constructorBuilder()
                    .addParameter(argsParameter.toParameterSpec())
                    .addParameters(paramsMinusArgs.map { it.toParameterSpec() })
                    .build()
            )

            addProperty(
                argsParameter
                    .toPropertySpec()
                    .toBuilder()
                    .apply {
                        addModifiers(KModifier.OVERRIDE)
                        modifiers.remove(KModifier.PRIVATE)
                    }
                    .build()
            )
            addProperties(paramsMinusArgs.map { it.toPropertySpec() })
        }
    }

    private fun TypeSpec.Builder.baseViewModelSuperClass(): TypeSpec.Builder {
        return apply {
            superclass(
                ClassName(packageName = MVVM_PACKAGE, MVVM_COMPONENT_CLASS)
                    .parameterizedBy(
                        typeArguments =
                            viewModelData.baseViewModelTypeArguments + viewModelData.type
                    )
            )
        }
    }

    private fun TypeSpec.Builder.viewModelProperty(): TypeSpec.Builder {
        return addProperty(
            PropertySpec.builder("viewModel", viewModelData.type, KModifier.OVERRIDE)
                .delegate(viewModelInitialization())
                .build()
        )
    }

    private fun viewModelInitialization(): CodeBlock {
        return CodeBlock.builder()
            .apply {
                beginControlFlow("viewModels")
                add("${viewModelData.name}(\n")
                indent()
                viewModelParameters.forEach { add("${it.name} = ${it.name},\n") }
                unindent()
                add(")\n")
                endControlFlow()
            }
            .build()
    }

    private fun TypeSpec.Builder.contentComposable(): TypeSpec.Builder {
        return addFunction(
            FunSpec.builder("Content")
                .apply {
                    addModifiers(KModifier.OVERRIDE)
                    addAnnotation(
                        AnnotationSpec.builder(
                                ClassName(
                                    packageName = COMPOSE_RUNTIME_PACKAGE,
                                    COMPOSABLE_ANNOTATION,
                                )
                            )
                            .build()
                    )

                    addParameter("state", viewModelData.baseViewModelTypeArguments.first())
                    addParameter(
                        "handle",
                        ClassName(packageName = MVVM_PACKAGE, "Handler")
                            .parameterizedBy(viewModelData.baseViewModelTypeArguments[1]),
                    )
                    addParameter(
                        ParameterSpec.builder(
                                "modifier",
                                ClassName(packageName = COMPOSE_UI_PACKAGE, MODIFIER_CLASS),
                            )
                            .build()
                    )

                    addCode(
                        CodeBlock.builder()
                            .apply {
                                add(componentName)
                                add("(\n")
                                indent()
                                composableParameters.forEach { add("${it.name} = ${it.name},\n") }
                                unindent()
                                add(")")
                            }
                            .build()
                    )
                }
                .build()
        )
    }

    companion object {
        private const val MVVM_PACKAGE = "com.sebastianvm.musicplayer.core.ui.mvvm"
        private const val MVVM_COMPONENT_CLASS = "MvvmComponent"

        private const val COMPOSE_RUNTIME_PACKAGE = "androidx.compose.runtime"
        private const val COMPOSE_UI_PACKAGE = "androidx.compose.ui"
        private const val MODIFIER_CLASS = "Modifier"
        private const val COMPOSABLE_ANNOTATION = "Composable"
    }
}
