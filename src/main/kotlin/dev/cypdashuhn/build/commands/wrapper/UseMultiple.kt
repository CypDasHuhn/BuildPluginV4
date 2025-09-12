package dev.cypdashuhn.build.commands.wrapper

import dev.jorel.commandapi.AbstractArgumentTree
import dev.jorel.commandapi.AbstractCommandTree
import dev.jorel.commandapi.CommandTree
import dev.jorel.commandapi.arguments.*
import dev.jorel.commandapi.executors.CommandExecutor
import org.bukkit.command.CommandSender
import java.util.*

fun AbstractArgumentTree<*, Argument<*>, CommandSender>.thenMerged(
    vararg args: Argument<*>,
    block: Argument<*>.() -> AbstractArgumentTree<*, Argument<*>, CommandSender>,
): AbstractArgumentTree<*, Argument<*>, CommandSender> {
    var tree = this

    val suggestionList: MutableList<ArgumentSuggestions<CommandSender>> = mutableListOf()

    args.forEachIndexed { idx, arg ->
        val isLast = idx == args.size - 1
        if (arg.overriddenSuggestions.isPresent) suggestionList += arg.overriddenSuggestions.get()
        if (!isLast) {
            findFieldRecursive(arg::class.java, "suggestions").apply {
                isAccessible = true
                set(arg, Optional.empty<ArgumentSuggestions<CommandSender>>())
            }
        }
        if (isLast) tree =
            tree.then(arg.replaceSuggestions(ArgumentSuggestions.merge(*suggestionList.toTypedArray())).block())
        tree = tree.then(arg.block())
    }

    return tree
}

fun AbstractArgumentTree<*, Argument<*>, CommandSender>.thenMerged(
    argList: List<Argument<*>>,
    block: Argument<*>.() -> AbstractArgumentTree<*, Argument<*>, CommandSender>,
) = this.thenMerged(args = argList.toTypedArray(), block)

fun AbstractArgumentTree<*, Argument<*>, CommandSender>.thenNested(vararg args: MergedArgument): AbstractArgumentTree<*, Argument<*>, CommandSender> {
    var tree = this

    var argIndex = 0
    while (true) {
        if (argIndex >= args.size) break
        val arg = args[argIndex]

        if (arg.isSingle) tree = tree.then(arg.arguments.first())
        else return tree.thenMerged(arg.arguments) {
            thenNested(*args.drop(argIndex + 1).toTypedArray())
        }
        argIndex++
    }

    return tree
}

fun test() {
    CommandTree("!test").then(
        LiteralArgument("test").thenNested(
            TextArgument("test1").single(),
            MergedArgument.of(IntegerArgument("number"), TextArgument("text")),
            TextArgument("test2").single(),
        )
    )
}

interface ArgumentPart {
    fun getArguments(): List<AbstractArgumentTree<*, Argument<*>, CommandSender>>
    val isSingle get() = getArguments().size == 1
}
class SingleArgumentPart<Impl : AbstractArgumentTree<Impl, Argument<*>, CommandSender>>(val instance: Impl) : AbstractArgumentTree<Impl, Argument<*>, CommandSender>(), ArgumentPart {
    override fun getArguments() = listOf(this)

    override fun instance(): Impl? {
        return instance
    }
}
fun Argument<*>.single() = SingleArgumentPart(this)
class MultiArgumentPart(val arguments: List<AbstractArgumentTree<*, Argument<*>, CommandSender>>) : ArgumentPart {
    override fun getArguments() = arguments

    companion object {
        fun of(vararg args: AbstractArgumentTree<*, Argument<*>, CommandSender>) = MultiArgumentPart(args.toList())
    }
}

class MergedTree {
    constructor(tree: AbstractCommandTree<*, Argument<*>, CommandSender>) {
        this.tree = tree
        this.stack = mutableListOf()
    }

    private constructor(tree: AbstractCommandTree<*, Argument<*>, CommandSender>, stack: List<AbstractCommandTree<*, Argument<*>, CommandSender>.() -> AbstractCommandTree<*, Argument<*>, CommandSender>>) {
        this.stack = stack.toMutableList()
        this.tree = tree
    }

    val tree: AbstractCommandTree<*, Argument<*>, CommandSender>
    val stack: MutableList<AbstractCommandTree<*, Argument<*>, CommandSender>.() -> AbstractCommandTree<*, Argument<*>, CommandSender>>

    private fun copy(stackAddition: AbstractCommandTree<*, Argument<*>, CommandSender>.() -> AbstractCommandTree<*, Argument<*>, CommandSender>): MergedTree {
        return MergedTree(tree, stack + stackAddition)
    }
    fun then(tree: AbstractArgumentTree<*, Argument<*>, CommandSender>) = copy { then(tree) }
    fun thenNested(vararg args: ArgumentPart): MergedTree = copy { thenNested(*args) }
    fun thenNested(vararg args: AbstractArgumentTree<*, Argument<*>, CommandSender>): MergedTree = copy { thenNested(*args) }

    fun executes(executor: CommandExecutor): MergedTree = copy { this.e }
    fun register() = merge().register()
    fun merge(): AbstractCommandTree<*, Argument<*>, CommandSender> {
        // apply stuff later
        return tree
    }
}