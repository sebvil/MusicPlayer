package com.sebastianvm.fakegen

import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeArgument
import com.google.devtools.ksp.symbol.KSTypeReference
import com.google.devtools.ksp.symbol.KSValueParameter
import com.google.devtools.ksp.symbol.Nullability
import com.google.devtools.ksp.symbol.Variance
import com.google.devtools.ksp.symbol.Variance.CONTRAVARIANT
import com.google.devtools.ksp.symbol.Variance.COVARIANT
import com.google.devtools.ksp.symbol.Variance.INVARIANT
import com.google.devtools.ksp.symbol.Variance.STAR
import com.google.devtools.ksp.validate
import com.google.devtools.ksp.visitor.KSDefaultVisitor
import java.io.OutputStream
import java.util.Locale

class FakeProcessor(
    private val options: Map<String, String>,
    private val logger: KSPLogger,
    private val codeGenerator: CodeGenerator
) : SymbolProcessor {

    operator fun OutputStream.plusAssign(str: String) {
        this.write(str.toByteArray())
    }

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver
            .getSymbolsWithAnnotation("com.sebastianvm.fakegen.FakeClass")
            .filterIsInstance<KSClassDeclaration>()

        if (!symbols.iterator().hasNext()) return emptyList()

        symbols.forEach {
            if (it.classKind != ClassKind.INTERFACE) {
                logger.error("Only interface can be annotated with @FakeClass", it)
                return@forEach
            }
            val packageName = it.packageName.asString()
            val interfaceName = it.simpleName.asString()
            val className = "Fake$interfaceName"

            @Suppress("SpreadOperator")
            val file = codeGenerator.createNewFile(
                // Make sure to associate the generated file with sources to keep/maintain it across incremental builds.
                // Learn more about incremental processing in KSP from the official docs:
                // https://kotlinlang.org/docs/ksp-incremental.html
                dependencies = Dependencies(false, *resolver.getAllFiles().toList().toTypedArray()),
                packageName = packageName,
                fileName = className
            )
            // Generating package statement.
            file += "package $packageName\n\n"

            file += it.accept(Visitor(), Unit)

            file += "}\n"

            // Don't forget to close the out stream.
            file.close()
        }

        return symbols.filterNot { it.validate() }.toList()
    }

    inner class Visitor : KSDefaultVisitor<Unit, String>() {

        private val imports: MutableSet<String> = mutableSetOf()
        private val classParameters: MutableSet<String> = mutableSetOf()

        override fun visitClassDeclaration(
            classDeclaration: KSClassDeclaration,
            data: Unit
        ): String {
            val interfaceName = classDeclaration.simpleName.asString()
            val className = "Fake$interfaceName"

            return buildString {
                val body =
                    classDeclaration.getDeclaredFunctions().map { it.accept(this@Visitor, Unit) }
                        .joinToString("\n")

                append(imports.sorted().joinToString("\n") { "import $it" })
                append("\n\n")
                append("class $className")

                if (classParameters.isNotEmpty()) {
                    append("(\n")
                    append(classParameters.joinToString(",\n") { "\tval $it" })
                    append("\n)")
                }

                append(": $interfaceName {\n\n")

                append(body)
            }
        }

        override fun visitFunctionDeclaration(function: KSFunctionDeclaration, data: Unit): String {
            val annotations =
                function.annotations.filter {
                    val name = it.shortName.asString()
                    name == "FakeQueryMethod" || name == "FakeCommandMethod"
                }.toList()
            if (annotations.size != 1) {
                logger.error(
                    "Method in FakeClass must be annotated with exactly one of @FakeQueryMethod or @FakeCommandMethod"
                )
                return ""
            }
            val annotation = annotations.first()

            val functionName = function.simpleName.asString()

            val modifiers = function.modifiers.joinToString(" ") { it.name.lowercase() }

            return when (annotation.shortName.asString()) {
                "FakeQueryMethod" -> {
                    imports.add("kotlinx.coroutines.flow.MutableSharedFlow")
                    buildString {
                        append("\toverride ")
                        if (modifiers.isNotBlank()) {
                            append("$modifiers ")
                        }
                        append("fun $functionName(")
                        append(function.parameters.joinToString { it.accept(this@Visitor, Unit) })
                        append("): ")
                        val returnType =
                            function.returnType?.accept(this@Visitor, Unit) ?: kotlin.run {
                                logger.error("Function annotated with FakeQueryMethod should have a return value")
                                return ""
                            }
                        append("$returnType {\n")
                        append("\t\treturn ")
                        val flowRegex = Regex("Flow<(.*)>")
                        val matches = flowRegex.find(returnType)
                        if (matches != null) {
                            val typeArgs = matches.groupValues[1]
                            classParameters.add("${functionName}Value: MutableSharedFlow<$typeArgs>")
                            append("${functionName}Value\n")
                        } else {
                            classParameters.add("${functionName}Value: MutableStateFlow<$returnType>")
                            append("${functionName}Value.value\n")
                        }
                        append("\t}\n")
                    }
                }

                "FakeCommandMethod" -> {
                    buildString {
                        append(
                            "\tprivate val _${functionName}Invocations: MutableList<List<Any>> = mutableListOf()\n\n"
                        )
                        append("\tval ${functionName}Invocations: List<List<Any>>\n")
                        append("\t\tget() = _${functionName}Invocations\n\n")
                        append("\toverride ")
                        if (modifiers.isNotBlank()) {
                            append("$modifiers ")
                        }
                        append("fun $functionName(")
                        append(function.parameters.joinToString { it.accept(this@Visitor, Unit) })
                        append(") {\n")
                        append("\t\t_${functionName}Invocations.add(listOf(")
                        append(function.parameters.joinToString { it.name?.asString() ?: "" })
                        append("))\n")
                        append("\t}\n")
                        append("\n")

                        append(
                            "\tfun reset${
                                functionName.replaceFirstChar {
                                    if (it.isLowerCase()) {
                                        it.titlecase(
                                            Locale.ROOT
                                        )
                                    } else {
                                        it.toString()
                                    }
                                }
                            }Invocations() {\n"
                        )
                        append("\t\t_${functionName}Invocations.clear()\n")
                        append("\t}\n")
                    }
                }

                else -> {
                    ""
                }
            }
        }

        override fun visitValueParameter(valueParameter: KSValueParameter, data: Unit): String {
            val type = valueParameter.type.accept(this, data)
            return "${valueParameter.name?.asString() ?: ""}: $type"
        }

        override fun visitTypeReference(typeReference: KSTypeReference, data: Unit): String {
            val import = typeReference.resolve().declaration.qualifiedName?.asString() ?: ""
            imports.add(import)
            val resolvedType = typeReference.resolve()
            resolvedType.arguments.forEach { it.type?.accept(this, Unit) }
            return resolvedType.toString()
        }

        private fun visitTypeArguments(typeArguments: List<KSTypeArgument>): String {
            return buildString {
                if (typeArguments.isNotEmpty()) {
                    append("<")
                    typeArguments.forEachIndexed { i, arg ->
                        append(visitTypeArgument(arg, data = Unit))
                        if (i < typeArguments.lastIndex) append(", ")
                    }
                    append(">")
                }
            }
        }

        override fun visitTypeArgument(typeArgument: KSTypeArgument, data: Unit): String {
            // Handling KSP options, specified in the consumer's build.gradle(.kts) file.
            return buildString {
                if (options["ignoreGenericArgs"] == "true") {
                    append("*")
                    return@buildString
                }

                when (val variance: Variance = typeArgument.variance) {
                    // <*>
                    STAR -> {
                        append("*")
                        return@buildString
                    }
                    // <out ...>, <in ...>
                    COVARIANT, CONTRAVARIANT -> {
                        append(variance.label)
                        append(" ")
                    }

                    INVARIANT -> Unit
                }

                val resolvedType: KSType? = typeArgument.type?.resolve()
                resolvedType?.declaration?.qualifiedName?.asString()?.also { append(it) } ?: run {
                    logger.error("Invalid type argument", typeArgument)
                    return@buildString
                }

                // Generating nested generic parameters if any.
                val genericArguments: List<KSTypeArgument> =
                    typeArgument.type?.element?.typeArguments ?: emptyList()
                append(visitTypeArguments(genericArguments))

                // Handling nullability.
                append(if (resolvedType.nullability == Nullability.NULLABLE) "?" else "")
            }
        }

        override fun defaultHandler(node: KSNode, data: Unit): String {
            return ""
        }
    }
}
