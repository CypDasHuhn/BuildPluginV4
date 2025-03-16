package de.cypdashuhn.rooster_processor

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration

class MyProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {

    // Hardcoded types to listen for, in this case `RoosterCommand` and other classes
    private val targetClasses = listOf(
        "de.cypdashuhn.rooster.commands.RoosterCommand",
    )

    private val collectedInstances = mutableMapOf<String, List<String>>()

    override fun process(resolver: Resolver): List<KSAnnotated> {
        for (targetClass in targetClasses) {
            val symbols = resolver.getSymbolsWithAnnotation(targetClass)
                .filterIsInstance<KSClassDeclaration>()
                .filter { it.superTypes.any { superType -> superType.resolve().declaration.qualifiedName?.asString() == targetClass } }

            for (symbol in symbols) {
                if (!symbol.isAnnotatedWithRoosterIgnore()) {
                    collectedInstances[targetClass] =
                        collectedInstances[targetClass]?.plus(symbol.qualifiedName.toString())
                            ?: listOf(symbol.qualifiedName.toString())
                    logger.info("Found: ${symbol.qualifiedName}")
                }
            }
        }

        // Generate the code if we have found instances
        if (collectedInstances.isNotEmpty()) {
            generateFile()
        }

        return emptyList()
    }

    private fun KSClassDeclaration.isAnnotatedWithRoosterIgnore(): Boolean {
        return this.annotations.any { it.shortName.asString() == "RoosterIgnore" }
    }

    private fun generateFile() {
        val fileSpec = buildString {
            appendLine("package de.cypdashuhn.rooster.generated")
            appendLine()
            appendLine("object RoosterTargets {")
            collectedInstances.entries.forEach { (clazz, instances) ->
                appendLine("    val ${clazz.toRegisterName()} = listOf(")
                instances.forEach { appendLine("        $it,") }
                appendLine("    )")
            }
            appendLine("}")
        }

        val file = codeGenerator.createNewFile(
            dependencies = Dependencies(false),
            packageName = "package de.cypdashuhn.rooster.generated",
            fileName = "RoosterTargets"
        )
        file.bufferedWriter().use { it.write(fileSpec) }

        logger.info("Generated RoosterTargets.kt")
    }

    fun String.toRegisterName(): String {
        val lastPart = this.split(".").last()
        return "registered${lastPart}s"
    }
}