package tidinari.mbpunish.commands

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.MinecraftClient
import tidinari.mbpunish.screens.OneNickMenu
import tidinari.mbpunish.sources.rules.RulesSource
import tidinari.mbpunish.sources.settings.SettingsSource


class PunishCommand(private val rulesSource: RulesSource, private val settingsSource: SettingsSource) {
    init {
        ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick {
            while (isExecuted) {
                open(name)
                isExecuted = false
            }
        })
    }

    private val suggestPlayerNames: SuggestionProvider<FabricClientCommandSource> = SuggestionProvider { context, builder ->
        val players = context.source.playerNames
        try {
            val playerName = StringArgumentType.getString(context, "playerName")
            players.filter { it.startsWith(playerName, ignoreCase = true) }.forEach { builder.suggest(it) }
        } catch (emptyName: IllegalArgumentException) {
            players.forEach { builder.suggest(it) }
        }
        builder.buildFuture()
    }

    fun registerCommand() {
        ClientCommandRegistrationCallback.EVENT.register(
            ClientCommandRegistrationCallback { dispatcher, _ ->
                dispatcher.register(
                    LiteralArgumentBuilder
                        .literal<FabricClientCommandSource?>("pn")
                        .then(ClientCommandManager.argument("playerName", StringArgumentType.greedyString())
                                .suggests(suggestPlayerNames)
                                .executes(::executes))
                        .executes(::executes)
                )
            }
        )
    }

    private var isExecuted = false
    private var name = ""

    private fun executes(context: CommandContext<FabricClientCommandSource>?): Int {
        name = try {
            StringArgumentType.getString(context, "playerName")
        } catch (emptyName: IllegalArgumentException) {
            ""
        }
        isExecuted = true
        return 0
    }

    private fun open(playerName: String) {
        MinecraftClient.getInstance().setScreenAndRender(OneNickMenu(playerName, rulesSource, settingsSource))
    }
}