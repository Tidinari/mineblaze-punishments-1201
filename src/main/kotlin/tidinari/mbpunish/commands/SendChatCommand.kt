package tidinari.mbpunish.commands

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.client.MinecraftClient

class SendChatCommand {

    fun registerCommand() {
        ClientCommandRegistrationCallback.EVENT.register(
            ClientCommandRegistrationCallback { dispatcher, _ ->
                dispatcher.register(
                    LiteralArgumentBuilder
                        .literal<FabricClientCommandSource?>("sendchat")
                        .then(
                            ClientCommandManager.argument("message", StringArgumentType.greedyString())
                                .executes(::executes)
                        )
                        .executes(::executes)
                )
            }
        )
    }

    private fun executes(context: CommandContext<FabricClientCommandSource>?): Int {
        try {
            val message = StringArgumentType.getString(context, "message")
            MinecraftClient.getInstance().networkHandler?.sendChatMessage(message)
        } catch (_: IllegalArgumentException) {
        }
        return 0
    }
}