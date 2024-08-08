package zinc.doiche.lib

import com.mojang.brigadier.Command
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.velocitypowered.api.command.BrigadierCommand
import com.velocitypowered.api.command.CommandManager
import com.velocitypowered.api.command.CommandSource
import com.velocitypowered.api.proxy.Player

data class CommandHolder(
    val name: String,
    val brigadierCommand: BrigadierCommand,
    val aliases: Array<String>
) {
    fun register(manager: CommandManager) {
        val meta = manager.metaBuilder(brigadierCommand)
            .aliases(*aliases)
            .build()

        manager.register(meta, brigadierCommand)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CommandHolder

        if (brigadierCommand != other.brigadierCommand) return false
        if (!aliases.contentEquals(other.aliases)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = brigadierCommand.hashCode()
        result = 31 * result + aliases.contentHashCode()
        return result
    }
}

fun simpleCommand(name: String, command: Command<CommandSource>) = LiteralArgumentBuilder
    .literal<CommandSource>(name)
    .executes(command)
    .build()

val CommandSource.player: Player?
    get() = this as? Player

fun <T: ArgumentBuilder<CommandSource, T>> ArgumentBuilder<CommandSource, T>.requiresPermission(permission: String): T {
    return requires { source ->
        source.player?.hasPermission(permission) ?: false
    }
}

fun <T: ArgumentBuilder<CommandSource, T>> ArgumentBuilder<CommandSource, T>.requiresPlayer(): T {
    return requires { source ->
        source is Player
    }
}

fun <T: ArgumentBuilder<CommandSource, T>> ArgumentBuilder<CommandSource, T>.requiresPlayer(
    predicate: (Player) -> Boolean
): T {
    return requires { source ->
        source.player?.let(predicate) ?: false
    }
}

fun <T> RequiredArgumentBuilder<CommandSource, T>.suggestArguments(
    suggest: () -> Collection<String>
): RequiredArgumentBuilder<CommandSource, T> {
    return suggests { _, builder ->
        suggest().forEach { builder.suggest(it) }
        builder.buildFuture()
    }
}

fun <T> RequiredArgumentBuilder<CommandSource, T>.suggestEnums(
    enumClass: Class<out Enum<*>>
): RequiredArgumentBuilder<CommandSource, T> {
    return suggests { _, builder ->
        enumClass.enumConstants.forEach { builder.suggest(it.name) }
        builder.buildFuture()
    }
}

fun <T: ArgumentBuilder<CommandSource, T>> ArgumentBuilder<CommandSource, T>.executesPlayer(
    execute: (CommandContext<CommandSource>, Player) -> Int
): T {
    return executes { context ->
        context.source.player?.let { player ->
            execute(context, player)
        } ?: Command.SINGLE_SUCCESS
    }
}