package com.sebastianvm.musicplayer.processor

import com.squareup.kotlinpoet.CodeBlock

class MvvmComponentInitializerSpec(
    mvvmComponentName: String,
    argumentsName: String,
    propsName: String,
    mvvmComponentParameters: List<Parameter>,
) {

    val hasProps = mvvmComponentParameters.any { it.name == "props" }
    private val actualPropsName = if (hasProps) propsName else "NoProps"
    private val propsParam = if (hasProps) "props" else "_"

    val codeBlock: CodeBlock =
        CodeBlock.builder()
            .apply {
                add(
                    "MvvmComponent.Initializer<$argumentsName, $actualPropsName> { arguments, $propsParam ->\n"
                )
                indent()
                add("$mvvmComponentName(\n")
                indent()
                mvvmComponentParameters.forEach { add("${it.name} = ${it.name},\n") }
                unindent()
                add(")\n")
                unindent()
                add("}\n")
            }
            .build()
}
