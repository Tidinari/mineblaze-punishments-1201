package tidinari.mbpunish.commands

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.client.MinecraftClient

class RealNameAliasCommand {

    fun registerCommand() {
        ClientCommandRegistrationCallback.EVENT.register(
            ClientCommandRegistrationCallback { dispatcher, _ ->
                dispatcher.register(
                    LiteralArgumentBuilder
                        .literal<FabricClientCommandSource?>("rn")
                        .then(
                            ClientCommandManager.argument("name", StringArgumentType.greedyString())
                                .executes(::executes)
                        )
                        .executes(::executes)
                )
            }
        )
    }

    private fun executes(context: CommandContext<FabricClientCommandSource>?): Int {
        try {
            val playerName = StringArgumentType.getString(context, "name")
            MinecraftClient.getInstance().networkHandler?.sendCommand("realname $playerName")
        } catch (emptyName: IllegalArgumentException) {
            MinecraftClient.getInstance().networkHandler?.sendCommand("realname")
        }
        return 0
    }
}